#!/bin/bash

cf login -a https://api.local.pcfdev.io -u admin -p admin -o pcfdev-org -s pcfdev-space --skip-ssl-validation
cf push -p ./build/libs/library-service.jar library-service



