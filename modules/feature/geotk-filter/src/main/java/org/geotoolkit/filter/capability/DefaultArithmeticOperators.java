

package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.Functions;

public class DefaultArithmeticOperators implements ArithmeticOperators {

    private final boolean simple;
    private final Functions functions;

    public DefaultArithmeticOperators(boolean simple, Functions functions) {
        if(functions == null){
            throw new NullPointerException("Functions can not be null");
        }
        this.simple = simple;
        this.functions = functions;
    }

    @Override
    public boolean hasSimpleArithmetic() {
        return simple;
    }

    @Override
    public Functions getFunctions() {
        return functions;
    }

}
