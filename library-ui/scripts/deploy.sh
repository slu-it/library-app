#!/bin/bash

cf login -a https://api.run.pivotal.io -u $1 -p $2 -o "NovaTec AQE" -s library-app
cf push -p ./dist/library-ui.zip -f ./manifest.yml library-ui




