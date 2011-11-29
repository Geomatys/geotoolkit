/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

/**
 * Text statements are queries express in a different form.
 * 
 * @author Johann Sorel (Geoamtys)
 * @module pending
 */
public interface TextStatement extends Source {

    /**
     * Returns the statement defined for this query.
     * Other languages may be similar to SQL, in those cases the statement
     * is the sql query.
     *
     * @return the query statement.
     */
    String getStatement();
    
    /**
     * In the case of multiple selector in a query, each selector must specify
     * it's session. When the query is composed of only one selector then
     * the session might be null.  
     * @return Session never null in multiple selector query.
     *         might be null in single selector query.
     */
    Session getSession();

}
