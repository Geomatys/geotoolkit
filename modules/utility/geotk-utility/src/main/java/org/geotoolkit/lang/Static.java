/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * Annotates classes that contain only static utility methods. This annotation is for documentation
 * purpose only. The "<cite>Use</cite>" javadoc link above gives a list of all annotated public
 * classes. The list below summarizes the main ones (not all utility methods are mentioned. See
 * the javadoc of individual classes for more details).
 *
 * <ul>
 *   <li><p>Primitives</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.util.Utilities}: Convenience {@code equals} and {@code hashCode} methods.</li>
 *     <li>{@link org.geotoolkit.util.Characters}: Find subscript and superscript digit characters.</li>
 *     <li>{@link org.geotoolkit.util.converter.Classes}: Conversions between different kind of
 *         {@link java.lang.Number}.</li>
 *     <li>{@link org.geotoolkit.util.XCollections}: Additions to the {@link java.util.Collections} methods.</li>
 *   </ul>
 *   <li><p>Mathematics and units of measurement</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.math.XMath}: Additions to the {@link java.lang.Math} methods.</li>
 *     <li>{@link org.geotoolkit.measure.Units}: Test if a {@linkplain javax.measure.unit.Unit unit}
 *         is angular, linear or temporal.</li>
 *     <li>{@link org.geotoolkit.referencing.operation.matrix.XAffineTransform} Get the scale factors
 *         or rotation angle from an {@linkplain java.awt.geom.AffineTransform affine transform}.</li>
 *     <li>{@link org.geotoolkit.display.shape.ShapeUtilities}: Calculate geometric
 *         properties (intersections, distances, control points, <i>etc.</i>) related to
 *         {@link java.awt.geom.Line2D} and {@link java.awt.geom.Point2D} objects.</li>
 *   </ul>
 *   <li><p>Structures (trees, collections, arrays, parameters)</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.util.Comparators}: Predefined comparators for comparing collections
 *         as a whole.</li>
 *     <li>{@link org.geotoolkit.util.Ranks}: Sorts elements in an array while remembering their ranks.</li>
 *     <li>{@link org.geotoolkit.util.XArrays}: Insert or remove elements in the middle of arrays.</li>
 *     <li>{@link org.geotoolkit.gui.swing.tree.Trees}: Parse and format trees in text format.
 *         Convert a XML tree into a Swing tree.</li>
 *     <li>{@link org.geotoolkit.parameter.Parameters}: Search and set a
 *         {@linkplain org.opengis.parameter.ParameterValue parameter value} from its
 *         {@linkplain org.opengis.parameter.ParameterDescriptor descriptor}.</li>
 *   </ul>
 *   <li><p>Metadata and referencing (except I/O)</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.metadata.iso.citation.Citations}: Get a predefined constant from
 *         a name. Test if the identifiers of two citations match.</li>
 *     <li>{@link org.geotoolkit.referencing.CRS}: Create
 *         {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference
 *         Systems} (CRS) from authority codes. Find {@linkplain org.opengis.referencing.operation.MathTransform
 *         math transforms} between two CRS. Transform {@linkplain org.opengis.geometry.Envelope envelopes}.</li>
 *   </ul>
 *   <li><p>Images (except I/O)</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.metadata.iso.spatial.PixelTranslation}: The translation to apply
 *         for different values of {@link org.opengis.metadata.spatial.PixelOrientation}.</li>
 *     <li>{@link org.geotoolkit.image.SampleModels}: Get the <cite>pixel stride</cite> and
 *         <cite>scan line stride</cite> of a {@linkplain java.awt.image.SampleModel sample model}.</li>
 *     <li>{@link org.geotoolkit.coverage.TypeMap}: Map {@link org.opengis.coverage.SampleDimensionType}
 *         to {@link java.awt.image.DataBuffer} types, and ranges of values to compatible sample and
 *         color models.</li>
 *     <li>{@link org.geotoolkit.image.jai.Registry}: Control whatever native plugins are allowed
 *         for a given JAI operation or image format.</li>
 *   </ul>
 *   <li><p>Input / Output (including CRS, XML, images)</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.image.io.XImageIO}: Get an {@link javax.imageio.ImageReader} for
 *         the given input, or an {@link javax.imageio.ImageWriter} for the given image.</li>
 *     <li>{@link org.geotoolkit.xml.XML}: Marshall or unmarshall ISO 19115 objects.</li>
 *     <li>{@link org.geotoolkit.io.wkt.PrjFiles}: Read/write {@code ".prj"} files
 *         to/from {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate
 *         Reference System} objects.</li>
 *     <li>{@link org.geotoolkit.io.LineReaders}: Read lines from a {@link java.io.BufferedReader},
 *         {@link java.io.RandomAccessFile} or multi-lines {@link java.lang.String}.</li>
 *   </ul>
 *   <li><p>Loggings and exceptions</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.util.logging.Logging}: Get a {@linkplain java.util.logging.Logger logger},
 *         which may be a wrapper around <cite>Apache Commons Logging</cite>.</li>
 *     <li>{@link org.geotoolkit.util.Exceptions}: Paints a stack trace in a {@link java.awt.Graphics2D}.</li>
 *   </ul>
 *   <li><p>Factories</p></li>
 *   <ul>
 *     <li>{@link org.geotoolkit.referencing.operation.matrix.MatrixFactory}: Create matrix
 *         of various size.</li>
 *     <li>{@link org.geotoolkit.factory.FactoryFinder}: Access the application's
 *         {@linkplain org.geotoolkit.factory.Factory factory} implementations.</li>
 *     <li>{@link org.geotoolkit.factory.AuthorityFactoryFinder}: Access the application's
 *         {@linkplain org.opengis.referencing.AuthorityFactory authority factory} implementations.</li>
 *     <li>{@link org.geotoolkit.factory.Factories}: Allow usage of alternative plugin systems
 *         (OSGi, <i>etc.</i>).</li>
 *   </ul>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Static {
}
