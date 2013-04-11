/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.apache.sis.util.Version;


/**
 * Interface of versioned objects or services.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public interface Versioned {
    /**
     * Returns the version of the implemented object or service.
     *
     * @return The object or service version.
     */
    Version getVersion();
}
