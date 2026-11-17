package com.lavyoung.ai.linkssh.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link SshConnectionResult} 对成功和失败结果的互斥表达。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshConnectionResultTest {

    @Test
    void shouldCreateConnectedResult() {
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult.Connected result = new SshConnectionResult.Connected(sessionId);

        assertSame(sessionId, result.sessionId());
    }

    @Test
    void shouldCreateFailedResultWithStableClassification() {
        SshSessionId sessionId = SshSessionId.newId();

        SshConnectionResult.Failed result = new SshConnectionResult.Failed(
                sessionId,
                SshConnectionFailure.AUTHENTICATION_FAILED
        );

        assertSame(sessionId, result.sessionId());
        assertEquals(SshConnectionFailure.AUTHENTICATION_FAILED, result.failure());
    }

    @Test
    void shouldRejectConnectedResultWithoutSessionIdentifier() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SshConnectionResult.Connected(null)
        );

        assertEquals("SSH session id must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectFailedResultWithoutSessionIdentifier() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SshConnectionResult.Failed(
                        null,
                        SshConnectionFailure.CONNECTION_TIMEOUT
                )
        );

        assertEquals("SSH session id must not be null", exception.getMessage());
    }

    @Test
    void shouldRejectFailedResultWithoutFailureClassification() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new SshConnectionResult.Failed(SshSessionId.newId(), null)
        );

        assertEquals("SSH connection failure must not be null", exception.getMessage());
    }
}
