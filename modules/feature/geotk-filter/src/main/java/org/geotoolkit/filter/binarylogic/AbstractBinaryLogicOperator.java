/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter.binarylogic;

import java.util.List;

import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;

/**
 * Immutable abstract binary logic operator.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractBinaryLogicOperator implements BinaryLogicOperator{

    protected final Filter[] filterArray;
    protected final List<Filter> filters;

    public AbstractBinaryLogicOperator(List<Filter> filters) {
        if(filters == null || filters.isEmpty()){
            throw new IllegalArgumentException("Filters list can not be null or empty");
        }

        //use a threadsafe optimized immutable list
        this.filterArray = filters.toArray(new Filter[filters.size()]);
        this.filters = UnmodifiableArrayList.wrap(filterArray);
    }

    public AbstractBinaryLogicOperator(Filter filter1, Filter filter2) {
        if(filter1 == null || filter2 == null){
            throw new IllegalArgumentException("Filters can not be null");
        }

        //use a threadsafe optimized immutable list
        this.filterArray = new Filter[]{filter1,filter2};
        this.filters = UnmodifiableArrayList.wrap(filterArray);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Filter> getChildren() {
        return filters;
    }

}
