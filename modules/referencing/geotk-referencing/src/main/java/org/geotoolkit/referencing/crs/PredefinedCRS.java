/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.crs;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Locale;
import javax.measure.unit.SI;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.InternationalString;
import org.apache.sis.referencing.crs.DefaultImageCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.crs.DefaultGeocentricCRS;
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.datum.DefaultImageDatum;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.cs.Axes;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ComparisonMode;

import static org.opengis.referencing.IdentifiedObject.ALIAS_KEY;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static org.opengis.referencing.ReferenceSystem.DOMAIN_OF_VALIDITY_KEY;


/**
 * Predefined CRS constants. <strong>This class is temporary</strong> - its content may
 * move to Apache SIS in future version.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @module
 */
public final class PredefinedCRS {
    /**
     * Do not allow instantiation of this class.
     */
    private PredefinedCRS() {
    }

    /**
     * Use the unlocalized name (usually in English locale), because the name is part of the elements
     * compared by the {@link #equals} method.
     */
    private static Map<String,Object> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString(Locale.ROOT));
        properties.put(ALIAS_KEY, name);
        return properties;
    }

    /**
     * A three-dimensional geographic coordinate reference system using the WGS84 datum.
     * This CRS uses (<var>longitude</var>, <var>latitude</var>, <var>height</var>)
     * ordinates with longitude values increasing towards the East, latitude values
     * increasing towards the North and height positive above the ellipsoid. The angular
     * units are decimal degrees, the height unit is the metre, and the prime meridian
     * is Greenwich.
     * <p>
     * This CRS is equivalent to {@code EPSG:4979} (the successor to
     * {@code EPSG:4329}, itself the successor to {@code EPSG:4327}) except for
     * axis order, since EPSG puts latitude before longitude.
     *
     * @see DefaultGeodeticDatum#WGS84
     */
    public static final DefaultGeographicCRS WGS84_3D;
    static {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY, "WGS84(DD)"); // Name used in WCS 1.0.
        final String[] alias = {
            "WGS84",
            "WGS 84",                // EPSG:4979 name.
            "WGS 84 (3D)",           // EPSG:4329 name.
            "WGS 84 (geographic 3D)" // EPSG:4327 name.
        };
        properties.put(ALIAS_KEY, alias);
        properties.put(DOMAIN_OF_VALIDITY_KEY, Extents.WORLD);
        // Do not declare EPSG identifiers, because axis order are not the same.
        alias[1] = "WGS 84 (geographic 3D)"; // Replaces the EPSG name.
        WGS84_3D = new DefaultGeographicCRS(properties, CommonCRS.WGS84.datum(),
                                            PredefinedCS.GEODETIC_3D);
    }

    /**
     * The default geocentric CRS with a
     * {@linkplain DefaultCartesianCS#GEOCENTRIC Cartesian coordinate system}.
     * Prime meridian is Greenwich, geodetic datum is WGS84 and linear units are metres.
     * The <var>X</var> axis points towards the prime meridian.
     * The <var>Y</var> axis points East.
     * The <var>Z</var> axis points North.
     */
    public static final DefaultGeocentricCRS GEOCENTRIC = new DefaultGeocentricCRS(
                        name(Vocabulary.Keys.CARTESIAN),
                        CommonCRS.WGS84.datum(), PredefinedCS.GEOCENTRIC);

    /**
     * The default geocentric CRS with a
     * {@linkplain DefaultSphericalCS#GEOCENTRIC spherical coordinate system}.
     * Prime meridian is Greenwich, geodetic datum is WGS84 and linear units are metres.
     */
    public static final DefaultGeocentricCRS SPHERICAL = new DefaultGeocentricCRS(
                        name(Vocabulary.Keys.SPHERICAL),
                        CommonCRS.WGS84.datum(), PredefinedCS.SPHERICAL);



    /**
     * A Cartesian local coordinate system.
     */
    private static final class Cartesian extends DefaultEngineeringCRS {
        /** Serial number for inter-operability with different versions. */
        private static final long serialVersionUID = -1773381554353809683L;

        /**
         * An engineering datum for unknown coordinate reference system. Such CRS are usually
         * assumed Cartesian, but will not have any transformation path to other CRS.
         */
        public static final DefaultEngineeringDatum UNKNOWN =
                new DefaultEngineeringDatum(name(Vocabulary.Keys.UNKNOWN));

        /** Constructs a coordinate system with the given name. */
        public Cartesian(final int key, final CoordinateSystem cs) {
            super(name(key), UNKNOWN, cs);
        }

        /**
         * Compares the specified object to this CRS for equality. This method is overridden
         * because, otherwise, {@code CARTESIAN_xD} and {@code GENERIC_xD} would be considered
         * equals when metadata are ignored.
         */
        @Override
        public boolean equals(final Object object, final ComparisonMode mode) {
            if (object instanceof EngineeringCRS && super.equals(object, mode)) {
                switch (mode) {
                    case STRICT:
                    case BY_CONTRACT: {
                        // No need to performs the check below if metadata were already compared.
                        return true;
                    }
                    default: {
                        final EngineeringCRS that = (EngineeringCRS) object;
                        return Objects.equals(getName().getCode(), that.getName().getCode());
                    }
                }
            }
            return false;
        }
    }

    /**
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_2D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_2D, PredefinedCS.CARTESIAN_2D);

    /**
     * A three-dimensional Cartesian coordinate reference system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y},
     * {@linkplain Axes#Z z}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_3D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_3D, PredefinedCS.CARTESIAN_3D);

    /**
     * A two-dimensional wildcard coordinate system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_2D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_2D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_2D, PredefinedCS.CARTESIAN_2D);

    /**
     * A three-dimensional wildcard coordinate system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y},
     * {@linkplain Axes#Z z}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_3D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_3D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_3D, PredefinedCS.CARTESIAN_3D);

    /**
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain org.geotoolkit.referencing.cs.Axes#COLUMN column},
     * {@linkplain org.geotoolkit.referencing.cs.Axes#ROW row} axes.
     * By default, this CRS has no transformation path to any other CRS (i.e. a map using this
     * CS can't be reprojected to a {@linkplain DefaultGeographicCRS geographic coordinate
     * reference system} for example).
     * <p>
     * The {@link PixelInCell} attribute of the associated {@link ImageDatum}
     * is set to {@link PixelInCell#CELL_CENTER CELL_CENTER}.
     *
     * @since 3.09
     */
    public static final DefaultImageCRS GRID_2D;
    static {
        final Map<String,?> properties = name(Vocabulary.Keys.GRID);
        GRID_2D = new DefaultImageCRS(properties, new DefaultImageDatum(properties,
                PixelInCell.CELL_CENTER), PredefinedCS.GRID);
    }
}
