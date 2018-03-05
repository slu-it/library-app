#!/bin/bash

cf login -a https://api.run.pivotal.io -u $1 -p $2 -o "NovaTec AQE" -s library-app
cf push -p ./build/libs/library-service.jar -f ./manifest.yml library-service




