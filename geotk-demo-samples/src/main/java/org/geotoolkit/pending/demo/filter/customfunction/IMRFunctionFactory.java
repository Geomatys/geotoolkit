

package org.geotoolkit.pending.demo.filter.customfunction;

import java.util.Collections;
import java.util.Set;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.parameter.ParameterDescriptorGroup;

public class IMRFunctionFactory implements FunctionFactory{

    public static final String DENSITY = "density";
    private static final ParameterDescriptorGroup DESC = new ParameterBuilder().addName("density").createGroup();

    @Override
    public String getAuthority() {
        return "Demo";
    }

    @Override
    public String getIdentifier() {
        return "imr";
    }

    @Override
    public Set<String> getNames() {
        return Collections.singleton(DENSITY);
    }

    @Override
    public Expression createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {
        if(DENSITY.equalsIgnoreCase(name)) return new DensityFunction(parameters, fallback);
        else throw new IllegalArgumentException("Unknowned function : " + name);
    }

    @Override
    public ParameterDescriptorGroup describeFunction(String name) throws IllegalArgumentException {
        return DESC;
    }

    @Override
    public Expression create(String name, Expression... parameters) throws IllegalArgumentException {
        if(DENSITY.equalsIgnoreCase(name)) return new DensityFunction(parameters, null);
        else throw new IllegalArgumentException("Unknowned function : " + name);
    }
}
