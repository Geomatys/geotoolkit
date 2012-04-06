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

import java.util.List;
import net.jcip.annotations.Immutable;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CylindricalProjection;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;


/**
 * The provider for "<cite>Transverse Mercator</cite>" projection (EPSG:9807).
 * The programmatic names and parameters are enumerated at
 * <A HREF="http://www.remotesensing.org/geotiff/proj_list/transverse_mercator.html">Transverse
 * Mercator on RemoteSensing.org</A>. The math transform implementations instantiated by this
 * provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.projection.TransverseMercator}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.20
 *
 * @since 2.1
 * @module
 */
@Immutable
public class TransverseMercator extends MapProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3386587506686432398L;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-180 &hellip; 180]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> CENTRAL_MERIDIAN =
            Identifiers.CENTRAL_MERIDIAN.select(null,
                "Longitude of natural origin",    // EPSG
                "central_meridian",               // OGC
                "Central_Meridian",               // ESRI
                "longitude_of_central_meridian",  // NetCDF
                "NatOriginLong");                 // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is [-90 &hellip; 90]&deg; and default value is 0&deg;.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> LATITUDE_OF_ORIGIN = Mercator2SP.LATITUDE_OF_ORIGIN;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is (0 &hellip; &infin;) and default value is 1.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE_FACTOR =
            Identifiers.SCALE_FACTOR.select(null,
                "Scale factor at natural origin",   // EPSG
                "scale_factor_at_central_meridian", // NetCDF
                "ScaleAtNatOrigin");                // GeoTIFF

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_EASTING = Mercator2SP.FALSE_EASTING;

    /**
     * The operation parameter descriptor for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter value.
     *
     * This parameter is <a href="package-summary.html#Obligation">mandatory</a>.
     * Valid values range is unrestricted and default value is 0 metre.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> FALSE_NORTHING = Mercator2SP.FALSE_NORTHING;

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.OGC,      "Transverse_Mercator"),
            new NamedIdentifier(Citations.EPSG,     "Transverse Mercator"),
            new NamedIdentifier(Citations.EPSG,     "Gauss-Kruger"),
            new NamedIdentifier(Citations.EPSG,     "Gauss-Boaga"),
            new NamedIdentifier(Citations.EPSG,     "TM"),
            new IdentifierCode (Citations.EPSG,      9807),
            new NamedIdentifier(Citations.ESRI,     "Transverse_Mercator"),
            new NamedIdentifier(Citations.ESRI,     "Gauss_Kruger"),
            new NamedIdentifier(Citations.NETCDF,   "TransverseMercator"),
            new NamedIdentifier(Citations.GEOTIFF,  "CT_TransverseMercator"),
            new IdentifierCode (Citations.GEOTIFF,   1),
            new NamedIdentifier(Citations.PROJ4,    "tmerc"),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.TRANSVERSE_MERCATOR_PROJECTION)),
        }, null, new ParameterDescriptor<?>[] {
            SEMI_MAJOR, SEMI_MINOR, ROLL_LONGITUDE,
            CENTRAL_MERIDIAN, LATITUDE_OF_ORIGIN,
            SCALE_FACTOR, FALSE_EASTING, FALSE_NORTHING
        });

    /**
     * Constructs a new provider.
     */
    public TransverseMercator() {
        super(PARAMETERS);
    }

    /**
     * Constructs a new provider with the specified parameters.
     */
    TransverseMercator(final ParameterDescriptorGroup descriptor) {
        super(descriptor);
    }

    /**
     * Returns the operation type for this map projection.
     */
    @Override
    public Class<CylindricalProjection> getOperationType() {
        return CylindricalProjection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MathTransform2D createMathTransform(ParameterValueGroup values) {
        return org.geotoolkit.referencing.operation.projection.TransverseMercator.create(getParameters(), values);
    }




    /**
     * The provider for <cite>Mercator Transverse (South Orientated)</cite> projection
     * (EPSG:9808). The coordinate axes are called <cite>Westings</cite> and <cite>Southings</cite>
     * and increment to the West and South from the origin respectively.
     * <p>
     * The terms <cite>false easting</cite> (FE) and <cite>false northing</cite> (FN) increase
     * the Westing and Southing value at the natural origin. In other words they are effectively
     * <cite>false westing</cite> (FW) and <cite>false southing</cite> (FS) respectively.
     *
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @version 3.20
     *
     * @see org.geotoolkit.referencing.operation.projection.TransverseMercator
     *
     * @since 2.2
     * @module
     */
    @Immutable
    public static class SouthOrientated extends TransverseMercator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -5938929136350638347L;

        /**
         * The parameters group.
         */
        @SuppressWarnings("hiding")
        public static final ParameterDescriptorGroup PARAMETERS;
        static {
            final Citation[] excludes = {
                Citations.ESRI, Citations.NETCDF, Citations.GEOTIFF, Citations.PROJ4
            };
            final List<GeneralParameterDescriptor> param = TransverseMercator.PARAMETERS.descriptors();
            PARAMETERS = Identifiers.createDescriptorGroup(
                new ReferenceIdentifier[] {
                    new NamedIdentifier(Citations.EPSG, "Transverse Mercator (South Orientated)"),
                    new IdentifierCode (Citations.EPSG,  9808),
                    sameNameAs(Citations.GEOTOOLKIT, TransverseMercator.PARAMETERS)
            }, excludes, param.toArray(new ParameterDescriptor<?>[param.size()]));
        }

        /**
         * Constructs a new provider.
         */
        public SouthOrientated() {
            super(PARAMETERS);
        }
    }
}
