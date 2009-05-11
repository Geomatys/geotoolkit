

package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.List;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.GeometryOperand;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperators;

public class DefaultSpatialCapabilities implements SpatialCapabilities{

    private final List<GeometryOperand> operands;
    private final SpatialOperators operators;

    public DefaultSpatialCapabilities(GeometryOperand[] operands, SpatialOperators operators) {
        if(operands == null || operands.length == 0){
            throw new IllegalArgumentException("Operands must not be null or empty");
        }
        if(operators == null){
            throw new NullPointerException("SpatialOperators can not be null");
        }

        this.operands = UnmodifiableArrayList.wrap(operands);
        this.operators = operators;
    }

    @Override
    public Collection<GeometryOperand> getGeometryOperands() {
        return operands;
    }

    @Override
    public SpatialOperators getSpatialOperators() {
        return operators;
    }

}
