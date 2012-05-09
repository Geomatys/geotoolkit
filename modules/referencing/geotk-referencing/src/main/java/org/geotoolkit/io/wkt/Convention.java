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

import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;

import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.CoordinateOperation;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;

import static javax.measure.unit.NonSI.DEGREE_ANGLE;


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
 * @version 3.20
 *
 * @see WKTFormat#getConvention()
 * @see WKTFormat#setConvention(Convention)
 *
 * @since 3.20
 * @module
 */
public enum Convention {
    /**
     * The <A HREF="http://www.opengeospatial.org">Open Geospatial consortium</A> convention.
     * This is the default convention for all WKT formatting in the Geotk library.
     *
     * @see Citations#OGC
     */
    OGC(null, false),

    /**
     * The <A HREF="http://www.epsg.org">European Petroleum Survey Group</A> convention.
     * This convention uses the most descriptive parameter and projection names.
     *
     * @see Citations#EPSG
     */
    EPSG(null, false),

    /**
     * The <A HREF="http://www.esri.com">ESRI</A> convention.
     * This convention differs from other conventions in four aspects:
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
     */
    ESRI(DEGREE_ANGLE, true),

    /**
     * The <A HREF="http://www.oracle.com">Oracle</A> convention.
     * This convention differs from other conventions in three aspects:
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
     */
    ORACLE(null, true),

    /**
     * The <A HREF="http://www.unidata.ucar.edu/software/netcdf-java">NetCDF</A> convention.
     * This convention differs from other conventions only in parameter and projection names.
     *
     * @see Citations#NETCDF
     */
    NETCDF(null, false),

    /**
     * The <A HREF="http://www.remotesensing.org/geotiff/geotiff.html">GeoTIFF</A> convention.
     * This convention differs from other conventions only in parameter and projection names.
     *
     * @see Citations#GEOTIFF
     */
    GEOTIFF(null, false),

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
     */
    PROJ4(DEGREE_ANGLE, false),

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
    INTERNAL(null, false);

    /**
     * If non-null, forces {@code PRIMEM} and {@code PARAMETER} angular units to this field
     * value instead than inferring it from the context. The standard value is {@code null},
     * which mean that the angular units are inferred from the context as required by the
     * <a href="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PRIMEM">WKT specification</a>.
     *
     * @see ReferencingParser#getForcedAngularUnit()
     */
    public final Unit<Angle> forcedAngularUnit;

    /**
     * {@code true} if the convention uses US unit names instead of the international names.
     * For example Americans said [@code "meter"} instead of {@code "metre"}.
     */
    final boolean unitUS;

    /**
     * The citation for this enumeration, fetched when first needed in order to avoid
     * too early class loading.
     */
    private transient Citation citation;

    /**
     * Creates a new enum.
     */
    private Convention(final Unit<Angle> angularUnit, final boolean unitUS) {
        this.forcedAngularUnit = angularUnit;
        this.unitUS = unitUS;
    }

    /**
     * Returns the citation for this enumeration. This method returns the {@link Citations}
     * constant of the same name than this enum.
     *
     * @return The citation for this enum.
     *
     * @see WKTFormat#getAuthority()
     */
    public Citation getCitation() {
        Citation c = citation;
        if (c == null) {
            // No need to synchronize - this is not a big deal if the field is fetched twice.
            if (this == INTERNAL) {
                final DefaultCitation dc = new DefaultCitation("Internal WKT");
                dc.setCitedResponsibleParties(Citations.GEOTOOLKIT.getCitedResponsibleParties());
                dc.freeze();
                c = dc;
            } else try {
                c = (Citation) Citations.class.getField(name()).get(null);
            } catch (ReflectiveOperationException e) {
                // Should never happen.
                throw new AssertionError(e);
            }
            citation = c;
        }
        return c;
    }

    /**
     * Returns the convention for the given citation.
     *
     * @param  citation The citation for which to get the convention, or {@code null}.
     * @param  defaultConvention The default convention to return if none where found for
     *         the given citation. May be {@code null}.
     * @return The convention, or {@code null} if no matching convention were found and the
     *         {@code defaultConvention} argument is {@code null}.
     */
    public static Convention forCitation(final Citation citation, final Convention defaultConvention) {
        if (citation != null) {
            for (final Convention candidate : values()) {
                if (Citations.identifierMatches(candidate.getCitation(), citation)) {
                    return candidate;
                }
            }
        }
        return defaultConvention;
    }

    /**
     * Returns the convention for the given identifier.
     *
     * @param  identifier The identifier for which to get the convention, or {@code null}.
     * @param  defaultConvention The default convention to return if none where found for
     *         the given identifier. May be {@code null}.
     * @return The convention, or {@code null} if no matching convention were found and the
     *         {@code defaultConvention} argument is {@code null}.
     */
    public static Convention forIdentifier(final String identifier, final Convention defaultConvention) {
        if (identifier != null) {
            for (final Convention candidate : values()) {
                if (Citations.identifierMatches(candidate.getCitation(), identifier)) {
                    return candidate;
                }
            }
        }
        return defaultConvention;
    }
}
