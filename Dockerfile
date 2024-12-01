FROM bellsoft/liberica-openjdk-alpine:17

# Kotlin Compiler
RUN apk update && \
    apk add --no-cache wget unzip
RUN wget -q -O kotlin.zip "https://github.com/JetBrains/kotlin/releases/download/v1.9.0/kotlin-compiler-1.9.0.zip" && \
    unzip -q kotlin.zip && \
    mv kotlinc /usr/local/kotlin && \
    rm kotlin.zip

# Gradle
ENV GRADLE_VERSION=7.6.1
RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" && \
    unzip -q "gradle-${GRADLE_VERSION}-bin.zip" -d /opt && \
    ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/bin/gradle && \
    rm "gradle-${GRADLE_VERSION}-bin.zip"

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY src/ ./src/

RUN gradle build

EXPOSE 7777

CMD ["gradle", "run"]