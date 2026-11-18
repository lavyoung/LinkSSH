package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.SshConnectionFailure;
import org.apache.sshd.common.SshConstants;
import org.apache.sshd.common.SshException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link SshConnectionFailureClassifier} 的稳定失败分类和优先级。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshConnectionFailureClassifierTest {

    @Test
    void shouldClassifyHostKeyMismatchFromNestedCause() {
        SshException hostKeyMismatch = new SshException(
                SshConstants.SSH2_DISCONNECT_HOST_KEY_NOT_VERIFIABLE,
                "Server key did not validate"
        );
        IOException wrapper = new IOException("SSH negotiation failed", hostKeyMismatch);

        assertEquals(
                SshConnectionFailure.HOST_KEY_MISMATCH,
                SshConnectionFailureClassifier.classifyAuthentication(wrapper)
        );
    }

    @Test
    void shouldGiveHostKeyMismatchPriorityOverTimeoutAndNetworkFailure() {
        SshException hostKeyMismatch = new SshException(
                SshConstants.SSH2_DISCONNECT_HOST_KEY_NOT_VERIFIABLE,
                "Server key did not validate"
        );
        TimeoutException timeout = new TimeoutException("Timed out");
        timeout.initCause(hostKeyMismatch);
        ConnectException networkFailure = new ConnectException("Connection failed");
        networkFailure.initCause(timeout);

        assertEquals(
                SshConnectionFailure.HOST_KEY_MISMATCH,
                SshConnectionFailureClassifier.classifyConnection(networkFailure)
        );
    }

    @Test
    void shouldClassifyNestedTimeoutInBothPhases() {
        IOException wrapper = new IOException(
                "Operation failed",
                new TimeoutException("Timed out")
        );

        assertEquals(
                SshConnectionFailure.CONNECTION_TIMEOUT,
                SshConnectionFailureClassifier.classifyConnection(wrapper)
        );
        assertEquals(
                SshConnectionFailure.CONNECTION_TIMEOUT,
                SshConnectionFailureClassifier.classifyAuthentication(wrapper)
        );
    }

    @ParameterizedTest
    @MethodSource("networkFailures")
    void shouldClassifyNetworkFailuresInBothPhases(Throwable exception) {
        assertEquals(
                SshConnectionFailure.HOST_UNREACHABLE,
                SshConnectionFailureClassifier.classifyConnection(exception)
        );
        assertEquals(
                SshConnectionFailure.HOST_UNREACHABLE,
                SshConnectionFailureClassifier.classifyAuthentication(exception)
        );
    }

    @Test
    void shouldUsePhaseSpecificFallbackForUnknownFailure() {
        IllegalStateException exception = new IllegalStateException("Unexpected state");

        assertEquals(
                SshConnectionFailure.UNEXPECTED_FAILURE,
                SshConnectionFailureClassifier.classifyConnection(exception)
        );
        assertEquals(
                SshConnectionFailure.AUTHENTICATION_FAILED,
                SshConnectionFailureClassifier.classifyAuthentication(exception)
        );
    }

    @Test
    void shouldRejectNullException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> SshConnectionFailureClassifier.classifyConnection(null)
        );

        assertEquals(
                "SSH connection exception must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldRemainPackagePrivateFinalAndNonInstantiable() {
        int modifiers = SshConnectionFailureClassifier.class.getModifiers();
        Constructor<?> constructor =
                SshConnectionFailureClassifier.class.getDeclaredConstructors()[0];

        assertFalse(Modifier.isPublic(modifiers));
        assertTrue(Modifier.isFinal(modifiers));
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    private static Stream<Throwable> networkFailures() {
        return Stream.of(
                new ConnectException("Connection refused"),
                new NoRouteToHostException("No route"),
                new UnknownHostException("Unknown host"),
                new UnresolvedAddressException(),
                new SocketException("Connection reset"),
                new EOFException("Connection closed"),
                new SshException(
                        SshConstants.SSH2_DISCONNECT_CONNECTION_LOST,
                        "Connection lost"
                )
        );
    }
}
