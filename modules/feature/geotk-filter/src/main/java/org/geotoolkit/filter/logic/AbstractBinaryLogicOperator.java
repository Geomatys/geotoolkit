

package org.geotoolkit.filter.logic;

import java.util.List;

import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;

public abstract class AbstractBinaryLogicOperator implements BinaryLogicOperator{

    protected final List<Filter> filters;

    public AbstractBinaryLogicOperator(List<Filter> filters) {
        if(filters == null || filters.isEmpty()){
            throw new IllegalArgumentException("Filters list can not be null or empty");
        }

        //use a threadsafe optimized immutable list
        this.filters = UnmodifiableArrayList.wrap(filters.toArray(new Filter[filters.size()]));
    }

    public AbstractBinaryLogicOperator(Filter filter1, Filter filter2) {
        if(filter1 == null || filter2 == null){
            throw new IllegalArgumentException("Filters can not be null");
        }

        //use a threadsafe optimized immutable list
        this.filters = UnmodifiableArrayList.wrap(new Filter[]{filter1,filter2});
    }

    @Override
    public List<Filter> getChildren() {
        return filters;
    }

}
