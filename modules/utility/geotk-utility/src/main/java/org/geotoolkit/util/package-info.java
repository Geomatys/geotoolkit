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
 * Simple data objects and miscellaneous utilities. This package provides:
 *
 * <ul>
 *   <li><p>Data objects:</p>
 *   <ul>
 *     <li>{@link org.apache.sis.measure.Range} and its subclasses:
 *         {@link org.geotoolkit.util.DateRange},
 *         {@link org.apache.sis.measure.NumberRange},
 *         {@link org.apache.sis.measure.MeasurementRange}.</li>
 *
 *     <li>Various implementations of {@link org.opengis.util.InternationalString}:
 *         {@link org.apache.sis.util.iso.SimpleInternationalString},
 *         {@link org.geotoolkit.util.DefaultInternationalString},
 *         {@link org.geotoolkit.util.ResourceInternationalString}.</li>
 *   </ul></li>
 *
 *   <li><p>General purpose static methods working on:</p>
 *   <ul>
 *     <li>Primitive or basic Java types:
 *         {@link org.geotoolkit.util.Utilities},
 *         {@link org.geotoolkit.util.Characters},
 *         {@link org.geotoolkit.util.Strings}.</li>
 *     <li>Arrays:
 *         {@link org.geotoolkit.util.XArrays},
 *         {@link org.geotoolkit.util.Utilities},
 *         {@link org.geotoolkit.util.Ranks}.</li>
 *     <li>Other standard Java types:
 *         {@link org.geotoolkit.util.collection.XCollections},
 *         {@link org.geotoolkit.util.Exceptions}.</li>
 *   </ul></li>
 * </ul>
 *
 * {@section Utility methods in other packages}
 * More public static methods are defined in the packages relevant to their services, for example:
 * <p><ul>
 * <li>{@link org.geotoolkit.math.XMath} for a few additional mathematic functions</li>
 * <li>{@link org.geotoolkit.referencing.operation.matrix.XAffineTransform} for informations about affine transforms</li>
 * <li>{@link org.geotoolkit.display.shape.ShapeUtilities} for calculation with <cite>Java2D</cite> lines and curves</li>
 * <li>{@link org.geotoolkit.gui.swing.tree.Trees} for parsing and formatting <cite>Swing</cite> trees</li>
 * <li>{@link org.geotoolkit.util.converter.Classes} for conversions of numeric types</li>
 * <li>{@link org.geotoolkit.referencing.CRS} for methods related to <cite>Coordinate Reference Systems</cite></li>
 * <li>{@link org.geotoolkit.factory.FactoryFinder} for factories of CRS and other objects</li>
 * </ul><p>
 * See the {@link org.geotoolkit.lang.Static} class javadoc for a more complete list.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.17
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.util;
