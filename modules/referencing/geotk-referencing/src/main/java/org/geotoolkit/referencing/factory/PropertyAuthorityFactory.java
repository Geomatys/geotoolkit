/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.io.wkt.Symbols;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.util.logging.Logging;


/**
 * A CRS Authority Factory that manages object creation using a set of static
 * strings from a property file. This gives some of the benificts of using the
 * {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory EPSG database}
 * in a portable property file.
 * <p>
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory should
 * be wrapped in {@link CachingAuthorityFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory
 *
 * @since 2.1
 * @module
 */
public class PropertyAuthorityFactory extends WKTParsingAuthorityFactory {
    /*
     * It is technically possible to add or remove elements after they have been
     * loaded by the constructor. However if such modification are made, then we
     * should update {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER} accordingly.
     * It may be an issue since hints are supposed to be immutable after factory
     * construction. For now, this class does not allow addition of elements.
     */

    /**
     * Creates a factory for the specified authorities using the definitions declared in the given
     * property file. There is usually only one authority, but more can be given when the objects
     * to create should have more than one {@linkplain CoordinateReferenceSystem#getIdentifiers
     * identifier}, each with the same code but different namespace. For example a
     * {@linkplain org.geotoolkit.referencing.factory.epsg.EsriExtension factory for CRS defined
     * by ESRI} uses the {@code "ESRI"} namespace, but also the {@code "EPSG"} namespace because
     * those CRS are used as extension of the EPSG database. Consequently the same CRS can be
     * identified as {@code "ESRI:53001"} and {@code "EPSG:53001"}, where {@code "53001"} is a
     * unused code in the official EPSG database.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param definitionFile
     *          URL to the definition file. This is typically a value returned by
     *          {@link Class#getResource(String)}.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     * @throws IOException
     *          If the definitions can't be read.
     *
     * @since 3.00
     */
    public PropertyAuthorityFactory(final Hints userHints, final URL definitionFile,
            final Citation... authorities) throws IOException
    {
        this(userHints, authorities);
        if (definitionFile != null) {
            load(Collections.singleton(definitionFile));
        }
    }

    /**
     * Creates a factory for the specified authorities using the definitions declared in the given
     * property files. There is usually only one file, but more are allowed. If there is more than
     * one file, their content will be merged. If the same key appears in more than one file, the
     * first occurence is used. This is consistent with the usual rule saying that the first item
     * in a class-path has precedence.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param definitionFiles
     *          URL to the definition file(s). This is typically built from the
     *          values returned by {@link ClassLoader#getResources(String)}.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     * @throws IOException
     *          If the definitions can't be read.
     *
     * @since 3.00
     */
    public PropertyAuthorityFactory(final Hints userHints, final Collection<URL> definitionFiles,
            final Citation... authorities) throws IOException
    {
        this(userHints, authorities);
        load(definitionFiles);
    }

    /**
     * Creates a factory using the definitions found in properties files loaded from a directory
     * and resources. The directory and the resources are both optional - one or both of them can
     * be omitted if the corresponding argument ({@code directoryKey} or {@code resourceLoader})
     * is null. If they are non-null, then they are used as below:
     *
     * <ol>
     *   <li><p>The file directory is specified as a hint stored under the {@code directoryKey}.
     *       That key is usually {@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY}, but other keys are
     *       allowed. If a value exists for that key and a file having the given {@code filename}
     *       exists in that directory, then it is loaded.</p></li>
     *
     *   <li><p>The resource directory (which may be a directory in a JAR file) is specified as
     *       a class given by the {@code resourceLoader} argument. The resources are found in
     *       the same way than {@link Class#getResource(String)} - with {@code filename} as the
     *       string argument - except that more than one URLs will be obtained if possible.</p></li>
     * </ol>
     *
     * If definitions are found for the same keys in both cases, then the definitions found in
     * step 1 have precedence over the definitios found in case 2.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param directoryKey
     *          The key under which a directory may be stored in the hints map, or {@code null} if none.
     *          If non-null, this value is typically {@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY}.
     * @param resourceLoader
     *          The class to use for loading resources, or {@code null} if none. If non-null, the
     *          class package determine the directory (potentially in a JAR file) where to look
     *          for the resources, in the way documented at {@link Class#getResource(String)}.
     * @param filename
     *          The name of the file to look in the directory if {@code directoryKey} is non-null,
     *          or the name of the resources to load if {@code resourceLoader} is non-null.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     * @throws IOException
     *          If the definitions can't be read.
     *
     * @since 3.00
     */
    public PropertyAuthorityFactory(final Hints userHints, final Hints.FileKey directoryKey,
            final Class<?> resourceLoader, final String filename, final Citation... authorities)
            throws IOException
    {
        this(userHints, authorities);
        /*
         * Gets the directory, or null if none. If the user requested us to look for the
         * directory, then we must save what we found in the super.hints map even if that
         * directory is null - the fact that the directory was not found is significant.
         */
        File directory = null;
        if (directoryKey != null) {
            if (userHints != null) {
                Object hint = userHints.get(directoryKey);
                if (hint instanceof File) {
                    directory = (File) hint;
                } else if (hint instanceof String) {
                    directory = new File((String) hint);
                }
            }
            hints.put(directoryKey, directory);
        }
        /*
         * Now build the list of URLs to load.
         */
        final List<URL> definitionFiles = new ArrayList<URL>(4);
        if (directory != null) try {
            final File file = new File(directory, filename);
            if (file.isFile()) { // May throw a SecurityException.
                definitionFiles.add(file.toURI().toURL());
            }
        } catch (SecurityException exception) {
            // Considered unexpected because if the user provided excplicitly a
            // directory, we assume that he was expecting us to read its file.
            Logging.unexpectedException(LOGGER, PropertyAuthorityFactory.class, "<init>", exception);
        }
        if (resourceLoader != null) {
            String path = resourceLoader.getName();
            path = path.substring(0, path.lastIndexOf('.') + 1).replace('.', '/');
            List<URL> more = Collections.emptyList();
            try {
                final ClassLoader cl = resourceLoader.getClassLoader();
                if (cl != null) {
                    more = Collections.list(cl.getResources(path + filename));
                    // Note: above list may still empty.
                }
            } catch (SecurityException exception) {
                // Not considered "unexpected" because this error is actually common in server
                // environment. The Class.getResource(...) method is the recommanded method,
                // but has no API returning the enumeration of all occurence of the file.
                Logging.recoverableException(LOGGER, PropertyAuthorityFactory.class, "<init>", exception);
            }
            definitionFiles.addAll(more);
            if (more.isEmpty()) {
                final URL candidate = resourceLoader.getResource(filename);
                if (candidate != null) {
                    definitionFiles.add(candidate);
                }
            }
        }
        load(definitionFiles);
    }

