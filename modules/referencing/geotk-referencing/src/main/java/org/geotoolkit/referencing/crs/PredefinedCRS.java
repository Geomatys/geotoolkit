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
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.datum.DefaultImageDatum;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.ComparisonMode;

import static org.opengis.referencing.IdentifiedObject.ALIAS_KEY;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


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
    static Map<String,Object> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString(Locale.ROOT));
        properties.put(ALIAS_KEY, name);
        return properties;
    }

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
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_2D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_2D, DefaultCartesianCS.GENERIC_2D);

    /**
     * A three-dimensional Cartesian coordinate reference system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y},
     * {@linkplain DefaultCoordinateSystemAxis#Z z}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_3D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_3D, DefaultCartesianCS.GENERIC_3D);

    /**
     * A two-dimensional wildcard coordinate system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_2D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_2D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_2D, DefaultCartesianCS.GENERIC_2D);

    /**
     * A three-dimensional wildcard coordinate system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y},
     * {@linkplain DefaultCoordinateSystemAxis#Z z}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_3D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_3D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_3D, DefaultCartesianCS.GENERIC_3D);

    /**
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis#COLUMN column},
     * {@linkplain org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis#ROW row} axes.
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
                PixelInCell.CELL_CENTER), DefaultCartesianCS.GRID);
    }
}
