/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.legacy;

import java.io.*;
import java.util.*;


/**
 * Verifies the content of the {@code ClassChanges.properties} file. This tool searchs
 * for {@code *.java} files in {@code src/main/java} directories. Classes declared in
 * the properties file but for which no corresponding Java file were found will be listed
 * to standard output.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class ClassChangesChecker extends MigrateFromGeoTools {
    /**
     * Initializes the checker.
     */
    ClassChangesChecker() throws IOException {
        super(true); // Run in "pretend" mode.
    }

    /**
     * Checks if all classes in the {@link #table} exists.
     * The current directory must be the root of the maven project.
     *
     * @param geotoolkit {@code false} for testing GeoTools sources, or
     *        {@code true} for testing Geotoolkit sources.
     */
    void check(final boolean geotoolkit) {
        final Set<File> sources = new SourceDirectoryCollector(".").getSourceDirectories();
        final String prefix;
        final Collection<String> classes;
        if (geotoolkit) {
            classes = table.values();
            prefix = MigrateFromGeoTools.GEOTOOLKIT_PREFIX;
        } else {
            classes = table.keySet();
            prefix = MigrateFromGeoTools.GEOTOOLS_PREFIX;
        }
search: for (String c : classes) {
            c = prefix + c;
            String filename = c;
            final int inner = filename.indexOf('$');
            if (inner >= 0) {
                filename = filename.substring(0, inner);
            }
            filename = filename.replace('.', '/') + ".java";
            for (final File directory : sources) {
                if (new File(directory, filename).isFile()) {
                    continue search;
                }
            }
            System.out.println(c);
        }
    }

    /**
     * Lists the GeoTools classes which have been ported to Geotoolkit. If non-null, the given
     * command is printed before the file. This is a convenience for building a file of commands
     * to execute, for example {@code "hg remove").
     */
    void listPortedClasses(final String command) {
        final Set<File> directories  = new SourceDirectoryCollector(".").getSourceDirectories();
        for (String name : table.keySet()) {
            if (name.lastIndexOf('$') >= 0) {
                continue; // An inner class.
            }
            name = GEOTOOLS_PREFIX + name;
            name = name.replace('.', File.separatorChar);
            name = name + ".java";
            for (final File directory : directories) {
                final File file = new File(directory, name);
                if (file.exists()) {
                    name = file.getPath();
                    break;
                }
            }
            System.out.print(command);
            System.out.print(' ');
            System.out.println(name);
        }
        System.out.flush();
    }

    /**
     * Prints to the standard output stream the intersection of the content of two given
     * project. The first argument must be the root directory of the GeoTools Maven project,
     * while the second argument must be the root directory of the Geotoolkit Maven project.
     */
    void intersect(final String gtRoot, final String gtoRoot) {
        final Set<String> gtSources  = new SourceDirectoryCollector(gtRoot) .getSourceFiles(GEOTOOLS_PREFIX);
        final Set<String> gtoSources = new SourceDirectoryCollector(gtoRoot).getSourceFiles(GEOTOOLKIT_PREFIX);
        gtoSources.retainAll(gtSources);
        final String[] names = gtoSources.toArray(new String[gtoSources.size()]);
        Arrays.sort(names);
        for (final String name : names) {
            System.out.println(name);
        }
    }
}
