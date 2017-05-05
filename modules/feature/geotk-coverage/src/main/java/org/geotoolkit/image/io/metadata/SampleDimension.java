/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import javax.measure.Unit;

import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.GridSampleDimension;


/**
 * The range of physical values in an image band. This interface is a generalization of the
 * {@link org.opengis.metadata.content.Band} interface defined by ISO 19115-2: {@code Band}
 * describes specifically the range of wavelengths in the electromagnetic spectrum, while
 * {@code SampleDimension} allows any kind of physical measurements.
 * <p>
 * The {@code SampleDimension} API is intentionally identical to the {@code Band} API with
 * the restriction to wavelengths removed, some methods omitted and the following method
 * added:
 * <p>
 * <ul>
 *   <li>{@link #getValidSampleValues()}</li>
 *   <li>{@link #getFillSampleValues()}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @see org.opengis.metadata.content.Band
 *
 * @since 3.06
 * @module
 */
public interface SampleDimension extends RangeDimension, SampleDomain {
    /**
     * The range of valid sample values in the band, not including {@linkplain #getFillSampleValues()
     * fill sample values}. This is the range of values <em>as they are stored in the image file</em>,
     * before the {@linkplain #getScaleFactor() scale factor} and {@linkplain #getOffset() offset}
     * are applied for conversion to physical values.
     * <p>
     * This range doesn't need to be determined from the actual content of the image (e.g. as
     * determined from the JAI {@linkplain javax.media.jai.operator.ExtremaDescriptor extrema
     * operation}). It is typically the minimal and maximal values that can be stored in the
     * band.
     * <p>
     * The information provided by this method could be inferred from the information provided
     * by the ISO 19115-2 {@link #getMinValue()} and {@link #getMaxValue()} methods. Nevertheless
     * this {@code getValidSampleValues()} method has been added because it allows to specify
     * whatever the minimal and maximal values are inclusive or exclusive, and can handle the
     * numbers as integers when this the type used by the underlying image format (it has an
     * impact on the arithmetic operations used).
     *
     * @return The range of sample values (not including fill values), or {@code null} if unspecified.
     *
     * @see #getMinValue()
     * @see #getMaxValue()
     * @see #getUnits()
     */
    @Override
    NumberRange<?> getValidSampleValues();

    /**
     * Returns the sample values used for filling the cells that do not have any physical value.
     * <cite>Sample</cite> values are values as they are stored in the image file, before the
     * {@linkplain #getScaleFactor() scale factor} and {@linkplain #getOffset() offset} are
     * applied for conversion to physical values.
     *
     * @return The sample values used for filling the cells that do not have any physical value.
     */
    @Override
    double[] getFillSampleValues();

    /**
     * The minimal value that can be stored in the designated band, or {@code null} if unspecified.
     * In the particular case of a measurement in the electromagnetic spectrum, this is the shortest
     * wavelength that the sensor is capable of collecting.
     * <p>
     * This method is equivalent to the code below (null checks omitted for simplicity):
     *
     * {@preformat java
     *     return getValidSampleValues().getMinimum(true) * getScaleFactor() + getOffset();
     * }
     *
     * @return The minimal value that can be stored in the designated band, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getMinValue()
     */
    Double getMinValue();

    /**
     * The maximal value that can be stored in the designated band, or {@code null} if unspecified.
     * In the particular case of a measurement in the electromagnetic spectrum, this is the longest
     * wavelength that the sensor is capable of collecting.
     * <p>
     * This method is equivalent to the code below (null checks omitted for simplicity):
     *
     * {@preformat java
     *     return getValidSampleValues().getMaximum(true) * getScaleFactor() + getOffset();
     * }
     *
     * @return The maximal value that can be stored in the designated band, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getMaxValue()
     */
    Double getMaxValue();

    /**
     * The units in which the value are expressed.
     *
     * @return Units in which the value are expressed, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getUnits()
     */
    Unit<?> getUnits();

    /**
     * Maximum number of significant bits in the uncompressed representation for the value
     * in each band of each pixel. Returns {@code null} if unspecified.
     *
     * @return Maximum number of significant bits in the uncompressed representation, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getBitsPerValue()
     */
    Integer getBitsPerValue();

    /**
     * Scale factor which has been applied to the cell value.
     * Returns {@code null} if unspecified.
     *
     * @return Scale factor which has been applied to the cell value, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getScaleFactor()
     */
    Double getScaleFactor();

    /**
     * The physical value corresponding to a cell value of zero.
     * Returns {@code null} if unspecified.
     *
     * @return The physical value corresponding to a cell value of zero, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getOffset()
     */
    Double getOffset();

    /**
     * Type of transfer function to be used when scaling a physical value for a given element.
     *
     * @return Type of transfer function.
     *
     * @see org.opengis.metadata.content.Band#getTransferFunctionType()
     */
    TransferFunctionType getTransferFunctionType();

    GridSampleDimension getGridSampleDimension();
}
