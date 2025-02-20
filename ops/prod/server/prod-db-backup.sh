#!/bin/bash

pg_dump --format c -d tts_prod --no-privileges --no-owner -h localhost -U tts_app --password  > `date +%F`-tts_prod.db