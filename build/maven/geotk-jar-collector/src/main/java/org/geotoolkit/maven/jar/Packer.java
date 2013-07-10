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
package org.geotoolkit.maven.jar;

import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.jar.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;


/**
 * Creates PAC200 files from the JAR builds by Maven.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public final class Packer implements FilenameFilter {
    /**
     * The sub-directory containing JAR files.
     * This directory must exists; it will not be created.
     */
    private static final String JAR_DIRECTORY = Collector.SUB_DIRECTORY;

    /**
     * The sub-directory containing pack files. This directory
     * will be automatically created if it doesn't already exist.
     */
    static final String PACK_DIRECTORY = "bundles";

    /**
     * The Maven target directory. Should contains the {@code "binaries"} sub-directory,
     * which should contains all JAR files collected by {@code geotk-jar-collector} plugin.
     */
    private final File targetDirectory;

    /**
     * The directory of JAR files.
     */
    final File jarDirectory;

    /**
     * The JAR files to read, by input filename.
     */
    private final Map<File,PackInput> inputs = new LinkedHashMap<>();

    /**
     * The JAR and PACK files to create, by output name.
     */
    private final Map<String,PackOutput> outputs = new LinkedHashMap<>();

    /**
     * The version to declare in the manifest file.
     */
    final String version;

    /**
     * Creates a packer.
     *
     * @param targetDirectory The Maven target directory.
     * @param version The version to declare in the manifest file.
     */
    public Packer(final File targetDirectory, final String version) {
        this.version = version;
        this.targetDirectory = targetDirectory;
        this.jarDirectory = new File(targetDirectory, JAR_DIRECTORY);
    }

    /**
     * Adds a pack which will contain every JAR files in the target directory.
     *
     * @param pack The name of the pack file to create.
     * @throws IOException If an error occurred while collecting the target directory content.
     */
    public void addPack(final String pack) throws IOException {
        addPack(null, pack, jarDirectory.list(this));
    }

    /**
     * Adds the given JAR files for the given pack.
     *
     * @param parent The pack from which to inherit the JAR files, or {@code null} if none.
     * @param pack   The name of the pack file to create.
     * @param jars   The list of JAR files in this pack file.
     * @throws IOException If an error occurred while collecting the target directory content.
     */
    public void addPack(final String parent, final String pack, final String[] jars) throws IOException {
        Objects.requireNonNull("pack", pack);
        PackOutput p = null;
        if (parent != null) {
            p = outputs.get(parent);
            if (p == null) {
                throw new IllegalArgumentException("Non-existant pack: " + parent);
            }
        }
        /*
         * If there is wildcard, replace the wildcard by the full name.
         * We allows only one name (the wildcard should be used for the
         * version number only, and we don't allow many versions of the
         * same file).
         */
        for (int i=0; i<jars.length; i++) {
            final String jarFile = jars[i];
            final int w = jarFile.lastIndexOf('*');
            if (w >= 0) {
                final String prefix = jarFile.substring(0, w);
                final String suffix = jarFile.substring(w+1);
                final File directory = new File(targetDirectory, JAR_DIRECTORY);
                final String[] f = directory.list(new FilenameFilter() {
                    @Override public boolean accept(final File directory, final String name) {
                        return name.startsWith(prefix) && name.endsWith(suffix);
                    }
                });
                if (f == null) {
                    throw new FileNotFoundException("Directory not found: " + directory);
                }
                switch (f.length) {
                    case 1:  jars[i] = f[0]; break;
                    case 0:  throw new IllegalArgumentException("No file found for pattern: " + jarFile);
                    default: throw new IllegalArgumentException("Duplicated files: " + f[0] + " and " + f[1]);
                }
            }
        }
        p = new PackOutput(this, p, jars);
        if (outputs.put(pack, p) != null) {
            throw new IllegalArgumentException("Duplicated pack: " + pack);
        }
    }

    /**
     * Creates the JAR files from the packages declared with {@link #addPack}.
     *
     * @throws IOException if an error occurred while reading existing JAR files
     *         or writing to the packed files.
     */
    public void createJars() throws IOException {
        /*
         * Creates the output directory. We do that first in order to avoid the
         * costly opening of all JAR files if we can't create this directory.
         */
        final File outDirectory = new File(targetDirectory, PACK_DIRECTORY);
        if (!outDirectory.isDirectory()) {
            if (!outDirectory.mkdir()) {
                throw new IOException("Can't create the \"" + PACK_DIRECTORY + "\" directory.");
            }
        }
        /*
         * Opens all input JAR files in read-only mode, and create the initially empty output JAR
         * file. We need to open all input files in order to check for duplicate entries before we
         * start the writing process. Files in the META-INF/services directory need to be merged.
         */
        for (final Map.Entry<String,PackOutput> entry : outputs.entrySet()) {
            final String name = entry.getKey();
            final PackOutput pack = entry.getValue();
            pack.createPackInputs(inputs);
            pack.open(new File(outDirectory, name));
        }
        /*
         * Iterates through the individual jars and merge them in single, bigger JAR file.
         * During each iteration we get the array of output streams where a particular file
         * need to be copied - all those "active" output streams will be filled in parallel.
         */
        final byte[] buffer = new byte[64*1024];
        final Map<File,PackInput> activeInputs = new LinkedHashMap<>(inputs.size() * 4/3);
        final PackOutput[] activesForFile      = new PackOutput[outputs.size()];
        final PackOutput[] activesForEntry     = new PackOutput[activesForFile.length];
        final PackOutput[] activesForFollow    = new PackOutput[activesForFile.length];
        for (final Iterator<Map.Entry<File,PackInput>> it = inputs.entrySet().iterator(); it.hasNext();) {
            final Map.Entry<File,PackInput> fileInputPair = it.next();
            final File  inputFile = fileInputPair.getKey();
            final PackInput input = fileInputPair.getValue();
            it.remove(); // Needs to be before next usage of "inputs" below.
            int countForFile = 0;
            for (final PackOutput candidate : outputs.values()) {
                if (candidate.contains(inputFile)) {
                    activesForFile[countForFile++] = candidate;
                    candidate.copyInputs(inputs, activeInputs);
                }
            }
            /*
             * "activesForFile" now contains the list of PackOutput we need to care about
             * for the current PackInput (i.e. a whole input JAR). Copies every entries
             * found in that JAR.
             */
            for (JarEntry entry; (entry = input.nextEntry()) != null;) {
                int countForEntry = 0;
                for (int i=0; i<countForFile; i++) {
                    final PackOutput candidate = activesForFile[i];
                    if (candidate.putNextEntry(entry)) {
                        activesForEntry[countForEntry++] = candidate;
                    }
                }
                copy(input.getInputStream(), activesForEntry, countForEntry, buffer);
                /*
                 * From that points, the entry has been copied to all target JARs. Now looks in
                 * following input JARs to see if there is some META-INF/services files to merge.
                 */
                final String name = entry.getName();
                if (PackOutput.mergeAllowed(name)) {
                    for (final Map.Entry<File,PackInput> continuing : activeInputs.entrySet()) {
                        final InputStream in = continuing.getValue().getInputStream(name);
                        if (in != null) {
                            final File file = continuing.getKey();
                            int countForFollow = 0;
                            for (int i=0; i<countForEntry; i++) {
                                final PackOutput candidate = activesForEntry[i];
                                if (candidate.contains(file)) {
                                    activesForFollow[countForFollow++] = candidate;
                                }
                            }
                            copy(in, activesForFollow, countForFollow, buffer);
                            Arrays.fill(activesForFollow, null);
                        }
                    }
                }
                for (int i=0; i<countForEntry; i++) {
                    activesForEntry[i].closeEntry();
                }
                Arrays.fill(activesForEntry, null);
            }
            Arrays.fill(activesForFile, null);
            activeInputs.clear();
            input.close();
        }
        close();
    }

    /**
     * Copies fully the given input stream to the given destination.
     * The given input stream is closed after the copy.
     *
     * @param  in     The input stream from which to get the the content to copy.
     * @param  out    Where to copy the input stream content.
     * @param  count  Number of valid entries in the {@code out} array.
     * @param  buffer Temporary buffer to reuse at each method call.
     * @throws IOException If an error occurred during the copy.
     */
    private static void copy(final InputStream in, final PackOutput[] out, final int count,
                             final byte[] buffer) throws IOException
    {
        int n;
        while ((n = in.read(buffer)) >= 0) {
            for (int i=0; i<count; i++) {
                out[i].write(buffer, n);
            }
        }
        in.close();
    }

    /**
     * Closes all streams.
     *
     * @throws IOException If an error occurred while closing the stream.
     */
    public void close() throws IOException {
        for (final PackOutput jar : outputs.values()) {
            jar.close();
        }
        for (final PackInput jar : inputs.values()) {
            jar.close();
        }
    }

    /**
     * Launch Pack200 after output JAR files have been created.
     *
     * @throws IOException If an error occurred while creating the PACK200 file.
     */
    public void pack() throws IOException {
        for (final PackOutput jar : outputs.values()) {
            jar.pack();
        }
    }

    /**
     * Filter the JAR files.
     *
     * @param  directory The directory (ignored).
     * @param  name The filename.
     * @return {@code true} if the given filename ends with {@code ".jar"}.
     */
    @Override
    public boolean accept(final File directory, final String name) {
        return name.endsWith(".jar");
    }

    /**
     * Runs from the command line. The only expected argument is the Maven target directory.
     * This target directory should contains the {@code "binaries"} sub-directory, which should
     * contains all JAR files collected by {@code geotk-jar-collector} plugin.
     *
     * @param  args The command-line arguments.
     * @throws IOException if an error occurred while reading existing JAR files
     *         or writing to the packed files.
     */
    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Expected argument: Maven target directory.");
            return;
        }
        final File targetDirectory = new File(args[0]);
        if (!targetDirectory.isDirectory()) {
            System.out.print(targetDirectory);
            System.out.println(" is not a directory.");
            return;
        }
        final Packer packer = new Packer(targetDirectory, PackerSpecificMojo.VERSION);
        packer.addPack("geotk-bundle-pending-" + PackerSpecificMojo.VERSION + ".jar");
        try {
            packer.createJars();
        } finally {
            packer.close();
        }
        packer.pack();
    }
}
