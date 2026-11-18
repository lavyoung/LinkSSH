package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.host.SshHostKeyFingerprint;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.digest.BuiltinDigests;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PinnedHostKeyVerifier} 固定指纹校验的拒绝策略和连接隔离约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class PinnedHostKeyVerifierTest {

    private final PinnedHostKeyVerifier verifier = new PinnedHostKeyVerifier();

    @Test
    void shouldAcceptServerKeyWithMatchingFingerprint() throws GeneralSecurityException {
        PublicKey serverKey = generatePublicKey();
        ClientSession session = sessionWith(
                PinnedHostKeyVerifier.connectionContext(fingerprintOf(serverKey))
        );

        assertTrue(verifier.verifyServerKey(session, null, serverKey));
    }

    @Test
    void shouldRejectServerKeyWithDifferentFingerprint() throws GeneralSecurityException {
        PublicKey expectedServerKey = generatePublicKey();
        PublicKey actualServerKey = generatePublicKey();
        ClientSession session = sessionWith(
                PinnedHostKeyVerifier.connectionContext(fingerprintOf(expectedServerKey))
        );

        assertFalse(verifier.verifyServerKey(session, null, actualServerKey));
    }

    @Test
    void shouldRejectMissingSessionOrServerKey() throws GeneralSecurityException {
        PublicKey serverKey = generatePublicKey();
        ClientSession session = sessionWith(
                PinnedHostKeyVerifier.connectionContext(fingerprintOf(serverKey))
        );

        assertFalse(verifier.verifyServerKey(null, null, serverKey));
        assertFalse(verifier.verifyServerKey(session, null, null));
    }

    @Test
    void shouldRejectMissingConnectionContext() throws GeneralSecurityException {
        PublicKey serverKey = generatePublicKey();

        assertFalse(verifier.verifyServerKey(sessionWith(null), null, serverKey));
    }

    @Test
    void shouldRejectConnectionContextWithoutExpectedFingerprint() throws GeneralSecurityException {
        PublicKey serverKey = generatePublicKey();
        AttributeRepository emptyContext = AttributeRepository.ofAttributesMap(Map.of());

        assertFalse(verifier.verifyServerKey(sessionWith(emptyContext), null, serverKey));
    }

    @Test
    void shouldRejectNullExpectedFingerprint() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> PinnedHostKeyVerifier.connectionContext(null)
        );

        assertEquals(
                "Expected SSH host key fingerprint must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldKeepExpectedFingerprintsIsolatedPerConnection() throws GeneralSecurityException {
        PublicKey firstServerKey = generatePublicKey();
        PublicKey secondServerKey = generatePublicKey();
        ClientSession firstSession = sessionWith(
                PinnedHostKeyVerifier.connectionContext(fingerprintOf(firstServerKey))
        );
        ClientSession secondSession = sessionWith(
                PinnedHostKeyVerifier.connectionContext(fingerprintOf(secondServerKey))
        );

        assertTrue(verifier.verifyServerKey(firstSession, null, firstServerKey));
        assertFalse(verifier.verifyServerKey(firstSession, null, secondServerKey));
        assertTrue(verifier.verifyServerKey(secondSession, null, secondServerKey));
        assertFalse(verifier.verifyServerKey(secondSession, null, firstServerKey));
    }

    @Test
    void shouldRemainPackagePrivateAndFinal() {
        int modifiers = PinnedHostKeyVerifier.class.getModifiers();

        assertFalse(Modifier.isPublic(modifiers));
        assertTrue(Modifier.isFinal(modifiers));
    }

    private static PublicKey generatePublicKey() throws GeneralSecurityException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair().getPublic();
    }

    private static SshHostKeyFingerprint fingerprintOf(PublicKey publicKey) {
        return new SshHostKeyFingerprint(
                KeyUtils.getFingerPrint(BuiltinDigests.sha256, publicKey)
        );
    }

    private static ClientSession sessionWith(AttributeRepository connectionContext) {
        return (ClientSession) Proxy.newProxyInstance(
                ClientSession.class.getClassLoader(),
                new Class<?>[]{ClientSession.class},
                (proxy, method, arguments) -> {
                    if ("getConnectionContext".equals(method.getName())
                            && method.getParameterCount() == 0) {
                        return connectionContext;
                    }
                    throw new UnsupportedOperationException(
                            "Unexpected ClientSession method: " + method.getName()
                    );
                }
        );
    }
}
