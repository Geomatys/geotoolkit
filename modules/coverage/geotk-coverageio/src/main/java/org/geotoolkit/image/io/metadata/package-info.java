/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
 * This package defines a {@linkplain org.geotoolkit.image.io.metadata.SpatialMetadataFormat spatial
 * metadata format} which is aimed image format neutral. The metadata format defines a structure for
 * a tree of nodes in a way similar to the way methods are defined in GeoAPI interfaces. For example
 * it defines a {@code "CoordinateReferenceSystem"} node with {@code "CoordinateSystem"} and
 * {@code "Datum"} child nodes.
 * <p>
 * The {@link org.geotoolkit.image.io.metadata.SpatialMetadata} class contains convenience methods
 * for encoding metatadata. Metadata are usually given as {@link java.lang.String} or {@code double}
 * attributes only, but image readers can optionaly attach fully constructed GeoAPI objects if they
 * wish. If only {@link java.lang.String} and {@code double} are used, then the duty to create GeoAPI
 * objects from them incomb to the {@link org.geotoolkit.coverage.io} package.
 *
 * {@section Relationship with the ISO-19129 standard}
 * The ISO 19129 standard (<cite>Geographic information — Imagery, gridded and coverage data
 * framework</cite>) defines the metadata that are expected to exist in every coverage format.
 * The table below gives the relationship between ISO 19129 constructs and Java or GeoAPI. Not
 * all ISO constructs are implemented; see {@link org.geotoolkit.image.io.metadata.SpatialMetadataFormat}
 * for the list of constructs available in Geotk.
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th>ISO 19129 construct</th>
 *     <th>Java or GeoAPI class</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Transmittal&nbsp;</code></td>
 *     <td nowrap>&nbsp;Input/output of <code>javax.imageio</code>&nbsp;</td>
 *     <td>&nbsp;The entity used in the encoded exchange format to carry all, part of, or several data sets.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DataSet&nbsp;</code></td>
 *     <td nowrap>&nbsp;Collection of <code>RenderedImage</code>s&nbsp;</td>
 *     <td>&nbsp;An identifiable collection of data that can be represented in an exchange format or stored on a storage media.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Collection&nbsp;</code></td>
 *     <td nowrap>&nbsp;Collection of <code>Coverage</code>s&nbsp;</td>
 *     <td>&nbsp;A collection of <code>IF_CoverageData</code> and associated metadata.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_CollectionMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>Metadata</code> in <code>IIOMetadata</code>&nbsp;</td>
 *     <td>&nbsp;A set of collection metadata that describes the data product as represented in the collection.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DiscoveryMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>Metadata.getIdentificationInfo()</code>&nbsp;</td>
 *     <td>&nbsp;A set of discovery metadata that describes the data set so that it can be accessed.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_AcquisitionMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>Metadata.getAcquisitionInformation()</code>&nbsp;</td>
 *     <td>&nbsp;A set of acquisition metadata that describes the source of the data values.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_QualityMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>Metadata.getDataQualityInfo()</code>&nbsp;</td>
 *     <td>&nbsp;A set of quality metadata that describes the quality of the data values.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_StructuralMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>RenderedImage</code> width, height and models&nbsp;</td>
 *     <td>&nbsp;A set of structural metadata that describes the structure of the coverage.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Tiling&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>RenderedImage</code> tile width & height&nbsp;</td>
 *     <td>&nbsp;Describes the tiling scheme used within the collection.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_GridCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>ContinuousQuadrilateralGridCoverage</code>&nbsp;</td>
 *     <td>&nbsp;Implements Continuous Quadrilateral Grid Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_TINCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>TINCoverage</code>&nbsp;</td>
 *     <td>&nbsp;Implements TIN Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_PointSetCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>PointSetCoverage</code>&nbsp;</td>
 *     <td>&nbsp;Implements Point Set Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DiscreteSurfaceCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;<code>DiscreteSurfaceCoverage</code>&nbsp;</td>
 *     <td>&nbsp;Implements Discrete Surface Coverage from ISO 19123.</td>
 *   </tr>
 *  </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.05
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.image.io.metadata;
