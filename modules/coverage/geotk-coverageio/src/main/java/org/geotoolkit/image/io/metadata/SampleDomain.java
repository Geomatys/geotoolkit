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

import org.apache.sis.measure.NumberRange;


/**
 * The range of sample values in an image band. This interface defines a subset of the
 * {@link SampleDimension} attributes, where only the method relative to sample values
 * are retained. All methods related to physical values, or conversion to physical values,
 * are out-of-scope of this interface.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
public interface SampleDomain {
    /**
     * The range of valid sample values in the band, not including {@linkplain #getFillSampleValues()
     * fill sample values}. This range doesn't need to be determined from the actual content of the
     * image (e.g. as determined from the JAI {@linkplain javax.media.jai.operator.ExtremaDescriptor
     * extrema operation}). It is typically the minimal and maximal values that can be stored in the
     * band.
     *
     * @return The range of sample values (not including fill values), or {@code null} if unspecified.
     */
    NumberRange<?> getValidSampleValues();

    /**
     * Returns the sample values used for filling the cells that do not have any physical value.
     *
     * @return The sample values used for filling the cells that do not have any physical value.
     */
    double[] getFillSampleValues();
}
