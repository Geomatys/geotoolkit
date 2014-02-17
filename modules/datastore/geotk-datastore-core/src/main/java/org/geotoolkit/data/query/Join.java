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

import org.opengis.filter.PropertyIsEqualTo;

/**
 * Define the feature provider that will be use by the query.
 * A join source define a relation between two other sources by a condition and
 * a type.
 *
 * This class is the counterpart of javax.jcr.query.qom.Join
 * from JSR-283 (Java Content Repository 2).
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Join extends Source{

    /**
     * Gets the join condition.
     * @return Filter
     */
    PropertyIsEqualTo getJoinCondition();

    /**
     * Gets the join type.
     */
    JoinType getJoinType();

    /**
     * Gets the left feature source.
     */
    Source getLeft();

    /**
     * Gets the right feature source.
     */
    Source getRight();

}
