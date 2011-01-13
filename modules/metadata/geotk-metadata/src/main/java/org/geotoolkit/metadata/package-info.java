/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 * Root package for various metadata implementations. For a global overview of metadata in Geotk,
 * see the <a href="{@docRoot}/../modules/metadata/index.html">Metadata page on the project web site</a>.
 * <p>
 * This root package is not linked to any particular metadata standard. It assumes that a standard
 * is defined through a set of Java interfaces (for example {@link org.opengis.metadata}) and uses
 * reflection for performing basic operations like comparisons and copies.
 * <p>
 * The available metadata implementations are:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.metadata.iso}:&nbsp;
 *       concrete implementation of ISO interfaces, including ISO&nbsp;19115 and ISO&nbsp;19115-2.</li>
 *
 *   <li>{@link org.geotoolkit.metadata.sql}:&nbsp;
 *       implementation of metadata interfaces backed by a SQL database. The metadata interfaces
 *       doesn't need to be ISO ones, which is why this package is not a sub-package of the ISO's
 *       one.</li>
 *
 *   <li>{@link org.geotoolkit.image.io.metadata}:&nbsp;
 *       Metadata managed by this package (it doesn't need to be a Geotk implementation) viewed
 *       as a XML tree of {@link javax.imageio.metadata.IIOMetadataNode}s. This is used for
 *       Image I/O operations.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.metadata;
