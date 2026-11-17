package com.lavyoung.ai.linkssh.host;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link CredentialReference} 只承载稳定凭据标识。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class CredentialReferenceTest {

    @Test
    void shouldPreserveProvidedReference() {
        UUID value = UUID.randomUUID();

        CredentialReference reference = new CredentialReference(value);

        assertEquals(value, reference.value());
    }

    @Test
    void shouldRejectNullReference() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new CredentialReference(null)
        );

        assertEquals("Credential reference must not be null", exception.getMessage());
    }
}
