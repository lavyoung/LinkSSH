package com.lavyoung.ai.linkssh.execution.internal.infrastructure.ssh;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.keyboard.UserInteraction;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.config.hosts.HostConfigEntryResolver;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ApacheMinaSshClientFactory} 创建的客户端安全基线。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class ApacheMinaSshClientFactoryTest {

    @Test
    void shouldCreateClientWithExplicitSecurityConfiguration() {
        SshClient client = ApacheMinaSshClientFactory.create();

        assertInstanceOf(PinnedHostKeyVerifier.class, client.getServerKeyVerifier());
        assertSame(HostConfigEntryResolver.EMPTY, client.getHostConfigEntryResolver());
        assertSame(KeyIdentityProvider.EMPTY_KEYS_PROVIDER, client.getKeyIdentityProvider());
        assertSame(
                PasswordIdentityProvider.EMPTY_PASSWORDS_PROVIDER,
                client.getPasswordIdentityProvider()
        );
        assertSame(UserInteraction.NONE, client.getUserInteraction());
    }

    @Test
    void shouldLeaveClientLifecycleToSessionAdapter() {
        SshClient client = ApacheMinaSshClientFactory.create();

        assertFalse(client.isStarted());
    }

    @Test
    void shouldRemainPackagePrivateFinalAndNonInstantiable() {
        int modifiers = ApacheMinaSshClientFactory.class.getModifiers();
        Constructor<?> constructor = ApacheMinaSshClientFactory.class.getDeclaredConstructors()[0];

        assertFalse(Modifier.isPublic(modifiers));
        assertTrue(Modifier.isFinal(modifiers));
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }
}
