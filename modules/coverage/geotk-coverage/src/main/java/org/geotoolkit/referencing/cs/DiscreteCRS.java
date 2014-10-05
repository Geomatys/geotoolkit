/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.cs;

import java.util.Set;
import java.util.Collection;
import java.io.Serializable;

import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.io.wkt.FormattableObject;
import org.opengis.metadata.extent.Extent;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.io.wkt.Formattable;
import org.geotoolkit.lang.Decorator;


/**
 * An implementation of {@link CoordinateReferenceSystem} delegating every method
 * calls to the wrapped CRS, except the coordinate system. The axes will be instances
 * of {@link DiscreteCoordinateSystemAxis} built from the ordinate values given at
 * construction time.
 * <p>
 * This class implements {@link GridGeometry}. But the <cite>grid to CRS</cite> transform
 * returned by the later is correct only if every axes are regular.  This is not verified
 * because the threshold for determining if an axis is regular or not is at caller choice.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.15
 * @module
 */
@Decorator(CoordinateReferenceSystem.class)
class DiscreteCRS<T extends CoordinateReferenceSystem> extends FormattableObject
        implements CoordinateReferenceSystem, GridGeometry, Formattable, Serializable
{
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6401567052946218355L;

    /**
     * The wrapped projected CRS.
     */
    protected final T crs;

    /**
     * The coordinate system.
     */
    protected final DiscreteCS cs;

    /**
     * The grid to CRS transform, created only when first needed.
     */
    transient MathTransform gridToCRS;

    /**
     * Creates a new instance wrapping the given CRS with the given coordinate system.
     *
     * @param crs The CRS to wrap.
     * @param cs  The coordinate system.
     */
    DiscreteCRS(final T crs, final DiscreteCS cs) {
        this.crs = crs;
        this.cs  = cs;
    }

    /**
     * Returns the name of the wrapped CRS.
     */
    @Override
    public final Identifier getName() {
        return crs.getName();
    }

    /**
     * Returns the alias of the wrapped CRS.
     */
    @Override
    public final Collection<GenericName> getAlias() {
        return crs.getAlias();
    }

    /**
     * Returns the identifiers of the wrapped CRS.
     */
    @Override
    public final Set<Identifier> getIdentifiers() {
        return crs.getIdentifiers();
    }

    /**
     * Returns the coordinate system built at construction time from the given axes.
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return cs;
    }

    /**
     * A discrete geographic CRS.
     */
    static final class Geographic extends DiscreteCRS<GeographicCRS> implements GeographicCRS {
        private static final long serialVersionUID = -6614621727541494429L;
        Geographic(final GeographicCRS crs, final double[]... ordinates) {
            super(crs, new DiscreteCS.Ellipsoidal(crs.getCoordinateSystem(), ordinates));
        }
        @Override public EllipsoidalCS getCoordinateSystem() {return (EllipsoidalCS) cs;}
        @Override public GeodeticDatum getDatum()            {return crs.getDatum();}
    }

    /**
     * A discrete projected CRS.
     */
    static final class Projected extends DiscreteCRS<ProjectedCRS> implements ProjectedCRS {
        private static final long serialVersionUID = 7468800070780104601L;
        Projected(final ProjectedCRS crs, final double[]... ordinates) {
            super(crs, new DiscreteCS.Cartesian(crs.getCoordinateSystem(), ordinates));
        }
        @Override public CartesianCS   getCoordinateSystem()   {return (CartesianCS) cs;}
        @Override public GeodeticDatum getDatum()              {return crs.getDatum();}
        @Override public GeographicCRS getBaseCRS()            {return crs.getBaseCRS();}
        @Override public Projection    getConversionFromBase() {return crs.getConversionFromBase();}
    }

    /**
     * A discrete vertical CRS.
     */
    static final class Vertical extends DiscreteCRS<VerticalCRS> implements VerticalCRS {
        private static final long serialVersionUID = -4037988222135517657L;
        Vertical(final VerticalCRS crs, final double[]... ordinates) {
            super(crs, new DiscreteCS.Vertical(crs.getCoordinateSystem(), ordinates));
        }
        @Override public VerticalCS getCoordinateSystem() {return (VerticalCS) cs;}
        @Override public VerticalDatum getDatum()         {return crs.getDatum();}
    }

    /**
     * A discrete temporal CRS.
     */
    static final class Temporal extends DiscreteCRS<TemporalCRS> implements TemporalCRS {
        private static final long serialVersionUID = -4154329164568410034L;
        Temporal(final TemporalCRS crs, final double[]... ordinates) {
            super(crs, new DiscreteCS.Time(crs.getCoordinateSystem(), ordinates));
        }
        @Override public TimeCS getCoordinateSystem() {return (TimeCS) cs;}
        @Override public TemporalDatum getDatum()     {return crs.getDatum();}
    }

    /**
     * A discrete single CRS of unknown type.
     */
    static final class Single extends DiscreteCRS<SingleCRS> implements SingleCRS {
        private static final long serialVersionUID = 1616462096697283316L;
        Single(final SingleCRS crs, final double[]... ordinates) {
            super(crs, new DiscreteCS(crs.getCoordinateSystem(), ordinates));
        }
        @Override public Datum getDatum() {return crs.getDatum();}
    }

    /**
     * Returns the grid range. The {@linkplain GridEnvelope#getLow() lower} values are
     * always 0, and the {@linkplain GridEnvelope#getHigh() upper} values are determined
     * by the number of discrete ordinates for each axes.
     */
    @Override
    public final GridEnvelope getExtent() {
        return cs.getExtent();
    }

    @Override
    @Deprecated
    public final GridEnvelope getGridRange() {
        return cs.getGridRange();
    }

    /**
     * Returns the transform from grid coordinates to CRS coordinates mapping pixel center.
     * This method assumes that all axes are regular (this is not verified).
     */
    @Override
    public synchronized MathTransform getGridToCRS() {
        if (gridToCRS == null) {
            gridToCRS = cs.getGridToCRS(this);
        }
        return gridToCRS;
    }

    /**
     * Returns the domain of validity of the wrapped CRS.
     */
    @Override
    public final Extent getDomainOfValidity() {
        return crs.getDomainOfValidity();
    }

    /**
     * Returns the scope of the wrapped CRS.
     */
    @Override
    public final InternationalString getScope() {
        return crs.getScope();
    }

    /**
     * Returns the remarks of the wrapped CRS.
     */
    @Override
    public final InternationalString getRemarks() {
        return crs.getRemarks();
    }

    /**
     * Delegates the formatting to the wrapped CRS if possible. It is okay to delegate to the
     * wrapped CRS despite having different CS, because the WKT representation of that CS is
     * not changed.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        if (crs instanceof Formattable) {
            return ((Formattable) crs).formatTo(formatter);
        } else {
            return null;
        }
    }

    /**
     * Returns the WKT formatted by the wrapped CRS.
     * See the javadoc comment in {@link #formatWKT(Formatter)}.
     */
    @Override
    public final String toWKT() throws UnsupportedOperationException {
        return crs.toWKT();
    }

    /**
     * Returns the string representation of the wrapped CRS.
     * This is usually the same than the WKT representation.
     */
    @Override
    public final String toString() {
        return crs.toString();
    }

    /**
     * Returns a hash code value for this CRS.
     */
    @Override
    public final int hashCode() {
        return crs.hashCode() + 31 * cs.hashCode();
    }

    /**
     * Compares this CS with the given object for equality.
     *
     * @param other The object to compare with this CRS for equality.
     */
    @Override
    public final boolean equals(final Object other) {
        if (other != null && other.getClass() == getClass()) {
            final DiscreteCRS<?> that = (DiscreteCRS<?>) other;
            return crs.equals(that.crs) && cs.equals(that.cs);
        }
        return false;
    }
}
