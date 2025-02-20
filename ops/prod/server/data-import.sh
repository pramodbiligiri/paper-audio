#!/bin/bash

sql=$1;

cat $1 | grep -E "^COPY|^([0-9]+\s)|^\\\." > table-data.sql;


cat <(cat table-data.sql | python3 extract-table.py arxiv_oai) <(cat table-data.sql | python3 extract-table.py paper_data) <(cat table-data.sql | python3 extract-table.py paper_audio) <(cat table-data.sql | python3 extract-table.py paper_category) <(cat table-data.sql | python3 extract-table.py email_sub) <(cat table-data.sql | python3 extract-table.py feedback) > out.sql;

sed -i '/^\\\./! s/\\/\\\\/g' out.sql;

cat start-import.sql out.sql end-import.sql > import-data.sql;

psql -v ON_ERROR_STOP=1 -h localhost -U tts_app --password tts_prod < import-data.sql
