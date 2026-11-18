package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import com.lavyoung.ai.linkssh.execution.SshConnectionFailure;
import org.apache.sshd.common.SshConstants;
import org.apache.sshd.common.SshException;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * 将 Apache MINA SSHD 和网络层异常转换为稳定的连接失败分类。
 *
 * <p>该类型属于基础设施防腐层。它负责识别异常因果链中的主机密钥拒绝、
 * 超时和网络故障，不向应用层泄漏底层异常类型或异常消息。</p>
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
final class SshConnectionFailureClassifier {

    private SshConnectionFailureClassifier() {
    }

    /**
     * 对建立网络连接阶段的异常进行分类。
     *
     * @param exception 连接阶段捕获的异常
     * @return 稳定的连接失败分类
     * @throws NullPointerException 当异常为 {@code null} 时抛出
     */
    static SshConnectionFailure classifyConnection(Throwable exception) {
        return classify(exception, SshConnectionFailure.UNEXPECTED_FAILURE);
    }

    /**
     * 对主机密钥交换和身份认证阶段的异常进行分类。
     *
     * @param exception 认证阶段捕获的异常
     * @return 稳定的连接失败分类
     * @throws NullPointerException 当异常为 {@code null} 时抛出
     */
    static SshConnectionFailure classifyAuthentication(Throwable exception) {
        return classify(exception, SshConnectionFailure.AUTHENTICATION_FAILED);
    }

    private static SshConnectionFailure classify(Throwable exception, SshConnectionFailure fallback) {
        Objects.requireNonNull(exception, "SSH connection exception must not be null");

        boolean timeoutFound = false;
        boolean networkFailureFound = false;
        Throwable current = exception;
        while (current != null) {
            if (isHostKeyMismatch(current)) {
                return SshConnectionFailure.HOST_KEY_MISMATCH;
            }
            timeoutFound = timeoutFound || isTimeout(current);
            networkFailureFound = networkFailureFound || isNetworkFailure(current);

            if (current.getCause() == current) {
                break;
            }
            current = current.getCause();
        }

        if (timeoutFound) {
            return SshConnectionFailure.CONNECTION_TIMEOUT;
        }
        if (networkFailureFound) {
            return SshConnectionFailure.HOST_UNREACHABLE;
        }
        return fallback;
    }

    private static boolean isHostKeyMismatch(Throwable exception) {
        return exception instanceof SshException sshException
            && sshException.getDisconnectCode() == SshConstants.SSH2_DISCONNECT_HOST_KEY_NOT_VERIFIABLE;
    }

    private static boolean isTimeout(Throwable exception) {
        return exception instanceof TimeoutException || exception instanceof SocketTimeoutException;
    }

    private static boolean isNetworkFailure(Throwable exception) {
        if (exception instanceof SshException sshException && sshException.getDisconnectCode() == SshConstants.SSH2_DISCONNECT_CONNECTION_LOST) {
            return true;
        }
        return exception instanceof ConnectException || exception instanceof NoRouteToHostException
            || exception instanceof UnknownHostException || exception instanceof UnresolvedAddressException
            || exception instanceof SocketException || exception instanceof EOFException;
    }

}
