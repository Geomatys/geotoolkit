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
package org.geotoolkit.image.color;

import javax.media.jai.operator.OrderedDitherDescriptor;
import javax.media.jai.operator.ErrorDiffusionDescriptor;


/**
 * Kinds of Color Quantization to be applied in some {@link ImageWorker} method calls.
 * Those enums can be used as the value of the {@link ImageWorker#COLOR_QUANTIZATION}
 * rendering hint.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated Deprecated together with {@link ImageWorker}.
 */
@Deprecated
public enum ColorQuantization {
    /**
     * Color quantization by finding the nearest color to each pixel in a color map and
     * "diffusing" the color quantization error below and to the right of the pixel.
     *
     * @see ErrorDiffusionDescriptor
     */
    ERROR_DIFFUSION,

    /**
     * Color quantization by finding the nearest color to each pixel in a color cube and
     * "shifting" the resulting index value by a pseudo-random amount determined by the
     * values of a dither mask.
     *
     * @see OrderedDitherDescriptor
     */
    ORDERED_DITHER
}
