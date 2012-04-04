/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Polar Stereographic (Variant A)</cite>" projection (EPSG:9810).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/polar_stereographic.html">Polar
 * Stereographic on RemoteSensing.org</A>. The math transform implementations instantiated
 * by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.PolarStereographic}</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class PolarStereographic extends Stereographic {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 9124091259039220308L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN;

    /**
     * The parameters group.
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final Citation[] excludes = {Citations.ESRI, Citations.NETCDF};
        CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                "Longitude of natural origin",  // EPSG
                "central_meridian",             // OGC
                "StraightVertPoleLong");        // GeoTIFF
        LATITUDE_OF_ORIGIN = Mercator1SP.LATITUDE_OF_ORIGIN;

        PARAMETERS = Identifiers.createDescriptorGroup(
            new ReferenceIdentifier[] {
                new NamedIdentifier(Citations.OGC,      "Polar_Stereographic"),
                new NamedIdentifier(Citations.EPSG,     "Polar Stereographic (variant A)"),
                new IdentifierCode (Citations.EPSG,      9810),
                new NamedIdentifier(Citations.GEOTIFF,  "CT_PolarStereographic"),
                new IdentifierCode (Citations.GEOTIFF,   15),
                sameNameAs(Citations.PROJ4,      Stereographic.PARAMETERS),
                sameNameAs(Citations.GEOTOOLKIT, Stereographic.PARAMETERS)
            }, excludes, new ParameterDescriptor<?>[] {
                (ParameterDescriptor<?>) Mercator1SP.PARAMETERS.descriptor("semi_major"),
                (ParameterDescriptor<?>) Mercator1SP.PARAMETERS.descriptor("semi_minor"),
                ROLL_LONGITUDE,
                CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN, SCALE_FACTOR,
                Mercator1SP.FALSE_EASTING,
                Mercator1SP.FALSE_NORTHING
            });
    }

    /**
     * Constructs a new provider.
     */
    public PolarStereographic() {
        super(PARAMETERS);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param parameters The set of parameters (never {@code null}).
     */
    PolarStereographic(final ParameterDescriptorGroup parameters) {
        super(parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.PolarStereographic.create(getParameters(), values);
    }




    /**
     * The provider for "<cite>Polar Stereographic (Variant B)</cite>" projection (EPSG:9829).
     * This provider includes a {@code "Standard_Parallel_1"} parameter and determines
     * the hemisphere of the projection from the {@code Standard_Parallel_1} value.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class VariantB extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 5188231050523249971L;

        /**
         * The operation parameter descriptor for the {@linkplain
         * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
         * central meridian} parameter value.
         *
         * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
         * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN;

        /**
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from [-90 &hellip; 90]&deg; and default value is 90&deg;N.
         */
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                // While I'm not sure that OGC parameter names are defined for this
                // projection, some WKT expect them. We could exclude the OGC param
                // if we had ESRI param (because there are often the same except for
                // the case), but EPSG parameter names are too different.
                Citations.ESRI, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            CENTRAL_MERIDIAN = Identifiers.CENTRAL_MERIDIAN.select(excludes,
                    "Longitude of origin",            // EPSG
                    "central_meridian");              // OGC
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, 90.0, excludes, null,
                    "Latitude of standard parallel",  // EPSG
                    "standard_parallel_1");           // OGC

            PARAMETERS = Identifiers.createDescriptorGroup(
                new ReferenceIdentifier[] {
                    new NamedIdentifier(Citations.EPSG, "Polar Stereographic (variant B)"),
                    new IdentifierCode (Citations.EPSG,  9829),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, excludes, new ParameterDescriptor<?>[] {
                    SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
                    CENTRAL_MERIDIAN, STANDARD_PARALLEL,
                    FALSE_EASTING, FALSE_NORTHING
                });
        }

        /**
         * Constructs a new provider.
         */
        public VariantB() {
            super(PARAMETERS);
        }
    }




    /**
     * The provider for "<cite>North Polar Stereographic</cite>" projection. This provider sets the
     * {@linkplain PolarStereographic#LATITUDE_OF_ORIGIN latitude of origin} parameter to 90&deg;N.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class North extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 657493908431273866L;

        /**
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from -90 to 90&deg;. The default value is 90&deg;N.
         */
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, 90.0, excludes, null,
                    "Standard_Parallel_1"); // ESRI

            PARAMETERS = Identifiers.createDescriptorGroup(
                new NamedIdentifier[] {
                    new NamedIdentifier(Citations.ESRI, "Stereographic_North_Pole"),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, excludes, new ParameterDescriptor<?>[] {
                    SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
                    Stereographic.CENTRAL_MERIDIAN, STANDARD_PARALLEL, SCALE_FACTOR,
                    FALSE_EASTING, FALSE_NORTHING
                });
        }

        /**
         * Constructs a new provider.
         */
        public North() {
            super(PARAMETERS);
        }
    }




    /**
     * The Provider for "<cite>South Polar Stereographic</cite>" projection. This provider sets the
     * {@linkplain PolarStereographic#LATITUDE_OF_ORIGIN latitude of origin} parameter to 90&deg;S.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 2.4
     * @module
     */
    @Immutable
    public static class South extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 6537800238416448564L;

        /**
         * The operation parameter descriptor for the {@code standardParallel} parameter value.
         * Valid values range is from -90 to 90&deg;. The default value is 90&deg;S.
         */
        public static final ParameterDescriptor<Double> STANDARD_PARALLEL;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.EPSG, Citations.OGC, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            STANDARD_PARALLEL = Identifiers.STANDARD_PARALLEL_1.select(false, -90.0, excludes, null,
                "Standard_Parallel_1"); // ESRI
            PARAMETERS = Identifiers.createDescriptorGroup(
                new NamedIdentifier[] {
                    new NamedIdentifier(Citations.ESRI, "Stereographic_South_Pole"),
                    sameNameAs(Citations.GEOTOOLKIT, PolarStereographic.PARAMETERS)
                }, excludes, new ParameterDescriptor<?>[] {
                    SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
                    Stereographic.CENTRAL_MERIDIAN, STANDARD_PARALLEL, SCALE_FACTOR,
                    FALSE_EASTING, FALSE_NORTHING
                });
        }

        /**
         * Constructs a new provider.
         */
        public South() {
            super(PARAMETERS);
        }
    }
}
