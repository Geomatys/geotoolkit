/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.Projection;


/**
 * A {@code <CoordinateReferenceSystem>} element in
 * {@linkplain GeographicMetadataFormat geographic metadata format}, together with its
 * {@code <CoordinateSystem>} and {@code <Datum>} child elements.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 *
 * @deprecated Replaced by the standard metadata objects defined by ISO 19115-2. The
 *   {@link SpatialMetadata} class can convert automatically those metadata objects
 *   to {@code IIOMetadata}.
 */
@Deprecated
public class ImageReferencing extends MetadataAccessor {
    /**
     * The {@code "rectifiedGridDomain/crs/cs"} node.
     */
    ChildList<Axis> cs;

    /**
     * The {@code "rectifiedGridDomain/crs/projection"} node.
     */
    ChildList<Parameter> projection;

    /**
     * The {@code "rectifiedGridDomain/crs/datum"} node.
     */
    private MetadataAccessor datum;

    /**
     * The {@code "rectifiedGridDomain/crs/datum/ellipsoid"} node, present only
     * in the case of a {@linkplain GeodeticDatum geodetic datum}.
     */
    private MetadataAccessor ellipsoid;

    /**
     * The {@code "rectifiedGridDomain/crs/datum/primeMeridian"} node, present only
     * in the case of a {@linkplain GeodeticDatum geodetic datum}.
     */
    private MetadataAccessor primeMeridian;

    /**
     * The {@code "rectifiedGridDomain/crs/datum/ellipsoid/semiMajorAxis"} node.
     */
    private MetadataAccessor semiMajorAxis;

    /**
     * The {@code "rectifiedGridDomain/crs/datum/ellipsoid/secondDefiningParameter/semiMinorAxis"}
     * node.
     */
    private MetadataAccessor semiMinorAxis;

    /**
     * The {@code "rectifiedGridDomain/crs/datum/ellipsoid/secondDefiningParameter/inverseFlattening"}
     * node.
     */
    private MetadataAccessor inverseFlattening;

    /**
     * Creates a parser for a coordinate system. This constructor should not be invoked
     * directly; use {@link GeographicMetadata#getReferencing} instead.
     *
     * @param metadata The metadata node.
     */
    protected ImageReferencing(final GeographicMetadata metadata) {
        super(metadata, null, "rectifiedGridDomain/crs", null);
    }

    /**
     * Returns the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain CoordinateReferenceSystem coordinate reference system}.
     *
     * @return The name and type of the coordinate reference system.
     *
     * @category CoordinateReferenceSystem
     */
    public Identification getCoordinateReferenceSystem() {
        return new Identification(this);
    }

    /**
     * Sets the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain CoordinateReferenceSystem coordinate reference system}.
     *
     * @param name The coordinate reference system name, or {@code null} if unknown.
     * @param type The coordinate reference system type (usually
     *             {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#GEOGRAPHIC} or
     *             {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#PROJECTED}), or
     *             {@code null} if unknown.
     *
     * @category CoordinateReferenceSystem
     */
    public void setCoordinateReferenceSystem(final String name, final String type) {
        setAttribute("name", name);
        setAttribute  ("type", type, GeographicMetadataFormat.CRS_TYPES);
    }

    /**
     * Returns the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain Datum datum}, or {@code null} if it is not defined.
     *
     * @return The name and type of the datum, or {@code null} if unknown.
     *
     * @category Datum
     */
    public Identification getDatum() {
        return (datum != null) ? new Identification(datum) : null;
    }

    /**
     * Sets the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain Datum datum}.
     *
     * @param name The datum name, or {@code null} if unknown.
     * @param type The datum type (usually
     *             {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#GEODETIC}), or
     *             {@code null} if unknown.
     *
     * @category Datum
     */
    public void setDatum(final String name, final String type) {
        final MetadataAccessor datum = getDatumAccessor();
        datum.setAttribute("name", name);
        datum.setAttribute  ("type", type, GeographicMetadataFormat.DATUM_TYPES);
    }

    /**
     * Returns the prime meridian name, or {@code null} if not defined.
     *
     * @return The prime meridian name.
     *
     * @category Datum
     */
    public String getPrimeMeridianName() {
        return (primeMeridian != null) ? primeMeridian.getAttribute("name") : null;
    }

    /**
     * Sets the name of the prime meridian.
     *
     * @param name The name of the prime meridian, or {@code null} if not known.
     *
     * @category Datum
     */
    public void setPrimeMeridianName(final String name) {
        getPrimeMeridianAccessor().setAttribute("name", name);
    }

