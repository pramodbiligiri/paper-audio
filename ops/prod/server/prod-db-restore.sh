#!/bin/bash

dbFile=$1;

pg_restore --format=c -U postgres --no-acl --no-owner --no-privileges -h localhost -d tts_local_prod --password $dbFile;