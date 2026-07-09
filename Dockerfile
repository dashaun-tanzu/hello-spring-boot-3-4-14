# syntax=docker/dockerfile:1
#
# The application jar is built on the host (mvn package) because Spring Boot
# 3.4.14 is a commercial LTS artifact pulled from the Spring Enterprise
# repository (typically via your internal Nexus/Artifactory), which the Docker
# build sandbox can't authenticate to. Build first, then `docker build`:
#
#     mvn -q clean package -DskipTests
#     docker build -t vuln-chain-demo:3.4.14 .
#
# Base image is chosen to scan ZERO HIGH/CRITICAL under `trivy image`. The
# whole point of the demo is that a clean scan does not mean safe.
# (amazoncorretto:21-alpine scans zero HIGH/CRITICAL as of this writing and
# runs as root by default, which is part of the container-layer weakness.)
FROM amazoncorretto:21-alpine

WORKDIR /app

# The application. The page templates and JS render function are packaged
# inside the jar (src/main/resources/scripts -> classpath:scripts/), so there
# is no separate templates/ directory to copy.
COPY target/vuln-chain-demo-1.0.0.jar /app/app.jar

# ---------------------------------------------------------------------------
# CONTAINER-LAYER WEAKNESS (the second half of the chain).
#
# Production secrets are exposed via ENV variables (loaded from .envrc) and
# written to a file at build time. The container runs as root (amazoncorretto:21
# defaults to uid 0; we do not drop to a nonroot user). On its own this is
# "just" a hardening finding -- no scanner flags it as a HIGH, and without a way
# to read files it is not remotely reachable. Chained with the app-layer
# CVE-2026-22737 file read, it becomes a remote disclosure of production
# credentials.
#
# Upgrading Spring Boot removes the file read but leaves THIS untouched -- the
# secrets are still baked in, the container still runs as root -- yet the
# exploit no longer works, because the only door to it is shut.
# ---------------------------------------------------------------------------
ENV BW_SESSION=${BW_SESSION}
ENV PROD_DB_PASSWORD=${PROD_DB_PASSWORD}
ENV AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}

RUN mkdir -p /app/secret && \
    echo "PROD_DB_PASSWORD=${PROD_DB_PASSWORD}" >> /app/secret/app-secret.env && \
    echo "AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}" >> /app/secret/app-secret.env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
