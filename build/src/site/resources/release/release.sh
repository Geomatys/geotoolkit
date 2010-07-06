#!/bin/sh

# -------------------------------------------------
# Perform a Geotk release. See the 'index' file for
# more information about how to use this script.
# -------------------------------------------------


# Instruct bash to stop the script on error,
# or if an environment variable is not defined.
set -o errexit
set -o nounset

# Tag the project.
hg tag "$NEW_VERSION"
echo "Pushing the tag to https://hg.geotoolkit.org"
hg push

# Copy the changes to the working repository.
cd ../release/clone
hg pull --update

# Update version number, and ask for confirmation.
ant -file ../update-version.xml -Dgeotk.version=$NEW_VERSION
hg diff
echo
echo "Please check that the above version number changes are correct.
echo "Press [Enter] to continue, or [Ctrl-C] to interrupt."
read

# Deploy the JAR files.
mvn deploy -Ppackage
mvn javadoc:aggregate -P\!no-site
mvn org.geotoolkit.project:geotk-jar-collector:pack-specific --non-recursive
echo "Copying the bundles to $GEOTK_SERVER:geotk-release"
scp target/bundles/* $GEOTK_SERVER:geotk-release

# TODO Next steps to be added later.
