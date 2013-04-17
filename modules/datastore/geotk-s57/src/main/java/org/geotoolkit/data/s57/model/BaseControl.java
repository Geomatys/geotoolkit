/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57.model;

import java.io.IOException;
import org.apache.sis.util.Classes;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import org.geotoolkit.data.s57.S57Constants;
import org.geotoolkit.data.s57.S57Constants.UpdateInstruction;

/**
 * For updates, base control structure.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class BaseControl extends S57Object {
    
    public S57Constants.UpdateInstruction update;
    public int index;
    public int number;
    
    protected abstract String getUpdateTag();
    
    protected abstract String getIndexTag();
    
    protected abstract String getNumberTag();
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if(getUpdateTag().equalsIgnoreCase(tag)) update = UpdateInstruction.valueOf(value);
            else if(getIndexTag().equalsIgnoreCase(tag)) index = toInteger(value);
            else if(getNumberTag().equalsIgnoreCase(tag)) number = toInteger(value);
        }
    }

    @Override
    public String toString() {
        return Classes.getShortClassName(this) +" "+update+" I:"+index+" N:"+number;
    }
    
}
