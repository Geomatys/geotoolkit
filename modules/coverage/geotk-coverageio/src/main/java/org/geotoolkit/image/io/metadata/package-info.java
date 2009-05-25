/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

/**
 * Geographic informations encoded in images as metadata.
 * <p>
 * This package defines a {@linkplain org.geotoolkit.image.io.metadata.GeographicMetadataFormat geographic
 * metadata format} which is aimed as image format neutral. The metadata format defines a structure
 * for a tree of nodes in a way similar to the way methods are defined in GeoAPI interfaces. For
 * example it defines a {@code "CoordinateReferenceSystem"} node with {@code "CoordinateSystem"}
 * and {@code "Datum"} child nodes.
 * <p>
 * The {@link org.geotoolkit.image.io.metadata.GeographicMetadata} class contains convenience methods
 * for encoding metatadata. Metadata are usually given as {@link java.lang.String} or {@code double}
 * attributes only, but image readers can optionaly attach fully constructed GeoAPI objects if they
 * wish. If only {@link java.lang.String} and {@code double} are used, then the duty to create GeoAPI
 * objects from them incomb to the {@link org.geotoolkit.coverage.io} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.image.io.metadata;
