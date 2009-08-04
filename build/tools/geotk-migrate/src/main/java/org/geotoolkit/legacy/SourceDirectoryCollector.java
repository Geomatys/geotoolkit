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
 * Collects source directories of a Maven project. This classes assumes that the project
 * use the standard Maven layout, which is the case of GeoTools and Geotk projects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class SourceDirectoryCollector implements FileFilter {
    /**
     * The source directories.
     */
    private final Set<File> directories = new LinkedHashSet<File>();

    /**
     * Creates a new collector of source directories.
     *
     * @param projectRoot The root directory of the Maven project.
     */
    SourceDirectoryCollector(final String projectRoot) {
        searchSourceDirectories(new File(projectRoot));
    }

    /**
     * Filter directories only. This method ignores hidden directory, directory starting
     * with dot (same as hidden on Unix but not necessarly on Windows) and the "target"
     * directory created by Maven.
     */
    @Override
    public boolean accept(final File directory) {
        if (directory.isDirectory() && !directory.isHidden()) {
            final String name = directory.getName();
            return !name.equals("target") && !name.startsWith(".");
        }
        return false;
    }

    /**
     * Searchs for {@code src/main/java} directories. We assume a maven-compliant
     * directory layout.
     */
    private void searchSourceDirectories(File directory) {
        final String name = directory.getName();
        if (name.equals("src")) {
            directory = new File(directory, "main");
            if (directory.isDirectory()) {
                directory = new File(directory, "java");
                if (directory.isDirectory()) {
                    directories.add(directory);
                }
            }
        } else {
            for (final File candidate : directory.listFiles(this)) {
                searchSourceDirectories(candidate);
            }
        }
    }

    /**
     * Searchs for source {@code ".java"} files and add them in the given set.
     * The name stored in the set are fully-qualified Java name.
     *
     * @param dir    The directory to scan.
     * @param base   The package name. This string ends with a ".", except if it is the root package.
     * @param prefix The required prefix of source files to search. That prefix will be removed
     *               from the string stored in the set.
     * @param addTo  The set where to add the names.
     */
    private static void searchSourceFiles(final File dir, final String base,
            final String prefix, final Set<String> addTo)
    {
        for (final File file : dir.listFiles()) {
            if (file.isHidden()) {
                continue;
            }
            String name = file.getName();
            if (name.startsWith(".")) {
                continue;
            }
            if (file.isDirectory()) {
                searchSourceFiles(file, base + name + '.', prefix, addTo);
                continue;
            }
            if (!name.endsWith(".java") || name.startsWith("package-info")) {
                continue;
            }
            name = name.substring(0, name.length() - 5); // Remove the ".java" suffix.
            name = base + name;
            if (name.startsWith(prefix)) {
                addTo.add(name.substring(prefix.length()));
            }
        }
    }

    /**
     * Returns the source files found in all source directories.
     * The prefix argument gives the required prefix of source files to search.
     * That prefix will be removed from the string stored in the set.
     */
    public Set<String> getSourceFiles(final String prefix) {
        final Set<String> addTo = new LinkedHashSet<String>();
        for (final File directory : directories) {
            searchSourceFiles(directory, "", prefix, addTo);
        }
        return addTo;
    }

    /**
     * Returns the source directories.
     */
    public Set<File> getSourceDirectories() {
        return Collections.unmodifiableSet(directories);
    }
}
