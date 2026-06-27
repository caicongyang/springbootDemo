# Repository Guidelines

A multi-module Maven workspace that hosts a collection of Spring Boot starters and small companion tools under the parent group `com.caicongyang` (Spring Boot `2.2.1.RELEASE`, Java 8, Spring Cloud `Hoxton.SR6`).

## Project Structure & Module Organization

- The root `pom.xml` is the parent aggregator. It pins Java 1.8 and versions for shared dependencies (Lombok, MyBatis-Plus, Redisson, RocketMQ, SkyWalking, etc.).
- Every top-level directory is one Maven module. Most are starters (e.g. `cache-starter`, `locker-starter`, `mq-branch-spring-boot-starter`, `shutdown-graceful-nacos-starter`); auxiliary modules include `git-tools` and `local-test`.
- A typical starter has this layout:
  ```
  <module>/
  ├── pom.xml
  └── src/main/
      ├── java/com/caicongyang/<topic>/...
      └── resources/META-INF/spring.factories
  ```
- Auto-configuration is wired through `META-INF/spring.factories` (`org.springframework.boot.autoconfigure.EnableAutoConfiguration=...`).
- Local sandbox services (MySQL, Redis) and `docker-compose` files live under `local-test/src/main/resources/docs/`.

## Build, Test, and Development Commands

Run from the repository root.

- `mvn clean install` — build all modules and install them to `~/.m2`.
- `mvn -pl <module> -am clean install` — build one module and its dependencies (e.g. `mvn -pl cache-starter -am install`).
- `mvn -pl <module> test` — run the tests for a single module.
- `mvn -Dmaven.test.skip=true clean package` — package without running tests; this is the path used by `upload.sh`, which then `scp`s the produced jar to the remote host.
- Add any new starter to the `<modules>` list of the root `pom.xml` so it is part of the reactor build.

## Coding Style & Naming Conventions

- Java 8 source, UTF-8, 4-space indentation, brace on its own line for methods.
- Lombok is on the classpath; use it when it improves clarity. Field injection via `@Resource` / `@Autowired` is the prevailing pattern — stay consistent with neighboring classes.
- Packages live under `com.caicongyang.<topic>`, mirroring the module name (`com.caicongyang.cache`, `com.caicongyang.config`, ...).
- Class naming: `XxxAutoConfiguration` for Spring Boot auto-configurations, `XxxConfiguration` / `XxxTemplate` / `XxxProxy` for supporting beans, `XxxProperties` for `@ConfigurationProperties` holders.
- No formatter or linter is enforced — match the surrounding style.

## Testing Guidelines

- Tests live under `src/test/java` and use JUnit 5 (`org.junit.jupiter.api.Test`).
- For Spring-context tests, extend `BaseApplicationTest` in `local-test` and annotate with `@SpringBootTest(classes = Application.class)`.
- Coverage is intentionally light across the demo; new starters should ship at least one happy-path integration test that exercises the relevant `local-test` service.

## Commit & Pull Request Guidelines

- Commit history is informal and mixes Chinese and English (`新增dubbo mock 组件`, `feat: add mq-branch-spring-boot-starter moudule`, `fix bug`). Keep messages short and descriptive; Conventional Commits prefixes (`feat:`, `fix:`, `refactor:`) are welcome but not required.
- Mention the affected module in the subject when it is not obvious from the change.
- Pull requests should describe the changed component, list any new dependencies, and include sample YAML or configuration snippets for new starter properties. Link the related issue when one exists.

## Agent-Specific Notes

- Pin shared dependency versions in the root `pom.xml`'s `dependencyManagement` rather than re-declaring them in module POMs.
- `target/`, IDE metadata, and built jars are git-ignored — do not commit them.
- When adding a new starter, update both the root `pom.xml` `<modules>` list and the top-level `README.md` module index.
