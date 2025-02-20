#!/bin/bash

newdir=$1;

echo "Updating latest to point to $newdir";

rm -f /home/etl/builds/latest;
ln -sf $newdir /home/etl/builds/latest;