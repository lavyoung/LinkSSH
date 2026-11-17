package com.lavyoung.ai.linkssh.execution;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link SshSessionId} 的会话身份契约。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshSessionIdTest {

    @Test
    void shouldPreserveProvidedIdentifier() {
        UUID value = UUID.randomUUID();

        SshSessionId sessionId = new SshSessionId(value);

        assertEquals(value, sessionId.value());
    }

    @Test
    void shouldGenerateDistinctIdentifiers() {
        SshSessionId first = SshSessionId.newId();
        SshSessionId second = SshSessionId.newId();

        assertNotNull(first.value());
        assertNotNull(second.value());
        assertNotEquals(first, second);
    }

    @Test
    void shouldRejectNullIdentifier() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SshSessionId(null)
        );

        assertEquals("SSH session id must not be null", exception.getMessage());
    }
}
