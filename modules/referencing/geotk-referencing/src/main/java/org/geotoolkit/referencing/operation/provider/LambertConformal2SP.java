/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.ConicProjection;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.projection.LambertConformal;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Lambert Conic Conformal (2SP)</cite>" projection (EPSG:9802).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp.html">Lambert
 * Conic Conformal 2SP on RemoteSensing.org</A>. The math transform implementations instantiated by
 * this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @since 2.2
 * @module
 */
public class LambertConformal2SP extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3240860802816724947L;

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
                "central_meridian",           // OGC
                "Central_Meridian",           // ESRI
                "Longitude of false origin",  // EPSG
                "FalseOriginLong");           // GeoTIFF

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
                "latitude_of_origin",        // OGC
                "Latitude_Of_Origin",        // ESRI
                "Latitude of false origin",  // EPSG
                "FalseOriginLat");           // GeoTIFF

    /**
     * The operation parameter descriptor for the first {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #LATITUDE_OF_ORIGIN}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     */
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_1 =
            Identifiers.STANDARD_PARALLEL_1.select(
                "standard_parallel_1",                  // OGC
                "Standard_Parallel_1",                  // ESRI
                "Latitude of 1st standard parallel");   // EPSG

    /**
     * The operation parameter descriptor for the second {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#standardParallels
     * standard parallel} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">optional</a> - if omitted,
     * it takes the same value than the one given for {@link #STANDARD_PARALLEL_1}.
     * Valid values range is [-90 &hellip; 90]&deg;.
     */
    public static final ParameterDescriptor<Double> STANDARD_PARALLEL_2 =
            Identifiers.STANDARD_PARALLEL_2.select("Latitude of 2nd standard parallel");

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
                "Easting at false origin",  // EPSG
                "FalseOriginEasting");      // GeoTIFF

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
                "Northing at false origin", // EPSG
                "FalseOriginNorthing");     // GeoTIFF

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,     "Lambert_Conformal_Conic_2SP"),
            new NamedIdentifier(Citations.EPSG,    "Lambert Conic Conformal (2SP)"),
            new IdentifierCode (Citations.EPSG,     9802),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic_2SP"),
            new NamedIdentifier(Citations.GEOTIFF, "CT_LambertConfConic"),
            new IdentifierCode (Citations.GEOTIFF,  9), // The same code is used for 1SP.
                     sameNameAs(Citations.GEOTOOLKIT, LambertConformal1SP.PARAMETERS)
        }, new ParameterDescriptor<?>[] {
            SEMI_MAJOR,          SEMI_MINOR,
            ROLL_LONGITUDE,
            CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
            STANDARD_PARALLEL_1, STANDARD_PARALLEL_2,
            FALSE_EASTING,       FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public LambertConformal2SP() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider with the given descriptor.
     */
    LambertConformal2SP(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
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
        return LambertConformal.create(getParameters(), values);
    }

    /**
     * The provider for "<cite>Lambert Conic Conformal (2SP Belgium)</cite>" projection (EPSG:9803).
     * The programmatic names and parameters are enumerated at
     * <A HREF="http://www.remotesensing.org/geotiff/proj_list/lambert_conic_conformal_2sp_belgium.html">Lambert
     * Conic Conformal 2SP (Belgium) on RemoteSensing.org</A>. The math transform implementations
     * instantiated by this provider may be any of the following classes:
     * <p>
     * <ul>
     *   <li>{@link org.geotoolkit.referencing.operation.projection.LambertConformal}</li>
     * </ul>
     *
     * @author Martin Desruisseaux (IRD)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.2
     * @module
     */
     public static class Belgium extends LambertConformal2SP {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -6388030784088639876L;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
            new ReferenceIdentifier[] {
                /*
                 * IMPORTANT: Do not put any name that could be confused with the 1SP or
                 * 2SP cases below, except for the Citations.GEOTOOLKIT authority which is
                 * ignored. The LambertConformal constructor relies on those names for
                 * distinguish the kind of projection being created.
                 */
                new NamedIdentifier(Citations.OGC,  "Lambert_Conformal_Conic_2SP_Belgium"),
                new NamedIdentifier(Citations.EPSG, "Lambert Conic Conformal (2SP Belgium)"),
                new IdentifierCode (Citations.EPSG,  9803),
                         sameNameAs(Citations.GEOTOOLKIT, LambertConformal2SP.PARAMETERS)
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR,          SEMI_MINOR,
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
                STANDARD_PARALLEL_1, STANDARD_PARALLEL_2,
                FALSE_EASTING,       FALSE_NORTHING
            });

        /**
         * Constructs a new provider.
         */
        public Belgium() {
            super(PARAMETERS);
        }
    }

    /**
     * The provider for "<cite>Lambert Conformal Conic</cite>" projection. This provider
     * accepts a scale factor in addition of the standard 2SP parameters.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @see LambertConformal
     *
     * @since 2.2
     * @module
     */
     public static class ESRI extends LambertConformal2SP {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -560511707695966609L;

        /**
         * The operation parameter descriptor for the {@linkplain
         * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
         * scale factor} parameter value.
         *
         * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
         * Valid values range is (0 &hellip; &infin;) and default value is 1.
         */
        public static final ParameterDescriptor<Double> SCALE_FACTOR = LambertConformal1SP.SCALE_FACTOR;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
            new ReferenceIdentifier[] {
                new NamedIdentifier(Citations.ESRI, "Lambert_Conformal_Conic"),
                         sameNameAs(Citations.GEOTOOLKIT, LambertConformal2SP.PARAMETERS)
            }, new ParameterDescriptor<?>[] {
                SEMI_MAJOR,          SEMI_MINOR,
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN,    LATITUDE_OF_ORIGIN,
                STANDARD_PARALLEL_1, STANDARD_PARALLEL_2, SCALE_FACTOR,
                FALSE_EASTING,       FALSE_NORTHING
            });

        /**
         * Constructs a new provider.
         */
        public ESRI() {
            super(PARAMETERS);
        }
    }
}
