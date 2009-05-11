

package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.Operator;

public class DefaultComparisonOperators implements ComparisonOperators {

    private final Map<String,Operator> operators = new HashMap<String,Operator>();

    public DefaultComparisonOperators(Operator[] operators) {
        if(operators == null || operators.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        for(Operator op : operators){
            this.operators.put(op.getName(), op);
        }
    }

    @Override
    public Collection<Operator> getOperators() {
        return operators.values();
    }

    @Override
    public Operator getOperator(String name) {
        return operators.get(name);
    }

}
