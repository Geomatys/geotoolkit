/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.internal;

import java.awt.image.SampleModel;

/**
 * Define internaly {@link SampleModel} planar configuration.
 * Define how datas are ordonnance within {@link SampleModel}. 
 *
 * @author Remi Marechal (Geomatys).
 */
public enum PlanarConfiguration {
    
    /**
     * Integer that define planar configuration as interleaved.
     * For example in a RGB image, within the same band, pixel value will be order like follow : RGBRGBRGB ...
     */
    Interleaved, 
    
    /**
     * Integer that define planar configuration as banded.
     * For example in a RGB image, within the first band, pixel value will be order 
     * like follow : RRRRRRRR... and next band : GGGGGGG... and last : BBBBBBB.
     */
    Banded;
    
    /**
     * Mapping between {@link ImageUtils#getPlanarConfiguration(java.awt.image.ColorModel)} and
     * {@link org.geotoolkit.image.internal.PlanarConfiguration} enum.
     *
     * @param planardConfiguration integer compute by {@link ImageUtils#getPlanarConfiguration(java.awt.image.SampleModel)}. 
     * @return {@link org.geotoolkit.image.internal.SampleType} or {@code null} if type undefined.
     */
    public static PlanarConfiguration valueOf(int planardConfiguration) {
        switch (planardConfiguration) {
            case 1 : return Interleaved;
            case 2 : return Banded;
            default: return null;
        }
    }
}
