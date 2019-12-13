

package org.geotoolkit.pending.demo.filter.customfunction;

import java.util.Collections;
import java.util.Set;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.parameter.ParameterDescriptorGroup;

public class IMRFunctionFactory implements FunctionFactory{

    public static final String DENSITY = "density";
    private static final ParameterDescriptorGroup DESC = new ParameterBuilder().addName("density").createGroup();

    /**
     * {@inheritDoc }
     */
    @Override
    public String getIdentifier() {
        return "imr";
    }

    @Override
    public Set<String> getNames() {
        return Collections.singleton(DENSITY);
    }

    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {
        if(DENSITY.equalsIgnoreCase(name)) return new DensityFunction(parameters, fallback);
        else throw new IllegalArgumentException("Unknowned function : " + name);
    }

    @Override
    public ParameterDescriptorGroup describeFunction(String name) throws IllegalArgumentException {
        return DESC;
    }

    @Override
    public Function create(String name, Expression... parameters) throws IllegalArgumentException {
        if(DENSITY.equalsIgnoreCase(name)) return new DensityFunction(parameters, null);
        else throw new IllegalArgumentException("Unknowned function : " + name);
    }

}
