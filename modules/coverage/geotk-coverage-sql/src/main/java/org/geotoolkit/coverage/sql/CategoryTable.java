/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.util.Locale;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import javax.swing.ComboBoxModel;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.util.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.internal.coverage.TransferFunction;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.CodeLists;
import org.geotoolkit.internal.coverage.ColorPalette;
import org.geotoolkit.resources.Errors;

import org.geotoolkit.internal.sql.table.Table;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.geotoolkit.internal.sql.table.IllegalUpdateException;
import org.geotoolkit.internal.sql.table.SpatialDatabase;


/**
 * Connection to a table of {@linkplain Category categories}. This table creates a list of
 * {@link Category} objects for a given sample dimension. Categories are one of the components
 * required for creating a {@link org.geotoolkit.coverage.grid.GridCoverage2D}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class CategoryTable extends Table {
    /**
     * Maximum number of bands allowed in an image. This is an arbitrary number used
     * only in order to catch bad records before we create too many objects in memory.
     */
    private static final int MAXIMUM_BANDS = 1000;

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
     *
     * @param database Connection to the database.
     */
    public CategoryTable(final Database database) {
        super(new CategoryQuery(database));
    }

    /**
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private CategoryTable(final CategoryTable table) {
        super(table);
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected CategoryTable clone() {
        return new CategoryTable(this);
    }

    /**
     * Returns the list of categories for the given format.
     *
     * @param  format The name of the format for which the categories are defined.
     * @return The categories for each sample dimension in the given format.
     * @throws SQLException if an error occurred while reading the database.
     */
    public CategoryEntry getCategories(final String format) throws SQLException {
        int paletteRange = 0;
        String paletteName = null;
        final CategoryQuery query = (CategoryQuery) this.query;
        final List<Category> categories = new ArrayList<>();
        final Map<Integer,Category[]> dimensions = new HashMap<>();
        MathTransformFactory mtFactory = null;  // Will be fetched only if needed.
        MathTransform      exponential = null;  // Will be fetched only if needed.
        int bandOfPreviousCategory = 0;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.LIST);
            final PreparedStatement statement = ce.statement;
            statement.setString(indexOf(query.byFormat), format);
            final int bandIndex     = indexOf(query.band    );
            final int nameIndex     = indexOf(query.name    );
            final int lowerIndex    = indexOf(query.lower   );
            final int upperIndex    = indexOf(query.upper   );
            final int c0Index       = indexOf(query.c0      );
            final int c1Index       = indexOf(query.c1      );
            final int functionIndex = indexOf(query.function);
            final int colorsIndex   = indexOf(query.colors  );
            try (ResultSet results = statement.executeQuery()) {
                PaletteFactory palettes = null;
                while (results.next()) {
                    boolean isQuantifiable = true;
                    final int        band = results.getInt   (bandIndex);
                    final String     name = results.getString(nameIndex);
                    final int       lower = results.getInt   (lowerIndex);
                    final int       upper = results.getInt   (upperIndex);
                    final double       c0 = results.getDouble(c0Index); isQuantifiable &= !results.wasNull();
                    final double       c1 = results.getDouble(c1Index); isQuantifiable &= !results.wasNull();
                    final String function = results.getString(functionIndex);
                    final String  colorID = results.getString(colorsIndex);
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
                                if (palettes == null) {
                                    palettes = ((TableFactory) getDatabase()).paletteFactory;
                                    palettes.setWarningLocale(getLocale());
                                }
                                colors = palettes.getColors(colorID);
                                final int range = upper - lower;
                                if (paletteName == null || range > paletteRange) {
                                    paletteName = colorID;
                                    paletteRange = range;
                                }
                             }
                        } catch (IOException | NumberFormatException exception) {
                            throw new IllegalRecordException(exception, this, results, colorsIndex, name);
                        }
                    }
                    /*
                     * Creates a category for the current record. A category can be 1) qualitive,
                     * 2) quantitative and linear, or 3) quantitative and logarithmic.
                     */
                    Category category;
                    final NumberRange<?> range = NumberRange.create(lower, upper);
                    if (!isQuantifiable) {
                        // Qualitative category.
                        if (colors == null) {
                            colors = TRANSPARENT;
                        }
                        category = new Category(name, colors, range, (MathTransform1D) null);
                    } else {
                        // Quantitative category.
                        if (mtFactory == null) {
                            mtFactory = ((SpatialDatabase) getDatabase()).getMathTransformFactory();
                        }
                        MathTransform tr;
                        try {
                            tr = mtFactory.createAffineTransform(new Matrix2(c1, c0, 0, 1));
                            /*
                             * Check for transfer function:
                             *
                             *   - NULL is considered synonymous to "linear".
                             *
                             *   - "log" (not to be confused with "logarithmic") is handled as
                             *     "exponential" for compatibility with legacy databases. It was
                             *      interpreted as log(y)=C0+C1*x.
                             *
                             *   - "logarithmic" is not yet implemented, because we don't know yet
                             *     if the log should be computed before or after the offset and scale
                             *     factor.
                             *
                             * NOTE: The formulas used below must be consistent with the formulas in
                             *       MetadataHelper.getGridSampleDimensions(List<SampleDimension>).
                             */
                            if (function != null && !function.equalsIgnoreCase("linear")) {
                                if (function.equalsIgnoreCase("exponential") || function.equalsIgnoreCase("log")) {
                                    // Quantitative and logarithmic category.
                                    if (exponential == null) {
                                        final ParameterValueGroup param = mtFactory.getDefaultParameters("Exponential");
                                        param.parameter("base").setValue(10d); // Must be a 'double'
                                        exponential = mtFactory.createParameterizedTransform(param);
                                    }
                                    tr = mtFactory.createConcatenatedTransform(tr, exponential);
                                } else {
                                    throw new IllegalRecordException(errors().getString(
                                            Errors.Keys.UNSUPPORTED_OPERATION_$1, function),
                                            this, results, functionIndex, name);
                                }
                            }
                        } catch (FactoryException exception) {
                            throw new CatalogException(exception);
                        }
                        try {
                            category = new Category(name, colors, range, (MathTransform1D) tr);
                        } catch (ClassCastException exception) { // If 'tr' is not a MathTransform1D.
                            throw new IllegalRecordException(exception, this, results, functionIndex, format);
                            // 'results' is closed by the above constructor.
                        }
                    }
                    /*
                     * Add to the new category to the lists. Note that the test below for the
                     * maximum band count is arbitrary and exists only for spotting bad records.
                     */
                    final int minBand = Math.max(1, bandOfPreviousCategory);
                    if (band < minBand || band > MAXIMUM_BANDS) {
                        throw new IllegalRecordException(errors().getString(Errors.Keys.VALUE_OUT_OF_BOUNDS_$3,
                                band, minBand, MAXIMUM_BANDS), this, results, bandIndex, name);
                    }
                    // If we are beginning a new band, stores the previous
                    // categories in the 'dimensions' map.
                    if (band != bandOfPreviousCategory) {
                        if (!categories.isEmpty()) {
                            store(dimensions, bandOfPreviousCategory, categories);
                            categories.clear();
                        }
                        bandOfPreviousCategory = band;
                    }
                    categories.add(category);
                }
            }
            release(lc, ce);
        }
        if (!categories.isEmpty()) {
            store(dimensions, bandOfPreviousCategory, categories);
        }
        return new CategoryEntry(dimensions, paletteName);
    }

    /**
     * Puts the categories from the given list in the given map.
     */
    private static void store(final Map<Integer,Category[]> dimensions, final int band,
            final List<Category> categories)
    {
        if (dimensions.put(band, categories.toArray(new Category[categories.size()])) != null) {
            throw new AssertionError(band); // Should never happen.
        }
    }

    /**
     * Adds the given categories in the database.
     *
     * @param  format The newly created format for which to write the sample dimensions.
     * @param  categories The categories to add for each band.
     * @throws SQLException if an error occurred while writing to the database.
     *
     * @since 3.13
     */
    public void addEntries(final String format, final List<List<Category>> categories) throws SQLException {
        final CategoryQuery query = (CategoryQuery) this.query;
        final Locale locale = getLocale();
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            boolean success = false;
            transactionBegin(lc);
            try {
                final LocalCache.Stmt ce = getStatement(lc, QueryType.INSERT);
                final PreparedStatement statement = ce.statement;
                statement.setString(indexOf(query.format), format);
                final int bandIndex     = indexOf(query.band    );
                final int nameIndex     = indexOf(query.name    );
                final int lowerIndex    = indexOf(query.lower   );
                final int upperIndex    = indexOf(query.upper   );
                final int c0Index       = indexOf(query.c0      );
                final int c1Index       = indexOf(query.c1      );
                final int functionIndex = indexOf(query.function);
                final int colorsIndex   = indexOf(query.colors  );
                int bandNumber = 0;
                for (final List<Category> list : categories) {
                    statement.setInt(bandIndex, ++bandNumber);
                    for (Category category : list) {
                        category = category.geophysics(false);
                        final TransferFunction tf = new TransferFunction(category, locale);
                        if (tf.warning != null) {
                            throw new IllegalUpdateException(tf.warning);
                        }
                        statement.setString(nameIndex, String.valueOf(category.getName()));
                        statement.setInt(lowerIndex, tf.minimum);
                        statement.setInt(upperIndex, tf.maximum);
                        if (tf.isQuantitative) {
                            statement.setDouble(c0Index, tf.offset);
                            statement.setDouble(c1Index, tf.scale);
                            if (tf.type != null) {
                                statement.setString(functionIndex, CodeLists.identifier(tf.type));
                            } else {
                                statement.setNull(functionIndex, Types.VARCHAR);
                            }
                        } else {
                            statement.setNull(c0Index, Types.DOUBLE);
                            statement.setNull(c1Index, Types.DOUBLE);
                            statement.setNull(functionIndex, Types.VARCHAR);
                        }
                        final String paletteName = getPaletteName(category.getColors());
                        if (paletteName != null) {
                            statement.setString(colorsIndex, paletteName);
                        } else {
                            statement.setNull(colorsIndex, Types.VARCHAR);
                        }
                        final int count = statement.executeUpdate();
                        if (count != 1) {
                            throw new IllegalUpdateException(locale, count);
                        }
                    }
                }
                release(lc, ce);
                success = true;
            } finally {
                transactionEnd(lc, success);
            }
        }
    }

    /**
     * Returns the name of the color palette for the given colors, or {@code null} if none.
     * This method is invoked only during the insertion of new entries.
     */
    private String getPaletteName(final Color... colors) {
        if (colors != null && colors.length != 0) {
            final PaletteFactory paletteFactory = ((TableFactory) getDatabase()).paletteFactory;
            if (paletteChoices == null) {
                paletteChoices = ColorPalette.getChoices(paletteFactory);
            }
            return ColorPalette.findName(colors, paletteChoices, paletteFactory);
        }
        return null;
    }
}
