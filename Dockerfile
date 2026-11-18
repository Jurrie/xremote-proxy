# -----------------------------------------------------------------------------
# Create the DEB package.
# There is no --platform here on purpose: the "podman build" in pom.xml passes "--platform linux/amd64,linux/arm64", so this stage runs once for each architecture (the ARM64 one under QEMU emulation).
FROM docker.io/library/eclipse-temurin:${maven.compiler.target}-jdk-noble AS builder

RUN apt-get update && apt-get install -y fakeroot && apt-get clean

WORKDIR /work

COPY /modules.txt /work/modules.txt
RUN /opt/java/openjdk/bin/jlink \
  --add-modules $(cat /work/modules.txt) \
  --strip-debug \
  --compress zip-0 \
  --no-header-files \
  --no-man-pages \
  --output /work/jlink-jre

COPY /target/${project.build.finalName}.jar /work/jpackage-input/
COPY /debian/${project.artifactId}.service /work/jpackage-input/
COPY LICENSE /work/
COPY /debian/postinst /work/jpackage-resources/
COPY /debian/prerm /work/jpackage-resources/
COPY /debian/postrm /work/jpackage-resources/

RUN /opt/java/openjdk/bin/jpackage \
  --type deb \
  --runtime-image /work/jlink-jre \
  --name "${project.name}" \
  --vendor "${project.developers[0].name}" \
  --copyright "${project.developers[0].name}" \
  --about-url "${project.url}" \
  --license-file "/work/LICENSE" \
  --app-version "${project.version}" \
  --linux-deb-maintainer "${project.developers[0].email}" \
  --linux-package-deps "libsystemd0" \
  --description "${project.description}" \
  --main-jar "${project.build.finalName}.jar" \
  --input "/work/jpackage-input" \
  --dest "/work/output" \
  --resource-dir "/work/jpackage-resources"

# -----------------------------------------------------------------------------
# Output the DEB package. With "--output type=local" this stage's contents are written to target/; the amd64 and arm64 .debs have different file names, so both builds land next to each other.
FROM scratch AS output
COPY --from=builder /work/output/ .
