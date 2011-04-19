/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.referencing.operation.provider;

import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.ConicProjection;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>American Polyconic</cite>" projection (EPSG:9818).
 * EPSG defines code for this projection, 9818 for the ellipsoidal case.
 * <p>
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/polyconic.html">Polyconic on
 * RemoteSensing.org</A>. The math transform implementations instantiated by this provider may
 * be any of the following classes:
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Polyconic}</li>
 * </ul>
 *
 * @author Simon Reynard (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@Immutable
public class Polyconic extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1681887819214500096L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     */
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            Identifiers.CENTRAL_MERIDIAN.select(
                "central_meridian",             // OGC
                "Central_Meridian",             // ESRI
                "Longitude of natural origin",  // EPSG
                "NatOriginLong");               // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN =
            Identifiers.LATITUDE_OF_ORIGIN.select(
                "latitude_of_origin",           // OGC
                "Latitude of natural origin",   // EPSG
                "Standard_Parallel_1",          // ESRI
                "NatOriginLat");                // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_EASTING =
            Identifiers.FALSE_EASTING.select(
                "False easting",    // EPSG
                "FalseEasting");    // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_NORTHING =
            Identifiers.FALSE_NORTHING.select(
                "False northing",   // EPSG
                "FalseNorthing");   // GeoTIFF

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Polyconic"),
            new NamedIdentifier(Citations.EPSG,    "American Polyconic"),
            new IdentifierCode (Citations.EPSG,     9818), // The ellipsoidal case
            new NamedIdentifier(Citations.GEOTIFF, "CT_Polyconic"),
            new IdentifierCode (Citations.GEOTIFF,  22)
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            LATITUDE_OF_ORIGIN, CENTRAL_MERIDIAN,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public Polyconic() {
        super(PARAMETERS);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<ConicProjection> getOperationType() {
        return ConicProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.Polyconic.create(getParameters(), values);
    }
}
