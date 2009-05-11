
package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.Operator;

public class DefaultOperator implements Operator{

    private final String name;

    public DefaultOperator(String name) {
        if(name == null){
            throw new NullPointerException("Operator name can not be null");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
