#!/bin/bash

cf login -a https://api.local.pcfdev.io -u admin -p admin -o pcfdev-org -s pcfdev-space --skip-ssl-validation
cf push -p ./dist/library-ui.zip -f ./manifest.yml library-ui



