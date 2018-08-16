/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import javax.swing.ComboBoxModel;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.TransferFunction;

import org.geotoolkit.coverage.Category;
import org.geotoolkit.image.palette.PaletteFactory;


/**
 * Connection to a table of categories.
 * This table creates a list of {@link Category} objects for a given sample dimension.
 * Categories are one of the components required for creating a {@code GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class CategoryTable extends Table {
    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "Categories";

    /**
     * A transparent color for missing data.
     */
    private static final Color[] TRANSPARENT = new Color[] {
        new Color(0,0,0,0)
    };

    /**
     * The choices of available palette names. Build only when first needed and cached
     * in order to avoid reloading the colors from the files for every images inserted
     * in the database.
     */
    private transient ComboBoxModel<ColorPalette> paletteChoices;

    /**
     * Creates a category table.
     */
    CategoryTable(final Transaction transaction) {
        super(transaction);
    }

    /**
     * Returns the list of categories for the given format.
     *
     * @param  format  the name of the format for which the categories are defined.
     * @return the categories for each sample dimension in the given format.
     * @throws SQLException if an error occurred while reading the database.
     */
    public SampleDimensionTable.Entry query(final String format) throws SQLException, CatalogException {
        String paletteName = null;
        int paletteRange = 0;
        final List<Category> categories = new ArrayList<>();
        final Map<Integer,Category[]> dimensions = new HashMap<>();
        int bandOfPreviousCategory = Integer.MIN_VALUE;
        final PreparedStatement statement = prepareStatement("SELECT "
                + "\"band\", \"name\", \"lower\", \"upper\", \"scale\", \"offset\", \"function\", \"colors\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"format\" = ? ORDER BY \"band\"");
        statement.setString(1, format);
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                boolean isQuantifiable = true;
                final int        band = results.getInt   (1);
                final String     name = results.getString(2);
                final int       lower = results.getInt   (3);
                final int       upper = results.getInt   (4);
                final double    scale = results.getDouble(5); isQuantifiable &= !results.wasNull();
                final double   offset = results.getDouble(6); isQuantifiable &= !results.wasNull();
                final String function = results.getString(7);
                final String  colorID = results.getString(8);
                /*
                 * Decode the "colors" value. This string is either the RGB numeric code starting
                 * with '#" (as in "#D2C8A0"), or the name of a color palette (as "rainbow").
                 */
                Color[] colors = null;
                if (colorID != null) {
                    final String id = colorID.trim();
                    if (!id.isEmpty()) try {
                        if (colorID.charAt(0) == '#') {
                            colors = new Color[] {Color.decode(id)};
                         } else {
                            colors = transaction.database.paletteFactory.getColors(colorID);
                            final int range = upper - lower;
                            if (paletteName == null || range > paletteRange) {
                                paletteName = colorID;
                                paletteRange = range;
                            }
                         }
                    } catch (IOException | NumberFormatException exception) {
                        throw new IllegalRecordException(exception, results, 8, name);
                    }
                }
                /*
                 * Creates a category for the current record. A category can be 1) qualitive,
                 * 2) quantitative and linear, or 3) quantitative and logarithmic.
                 */
                final NumberRange<?> range = NumberRange.create(lower, true, upper, true);
                MathTransform1D tr = null;
                if (!isQuantifiable) {
                    // Qualitative category.
                    if (colors == null) {
                        colors = TRANSPARENT;
                    }
                } else {
                    // Quantitative category.
                    TransferFunctionType type = org.apache.sis.util.iso.Types.forCodeName(TransferFunctionType.class, function, false);
                    if (type == null) type = TransferFunctionType.LINEAR;
                    final TransferFunction trf = new TransferFunction();
                    trf.setScale(scale);
                    trf.setOffset(offset);
                    trf.setType(type);
                    try {
                        tr = (MathTransform1D) trf.createTransform(transaction.database.mtFactory);
                    } catch (FactoryException | ClassCastException e) {
                        throw new CatalogException(e);
                    }
                }
                final Category category = new Category(name, colors, range, tr);
                /*
                 * Add the new category to the list. If we are beginning a new band,
                 * stores the previous categories in the 'dimensions' map.
                 */
                if (band != bandOfPreviousCategory) {
                    store(dimensions, bandOfPreviousCategory, categories);
                    bandOfPreviousCategory = band;
                }
                categories.add(category);
            }
        }
        store(dimensions, bandOfPreviousCategory, categories);
        return new SampleDimensionTable.Entry(dimensions, paletteName);
    }

    /**
     * Puts the categories from the given list in the given map.
     */
    private static void store(final Map<Integer,Category[]> dimensions, final int band, final List<Category> categories) {
        final int size = categories.size();
        if (size != 0) {
            if (dimensions.put(band, categories.toArray(new Category[size])) != null) {
                throw new AssertionError(band);         // Should never happen (TODO: replace by InternalDataStoreException).
            }
            categories.clear();
        }
    }

    /**
     * Adds the given categories in the database.
     *
     * @param  format      the newly created format for which to write the sample dimensions.
     * @param  categories  the categories to add for each band.
     * @throws SQLException if an error occurred while writing to the database.
     */
    public void insert(final String format, final List<List<Category>> categories) throws SQLException, IllegalUpdateException {
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"format\", \"band\", \"name\", \"lower\", \"upper\", \"scale\", \"offset\", \"function\", \"colors\")"
                + " VALUES (?,?,?,?,?,?,?,?,?)");
        statement.setString(1, format);
        int bandNumber = 0;
        for (final List<Category> list : categories) {
            statement.setInt(2, ++bandNumber);
            for (Category category : list) {
                category = category.geophysics(false);
                final org.geotoolkit.coverage.sql.TransferFunction tf =
                        new org.geotoolkit.coverage.sql.TransferFunction(category, transaction.database.locale);
                if (tf.warning != null) {
                    throw new IllegalUpdateException(tf.warning);
                }
                statement.setString(3, String.valueOf(category.getName()));
                statement.setInt(4, tf.minimum);
                statement.setInt(5, tf.maximum);
                if (tf.isQuantitative) {
                    statement.setDouble(6, tf.getScale());
                    statement.setDouble(7, tf.getOffset());
                    if (tf.getType() != null) {
                        statement.setString(8, org.apache.sis.util.iso.Types.getCodeName(tf.getType()));
                    } else {
                        statement.setNull(8, Types.VARCHAR);
                    }
                } else {
                    statement.setNull(7, Types.DOUBLE);
                    statement.setNull(8, Types.DOUBLE);
                    statement.setNull(8, Types.VARCHAR);
                }
                final String paletteName = getPaletteName(category.getColors());
                if (paletteName != null) {
                    statement.setString(9, paletteName);
                } else {
                    statement.setNull(9, Types.VARCHAR);
                }
                final int count = statement.executeUpdate();
                if (count != 1) {
                    throw new IllegalUpdateException(transaction.database.locale, count);
                }
            }
        }
    }

    /**
     * Returns the name of the color palette for the given colors, or {@code null} if none.
     * This method is invoked only during the insertion of new entries.
     */
    private String getPaletteName(final Color... colors) {
        if (colors != null && colors.length != 0) {
            final PaletteFactory paletteFactory = transaction.database.paletteFactory;
            if (paletteChoices == null) {
                paletteChoices = ColorPalette.getChoices(paletteFactory);
            }
            return ColorPalette.findName(colors, paletteChoices, paletteFactory);
        }
        return null;
    }
}
