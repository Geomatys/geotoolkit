/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import javax.imageio.ImageWriter;


/**
 * Interface for {@link ImageReader} and {@link ImageWriter} implementations where each image
 * have a name. The standard Java API uses an integer for identifying the images to be read or
 * written, using a zero-based numbering. But some file formats like NetCDF identify the images
 * by name (in the case of NetCDF, "<cite>images</cite>" are actually "<cite>variables</cite>").
 * This class provides a mean to map image (or variable) names to image index for use with the
 * {@code ImageReader} or {@code ImageWriter} API.
 *
 * {@section Common usage: names for images}
 * If the image names can not be known <i>a priori</i>, then the user needs to invoke
 * {@link #getImageNames()} and inspect the returned list. But if the user know in advance
 * the name of the images to be read, then a more efficient approach is to declare the names
 * of the image of interest. This approach avoid the need to extract the list of all image names.
 * <p>
 * The example below declares that the image named {@code "temperature"} should be assigned
 * to the image index 0, and the image named {@code "salinity"} to image index 1:
 *
 * {@preformat java
 *     imageReader.setImageNames("temperature", "salinity");
 *     BufferedImage temperature = imageReader.read(0);
 *     BufferedImage salinity    = imageReader.read(1);
 * }
 *
 * If no image exist for a given name, then an {@link ImageNameNotFoundException} will be thrown
 * at reading time.
 *
 * {@section Alternative usage: names for bands}
 * In some cases, it is convenient to use the variable names for band indices rather than image
 * indices. For example when reading <cite>geostrophic current</cite> data as a vector field,
 * where the variable named <var>u</var> is the East-West component of the vectors and the
 * variable named <var>v</var> is the North-South component, we may want to read those two
 * components as two bands in the same image:
 *
 * {@preformat java
 *     imageReader.setBandNames(0, "u", "v");
 *     BufferedImage currents = imageReader.read(0); // Two-banded image.
 * }
 *
 * Named bands, if any, have precedence over named images. By default there is no named band.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.08
 * @module
 */
public interface NamedImageStore {
    /**
     * Returns the names associated to all image indices. The first name is assigned to the
     * image at index 0, the second name to image at index 1, <i>etc</i>. In other words a
     * call to <code>{@linkplain ImageReader#read(int) ImageReader.read}(imageIndex)</code>
     * will read the image named {@code imageNames.get(imageIndex)} where {@code imageNames}
     * is the list returned by this method.
     * <p>
     * This method initially returns the list of all images contained in the underlying file.
     * If the {@link #setImageNames(String[])} method has been invoked with a non-null array
     * argument, then {@code getImageNames()} returns the names which were specified to that
     * {@code setImageNames} method.
     *
     * @return The names associated to all image indices, or {@code null} if there is no
     *         such mapping.
     * @throws IOException if the image stream can not be read.
     */
    List<String> getImageNames() throws IOException;

    /**
     * Sets the names to associate to all image indices. The first name is assigned to the image
     * at index 0, the second name to the image at index 1, <i>etc</i>. This method does not
     * delete or create image in the file; it merely changes the mapping between names and image
     * indices for the purpose of using the {@code ImageReader} or {@code ImageWriter} API.
     * <p>
     * If a supplied image name does not exist in the file to be read or written, then an
     * {@link ImageNameNotFoundException} will be thrown either at {@code setImageNames(...)}
     * invocation time, or deferred to the first invocation of an {@link ImageReader} or
     * {@link ImageWriter} method expecting an image index argument.
     * <p>
     * If {@code imageNames} array is set to {@code null}, then the names will be inferred
     * from the content of the file. This is the default behavior.
     *
     * @param  imageNames The set of names to be assigned to image index, or
     *         {@code (String[]) null} for the default set of names declared in the file.
     * @throws IOException If the given names can not be assigned to this image reader or
     *         writer, or if an I/O error occurred while processing.
     */
    void setImageNames(String... imageNames) throws IOException;

    /**
     * Returns the names of the bands for the given image, or {@code null} if none.
     * By default, this method returns {@code null} for every image index.
     *
     * @param  imageIndex Index of the image for which to get the band names.
     * @return The variable names of the bands for the given image, or {@code null}
     *         if the bands for the given image are unnamed.
     * @throws IOException if the image stream can not be read.
     *
     * @since 3.11
     */
    List<String> getBandNames(int imageIndex) throws IOException;

    /**
     * Sets the names of the bands for the given image, or {@code null} for removing any naming.
     * See the class-javadoc for a usage example.
     *
     * @param  imageIndex Index of the image for which to set the band names.
     * @param  bandNames The variable names of the bands for the given image,
     *         or {@code null} for removing any naming.
     * @throws IOException If the given names can not be assigned to this image reader or
     *         writer, or if an I/O error occurred while processing.
     *
     * @since 3.11
     */
    void setBandNames(int imageIndex, String... bandNames) throws IOException;
}
