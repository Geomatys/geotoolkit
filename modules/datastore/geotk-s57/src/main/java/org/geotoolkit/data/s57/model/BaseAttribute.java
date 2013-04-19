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
import java.util.List;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import static org.geotoolkit.data.s57.model.S57Object.toInteger;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class BaseAttribute extends S57Object {
    
    public int code;
    public String value;
        
    protected abstract String getKeyTag();
    protected abstract String getValueTag();
    
    @Override
    public void read(Field isofield) throws IOException {
        read(isofield.getSubFields());
    }

    public void read(List<SubField> subFields) throws IOException {
        for(SubField sf : subFields){
            final String tag = sf.getType().getTag();
            final Object val = sf.getValue();
                 if(getKeyTag().equals(tag)) code = toInteger(val);
            else if(getValueTag().equals(tag)){
                if(attfLexicalLevel==null) throw new IOException("ATTF Lexical level not provided.");
                value = new String(sf.getValueBytes(),attfLexicalLevel.getCharSet());
            }
        }
    }

    @Override
    public String toString() {
        return "Att:"+code+"="+value;
    }
    
}
