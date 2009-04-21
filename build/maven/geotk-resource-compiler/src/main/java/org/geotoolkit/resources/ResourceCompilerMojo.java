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
package org.geotoolkit.resources;

import java.io.File;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;


/**
 * Compiles the international resources that are found in the module from which this mojo is invoked.
 * This mojo is invoked with <code>mvn org.geotoolkit.project:geotk-resource-compiler:compile</code>.
 * It wraps <code>IndexedResourceCompiler</code> in a Maven mojo for convenience, but the later
 * can also be invoked directly from the command line (without Maven).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 *
 * @goal compile
 * @phase process-sources
 */
public class ResourceCompilerMojo extends AbstractMojo {
    /**
     * The source directories containing the sources to be compiled.
     *
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;

    /**
     * If <code>true</code>, the number assigned to resources will be renumeroted.
     * If <code>false</code> (the default), the existing numbering is preserved.
     *
     * @parameter
     */
    private boolean renumber;

    /**
     * Executes the mojo.
     *
     * @throws MojoExecutionException if the plugin execution failed.
     */
    @Override
    @SuppressWarnings("unchecked") // Generic array creation.
    public void execute() throws MojoExecutionException {
        final String[] arguments;
        if (renumber) {
            arguments = new String[] {
                "--renumber"
            };
        } else {
            arguments = new String[] {
            };
        }
        for (final String sourceDirectory : compileSourceRoots) {
            File directory = new File(sourceDirectory);
            if (!directory.getName().equals("java")) {
                continue;
            }
            directory = directory.getParentFile();
            final String module;
            try {
                module = directory.getParentFile().getParentFile().getName();
            } catch (NullPointerException e) {
                continue;
            }
            /*
             * Selects the set of resources according the module to be processed.
             */
            final Class<? extends IndexedResourceBundle>[] resourcesToProcess;
            if (module.equals("geotk-utility")) {
                resourcesToProcess = new Class[] {
                    org.geotoolkit.resources.Descriptions.class,
                    org.geotoolkit.resources.Vocabulary  .class,
                    org.geotoolkit.resources.Loggings    .class,
                    org.geotoolkit.resources.Errors      .class
                };
            } else {
                continue;
            }
            final IndexedResourceCompiler compiler = new Compiler(arguments, directory, resourcesToProcess);
            try {
                compiler.run();
            } catch (ResourceCompilerException e) {
                throw new MojoExecutionException("Failed to compile internationalized resources.");
            }
        }
    }

    /**
     * A resource compiler that delegates the messages to the Mojo logger.
     */
    private final class Compiler extends IndexedResourceCompiler {
        public Compiler(final String[] arguments, final File mainDirectory,
                final Class<? extends IndexedResourceBundle>[] resourcesToProcess)
        {
            super(arguments, mainDirectory, resourcesToProcess);
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

        @Override
        protected void exit(final int code) {
            throw new ResourceCompilerException();
        }
    }
}
