FROM ubuntu:trusty

# update and install java
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y --no-install-recommends openjdk-7-jre-headless

ADD ./target/universal/stage/ /opt/app

EXPOSE 9000

WORKDIR /opt/app/

ENTRYPOINT ["bin/cameowww"]

CMD ["-Dconfig.file=conf/secret.conf"]
