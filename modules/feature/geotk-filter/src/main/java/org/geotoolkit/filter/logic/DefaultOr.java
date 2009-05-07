

package org.geotoolkit.filter.logic;

import java.util.List;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Or;

public class DefaultOr extends AbstractBinaryLogicOperator implements Or {

    public DefaultOr(List<Filter> filters) {
        super(filters);
    }

    public DefaultOr(Filter filter1, Filter filter2){
        super(filter1,filter2);
    }

    @Override
    public boolean evaluate(Object object) {
        for (Filter filter : filters) {
            if (filter.evaluate(object)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
