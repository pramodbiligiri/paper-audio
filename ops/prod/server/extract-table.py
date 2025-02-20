#!/usr/bin/env python

import sys

found=False

table_name = sys.argv[1]

for line in sys.stdin:
    if line.startswith("COPY") and table_name in line:
        found=True

    if not found:
        continue

    line = line.strip()

    print(line)

    if found and line.endswith("\."):
        break

