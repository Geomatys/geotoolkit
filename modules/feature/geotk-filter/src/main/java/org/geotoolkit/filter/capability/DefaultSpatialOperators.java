

package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.opengis.filter.capability.SpatialOperator;
import org.opengis.filter.capability.SpatialOperators;

public class DefaultSpatialOperators implements SpatialOperators {

    private final Map<String,SpatialOperator> operators = new HashMap<String, SpatialOperator>();

    public DefaultSpatialOperators(SpatialOperator[] operators) {
        if(operators == null || operators.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        for(SpatialOperator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    @Override
    public Collection<SpatialOperator> getOperators() {
        return operators.values();
    }

    @Override
    public SpatialOperator getOperator(String name) {
        return operators.get(name);
    }

}
