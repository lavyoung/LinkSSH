package com.lavyoung.ai.linkssh.host;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link SshConnectionTarget} 的连接参数和安全约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshConnectionTargetTest {

    private static final SshHostKeyFingerprint FINGERPRINT = new SshHostKeyFingerprint(
            "SHA256:AbCdEf0123456789012345678901234567890123456"
    );

    private final HostId hostId = HostId.newId();
    private final CredentialReference credentialReference =
            new CredentialReference(UUID.randomUUID());

    @Test
    void shouldCreateTargetAndNormalizeText() {
        SshConnectionTarget target = new SshConnectionTarget(
                hostId,
                "  server.example.com  ",
                22,
                "  deploy  ",
                credentialReference,
                FINGERPRINT
        );

        assertAll(
                () -> assertSame(hostId, target.hostId()),
                () -> assertEquals("server.example.com", target.hostname()),
                () -> assertEquals(22, target.port()),
                () -> assertEquals("deploy", target.username()),
                () -> assertSame(credentialReference, target.credentialReference()),
                () -> assertSame(FINGERPRINT, target.expectedHostKeyFingerprint())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 22, 65_535})
    void shouldAcceptValidPortBoundaries(int port) {
        SshConnectionTarget target = createTarget("server.example.com", port, "deploy");

        assertEquals(port, target.port());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 65_536, Integer.MAX_VALUE})
    void shouldRejectPortOutsideValidRange(int port) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createTarget("server.example.com", port, "deploy")
        );

        assertEquals("SSH port must be between 1 and 65535", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectBlankHostname(String hostname) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createTarget(hostname, 22, "deploy")
        );

        assertEquals("Hostname must not be blank", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectBlankUsername(String username) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createTarget("server.example.com", 22, username)
        );

        assertEquals("Username must not be blank", exception.getMessage());
    }

    @Test
    void shouldRejectMissingRequiredValues() {
        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SshConnectionTarget(
                                null,
                                "server.example.com",
                                22,
                                "deploy",
                                credentialReference,
                                FINGERPRINT
                        )
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SshConnectionTarget(
                                hostId,
                                null,
                                22,
                                "deploy",
                                credentialReference,
                                FINGERPRINT
                        )
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SshConnectionTarget(
                                hostId,
                                "server.example.com",
                                22,
                                null,
                                credentialReference,
                                FINGERPRINT
                        )
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SshConnectionTarget(
                                hostId,
                                "server.example.com",
                                22,
                                "deploy",
                                null,
                                FINGERPRINT
                        )
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> new SshConnectionTarget(
                                hostId,
                                "server.example.com",
                                22,
                                "deploy",
                                credentialReference,
                                null
                        )
                )
        );
    }

    private SshConnectionTarget createTarget(String hostname, int port, String username) {
        return new SshConnectionTarget(
                hostId,
                hostname,
                port,
                username,
                credentialReference,
                FINGERPRINT
        );
    }
}
