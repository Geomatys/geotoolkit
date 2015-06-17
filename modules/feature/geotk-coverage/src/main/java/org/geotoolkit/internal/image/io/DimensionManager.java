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
package org.geotoolkit.internal.image.io;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import org.geotoolkit.image.io.DimensionSet;
import org.geotoolkit.image.io.DimensionSlice;
import org.geotoolkit.image.io.NamedImageStore;
import org.geotoolkit.image.io.MultidimensionalImageStore;
import org.geotoolkit.image.io.IllegalImageDimensionException;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.util.collection.XCollections;
import org.apache.sis.internal.util.UnmodifiableArrayList;


/**
 * A {@link DimensionSet} extended with convenience methods for {@link ImageReader}s and
 * {@link ImageWriter}s implementing the {@link NamedImageStore} interface in addition of
 * {@link MultidimensionalImageStore}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see org.geotoolkit.image.io.plugin.NetcdfImageReader
 *
 * @since 3.15
 * @module
 */
public final class DimensionManager extends DimensionSet {
    /**
     * The index of the image to read in situations where the {@code imageIndex} argument can
     * not be used directly. The value, which is {@value}, is defined as a constant in order
     * to spot the places where the code makes such "image index replacements".
     * <p>
     * Note that using the image index 0 means that the first variable given to the
     * {@link #setImageNames(String[])} or {@link #setBandNames(int, String[])} methods
     * will be used, if any.
     */
    public static final int DEFAULT_IMAGE_INDEX = 0;

    /**
     * The user-supplied names of the variables to be read from the data file. The first name
     * is assigned to image index 0, the second name to image index 1, <i>etc</i>.  This list
     * shall be immutable.
     * <p>
     * This list is {@code null} if the user did not specified explicitly the variables to
     * read. In such case, the {@code ImageReader} implementation shall provides a default
     * list of variables.
     */
    private List<String> variableNames;

    /**
     * The names of the variables to be assigned to bands, or {@code null} if none.
     */
    private Map<Integer,List<String>> bandNames;

    /**
     * Creates a new {@code DimensionManager} instance for the given image reader or writer.
     *
     * @param store The image reader or writer for which this instance is created, or {@code null}.
     */
    public DimensionManager(final MultidimensionalImageStore store) {
        super(store);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        XCollections.clear(bandNames);
        variableNames = null;
    }

    /**
     * Returns {@code true} if the image API is used for navigating in
     * a hyper-cube dimension.
     *
     * @return {@code true} if the image API is used.
     */
    public boolean usesImageAPI() {
        return getAPIs().contains(DimensionSlice.API.IMAGES);
    }

    /**
     * Sets the user-supplied names of the variables to be read in a data files.
     * A value of {@code null} removes the user-supplied names, in which case the
     * {@link ImageReader} implementation will be responsible for providing a
     * default list of variables.
     *
     * @param names The list of variables to be assigned to image index, or {@code null}
     *        for removing the user-supplied names.
     */
    public void setImageNames(String... names) {
        if (names != null) {
            names = names.clone();
            ensureNonNull(names);
            variableNames = UnmodifiableArrayList.wrap(names);
        } else {
            variableNames = null;
        }
    }

    /**
     * Returns the user-supplied names of the variables to be read. This list is {@code null}
     * if the user did not specified explicitly the variables to read. In such case, the
     * {@link ImageReader} implementation shall provides a default list of variables.
     *
     * @return The names of the variables to be read, or {@code null} if
     *         the user did not supplied explicitly a list of variables.
     */
    public List<String> getImageNames() {
        return variableNames;
    }

    /**
     * Sets the names of the bands for the given image, or {@code null} for removing any naming.
     *
     * @param  imageIndex Index of the image for which to set the band names.
     * @param  names The variable names of the bands for the given image,
     *         or {@code null} for removing any naming.
     */
    public void setBandNames(final int imageIndex, String... names) {
        if (names == null) {
            if (bandNames != null) {
                bandNames.remove(imageIndex);
            }
        } else {
            if (names.length == 0) {
                throw new IllegalArgumentException(errors().getString(Errors.Keys.EmptyArray));
            }
            if (bandNames == null) {
                bandNames = new HashMap<>();
            }
            names = names.clone();
            ensureNonNull(names);
            bandNames.put(imageIndex, UnmodifiableArrayList.wrap(names));
        }
    }

