#!/bin/sh
# Create the EPSG database once, then flag it read-only: HSQLDB takes no lock on a
# read-only database, so every parallel surefire fork can open it concurrently.
#
# Without this, parallel module builds (-T / mvnd) have several forks opening the
# same $SIS_DATA/Databases/SpatialMetadata file database, and racing to install the
# EPSG dataset into it when it does not exist yet.
set -e

DB="$SIS_DATA/Databases/SpatialMetadata"
if [ -f "$DB.data" ]; then
  echo "SpatialMetadata already initialised"
  exit 0
fi

# geotk-utility has no intra-project dependency, so its classpath resolves without
# building the reactor. It brings sis-referencing; sis-epsg and hsqldb come from the
# test-scoped dependencies declared for all modules in the root pom.
CP=$(mktemp)
mvn -B -ntp -q -s "$MAVEN_SETTINGS" -f geotk-utility/pom.xml \
    dependency:build-classpath -Dmdep.includeScope=test -Dmdep.outputFile="$CP"

# Asking for any CRS is enough: SIS creates and populates the database on first use,
# which it only does when SIS_DATA is set and the database is missing.
SRC="$(mktemp -d)/InitSisData.java"
cat > "$SRC" <<'JAVA'
import org.apache.sis.referencing.CRS;
public class InitSisData {
    public static void main(String[] args) throws Exception {
        System.out.println("EPSG dataset installed: " + CRS.forCode("EPSG:2154").getName());
    }
}
JAVA
java -cp "$(cat "$CP")" "$SRC"

# SIS's shutdown hook closed HSQLDB on JVM exit; a leftover lock means it did not,
# and flagging a half-written database read-only would make it unusable.
if [ -e "$DB.lck" ]; then
  echo "ERROR: $DB.lck still present, database was not closed cleanly" >&2
  exit 1
fi
printf 'readonly=true\n' >> "$DB.properties"
echo "SpatialMetadata is now read-only"
