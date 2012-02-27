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
 * {@linkplain org.geotoolkit.parameter.Parameter Parameter} implementations. An explanation for this
 * package is provided in the {@linkplain org.opengis.parameter OpenGIS&reg; javadoc}. The remaining
 * discussion on this page is specific to the Geotk implementation.
 * <p>
 * The starting point is often {@link org.opengis.parameter.ParameterDescriptorGroup}.
 * Operation implementations need to defines one. Operation usages typically invoke its
 * {@link org.opengis.parameter.ParameterDescriptorGroup#createValue createValue} method
 * and fill the returned object with parameter values. This Geotk package provides the
 * following implementations:
 *
 * <ul>
 *   <li><p>{@link org.geotoolkit.parameter.DefaultParameterDescriptorGroup} for the general case.</p></li>
 *   <li><P>{@link org.geotoolkit.parameter.ImagingParameterDescriptors} for wrappers around
 *       {@linkplain javax.media.jai.ParameterListDescriptor Java Advanced Imaging's parameters}.</p></li>
 *   <li><p>{@link org.geotoolkit.parameter.MatrixParameterDescriptors} for matrix parameters,
 *       including the number of rows and columns. The total number of parameters in this group
 *       vary according the number of rows and columns.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
package org.geotoolkit.parameter;
