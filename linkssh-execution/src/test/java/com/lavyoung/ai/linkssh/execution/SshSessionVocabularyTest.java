package com.lavyoung.ai.linkssh.execution;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 SSH 会话状态和连接失败分类的稳定业务词汇。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshSessionVocabularyTest {

    @Test
    void shouldExposeCompleteSessionLifecycle() {
        assertEquals(
                EnumSet.of(
                        SshSessionStatus.CONNECTING,
                        SshSessionStatus.CONNECTED,
                        SshSessionStatus.DISCONNECTING,
                        SshSessionStatus.DISCONNECTED,
                        SshSessionStatus.FAILED
                ),
                EnumSet.allOf(SshSessionStatus.class)
        );
    }

    @Test
    void shouldExposeStableConnectionFailureClassifications() {
        assertEquals(
                EnumSet.of(
                        SshConnectionFailure.CREDENTIAL_UNAVAILABLE,
                        SshConnectionFailure.AUTHENTICATION_FAILED,
                        SshConnectionFailure.HOST_KEY_MISMATCH,
                        SshConnectionFailure.CONNECTION_TIMEOUT,
                        SshConnectionFailure.HOST_UNREACHABLE,
                        SshConnectionFailure.CLIENT_UNAVAILABLE,
                        SshConnectionFailure.UNEXPECTED_FAILURE
                ),
                EnumSet.allOf(SshConnectionFailure.class)
        );
    }
}
