package com.lavyoung.ai.linkssh.host;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link HostId} 的主机身份契约。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class HostIdTest {

    @Test
    void shouldPreserveProvidedIdentifier() {
        UUID value = UUID.randomUUID();

        HostId hostId = new HostId(value);

        assertEquals(value, hostId.value());
    }

    @Test
    void shouldGenerateDistinctIdentifiers() {
        HostId first = HostId.newId();
        HostId second = HostId.newId();

        assertNotNull(first.value());
        assertNotNull(second.value());
        assertNotEquals(first, second);
    }

    @Test
    void shouldRejectNullIdentifier() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new HostId(null)
        );

        assertEquals("Host id must not be null", exception.getMessage());
    }
}
