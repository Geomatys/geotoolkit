/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
 * Utility classes which enable dynamic binding to factory implementations at runtime. Because Geotk
 * core API consists mostly of interfaces (including <A HREF="http://www.geoapi.org">GeoAPI</A>),
 * factories play a role in how developers use the API. Although the interfaces that are declared in GeoAPI
 * are implemented in various Geotk packages, they should not be used directly. Instead they should be
 * obtained through factories.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
package org.geotoolkit.factory;
