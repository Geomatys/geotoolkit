/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import net.jcip.annotations.Immutable;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.internal.referencing.DeprecatedName;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Krovak Oblique Conic Conformal</cite>" projection (EPSG:9819).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/krovak.html">Krovak on RemoteSensing.org</A>.
 * The math transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.Krovak}</li>
 * </ul>
 *
 * @author Jan Jezek (HSRS)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class Krovak extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -278392856661204734L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is
     * 24&deg;50' (which is 42&deg;30' from Ferro prime meridian).
     */
    public static final ParameterDescriptor<Double> LONGITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 49.5&deg;.
     */
    public static final ParameterDescriptor<Double> LATITUDE_OF_CENTRE;

    /**
     * The operation parameter descriptor for the {@code azimuth} parameter value. This has
     * been renamed "<cite>Co-latitude of cone axis</cite>" in latest EPSG database versions.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 30.28813972222&deg;.
     */
    public static final ParameterDescriptor<Double> AZIMUTH;

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 78.5&deg;.
     */
    public static final ParameterDescriptor<Double> PSEUDO_STANDARD_PARALLEL;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 0.9999.
     */
    public static final ParameterDescriptor<Double> SCALE_FACTOR;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     */
    public static final ParameterDescriptor<Double> FALSE_NORTHING;

    /**
     * Parameters creation, which must be done before to initialize the {@link #PARAMETERS} field.
     */
    static {
        final Citation[] excludes = new Citation[] {Citations.NETCDF};
        LONGITUDE_OF_CENTRE = Identifiers.CENTRAL_MERIDIAN.select(true, 42.5 - 17.66666666666667, excludes,
                "Longitude of origin",           // EPSG
                "longitude_of_center",           // OGC
                "Longitude_Of_Center",           // ESRI
                "CenterLong");                   // GeoTIFF
        LATITUDE_OF_CENTRE = Identifiers.LATITUDE_OF_ORIGIN.select(true, 49.5, excludes,
                "Latitude of projection centre", // EPSG
                "latitude_of_center",            // OGC
                "Latitude_Of_Center",            // ESRI
                "CenterLat");                    // GeoTIFF
        AZIMUTH = Identifiers.AZIMUTH.select(true, 30.28813972222222, excludes,
                "Co-latitude of cone axis",      // EPSG
                "azimuth",                       // OGC
                "AzimuthAngle");                 // GeoTIFF
        PSEUDO_STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(true, 78.5, excludes,
                "Latitude of pseudo standard parallel",     // EPSG
                "pseudo_standard_parallel_1",               // OGC
                "Pseudo_Standard_Parallel_1");              // ESRI
        SCALE_FACTOR = Identifiers.SCALE_FACTOR.select(true, 0.9999, excludes,
                "Scale factor on pseudo standard parallel", // EPSG
                "ScaleAtCenter");                           // GeoTIFF
        // Following are the same than Mercator1SP except for the exclusion list.
        FALSE_EASTING  = EquidistantCylindrical.FALSE_EASTING;
        FALSE_NORTHING = EquidistantCylindrical.FALSE_NORTHING;
    }

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,        "Krovak"),
            new NamedIdentifier(Citations.EPSG,       "Krovak"), // Starting from EPSG version 7.6
            new DeprecatedName (Citations.EPSG,       "Krovak Oblique Conic Conformal"), // Legacy
            new IdentifierCode (Citations.EPSG,        9819),
            new NamedIdentifier(Citations.ESRI,       "Krovak"),
            new NamedIdentifier(Citations.GEOTIFF,    "Krovak"),
            new NamedIdentifier(Citations.PROJ4,      "krovak"),
            new NamedIdentifier(Citations.GEOTOOLKIT, "Krovak Oblique Conformal Conic"),
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            LATITUDE_OF_CENTRE, LONGITUDE_OF_CENTRE,
            AZIMUTH, PSEUDO_STANDARD_PARALLEL, SCALE_FACTOR,
            X_SCALE, Y_SCALE, XY_PLANE_ROTATION,
            FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public Krovak() {
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
        return org.geotoolkit.referencing.operation.projection.Krovak.create(getParameters(), values);
    }
}
