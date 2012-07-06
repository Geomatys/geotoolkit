/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.NoSuchElementException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @deprecated Renamed {@link MetadataNodeAccessor}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @see SpatialMetadata#getInstanceForType(Class)
 * @see SpatialMetadata#getListForType(Class)
 *
 * @since 2.5
 * @module
 */
@Deprecated
public class MetadataAccessor extends MetadataNodeAccessor {
    /**
     * Creates an accessor with the same parent and childs than the specified one. The two
     * accessors will share the same {@linkplain Node metadata nodes} (including the list
     * of childs), so change in one accessor will be immediately reflected in the other
     * accessor. However each accessor can {@linkplain #selectChild select their child}
     * independently.
     * <p>
     * The initially {@linkplain #selectChild selected child} and {@linkplain #getWarningLevel()
     * warnings level} are the same than the given accessor.
     * <p>
     * The main purpose of this constructor is to create many views over the same list
     * of childs, where each view can {@linkplain #selectChild select} a different child.
     *
     * @param clone The accessor to clone.
     */
    public MetadataAccessor(final MetadataAccessor clone) {
        super(clone);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative
     * to the given parent. In the example below, the complete path to the child accessor
     * is {@code "DiscoveryMetadata/Extent/GeographicElement"}:
     *
     * {@preformat java
     *     MetadataAccessor parent = new MetadataAccessor(..., "DiscoveryMetadata/Extent", ...);
     *     MetadataAccessor child  = new MetadataAccessor(parent, "GeographicElement", null);
     * }
     *
     * {@section Auto-detection of children}
     * If the metadata node has no child, then {@code childPath} shall be {@code null}.
     * If the caller does not know whatever the node has childs or not, then the
     * {@code "#auto"} special value can be used. Note that this auto-detection may
     * throw an {@link IllegalArgumentException} if the node is not defined by the
     * {@link IIOMetadataFormat}. It is preferable to specify explicitly the child
     * element name when this name is known.
     *
     * @param parent    The accessor for which the {@code path} is relative.
     * @param path      The path to the {@linkplain Node node} of interest.
     * @param childPath The path to the child {@linkplain Element elements}, or {@code null}
     *                  if none, or {@code "#auto"} for auto-detection.
     *
     * @throws IllegalArgumentException if {@code childPath} is {@code "#auto"} but the childs
     *         can not be inferred from the metadata format.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @since 3.06
     */
    public MetadataAccessor(MetadataAccessor parent, String path, String childPath)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(parent, path, childPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} accepting a user object of the
     * given type. This method is convenient for fetching an object of some known type without
     * regards to its location in the sub-tree.
     *
     * @param  parent      The accessor from which to start the search for an element accepting
     *                     the given type.
     * @param  objectClass The {@linkplain IIOMetadataFormat#getObjectClass(String) class of user
     *                     object} to locate.
     *
     * @throws IllegalArgumentException If no element accepting the given type was found,
     *         or if more than one element accepting that type was found.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @see #listPaths(IIOMetadataFormat, Class)
     *
     * @since 3.06
     */
    public MetadataAccessor(MetadataAccessor parent, Class<?> objectClass)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(parent, objectClass);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative to
     * the {@linkplain IIOMetadataFormat#getRootName() root}. This is a convenience method for the
     * {@linkplain #MetadataAccessor(IIOMetadata, String, String, String) constructor below}
     * with {@code formatName} and {@code childPath} argument set to {@code "#auto"} value.
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  parentPath  The path to the {@linkplain Node node} of interest, or {@code null}
     *                     if the {@code metadata} root node is directly the node of interest.
     *
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @since 3.06
     */
    public MetadataAccessor(IIOMetadata metadata, String parentPath)
            throws NoSuchElementException
    {
        super(metadata, parentPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} at the given path relative to
     * the {@linkplain IIOMetadataFormat#getRootName() root}. The paths can contain many elements
     * separated by the {@code '/'} character.
     * See the <a href="#skip-navbar_top">class javadoc</a> for more details.
     *
     * {@section Auto-detection of children and format}
     * The {@code childPath} argument can be {@code "#auto"}, which is processed as documented
     * in the {@linkplain #MetadataAccessor(MetadataAccessor, String, String) above constructor}.
     * <p>
     * The {@code formatName} can be {@code null} or {@code "#auto"}, in which case a format
     * is selected automatically:
     * <p>
     * <ul>
     *   <li>If {@code metadata} is an instance of {@link SpatialMetadata}, then the
     *       {@linkplain SpatialMetadata#format format} given at {@code SpatialMetadata}
     *       construction time is used.</li>
     *   <li>Otherwise the first format returned by {@link IIOMetadata#getMetadataFormatNames()}
     *       is used. This is usually in preference order: the native format, the standard format
     *       or the first extra format.</li>
     * </ul>
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  formatName  The name of the {@linkplain IIOMetadata#getMetadataFormat(String) format
     *                     to use}, or {@code null} or {@code "#auto"} for an automatic selection.
     * @param  parentPath  The path to the {@linkplain Node node} of interest, or {@code null}
     *                     if the {@code metadata} root node is directly the node of interest.
     * @param  childPath   The path (relative to {@code parentPath}) to the child
     *                     {@linkplain Element elements}, or {@code null} if none,
     *                     or {@code "#auto"} for auto-detection.
     *
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     */
    public MetadataAccessor(IIOMetadata metadata, String formatName, String parentPath,
            String childPath) throws NoSuchElementException
    {
        super(metadata, formatName, parentPath, childPath);
    }

    /**
     * Creates an accessor for the {@linkplain Element element} accepting a user object of the
     * given type. This method is convenient for fetching an object of some known type without
     * regards to its location in the tree. For example if the metadata format is
     * {@link SpatialMetadataFormat#STREAM} then:
     *
     * {@preformat java
     *     new MetadataAccessor(metadata, formatName, GeographicElement.class);
     * }
     *
     * is equivalent to:
     *
     * {@preformat java
     *     new MetadataAccessor(metadata, formatName, "DiscoveryMetadata/Extent/GeographicElement", "#auto");
     * }
     *
     * @param  metadata    The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                     sub-class is recommended, but not mandatory.
     * @param  formatName  The name of the {@linkplain IIOMetadata#getMetadataFormat(String) format
     *                     to use}, or {@code null} or {@code "#auto"} for an automatic selection.
     * @param  objectClass The {@linkplain IIOMetadataFormat#getObjectClass(String) class of user
     *                     object} to locate.
     *
     * @throws IllegalArgumentException If no element accepting the given type was found,
     *         or if more than one element accepting that type was found.
     * @throws NoSuchElementException If this accessor is {@linkplain #isReadOnly() is read only}
     *         and the given metadata doesn't contains a node for the element to fetch.
     *
     * @see #listPaths(IIOMetadataFormat, Class)
     *
     * @since 3.06
     */
    public MetadataAccessor(IIOMetadata metadata, String formatName, Class<?> objectClass)
            throws IllegalArgumentException, NoSuchElementException
    {
        super(metadata, formatName, objectClass);
    }
}
