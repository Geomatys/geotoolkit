/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.PrintWriter;
import java.io.IOException;

import java.util.Set;
import java.util.Map;
import java.util.TreeSet;
import java.util.TreeMap;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.PropertyAuthorityFactory;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.IndentedLineWriter;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.lang.ThreadSafe;


/**
 * Authority factory for {@linkplain CoordinateReferenceSystem Coordinate Reference Systems}
 * beyong the one defined in the EPSG database. This factory is used as a fallback when a
 * requested code is not found in the EPSG database, or when there is no connection at all
 * to the EPSG database. The CRS are defined as <cite>Well Known Text</cite> in a property
 * file named {@value #FILENAME}. The search path is as below, in that order:
 *
 * <ol>
 *   <li><p>If a value for {@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY} exists in the hints map
 *       given at construction time, then that value will be used as the directory where to
 *       search for the {@value #FILENAME} file.</p></li>
 *   <li><p>The {@value #FILENAME} files found in all {@code org/geotoolkit/referencing/factory/epsg}
 *       directories on the classpath are merged with the values found in previous step, if any.
 *       If the same value is defined twice, the value of previous step have precedence.</p></li>
 * </ol>
 *
 * This factory can also be used to provide custom extensions or overrides to a main EPSG
 * factory. In order to provide a custom extension, you can create a subclass that invoke the
 * {@link #PropertyEpsgFactory(Hints, String, Citation[])} constructor with different constants.
 * You can also subclass {@link PropertyAuthorityFactory} directly for getting more control. In
 * order to make the factory be an override, override the {@link #setOrdering setOrdering(...)}
 * as below:
 *
 * {@preformat java
 *     protected void setOrdering(Organizer organizer) {
 *         organizer.before(ThreadedEpsgFactory.class);
 *     }
 * }
 *
 * This factory doesn't cache any result. Any call to a {@code createFoo} method
 * will trig a new WKT parsing. For adding caching service, this factory should
 * be wrapped in {@link org.geotoolkit.referencing.factory.CachingAuthorityFactory}.
 * Note that this is done automatically when this factory is used through the
 * {@link org.geotoolkit.referencing.CRS} static methods.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @author Rueben Schulz (UBC)
 * @author Andrea Aime (TOPP)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@ThreadSafe(concurrent = false)
public class PropertyEpsgFactory extends PropertyAuthorityFactory implements CRSAuthorityFactory {
    /**
     * The default filename to read. The default {@code PropertyEpsgFactory}
     * implementation will search for all occurences of this file in every
     * {@code org/geotoolkit/referencing/factory/espg} directories found on the
     * classpath.
     */
    public static final String FILENAME = "epsg.properties";

    /**
     * Constructs a default authority factory.
     *
     * @throws IOException If an error occured while reading the definition files.
     *         Note that do exception is thrown if there is no file - in this case
     *         the factory is only considered not {@linkplain #availability available}.
     */
    public PropertyEpsgFactory() throws IOException {
        this(EMPTY_HINTS);
    }

    /**
     * Constructs an authority factory from the given hints.
     * Hints of special interest are:
     * <p>
     * <ul>
     *   <li>{@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY}</li>
     *   <li>{@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER}</li>
     * </ul>
     * <p>
     * This constructor recognizes also {@link Hints#CRS_FACTORY CRS}, {@link Hints#CS_FACTORY CS},
     * {@link Hints#DATUM_FACTORY DATUM} and {@link Hints#MATH_TRANSFORM_FACTORY MATH_TRANSFORM}
     * {@code FACTORY} hints.
     *
     * @param userHints An optional set of hints, or {@code null} if none.
     * @throws IOException If an error occured while reading the definition files.
     *         Note that do exception is thrown if there is no file - in this case
     *         the factory is only considered not {@linkplain #availability available}.
     */
    public PropertyEpsgFactory(final Hints userHints) throws IOException {
        this(userHints, FILENAME, Citations.EPSG);
    }

    /**
     * Constructs an authority factory for the given authorities. This constructor recognizes
     * the same hints than {@link #PropertyEpsgFactory(Hints)}. The {@code authorities} argument
     * should enumerate all relevant authorities, with {@linkplain Citations#EPSG EPSG} in last
     * position. For example {@link EsriExtension} returns {{@linkplain Citations#ESRI ESRI},
     * {@linkplain Citations#EPSG EPSG}}.
     *
     * @param userHints
     *          An optional set of hints, or {@code null} if none.
     * @param filename
     *          The name of the file to look in the {@link Hints#CRS_AUTHORITY_EXTRA_DIRECTORY}
     *          directory and in every {@code org/geotoolkit/referencing/factory/espg} directories
     *          found on the classpath.
     * @param authorities
     *          The organizations or parties responsible for definition and maintenance of the
     *          database. Should contains at least {@link Citations#EPSG}.
     * @throws IOException If an error occured while reading the definition files.
     *         Note that do exception is thrown if there is no file - in this case
     *         the factory is only considered not {@linkplain #availability available}.
     *
     * @since 3.00
     */
    public PropertyEpsgFactory(final Hints userHints, final String filename, final Citation... authorities)
            throws IOException
    {
        super(userHints, Hints.CRS_AUTHORITY_EXTRA_DIRECTORY, PropertyEpsgFactory.class, filename, authorities);
    }

    /**
     * Invoked by {@code FactoryRegistry} on registration. The default implementation
     * declares that this factory should give precedence to {@link ThreadedEpsgFactory}
     * and {@link LongitudeFirstEpsgFactory}.
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer);
        organizer.after(ThreadedEpsgFactory.class, true);
        organizer.after(LongitudeFirstEpsgFactory.class, false);
    }

    /**
     * Prints a list of codes that duplicate the ones provided by {@link ThreadedEpsgFactory}.
     * This is used in order to check the content of the {@value #FILENAME} file (or whatever
     * property file used as backing store for this factory) from the command line.
     *
     * {@preformat text
     *     java -jar geotk-referencing.jar test duplicates
     * }
     *
     * @param  out The writer where to print the report.
     * @return The set of duplicated codes.
     * @throws FactoryException if an error occured.
     *
     * @see org.geotoolkit.console.ReferencingCommands#test
     *
     * @since 2.4
     */
    public Set<String> reportDuplicates(final PrintWriter out) throws FactoryException {
        final AbstractAuthorityFactory sqlFactory =
                (AbstractAuthorityFactory) AuthorityFactoryFinder.getCRSAuthorityFactory(
                "EPSG", new Hints(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class));
        final Vocabulary resources = Vocabulary.getResources(null);
        out.println(resources.getLabel(Vocabulary.Keys.COMPARE_WITH));
        try {
            final IndentedLineWriter w = new IndentedLineWriter(out);
            w.setIndentation(4);
            w.write(sqlFactory.getBackingStoreDescription());
            w.flush();
        } catch (IOException e) {
            // Should never happen, since we are writting to a PrintWriter.
            throw new AssertionError(e);
        }
        out.println();
        final Set<String> wktCodes   = this.      getAuthorityCodes(IdentifiedObject.class);
        final Set<String> sqlCodes   = sqlFactory.getAuthorityCodes(IdentifiedObject.class);
        final Set<String> duplicated = new TreeSet<String>();
        for (String code : wktCodes) {
            code = code.trim();
            if (sqlCodes.contains(code)) {
                duplicated.add(code);
                /*
                 * Note: we don't use wktCodes.retainsAll(sqlCode) because the Set implementations
                 *       are usually not the standard ones, but rather some implementations backed
                 *       by a connection to the resources of the underlying factory. We also close
                 *       the connection after this loop for the same reason.  In addition, we take
                 *       this opportunity for sorting the codes.
                 */
            }
        }
        if (duplicated.isEmpty()) {
            out.println(resources.getString(Vocabulary.Keys.NO_DUPLICATION_FOUND));
        } else {
            for (final String code : duplicated) {
                out.print(resources.getLabel(Vocabulary.Keys.DUPLICATED_VALUE));
                out.println(code);
            }
        }
        return duplicated;
    }

    /**
     * Prints a list of CRS that can't be instantiated. This is used in order to check the content
     * of the {@value #FILENAME} file (or whatever property file used as backing store for this
     * factory) from the command line. To lauch from the command line, use the following:
     *
     * {@preformat text
     *     java -jar geotk-referencing.jar test creates
     * }
     *
     * @param  out The writer where to print the report.
     * @return The set of codes that can't be instantiated.
     * @throws FactoryException if an error occured while
     *         {@linkplain #getAuthorityCodes fetching authority codes}.
     *
     * @see org.geotoolkit.console.ReferencingCommands#test
     *
     * @since 2.4
     */
    public Set<String> reportInstantiationFailures(final PrintWriter out) throws FactoryException {
        final Set<String> codes = getAuthorityCodes(CoordinateReferenceSystem.class);
        final Map<String,String> failures = new TreeMap<String,String>();
        for (final String code : codes) {
            try {
                createCoordinateReferenceSystem(code);
            } catch (FactoryException exception) {
                failures.put(code, exception.getLocalizedMessage());
            }
        }
        if (!failures.isEmpty()) {
            final TableWriter writer = new TableWriter(out, " ");
            for (final Map.Entry<String,String> entry : failures.entrySet()) {
                writer.write(entry.getKey());
                writer.write(':');
                writer.nextColumn();
                writer.write(entry.getValue());
                writer.nextLine();
            }
            try {
                writer.flush();
            } catch (IOException e) {
                // Should not happen, since we are writting to a PrintWriter
                throw new AssertionError(e);
            }
        }
        return failures.keySet();
    }
}
