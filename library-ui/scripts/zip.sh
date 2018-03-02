#!/bin/bash

. "$(dirname $(readlink -f "$0"))/filenames.sh"

echo "Filename for app zip file will be: $FILENAME_APP"

(cd ./dist && zip -r $FILENAME_APP *)

(zip ./dist/$FILENAME_APP Staticfile)

