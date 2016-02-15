/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.openoffice;

import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.Pack200;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.registry.XRegistryKey;


/**
 * The registration of all formulas provided in this package.
 *
 * {@section Implementation note}
 * No GeoAPI or Geotk classes should appear in method signature. For example no method should
 * contain {@link org.opengis.util.FactoryException} in their {@code throws} declaration. This
 * is because those classes can be loaded only after the Pack200 files have been unpacked, which
 * is the work of this {@code Registration} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @module
 */
public final class Registration implements FilenameFilter {
    /**
     * Do not allow instantiation of this class, except for internal use.
     */
    private Registration() {
    }

    /**
     * Returns the directory where the add-in is installed.
     *
     * @return The installation directory.
     * @throws URISyntaxException If the path to the add-in JAR file does not have the expected syntax.
     */
    private static File getInstallDirectory() throws URISyntaxException {
        String path = Registration.class.getResource("Registration.class").toString();
        int numParents = 4; // Number of calls to File.getParentFile() needed for reaching the root.
        if (path.startsWith("jar:")) {
            path = path.substring(4, path.indexOf('!'));
            numParents = 1; // The file should be the geotk-openoffice.jar file in the root.
        }
        File file = new File(new URI(path));
        while (--numParents >= 0) {
            file = file.getParentFile();
        }
        return file;
    }

    /**
     * Ensures that the {@code geotk.pack} files have been uncompressed.
     *
     * @throws URISyntaxException If the path to the add-in JAR file does not have the expected syntax.
     * @throws IOException        If an error occurred while uncompressing the PACK200 file.
     */
    private static void ensureInstalled() throws URISyntaxException, IOException {
        final File directory = getInstallDirectory();
        final String[] content = directory.list(new Registration());
        if (content != null && content.length != 0) {
            final Pack200.Unpacker unpacker = Pack200.newUnpacker();
            for (final String filename : content) {
                final File packFile = new File(directory, filename);
                final File jarFile  = new File(directory, filename.substring(0, filename.length()-4) + "jar");
                try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile))) {
                    unpacker.unpack(packFile, out);
                }
                packFile.delete();
            }
            /*
             * Ensures that the EPSG database is installed. We force the EPSG installation at add-in
             * installation time rather than the first time a user ask for a referencing operations,
             * because users may be less surprised by a delay at installation time than at use time.
             * However if the EPSG database is deleted after the installation, it will be recreated
             * when first needed.
             *
             * Note: do not reach this code before all Pack200 files have been unpacked.
             * Remainder: no GeoAPI or Geotk classes in any method signature of this class!
             */
            // TODO: to be re-enabled after the migration to SIS.
//            try {
//                final EpsgInstaller installer = new EpsgInstaller();
//                if (!installer.exists()) {
//                    installer.call();
//                }
//            } catch (Throwable e) {
//                unexpectedException(EpsgInstaller.class, "call", e);
//                // Ignore. A new attempt to create the EPSG database will be performed
//                // when first needed, and the user may see the error at that time.
//            }
        }
    }

    /**
     * Logs the given exception.
     */
    private static void unexpectedException(final Class<?> classe, final String method, final Throwable exception) {
        final Logger logger = Logger.getLogger("org.geotoolkit.openoffice");
        final LogRecord record = new LogRecord(Level.WARNING, exception.getLocalizedMessage());
        record.setLoggerName(logger.getName());
        record.setSourceClassName(classe.getName());
        record.setSourceMethodName(method);
        record.setThrown(exception);
        logger.log(record);
    }

    /**
     * Filters a directory content in order to retain only the {@code "*.pack"} files.
     *
     * @param directory The add-in installation directory.
     * @param name The name of a file in the given directory.
     */
    @Override
    public boolean accept(final File directory, final String name) {
        return name.endsWith(".pack");
    }

    /**
     * Returns a factory for creating the service.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param   implementation The name of the implementation for which a service is desired.
     * @param   factories      The service manager to be used if needed.
     * @param   registry       The registry key
     * @return  A factory for creating the component.
     * @throws  URISyntaxException If the path to the add-in JAR file does not have the expected syntax.
     * @throws  IOException        If an error occurred while uncompressing the PACK200 file.
     */
    public static XSingleServiceFactory __getServiceFactory(
            final String               implementation,
            final XMultiServiceFactory factories,
            final XRegistryKey         registry) throws URISyntaxException, IOException
    {
        ensureInstalled();
        if (implementation.equals(org.geotoolkit.openoffice.geoapi.Referencing.class.getName())) {
            return FactoryHelper.getServiceFactory(org.geotoolkit.openoffice.geoapi.Referencing.class,
                    org.geotoolkit.openoffice.geoapi.Referencing.__serviceName, factories, registry);
        }
        if (implementation.equals(Referencing.class.getName())) {
            return FactoryHelper.getServiceFactory(Referencing.class, Referencing.__serviceName, factories, registry);
        }
        if (implementation.equals(Nature.class.getName())) {
            return FactoryHelper.getServiceFactory(Nature.class, Nature.__serviceName, factories, registry);
        }
        return null;
    }

    /**
     * Writes the service information into the given registry key.
     * This method is called by the {@code com.sun.star.comp.loader.JavaLoader}; do not rename!
     *
     * @param  registry     The registry key.
     * @return {@code true} if the operation succeeded.
     * @throws URISyntaxException If the path to the add-in JAR file does not have the expected syntax.
     * @throws IOException        If an error occurred while uncompressing the PACK200 file.
     */
    public static boolean __writeRegistryServiceInfo(final XRegistryKey registry)
            throws URISyntaxException, IOException
    {
        ensureInstalled();
        return register(org.geotoolkit.openoffice.geoapi.Referencing.class,
                        org.geotoolkit.openoffice.geoapi.Referencing.__serviceName, registry)
            && register(Referencing.class, Referencing.__serviceName, registry)
            && register(Nature     .class, Nature.     __serviceName, registry);
    }

    /**
     * Helper method for the above {@link #__writeRegistryServiceInfo} method.
     */
    private static boolean register(final Class<? extends Formulas> classe,
            final String serviceName, final XRegistryKey registry)
    {
        final String cn = classe.getName();
        return FactoryHelper.writeRegistryServiceInfo(cn, serviceName, registry)
            && FactoryHelper.writeRegistryServiceInfo(cn, Formulas.ADDIN_SERVICE, registry);
    }
}
