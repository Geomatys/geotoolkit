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
 * Spatial (usually geographic) informations encoded in images as metadata.
 * <p>
 * This package defines a {@linkplain org.geotoolkit.image.io.metadata.SpatialMetadataFormat spatial
 * metadata format} which is aimed image format neutral. The metadata format defines a structure for
 * a XML-like tree of nodes with elements and attributes inferred from GeoAPI interfaces and methods.
 * For example, the GeoAPI {@link org.opengis.metadata.content.ImageDescription} interface contains a
 * {@link org.opengis.metadata.content.ImageDescription#getCloudCoverPercentage() getCloudCoverPercentage()}
 * method which return a value of type {@link java.lang.Double}. They are reflected in the spatial
 * metadata format as an {@code "ImageDescription"} node with a {@code "cloudCoverPercentage"}
 * attribute of type {@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_DOUBLE}.
 * <p>
 * The metadata values are can be stored in a standard
 * {@link javax.imageio.metadata.IIOMetadata} object. However this package defines a convenience
 * subclass, {@link org.geotoolkit.image.io.metadata.SpatialMetadata}, which can instantiate
 * implementations of the ISO 19115-2 standard. This allow fetching attribute values in the
 * XML tree with simple method calls like:
 *
 * {@preformat java
 *     ImageDescription desc = metadata.getInstanceForType(ImageDescription.class);
 *     Double cloudCover = desc.getCloudCoverPercentage()
 * }
 *
 * <a name="accessor-types">{@section Attributes accessor}</a>
 * This package uses {@link org.geotoolkit.image.io.metadata.MetadataNodeAccessor} for reading and
 * writing attribute values. That accessor provides parsing and formatting convenience methods
 * for the following attribute types. Note that this restriction applies to attributes only;
 * {@linkplain javax.imageio.metadata.IIOMetadataNode#getUserObject() user object} attached
 * to elements can be of any type.
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th nowrap>&nbsp;Java type&nbsp;</th>
 *     <th nowrap>&nbsp;{@code IIOMetadataFormat} type&nbsp;</th>
 *     <th nowrap>&nbsp;{@code IIOMetadataFormat} value&nbsp;</th>
 *   </tr>
 *   <tr>
 *     <td>&nbsp;{@link java.lang.String}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link java.lang.Boolean}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_BOOLEAN DATATYPE_BOOLEAN}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ENUMERATION VALUE_ENUMERATION}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link java.lang.Integer}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_INTEGER DATATYPE_INTEGER}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link java.lang.Float}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_FLOAT DATATYPE_FLOAT}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link java.lang.Double}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_DOUBLE DATATYPE_DOUBLE}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@code String[]}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_LIST VALUE_LIST}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@code int[]}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_INTEGER DATATYPE_INTEGER}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_LIST VALUE_LIST}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@code float[]}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_FLOAT DATATYPE_FLOAT}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_LIST VALUE_LIST}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@code double[]}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_DOUBLE DATATYPE_DOUBLE}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_LIST VALUE_LIST}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.apache.sis.measure.NumberRange}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link java.util.Date}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.opengis.util.CodeList}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ENUMERATION VALUE_ENUMERATION}</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link org.opengis.metadata.citation.Citation}&nbsp;</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#DATATYPE_STRING DATATYPE_STRING}</td>
 *     <td>&nbsp;{@link javax.imageio.metadata.IIOMetadataFormat#VALUE_ARBITRARY VALUE_ARBITRARY}</td>
 *   </tr>
 * </table>
 *
 * {@section String formatting in attributes}
 * The following formatting rules apply:
 * <p>
 * <ul>
 *   <li>Numbers are formatted as in the {@linkplain java.util.Locale#US US locale}, i.e. as
 *       {@link java.lang.Integer#toString(int)} or {@link java.lang.Double#toString(double)}.</li>
 *   <li>Dates are formatted with the {@code "yyyy-MM-dd HH:mm:ss"}
 *       {@linkplain java.text.SimpleDateFormat pattern} in UTC
 *       {@linkplain java.util.TimeZone timezone}.</li>
 * </ul>
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
 *     <td nowrap>&nbsp;Collection of {@link org.opengis.coverage.grid.GridCoverage}s&nbsp;</td>
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
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
package org.geotoolkit.image.io.metadata;
