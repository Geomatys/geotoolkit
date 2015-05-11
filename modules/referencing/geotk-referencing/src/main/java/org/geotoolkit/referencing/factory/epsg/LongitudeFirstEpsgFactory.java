/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;


import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.referencing.factory.ImplementationHints;
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory;
import org.geotoolkit.metadata.Citations;


/**
 * An EPSG authority factory using (<var>longitude</var>, <var>latitude</var>) axis order.
 * This factory wraps a {@link ThreadedEpsgFactory} into an {@link OrderedAxisAuthorityFactory}
 * when first needed.
 * <p>
 * Users don't need to create explicitly an instance of this class. Instead, one can get
 * an instance using the following code:
 *
 * {@preformat java
 *     Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
 *     CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
 * }
 *
 * {@section To be more specific...}
 * The name of this class contains "<cite>longitude first</cite>" for simplicity, because the axes
 * reordering apply mostly to geographic CRS (in contrast, most projected CRS already have
 * (<var>x</var>, <var>y</var>) axis order, in which case this class has no effect). However, what
 * this implementation actually does is to force a <cite>right-handed</cite> coordinate system.
 * This approach works for projected CRS as well as geographic CRS ("<cite>longitude first</cite>"
 * is an inappropriate expression for projected CRS). It even works in cases like stereographic
 * projections, where the axes names look like (<var>South along 180°</var>, <var>South along 90°E</var>).
 * In such cases, aiming for "<cite>longitude first</cite>" would not make sense.
 *
 * {@note This class implements also the <code>DatumAuthorityFactory</code> interface as a matter
 *        of principle, in order to have a single class implementing all factories. However this
 *        is in theory useless since "axis order" doesn't make any sense for datum.}
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see OrderedAxisAuthorityFactory
 * @see Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER
 *
 * @since 2.3
 * @module
 */
@ImplementationHints(forceLongitudeFirst=true)
public class LongitudeFirstEpsgFactory extends OrderedAxisAuthorityFactory implements CRSAuthorityFactory,
        CSAuthorityFactory, CoordinateOperationAuthorityFactory, DatumAuthorityFactory
{
    /**
     * Creates a default factory. The
     * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER}
     * hint is always set to {@link Boolean#TRUE TRUE}. The
     * {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS FORCE_STANDARD_AXIS_DIRECTIONS} and
     * {@link Hints#FORCE_STANDARD_AXIS_UNITS FORCE_STANDARD_AXIS_UNITS} hints are set
     * to {@link Boolean#FALSE FALSE} by default. A different value for those two hints
     * can be specified using the {@linkplain #LongitudeFirstEpsgFactory(Hints) constructor
     * below}.
     */
    public LongitudeFirstEpsgFactory() {
        this(EMPTY_HINTS);
    }

    /**
     * Creates a factory from the specified set of hints.
     *
     * @param userHints An optional set of hints, or {@code null} for the default values.
     */
    public LongitudeFirstEpsgFactory(final Hints userHints) {
        super("EPSG", hints(userHints), null);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Hints hints(final Hints userHints) {
        /*
         * Set the hints for the backing store to fetch. I'm not sure that we should request a
         * ThreadedEpsgFactory implementation; for now we are making this requirement mostly as
         * a safety in order to get an implementation that is known to work, but we could relax
         * that in a future version. AbstractAuthorityFactory is the minimal class required with
         * current OrderedAxisAuthorityFactory API.
         */
        final Hints hints = (userHints != null ? userHints : EMPTY_HINTS).clone();
        hints.put(Hints.CRS_AUTHORITY_FACTORY, ThreadedEpsgFactory.class);
        return hints;
    }

    /**
     * Specifies that this factory should give precedence to {@link ThreadedEpsgFactory}.
     * So by default, the referencing framework will create CRS objects with axis order as defined
     * by the EPSG database in preference to CRS objects having the longitude before the latitude.
     *
     * @since 3.00
     */
    @Override
    protected void setOrdering(final Organizer organizer) {
        super.setOrdering(organizer);
        organizer.after(ThreadedEpsgFactory.class, true);
    }

    /**
     * Returns the authority for this EPSG database.
     * This authority will contains the database version in the {@linkplain Citation#getEdition
     * edition} attribute, together with the {@linkplain Citation#getEditionDate edition date}.
     */
    @Override
    public Citation getAuthority() {
        final Citation authority = super.getAuthority();
        return (authority != null) ? authority : Citations.EPSG;
    }
}
