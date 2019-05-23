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
import java.util.Locale;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.util.InternationalString;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.crs.DefaultImageCRS;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.crs.DefaultEngineeringCRS;
import org.apache.sis.referencing.datum.DefaultImageDatum;
import org.apache.sis.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.cs.Axes;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.resources.Vocabulary;

import static org.opengis.referencing.IdentifiedObject.ALIAS_KEY;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Predefined CRS constants.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
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
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y}
     * axes in {@linkplain Units#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_2D;

    /**
     * A three-dimensional Cartesian coordinate reference system with
     * {@linkplain Axes#X x},
     * {@linkplain Axes#Y y},
     * {@linkplain Axes#Z z}
     * axes in {@linkplain Units#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_3D;
    static {
        final DefaultEngineeringDatum datum = new DefaultEngineeringDatum(name(Vocabulary.Keys.Unknown));
        CARTESIAN_2D = new DefaultEngineeringCRS(name(Vocabulary.Keys.Cartesian2d), datum, PredefinedCS.CARTESIAN_2D);
        CARTESIAN_3D = new DefaultEngineeringCRS(name(Vocabulary.Keys.Cartesian3d), datum, PredefinedCS.CARTESIAN_3D);
    }

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
     */
    public static final DefaultImageCRS GRID_2D;
    static {
        final Map<String,?> properties = name(Vocabulary.Keys.Grid);
        GRID_2D = new DefaultImageCRS(properties, new DefaultImageDatum(properties, PixelInCell.CELL_CENTER), PredefinedCS.GRID);
    }
}
