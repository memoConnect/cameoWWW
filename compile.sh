#!/bin/bash

./sbt clean compile stage

if [[ ${1} == "--dev" ]]; then
    cp ../cameoSecrets/www_secret_dev.conf target/universal/stage/conf/secret.conf
elif [[ ${1} == "--prod" ]]; then
    cp ../cameoSecrets/www_secret_prod.conf target/universal/stage/conf/secret.conf
else
    echo "include \"application\"" > target/universal/stage/conf/secret.conf
fi
