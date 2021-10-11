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

import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Types;
import javax.measure.Unit;

import org.opengis.util.NameFactory;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.TransferFunction;


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
     * Creates a category table.
     */
    CategoryTable(final Transaction transaction) {
        super(transaction);
    }

    /**
     * Returns the list of categories for the given format.
     *
     * @param  format       the name of the format for which the categories are defined.
     * @param  bands        the sample dimension names.
     * @param  units        the unit of measurement of quantitative categories for each sample dimension.
     * @param  isPacked     {@code true} if sample values are packed, or {@code false} if they are real values.
     * @param  backgrounds  the background sample values, or {@code null} if none.
     * @return the sample dimension for the given format.
     * @throws SQLException if an error occurred while reading the database.
     */
    final SampleDimension[] query(final String format, final String[] bands, final Unit<?>[] units, final boolean[] isPacked,
            final Integer[] backgrounds)  throws SQLException, FactoryException
    {
        final NameFactory factory = transaction.database.nameFactory;
        final List<SampleDimension> dimensions = new ArrayList<>();
        final class Builder extends SampleDimension.Builder {
            public void build(final int i) {
                final Integer bg = backgrounds[i];
                if (bg != null) setBackground(null, bg);
                dimensions.add(setName(factory.createLocalName(null, bands[i])).build().forConvertedValues(!isPacked[i]));
            }
        }
        final Builder categories = new Builder();
        int bandOfPreviousCategory = 0;
        final PreparedStatement statement = prepareStatement("SELECT "
                + "\"band\", \"name\", \"lower\", \"upper\", \"scale\", \"offset\", \"function\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"format\"=? ORDER BY \"band\"");
        statement.setString(1, format);
        try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                boolean isQuantifiable = true;
                final int        band = results.getInt   (1) - 1;   // Numbering starts at 1 in the database.
                final String     name = results.getString(2);
                final int       lower = results.getInt   (3);
                final int       upper = results.getInt   (4);
                final double    scale = results.getDouble(5); isQuantifiable &= !results.wasNull();
                final double   offset = results.getDouble(6); isQuantifiable &= !results.wasNull();
                final String function = results.getString(7);
                /*
                 * If we are beginning a new band, stores the previous categories in the 'dimensions' map.
                 */
                if (band != bandOfPreviousCategory) {
                    categories.build(bandOfPreviousCategory);
                    bandOfPreviousCategory = band;
                    categories.clear();
                }
                /*
                 * Creates a category for the current record. A category can be 1) qualitive,
                 * 2) quantitative and linear, or 3) quantitative and logarithmic.
                 */
                final NumberRange<?> range = NumberRange.create(lower, true, upper, true);
                if (isQuantifiable) {
                    // Quantitative category.
                    TransferFunctionType type = org.apache.sis.util.iso.Types.forCodeName(TransferFunctionType.class, function, false);
                    if (type == null) type = TransferFunctionType.LINEAR;
                    final TransferFunction trf = new TransferFunction();
                    trf.setScale(scale);
                    trf.setOffset(offset);
                    trf.setType(type);
                    final MathTransform tr = trf.createTransform(transaction.database.getMathTransformFactory());
                    categories.addQuantitative(name, range, (MathTransform1D) tr, units[band]);
                } else {
                    final Integer bg = backgrounds[band];
                    if (bg != null && lower == bg && upper == bg) {
                        categories.setBackground(name, bg);
                        backgrounds[band] = null;
                    } else {
                        categories.addQualitative(name, range);
                    }
                }
            }
        }
        categories.build(bandOfPreviousCategory);
        return dimensions.toArray(new SampleDimension[dimensions.size()]);
    }

    /**
     * Adds the given categories in the database.
     *
     * @param  format      the newly created format for which to write the sample dimensions.
     * @param  categories  the categories to add for each band.
     * @throws SQLException if an error occurred while writing to the database.
     */
    final void insert(final String format, final List<List<Category>> categories) throws SQLException, IllegalUpdateException {
        final PreparedStatement statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\" ("
                + "\"format\", \"band\", \"name\", \"lower\", \"upper\", \"scale\", \"offset\", \"function\")"
                + " VALUES (?,?,?,?,?,?,?,CAST(? AS metadata.\"TransferFunctionTypeCode\"))");
        statement.setString(1, format);
        int bandNumber = 0;
        for (final List<Category> list : categories) {
            statement.setInt(2, ++bandNumber);
            for (final Category category : list) {
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
                    statement.setNull(6, Types.DOUBLE);
                    statement.setNull(7, Types.DOUBLE);
                    statement.setNull(8, Types.VARCHAR);
                }
                final int count = statement.executeUpdate();
                if (count != 1) {
                    throw new IllegalUpdateException(transaction.database.locale, count);
                }
            }
        }
    }
}
