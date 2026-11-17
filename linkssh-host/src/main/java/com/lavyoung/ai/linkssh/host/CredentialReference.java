package com.lavyoung.ai.linkssh.host;

import java.util.Objects;
import java.util.UUID;

/**
 * 表示保存在受控凭据存储中的凭据引用。
 *
 * <p>该类型只保存凭据的稳定标识，不保存密码、私钥、私钥口令或其他凭据正文。
 * Execution 上下文只能通过受控端口按该引用临时解析凭据材料。</p>
 *
 * @param value 凭据在受控存储中的唯一标识
 * @author <a href="mailto:lavyoung1325@outlook.com">lavyoung</a>
 * @since 0.1.0
 */
public record CredentialReference(UUID value) {

    /**
     * 创建凭据引用。
     *
     * @throws NullPointerException 当引用值为 {@code null} 时抛出
     */
    public CredentialReference {
        Objects.requireNonNull(value, "Credential reference must not be null");
    }
}
