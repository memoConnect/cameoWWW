FROM ubuntu:trusty

# update and install java
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list

RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install -y --no-install-recommends openjdk-7-jre-headless


ADD ./target/universal/stage/ /opt/app


RUN ls /opt/app/bin

EXPOSE 9000

WORKDIR /opt/app/

ENTRYPOINT ["bin/cameowww"]

CMD ["-Dconfig.file=conf/secret.conf"]