#!/bin/bash

ssh -i ~/.ssh/id_het etl@new.papertime.app "bin/daily-prod-update.sh"
