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
package org.geotoolkit.maven.unopkg;

import java.io.*;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;

import static java.util.jar.Pack200.Packer;


// Note: javadoc in class and fields descriptions must be XHTML.
/**
 * Creates a <code>.oxt</code> package for <a href="http://www.openoffice.org">OpenOffice</a>
 * addins.
 *
 * @goal unopkg
 * @phase package
 * @description Creates a .oxt package for OpenOffice addins
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.09
 *
 * @since 3.09 (derived from 2.2)
 */
public class UnoPkg extends AbstractMojo implements FilenameFilter {
    /**
     * The encoding for text files to read and write.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * The string to replace by the final name.
     */
    private static final String SUBSTITUTE = "${project.build.finalName}";

    /**
     * Directory where the UNO files are located. The plugin will looks for the
     * <code>META-INF/manifest.xml</code> and <code>*.rdb</code> files in this directory.
     *
     * @parameter expression="${basedir}/src/main/unopkg"
     * @required
     */
    private String sourceDirectory;

    /**
     * Directory where the output <code>oxt</code> file will be located.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * In <code>META-INF/manifest.xml</code>, replaces all occurrences of
     * <code>${project.build.finalName}</code> by this value.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The name for the <code>oxt</code> file to create.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String oxtName;

    /**
     * The name of the Pack200 file, or <code>none</code> for storing plain JAR files.
     * If a Pack200 file is generated, then the add-in registration class needs to unpack
     * the file itself before use.
     *
     * @parameter expression="none"
     */
    private String pack200;

    /**
     * The Maven project running this plugin.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * The prefix to be added before JAR file names.
     * To be determined by heuristic rule.
     */
    private transient String prefix;

    /**
     * Apply prefix only for dependencies of this group.
     */
    private transient String prefixGroup;

    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param   directory the directory in which the file was found.
     * @param   name      the name of the file.
     */
    @Override
    public boolean accept(final File directory, final String name) {
        if (name.endsWith("-sources.jar") || name.endsWith("-javadoc.jar")) {
            return false;
        }
        return name.endsWith(".jar") || name.endsWith(".JAR") ||
               name.endsWith(".rdb") || name.endsWith(".RDB") ||
               name.endsWith(".xml") || name.endsWith(".XML") |
               name.endsWith(".png") || name.endsWith(".PNG");
    }

