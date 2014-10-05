/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.referencing;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.io.Serializable;

import org.opengis.util.*;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.metadata.extent.Extent;

import org.apache.sis.xml.NilReason;
import org.apache.sis.xml.NilObject;
import org.geotoolkit.referencing.cs.Axes;
import org.apache.sis.io.wkt.UnformattableObjectException;
import org.apache.sis.referencing.CommonCRS;


/**
 * A referencing object for which every methods return {@code null} or a neutral value.
 * <strong>This is not a valid object</strong>. It is used only for initialization of
 * objects to be used by JAXB at unmarshalling time, as a way to simulate "no-argument"
 * constructor required by JAXB.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public final class NilReferencingObject implements GeocentricCRS, GeographicCRS, ProjectedCRS,
        DerivedCRS, CompoundCRS, VerticalCRS, TemporalCRS, EngineeringCRS, ImageCRS, UserDefinedCS,
        SphericalCS, EllipsoidalCS, CartesianCS, CylindricalCS, PolarCS, VerticalCS, TimeCS, LinearCS,
        GeodeticDatum, VerticalDatum, TemporalDatum, EngineeringDatum, ImageDatum, NilObject, Serializable
{
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7833201634716462036L;

    /**
     * The unique instance.
     */
    public static final NilReferencingObject INSTANCE = new NilReferencingObject();

    /**
     * Do not allow other instantiation of {@link #INSTANCE}.
     */
    private NilReferencingObject() {
    }

    /** This object is empty because the value will be provided later. */
    @Override
    public NilReason getNilReason() {
        return NilReason.TEMPLATE;
    }

    /** Returns {@code 0} in all cases. */
    @Override
    public int getDimension() {
        return 0;
    }

    /** Returns {@code DefaultCoordinateSystemAxis#UNDEFINED} in all cases. */
    @Override
    public CoordinateSystemAxis getAxis(int dimension) {
        return Axes.UNDEFINED;
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
    public NilReferencingObject getCoordinateSystem() {
        return this;
    }

    /** Returns {@code this} in all cases. */
    @Override
    public NilReferencingObject getDatum() {
        return this;
    }

    /** Returns {@link VerticalDatumTypes#ELLIPSOIDAL} in all cases. */
    @Override
    public VerticalDatumType getVerticalDatumType() {
        return VerticalDatumTypes.ELLIPSOIDAL;
    }

    /** Returns WGS84 in all cases. */
    @Override
    public Ellipsoid getEllipsoid() {
        return CommonCRS.WGS84.ellipsoid();
    }

    /** Returns Greenwich in all cases. */
    @Override
    public PrimeMeridian getPrimeMeridian() {
        return CommonCRS.WGS84.primeMeridian();
    }

    /** Returns {@code null} in all cases. */
    @Override
    public Identifier getName() {
        return null;
    }

    /** Returns an empty set in all cases. */
    @Override
    public Collection<GenericName> getAlias() {
        return Collections.emptySet();
    }

    /** Returns an empty set in all cases. */
    @Override
    public Set<Identifier> getIdentifiers() {
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

    /** Returns an arbitrary origin (for now the Unix epoch) in all cases. */
    @Override
    public Date getOrigin() {
        return new Date(0);
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
        throw new UnformattableObjectException();
    }

    /**
     * Returns the unique instance on deserialization.
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
