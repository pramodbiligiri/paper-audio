#!/bin/bash

newdir=`ssh -i ~/.ssh/id_het etl@new.papertime.app 'bin/new-build-dir'`;
echo "New build dir: $newdir";

scp -i ~/.ssh/id_het target/paper-audio-1.0-SNAPSHOT.jar etl@new.papertime.app:$newdir

remote_cmd="bin/update-build $newdir";

ssh -i ~/.ssh/id_het etl@new.papertime.app "$remote_cmd"
