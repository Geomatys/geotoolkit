/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import javax.measure.unit.Unit;

import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.TransferFunctionType;

import org.geotoolkit.util.MeasurementRange;


/**
 * The range of physical values in an image band. This interface is a generalization of the
 * {@link org.opengis.metadata.content.Band} interface defined by ISO 19115-2: {@code Band}
 * describes specifically the range of wavelengths in the electromagnetic spectrum, while
 * {@code SampleDimension} allows any kind of physical measurements.
 * <p>
 * The {@code SampleDimension} API is intentionnaly identical to the {@code Band} API with
 * the restriction to wavelengths removed, some methods omitted and the following method
 * added:
 * <p>
 * <ul>
 *   <li>{@link #getValueRange()}</li>
 *   <li>{@link #getFillValues()}</li>
 * </ul>
 * <p>
 * If image bands are known to be a measurement of wavelengths in the electromagnetic spectrum,
 * then the instances returned by {@link SpatialMetadata#getSampleDimensions()} shall implement
 * both the {@code SampleDimension} and the {@code Band} interfaces.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @see org.opengis.metadata.content.Band
 *
 * @since 3.06
 * @module
 */
public interface SampleDimension extends RangeDimension {
    /**
     * The range of values in the band. It doesn't need to be determined from the actual content of
     * the image (e.g. as determined from the {@linkplain javax.media.jai.operator.ExtremaDescriptor
     * extrema operation}). It is typically the minimal and maximal values that can be stored in the
     * band.
     *
     * {@note The information provided by this method can also be obtained by the ISO 19115-2
     *        <code>getMinValue()</code>, <code>getMaxValue()</code> and <code>getUnits()</code>
     *        methods. Nevertheless this <code>getValueRange()</code> method has been added
     *        because it allows to specify whatever the minimal and maximal values are inclusive,
     *        and can distinguish the case of integer values from the case of floating point values.}
     *
     * @return The range of values, or {@code null} if none.
     *
     * @see #getMinValue()
     * @see #getMaxValue()
     * @see #getUnits()
     */
    MeasurementRange<?> getValueRange();

    /**
     * The minimal value that can be stored in the designated band, or {@code null} if unspecified.
     * In the particular case of a measurement in the electromagnetic spectrum, this is the shortest
     * wavelength that the sensor is capable of collecting.
     * <p>
     * This method is equivalent to the code below (null checks omitted for simplicity):
     *
     * {@preformat java
     *     return getValueRange().getMinimum(true);
     * }
     *
     * @return The minimal value that can be stored in the designated band, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getMinValue()
     * @see MeasurementRange#getMinimum(boolean)
     */
    Double getMinValue();

    /**
     * The maximal value that can be stored in the designated band, or {@code null} if unspecified.
     * In the particular case of a measurement in the electromagnetic spectrum, this is the longuest
     * wavelength that the sensor is capable of collecting.
     * <p>
     * This method is equivalent to the code below (null checks omitted for simplicity):
     *
     * {@preformat java
     *     return getValueRange().getMinimum(true);
     * }
     *
     * @return The maximal value that can be stored in the designated band, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getMaxValue()
     * @see MeasurementRange#getMaximum(boolean)
     */
    Double getMaxValue();

    /**
     * The units in which the value are expressed.
     * This method is equivalent to the code below (null checks omitted for simplicity):
     *
     * {@preformat java
     *     return getValueRange().getUnits();
     * }
     *
     * @return Units in which the value are expressed, or {@code null}.
     *
     * @see org.opengis.metadata.content.Band#getUnits()
     * @see MeasurementRange#getUnits()
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

    /**
     * Returns the values used for filling the cells that do not have any physical value.
     *
     * @return The values used for filling the cells that do not have any physical value.
     */
    double[] getFillValues();
}
