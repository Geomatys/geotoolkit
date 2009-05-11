
package org.geotoolkit.filter.capability;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.capability.Functions;

public class DefaultFunctions implements Functions{

    private final Map<String,FunctionName> functions = new HashMap<String, FunctionName>();

    public DefaultFunctions(FunctionName[] functions) {
        if(functions == null || functions.length == 0){
            throw new IllegalArgumentException("Functions must not be null or empty");
        }
        for(FunctionName fn : functions){
            this.functions.put(fn.getName(), fn);
        }
    }

    @Override
    public Collection<FunctionName> getFunctionNames() {
        return functions.values();
    }

    @Override
    public FunctionName getFunctionName(String name) {
        return functions.get(name);
    }

}
