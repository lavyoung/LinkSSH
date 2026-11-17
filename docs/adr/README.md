# Architecture Decision Records

本目录记录 LinkSSH 已作出的重要架构决策。ADR 与根目录
`PROJECT_PLAN.md` 共同构成项目的架构管控机制。

## 使用规则

以下变更必须先创建或更新 ADR：

- 新增、拆分或合并限界上下文
- 改变模块依赖方向
- 引入核心框架、数据库或通信协议
- 改变安全边界或凭据管理方式
- 从模块化单体拆分微服务
- 引入当前规划明确排除的能力

## 状态

- `Proposed`：讨论中，不能作为实施依据
- `Accepted`：已经批准，应按决策实施
- `Superseded`：已被后续 ADR 取代
- `Deprecated`：不再推荐，但尚未被其他 ADR 替代
- `Rejected`：经过评估后不采用

## 命名约定

```text
NNNN-short-kebab-case-title.md
```

ADR 编号一经使用不得重复。决策变化时新增 ADR，不重写历史。

## 索引

| 编号 | 决策 | 状态 |
|---|---|---|
| [0001](0001-modular-monolith-ddd-java21.md) | 采用模块化单体、DDD 与 Java 21 | Accepted |
| [0002](0002-desktop-internationalization.md) | 桌面端采用资源键驱动的中英文国际化 | Accepted |

## 模板

```markdown
# ADR-NNNN：标题

- 状态：Proposed
- 日期：YYYY-MM-DD
- 决策者：LinkSSH Maintainers

## 背景

描述问题、约束和需要作出决策的原因。

## 决策

描述选定方案和必须遵守的规则。

## 后果

### 正面影响

### 代价与风险

## 备选方案

列出评估过但未采用的方案及原因。

## 验证方式

说明如何通过测试、工具或评审验证决策得到执行。

## 重新评估条件

说明何时需要重新审视该决策。
```

