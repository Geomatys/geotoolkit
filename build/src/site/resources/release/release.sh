#!/bin/sh

# ------------------------------------------------------------
# Perform a Geotk release. See the 'index' file for
# more information about how to use this script.
#
# Prerequites:
#   - OLD_VERSION defined.
#   - NEW_VERSION defined.
#   - GEOTK_SERVER defined (e.g.: user@server.com)
#   - Directory GEOTK_SERVER:geotk-release/packs shall exists.
# ------------------------------------------------------------


# Instruct bash to stop the script on error,
# or if an environment variable is not defined.
set -o errexit
set -o nounset


#
# Create the tag. This operation will fail right at the beginning
# (with no change performed on the repository) if the tag already
# exists.
#
echo
echo "---------------------------------------------"
echo "Pushing the $NEW_VERSION tag."
echo "---------------------------------------------"
echo
hg tag "$NEW_VERSION"
echo "Pushing the tag to https://hg.geotoolkit.org"
hg push


#
# Copy the changes to the working repository and update the version
# numbers locally. We will ask for confirmation before to continue.
#
echo
echo "---------------------------------------------"
echo "Pulling the changes in the release directory"
echo "and updating version numbers locally."
echo "---------------------------------------------"
echo
cd ../release/clone
hg pull --update
ant -file ../update-version.xml -Dgeotk.version=$NEW_VERSION
hg diff
echo
echo "Please check that the above version number changes are correct."
echo "Press [Enter] to continue, or [Ctrl-C] to interrupt."
read


#
# Deploy the JAR files.
#
mvn deploy --activate-profiles package
mvn site --activate-profiles \!no-site --define skipTests
mvn org.geotoolkit.project:geotk-jar-collector:pack-specific --non-recursive
echo "Copying the bundles to $GEOTK_SERVER:geotk-release"
scp target/bundles/* $GEOTK_SERVER:geotk-release
scp modules/analytics/geotk-openoffice/target/geotk-$NEW_VERSION.oxt $GEOTK_SERVER:geotk-release


#
# Prepare PACK200 files
#
cd target/binaries
mv -i ../../demos/geotk-simples/target/geotk-simples-$NEW_VERSION.jar .
find geotk-*.jar -exec zip -d '{}' META-INF/INDEX.LIST \;
find *.jar -exec pack200 --strip-debug --no-keep-file-order --segment-limit=-1 --effort=9 --deflate-hint=true '{}'.pack.gz '{}' \;
echo "Copying the bundles to $GEOTK_SERVER:geotk-release/packs"
scp *.gz $GEOTK_SERVER:geotk-release/packs
cd -


#
# Generate javadoc and run JDiff.
#
cd ..
mkdir $NEW_VERSION
cd $NEW_VERSION
cp -r ../clone/modules .
java -cp .. Move
cd -
./jdiff.sh
cd changes
ant -file ../fix-html.xml
scp -r changes changes.html $GEOTK_SERVER:geotk-release

#
# Cleanup.
#
rm -r *
cd -
rm Geotk-*.xml
rm -r $OLD_VERSION
cd clone
mvn clean --quiet
hg revert --all --no-backup
cd ../..
