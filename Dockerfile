FROM gradle:jdk11

#Install Z3, adpated from https://github.com/SebastianS90/docker-z3-java/blob/latest/Dockerfile, URL changed for z3 Release 4.8.5

ENV Z3_VERSION "4.8.5"

RUN apt-get update -qq -y \
 && apt-get install binutils g++ make ant -y \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/* \
 && Z3_DIR="$(mktemp -d)" \
 && cd "$Z3_DIR" \
 && wget -qO- https://github.com/Z3Prover/z3/archive/Z3-${Z3_VERSION}.tar.gz | tar xz --strip-components=1 \
 && python scripts/mk_make.py --java \
 && cd build \
 && make \
 && make install \
 && cd / \
 && rm -rf "$Z3_DIR"


COPY --chown=gradle:gradle . /home/gradle/src
RUN cp /usr/lib/com.microsoft.z3.jar /home/gradle/src/lib
WORKDIR /home/gradle/src

RUN gradle clean build

ENTRYPOINT java -jar /home/gradle/src/build/libs/simulator-0.0.1-SNAPSHOT.jar
