

package org.geotoolkit.filter.sort;

import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

public class DefaultSortBy implements SortBy{

    private final PropertyName property;
    private final SortOrder order;

    public DefaultSortBy(PropertyName property, SortOrder order) {
        if(property == null || order == null){
            throw new NullPointerException("Property and sort order can not be null.");
        }
        this.property = property;
        this.order = order;
    }

    @Override
    public PropertyName getPropertyName() {
        return property;
    }

    @Override
    public SortOrder getSortOrder() {
        return order;
    }

}
