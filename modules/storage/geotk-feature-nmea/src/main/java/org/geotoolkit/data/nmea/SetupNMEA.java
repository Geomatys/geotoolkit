/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.nmea;

import gnu.io.CommPortIdentifier;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.SetupService;
import org.geotoolkit.nio.IOUtilities;

/**
 * Load native libraries for NMEA.
 *
 * @author Alexis Manin (Geomatys)
 */
public class SetupNMEA implements SetupService {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.nmea");
    private static final String LIB8PATH = "/lib";
    private static final String[] RXTX_LIB_NAMES = new String[] {
      "rxtxSerial",
      "rxtxParallel",
      "rxtxI2C",
      "rxtxRaw",
      "rxtxRS485"
    };

    @Override
    public void initialize(final Properties properties, final boolean reinit) {

        try {
            // Create folder where we'll store native libraries...
            final Path tmpFolder = Files.createTempDirectory("geotk_nmea_lib");
            // ... and add its location to the library path.
            addLibraryPath(tmpFolder.toAbsolutePath().toString());

            // build library folder location. Windows /mac are special cases, as versions have different names.
            String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (osName.contains("windows")) {
                osName = "windows";
            } else if (osName.contains("mac")) {
                osName = "mac";
            }
            final StringBuilder libDirBuilder = new StringBuilder(LIB8PATH);
            libDirBuilder.append('/')
                    .append(System.getProperty("os.arch"))
                    .append('/')
                    .append(osName)
                    .append('/');

            final String libDir = libDirBuilder.toString();

            for (final String libName : RXTX_LIB_NAMES) {
                final String libFile = System.mapLibraryName(libName);
                final InputStream resource = SetupNMEA.class.getResourceAsStream(libDir + libFile);
                final Path tmpFile = tmpFolder.resolve(libFile);
                IOUtilities.writeStream(resource, tmpFile);
            }

           CommPortIdentifier.getPortIdentifiers();

        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "A problem occured while loading RXTX native libraries. Port communication might not work.", ex);
        } catch (UnsatisfiedLinkError er){
            LOGGER.log(Level.INFO, "A problem occured while loading RXTX native libraries. Library not found for "+System.getProperty("os.name")+" "+System.getProperty("os.arch")+" architecture.");
        }

    }

    @Override
    public void shutdown() {
    }

    /**
     * Add a path to system property 'java.library.path', to allow JVM searching
     * into given directory when loading a library.
     * Note that it's a hack solution, used because of the fact that we must let
     * gnu.io package load its native libraries itself.
     *
     * Original code from <href url=http://stackoverflow.com/questions/15409223/adding-new-paths-for-native-libraries-at-runtime-in-java>here</href>.
     * @param pathToAdd
     */
    private static void addLibraryPath(final String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

}