    /**
     * Creates an initially empty factory. This constructors is reserved for subclasses constructors
     * only. Subclasses must invoke {@link #load(Collection)} in their constructor in order to
     * populate the factory before it is used.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} for the default ones.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the database.
     *
     * @since 3.00
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    protected PropertyAuthorityFactory(final Hints userHints, final Citation... authorities) {
        super(userHints, (Map) new Properties(), authorities);
    }

    /**
     * Loads the WKT strings from the given property files. This method should be
     * invoked from constructors only. Changes after construction are not allowed.
     *
     * @param  definitionFiles URL to the definition file(s). This is typically built
     *         from the values returned by {@link ClassLoader#getResources(String)}.
     * @throws IOException If the definition files can't be read.
     *
     * @since 3.00
     */
    protected synchronized void load(final Collection<URL> definitionFiles) throws IOException {
        ensureNonNull("definitionFiles", definitionFiles);
        @SuppressWarnings("unchecked")
        final Properties definitions = (Properties) (Map<?,?>) this.definitions;
        Properties properties = definitions;
        boolean containsAxis = false;
        for (final URL url : definitionFiles) {
            /*
             * If we have read other files before this one, use a temporary object.
             * It allows us to remove duplicated keys before that are merged.
             */
            if (!properties.isEmpty()) {
                if (properties == definitions) {
                    properties = new Properties();
                } else {
                    properties.clear();
                }
            }
            final InputStream in = url.openStream();
            properties.load(in);
            in.close();
            if (!properties.isEmpty()) {
                final String authority = String.valueOf(Citations.getIdentifier(authority()));
                log(Loggings.Keys.USING_FILE_AS_FACTORY_$2, url, authority);
            }
            if (properties != definitions) {
                if (properties.keySet().removeAll(definitions.keySet())) {
                    log(Loggings.Keys.DUPLICATED_CONTENT_IN_FILE_$1, url, null);
                }
                definitions.putAll(properties);
            }
            /*
             * Checks if the map we just loaded contains axis. We don't do that in the constructor
             * expecting a Map argument because we don't know if iteration over that map is costly,
             * neither if the user intend to modify it after construction.
             */
            if (!containsAxis) {
                final Symbols s = Symbols.DEFAULT;
                for (final Object wkt : definitions.values()) {
                    if (s.containsAxis((String) wkt)) {
                        containsAxis = true;
                        break;
                    }
                }
            }
        }
        /*
         * If the WKT definitions do not contain any AXIS[...] element, then every CRS will be
         * created with the default (longitude,latitude) axis order. In such case this factory
         * is insensitive to the FORCE_LONGITUDE_FIRST_AXIS_ORDER hint  (in other words, every
         * CRS created by this instance are invariant under the above-cited hint value) and we
         * can ommit them from the hint map. This omission allows the CRS.decode(..., true)
         * convenience method to accept this factory (GEOT-1175).
         */
        if (!containsAxis) {
            hints.remove(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER);
            hints.remove(Hints.FORCE_STANDARD_AXIS_DIRECTIONS);
            // Do not remove FORCE_STANDARD_AXIS_UNITS. It still taken in account
            // because units are defined outside AXIS[...] elements in WKT format.
        }
    }

    /**
     * Logs a message using the given resource key and using the given URL as a value.
     */
    private static void log(final int key, final URL url, final Object ext) {
        String path = url.getPath();
        path = path.substring(path.lastIndexOf('/') + 1);
        final LogRecord record;
        if (ext != null) {
            record = Loggings.format(Level.CONFIG, key, path, ext);
        } else {
            record = Loggings.format(Level.WARNING, key, path);
        }
        record.setSourceClassName(PropertyAuthorityFactory.class.getName());
        record.setSourceMethodName("load");
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Disposes the resources used by this factory.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if
     *        this method is invoked during the process of a JVM shutdown.
     */
    @Override
    protected synchronized void dispose(final boolean shutdown) {
        // We don't invoke Map.clear() in the super-class because we don't want
        // to touch user-supplied collection. It could be backed by a database.
        definitions.clear();
        super.dispose(shutdown);
    }
}
