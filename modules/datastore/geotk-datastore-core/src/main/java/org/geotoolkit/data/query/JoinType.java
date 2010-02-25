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

/**
 * Define join type, similar to SQL "inner join".
 *
 * This class is the counterpart of javax.jcr.query.qom.QueryObjectModelConstants.JCR_JOIN_TYPE_*
 * from JSR-283 (Java Content Repository 2).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public enum JoinType {
    /**
     * Both side must have a value to be included.
     */
    INNER,

    /**
     * A least left side must have a value to be included.
     */
    LEFT_OUTER,

    /**
     * A least right side must have a value to be included.
     */
    RIGHT_OUTER
}
