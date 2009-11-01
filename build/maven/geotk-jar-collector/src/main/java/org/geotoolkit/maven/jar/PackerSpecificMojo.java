/*
 *    Geotoolkit - An Open Source Java GIS Tookit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.maven.jar;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


/**
 * Merges the binaries produced by <code>JarCollector</code> and compress them using Pack200.
 * This mojo is very specific to the Geotk project and is not appropriate for any other project.
 * <p>
 * Maven invocation syntax is:
 *
 * <blockquote><code>mvn org.geotoolkit.project:geotk-jar-collector:pack-specific --non-recursive</code></blockquote>
 *
 * Do not forget the <code>--non-recursive</code> option, otherwise the Mojo will be executed
 * many time.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 *
 * @goal pack-specific
 * @phase install
 */
public class PackerSpecificMojo extends AbstractMojo {
    /**
     * The Geotoolkit version.
     */
    private static final String VERSION = "SNAPSHOT";

    /**
     * The JavaDB version.
     */
    private static final String DERBY_VERSION = "10.5.3.0_1";

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
        final File targetDirectory = new File(parent.getBuild().getDirectory());
        /*
         * Now packs the JARs.
         */
        try {
            final String referencing = "geotk-bundle-referencing-" + VERSION + ".jar";
            final String coverage    = "geotk-bundle-coverage-"    + VERSION + ".jar";
            final String storage     = "geotk-bundle-storage-"     + VERSION + ".jar";
            final String all         = "geotk-bundle-"             + VERSION + ".jar";
            final Packer packer = new Packer(targetDirectory, VERSION);
            packer.addPack(null, referencing, new String[] {
                    "vecmath-1.5.2.jar",
                    "jsr-275-1.0-beta-2.jar",
                    "geoapi-pending-2.3-SNAPSHOT.jar",
                    "geotk-epsg-"        + VERSION + ".jar",
                    "geotk-utility-"     + VERSION + ".jar",
                    "geotk-metadata-"    + VERSION + ".jar",
                    "geotk-referencing-" + VERSION + ".jar" // Last in order to pickup its main class.
            });
            packer.addPack(referencing, coverage, new String[] {
                    "geotk-coverage-"   + VERSION + ".jar",
                    "geotk-coverageio-" + VERSION + ".jar"
            });
            packer.addPack(coverage, storage, new String[] {
                    "geotk-metadata-sql-" + VERSION + ".jar",
                    "geotk-epsg-javadb-"  + VERSION + ".jar",
                    "derby-"             + DERBY_VERSION + ".jar",
                    "derbyLocale_cs-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_de_DE-" + DERBY_VERSION + ".jar",
                    "derbyLocale_es-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_fr-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_hu-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_it-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_ja_JP-" + DERBY_VERSION + ".jar",
                    "derbyLocale_ko_KR-" + DERBY_VERSION + ".jar",
                    "derbyLocale_pl-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_pt_BR-" + DERBY_VERSION + ".jar",
                    "derbyLocale_ru-"    + DERBY_VERSION + ".jar",
                    "derbyLocale_zh_CN-" + DERBY_VERSION + ".jar",
                    "derbyLocale_zh_TW-" + DERBY_VERSION + ".jar"
            });
            packer.addPack(storage, all, new String[] {
                    "jlfgr-1.0.jar",
                    "swingx-1.0.jar",
                    "wizard-0.998.1.jar",
                    "swing-worker-1.1.jar",
                    "filters-2.0.235.jar",
                    "geotk-setup-"         + VERSION + ".jar",
                    "geotk-display-"       + VERSION + ".jar",
                    "geotk-widgets-swing-" + VERSION + ".jar",
                    "geotk-wizards-swing-" + VERSION + ".jar"
            });
            try {
                packer.createJars();
            } finally {
                packer.close();
            }
            packer.pack();
        } catch (IllegalArgumentException e) {
            throw new MojoExecutionException(e.toString());
        } catch (IOException e) {
            throw new MojoExecutionException("Error packing the JAR file.", e);
        }
        /*
         * Packs javadoc.
         */
        final PackFiles pack = new PackFiles(new File(targetDirectory, Packer.PACK_DIRECTORY));
        File directory = new File(targetDirectory, "site/apidocs");
        try {
            pack.pack(directory, "geotk-" + VERSION + "-javadoc.zip");
        } catch (IOException e) {
            throw new MojoExecutionException("Error packing the ZIP file for javadoc.", e);
        }
        /*
         * Packs source code.
         */
        directory = parent.getBasedir();
        try {
            pack.pack(directory, "geotk-" + VERSION + "-sources.zip");
        } catch (IOException e) {
            throw new MojoExecutionException("Error packing the ZIP file for source code.", e);
        }
    }
}
