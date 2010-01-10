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
package org.geotoolkit.internal.image.io;

import javax.imageio.metadata.IIOMetadata;

import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.operation.Conversion;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.image.io.metadata.MetadataAccessor;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.referencing.CRS;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * A convenience specialization of {@link MetadataAccessor} for nodes related to the
 * {@code "CoordinateReferenceSystem"} node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07
 * @module
 */
public final class CRSAccessor extends MetadataAccessor {
    /**
     * Small tolerance factor for comparisons of floating point numbers.
     */
    private static final double EPS = 1E-10;

    /**
     * {@code true} if the elements that are equal to the default value should be ommited.
     */
    private static final boolean OMMIT_DEFAULTS = true;

    /**
     * Creates a new accessor for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                 sub-class is recommanded, but not mandatory.
     */
    public CRSAccessor(final IIOMetadata metadata) {
        super(metadata, FORMAT_NAME, "CoordinateReferenceSystem", null);
    }

    /**
     * Sets the {@code "name"} attribute for the given object.
     *
     * @param object    The object from which to fetch the name.
     * @param accessor  The accessor to use for setting the name attribute.
     */
    private static void setName(final IdentifiedObject object, final MetadataAccessor accessor) {
        setName(object, true, accessor, "name");
    }

    /**
     * Same as {@link #setName}, but uses the given attribute name instead than {@code "name"}.
     *
     * @param object    The object from which to fetch the name.
     * @param scoped    {@code true} if the name should contains the authority prefix.
     * @param accessor  The accessor to use for setting the name attribute.
     * @param attribute The attribute name ({@code "name"} by default).
     */
    private static void setName(final IdentifiedObject object, final boolean scoped,
            final MetadataAccessor accessor, final String attribute)
    {
        final ReferenceIdentifier id = object.getName();
        if (id != null) {
            String name = id.getCode();
            if (scoped) {
                final String authority = Citations.getIdentifier(id.getAuthority());
                if (authority != null) {
                    name = authority + DefaultNameSpace.DEFAULT_SEPARATOR + name;
                }
            }
            accessor.setAttribute(attribute, name);
        }
    }

    /**
     * Sets the datum to the given value.
     *
     * @param datum The datum, or {@code null}.
     */
    public void setDatum(final Datum datum) {
        final MetadataAccessor accessor = new MetadataAccessor(this, "Datum", null);
        setName(datum, accessor);
        accessor.setAttribute("type", MetadataEnum.getType(datum));
        if (datum instanceof GeodeticDatum) {
            final GeodeticDatum gd = (GeodeticDatum) datum;
            final Ellipsoid ellipsoid = gd.getEllipsoid();
            if (ellipsoid != null) {
                final MetadataAccessor child = new MetadataAccessor(accessor, "Ellipsoid", null);
                setName(ellipsoid, child);
                child.setAttribute("axisUnit", ellipsoid.getAxisUnit());
                child.setAttribute("semiMajorAxis", ellipsoid.getSemiMajorAxis());
                if (ellipsoid.isIvfDefinitive()) {
                    child.setAttribute("inverseFlattening", ellipsoid.getInverseFlattening());
                } else {
                    child.setAttribute("semiMinorAxis", ellipsoid.getSemiMinorAxis());
                }
            }
            final PrimeMeridian pm = gd.getPrimeMeridian();
            if (pm != null) {
                final MetadataAccessor child = new MetadataAccessor(accessor, "PrimeMeridian", null);
                setName(pm, child);
                child.setAttribute("greenwichLongitude", pm.getGreenwichLongitude());
                child.setAttribute("angularUnit", pm.getAngularUnit());
            }
        }
    }

