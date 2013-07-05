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
package org.geotoolkit.lang;


/**
 * Parent of classes that contain only static utility methods. This parent is for documentation
 * purpose only. The list below summarizes the main utility classes (not all utility methods are
 * mentioned. See the javadoc of individual classes for more details).
 *
 * <table>
 * <tr><th colspan="2" bgcolor="lightblue">Primitives and classes</th></tr>
 * <tr><td>{@link org.geotoolkit.util.Utilities}</td>
 *     <td>Convenience {@code equals} and {@code hashCode} methods.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.Characters}</td>
 *     <td>Find subscript and superscript digit characters.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.converter.Classes}</td>
 *     <td>Conversions between different kind of {@link java.lang.Number}.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.GeoAPI}</td>
 *     <td>Mapping between ISO identifiers and GeoAPI types.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Mathematics and units of measurement</th></tr>
 * <tr><td>{@link org.geotoolkit.math.XMath}</td>
 *     <td>Additions to the {@link java.lang.Math} methods.</td></tr>
 * <tr><td>{@link org.geotoolkit.measure.Units}</td>
 *     <td>Test if a {@linkplain javax.measure.unit.Unit unit} is angular, linear or temporal.</td></tr>
 * <tr><td>{@link org.geotoolkit.referencing.operation.matrix.XAffineTransform}</td>
 *     <td>Get the scale factors or rotation angle from an {@linkplain java.awt.geom.AffineTransform affine transform}.</td></tr>
 * <tr><td>{@link org.geotoolkit.display.shape.ShapeUtilities}</td>
 *     <td>Calculate geometric properties (intersections, distances, control points, <i>etc.</i>)
 *         related to {@link java.awt.geom.Line2D} and {@link java.awt.geom.Point2D} objects.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Structures (trees, collections, arrays, parameters)</th></tr>
 * <tr><td>{@link org.geotoolkit.util.collection.XCollections}</td>
 *     <td>Additions to the {@link java.util.Collections} methods.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.Ranks}</td>
 *     <td>Sorts elements in an array while remembering their ranks.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.XArrays}</td>
 *     <td>Insert or remove elements in the middle of arrays.</td></tr>
 * <tr><td>{@link org.geotoolkit.gui.swing.tree.Trees}</td>
 *     <td>Parse and format trees in text format. Convert a XML tree into a Swing tree.</td></tr>
 * <tr><td>{@link org.geotoolkit.parameter.Parameters}</td>
 *     <td>Search and set a
 *         {@linkplain org.opengis.parameter.ParameterValue parameter value} from its
 *         {@linkplain org.opengis.parameter.ParameterDescriptor descriptor}.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Metadata and referencing (except I/O)</th></tr>
 * <tr><td>{@link org.geotoolkit.metadata.iso.citation.Citations}</td>
 *     <td>Get a predefined constant from a name. Test if the identifiers of two citations match.</td></tr>
 * <tr><td>{@link org.geotoolkit.referencing.CRS}</td>
 *     <td>Create {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference
 *         Systems} (CRS) from authority codes. Find {@linkplain org.opengis.referencing.operation.MathTransform
 *         math transforms} between two CRS.</td></tr>
 * <tr><td>{@link org.geotoolkit.geometry.Envelopes}</td>
 *     <td>Transform {@linkplain org.opengis.geometry.Envelope envelopes}.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Images (except I/O)</th></tr>
 * <tr><td>{@link org.geotoolkit.metadata.iso.spatial.PixelTranslation}</td>
 *     <td>The translation to apply for different values of {@link org.opengis.metadata.spatial.PixelOrientation}.</td></tr>
 * <tr><td>{@link org.geotoolkit.image.SampleModels}</td>
 *     <td>Get the <cite>pixel stride</cite> and <cite>scan line stride</cite> of a {@linkplain java.awt.image.SampleModel sample model}.</td></tr>
 * <tr><td>{@link org.geotoolkit.coverage.TypeMap}</td>
 *     <td>Map {@link org.opengis.coverage.SampleDimensionType} to {@link java.awt.image.DataBuffer}
 *         types, and ranges of values to compatible sample and color models.</td></tr>
 * <tr><td>{@link org.geotoolkit.image.jai.Registry}</td>
 *     <td>Control whatever native plugins are allowed for a given JAI operation or image format.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Input / Output (including CRS, XML, images)</th></tr>
 * <tr><td>{@link org.geotoolkit.image.io.XImageIO}</td>
 *     <td>Get an {@link javax.imageio.ImageReader} for the given input, or an {@link javax.imageio.ImageWriter} for the given image.</td></tr>
 * <tr><td>{@link org.apache.sis.xml.XML}</td>
 *     <td>Marshall or unmarshall ISO 19115 objects.</td></tr>
 * <tr><td>{@link org.geotoolkit.io.wkt.PrjFiles}</td>
 *     <td>Read/write {@code ".prj"} files to/from {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System} objects.</td></tr>
 * <tr><td>{@link org.geotoolkit.io.LineReaders}</td>
 *     <td>Read lines from a {@link java.io.BufferedReader}, {@link java.io.RandomAccessFile} or multi-lines {@link java.lang.String}.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Loggings and exceptions</th></tr>
 * <tr><td>{@link org.geotoolkit.util.ArgumentChecks}</td>
 *     <td>Perform argument checks and throw {@link IllegalArgumentException} if needed.</td></tr>
 * <tr><td>{@link org.apache.sis.util.logging.Logging}</td>
 *     <td>Get a {@linkplain java.util.logging.Logger logger}, which may be a wrapper around <cite>Apache Commons Logging</cite>.</td></tr>
 * <tr><td>{@link org.geotoolkit.util.Exceptions}</td>
 *     <td>Paint a stack trace in a {@link java.awt.Graphics2D}.</td></tr>
 * <tr><td colspan="2"><hr></td></tr>
 *
 * <tr><th colspan="2" bgcolor="lightblue">Factories</th></tr>
 * <tr><td>{@link org.geotoolkit.referencing.operation.matrix.MatrixFactory}</td>
 *     <td>Create matrix of various size.</td></tr>
 * <tr><td>{@link org.geotoolkit.factory.FactoryFinder}</td>
 *     <td>Access the application {@linkplain org.geotoolkit.factory.Factory factory} implementations.</td></tr>
 * <tr><td>{@link org.geotoolkit.factory.AuthorityFactoryFinder}</td>
 *     <td>Access the application {@linkplain org.opengis.referencing.AuthorityFactory authority factory} implementations.</td></tr>
 * <tr><td>{@link org.geotoolkit.factory.Factories}</td>
 *     <td>Allow usage of alternative plugin systems (OSGi, <i>etc.</i>).</td></tr>
 * </ul>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 * @module
 */
public class Static {
    /**
     * Do not allow instantiation. This construction is defined only in order to allow
     * subclassing. Subclasses shall declare their own private constructor in order to
     * prevent instantiation.
     */
    protected Static() {
    }
}
