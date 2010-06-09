/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.util.List;
import javax.imageio.ImageReader;

import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.MathTransform1D;

import org.geotoolkit.util.Localized;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.ImageMetadataException;
import org.geotoolkit.image.io.metadata.MetadataHelper;
import org.geotoolkit.image.io.metadata.SampleDimension;
import org.geotoolkit.referencing.operation.transform.LinearTransform1D;


/**
 * A helper class for processing image metadata. If the category to create is geophysics, then
 * we use a default range of sample values (1 to 255 because it fit in a 8 bits image, keeping
 * 0 for "no data") and define the transfer function accordingly. We don't do that in the default
 * {@code MetadataHelper} implementation because the choosen range is arbitrary, and we need the
 * transfer function for filling the {@code "Categories"} table in the coverage database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
final class NewGridCoverageHelper extends MetadataHelper {
    /**
     * The upper sample value (inclusive) to use if no transfer function is defined.
     * Note that the lower sample value is fixed to 1.
     */
    private static final int UPPER = 255;

    /**
     * {@code true} if the category created by {@link #createCategory} is geophysics.
     */
    private transient boolean isGeophysics;

    /**
     * Creates a new {@code MetadataHelper} for the given reader.
     */
    NewGridCoverageHelper(final ImageReader reader) {
        super((reader instanceof Localized) ? (Localized) reader : null);
    }

    /**
     * Converts the given {@link SampleDimension}s to {@link GridSampleDimension}s. If
     * {@link #createCategory} created a geophysics category, then we will ignore the
     * fill values ({@link org.geotoolkit.coverage.io.ImageCoverageReader} will process
     * them) and use 0 as the "no data" value instead.
     */
    @Override
    public List<GridSampleDimension> getGridSampleDimensions(
            final List<? extends SampleDimension> sampleDimensions) throws ImageMetadataException
    {
        isGeophysics = false;
        final List<GridSampleDimension> bands = super.getGridSampleDimensions(sampleDimensions);
        if (bands != null && isGeophysics) {
            for (int i=bands.size(); --i>=0;) {
                GridSampleDimension band = bands.get(i);
                if (band != null) {
                    final List<Category> categories = band.getCategories();
                    for (int j=categories.size(); --j>=0;) {
                        final Category category = categories.get(j);
                        if (category.isQuantitative()) {
                            band = new GridSampleDimension(band.getDescription(), new Category[] {
                                Category.NODATA, category
                            }, band.getUnits());
                            break;
                        }
                    }
                    band = band.geophysics(true);
                    bands.set(i, band);
                }
            }
        }
        return bands;
    }

    /**
     * Invoked by {@link #getGridSampleDimensions(List)} for creating the quantitative
     * category. If the category is geophysics, we will create a transfer function for
     * the 1 to 255 sample value range.
     */
    @Override
    protected Category createCategory(InternationalString dimensionName, Color[] colors,
            NumberRange<?> validSampleValues, MathTransform1D transferFunction)
            throws ImageMetadataException
    {
        if (transferFunction == null && validSampleValues != null) {
            final double minimum = validSampleValues.getMinimum();
            final double maximum = validSampleValues.getMaximum();
            final double scale = (maximum - minimum) / (validSampleValues.isMaxIncluded() ? UPPER-1 : UPPER);
            // Note: we can ignore isMinIncluded(); it is safe to assume 'true' in all cases.
            if (!Double.isNaN(scale) && !Double.isInfinite(scale)) {
                validSampleValues = NumberRange.create(1, UPPER);
                transferFunction = LinearTransform1D.create(scale, minimum - scale);
                isGeophysics = true;
            }
        }
        return super.createCategory(dimensionName, colors, validSampleValues, transferFunction);
    }
}
