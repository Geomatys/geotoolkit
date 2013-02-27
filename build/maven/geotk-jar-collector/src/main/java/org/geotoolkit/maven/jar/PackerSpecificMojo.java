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
 * @version 3.20
 *
 * @since 3.00
 *
 * @goal pack-specific
 * @phase install
 */
public class PackerSpecificMojo extends AbstractMojo {
    /**
     * The Geotoolkit.org version.
     */
    static final String VERSION = "4.x-SNAPSHOT";

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
            final String metadata    = "geotk-bundle-metadata-"    + VERSION + ".jar";
            final String referencing = "geotk-bundle-referencing-" + VERSION + ".jar";
            final String coverage    = "geotk-bundle-coverage-"    + VERSION + ".jar";
            final String netcdf      = "geotk-bundle-netcdf-"      + VERSION + ".jar";
            final String storage     = "geotk-bundle-storage-"     + VERSION + ".jar";
            final String all         = "geotk-bundle-"             + VERSION + ".jar";
            final Packer packer = new Packer(targetDirectory, VERSION);
            packer.addPack(null, metadata, new String[] {
                    "jsr-275-*.jar",
                    "jcip-annotations-*.jar",
                    "geoapi-pending-*.jar",
                    "geotk-utility-"  + VERSION + ".jar",
                    "geotk-xml-base-" + VERSION + ".jar",
                    "geotk-metadata-" + VERSION + ".jar"
            });
            packer.addPack(metadata, referencing, new String[] {
                    "vecmath-*.jar",
                    "geotk-epsg-"        + VERSION + ".jar",
                    "geotk-referencing-" + VERSION + ".jar" // Last in order to pickup its main class.
            });
            packer.addPack(referencing, coverage, new String[] {
                    "geotk-storage-"     + VERSION + ".jar",
                    "geotk-coverage-"    + VERSION + ".jar",
                    "geotk-coverageio-"  + VERSION + ".jar",
                    "jai_imageio-*.jar"
            });
            packer.addPack(coverage, netcdf, new String[] {
                    "geotk-coverageio-netcdf-" + VERSION + ".jar",
                    "netcdf-*.jar",
                    "udunits-*.jar",
                    "opendap-*.jar",
                    "grib-*.jar",
                    "joda-time-*.jar",
                    "protobuf-java-*.jar",
                    "jdom-*.jar",
                    "servlet-api-*.jar",
                    "commons-codec-*.jar",
                    "commons-httpclient-*.jar",
                    "commons-logging-*.jar",
                    "slf4j-api-*.jar",
                    "slf4j-jdk14-*.jar"
            });
            packer.addPack(netcdf, storage, new String[] {
                    "geotk-metadata-sql-" + VERSION + ".jar",
                    "geotk-coverage-sql-" + VERSION + ".jar",
                    "geotk-epsg-javadb-"  + VERSION + ".jar",
                    "derby-*.jar",
                    "derbyLocale_cs-*.jar",
                    "derbyLocale_de_DE-*.jar",
                    "derbyLocale_es-*.jar",
                    "derbyLocale_fr-*.jar",
                    "derbyLocale_hu-*.jar",
                    "derbyLocale_it-*.jar",
                    "derbyLocale_ja_JP-*.jar",
                    "derbyLocale_ko_KR-*.jar",
                    "derbyLocale_pl-*.jar",
                    "derbyLocale_pt_BR-*.jar",
                    "derbyLocale_ru-*.jar",
                    "derbyLocale_zh_CN-*.jar",
                    "derbyLocale_zh_TW-*.jar",
                    "postgresql-*.jar"
            });
            packer.addPack(storage, all, new String[] {
                    "jlfgr-*.jar",
                    "swingx-*.jar",
                    "wizard-*.jar",
                    "swing-worker-*.jar",
                    "filters-*.jar",
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