    /**
     * Returns the names of the bands for the given image, or {@code null} if none.
     *
     * @param  imageIndex Index of the image for which to get the band names.
     * @return The variable names of the bands for the given image, or {@code null}
     *         if the bands for the given image are unnamed.
     */
    public List<String> getBandNames(final int imageIndex) {
        return (bandNames != null) ? bandNames.get(imageIndex) : null;
    }

    /**
     * Get the name of the variable to read for the given <cite>internal</cite> image index.
     * This is usually the name at the given index in the {@link #variableNames} list. However
     * a special case is performed if the user invoked the {@link #setBandNames(String[])} method
     * (i.e. specified explicitly which variable to assign to each band). In such case we will
     * load the first variable.
     *
     * {@note Using the first variable is an arbitrary choice, but work well if
     *        the bands are going to be read in sequential order.}
     *
     * @param  internalIndex The index of the image to be read, <strong>after</strong>
     *         processing by {@link #replaceImageIndex(int)}.
     * @return The name of the variable to load, or {@code null} if unspecified.
     */
    public String getVariableName(final int internalIndex) {
        final List<String> bandNames = getBandNames(internalIndex);
        if (bandNames != null) {
            for (final String name : bandNames) {
                if (name != null) {
                    return name;
                }
            }
        }
        if (variableNames != null) {
            return variableNames.get(internalIndex);
        }
        return null;
    }

    /**
     * Eventually updates the given image index for the currently API assignation.
     * If the {@code IMAGES} API is assigned to a dimension, then this method ensures
     * that the variables for at most one image has been specified by the user, in order
     * to avoid confusion.
     *
     * @param  imageIndex The user-specified image index.
     * @return The image index to use.
     * @throws IllegalImageDimensionException If this class is used in a way that may leads to confusion.
     */
    public int replaceImageIndex(int imageIndex) throws IllegalImageDimensionException {
        if (imageIndex != DEFAULT_IMAGE_INDEX && usesImageAPI()) {
            String variableName = null;
            if (variableNames != null) {
                switch (variableNames.size()) {
                    case 0:  break;
                    case 1:  variableName = variableNames.get(0); break;
                    default: throw new IllegalImageDimensionException(errors().getString(
                            Errors.Keys.DuplicatedValue_1, "variableName"));
                }
            }
            if (bandNames != null) {
                for (final Map.Entry<Integer,List<String>> entry : bandNames.entrySet()) {
                    final int index = entry.getKey();
                    if (index != DEFAULT_IMAGE_INDEX) {
                        throw new IllegalImageDimensionException(errors().getString(
                                Errors.Keys.UnexpectedParameter_1, "bandNames(" + index + ')'));
                    }
                    if (variableName != null) {
                        final List<String> names = entry.getValue();
                        if (!names.isEmpty() && !names.contains(variableName)) {
                            throw new IllegalImageDimensionException(errors().getString(
                                    Errors.Keys.InconsistentValue));
                        }
                    }
                }
            }
            imageIndex = DEFAULT_IMAGE_INDEX;
        }
        return imageIndex;
    }

    /**
     * Ensures that the given array does not contain any null element.
     *
     * @param names The array to check.
     * @throws NullArgumentException If at least one element of the given array is null.
     */
    private void ensureNonNull(final String[] names) throws NullArgumentException {
        for (int i=0; i<names.length; i++) {
            final String name = names[i];
            if (name == null) {
                throw new NullArgumentException(errors().getString(
                        Errors.Keys.NullArgument_1, "names[" + i + ']'));
            }
        }
    }

    /**
     * Returns the error resources bundle.
     */
    private Errors errors() {
        return Errors.getResources(getLocale());
    }
}
