FROM openjdk:8
COPY . /usr/app

WORKDIR /usr/app
RUN ./gradlew shadowJar --no-daemon
RUN ./gradlew generateDocumentation --no-daemon
ENV GOOGLE_APPLICATION_CREDENTIALS ../../nodal-thunder-279319-7a4d2b6d03a6.json

WORKDIR /usr/app/build/libs
ENTRYPOINT ["java", "-jar", "speech-to-text-demo-1.0.0-all.jar", "../../config/cloud-run.json"]