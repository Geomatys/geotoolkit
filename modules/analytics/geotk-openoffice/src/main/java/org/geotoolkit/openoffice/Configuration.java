/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.openoffice;

import java.io.*;
import java.net.URI;

import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XStringSubstitution;
import com.sun.star.lang.XMultiServiceFactory;


/**
 * Edit the OpenOffice.org configuration. <strong>This is dangerous</strong>, but I have not
 * yet found a better way to get the HSQL dependency in the classpath. We should remove this
 * class if we find a safer way to do this job.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class Configuration {
    /**
     * The name of the key to look for in the {@link #CONFIG_FILES}.
     */
    private static final String URE_MORE_JAVA_CLASSPATH_URLS = "URE_MORE_JAVA_CLASSPATH_URLS";

    /**
     * The JAR file name of HSQLDB.
     */
    static final String HSQL_JAR = "hsqldb.jar";

    /**
     * Do not allow instantiation of this class.
     */
    private Configuration() {
    }

    /**
     * Verifies if the {@code hsqldb.jar} dependencies exists and is declared in the
     * {@code URE_MORE_JAVA_CLASSPATH_URLS} classpath of the {@code fundamentalbasis}
     * file. If the HSQL dependency is not declared in the above file and if we have
     * write authorization, appends the {@code hsqldb.jar} in that file.
     * <p>
     * If we can not modify the {@code fundamentalbasis}, returns the file to the {@code hsqldb.jar}.
     * In such case, we will need to use our own classloader for loading those classes.
     *
     * @return A non-null file to {@code hsqldb.jar} if a custom classloader is required
     *         for loading that driver, or {@code null} if no custom classloader is needed.
     * @throws Exception Numerous possible exceptions, including unchecked ones
     *         (security exceptions, etc.).
     */
    static File setMoreJavaClasspathURLs(final XMultiServiceFactory factories) throws Exception {
        final Object service = factories.createInstance("com.sun.star.util.PathSubstitution");
        final XStringSubstitution subst = UnoRuntime.queryInterface(XStringSubstitution.class, service);
        final File programDirectory = new File(new URI(subst.getSubstituteVariableValue("$(prog)")));
        final File jarFile = new File(programDirectory, "classes/" + HSQL_JAR);
        if (!jarFile.isFile()) {
            // hsqldb.jar not found. We should report a warning
            // to the user, but I don't know how to do that...
            return null;
        }
        File configFile = new File(programDirectory, "fundamentalbasisrc"); // Unix of MacOS
        if (configFile == null) {
            configFile = new File(programDirectory, "fundamentalbasis.ini"); // Windows
            if (configFile == null) {
                return jarFile; // Configuration file not found.
            }
        }
        if ((configFile.canRead() && configFile.canWrite()) || chmod(configFile, "u+rw")) {
            /*
             * Found the "fundamentalbasis" file. Reads its content, searching for the
             * URE_MORE_JAVA_CLASSPATH_URLS line. We will edit that line if we find it.
             */
            final BufferedReader reader = new BufferedReader(new FileReader(configFile));
            final String lineSeparator  = System.getProperty("line.separator", "\n");
            final StringBuilder buffer  = new StringBuilder();
            boolean canWrite = false;
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                if (line.startsWith(URE_MORE_JAVA_CLASSPATH_URLS)) {
                    final int s = line.indexOf('=', URE_MORE_JAVA_CLASSPATH_URLS.length());
                    if (s >= 0) {
                        if (line.indexOf(HSQL_JAR, s) >= 0) {
                            // If HSQL is already on the classpath, we are done.
                            reader.close();
                            return null;
                        }
                        if (s != line.trim().length()-1) {
                            buffer.append(' '); // The path separator used by OOo.
                        }
                        buffer.append("${ORIGIN}/classes/" + HSQL_JAR);
                        canWrite = true;
                    }
                }
                buffer.append(lineSeparator);
            }
            reader.close();
            /*
             * At this point we finished to read and edit the "fundamentalbasis" file,
             * or we cancelled the process. If we finished reading, we can write it.
             */
            if (canWrite) {
                final File tmpFile = replaceExtension(configFile, ".tmp");
                final File bakFile = replaceExtension(configFile, ".bak");
                final FileWriter writer = new FileWriter(tmpFile);
                writer.write(buffer.toString());
                writer.close();
                bakFile.delete();
                if (configFile.renameTo(bakFile)) {
                    if (tmpFile.renameTo(configFile)) {
                        return jarFile; // Success will take effect after OOo will be restarted.
                    }
                    bakFile.renameTo(configFile); // Rollback.
                }
                tmpFile.delete();
            }
        }
        return jarFile;
    }

    /**
     * Replaces the extension of the given file. The given extension shall start with the
     * {@code '.'} separator.
     */
    private static File replaceExtension(final File file, final String extension) {
        String name = file.getName();
        final int s = name.lastIndexOf('.');
        if (s >= 0) {
            name = name.substring(0, s);
        }
        return new File(file.getParentFile(), name + extension);
    }

    /**
     * Runs the Unix {@code chmod} command on the given file.
     */
    private static boolean chmod(final File file, final String options) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(new String[] {"chmod", options, file.getPath()});
        process.waitFor();
        return process.exitValue() == 0;
    }
}
