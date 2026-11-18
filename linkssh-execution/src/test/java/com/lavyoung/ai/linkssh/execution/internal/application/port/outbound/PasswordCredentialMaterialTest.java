package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PasswordCredentialMaterial} 的复制、清理和脱敏约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class PasswordCredentialMaterialTest {

    @Test
    void shouldDefensivelyCopyPasswordOnInputAndOutput() {
        char[] source = {'a', 'b', 'c'};
        PasswordCredentialMaterial material = new PasswordCredentialMaterial(source);
        source[0] = 'x';

        char[] firstCopy = material.copyPassword();
        firstCopy[1] = 'y';
        char[] secondCopy = material.copyPassword();

        assertArrayEquals(new char[]{'a', 'b', 'c'}, secondCopy);
        assertNotSame(firstCopy, secondCopy);

        material.close();
    }

    @Test
    void shouldClearInternalPasswordAndRejectAccessAfterClose() throws ReflectiveOperationException {
        PasswordCredentialMaterial material = new PasswordCredentialMaterial(
                new char[]{'a', 'b', 'c'}
        );
        Field passwordField = PasswordCredentialMaterial.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        char[] internalPassword = (char[]) passwordField.get(material);

        material.close();

        assertArrayEquals(new char[internalPassword.length], internalPassword);
        assertNull(passwordField.get(material));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                material::copyPassword
        );
        assertEquals(
                "SSH password credential material has already been closed",
                exception.getMessage()
        );
    }

    @Test
    void shouldAllowRepeatedClose() {
        PasswordCredentialMaterial material = new PasswordCredentialMaterial(
                new char[]{'a'}
        );

        material.close();

        assertDoesNotThrow(material::close);
    }

    @Test
    void shouldRejectNullPassword() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new PasswordCredentialMaterial(null)
        );

        assertEquals("SSH password must not be null", exception.getMessage());
    }

    @Test
    void shouldExposeOnlyRedactedText() {
        PasswordCredentialMaterial material = new PasswordCredentialMaterial(
                new char[]{'a', 'b', 'c'}
        );

        assertEquals("PasswordCredentialMaterial[redacted]", material.toString());

        material.close();
    }

    @Test
    void shouldSynchronizeReadAndCloseOperations() throws NoSuchMethodException {
        assertTrue(Modifier.isSynchronized(
                PasswordCredentialMaterial.class.getMethod("copyPassword").getModifiers()
        ));
        assertTrue(Modifier.isSynchronized(
                PasswordCredentialMaterial.class.getMethod("close").getModifiers()
        ));
    }
}
