#!/bin/sh

#
# Define the variables used for every scripts in the ../bin directory.
# Those variables may be modified in order to fit a particular system
# configuration. Those variables are locale to the scripts execution;
# they will not affect the system-wide configuration.
#

# The version of Geotk JAR files to use.
export GEOTOOLKIT_VERSION=SNAPSHOT

# The directory which contains every JAR files.
export JARS="$BASE_DIR/jar"

# The default classpath, in addition to the classpath specific to the script being executed.
export CLASSPATH="$JARS/postgresql-9.0-801.jdbc4.jar"

# Options used by every scripts.
export OPTS="-Djava.util.logging.config.file=$BASE_DIR/etc/logging.properties"
