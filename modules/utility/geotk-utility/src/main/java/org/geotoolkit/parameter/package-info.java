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
 * {@linkplain org.geotoolkit.parameter.DefaultParameterDescriptor Parameter descriptor}
 * and {@linkplain org.geotoolkit.parameter.Parameter parameter value} implementations. An explanation
 * for this package is provided in the {@linkplain org.opengis.parameter OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 * <p>
 * The starting point is often {@link org.geotoolkit.parameter.DefaultParameterDescriptorGroup}.
 * Operation implementations need to defines one. The following example creates a group of two
 * parameters. The first parameter accepts integers ranging from 0 to 3 inclusive, with a default
 * value of 2. The second parameter accepts real numbers ranging from 0 to 100 kilometres inclusive,
 * with no default value:
 *
 * {@preformat java
 *   // Creates the group of parameters named "MyOperation".
 *   ParameterDescriptorGroup myOperation = new DefaultParameterDescriptorGroup("MyOperation",
 *           DefaultParameterDescriptor.create("dimension", 2, 0, 3),
 *           DefaultParameterDescriptor.create("distance", Double.NaN, 0, 100, Units.KILOMETRE));
 * }
 *
 * Operation usages typically invoke the
 * {@link org.opengis.parameter.ParameterDescriptorGroup#createValue()} method on the above
 * {@code parameters} instance, and fill the returned object with parameter values. Example:
 *
 * {@preformat java
 *     ParameterValueGroup group = myOperation.createValue();
 *     group.parameter("dimension").setValue(3);
 *     group.parameter("distance").setValue(200.0, Units.METRE);
 * }
 *
 * <p>This Geotk package provides the following implementations:</p>
 *
 * <ul>
 *   <li>{@link org.geotoolkit.parameter.DefaultParameterDescriptorGroup} for the general case.</li>
 *   <li>{@link org.geotoolkit.parameter.MatrixParameterDescriptors} for matrix parameters,
 *       including the number of rows and columns. The total number of parameters in this group
 *       vary according the number of rows and columns.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@Deprecated
package org.geotoolkit.parameter;