    /**
     * Returns the prime meridian greenwich longitude, or {@linkplain Double#NaN NaN}
     * if this value is not defined.
     *
     * @return The prime meridian Greenwich longitude.
     *
     * @category Datum
     */
    public double getPrimeMeridianGreenwichLongitude() {
        final Double green = (primeMeridian != null) ?
            primeMeridian.getAttributeAsDouble("greenwichLongitude") : null;
        return (green == null) ? Double.NaN : green;
    }

    /**
     * Sets the greenwich longitude for the prime meridian.
     *
     * @param greenwichLongitude The greenwich longitude for the prime meridian, or
     *        {@linkplain Double#NaN NaN} if unknown.
     *
     * @category Datum
     */
    public void setPrimeMeridianGreenwichLongitude(final double greenwichLongitude) {
        getPrimeMeridianAccessor().
                setAttribute("greenwichLongitude", greenwichLongitude);
    }

    /**
     * Returns the name of the {@linkplain Ellipsoid ellipsoid}, or {@code null} if
     * not defined.
     *
     * @return The name and type of the ellipsoid, or {@code null} if unknown.
     *
     * @category Ellipsoid
     */
    public String getEllipsoidName() {
        return (ellipsoid != null) ? ellipsoid.getAttribute("name") : null;
    }

    /**
     * Sets the name of the {@linkplain Ellipsoid ellipsoid}.
     *
     * @param name The ellipsoid name, or {@code null} if unknown.
     *
     * @category Ellipsoid
     */
    public void setEllipsoidName(final String name) {
        getEllipsoidAccessor().setAttribute("name", name);
    }

    /**
     * Returns the unit of the {@linkplain Ellipsoid ellipsoid}, or {@code null}
     * if it is not defined.
     *
     * @return The unit of ellipsoid axes, or {@code null} if unknown.
     *
     * @category Ellipsoid
     */
    public String getEllipsoidUnit() {
        return (ellipsoid != null) ? ellipsoid.getAttribute("unit") : null;
    }

    /**
     * Sets the unit of the {@linkplain Ellipsoid ellipsoid}.
     *
     * @param unit The ellipsoid unit, or {@code null} if unknown.
     *
     * @category Ellipsoid
     */
    public void setEllipsoidUnit(final String unit) {
        getEllipsoidAccessor().setAttribute("unit", unit);
    }

    /**
     * Returns the semi-major axis value for this {@linkplain Ellipsoid ellipsoid}.
     * or {@linkplain Double#NaN NaN} if not defined.
     *
     * @return The semi-major axis length.
     *
     * @category Ellipsoid
     */
    public double getSemiMajorAxis() {
        final Double semiMajor = (semiMajorAxis != null) ?
                semiMajorAxis.getUserObject(Double.class) : null;
        return (semiMajor == null) ? Double.NaN : semiMajor;
    }

    /**
     * Sets the semi-major axis value of the {@linkplain Ellipsoid ellipsoid}.
     *
     * @param value The semi-major axis value, or {@linkplain Double#NaN NaN} if unknown.
     *
     * @category Ellipsoid
     */
    public void setSemiMajorAxis(final double value) {
        getSemiMajorAxisAccessor().setUserObject(value);
    }

    /**
     * Returns the semi-minor axis value for this {@linkplain Ellipsoid ellipsoid},
     * or {@linkplain Double#NaN NaN} if not defined.
     *
     * @return The semi-minor axis length.
     *
     * @category Ellipsoid
     */
    public double getSemiMinorAxis() {
        final Double semiMinor = (semiMinorAxis != null) ?
                semiMinorAxis.getUserObject(Double.class) : null;
        return (semiMinor == null) ? Double.NaN : semiMinor;
    }

    /**
     * Sets the semi-minor axis value of the {@linkplain Ellipsoid ellipsoid}. This
     * method should be called only if the {@link #setInverseFlattening} method
     * has not already been called.
     *
     * @param value The semi-minor axis value, or {@linkplain Double#NaN NaN} if unknown.
     *
     * @category Ellipsoid
     */
    public void setSemiMinorAxis(final double value) {
        getSemiMinorAxisAccessor().setUserObject(value);
    }

    /**
     * Returns the inverse flattening value of the {@linkplain Ellipsoid ellipsoid},
     * or {@linkplain Double#NaN NaN} if not defined.
     *
     * @return The ellipsoid inverse flattening.
     *
     * @category Ellipsoid
     */
    public double getInverseFlattening() {
        return (inverseFlattening != null) ? inverseFlattening.getUserObject(Double.class) : null;
    }

