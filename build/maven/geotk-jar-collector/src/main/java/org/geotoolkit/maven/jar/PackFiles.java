/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.maven.jar;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Packs javadoc and source files.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class PackFiles implements FileFilter, Comparator<File> {
    /**
     * The directory where to write the ZIP files.
     */
    private final File targetDirectory;

    /**
     * The source directory for the javadoc or the source files.
     */
    private String sourcePath;

    /**
     * The output stream in process of being written.
     */
    private ZipOutputStream out;

    /**
     * Temporary buffer for copying data.
     */
    private final byte[] buffer = new byte[8 * 1024];

    /**
     * Creates a new object which will packs the javadoc and source files in the given directory.
     *
     * @param targetDirectory The directory where to write the ZIP files.
     */
    public PackFiles(final File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**
     * Filters the files to be included in the ZIP file. We ommit hidden files and everything
     * begining with a dot (this is the same than hidden files on Unix only). Also ommit the
     * target directory created by Maven, and the Derby log file.
     */
    @Override
    public boolean accept(final File file) {
        if (file.isHidden()) {
            return false;
        }
        final String name = file.getName();
        if (file.isDirectory()) {
            if (name.equalsIgnoreCase("target")) {
                return false;
            }
        }
        if (file.isFile()) {
            if (name.equalsIgnoreCase("derby.log")) {
                return false;
            }
        }
        return !name.startsWith(".");
    }

    /**
     * Sorts the files to be included in ZIP files. We put files before directories.
     */
    @Override
    public int compare(final File file1, final File file2) {
        if (file1.isDirectory()) {
            if (!file2.isDirectory()) {
                return +1;
            }
        } else if (file2.isDirectory()) {
            return -1;
        }
        return file1.getName().compareToIgnoreCase(file2.getName());
    }

    /**
     * Packs everything in the given source directory.
     *
     * @param  source The source directory containing the file to ZIP.
     * @param  targetFilename The name of the file to create.
     * @throws IOException if an error occurred while packing the javadoc.
     */
    public void pack(final File sourceDirectory, final String targetFilename) throws IOException {
        sourcePath = sourceDirectory.getPath();
        if (!sourcePath.endsWith(File.separator)) {
            sourcePath += File.separatorChar;
        }
        final File outFile = new File(targetDirectory, targetFilename);
        out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        out.setLevel(9);
        addEntries(sourceDirectory);
        out.close();
        out = null;
        sourcePath = null;
    }

    /**
     * Adds the entries for the given directory. This method invokes itself recursively.
     */
    private void addEntries(final File directory) throws IOException {
        final File[] content = directory.listFiles(this);
        if (content == null) {
            return;
        }
        Arrays.sort(content, this);
        for (final File file : content) {
            final boolean isDirectory = file.isDirectory();
            String name = file.getPath();
            if (!name.startsWith(sourcePath)) {
                // Should never happen.
                throw new IOException("Illegal path: " + name);
            }
            name = name.substring(sourcePath.length()).replace(File.separatorChar, '/');
            if (isDirectory) {
                name += '/';
            }
            final ZipEntry entry = new ZipEntry(name);
            entry.setTime(file.lastModified());
            if (isDirectory) {
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(0);
                entry.setCompressedSize(0);
                entry.setCrc(0);
            } else {
                entry.setMethod(ZipEntry.DEFLATED);
                entry.setSize(file.length());
            }
            out.putNextEntry(entry);
            if (!isDirectory) {
                final InputStream in = new FileInputStream(file);
                int n; while ((n = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, n);
                }
                in.close();
            }
            out.closeEntry();
            if (isDirectory) {
                addEntries(file);
            }
        }
    }
}
