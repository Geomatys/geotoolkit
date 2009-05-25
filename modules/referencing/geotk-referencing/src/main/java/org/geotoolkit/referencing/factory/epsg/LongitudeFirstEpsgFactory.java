/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.referencing.factory.OrderedAxisAuthorityFactory;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * An EPSG authority factory using (<var>longitude</var>, <var>latitude</var>) axis order.
 * This factory wraps a {@link ThreadedEpsgFactory} into an {@link OrderedAxisAuthorityFactory}
 * when first needed.
 *
 * {@note In theory implementing the <code>DatumAuthorityFactory</code> interface is useless
 *        here, since "axis order" doesn't make any sense for them. However if we do not
 *        register this class as a <code>DatumAuthorityFactory</code> as well, users will
 *        get a <code>NoSuchFactoryException</code> when asking for a factory with the
 *        <code>FORCE_LONGITUDE_FIRST_AXIS_ORDER</code> hint set.}
 *
 * Users don't need to create explicitly an instance of this class. Instead, one can get
 * an instance using the following code:
 *
 * {@preformat java
 *     Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
 *     CRSAuthorityFactory factory = AuthorityFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
 * }
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