    /**
     * Sets the inverse flattening value of the {@linkplain Ellipsoid ellipsoid}. This method
     * should be called only if the {@link #setSemiMinorAxis} method has not already been called.
     *
     * @param value The inverseFlattening value, or {@linkplain Double#NaN NaN} if unknown.
     *
     * @category Ellipsoid
     */
    public void setInverseFlattening(final double value) {
        getInverseFlatteningAccessor().setUserObject(value);
    }

    /**
     * Returns the number of dimensions, which is the number of {@linkplain Axis axes}
     * of the {@linkplain CoordinateSystem coordinate system}.
     *
     * @return The number of axes in the coordinate system.
     *
     * @category CoordinateSystem
     */
    public int getDimension() {
        return getCoordinateSystemAccessor().childCount();
    }

    /**
     * Returns the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain CoordinateSystem coordinate system}, or {@code null} if it is not defined.
     *
     * @return The name and type of the coordinate system, or {@code null} if unknown.
     *
     * @category CoordinateSystem
     */
    public Identification getCoordinateSystem() {
        return (cs != null) ? new Identification(cs) : null;
    }

    /**
     * Sets the {@linkplain Identification#name name} and {@linkplain Identification#type type}
     * of the {@linkplain CoordinateSystem coordinate system}.
     *
     * @param name The coordinate system name, or {@code null} if unknown.
     * @param type The coordinate system type (usually
     *             {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#ELLIPSOIDAL} or
     *             {@value org.geotoolkit.image.io.metadata.GeographicMetadataFormat#CARTESIAN}), or
     *             {@code null} if unknown.
     *
     * @category CoordinateSystem
     */
    public void setCoordinateSystem(final String name, final String type) {
        final MetadataAccessor cs = getCoordinateSystemAccessor();
        cs.setAttribute("name", name);
        cs.setAttribute  ("type", type, GeographicMetadataFormat.CS_TYPES);
    }

    /**
     * Returns the axis at the specified index.
     *
     * @param  index the axis index, ranging from 0 inclusive to {@link #getDimension} exclusive.
     * @return The axis at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     *
     * @category CoordinateSystem
     */
    public Axis getAxis(final int index) throws IndexOutOfBoundsException {
        return getCoordinateSystemAccessor().getChild(index);
    }

    /**
     * Adds an {@linkplain CoordinateSystemAxis axis} to the
     * {@linkplain CoordinateSystem coordinate system}.
     *
     * @param  name The axis name, or {@code null} if unknown.
     * @param  direction The {@linkplain AxisDirection axis direction}
     *         (usually {@code "east"}, {@code "weast"}, {@code "north"}, {@code "south"},
     *         {@code "up"} or {@code "down"}), or {@code null} if unknown.
     * @param  units The axis units symbol, or {@code null} if unknown.
     * @return The newly added axis.
     *
     * @category CoordinateSystem
     * @see CoordinateSystemAxis
     * @see AxisDirection
     */
    public Axis addAxis(final String name, final String direction, final String units) {
        final Axis axis = getCoordinateSystemAccessor().addChild();
        axis.setName(name);
        axis.setDirection(direction);
        axis.setUnits(units);
        return axis;
    }

    /**
     * Returns the name of the {@code Projection projection}, or {@code null} if not defined.
     *
     * @return The projection name.
     *
     * @category Projection
     * @see Projection
     */
    public String getProjectionName() {
        return (projection != null) ? projection.getAttribute("name") : null;
    }

    /**
     * Sets the name of the {@linkplain Projection projection}.
     *
     * @param name The projection name, or {@code null} if unknown.
     *
     * @category Projection
     * @see Projection
     */
    public void setProjectionName(final String name) {
        getProjectionAccessor().setAttribute("name", name);
    }

    /**
     * Returns the parameter at the index for the {@linkplain Projection projection},
     * or {@code null} if not defined.
     *
     * @param  index the parameter index.
     * @return The parameter at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     *
     * @category Projection
     */
    public Parameter getParameter(final int index) throws IndexOutOfBoundsException {
        return (projection != null) ? projection.getChild(index) : null;
    }

    /**
     * Returns all parameters found for the {@linkplain Projection projection} in an
     * array. Returns an empty array if no parameters have been defined. This method
     * never returns {@code null}.
     *
     * @return All parameters found for the projection.
     *
     * @category Projection
     */
    public Parameter[] getParameters() {
        final int size = (projection != null) ? projection.childCount() : 0;
        final Parameter[] params = new Parameter[size];
        for (int i=0; i<size; i++) {
            params[i] = getProjectionAccessor().getChild(i);
        }
        return params;
    }

