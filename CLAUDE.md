# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

`kora-sync` (`com.minierp:kora-sync`) is a freshly generated Spring Boot 4.1.2 / Java 21 / Maven project (Spring Initializr skeleton). It contains only `KoraSyncApplication`, a smoke test (`KoraSyncApplicationTests`) and an `application.yaml` that sets just `spring.application.name`. There is no domain code, controller, entity or migration yet, so there is no architecture to document beyond the stack below. Update this file as the structure emerges.

The root package is `com.minierp.kora_sync` (underscore, not hyphen).

## Commands

Use the Maven wrapper (no system Maven needed):

- Run the app: `./mvnw spring-boot:run`
- Build / package: `./mvnw package` (skip tests: `-DskipTests`)
- All tests: `./mvnw test`
- Single test class: `./mvnw test -Dtest=ClassName`
- Single test method: `./mvnw test -Dtest=ClassName#method`

No linter or formatter is configured.

## Stack and things to know

Dependencies in `pom.xml`: Web MVC (`spring-boot-starter-webmvc`), Data JPA, Flyway (`spring-boot-starter-flyway` + `flyway-database-postgresql`), Validation, Actuator, PostgreSQL driver, Lombok. Each has a matching `-test` starter.

- **A PostgreSQL database is required to start the app, and `contextLoads` needs one too.** JPA, Flyway and the Postgres driver are on the classpath but `application.yaml` defines no `spring.datasource.*`. Until a datasource is configured (or a Testcontainers/H2 test setup is added), `./mvnw spring-boot:run` and `KoraSyncApplicationTests` will fail at startup. There is no `docker-compose.yml` yet; add one at the project root if a local DB is needed.
- **Flyway migrations** go in `src/main/resources/db/migration` (default location, which doesn't exist yet). Flyway runs on startup, so schema changes belong in migrations, not in JPA auto-DDL.
- **Lombok** is wired through explicit `annotationProcessorPaths` in the `maven-compiler-plugin` executions (`default-compile` and `default-testCompile`). If you add another annotation processor (e.g. MapStruct), add it to both executions or it won't run.
- Spring Boot 4 modularised its starters, so test slices and starter names differ from Boot 3 (e.g. `spring-boot-starter-webmvc`, `spring-boot-starter-data-jpa-test`). Check the Boot 4 names before copying Boot 3 snippets.
