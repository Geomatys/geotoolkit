

package org.geotoolkit.filter.function;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.spi.ServiceRegistry;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public class Functions {

    private static final Map<String,FunctionFactory> FACTORIES = new HashMap<String,FunctionFactory>();

    static{
        final Iterator<FunctionFactory> factories = ServiceRegistry.lookupProviders(FunctionFactory.class);

        while(factories.hasNext()){
            final FunctionFactory ff = factories.next();
            FACTORIES.put(ff.getName(), ff);
        }

    }

    public static final Function function(String name, Literal fallback, Expression ... parameters){
        final FunctionFactory ff = FACTORIES.get(name);
        if(ff != null){
            return ff.createFunction(fallback, parameters);
        }
        return null;
    }

}
