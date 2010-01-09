/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
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

/**
 * Helper classes for creating {@linkplain org.opengis.referencing.operation.MathTransform
 * math transforms}. Most of those convenience classes uses control points common to two data
 * sets to derive empirically the transformation parameters needed to convert positions between
 * the coordinate systems of the two data sets.
 * <p>
 * Current implementation is limited to two dimensions. The methods, however, are generic and
 * could be expanded to three dimensions someday. At that time, we will probably make minor
 * changes to the API.
 * <p>
 * The package consists of two types of convenience classes: the various builder classes
 * which use a set of individual control points to obtain the conversion and the
 * {@link org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper} class
 * which derives the conversion from a grid envelope to a georeferenced Envelope.
 * <p>
 * The builder classes should be used by users who have two data sets that are known to share
 * certain common points but who currently do not line up. This could be the case, for example,
 * if a user has two data sets describing the same region but one of these has an unknown
 * coordinate referencing system. In this situation, there is no way to convert coordinate
 * positions between the two data sets. However, if the user can identify a series of positions
 * coupled in each data set, a Builder can calculate an empirical conversion between the two
 * data sets. The different Builder classes use different mathematical approaches to obtain the
 * empirical estimate.
 * <p>
 * The {@code GridToEnvelopeMapper} should be used by users who have a grid, such as an image,
 * which is not georeferenced but the user knows the grid is aligned in one of the four cardinal
 * directions and the user can identify the outer georeferenced envelope of the grid. The Mapper
 * class can then calculate an empirical conversion object to map positions in the image
 * coordinate system to georeferenced positions.
 * <p>
 * The builder classes require a matched set of known positions, one from a "source" data set
 * and another from a "target" data set; the builder will then provide a structure which contains
 * a conversion object to transform positions from the "source" coordinate system to the "target"
 * coordinate system. The builders require a list of
 * {@linkplain org.geotoolkit.referencing.operation.builder.MappedPosition MappedPosition}
 * objects which are associations of a
 * {@linkplain org.opengis.referencing.DirectPosition DirectPosition} in the
 * "source" data set with another DirectPosition in the "target" data set. The
 * {@linkplain org.geotoolkit.referencing.operation.builder.MathTransformBuilder#getTransformation()
 * getTransformation() method} in the builder can then be used to provide a
 * {@linkplain org.opengis.referencing.operation.Transformation Transformation}
 * object from which the user can obtain the
 * {@linkplain org.opengis.referencing.operation.MathTransform MathTransform}
 * to use for conversion operations.
 * <p>
 * Different builders use different mathematical approaches for obtaining the
 * empirical estimate of the conversion parameters. The builders are:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.builder.ProjectiveTransformBuilder}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.builder.AffineTransformBuilder}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.builder.SimilarTransformBuilder}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.builder.BursaWolfTransformBuilder}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.builder.RubberSheetBuilder}</li>
 * </ul>
 * <p>
 * with the mathematical details of each estimation procedure explained in the documentation
 * of the builder class itself. The first four of these use a least squares estimation method
 * in which, if the system is over-determined by having more than the minimum number of control
 * points necessary to derive the estimate, the best matching parameter estimate will be obtained
 * by minimising the sum of the squared distances to the points. The Rubber Sheet algorithm uses
 * a linear interpolation between the various control points.
 *
 * @author Jan Jezek (UWB)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
package org.geotoolkit.referencing.operation.builder;
