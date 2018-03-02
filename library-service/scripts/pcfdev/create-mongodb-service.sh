#!/bin/bash

IPADDRESS=$1

cf login -a https://api.local.pcfdev.io -u admin -p admin -o pcfdev-org -s pcfdev-space --skip-ssl-validation
cf cups mongo-db -p "{ \"uri\": \"mongodb://${IPADDRESS}:27017/library-service\" }"
