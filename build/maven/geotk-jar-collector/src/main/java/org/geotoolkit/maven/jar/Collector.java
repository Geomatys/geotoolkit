/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;


/**
 * Copies <code>.jar</code> files in a single directory. Dependencies are copied as well,
 * except if already presents.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 *
 * @goal collect
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class Collector extends AbstractMojo implements FileFilter {
    /**
     * The sub directory to create inside the "target" directory.
     */
    static final String SUB_DIRECTORY = "binaries";

    /**
     * The directory where JARs are to be copied. It should
     * be the "target" directory of the parent {@code pom.xml}.
     */
    private String collectDirectory;

    /**
     * Directory containing the generated JAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * Name of the generated JAR.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String jarName;

    /**
     * The Maven project running this plugin.
     *
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Copies the {@code .jar} files to the collect directory.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    public void execute() throws MojoExecutionException {
        /*
         * Gets the parent "target" directory.
         */
        MavenProject parent = project;
        while (parent.hasParent()) {
            parent = parent.getParent();
        }
        collectDirectory = parent.getBuild().getDirectory();
        if (collectDirectory.startsWith("${")) {
            getLog().warn("Unresolved directory: " + collectDirectory);
            return;
        }
        /*
         * Now collects the JARs.
         */
        try {
            collect();
        } catch (IOException e) {
            throw new MojoExecutionException("Error collecting the JAR file.", e);
        }
    }

    /**
     * Implementation of the {@link #execute()} method.
     */
    private void collect() throws MojoExecutionException, IOException {
        /*
         * Make sure that we are collecting the JAR file from a module which produced
         * such file. Some modules use pom packaging, which do not produce any JAR file.
         */
        final File jarFile = getProjectFile();
        if (jarFile == null) {
            return;
        }
        /*
         * Get the "target" directory of the parent pom.xml and make sure it exists.
         */
        File collect = new File(collectDirectory);
        if (!collect.exists()) {
            if (!collect.mkdir()) {
                throw new MojoExecutionException("Failed to create target directory.");
            }
        }
        if (collect.getCanonicalFile().equals(jarFile.getParentFile().getCanonicalFile())) {
            /*
             * The parent's directory is the same one than this module's directory.
             * In other words, this plugin is not executed from the parent POM. Do
             * not copy anything, since this is not the place where we want to
             * collect the JAR files.
             */
            return;
        }
        /*
         * Creates a "binaries" subdirectory inside the "target" directory, then copy the
         * JAR file compiled by Maven. If an JAR file already existed, it will be deleted.
         */
        collect = new File(collect, SUB_DIRECTORY);
        if (!collect.exists()) {
            if (!collect.mkdir()) {
                throw new MojoExecutionException("Failed to create binaries directory.");
            }
        }
        File copy = new File(collect, jarFile.getName());
        copy.delete();
        copyFileToDirectory(jarFile, copy);
        /*
         * Copies the dependencies.
         */
        final Set<Artifact> dependencies = project.getArtifacts();
        if (dependencies != null) {
            for (final Artifact artifact : dependencies) {
                final String scope = artifact.getScope();
                if (scope != null &&  // Maven 2.0.6 bug?
                   (scope.equalsIgnoreCase(Artifact.SCOPE_COMPILE) ||
                    scope.equalsIgnoreCase(Artifact.SCOPE_RUNTIME)))
                {
                    final File file = artifact.getFile();
                    if (file != null) { // I'm not sure why the file is sometime null...
                        copy = new File(collect, getFinalName(file, artifact));
                        if (!copy.exists()) {
                            /*
                             * Copies the dependency only if it was not already copied. Note that
                             * the module's JAR was copied unconditionally above (because it may
                             * be the result of a new compilation).
                             */
                            copyFileToDirectory(file, copy);
                        }
                    }
                }
            }
        }
    }

    /**
     * Filters the content of the "target" directory in order to keep only the project
     * build result. We scan the directory because the final name may be different than
     * the {@link #jarName} declaration, because a classifier may have been added to the
     * name.
     * <p>
     * <b>Complain:</b> Why the hell peoples use the {@code ".jar"} file extension for what
     * should be plain source and javadoc {@code ".zip"} files?
     */
    @Override
    public boolean accept(final File pathname) {
        final String name = pathname.getName();
        return name.startsWith(jarName) && name.endsWith(".jar") &&
                !name.endsWith("-sources.jar") && !name.endsWith("-javadoc.jar") && pathname.isFile();
    }

    /**
     * Returns the JAR file, or {@code null} if none.
     * In case of doubt, conservatively returns {@code null}.
     */
    private File getProjectFile() {
        final File[] files = new File(outputDirectory).listFiles(this);
        if (files != null && files.length == 1) {
            return files[0];
        }
        return null;
    }

    /**
     * Returns the name of the given file. If the given file is a snapshot, then the
     * {@code "SNAPSHOT"} will be replaced by the timestamp if possible.
     *
     * @param  file     The file from which to get the filename.
     * @param  artifact The artifact that produced the given file.
     * @return The filename to use.
     *
     * @since 3.18
     */
    private static String getFinalName(final File file, final Artifact artifact) {
        String filename = file.getName();
        final String baseVersion = artifact.getBaseVersion();
        if (baseVersion != null) {
            final int pos = filename.lastIndexOf(baseVersion);
            if (pos >= 0) {
                final String version = artifact.getVersion();
                if (version != null && !baseVersion.equals(version)) {
                    filename = filename.substring(0, pos) + version + filename.substring(pos + baseVersion.length());
                }
            }
        }
        return filename;
    }

    /**
     * Copies the given file to the given target file. Since JDK 7, this method actually
     * creates a hard link.
     *
     * @param file The source file to read.
     * @param copy The destination file to create.
     */
    private static void copyFileToDirectory(final File file, final File copy) throws IOException {
        final Path source = file.toPath();
        final Path target = copy.toPath();
        try {
            Files.createLink(target, source);
        } catch (UnsupportedOperationException e) {
            // If hard links are not supported, do a plain copy.
            Files.copy(source, target);
        }
    }
}
