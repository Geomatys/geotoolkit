/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.plugin;

/**
 * To define which part of image will be written or read.
 * 
 * @see TestTiffImageReaderWriter#lowerLeftCorner() 
 * @see TestTiffImageReaderWriter#lowerRightCorner() 
 * @see TestTiffImageReaderWriter#upperLeftCornerTest()  
 * @see TestTiffImageReaderWriter#upperRightCorner()  
 * @see TestTiffImageReaderWriter#lowerLeftCorner() 
 * @author Remi Marechal (Geomatys).
 */
public enum ImageOrientation {
    IMAGE_UPPER_LEFT_CORNER,
     IMAGE_UPPER_RIGHT_CORNER, 
    IMAGE_LOWER_LEFT_CORNER,  
     IMAGE_LOWER_RIGHT_CORNER, 
     IMAGE_CENTER             
}
