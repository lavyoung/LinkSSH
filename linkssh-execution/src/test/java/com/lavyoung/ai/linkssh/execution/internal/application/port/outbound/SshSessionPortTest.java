package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import com.lavyoung.ai.linkssh.execution.SshConnectionResult;
import com.lavyoung.ai.linkssh.execution.SshSessionId;
import com.lavyoung.ai.linkssh.execution.SshSessionStatus;
import com.lavyoung.ai.linkssh.host.SshConnectionTarget;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link SshSessionPort} 的出站边界没有暴露 SSH 客户端技术类型。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshSessionPortTest {

    @Test
    void shouldExposeOnlySessionLifecycleOperations() throws NoSuchMethodException {
        Method connect = SshSessionPort.class.getMethod(
                "connect",
                SshSessionId.class,
                SshConnectionTarget.class,
                Duration.class,
                Duration.class
        );
        Method disconnect = SshSessionPort.class.getMethod(
                "disconnect",
                SshSessionId.class,
                Duration.class
        );
        Method findStatus = SshSessionPort.class.getMethod("findStatus", SshSessionId.class);

        assertEquals(SshConnectionResult.class, connect.getReturnType());
        assertEquals(void.class, disconnect.getReturnType());
        assertEquals(Optional.class, findStatus.getReturnType());
        assertTrue(
                findStatus.getGenericReturnType().getTypeName().contains(SshSessionStatus.class.getName())
        );
        assertEquals(3, SshSessionPort.class.getDeclaredMethods().length);
    }

    @Test
    void shouldNotExposeApacheMinaTypes() {
        boolean exposesApacheMinaType = Arrays.stream(SshSessionPort.class.getDeclaredMethods())
                .flatMap(method -> Stream.concat(
                        Arrays.stream(method.getParameterTypes()),
                        Stream.of(method.getReturnType())
                ))
                .map(Class::getName)
                .anyMatch(typeName -> typeName.startsWith("org.apache.sshd."));

        assertFalse(exposesApacheMinaType);
    }
}
