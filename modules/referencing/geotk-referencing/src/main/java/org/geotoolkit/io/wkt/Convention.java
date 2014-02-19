/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.io.wkt;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.CoordinateOperation;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;


/**
 * The convention used for WKT formatting. This enumeration exists as an attempt to address
 * some of the problems documented in the Frank Warmerdam's
 * <a href="http://home.gdal.org/projects/opengis/wktproblems.html">OGC WKT Coordinate System Issues</a>
 * page.
 *
 * <p>The various conventions differ mostly in parameter names, and sometime in WKT syntax. For
 * example the Mercator projection has a parameter named "<cite>Longitude of natural origin</cite>"
 * by {@linkplain #EPSG}, "{@code central_meridian}" by {@linkplain #OGC} and "{@code NatOriginLong}"
 * by {@linkplain #GEOTIFF}. In addition the unit of the prime meridian shall be the angular unit
 * of the enclosing {@linkplain GeographicCRS geographic CRS} according the {@linkplain #OGC}
 * standard, but is restricted to decimal degrees by {@linkplain #ESRI}. Other differences are
 * documented in the javadoc of each enum value.</p>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.00
 *
 * @see WKTFormat#getConvention()
 * @see WKTFormat#setConvention(Convention)
 *
 * @since 3.20
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
 @Deprecated
public final class Convention {
    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> convention.
     * This is the default convention for all WKT formatting in the Geotk library.
     *
     * {@section Spacial case}
     * For {@link GeocentricCRS}, this convention uses the legacy set of Cartesian axes.
     * Those axes were defined in OGC 01-009 as <var>Other</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#EASTING Easting}</var> and
     * <var>{@linkplain DefaultCoordinateSystemAxis#NORTHING Northing}</var>
     * in metres, where the "<var>Other</var>" axis is toward prime meridian.
     *
     * @see Citations#OGC
     * @see #toConformCS(CoordinateSystem)
     */
    public static final org.apache.sis.io.wkt.Convention OGC = org.apache.sis.io.wkt.Convention.WKT1;

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> convention.
     * This convention uses the most descriptive parameter and projection names.
     *
     * {@section Spacial case}
     * For {@link GeocentricCRS}, this convention uses the new set of Cartesian axes.
     * Those axes are defined in ISO 19111 as
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_X Geocentric X}</var>,
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Y Geocentric Y}</var> and
     * <var>{@linkplain DefaultCoordinateSystemAxis#GEOCENTRIC_Z Geocentric Z}</var> in metres.
     *
     * @see Citations#EPSG
     * @see #toConformCS(CoordinateSystem)
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT2} enum,
     *             which is similar in projection and parameter names but different in syntax.
     */
    public static final org.apache.sis.io.wkt.Convention EPSG = org.apache.sis.io.wkt.Convention.WKT2;

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> convention.
     * This convention is similar to the {@link #OGC} convention except in four aspects:
     * <p>
     * <ul>
     *   <li>The angular units of {@code PRIMEM} and {@code PARAMETER} elements are always degrees,
     *       no matter the units of the enclosing {@code GEOGCS} element.</li>
     *   <li>The {@code AXIS} elements are ignored at parsing time.</li>
     *   <li>Unit names use American spelling instead than the international ones
     *       (e.g. "<cite>meter</cite>" instead than "<cite>metre</cite>").</li>
     *   <li>At parsing time, the {@code AXIS} elements are ignored.</li>
     * </ul>
     *
     * @see Citations#ESRI
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT1_COMMON_UNITS} enum,
     *             which is similar in syntax but different in projection and parameter names.
     */
    public static final org.apache.sis.io.wkt.Convention ESRI = org.apache.sis.io.wkt.Convention.WKT1_COMMON_UNITS;

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> convention.
     * This convention is similar to the {@link #OGC} convention except in three aspects:
     * <p>
     * <ul>
     *   <li>The Bursa-Wolf parameters are inserted straight into the {@code DATUM} element,
     *       without enclosing them in a {@code TOWGS84} element.</li>
     *   <li>The {@code PROJECTION} names are {@linkplain CoordinateOperation Coordinate
     *       Operation} names rather than {@linkplain OperationMethod Operation Method} names.</li>
     *   <li>Unit names use American spelling instead than the international ones
     *       (e.g. "<cite>meter</cite>" instead than "<cite>metre</cite>").</li>
     * </ul>
     *
     * @see Citations#ORACLE
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT1_COMMON_UNITS} enum,
     *             which is similar in syntax but different in projection and parameter names.
     */
    public static final org.apache.sis.io.wkt.Convention ORACLE = org.apache.sis.io.wkt.Convention.WKT1_COMMON_UNITS;

    /**
     * The <A HREF="http://www.unidata.ucar.edu/software/netcdf-java">NetCDF</A> convention.
     * This convention is similar to the {@link #OGC} convention except for parameter and
     * projection names.
     *
     * @see Citations#NETCDF
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT1_COMMON_UNITS} enum,
     *             which is similar in syntax but different in projection and parameter names.
     */
    public static final org.apache.sis.io.wkt.Convention NETCDF = org.apache.sis.io.wkt.Convention.WKT1_COMMON_UNITS;

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> convention.
     * This convention is similar to the {@link #OGC} convention except for parameter and
     * projection names.
     *
     * @see Citations#GEOTIFF
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT1_COMMON_UNITS} enum,
     *             which is similar in syntax but different in projection and parameter names.
     */
    public static final org.apache.sis.io.wkt.Convention GEOTIFF = org.apache.sis.io.wkt.Convention.WKT1_COMMON_UNITS;

    /**
     * The <A HREF="http://trac.osgeo.org/proj/">Proj.4</A> convention.
     * This convention uses very short parameter and projection names.
     * Other differences are:
     * <p>
     * <ul>
     *   <li>The angular units of {@code PRIMEM} and {@code PARAMETER} elements are always degrees,
     *       no matter the units of the enclosing {@code GEOGCS} element.</li>
     * </ul>
     *
     * @see Citations#PROJ4
     *
     * @deprecated This enum has been mapped to the Apache SIS {@code WKT1_COMMON_UNITS} enum,
     *             which is similar in syntax but different in projection and parameter names.
     */
    public static final org.apache.sis.io.wkt.Convention PROJ4 = org.apache.sis.io.wkt.Convention.WKT1_COMMON_UNITS;

    /**
     * A special convention for formatting objects as stored internally by Geotk. In the majority
     * of cases, the result will be identical to the one we would get using the {@link #OGC}
     * convention. However in the particular case of map projections, the result may be quite
     * different because of the way Geotk separates the linear from the non-linear parameters.
     * <p>
     * This convention is used only for debugging purpose.
     *
     * @see Formatter#isInternalWKT()
     */
    @Debug
    public static final org.apache.sis.io.wkt.Convention INTERNAL = org.apache.sis.io.wkt.Convention.INTERNAL;

    private Convention() {
    }
}