    /**
     * Generates the {@code .oxt} file from all {@code .jar} files found in the target directory.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    public void execute() throws MojoExecutionException {
        final int i = finalName.indexOf(project.getArtifactId());
        prefix = (i >= 0) ? finalName.substring(0, i) : "";
        prefixGroup = project.getGroupId();
        try {
            createPackage();
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating the oxt file.", e);
        }
    }

    /**
     * Creates the {@code .oxt} file.
     */
    private void createPackage() throws IOException {
        final String  manifestName = "META-INF/manifest.xml";
        final File outputDirectory = new File(this.outputDirectory);
        final File         zipFile = new File(outputDirectory, oxtName + ".oxt");
        final File    manifestFile = new File(sourceDirectory, manifestName);
        final File[]          jars = outputDirectory.listFiles(this);
        final File[]          rdbs = new File(sourceDirectory).listFiles(this);
        final ZipOutputStream  out = new ZipOutputStream(new FileOutputStream(zipFile));
        out.setLevel(9);
        if (manifestFile.isFile()) {
            copyFiltered(manifestFile, out, manifestName);
        }
        /*
         * Copies the RDB files.
         */
        for (int i=0; i<rdbs.length; i++) {
            copy(rdbs[i], out, null);
        }
        /*
         * Copies the JAR (and any additional JARs provided in the output directory).
         * Do not pack this JAR, because it contains the code needed to inflate the
         * PACK200 file.
         */
        for (int i=0; i<jars.length; i++) {
            copy(jars[i], out, null);
        }
        /*
         * Copies the dependencies, optionally in a single PACK200 entry.
         */
        Pack200.Packer packer = null;
        if (!pack200.trim().equalsIgnoreCase("none")) {
            out.putNextEntry(new ZipEntry(pack200));
            packer = Pack200.newPacker();
            final Map<String,String> p = packer.properties();
            p.put(Packer.EFFORT,            "9");           // take more time choosing codings for better compression.
            p.put(Packer.SEGMENT_LIMIT,     "-1");          // use largest-possible archive segments (>10% better compression).
            p.put(Packer.KEEP_FILE_ORDER,   Packer.FALSE);  // reorder files for better compression.
            p.put(Packer.MODIFICATION_TIME, Packer.LATEST); // smear modification times to a single value.
            p.put(Packer.CODE_ATTRIBUTE_PFX+"LineNumberTable",    Packer.STRIP); // discard debug attributes.
            p.put(Packer.CODE_ATTRIBUTE_PFX+"LocalVariableTable", Packer.STRIP); // discard debug attributes.
            p.put(Packer.CLASS_ATTRIBUTE_PFX+"SourceFile",        Packer.STRIP); // discard debug attributes.
            p.put(Packer.DEFLATE_HINT,      Packer.TRUE);   // transmitting a single request to use "compress" mode.
            p.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);  // throw an error if an attribute is unrecognized.
        }
        for (final Artifact artifact : project.getDependencyArtifacts()) {
            final String scope = artifact.getScope();
            if (scope != null &&  // Maven 2.0.6 bug?
               (scope.equalsIgnoreCase(Artifact.SCOPE_COMPILE) ||
                scope.equalsIgnoreCase(Artifact.SCOPE_RUNTIME)))
            {
                final File file = artifact.getFile();
                if (packer != null) {
                    final JarFile jar = new JarFile(file);
                    packer.pack(jar, out);
                    jar.close();
                } else {
                    String name = file.getName();
                    if (artifact.getGroupId().startsWith(prefixGroup) && !name.startsWith(prefix)) {
                        name = prefix + name;
                    }
                    copy(file, out, name);
                }
            }
        }
        if (packer != null) {
            out.closeEntry();
        }
        out.close();
    }

    /**
     * Copies the content of the specified binary file to the specified output stream.
     *
     * @param name The ZIP entry name, or <code>null</code> for using the name of the given file.
     */
    private static void copy(final File file, final ZipOutputStream out, String name)
            throws IOException
    {
        if (name == null) {
            name = file.getName();
        }
        final ZipEntry entry = new ZipEntry(name);
        if (name.endsWith(".png")) {
            final long size = file.length();
            entry.setMethod(ZipOutputStream.STORED);
            entry.setSize(size);
            entry.setCompressedSize(size);
            entry.setCrc(getCRC32(file));
        }
        out.putNextEntry(entry);
        final InputStream in = new FileInputStream(file);
        final byte[] buffer = new byte[4*1024];
        int length;
        while ((length = in.read(buffer)) >= 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.closeEntry();
    }

    /**
     * Copies the content of the specified ASCII file to the specified output stream.
     */
    private void copyFiltered(final File file, final ZipOutputStream out, String name)
            throws IOException
    {
        if (name == null) {
            name = file.getName();
        }
        final ZipEntry entry = new ZipEntry(name);
        out.putNextEntry(entry);
        final Writer writer = new OutputStreamWriter(out, ENCODING);
        final BufferedReader in = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), ENCODING));
        String line; while ((line=in.readLine()) != null) {
            int r=-1; while ((r=line.indexOf(SUBSTITUTE, r+1)) >= 0) {
                line = line.substring(0, r) + finalName + line.substring(r + SUBSTITUTE.length());
            }
            writer.write(line);
            writer.write('\n');
        }
        in.close();
        writer.flush();
        out.closeEntry();
    }

    /**
     * Computes CRC32 for the given file.
     */
    private static long getCRC32(final File file) throws IOException {
        final CRC32 crc = new CRC32();
        final InputStream in = new FileInputStream(file);
        final byte[] buffer = new byte[4*1024];
        int length;
        while ((length = in.read(buffer)) >= 0) {
            crc.update(buffer, 0, length);
        }
        in.close();
        return crc.getValue();
    }
}
