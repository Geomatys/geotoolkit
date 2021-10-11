/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
