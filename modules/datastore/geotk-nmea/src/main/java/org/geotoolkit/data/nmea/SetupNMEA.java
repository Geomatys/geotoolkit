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
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.SetupService;
import org.geotoolkit.util.FileUtilities;

/**
 * Load native libraries for NMEA.
 *
 * @author Alexis Manin (Geomatys)
 */
public class SetupNMEA implements SetupService {

    private static final Logger LOGGER = Logging.getLogger(SetupNMEA.class);
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

        // Create folder where we'll store native libraries...
        final String tempDir = System.getProperty("java.io.tmpdir");
        final File tmpFolder = new File(tempDir, UUID.randomUUID().toString());
        if (!tmpFolder.isDirectory()) {
            tmpFolder.mkdir();
            tmpFolder.deleteOnExit();
        }

        try {
            // ... and add its location to the library path.
            addLibraryPath(tmpFolder.getAbsolutePath());

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
                final InputStream resource = SetupNMEA.class.getResourceAsStream(libDir.toString() + libFile);
                final File tmpFile = new File(tmpFolder, libFile);
                FileUtilities.buildFileFromStream(resource, tmpFile);
                tmpFile.deleteOnExit();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "A problem occured while loading RXTX native libraries. Port communication might not work.", ex);
        }

        CommPortIdentifier.getPortIdentifiers();
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
