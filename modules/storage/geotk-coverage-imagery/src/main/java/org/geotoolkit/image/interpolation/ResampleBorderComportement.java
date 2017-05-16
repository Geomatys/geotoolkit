/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
 * An enum to stipulate to resampling procedure which comportement adopted
 * when projected destination coordinates are out of validity pixel source area.
 *
 * {@linkplain #EXTRAPOLATION} : allow extrapolation.
 * {@linkplain #FILL_VALUE} : fill pixel which is out of expected source area, by the given array.
 * {@linkplain #CROP} :
 *
 *
 * @author Remi Marechal (Geomatys).
 */
public enum ResampleBorderComportement {
    EXTRAPOLATION,
    FILL_VALUE,
    CROP
}
