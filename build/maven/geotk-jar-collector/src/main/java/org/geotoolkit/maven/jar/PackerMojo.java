/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


/**
 * Merges the binaries produced by <code>JarCollector</code> and compress them using Pack200.
 * This mojo delegates the work to <code>Packer</code>, which can be invoked from the command
 * line without Maven. Maven invocation syntax is:
 *
 * <blockquote><code>mvn org.geotoolkit.project:geotk-jar-collector:pack --non-recursive</code></blockquote>
 *
 * Do not forget the <code>--non-recursive</code> option, otherwise the Mojo will be executed
 * many time.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 *
 * @goal pack
 * @phase install
 */
public class PackerMojo extends AbstractMojo {
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
         * Gets the parent "target" directory where JARs are copied.
         * It should be the "target" directory of the parent pom.xml.
         */
        MavenProject parent = project;
        while (parent.hasParent()) {
            parent = parent.getParent();
        }
        final String targetDirectory = parent.getBuild().getDirectory();
        /*
         * Now packs the JARs.
         */
        final String[] arguments = new String[] {
            targetDirectory
        };
        try {
            Packer.main(arguments);
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException(e.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Error packing the JAR file.", e);
        }
    }
}
