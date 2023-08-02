

package org.geotoolkit.filter.function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.util.iso.Names;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.capability.Argument;
import org.opengis.filter.capability.AvailableFunction;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;
import org.opengis.util.LocalName;
import org.opengis.util.TypeName;

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
    private final Set<String> names;

    public AbstractFunctionFactory(String identifier, Map<String, Class> functions) {
        this.identifier = identifier;
        this.functions = functions;
        this.names = Collections.unmodifiableSet(functions.keySet());
    }

    @Override
    public String getAuthority() {
        return "Geotk";
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Set<String> getNames() {
        return names;
    }

    @Override
    public Expression create(String name, Expression... parameters) throws IllegalArgumentException {
        return createFunction(name, null, parameters);
    }

    private Class<?> find(final String name) {
        final Class<?> type = functions.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown function name: " + name);
        }
        return type;
    }

    @Override
    public Expression createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {
        final Class clazz = find(name);
        final Constructor construct = clazz.getConstructors()[0];
        final Object[] cstParams = new Object[construct.getParameterTypes().length];
        for(int i=0;i<cstParams.length && i<parameters.length;i++){
            cstParams[i] = parameters[i];
        }

        try {
            return (Expression) construct.newInstance(cstParams);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new IllegalArgumentException("Failed to initialize function wih given parameters : "+ name + "  "+ex.getMessage(),ex);
        }
    }

    @Override
    public ParameterDescriptorGroup describeFunction(String name) throws IllegalArgumentException {
        final Class clazz = find(name);
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

        final ParameterBuilder params = new ParameterBuilder();
        for(int i=0;;i++){
            try {
                final Short key = (Short) Bundle.Keys.class.getDeclaredField(baseName+"_arg"+i).get(null);
                final InternationalString argDesc = Bundle.formatInternational(key);

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
                    args.add(params.addName("param"+(i+1)).create(Object.class, null));
                }
            }catch(Exception ex){
            }catch(Error ex){
            }
        }

        final GeneralParameterDescriptor[] cstParams = new GeneralParameterDescriptor[args.size()];
        args.toArray(cstParams);

        params.addName(name);
        params.setRemarks(description);
        try{
            return params.createGroup(cstParams);
        }catch(Exception ex){
            return new ParameterBuilder().addName(name).createGroup();
        }
    }

    @Override
    public AvailableFunction describe(final String name) {
        final ParameterDescriptorGroup p = describeFunction(name);
        return new AvailableFunction() {
            @Override
            public LocalName getName() {
                return NamedIdentifier.castOrCopy(p.getName()).tip();
            }

            @Override
            public TypeName getReturnType() {
                return Names.createTypeName(find(name));
            }

            @Override
            public List<? extends Argument> getArguments() {
                final List<GeneralParameterDescriptor> args = p.descriptors();
                return args.stream().map((a) -> new Argument() {
                    @Override
                    public LocalName getName() {
                        return NamedIdentifier.castOrCopy(a.getName()).tip();
                    }

                    @Override
                    public TypeName getValueType() {
                        return ((ParameterDescriptor<?>) a).getValueType();
                    }
                }).toList();
            }
        };
    }
}
