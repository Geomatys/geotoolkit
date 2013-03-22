/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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

package org.geotoolkit.filter.accessor;

import java.io.Serializable;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.parameter.ParametersExt;
import org.geotoolkit.util.Converters;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Accessor for Parameters.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class ParameterAccessorFactory implements PropertyAccessorFactory{

    private static final ParameterAccessor ACCESSOR = new ParameterAccessor();

    @Override
    public PropertyAccessor createPropertyAccessor(final Class type, final String xpath, final Class target, final Hints hints) {
        if(ACCESSOR.canHandle(type, xpath, target)){
            return ACCESSOR;
        }
        return null;
    }

    @Override
    public int getPriority() {
        return -1;
    }

    private static class ParameterAccessor implements PropertyAccessor,Serializable{

        @Override
        public boolean canHandle(final Class clazz, final String xpath, final Class target) {
            return ParameterValueGroup.class.isAssignableFrom(clazz);
        }

        @Override
        public Object get(final Object object, final String xpath, final Class target) throws IllegalArgumentException {
            final ParameterValueGroup group = (ParameterValueGroup) object;
            Object value = null;
            try{
                GeneralParameterValue param = ParametersExt.getParameter(group, xpath);
                if(param instanceof ParameterValue){
                    value = ((ParameterValue)param).getValue();
                }else if(param instanceof ParameterValueGroup){
                    value = ((ParameterValueGroup)param).values();
                }
            }catch(ParameterNotFoundException ex){
                //we are laxiste, we don't consider
            }
            return Converters.convert(value,target);
        }

        @Override
        public void set(final Object object, final String xpath, final Object value, final Class target) throws IllegalArgumentException {
            final ParameterValueGroup group = (ParameterValueGroup) object;
            if(group == null) return;
            
            try{
                GeneralParameterValue param = ParametersExt.getParameter(group, xpath);
                if(param instanceof ParameterValue){
                    ((ParameterValue)param).setValue(Converters.convert(value, target));
                }else if(param instanceof ParameterValueGroup){
                    //how to map this ?
                }
            }catch(ParameterNotFoundException ex){
                //we are laxiste, we don't consider
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            //unique instance of this class
            return this == obj;
        }

        @Override
        public int hashCode() {
            return 6;
        }
        
    }

}
