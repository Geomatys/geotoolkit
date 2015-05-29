/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;


/**
 * The policy for {@linkplain MosaicImageReader#getImageTypes computing image types} in a mosaic.
 * Those policies offer various compromise between performance and safety in presence of a mosaic
 * of heterogenous image format. The {@link #SUPPORTED_BY_ALL} mode is the safest one, but may be
 * very expensive to compute. The {@link #SUPPORTED_BY_FIRST} mode is faster, but assume that every
 * tiles in a mosaic use the same image format.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @see MosaicImageReadParam#getImageTypePolicy
 * @see MosaicImageReader#getDefaultImageTypePolicy
 *
 * @since 2.5
 * @module
 */
public enum ImageTypePolicy {
    /**
     * Returns {@linkplain javax.imageio.ImageTypeSpecifier type specifiers} that are supported
     * by every tiles. This is the most robust policy, but also the most expensive. This policy
     * should be selected when the tiles may be stored in heterogeneous formats.
     */
    SUPPORTED_BY_ALL(true),

    /**
     * Returns {@linkplain javax.imageio.ImageTypeSpecifier type specifiers} that are supported
     * by one tile, selected arbitrary. This policy is appropriate when the tiles are stored in
     * a homogeneous format.
     * <p>
     * When Java assertions are enabled, {@linkplain MosaicImageReader} will ensures that this
     * policy produces the same result than the {@link #SUPPORTED_BY_ALL} policy.
     */
    SUPPORTED_BY_ONE(true),

    /**
     * Returns {@linkplain javax.imageio.ImageTypeSpecifier type specifiers} that are supported
     * by one of the first tiles to be read. This policy is similar to {@link #SUPPORTED_BY_ONE},
     * except that the result is cached for all future invocation of the {@code read} method,
     * unless a new input is set.
     *
     * @since 3.15
     */
    SUPPORTED_BY_FIRST(true),

    /**
     * Returns a single {@linkplain javax.imageio.ImageTypeSpecifier type specifier} for images
     * of {@link java.awt.image.BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB}. This policy should
     * be used only when tiles are known in advance to be compatible with the ARGB model, and
     * this model is wanted.
     */
    ALWAYS_ARGB(false),

    /**
     * Returns a single {@linkplain javax.imageio.ImageTypeSpecifier type specifier} for images
     * of {@link java.awt.image.BufferedImage#TYPE_INT_RGB TYPE_INT_RGB}. This policy should
     * be used only when tiles are known in advance to be compatible with the RGB model, and
     * this model is wanted.
     *
     * @since 3.15
     */
    ALWAYS_RGB(false);

    /**
     * {@code true} if reading a single tile with this policy can be delegated directly to the
     * underlying image reader as an optimization.
     */
    final boolean canDelegate;

    /**
     * Creates a new enum.
     */
    private ImageTypePolicy(final boolean canDelegate) {
        this.canDelegate = canDelegate;
    }
}
