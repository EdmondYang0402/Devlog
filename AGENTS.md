# DevLog 项目指令

## Project

- DevLog 是已投入运行的个人技术博客与 CMS，当前持续开发功能并优化 UI/架构，不是 greenfield 项目。
- 开始任务前先读取真实实现，定位已有组件、API、Controller、Service、Mapper、DTO 与数据库脚本；复用现有设计，不按需求文字猜文件名、字段或接口。
- 小需求保持最小改动，不因发现旧代码可优化而顺手重构无关区域。

## Repository and stack

- 后端工程根目录即仓库根目录：Java 21、Spring Boot 3.5、MyBatis、MySQL、Redis、JWT、Bean Validation；源码与测试位于 `src/main`、`src/test`。
- 前端位于 `frontend/`：Vue 3、Vite 5、Element Plus、Axios、Vue Router、Vue I18n。
- 部署使用 Linux、Docker Compose 与 Nginx。当前上传实现是本地文件系统，在 Docker 中由 `upload_data` 卷持久化并通过 `/uploads` 暴露；不要把历史 OSS URL 当成当前存储架构。
- 数据库初始化与迁移脚本位于 `database/`；`devlog.sql` 含历史数据，不能替代当前 schema 脚本。

## Scope discipline

- 纯前端任务默认只改 `frontend/`；纯后端任务默认只改后端及必要的数据库脚本。仅在需求客观需要或用户明确要求时跨端修改。
- 保持现有 Controller → Service → Mapper 分层：Controller 薄，结构校验交给 DTO/Bean Validation，业务规则交给 Service/Validator，持久化交给 Mapper。
- 预期业务失败使用 `BusinessException`，不要用裸 `RuntimeException` 表达正常业务结果。
- 不安装新依赖、不改变认证/存储/部署架构，除非需求明确需要且现有能力无法满足。

## Data and API contracts

- 数据库任务先检查 `database/` 脚本与 Mapper 中的真实 schema；不发明表名或字段。优先复用模型、批量查询/写入，避免 N+1，多步骤一致性写入使用事务。
- `Article -> Category`；`Article <-> Tag` 通过 `article_tag`，表示文章实际使用的标签；`Category <-> Tag` 通过 `category_tag`，表示分类与标签的多对多关系。两张关系表不可混用。
- 手记复用 Article 与固定分类语义；除非用户明确改变长期设计，不新增独立 Note CRUD。Media Review（作品档案）和 Project Showcase 均独立于 Article。
- HTTP 状态必须表达真实结果：成功 `200`；输入/业务请求无效 `400`；未认证 `401`；无权限 `403`；资源不存在 `404`；冲突 `409`；意外错误 `500`。不要用 HTTP 200 + `Result.error` 表达失败。

## Verification and delivery

- 前端改动：在 `frontend/` 运行 `npm run build`；涉及现有工具测试时运行 `npm test`。项目目前没有 lint script，不要声称已运行 lint。
- 后端改动：按风险运行相关 Maven 测试，至少验证 `./mvnw test`（Windows 可用 `.\mvnw.cmd test`）；仅需编译时使用对应 Maven compile/package 命令。
- 完成报告列出修改文件、核心实现、实际构建/测试结果和未解决问题。未运行或失败的验证必须如实说明。
- 保留用户已有工作区改动，不覆盖、不回滚、不把无关文件纳入本任务。

## Instruction maintenance

- `AGENTS.md` 只记录未来数周或数月仍有效的约定，不记录临时文案、尺寸、截图要求、单次 bug 或开发日志。
- 当任务明确改变长期约定时，检查相关 `AGENTS.md` 是否过时；只有规则真正变化时才更新，不随每次代码修改更新。
