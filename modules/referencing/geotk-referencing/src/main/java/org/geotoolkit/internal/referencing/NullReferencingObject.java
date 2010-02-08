/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.referencing;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;

import org.opengis.util.*;
import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.metadata.extent.Extent;

import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.io.wkt.UnformattableObjectException;


/**
 * A referencing object for which every methods return {@code null} or a neutral value.
 * <strong>This is not a valid object</strong>. It is used only for initialization of
 * objects to be used by JAXB at unmarshalling time, as a way to simulate "no-argument"
 * constructor required by JAXB.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
public final class NullReferencingObject implements GeocentricCRS, GeographicCRS, ProjectedCRS,
        DerivedCRS, CompoundCRS, VerticalCRS, TemporalCRS, EngineeringCRS, ImageCRS, UserDefinedCS,
        SphericalCS, EllipsoidalCS, CartesianCS, CylindricalCS, PolarCS, VerticalCS, TimeCS, LinearCS,
        GeodeticDatum, VerticalDatum, TemporalDatum, EngineeringDatum, ImageDatum, Serializable
{
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7833201634716462036L;

    /**
     * The unique instance.
     */
    public static NullReferencingObject INSTANCE = new NullReferencingObject();

    /**
     * An arbitrary date to be used as the origin
     * (for now the Unix epoch, but could change in a future version).
     */
    private static final Date ORIGIN = new Date(0);

    /**
     * Do not allow other instantiation of {@link #INSTANCE}.
     */
    private NullReferencingObject() {
    }

    /** Returns {@code 0} in all cases. */
    @Override
    public int getDimension() {
        return 0;
    }

    /** Returns {@code DefaultCoordinateSystemAxis#UNDEFINED} in all cases. */
    @Override
    public CoordinateSystemAxis getAxis(int dimension) {
        return DefaultCoordinateSystemAxis.UNDEFINED;
    }

    /** Returns {@code this} in all cases. */
    @Override
    public GeographicCRS getBaseCRS() {
        return this;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public Projection getConversionFromBase() {
        return null;
    }

    /** Returns an empty list in all cases. */
    @Override
    public List<CoordinateReferenceSystem> getComponents() {
        return Collections.emptyList();
    }

    /** Returns {@code this} in all cases. */
    @Override
    public NullReferencingObject getCoordinateSystem() {
        return this;
    }

    /** Returns {@code this} in all cases. */
    @Override
    public NullReferencingObject getDatum() {
        return this;
    }

    /** Returns {@link VerticalDatumTypes#ELLIPSOIDAL} in all cases. */
    @Override
    public VerticalDatumType getVerticalDatumType() {
        return VerticalDatumTypes.ELLIPSOIDAL;
    }

    /** Returns {@link DefaultEllipsoid#WGS84} in all cases. */
    @Override
    public Ellipsoid getEllipsoid() {
        return DefaultEllipsoid.WGS84;
    }

    /** Returns {@link DefaultPrimeMeridian#GREENWICH} in all cases. */
    @Override
    public PrimeMeridian getPrimeMeridian() {
        return DefaultPrimeMeridian.GREENWICH;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public ReferenceIdentifier getName() {
        return null;
    }

    /** Returns an empty set in all cases. */
    @Override
    public Collection<GenericName> getAlias() {
        return Collections.emptySet();
    }

    /** Returns an empty set in all cases. */
    @Override
    public Set<ReferenceIdentifier> getIdentifiers() {
        return Collections.emptySet();
    }

    /** Returns {@code null} in all cases. */
    @Override
    public InternationalString getRemarks() {
        return null;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public InternationalString getScope() {
        return null;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public Extent getDomainOfValidity() {
        return null;
    }

    /** Returns {@link PixelInCell#CELL_CENTER} in all cases. */
    @Override
    public PixelInCell getPixelInCell() {
        return PixelInCell.CELL_CENTER;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public InternationalString getAnchorPoint() {
        return null;
    }

    /** Returns an arbitrary origin in all cases. */
    @Override
    public Date getOrigin() {
        return ORIGIN;
    }

    /** Returns {@code null} in all cases. */
    @Override
    public Date getRealizationEpoch() {
        return null;
    }

    /**
     * Throws the exception in all cases.
     *
     * @throws UnformattableObjectException Always thrown.
     */
    @Override
    public String toWKT() throws UnformattableObjectException {
        throw new UnformattableObjectException(NullReferencingObject.class);
    }

    /**
     * Returns the unique instance on deserialization.
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