    /**
     * Adds an {@linkplain ParameterValue parameter} to the
     * {@linkplain Projection projection}.
     *
     * @param  name  The parameter name, or {@code null} if unknown.
     * @param  value The value for this parameter, or {@linkplain Double#NaN NaN} if unknown.
     * @return The newly added parameter.
     *
     * @category Projection
     * @see ParameterValue
     */
    public Parameter addParameter(final String name, final double value) {
        final Parameter parameter = getProjectionAccessor().addChild();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
    }

    /**
     * Returns the {@linkplain CoordinateReferenceSystem coordinate reference system} in
     * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> format</A>, or {@code null} if none. This is used only if the
     * CRS was defined explicitly that way. Doing so is not compliant with OGC standard,
     * but is sometime done in practice.
     *
     * @return The coordinate reference system in WKT format.
     */
    public String getWKT() {
        return getAttribute("WKT");
    }

    /**
     * Sets the {@linkplain CoordinateReferenceSystem coordinate reference system} in
     * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> format</A>.
     *
     * @param wkt The coordinate reference system in WKT format.
     */
    public void setWKT(final String wkt) {
        setAttribute("WKT", wkt);
    }

    /**
     * Builds a {@linkplain MetadataAccessor coordinate system accessor} if it is
     * not already instanciated, and returns it, or return the current one if defined.
     */
    private ChildList<Axis> getCoordinateSystemAccessor() {
        if (cs == null) {
            cs = new Axis.List(metadata);
        }
        return cs;
    }

    /**
     * Builds a {@linkplain MetadataAccessor datum accessor} if it is not already
     * instanciated, and returns it, or return the current datum if defined.
     */
    private MetadataAccessor getDatumAccessor() {
        if (datum == null) {
            datum = new MetadataAccessor(metadata, null, "rectifiedGridDomain/crs/datum", null);
        }
        return datum;
    }

    /**
     * Builds a {@linkplain MetadataAccessor ellipsoid accessor} if it is not already
     * instanciated, and returns it, or return the current one if defined.
     */
    private MetadataAccessor getEllipsoidAccessor() {
        if (ellipsoid == null) {
            ellipsoid = new MetadataAccessor(metadata, null,
                "rectifiedGridDomain/crs/datum/ellipsoid", null);
        }
        return ellipsoid;
    }

    /**
     * Builds a {@linkplain MetadataAccessor inverse flattening accessor} if it is not
     * already instanciated, and returns it, or return the current one if defined.
     */
    private MetadataAccessor getInverseFlatteningAccessor() {
        if (inverseFlattening == null) {
            inverseFlattening = new MetadataAccessor(metadata, null,
                "rectifiedGridDomain/crs/datum/ellipsoid/secondDefiningParameter/inverseFlattening", null);
        }
        return inverseFlattening;
    }

    /**
     * Builds a {@linkplain MetadataAccessor prime meridian accessor} if it is not
     * already instanciated, and returns it, or return the current one if defined.
     */
    private MetadataAccessor getPrimeMeridianAccessor() {
        if (primeMeridian == null) {
            primeMeridian = new MetadataAccessor(metadata, null,
                "rectifiedGridDomain/crs/datum/primeMeridian", null);
        }
        return primeMeridian;
    }

    /**
     * Builds a {@linkplain MetadataAccessor projection accessor} if it is not
     * already instanciated, and returns it, or return the current one if defined.
     */
    private ChildList<Parameter> getProjectionAccessor() {
        if (projection == null) {
            projection = new Parameter.List(metadata);
        }
        return projection;
    }

    /**
     * Builds a {@linkplain MetadataAccessor semi major axis accessor} if it is not
     * already instanciated, and returns it, or return the current one if defined.
     */
    private MetadataAccessor getSemiMajorAxisAccessor() {
        if (semiMajorAxis == null) {
            semiMajorAxis = new MetadataAccessor(metadata, null,
                "rectifiedGridDomain/crs/datum/ellipsoid/semiMajorAxis", null);
        }
        return semiMajorAxis;
    }

    /**
     * Builds a {@linkplain MetadataAccessor semi minor axis accessor} if it is not
     * already instanciated, and returns it, or return the current one if defined.
     */
    private MetadataAccessor getSemiMinorAxisAccessor() {
        if (semiMinorAxis == null) {
            semiMinorAxis     = new MetadataAccessor(metadata, null,
                "rectifiedGridDomain/crs/datum/ellipsoid/secondDefiningParameter/semiMinorAxis", null);
        }
        return semiMinorAxis;
    }
}
