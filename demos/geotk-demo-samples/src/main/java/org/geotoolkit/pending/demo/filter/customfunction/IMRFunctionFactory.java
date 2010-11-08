

package org.geotoolkit.pending.demo.filter.customfunction;

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public class IMRFunctionFactory implements FunctionFactory{

    public static final String DENSITY = "density";

    @Override
    public String[] getNames() {
        return new String[]{DENSITY};
    }

    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {
        if(DENSITY.equalsIgnoreCase(name)) return new DensityFunction(parameters, fallback);
        else throw new IllegalArgumentException("Unknowned function : " + name);
    }

}
