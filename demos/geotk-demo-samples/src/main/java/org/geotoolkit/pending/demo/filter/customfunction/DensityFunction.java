

package org.geotoolkit.pending.demo.filter.customfunction;

import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.pending.demo.filter.customaccessor.Pojo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

public class DensityFunction extends AbstractFunction{

    /**
     * @param parameters :
     *  1 - x var
     *  2 - y var
     * @param fallback
     */
    public DensityFunction(Expression[] parameters, Literal fallback){
        super(IMRFunctionFactory.DENSITY, parameters, fallback);

        if(parameters.length != 2){
            throw new IllegalArgumentException("Expecting 2 parameters.");
        }

    }

    @Override
    public Object evaluate(Object object) {

        final Number x = parameters.get(0).evaluate(object, Number.class);
        final Number y = parameters.get(1).evaluate(object, Number.class);

        //an incredible highly complex mathematic algorithm
        return x.doubleValue() * 2 + y.doubleValue();
    }

}
