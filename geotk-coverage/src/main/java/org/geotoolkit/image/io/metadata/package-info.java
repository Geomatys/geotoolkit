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

/**
 * Legacy framework for spatial information encoded in images as metadata.
 *
 * {@section Relationship with ISO/OGC standards}
 * The <a href="SpatialMetadataFormat.html#default-formats">default metadata formats</a>
 * defined in this package are inspired by the following material:
 * <p>
 * <ul>
 *   <li><a href="http://www.opengeospatial.org/standards/gmljp2">GML in JPEG 2000 for Geographic Imagery Encoding</a></li>
 *   <li>ISO 19115:   <cite>Geographic information — Metadata</cite></li>
 *   <li>ISO 19115-2: <cite>Geographic information — Metadata — Part 2: Extensions for imagery and gridded data</cite></li>
 *   <li>ISO 19129:   <cite>Geographic information — Imagery, gridded and coverage data framework</cite></li>
 * </ul>
 * <p>
 * The ISO 19129 standard defines the metadata that are expected to exist in every coverage format.
 * The table below gives the relationship between ISO 19129 constructs and Java or GeoAPI.
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th>ISO 19129 construct</th>
 *     <th>Java or GeoAPI class</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Transmittal&nbsp;</code></td>
 *     <td nowrap>&nbsp;Input/output of {@link javax.imageio}&nbsp;</td>
 *     <td>&nbsp;The entity used in the encoded exchange format to carry all, part of, or several data sets.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DataSet&nbsp;</code></td>
 *     <td nowrap>&nbsp;Collection of {@link java.awt.image.RenderedImage}s&nbsp;</td>
 *     <td>&nbsp;An identifiable collection of data that can be represented in an exchange format or stored on a storage media.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Collection&nbsp;</code></td>
 *     <td nowrap>&nbsp;Collection of {@link org.geotoolkit.coverage.grid.GridCoverage}s&nbsp;</td>
 *     <td>&nbsp;A collection of <code>IF_CoverageData</code> and associated metadata.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_CollectionMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.metadata.Metadata} in <code>IIOMetadata</code>&nbsp;</td>
 *     <td>&nbsp;A set of collection metadata that describes the data product as represented in the collection.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DiscoveryMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.metadata.identification.Identification} in <code>IIOMetadata</code>&nbsp;</td>
 *     <td>&nbsp;A set of discovery metadata that describes the data set so that it can be accessed.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_AcquisitionMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.metadata.acquisition.AcquisitionInformation} in <code>IIOMetadata</code>&nbsp;</td>
 *     <td>&nbsp;A set of acquisition metadata that describes the source of the data values.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_QualityMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.metadata.quality.DataQuality} in <code>IIOMetadata</code>&nbsp;</td>
 *     <td>&nbsp;A set of quality metadata that describes the quality of the data values.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_StructuralMetadata&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link java.awt.image.RenderedImage} width, height and models&nbsp;</td>
 *     <td>&nbsp;A set of structural metadata that describes the structure of the coverage.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_Tiling&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link java.awt.image.RenderedImage} tile width & height&nbsp;</td>
 *     <td>&nbsp;Describes the tiling scheme used within the collection.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_GridCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.coverage.grid.ContinuousQuadrilateralGridCoverage}&nbsp;</td>
 *     <td>&nbsp;Implements Continuous Quadrilateral Grid Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_TINCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.coverage.TinCoverage}&nbsp;</td>
 *     <td>&nbsp;Implements TIN Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_PointSetCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@code PointSetCoverage}&nbsp;</td>
 *     <td>&nbsp;Implements Point Set Coverage from ISO 19123.</td>
 *   </tr>
 *   <tr>
 *     <td nowrap><code>&nbsp;IF_DiscreteSurfaceCoverage&nbsp;</code></td>
 *     <td nowrap>&nbsp;{@link org.opengis.coverage.DiscreteSurfaceCoverage}&nbsp;</td>
 *     <td>&nbsp;Implements Discrete Surface Coverage from ISO 19123.</td>
 *   </tr>
 *  </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
package org.geotoolkit.image.io.metadata;
