/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.filter.binding;

import java.io.Serializable;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.parameter.Parameters;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Binding for Parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ParameterBinding extends AbstractBinding<ParameterValueGroup> implements Serializable{

    public ParameterBinding() {
        super(ParameterValueGroup.class, 0);
    }

    @Override
    public boolean support(String xpath) {
        return true;
    }

    @Override
    public <T> T get(ParameterValueGroup candidate, String xpath, Class<T> target) throws IllegalArgumentException {
        Object value = null;
        try{
            GeneralParameterValue param = Parameters.getParameterOrGroup(candidate, xpath);
            if(param instanceof ParameterValue){
                value = ((ParameterValue)param).getValue();
            }else if(param instanceof ParameterValueGroup){
                value = ((ParameterValueGroup)param).values();
            }
        }catch(ParameterNotFoundException ex){
            //we are laxiste, we don't consider
        }
        if (target == null) {
            return (T) value; // TODO - unsafe cast!!!
        }
        return ObjectConverters.convert(value, target);
    }

    @Override
    public void set(ParameterValueGroup candidate, String xpath, Object value) throws IllegalArgumentException {
        if(candidate == null) return;

        try{
            GeneralParameterValue param = Parameters.getParameterOrGroup(candidate, xpath);
            if(param instanceof ParameterValue){
                final ParameterValue pm = (ParameterValue) param;
                pm.setValue(ObjectConverters.convert(value, pm.getDescriptor().getValueClass()));
            }else if(param instanceof ParameterValueGroup){
                //how to map this ?
            }
        }catch(ParameterNotFoundException ex){
            //we are laxiste, we don't consider
        }
    }

}
