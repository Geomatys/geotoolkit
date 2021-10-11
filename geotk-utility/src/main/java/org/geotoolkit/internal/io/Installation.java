/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.prefs.Preferences;
import org.apache.sis.internal.system.OS;
import org.apache.sis.util.logging.Logging;


/**
 * Methods related to the Geotoolkit.org installation directory. This is provided for data that need
 * to be saved in a user-specified directory. If the user didn't specified any directory, they
 * will be saved in the temporary directory.
 * <p>
 * We try to keep the configuration options to a strict minimum, but we still need is some case
 * to specify in which directory are stored the data, for example the NADCON data used for datum
 * shift over United States.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @module
 */
public enum Installation {
    /**
     * The root directory of Geotoolkit.org installation.
     */
    ROOT_DIRECTORY("org/geotoolkit", "Root directory", null),

    /**
     * The directory where to put configuration files for the tests.
     * This is used only during Maven builds, for example in order to
     * fetch connection parameters to a database.
     * <p>
     * This field is read by {@link org.geotoolkit.test.image.ImageTestBase} using Java reflection.
     * This needs to be keep in mind in case of refactoring, since refactoring tools may not detect
     * this case.
     *
     * @since 3.10
     */
    TESTS(null, null, "Tests"),

    /**
     * Where to store the metadata database used by SIS. Used only if the user did not set the
     * {@code SIS_DATA} environment variable or the {@code "derby.system.home"} property.
     */
    SIS("org/geotoolkit/metadata", "SIS", "Databases"),

    /**
     * The parameters required for a connection to a distant EPSG database.
     */
    EPSG("org/geotoolkit/referencing/factory", "EPSG", "EPSG"),

    /**
     * The directory of the properties file which contains the parameters required for a
     * connection to a distant Coverages database.
     *
     * @since 3.11
     */
    COVERAGES("org/geotoolkit/coverage/sql", "Database", "Coverages");

    /**
     * The user configuration file for a connection to a JDBC database.
     *
     * @since 3.11
     */
    public static final String DATASOURCE_FILE = "DataSource.properties";

    /**
     * The preference node and key for storing the value of this configuration option.
     * May be {@code null} if the value should not be stored in any preference node.
     */
    private final String node, key;

    /**
     * The default subdirectory in the root directory, or
     * {@code null} if this key is for the root directory.
     */
    private final String directory;

    /**
     * The default root directory. Computed only once at class initialization time in
     * order to make sure that the value stay consistent during all the JVM execution.
     */
    private static final Path DEFAULT_ROOT = root();

    /**
     * Creates a new configuration key.
     *
     * @param node The preference node where to store the configuration value.
     * @param key  The key where to store the value in the above node.
     * @param directory The default subdirectory in the root directory.
     */
    private Installation(final String node, final String key, final String directory) {
        this.node = node;
        this.key = key;
        this.directory = directory;
    }

    /**
     * Returns the preferences node.
     */
    private Preferences preference() {
        return Preferences.userRoot();
    }

    /**
     * Sets the preference to the given value. If the preference is set for the current user,
     * then the system preference is left untouched. But if the preference is set for the system,
     * we assume that it applies to all users including the current one, so the current user
     * preference is removed.
     *
     * @param value The preference value, or {@code null} for removing it.
     */
    public final void set(final String value) {
        try {
            final Preferences prefs = preference();
            if (value != null) {
                prefs.put(key, value);
            } else {
                prefs.remove(key);
            }
        } catch (SecurityException e) {
            Logging.recoverableException(Logging.getLogger("org.geotoolkit"), Installation.class, "set", e);
        }
    }

    /**
     * Returns the preference, or {@code null} if none.
     *
     * @return The preference value, or {@code null} if none.
     */
    public final String get() {
        try {
            if (key != null) {
                return preference().get(key, null);
            }
        } catch (SecurityException e) {
            Logging.recoverableException(Logging.getLogger("org.geotoolkit"), Installation.class, "set", e);
        }
        return null;
    }

    /**
     * Returns the default root directory, ignoring user's preferences.
     * This method is used only for the initialization of the {@link #DEFAULT_ROOT}
     * static constant.
     *
     * @return The default installation root directory.
     */
    private static Path root() {
        try {
            final OS system = OS.current();
            if (system == OS.WINDOWS) {
                final String app = System.getenv("APPDATA");
                if (app != null) {
                    final Path file = Paths.get(app);
                    if (Files.isDirectory(file)) {
                        return file.resolve("Geotoolkit.org");
                    }
                }
            }
            final String directory = System.getProperty("user.home");
            if (directory != null) {
                Path file =  Paths.get(directory);
                String name = ".geotoolkit.org";
                switch (system) {
                    case WINDOWS: {
                        file = file.resolve("Application Data");
                        name = "Geotoolkit.org";
                        break;
                    }
                    case MAC_OS: {
                        file = file.resolve("Library");
                        name = "Geotoolkit.org";
                        break;
                    }
                    // For Linux and unknown OS, keep the directory selected above.
                }
                if (Files.isDirectory(file) && (!system.unix || Files.isWritable(file))) {
                    return file.resolve(name);
                }
            }
        } catch (SecurityException e) {
            Logging.getLogger("org.geotoolkit").warning(e.toString());
        }
        return Paths.get(System.getProperty("java.io.tmpdir"), "Geotoolkit.org");
    }

    /**
     * If the preference is defined, returns its value as a {@link Path}. Otherwise returns a
     * sub-directory of the <cite>root directory</cite> where the later is defined as the first
     * of the following directories which is found suitable:
     * <p>
     * <ul>
     *   <li>{@link #ROOT_DIRECTORY} user preferences, if defined.</li>
     *   <li>{@link #ROOT_DIRECTORY} system preferences, if defined.</li>
     *   <li>{@code ".geotoolkit"} subdirectory in the user home directory,
     *       if the user home directory exists and is writable.</li>
     *   <li>{@code "Geotoolkit"} subdirectory in the temporary directory.</li>
     * </ul>
     *
     * @param  usePreferences Usually {@code true}. If {@code false}, the preferences
     *         are ignored and only the default directory is returned.
     * @return The directory (never {@code null}).
     */
    public Path directory(final boolean usePreferences) {
        if (usePreferences) {
            final String candidate = get();
            if (candidate != null) {
                return Paths.get(candidate);
            }
        }
        if (directory != null) {
            return ROOT_DIRECTORY.directory(true).resolve(directory);
        } else {
            return DEFAULT_ROOT;
        }
    }

    /**
     * Returns the content of the {@value #DATASOURCE_FILE} file as a properties map,
     * or {@code null} if the file does not exist.
     *
     * @return The content of the {@value #DATASOURCE_FILE} file, or {@code null}.
     * @throws IOException If an error occurred while reading the file.
     *
     * @since 3.11
     */
    public Properties getDataSource() throws IOException {
        final Path file = directory(true).resolve(DATASOURCE_FILE);
        if (Files.isRegularFile(file)) {
            final Properties properties;
            try (InputStream in = Files.newInputStream(file)) {
                properties = new Properties();
                properties.load(in);
            }
            return properties;
        }
        return null;
    }
}
