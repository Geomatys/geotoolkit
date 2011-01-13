/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.resources;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * Compiles the international resources that are found in the module from which this mojo is invoked.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 *
 * @goal compile
 * @phase generate-resources
 */
public class ResourceCompilerMojo extends AbstractMojo implements FilenameFilter {
    /**
     * The source directories containing the sources to be compiled.
     *
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;

    /**
     * Directory containing the generated class files.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private String outputDirectory;

    /**
     * Executes the mojo.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
    public void execute() throws MojoExecutionException {
        int errors = 0;
        final File target = new File(outputDirectory);
        for (final String sourceDirectory : compileSourceRoots) {
            File directory = new File(sourceDirectory);
            if (directory.getName().equals("java")) {
                final File[] resourcesToProcess = new File(sourceDirectory, "org/geotoolkit/resources").listFiles(this);
                if (resourcesToProcess != null && resourcesToProcess.length != 0) {
                    errors += new Compiler(directory, target, resourcesToProcess).run();
                }
            }
        }
        if (errors != 0) {
            throw new ResourceCompilerException(String.valueOf(errors) + " errors in resources bundles.");
        }
    }

    /**
     * Returns {@code true} if the given file is the source code for a resources bundle.
     * This method returns {@code true} if the given file is a Java source file and if a
     * properties file of the same name exists.
     *
     * @param directory The directory.
     * @param name The file name.
     * @return {@code true} if the given file is a property file.
     */
    @Override
    public final boolean accept(final File directory, String name) {
        if (!name.endsWith(IndexedResourceCompiler.JAVA_EXT)) {
            return false;
        }
        name = name.substring(0, name.length() - IndexedResourceCompiler.JAVA_EXT.length());
        name = name + IndexedResourceCompiler.PROPERTIES_EXT;
        return new File(directory, name).isFile();
    }

    /**
     * A resource compiler that delegates the messages to the Mojo logger.
     */
    private final class Compiler extends IndexedResourceCompiler {
        public Compiler(File sourceDirectory, File buildDirectory, File[] resourcesToProcess) {
            super(sourceDirectory, buildDirectory, resourcesToProcess);
        }

        /**
         * Logs the given message at the {@code INFO} level.
         */
        @Override
        protected void info(final String message) {
            getLog().info(message);
        }

        /**
         * Logs the given message at the {@code WARNING} level.
         */
        @Override
        protected void warning(final String message) {
            getLog().warn(message);
        }
    }
}
