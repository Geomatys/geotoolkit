

package org.geotoolkit.filter.logic;

import java.util.List;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;

public class DefaultAnd extends AbstractBinaryLogicOperator implements And {

    public DefaultAnd(List<Filter> filters) {
        super(filters);
    }

    public DefaultAnd(Filter filter1, Filter filter2){
        super(filter1,filter2);
    }

    @Override
    public boolean evaluate(Object object) {
        for (final Filter filter : filters) {
            if(!filter.evaluate(object)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
