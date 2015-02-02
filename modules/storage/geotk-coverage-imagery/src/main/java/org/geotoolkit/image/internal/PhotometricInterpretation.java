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

/**
 * Define type of photometric interpretation.
 *
 * @author Remi Marechal (Geomatys).
 */
public enum PhotometricInterpretation {
    
    /**
     * Define image properties for bilevel and grayscale images: 0 is imaged as black.
     */
    GrayScale, 
    
    /**
     * Define image properties for RGB image.
     * RGB value of (0, 0, 0) represents black, and (255, 255, 255) represents white, assuming 8-bit components. 
     * The components are stored in the indicated order: first Red, then Green, then Blue.
     */
    RGB, 
    
    /**
     * Define image with Palette color. 
     * In this model, a color is described with a single component. 
     * The value of the component is used as an index into the red, 
     * green and blue curves in the ColorMap field to retrieve an RGB triplet that defines the color. 
     * When PhotometricInterpretation = palette is used, ColorMap must be present and SamplesPerPixel must be 1.
     */
    Palette;
    
    /**
     * Mapping between {@link ImageUtils#getPhotometricInterpretation(java.awt.image.ColorModel)} and
     * {@link org.geotoolkit.image.internal.PhotometricInterpretation} enum.
     *
     * @param photometricInterpretation integer compute by {@link ImageUtils#getPhotometricInterpretation(java.awt.image.ColorModel)}. 
     * @return {@link org.geotoolkit.image.internal.SampleType} or {@code null} if type undefined.
     */
    public static PhotometricInterpretation valueOf(int photometricInterpretation) {
        switch (photometricInterpretation) {
            case 1 : return GrayScale;
            case 2 : return RGB;
            case 3 : return Palette;
            default: return null;
        }
    }
}
