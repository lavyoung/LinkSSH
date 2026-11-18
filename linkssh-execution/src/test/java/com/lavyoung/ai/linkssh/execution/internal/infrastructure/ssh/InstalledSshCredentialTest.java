package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.PasswordCredentialMaterial;
import com.lavyoung.ai.linkssh.execution.internal.application.port.outbound.PrivateKeyCredentialMaterial;
import org.apache.sshd.client.session.ClientSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link InstalledSshCredential} 的会话身份安装、移除和脱敏约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class InstalledSshCredentialTest {

    @Test
    void shouldInstallAndRemovePasswordIdentity() throws Exception {
        SessionIdentityRecorder recorder = new SessionIdentityRecorder();

        try (PasswordCredentialMaterial material = new PasswordCredentialMaterial(
                new char[]{'t', 'e', 's', 't'}
        )) {
            InstalledSshCredential installed = InstalledSshCredential.install(
                    recorder.session(),
                    material
            );

            assertEquals(List.of("test"), recorder.passwordIdentities());
            assertEquals("InstalledSshCredential[redacted]", installed.toString());

            installed.close();

            assertTrue(recorder.passwordIdentities().isEmpty());
        }
    }

    @Test
    void shouldInstallAndRemovePrivateKeyIdentity() throws Exception {
        SessionIdentityRecorder recorder = new SessionIdentityRecorder();
        byte[] privateKey = createPkcs8PrivateKey();

        try (PrivateKeyCredentialMaterial material =
                     new PrivateKeyCredentialMaterial(privateKey)) {
            InstalledSshCredential installed = InstalledSshCredential.install(
                    recorder.session(),
                    material
            );

            assertEquals(1, recorder.publicKeyIdentities().size());

            installed.close();

            assertTrue(recorder.publicKeyIdentities().isEmpty());
        }
    }

    @Test
    void shouldRejectInvalidPrivateKeyWithoutInstallingIdentity() {
        SessionIdentityRecorder recorder = new SessionIdentityRecorder();

        try (PrivateKeyCredentialMaterial material = new PrivateKeyCredentialMaterial(
                "not-a-private-key".getBytes(StandardCharsets.US_ASCII)
        )) {
            assertThrows(
                    GeneralSecurityException.class,
                    () -> InstalledSshCredential.install(recorder.session(), material)
            );
        }

        assertTrue(recorder.publicKeyIdentities().isEmpty());
    }

    @Test
    void shouldRejectNullSessionOrCredentialMaterial() {
        SessionIdentityRecorder recorder = new SessionIdentityRecorder();

        try (PasswordCredentialMaterial material =
                     new PasswordCredentialMaterial(new char[]{'x'})) {
            NullPointerException sessionException = assertThrows(
                    NullPointerException.class,
                    () -> InstalledSshCredential.install(null, material)
            );
            NullPointerException materialException = assertThrows(
                    NullPointerException.class,
                    () -> InstalledSshCredential.install(recorder.session(), null)
            );

            assertEquals("SSH client session must not be null", sessionException.getMessage());
            assertEquals(
                    "SSH credential material must not be null",
                    materialException.getMessage()
            );
        }
    }

    @Test
    void shouldAllowRepeatedClose() throws Exception {
        SessionIdentityRecorder recorder = new SessionIdentityRecorder();

        try (PasswordCredentialMaterial material =
                     new PasswordCredentialMaterial(new char[]{'x'})) {
            InstalledSshCredential installed = InstalledSshCredential.install(
                    recorder.session(),
                    material
            );

            installed.close();

            assertDoesNotThrow(installed::close);
            assertTrue(recorder.passwordIdentities().isEmpty());
        }
    }

    @Test
    void shouldRemainPackagePrivateFinalWithSynchronizedClose() throws NoSuchMethodException {
        int modifiers = InstalledSshCredential.class.getModifiers();
        int closeModifiers = InstalledSshCredential.class.getMethod("close").getModifiers();

        assertFalse(Modifier.isPublic(modifiers));
        assertTrue(Modifier.isFinal(modifiers));
        assertTrue(Modifier.isSynchronized(closeModifiers));
    }

    private static byte[] createPkcs8PrivateKey() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        String encodedPrivateKey = Base64.getMimeEncoder(
                64,
                new byte[]{'\n'}
        ).encodeToString(keyPair.getPrivate().getEncoded());
        String pem = "-----BEGIN PRIVATE KEY-----\n"
                + encodedPrivateKey
                + "\n-----END PRIVATE KEY-----\n";
        return pem.getBytes(StandardCharsets.US_ASCII);
    }

    private static final class SessionIdentityRecorder {

        private final List<String> passwordIdentities = new ArrayList<>();
        private final List<KeyPair> publicKeyIdentities = new ArrayList<>();
        private final ClientSession session = createSession();

        ClientSession session() {
            return session;
        }

        List<String> passwordIdentities() {
            return List.copyOf(passwordIdentities);
        }

        List<KeyPair> publicKeyIdentities() {
            return List.copyOf(publicKeyIdentities);
        }

        private ClientSession createSession() {
            return (ClientSession) Proxy.newProxyInstance(
                    ClientSession.class.getClassLoader(),
                    new Class<?>[]{ClientSession.class},
                    (proxy, method, arguments) -> switch (method.getName()) {
                        case "addPasswordIdentity" -> {
                            passwordIdentities.add((String) arguments[0]);
                            yield null;
                        }
                        case "removePasswordIdentity" -> {
                            String password = (String) arguments[0];
                            yield passwordIdentities.remove(password) ? password : null;
                        }
                        case "addPublicKeyIdentity" -> {
                            publicKeyIdentities.add((KeyPair) arguments[0]);
                            yield null;
                        }
                        case "removePublicKeyIdentity" -> {
                            KeyPair keyPair = (KeyPair) arguments[0];
                            yield publicKeyIdentities.remove(keyPair) ? keyPair : null;
                        }
                        case "toString" -> "ClientSessionTestDouble";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == arguments[0];
                        default -> throw new UnsupportedOperationException(
                                "Unexpected ClientSession method: " + method.getName()
                        );
                    }
            );
        }
    }
}
