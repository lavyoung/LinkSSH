package com.lavyoung.ai.linkssh.host;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link SshHostKeyFingerprint} 的规范格式和必填约束。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshHostKeyFingerprintTest {

    private static final String VALID_FINGERPRINT =
            "SHA256:AbCdEf0123456789012345678901234567890123456";

    @Test
    void shouldAcceptCanonicalSha256Fingerprint() {
        SshHostKeyFingerprint fingerprint = new SshHostKeyFingerprint(VALID_FINGERPRINT);

        assertEquals(VALID_FINGERPRINT, fingerprint.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "SHA256:",
            "MD5:00:11:22:33",
            "SHA256:AbCdEf012345678901234567890123456789012345",
            "SHA256:AbCdEf0123456789012345678901234567890123456=",
            " SHA256:AbCdEf0123456789012345678901234567890123456"
    })
    void shouldRejectNonCanonicalFingerprint(String value) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SshHostKeyFingerprint(value)
        );

        assertEquals(
                "SSH host key fingerprint must use the SHA256:<base64> format",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullFingerprint() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SshHostKeyFingerprint(null)
        );

        assertEquals("SSH host key fingerprint must not be null", exception.getMessage());
    }
}
