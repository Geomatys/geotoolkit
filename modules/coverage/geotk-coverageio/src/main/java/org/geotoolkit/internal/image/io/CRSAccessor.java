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
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.crs.ProjectedCRS;
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

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * A convenience specialization of {@link MetadataAccessor} for nodes related to the
 * {@code "CoordinateReferenceSystem"} node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public final class CRSAccessor extends MetadataAccessor {
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
            axes.setAttribute("axisAbbrev", axis.getAbbreviation());
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
        if (crs instanceof ProjectedCRS) {
            final Conversion conversion = ((GeneralDerivedCRS) crs).getConversionFromBase();
            final MetadataAccessor proj = new MetadataAccessor(this, "Conversion", null);
            setName(conversion, proj);
            setName(conversion.getMethod(), false, proj, "method");
            addParameter(new MetadataAccessor(proj, "Parameters", "ParameterValue"),
                         conversion.getParameterValues());
        }
    }

    /**
     * Adds the given parameter value using the given accessor. If the parameter value is actually
     * a {@link ParameterValueGroup}, then its child are added recursively.
     *
     * @param accessor The accessor to use for adding the parameters.
     * @param param The parameter or group of parameters to add.
     */
    private static void addParameter(final MetadataAccessor accessor, final GeneralParameterValue param) {
        if (param instanceof ParameterValue<?>) {
            final Object value = ((ParameterValue<?>) param).getValue();
            if (value != null) {
                accessor.selectChild(accessor.appendChild());
                setName(param.getDescriptor(), false, accessor, "name");
                accessor.setAttribute("value", value.toString());
            }
        } else if (param instanceof ParameterValueGroup) {
            for (final GeneralParameterValue p : ((ParameterValueGroup) param).values()) {
                addParameter(accessor, p);
            }
        }
    }
}
