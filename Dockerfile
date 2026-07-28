FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN groupadd --system appuser \
    && useradd --system --gid appuser --home-dir /app --shell /usr/sbin/nologin appuser \
    && mkdir -p /app/uploads \
    && chown -R appuser:appuser /app

COPY --chown=appuser:appuser --from=build /workspace/target/*.jar app.jar
COPY docker-entrypoint.sh /usr/local/bin/devlog-entrypoint
RUN chmod 0755 /usr/local/bin/devlog-entrypoint

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/devlog-entrypoint"]
