#!/bin/bash

cf login -a https://api.local.pcfdev.io -u admin -p admin -o pcfdev-org -s pcfdev-space --skip-ssl-validation

cf set-env library-service USERS_ADMIN_PASSWORD admin
cf set-env library-service USERS_CURATOR_PASSWORD curator
cf set-env library-service USERS_USER_PASSWORD user
cf restage library-service



