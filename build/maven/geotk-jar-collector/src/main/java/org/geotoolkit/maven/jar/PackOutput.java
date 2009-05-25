/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.jar.*;
import java.io.File;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.jar.Pack200.Packer.*;


/**
 * A JAR file to be created for output by {@link Packer}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class PackOutput implements Closeable {
    /**
     * The packer that created this object. Will be used in order to fetch
     * additional informations like the version to declare in the pom.xml file.
     */
    private final Packer packer;

    /**
     * The output file.
     */
    private File file;

    /**
     * The stream where to write the JAR. Will be created
     * only when {@link #open} will be invoked.
     */
    private JarOutputStream out;

    /**
     * The main class, or {@code null} if none. We will set this field to the main class
     * of the last {@link PackInput} to be used by this {@code PackOutput}. This is on
     * the assumption that the last input is the main one.
     */
    private String mainClass;

    /**
     * The JAR to be used as inputs.
     */
    private final Set<File> inputs;

    /**
     * The entries which were already done by previous invocation of {@link #getInputStream}.
     */
    private final Set<String> entriesDone = new HashSet<String>();

    /**
     * Creates an output jar.
     *
     * @param packer    The packer that created this object.
     * @param parent    The parent, or {@code null} if none.
     * @param jars      The JAR filenames.
     */
    PackOutput(final Packer packer, final PackOutput parent, final String[] jars) {
        this.packer = packer;
        if (parent != null) {
            inputs = new LinkedHashSet<File>(parent.inputs);
        } else {
            inputs = new LinkedHashSet<File>(jars.length * 4/3);
        }
        for (final String jar : jars) {
            final File file = new File(packer.jarDirectory, jar);
            if (!file.isFile()) {
                throw new IllegalArgumentException("Not a file: " + file);
            }
            if (!inputs.add(file)) {
                throw new IllegalArgumentException("Duplicated JAR: " + file);
            }
        }
    }

    /**
     * Returns {@code true} if this pack contains the given JAR file.
     *
     * @param  file The JAR file to check for inclusion.
     * @return {@code true} if this pack contains the given JAR file.
     */
    boolean contains(final File file) {
        return inputs.contains(file);
    }

    /**
     * Copies the entries from the given {@code mapping} to the given {@code actives} map, but
     * only those having a key included in the set of input files used by this {@code PackOutput}.
     *
     * @param mapping The mapping from {@link File} to {@link PackInput}.
     * @param actives Where to store the {@link PackInput} required for
     *        input by this {@code PackOutput}.
     */
    void copyInputs(final Map<File,PackInput> mapping, final Map<File,PackInput> actives) {
        for (final File file : inputs) {
            final PackInput input = mapping.get(file);
            if (input != null) {
                final PackInput old = actives.put(file, input);
                if (old != null && old != input) {
                    throw new AssertionError("Inconsistent mapping.");
                }
            }
        }
    }

    /**
     * Opens the JAR files that were not already opens and store them in the given map.
     *
     * @param  inputs The map where to store the opened JAR files.
     * @throws IOException If a file can not be open.
     */
    void createPackInputs(final Map<File,PackInput> inputs) throws IOException {
        for (final File jar : this.inputs) {
            PackInput in = inputs.get(jar);
            if (in == null) {
                in = new PackInput(jar);
                if (inputs.put(jar, in) != null) {
                    throw new AssertionError(jar);
                }
            }
            if (in.mainClass != null) {
                mainClass = in.mainClass;
            }
        }
    }

    /**
     * Opens the given JAR file for writting
     *
     * @param  file The file to open.
     * @throws IOException if the file can't be open.
     */
    void open(final File file) throws IOException {
        this.file = file;
        final Manifest manifest = new Manifest();
        final Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION,       "1.0");
        attributes.put(Attributes.Name.SPECIFICATION_VENDOR,   "Geotoolkit");
        attributes.put(Attributes.Name.IMPLEMENTATION_VERSION, packer.version);
        attributes.put(Attributes.Name.IMPLEMENTATION_URL,     "http://www.geotoolkit.org/");
        if (mainClass != null) {
            attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
        }
        out = new JarOutputStream(new FileOutputStream(file), manifest);
        out.setLevel(1); // Use a cheap compression level since this JAR file is temporary.
    }

    /**
     * Returns {@code true} if entries of the given name are allowed to be concatenated
     * if they appear in more than one input JAR files.
     */
    static boolean mergeAllowed(final String name) {
        return name.startsWith(PackInput.SERVICES);
    }

    /**
     * Begins writting a new JAR entry.
     *
     * @param  entry The new entry to write.
     * @return {@code true} if the entry is ready to write, or {@code false} if it should be skipped.
     * @throws IOException If a failure occurs while creating the entry.
     */
    boolean putNextEntry(final JarEntry entry) throws IOException {
        final String name = entry.getName();
        if (entry.isDirectory() || mergeAllowed(name)) {
            if (!entriesDone.add(name)) {
                return false;
            }
        }
        out.putNextEntry(entry);
        return true;
    }

    /**
     * Writes the given number of bytes.
     *
     * @param  buffer The buffer containing the bytes to write.
     * @param  n The number of bytes to write.
     * @throws IOException if an exception occured while writting the bytes.
     */
    void write(final byte[] buffer, final int n) throws IOException {
        out.write(buffer, 0, n);
    }

    /**
     * Close the current entry.
     *
     * @throws IOException If an error occured while closing the entry.
     */
    void closeEntry() throws IOException {
        out.closeEntry();
    }

    /**
     * Closes this output.
     *
     * @throws IOException if an error occured while closing the file.
     */
    @Override
    public void close() throws IOException {
        if (out != null) {
            out.close();
        }
        out = null;
    }

    /**
     * Packs the output JAR.
     *
     * @throws IOException if an error occured while packing the JAR.
     */
    void pack() throws IOException {
        if (out != null) {
            throw new IllegalStateException("JAR output stream not closed.");
        }
        final File inputFile = file;
        String filename = inputFile.getName();
        final int ext = filename.lastIndexOf('.');
        if (ext > 0) {
            filename = filename.substring(0, ext);
        }
        filename += ".pack.gz";
        final File outputFile = new File(inputFile.getParent(), filename);
        if (outputFile.equals(inputFile)) {
            throw new IOException("Input file is already a packed: " + inputFile);
        }
        /*
         * Now process to the compression.
         */
        final Pack200.Packer packer = Pack200.newPacker();
        final Map<String,String> p = packer.properties();
        p.put(EFFORT, String.valueOf(9));  // Maximum compression level.
        p.put(KEEP_FILE_ORDER,    FALSE);  // Reorder files for better compression.
        p.put(MODIFICATION_TIME,  LATEST); // Smear modification times to a single value.
        p.put(DEFLATE_HINT,       FALSE);  // Ignore all JAR deflation requests,
        p.put(UNKNOWN_ATTRIBUTE,  ERROR);  // Throw an error if an attribute is unrecognized
        final JarFile jarFile = new JarFile(inputFile);
        final OutputStream out = new GZIPOutputStream(new FileOutputStream(outputFile));
        packer.pack(jarFile, out);
        out.close();
        jarFile.close();
        if (!inputFile.delete()) {
            throw new IOException("Can't delete temporary file: " + inputFile);
        }
    }
}
