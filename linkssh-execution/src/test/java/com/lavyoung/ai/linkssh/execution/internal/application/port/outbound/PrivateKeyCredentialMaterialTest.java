package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PrivateKeyCredentialMaterial} 的复制、清理和脱敏约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class PrivateKeyCredentialMaterialTest {

    @Test
    void shouldDefensivelyCopyPrivateKeyAndPassphrase() {
        byte[] privateKey = {1, 2, 3};
        char[] passphrase = {'x', 'y'};
        PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                privateKey,
                passphrase
        );
        privateKey[0] = 9;
        passphrase[0] = 'z';

        byte[] firstKeyCopy = material.copyPrivateKey();
        char[] firstPassphraseCopy = material.copyPassphrase();
        firstKeyCopy[1] = 8;
        firstPassphraseCopy[1] = 'w';

        assertArrayEquals(new byte[]{1, 2, 3}, material.copyPrivateKey());
        assertArrayEquals(new char[]{'x', 'y'}, material.copyPassphrase());
        assertNotSame(firstKeyCopy, material.copyPrivateKey());
        assertNotSame(firstPassphraseCopy, material.copyPassphrase());
        assertTrue(material.hasPassphrase());

        material.close();
    }

    @Test
    void shouldRepresentMissingPassphraseAsEmptyArray() {
        PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                new byte[]{1, 2, 3}
        );

        assertFalse(material.hasPassphrase());
        assertArrayEquals(new char[0], material.copyPassphrase());

        material.close();
    }

    @Test
    void shouldClearInternalKeyAndPassphraseAndRejectAccessAfterClose()
            throws ReflectiveOperationException {
        PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                new byte[]{1, 2, 3},
                new char[]{'x', 'y'}
        );
        Field privateKeyField = PrivateKeyCredentialMaterial.class.getDeclaredField("privateKey");
        Field passphraseField = PrivateKeyCredentialMaterial.class.getDeclaredField("passphrase");
        privateKeyField.setAccessible(true);
        passphraseField.setAccessible(true);
        byte[] internalPrivateKey = (byte[]) privateKeyField.get(material);
        char[] internalPassphrase = (char[]) passphraseField.get(material);

        material.close();

        assertArrayEquals(new byte[internalPrivateKey.length], internalPrivateKey);
        assertArrayEquals(new char[internalPassphrase.length], internalPassphrase);
        assertNull(privateKeyField.get(material));
        assertNull(passphraseField.get(material));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                material::copyPrivateKey
        );
        assertEquals(
                "SSH private key credential material has already been closed",
                exception.getMessage()
        );
        assertThrows(IllegalStateException.class, material::copyPassphrase);
        assertThrows(IllegalStateException.class, material::hasPassphrase);
    }

    @Test
    void shouldAllowRepeatedClose() {
        PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                new byte[]{1}
        );

        material.close();

        assertDoesNotThrow(material::close);
    }

    @Test
    void shouldRejectMissingOrEmptyKeyMaterial() {
        NullPointerException missingKey = assertThrows(
                NullPointerException.class,
                () -> new PrivateKeyCredentialMaterial(null)
        );
        IllegalArgumentException emptyKey = assertThrows(
                IllegalArgumentException.class,
                () -> new PrivateKeyCredentialMaterial(new byte[0])
        );
        NullPointerException missingPassphrase = assertThrows(
                NullPointerException.class,
                () -> new PrivateKeyCredentialMaterial(new byte[]{1}, null)
        );

        assertEquals("SSH private key must not be null", missingKey.getMessage());
        assertEquals("SSH private key must not be empty", emptyKey.getMessage());
        assertEquals(
                "SSH private key passphrase must not be null",
                missingPassphrase.getMessage()
        );
    }

    @Test
    void shouldExposeOnlyRedactedText() {
        PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                new byte[]{1, 2, 3},
                new char[]{'x', 'y'}
        );

        assertEquals("PrivateKeyCredentialMaterial[redacted]", material.toString());

        material.close();
    }

    @Test
    void shouldSynchronizeReadAndCloseOperations() throws NoSuchMethodException {
        assertTrue(Modifier.isSynchronized(
                PrivateKeyCredentialMaterial.class.getMethod("copyPrivateKey").getModifiers()
        ));
        assertTrue(Modifier.isSynchronized(
                PrivateKeyCredentialMaterial.class.getMethod("copyPassphrase").getModifiers()
        ));
        assertTrue(Modifier.isSynchronized(
                PrivateKeyCredentialMaterial.class.getMethod("hasPassphrase").getModifiers()
        ));
        assertTrue(Modifier.isSynchronized(
                PrivateKeyCredentialMaterial.class.getMethod("close").getModifiers()
        ));
    }
}
