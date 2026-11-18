package com.lavyoung.ai.linkssh.execution.internal.application.port.outbound;

import com.lavyoung.ai.linkssh.host.CredentialReference;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link SshCredentialResolverPort} 只返回受生命周期控制的凭据材料。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class SshCredentialResolverPortTest {

    @Test
    void shouldResolveCredentialReferenceToManagedMaterial() throws NoSuchMethodException {
        Method resolve = SshCredentialResolverPort.class.getMethod(
                "resolve",
                CredentialReference.class
        );

        assertEquals(Optional.class, resolve.getReturnType());
        assertTrue(
                resolve.getGenericReturnType().getTypeName()
                        .contains(SshCredentialMaterial.class.getName())
        );
        assertEquals(1, SshCredentialResolverPort.class.getDeclaredMethods().length);
    }

    @Test
    void shouldNotExposeRawOrApacheMinaCredentialTypes() {
        boolean exposesForbiddenType = Arrays.stream(
                        SshCredentialResolverPort.class.getDeclaredMethods()
                )
                .flatMap(method -> Stream.concat(
                        Arrays.stream(method.getParameterTypes()),
                        Stream.of(method.getReturnType())
                ))
                .map(Class::getName)
                .anyMatch(typeName -> typeName.equals(String.class.getName())
                        || typeName.equals(char[].class.getName())
                        || typeName.equals(byte[].class.getName())
                        || typeName.startsWith("org.apache.sshd."));

        assertFalse(exposesForbiddenType);
    }
}
