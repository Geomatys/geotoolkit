/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
 * Custom validators which complete the GeoAPI validations with some Geotk-specific additional checks.
 * Those validators ensures that ISO or GeoAPI restrictions apply, then checks for yet more restrictive
 * Geotk conditions. For example Geotk requires the exact same instance where GeoAPI requires only
 * instances that are {@linkplain Object#equals(Object) equal}.
 * <p>
 * Those validators are installed on a system-wide basis at
 * {@link org.geotoolkit.test.validator.Validators} class initialization time.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
package org.geotoolkit.test.validator;
