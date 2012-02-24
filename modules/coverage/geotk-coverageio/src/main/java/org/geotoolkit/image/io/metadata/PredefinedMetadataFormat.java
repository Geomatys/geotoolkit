/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;


/**
 * Provides constructor methods for predefined metadata formats. This class provides the methods
 * that created the <a href="SpatialMetadataFormat.html#default-formats">trees documented in the
 * super-class</a>. This class is public in order to allow users to create their own metadata
 * trees derived from the predefined Geotk trees.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.07 (derived from 3.05)
 * @module
 *
 * @deprecated Replaced by {@link SpatialMetadataFormatBuilder}.
 */
@Deprecated
public class PredefinedMetadataFormat extends SpatialMetadataFormat {
    /**
     * Creates an initially empty format. Subclasses shall invoke the various
     * {@code addTree(...)} methods defined in this class or parent class for
     * adding new elements and attributes.
     *
     * @param rootName the name of the root element.
     */
    protected PredefinedMetadataFormat(final String rootName) {
        super(rootName);
    }

    /**
     * Adds the tree structure for <cite>stream</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Stream metadata</cite>" column of the
     * <a href="SpatialMetadataFormat.html#default-formats">class javadoc</a>.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#STREAM
     *
     * @deprecated Replaced by {@link SpatialMetadataFormatBuilder}.
     */
    @Deprecated
    protected void addTreeForStream(String addToElement) {
        new SpatialMetadataFormatBuilder(this).addTreeForStream(addToElement);
    }

    /**
     * Adds the tree structure for <cite>image</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Image metadata</cite>" column of the
     * <a href="SpatialMetadataFormat.html#default-formats">class javadoc</a>.
     * <p>
     * The <cite>Coordinate Reference System</cite> branch is not included by this method.
     * For including CRS information, the {@link #addTreeForCRS(String)} method shall be
     * invoked explicitly.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#IMAGE
     *
     * @deprecated Replaced by {@link SpatialMetadataFormatBuilder}.
     */
    @Deprecated
    protected void addTreeForImage(String addToElement) {
        new SpatialMetadataFormatBuilder(this).addTreeForImage(addToElement);
    }

    /**
     * Adds the tree structure for a <cite>Coordinate Reference System</cite> object.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @deprecated Replaced by {@link SpatialMetadataFormatBuilder}.
     */
    @Deprecated
    protected void addTreeForCRS(String addToElement) {
        new SpatialMetadataFormatBuilder(this).addTreeForCRS(addToElement);
    }
}
