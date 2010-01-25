/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.image.io;

import java.util.List;
import java.io.IOException;
import javax.imageio.ImageReader;


/**
 * Interface for {@link ImageReader} implementations where each image have a name. The standard
 * {@code ImageReader} API uses an integer for identifying the images to be read, using a zero-based
 * numbering. But some file formats like NetCDF identify the images by name (in the case of NetCDF,
 * "<cite>images</cite>" are actually "<cite>variables</cite>"). This class provides a mean to map
 * image (or variable) names to image index for use with the {@code ImageReader} API.
 *
 * {@section Usage}
 * If the names of images in a particular file can not be known <i>a priori</i>, then
 * the caller needs to invoke {@link #getImageNames()} and inspect the returned list.
 * <p>
 * If the names of the images to be read are known, then there is two possible approachs.
 * The first approach is to find the image index in the list of image names. The example
 * below uses this approach for reading the image named {@code "temperature"}:
 *
 * {@preformat java
 *     int imageIndex = imageReader.getImageNames().indexOf("temperature");
 *     if (imageIndex >= 0) {
 *         BufferedImage image = imageReader.read(imageIndex);
 *     }
 * }
 *
 * The second approach is to declare the names to assign to image indices. This approach avoid
 * the need to extract the list of all image names. The example below declare that the image
 * named {@code "temperature"} should be assigned to the image index 0:
 *
 * {@preformat java
 *     imageReader.setImageNames("temperature");
 *     BufferedImage image = imageReader.read(0);
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see ImageNameNotFoundException
 *
 * @since 3.08
 * @module
 */
public interface NamedImageReader {
    /**
     * Returns the names of the images to be read. The first name is assigned to the image
     * at index 0, the second name to image at index 1, <i>etc</i>. In other words a call
     * to <code>{@linkplain ImageReader#read(int) read}(imageIndex)</code> will read the
     * image named {@code imageNames.get(imageIndex)} where {@code imageNames} is the
     * list returned by this method.
     *
     * @return The name of the images to be read, or {@code null} if there is no mapping
     *         from name to image index.
     * @throws IOException if the image stream can not be read.
     */
    List<String> getImageNames() throws IOException;

    /**
     * Sets the name of the images to be read. The first name is assigned to the image at
     * index 0, the second name to the image at index 1, <i>etc</i>.
     * <p>
     * If a supplied image name does not exist in the file to be read, then an
     * {@link ImageNameNotFoundException} will be thrown either at {@code setImageNames(...)}
     * invocation time, or deferred to the first invocation of an {@link ImageReader} method
     * expecting an image index argument.
     * <p>
     * If {@code imageNames} array is set to {@code null}, then the names will be inferred
     * from the content of the file. This is the default behavior.
     *
     * @param  imageNames The set of names to be assigned to image index, or
     *         {@code (String[]) null} for the default set of names declared in the file.
     * @throws IOException If the given names can not be assigned to this image reader,
     *         or if an I/O error occured while processing.
     */
    void setImageNames(String... imageNames) throws IOException;
}
