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
 * {@linkplain org.opengis.referencing.operation.MathTransform Math transform} implementations.
 * This package does not include map projections, which are a special kind of transforms defined
 * in their own {@linkplain org.geotoolkit.referencing.operation.projection projection} package.
 * <p>
 * Users wanting to know more about the available transforms (including map projections) and their
 * parameters should look at the {@linkplain org.geotoolkit.referencing.operation.provider provider}
 * package.
 *
 * {@section Grid data}
 *
 * Some transforms are backed by a grid of data. This is the case for example of the
 * {@linkplain org.geotoolkit.referencing.operation.transform.NadconTransform NADCON transform},
 * which implements a datum shift for the United States. The data may need to be downloaded
 * separatly. To download the data, see the
 * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
 *
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @see org.geotoolkit.referencing.operation.provider
 *
 * @since 1.2
 * @module
 */
package org.geotoolkit.referencing.operation.transform;
