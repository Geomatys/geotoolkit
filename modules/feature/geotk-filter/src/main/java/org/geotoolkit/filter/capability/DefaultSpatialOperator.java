

package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.List;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialOperator;

public class DefaultSpatialOperator extends DefaultOperator implements SpatialOperator{

    private final List<GeometryOperand> operands;

    public DefaultSpatialOperator(String name, GeometryOperand[] operands) {
        super(name);
        
        if(operands == null || operands.length == 0){
            throw new IllegalArgumentException("Operands list can not be null or empty");
        }

        //use a threadsafe optimized immutable list
        this.operands = UnmodifiableArrayList.wrap(operands.clone());
    }

    @Override
    public Collection<GeometryOperand> getGeometryOperands() {
        return operands;
    }

}
