

package org.geotoolkit.filter.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import org.geotoolkit.internal.simple.SimpleParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * Abstract function factory.
 * Expects Function classes to have a constructors only with Expressions.
 * Function may declare there parameters with a static variable called DESCRIPTOR instance of OperationType.
 * If the descriptor is not found a generic one will be created from the constructor number of parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AbstractFunctionFactory implements FunctionFactory{

    private final String identifier;
    private final Map<String,Class> functions;
    private final String[] names;

    public AbstractFunctionFactory(String identifier, Map<String, Class> functions) {
        this.identifier = identifier;
        this.functions = functions;
        this.names = functions.keySet().toArray(new String[0]);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String[] getNames() {
        return names;
    }

    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {
        final Class clazz = functions.get(name);
        if(clazz == null){
            throw new IllegalArgumentException("Unknowed function name : "+ name);
        }

        final Constructor construct = clazz.getConstructors()[0];
        final Object[] cstParams = new Object[construct.getParameterTypes().length];
        for(int i=0;i<cstParams.length;i++){
            cstParams[i] = parameters[i];
        }

        try {
            return (Function) construct.newInstance(cstParams);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException("Failed to initialize function wih given parameters : "+ name + "  "+ex.getMessage(),ex);
        }
    }

    @Override
    public ParameterDescriptorGroup describeFunction(String name) throws IllegalArgumentException {

        final Class clazz = functions.get(name);
        if(clazz == null){
            throw new IllegalArgumentException("Unknowed function name : "+ name);
        }

        try{
            final Constructor construct = clazz.getConstructors()[0];
            final GeneralParameterDescriptor[] cstParams = new GeneralParameterDescriptor[construct.getParameterTypes().length];
            for(int i=0;i<cstParams.length;i++){
                cstParams[i] = new SimpleParameterDescriptor(Object.class, "", "param"+(i+1));
            }
            final ParameterDescriptorGroup params = new DefaultParameterDescriptorGroup(name, cstParams);
            return params;
        }catch(Exception ex){
            return new DefaultParameterDescriptorGroup(name);
        }catch(Error ex){
            return new DefaultParameterDescriptorGroup(name);
        }
    }

}
