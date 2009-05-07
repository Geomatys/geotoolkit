

package org.geotoolkit.filter;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Not;

public class DefaultNot implements Not{

    private final Filter filter;

    public DefaultNot(Filter filter) {
        if(filter == null){
            throw new NullPointerException("Fitler can not be null");
        }
        this.filter = filter;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public boolean evaluate(Object object) {
        return !filter.evaluate(object);
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
