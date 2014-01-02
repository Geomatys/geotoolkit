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

/**
 * Root package for various metadata implementations. For a global overview of metadata in Geotk,
 * see the <a href="{@docRoot}/../modules/metadata/index.html">Metadata page on the project web site</a>.
 * <p>
 * This root package can work with different {@linkplain org.geotoolkit.metadata.MetadataStandard
 * metadata standards}, not just ISO 19115. In this package, a metadata standard is defined by a
 * collection of Java interfaces defined in a specific package and its sub-packages. For example
 * the {@linkplain org.geotoolkit.metadata.MetadataStandard#ISO_19115 ISO 19115} standard is
 * defined by the interfaces in the {@link org.opengis.metadata} package and sub-packages.
 * This {@code org.geotoolkit.metadata} package uses Java reflection for performing basic
 * operations like comparisons and copies.
 * <p>
 * The available metadata implementations are:
 * <p>
 * <table>
 *   <tr valign="top"><td nowrap>&#8226; {@link org.apache.sis.metadata.iso}:&nbsp;</td>
 *   <td>concrete implementation of ISO interfaces, including ISO&nbsp;19115 and ISO&nbsp;19115-2.</td></tr>
 *
 *   <tr valign="top"><td nowrap>&#8226; {@link org.geotoolkit.metadata.sql}:&nbsp;</td>
 *   <td>implementation of metadata interfaces backed by a SQL database. The metadata interfaces
 *       doesn't need to be ISO ones, which is why this package is not a sub-package of the ISO's
 *       one.</td></tr>
 *
 *   <tr valign="top"><td nowrap>&#8226; {@link org.geotoolkit.image.io.metadata}:&nbsp;</td>
 *   <td>Metadata managed by this package (it doesn't need to be a Geotk implementation) viewed
 *       as a XML tree of {@link javax.imageio.metadata.IIOMetadataNode}s. This is used for
 *       Image I/O operations.</td></tr>
 * </table>
 * <p>
 * All metadata can be view {@linkplain org.geotoolkit.metadata.AbstractMetadata#asMap() as a map}
 * for use with Java collections, or {@linkplain org.geotoolkit.metadata.AbstractMetadata#asTree()
 * as a tree} for use in Swing or other GUI applications.
 * <p>
 * ISO 19115 metadata can be marshalled and unmarshalled in XML using the
 * {@link org.apache.sis.xml.XML} convenience methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.metadata;
