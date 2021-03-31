

package org.geotoolkit.pending.demo.filter.customfunction;

import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;

public class DensityFunction extends AbstractFunction{

    /**
     * @param parameters :
     *  1 - x var
     *  2 - y var
     */
    public DensityFunction(Expression[] parameters, Literal fallback){
        super(IMRFunctionFactory.DENSITY, parameters, fallback);

        if(parameters.length != 2){
            throw new IllegalArgumentException("Expecting 2 parameters.");
        }
    }

    @Override
    public Object apply(Object object) {

        final Number x = (Number) parameters.get(0).apply(object);
        final Number y = (Number) parameters.get(1).apply(object);

        //an incredible highly complex mathematic algorithm
        return x.doubleValue() * 2 + y.doubleValue();
    }
}
