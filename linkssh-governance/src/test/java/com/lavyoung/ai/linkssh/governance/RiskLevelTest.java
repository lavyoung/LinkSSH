package com.lavyoung.ai.linkssh.governance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link RiskLevel} 对外暴露的风险顺序和阈值比较契约。
 *
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
class RiskLevelTest {

    @Test
    void shouldExposeStableSeverityValues() {
        assertAll(
                () -> assertEquals(10, RiskLevel.LOW.severity()),
                () -> assertEquals(20, RiskLevel.MEDIUM.severity()),
                () -> assertEquals(30, RiskLevel.HIGH.severity()),
                () -> assertEquals(40, RiskLevel.CRITICAL.severity())
        );
    }

    @Test
    void shouldCompareRiskLevelsAgainstThreshold() {
        assertAll(
                () -> assertTrue(RiskLevel.LOW.isAtLeast(RiskLevel.LOW)),
                () -> assertTrue(RiskLevel.HIGH.isAtLeast(RiskLevel.MEDIUM)),
                () -> assertTrue(RiskLevel.CRITICAL.isAtLeast(RiskLevel.HIGH)),
                () -> assertFalse(RiskLevel.LOW.isAtLeast(RiskLevel.MEDIUM)),
                () -> assertFalse(RiskLevel.MEDIUM.isAtLeast(RiskLevel.CRITICAL))
        );
    }

    @Test
    void shouldRejectNullThreshold() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> RiskLevel.HIGH.isAtLeast(null)
        );

        assertEquals("Risk threshold must not be null", exception.getMessage());
    }
}
