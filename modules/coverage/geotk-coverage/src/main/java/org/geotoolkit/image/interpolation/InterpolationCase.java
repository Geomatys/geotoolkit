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
 *  - lanczos  : todo.</p>
 *
 * @author Rémi Marechal (Geomatys).
 */
public enum InterpolationCase {
    NEIGHBOR,
    BILINEAR,
    BICUBIC,
    BICUBIC2,
    LANCZOS

}
