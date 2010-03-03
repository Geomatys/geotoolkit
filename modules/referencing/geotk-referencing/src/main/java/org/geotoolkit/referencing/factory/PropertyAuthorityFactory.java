/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.io.IOException;
import java.util.Collection;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.ThreadSafe;


/**
 * A CRS Authority Factory that manages object creation using a set of static
 * strings from a property file. This gives some of the benificts of using the
 * {@linkplain org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory EPSG database}
 * in a portable property file.
 *
 * {@section Declaring more than one authority}
 * There is usually only one authority for a given instance of {@code PropertyAuthorityFactory},
 * but more authorities can be given to the constructor if the CRS objects to create should have
 * more than one {@linkplain CoordinateReferenceSystem#getIdentifiers identifier}, each with the
 * same code but different namespace. See {@link WKTParsingAuthorityFactory} for more details.
 *
 * {@section Caching of CRS objects}
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory should
 * be wrapped in {@link CachingAuthorityFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @see org.geotoolkit.referencing.factory.epsg.PropertyEpsgFactory
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.geotoolkit.referencing.factory.wkt} package.
 */
@Deprecated
@ThreadSafe(concurrent = false)
public class PropertyAuthorityFactory extends org.geotoolkit.referencing.factory.wkt.PropertyAuthorityFactory {
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
        super(userHints, definitionFile, authorities);
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
        super(userHints, definitionFiles, authorities);
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
        super(userHints, directoryKey, resourceLoader, filename, authorities);
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
    protected PropertyAuthorityFactory(final Hints userHints, final Citation... authorities) {
        super(userHints, authorities);
    }
}
