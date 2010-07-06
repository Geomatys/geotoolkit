#!/bin/sh

# ----------------------------------------------------
# Setup the environment for performing Geotk releases.
# This script needs to be executed only once.
# ----------------------------------------------------


# Instruct bash to stop the script on error,
# or if an environment variable is not defined.
set -o errexit
set -o nounset

mkdir release
cd release
mkdir changes
hg clone ../public clone

ln -s ../public/build/src/site/resources/release/update-version.xml
ln -s ../public/build/src/site/resources/release/fix-html.xml
ln -s ../public/build/src/site/resources/release/release.sh
chmod +x release.sh
