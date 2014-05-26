#!/bin/bash

./sbt clean compile stage

if [[ ${1} == "--with-secrets" ]]; then
    cp ../cameoSecrets/www_secret.conf target/universal/stage/conf/secret.conf
else
    echo "include \"application\"" > target/universal/stage/conf/secret.conf
fi