    /**
     * Sets the coordinate system to the given value.
     *
     * @param cs The coordinate system, or {@code null}.
     */
    public void setCoordinateSystem(final CoordinateSystem cs) {
        final MetadataAccessor accessor = new MetadataAccessor(this, "CoordinateSystem", null);
        setName(cs, accessor);
        accessor.setAttribute("type", MetadataEnum.getType(cs));
        final int dimension = cs.getDimension();
        accessor.setAttribute("dimension", dimension);
        final MetadataAccessor axes = new MetadataAccessor(accessor, "Axes", "CoordinateSystemAxis");
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            axes.selectChild(axes.appendChild());
            setName(axis, axes);
            final String abbreviation = axis.getAbbreviation();
            if (!abbreviation.equals(axis.getName().getCode())) {
                axes.setAttribute("axisAbbrev", abbreviation);
            }
            axes.setAttribute("direction", axis.getDirection());
            boolean hasRangeMeaning = false;
            double value = axis.getMinimumValue();
            if (value > Double.NEGATIVE_INFINITY) {
                axes.setAttribute("minimumValue", value);
                hasRangeMeaning = true;
            }
            value = axis.getMaximumValue();
            if (value < Double.POSITIVE_INFINITY) {
                axes.setAttribute("maximumValue", value);
                hasRangeMeaning = true;
            }
            if (hasRangeMeaning) {
                axes.setAttribute("rangeMeaning", axis.getRangeMeaning());
            }
            axes.setAttribute("unit", axis.getUnit());
        }
    }

    /**
     * Sets the coordinate reference system to the given value.
     *
     * @param crs The coordinate reference system.
     *
     * @todo The base CRS is not yet declared for the {@code DerivedCRS} case.
     */
    public void setCRS(final CoordinateReferenceSystem crs) {
        setName(crs, this);
        setAttribute("type", MetadataEnum.getType(crs));
        final Datum datum = CRSUtilities.getDatum(crs);
        if (datum != null) {
            setDatum(datum);
        }
        final CoordinateSystem cs = crs.getCoordinateSystem();
        if (cs != null) {
            setCoordinateSystem(cs);
        }
        /*
         * For ProjectedCRS, the baseCRS is implicitly a GeographicCRS with the same datum.
         * For other kind of DerivedCRS, we need to declare the baseCRS (TODO).
         */
        if (crs instanceof GeneralDerivedCRS) {
            final Conversion conversion = ((GeneralDerivedCRS) crs).getConversionFromBase();
            final MetadataAccessor opAccessor = new MetadataAccessor(this, "Conversion", null);
            setName(conversion, opAccessor);
            setName(conversion.getMethod(), false, opAccessor, "method");
            addParameter(new MetadataAccessor[] {opAccessor, null}, conversion.getParameterValues(),
                    CRS.getEllipsoid(crs));
        }
    }

    /**
     * Adds the given parameter value using the given accessor. If the parameter value is actually
     * a {@link ParameterValueGroup}, then its child are added recursively.
     * <p>
     * In order to keep the metadata simplier, this method ommits some parameters that are equal
     * to the default value. In order to reduce the risk of error, we ommits a parameter only if
     * its default value is 0, or 1 in the particular case of the scale factor.
     *
     * @param accessors An array of length 2 where the first element is the accessor for the
     *        {@link Conversion} element. The second element will be created by this method
     *        when first needed, in order to create a {@code "Parameters"} element only if
     *        there is at least one parameter to write.
     * @param param The parameter or group of parameters to add.
     * @param ellipsoid The ellipsoid defined in the datum, or {@code null} if none.
     */
    private static void addParameter(final MetadataAccessor[] accessors,
            final GeneralParameterValue param, final Ellipsoid ellipsoid)
    {
        if (param instanceof ParameterValueGroup) {
            for (final GeneralParameterValue p : ((ParameterValueGroup) param).values()) {
                addParameter(accessors, p, ellipsoid);
            }
        }
        if (param instanceof ParameterValue<?>) {
            final ParameterValue<?> pv = (ParameterValue<?>) param;
            final Object value = pv.getValue();
            if (value != null) {
                final ParameterDescriptor<?> descriptor = pv.getDescriptor();
                final String name = descriptor.getName().getCode().trim();
                if (value instanceof Number) {
                    /*
                     * Check if we should skip this value (see the method javadoc for more details).
                     * Note that the omission of values equal to the default values can be disabled,
                     * but not the omission of ellipsoid value. This is for consistency with WKT
                     * formatting.
                     */
                    final double numericValue = ((Number) value).doubleValue();
                    if (ellipsoid != null) {
                        if (name.equalsIgnoreCase("semi_major")) {
                            if (equals(numericValue, ellipsoid.getSemiMajorAxis())) {
                                return;
                            }
                        } else if (name.equalsIgnoreCase("semi_minor")) {
                            if (equals(numericValue, ellipsoid.getSemiMinorAxis())) {
                                return;
                            }
                        }
                    }
                    if (OMMIT_DEFAULTS) {
                        final Object defaultValue = descriptor.getDefaultValue();
                        if (defaultValue instanceof Number) {
                            final double df = ((Number) defaultValue).doubleValue();
                            if (equals(numericValue, df)) {
                                if (df == (name.equalsIgnoreCase("scale_factor") ? 1 : 0)) {
                                    return;
                                }
                            }
                        }
                    }
                }
                MetadataAccessor accessor = accessors[1];
                if (accessor == null) {
                    accessor = new MetadataAccessor(accessors[0], "Parameters", "ParameterValue");
                    accessors[1] = accessor;
                }
                accessor.selectChild(accessor.appendChild());
                accessor.setAttribute("name", name);
                accessor.setAttribute("value", value.toString());
            }
        }
    }

    /**
     * Returns {@code true} if the given value is equals to the expected one,
     * accepting a tolerance interval.
     */
    private static boolean equals(final double actual, final double expected) {
        return Math.abs(actual - expected) <= Math.abs(expected)*EPS;
    }
}
