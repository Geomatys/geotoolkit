/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
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
 * {@linkplain org.opengis.referencing.operation.Matrix} implementations on top of the
 * {@link javax.vecmath} package. Matrix of arbitrary size are supported, but the most
 * common ones are those that are used for <cite>affine transforms</cite>. In the two
 * dimensional case, the matrix for an affine transform is:
 * <p>
 * <center><img src="doc-files/AffineTransform.png"></center>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.16
 *
 * @since 2.2
 * @module
 */
package org.geotoolkit.referencing.operation.matrix;
