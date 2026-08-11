# DevLog 后端指令

本文件适用于 Maven 后端的 `src/` 源码与测试。工程配置、数据库脚本和部署文件仍受仓库根目录 `AGENTS.md` 约束。

## Layers and responsibilities

- Controller 只处理 HTTP 请求、参数绑定、`@Valid`、调用 Service 和返回响应；不得直接调用 Mapper，也不承载复杂业务判断。
- Service 负责业务规则、权限、资源存在性、状态转换、跨资源协调与事务边界。可复用现有 Validator，避免把同一规则散落在多个 Service。
- Mapper 只负责持久化。遵循现有注解 SQL/Mapper 方法命名风格；复杂 SQL 保持可读，避免把业务决策塞进 SQL。
- 优先扩展现有 DTO、Entity、VO、Converter、Service 和 Mapper；没有证据时不创建平行抽象或新分层。

## Validation and exceptions

- DTO/Bean Validation 负责 required、size、format、numeric range 等结构约束；Service 负责数据库存在性、权限、唯一性、业务状态与跨资源规则。
- 同一规则只在最合适的一层验证，不在 Controller、Service、Validator 重复实现。
- 预期业务问题抛 `BusinessException` 并使用准确 HTTP 状态。不要用 `throw new RuntimeException(...)` 表达正常业务失败。
- `IllegalStateException` 可用于真正不应出现的内部状态；不要机械替换所有 Java runtime exception。
- 未预期异常由全局处理器记录并返回 500；不要把数据库约束错误或内部异常细节泄露给客户端。

## MyBatis and database work

- 改 Mapper 前核对 `database/` 当前 schema、对应 Entity/DTO 与现有 SQL；不猜表名、列名、空值语义或级联行为。
- 避免 N+1。多 ID 存在性/权限校验优先 batch select，关系表更新优先 batch insert/delete；分页、计数与过滤延续现有查询语义。
- `article_tag` 只表达 Article 实际使用的 Tag；`category_tag` 只表达 Category 与 Tag 的多对多关联。不要给 `tag` 添加 `category_id`，也不要用其中一张关系表代替另一张。
- 手记仍是固定分类下的 Article；Media Review 与 Project Showcase 是独立领域，除非需求明确改变边界，不要互相合并 CRUD 或表结构。

## Transactions

- Article 与 `article_tag`、Category 与 `category_tag`、删除实体与清理关联、其他多步骤写入必须评估事务并保证原子性。
- 事务边界放在 Service；遵循现有 `@Transactional` 风格，涉及受检/运行时异常差异时明确 rollback 行为。
- 不在事务中进行无必要的远程调用或长时间工作；批量写入前完成可预先执行的校验。

## Authentication and authorization

- 当前认证链路为 JWT + Redis session + `UserContext`，由现有拦截器与安全配置协作。非认证需求不要修改 JWT、Redis、Interceptor 或 Security 架构。
- 需要当前用户身份时从 `UserContext` 获取，不信任前端提交的 `userId`；管理员能力继续复用现有管理员校验。
- 新增公开端点前检查拦截器白名单和读写语义；不要为解决局部访问问题扩大匿名范围。

## Mapping, storage, and comments

- 不机械清除 `BeanUtils`。低风险展示映射可沿用现有 Converter/BeanUtils；安全敏感的 DTO → Entity 更新优先显式字段白名单，防止越权字段写入。
- 当前上传由 `StorageService`/`LocalStorageServiceImpl` 写入配置目录并生成 `/uploads/...` URL；Docker 使用持久卷。除非需求明确，不引入 OSS 或改变 URL 契约。
- Java 注释使用简体中文，只解释非显而易见的业务规则、事务原因、性能原因或安全原因；不生成逐行复述代码的 AI 风格注释。

## Tests and verification

- 修改行为时优先补充或更新相邻层测试，覆盖成功、校验失败、权限/不存在、冲突与事务相关边界；复用当前 JUnit、Mockito、MockMvc 测试风格。
- 按改动范围先运行定向测试，再运行完整 `./mvnw test`（Windows：`.\mvnw.cmd test`）。仅声称实际执行并通过的验证。
- API 变更同时核对 Controller 状态码、`Result` body、全局异常映射和相关调用方，但不因后端任务默认修改前端。
