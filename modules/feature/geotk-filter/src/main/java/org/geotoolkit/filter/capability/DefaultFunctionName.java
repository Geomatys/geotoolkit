

package org.geotoolkit.filter.capability;

import java.util.List;
import org.opengis.filter.capability.FunctionName;

public class DefaultFunctionName implements FunctionName{

    private final String name;
    private final List<String> argNames;
    private final int size;

    public DefaultFunctionName(String name, List<String> argNames, int size) {
        this.name = name;
        this.argNames = argNames;
        this.size = size;
    }

    @Override
    public int getArgumentCount() {
        return size;
    }

    @Override
    public List<String> getArgumentNames() {
        return argNames;
    }

    @Override
    public String getName() {
        return name;
    }

}
