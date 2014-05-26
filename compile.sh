#!/bin/bash

if [ ${1} == "--with-secrets" ]; then
    cp ../cameoSecrets/www_secret.conf conf/secret.conf
else
    echo "include \"application\"" > conf/secret.conf
fi

./sbt clean compile stage