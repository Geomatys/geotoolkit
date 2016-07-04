

package org.geotoolkit.filter.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.internal.simple.SimpleParameterDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

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

        return getDescription(name,clazz);
    }

    private static ParameterDescriptorGroup getDescription(String name, Class clazz){
        final String baseName = clazz.getName().replace('.', '_');

        InternationalString description;
        final List<GeneralParameterDescriptor> args = new ArrayList<>();
        try {
            Short key = (Short) Bundle.Keys.class.getDeclaredField(baseName+"_description").get(null);
            description = Bundle.formatInternational(key);
        } catch (Exception ex) {
            description = new SimpleInternationalString(clazz.getSimpleName());
        }

        for(int i=0;;i++){
            try {
                final Short key = (Short) Bundle.Keys.class.getDeclaredField(baseName+"_arg"+i).get(null);
                final InternationalString argDesc = Bundle.formatInternational(key);

                final ParameterBuilder params = new ParameterBuilder();
                params.addName("arg"+i);
                params.setRemarks(argDesc);
                args.add(params.create(Object.class, null));
            } catch (Exception ex) {
                break;
            }
        }
        if(args.isEmpty()){
            //use reflection
            try{
                final Constructor construct = clazz.getConstructors()[0];
                final GeneralParameterDescriptor[] cstParams = new GeneralParameterDescriptor[construct.getParameterTypes().length];
                for(int i=0;i<cstParams.length;i++){
                    args.add(new SimpleParameterDescriptor(Object.class, "", "param"+(i+1)));
                }
            }catch(Exception ex){
            }catch(Error ex){
            }
        }

        final GeneralParameterDescriptor[] cstParams = new GeneralParameterDescriptor[args.size()];
        args.toArray(cstParams);

        final ParameterBuilder params = new ParameterBuilder();
        params.addName(name);
        params.setRemarks(description);
        try{
            return params.createGroup(cstParams);
        }catch(Exception ex){
            return new ParameterBuilder().addName(name).createGroup();
        }
    }


}
