/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.wkt;

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
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.DatumAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.io.wkt.Symbols;
import org.apache.sis.internal.util.Citations;
import org.geotoolkit.resources.Loggings;
import org.apache.sis.util.logging.Logging;

import static org.geotoolkit.util.collection.XCollections.addIfNonNull;


/**
 * A CRS Authority Factory that manages object creation using a set of static strings from a
 * {@linkplain java.util.Properties property file}. This gives some of the benefits of using
 * the {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory EPSG database}
 * in a portable property file (which must be provided by the users), or add new authorities.
 * See {@link org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory} for a subclass
 * specialized for the EPSG authority.
 * <p>
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory needs to
 * be wrapped in {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory}.
 * The {@link AuthorityFactoryProvider#createFromProperties AuthorityFactoryProvider}
 * convenience class can be used for that purpose.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.1)
 * @module
 */
@ThreadSafe
public class PropertyAuthorityFactory extends WKTParsingAuthorityFactory
        implements CRSAuthorityFactory, CSAuthorityFactory, DatumAuthorityFactory
{
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
     * identifier}, each with the same code but different namespace.
     * See {@link WKTParsingAuthorityFactory} for more details.
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
     * first occurrence is used. This is consistent with the usual rule saying that the first item
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
     * step 1 have precedence over the definitions found in case 2.
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
         * Now build the list of URLs to load. First we look for a real file (not
         * a URL to a resource) in the directory supplied by the user, if any.
         */
        String path = filename; // Will be used for formatting a "File not found" message if needed.
        final List<URL> definitionFiles = new ArrayList<>(4);
        if (directory != null) try {
            final File file = new File(directory, filename);
            path = file.getPath();
            if (file.isFile()) { // May throw a SecurityException.
                definitionFiles.add(file.toURI().toURL());
            }
        } catch (SecurityException exception) {
            // Considered unexpected because if the user provided excplicitly a
            // directory, we assume that he was expecting us to read its file.
            Logging.unexpectedException(LOGGER, PropertyAuthorityFactory.class, "<init>", exception);
        }
        /*
         * Now search for URL to resources, which may be entries in a JAR file.
         * The same resources may be present in more than one JAR file.
         */
        if (resourceLoader != null) {
            List<URL> more = Collections.emptyList();
            path = resourceLoader.getName();
            path = path.substring(0, path.lastIndexOf('.') + 1).replace('.', '/') + filename;
            try {
                final ClassLoader cl = resourceLoader.getClassLoader();
                if (cl != null) {
                    more = Collections.list(cl.getResources(path));
                    // Note: above list may still empty.
                }
            } catch (SecurityException exception) {
                // Not considered "unexpected" because this error is actually common in server
                // environment. The Class.getResource(...) method is the recommended method,
                // but has no API returning the enumeration of all occurrence of the file.
                Logging.recoverableException(LOGGER, PropertyAuthorityFactory.class, "<init>", exception);
            }
            definitionFiles.addAll(more);
            /*
             * If we have not been able to get the resources from the root (typically because
             * of security constraints), try to get the resource relative to the class.  This
             * approach usually don't have security constraint.
             */
            if (more.isEmpty()) {
                addIfNonNull(definitionFiles, resourceLoader.getResource(filename));
            }
        }
        /*
         * Now the list of URLs to load is complete. If this list is empty,
         * logs a message for debugging purpose.
         */
        if (definitionFiles.isEmpty()) {
            log(false, Loggings.Keys.CANT_READ_FILE_1, null, path);
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
        Properties currentFile = definitions;
        boolean containsAxis = false;
        for (final URL url : definitionFiles) {
            /*
             * If we have read other files before this one, use a temporary object.
             * It allows us to remove duplicated keys before they are merged.
             */
            if (!currentFile.isEmpty()) {
                if (currentFile == definitions) {
                    currentFile = new Properties();
                } else {
                    currentFile.clear();
                }
            }
            try (InputStream in = url.openStream()) {
                currentFile.load(in);
            }
            if (!currentFile.isEmpty()) {
                // Note: the 'authorities' array length is never 0 (checked by the constructor).
                final String authority = String.valueOf(Citations.getIdentifier(authorities[0]));
                log(false, Loggings.Keys.USING_FILE_AS_FACTORY_2, url, authority);
            }
            if (currentFile != definitions) {
                if (currentFile.keySet().removeAll(definitions.keySet())) {
                    log(true, Loggings.Keys.DUPLICATED_CONTENT_IN_FILE_1, url, null);
                }
                definitions.putAll(currentFile);
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
         * can omit them from the hint map. This omission allows the CRS.decode(..., true)
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
     *
     * @param warning {@code true} if the message should be logged as a warning.
     * @param key The key of the internationalized string to fetch.
     * @param url The URL to put in the message, or {@code null} if none.
     * @param ext An additional parameter to put in the message, or {@code null} if none.
     */
    private static void log(final boolean warning, final int key, final URL url, final Object ext) {
        final Level level = warning ? Level.WARNING : Level.CONFIG;
        final LogRecord record;
        if (url != null) {
            String path = url.getPath();
            path = path.substring(path.lastIndexOf('/') + 1);
            if (ext != null) {
                record = Loggings.format(level, key, path, ext);
            } else {
                record = Loggings.format(level, key, path);
            }
        } else {
            record = Loggings.format(level, key, ext);
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
        // The call to definitions.clear() is not performed in the super-class because we
        // don't want to touch user-supplied collection. It could be backed by a database.
        definitions.clear();
        super.dispose(shutdown);
    }
}
