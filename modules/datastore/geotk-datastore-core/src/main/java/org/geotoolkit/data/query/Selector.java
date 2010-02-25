/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.query;

import org.geotoolkit.data.session.Session;

import org.opengis.feature.type.Name;

/**
 * Define the feature provider that will be use by the query.
 * A selector reference a Session and a feature type name.
 *
 * This class is the counterpart of javax.jcr.query.qom.Selector
 * from JSR-283 (Java Content Repository 2).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Selector {

    /**
     * In the case of mutiple selector in a query, each selector must specify
     * it's session. When the query is composed of only one selector then
     * the session might be null.  
     * @return Session never null in multiple selector query.
     *         might be null in single selector query.
     */
    Session getSession();

    /**
     * Gets the name of the required feature type.
     */
    Name getFeatureTypeName();

    /**
     * Gets the selector name.
     * This name is used in filter to define from which source the
     * feature property comes from.
     *
     * @return String
     */
    String getSelectorName();

}
