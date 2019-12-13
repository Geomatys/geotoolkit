/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

/**
 * <p>Choose interpolation :<br/>
 *  - neighbor : find nearest pixel values.<br/>
 *  - bilinear : compute biLinear pixel value from nearest 4 pixels values.<br/>
 *  - biCubic  : compute biCubic pixel value from nearest 16 pixels values.<br/>
 *  - lanczos  : compute biCubic pixel value from nearest n * n pixels values.<br/>
 * where n = 2 * {@link LanczosInterpolation#lanczosWindow} </p>
 *
 * Note that a margin is defined for each value, specifying a radius in pixel unit. It's the maximal distance used by
 * the interpolation to mix colors. This margin value can be < 0 when the radius is not fixed. Therefore, it's user
 * responsability to define the right value. However, when the margin is negative, it's absolute value should be a clue
 * about a default acceptable margin to apply for this interpolation.
 *
 * @author Rémi Marechal (Geomatys).
 */
public enum InterpolationCase {
    NEIGHBOR(0),
    BILINEAR(1),
    BICUBIC(2),
    BICUBIC2(2),
    LANCZOS(-3);

    public final int margin;

    InterpolationCase(final int margin) {
        this.margin = margin;
    }
}
