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
package org.geotoolkit.data.iso8211;

import java.io.DataOutput;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Field {
    
    private final FieldDescription type;    
    private final List<Field> fields = new ArrayList<Field>();
    private final List<SubField> subfields = new ArrayList<SubField>();
    private Object value;

    public Field(FieldDescription type) {
        this.type = type;
    }
    
    /**
     * @return the type
     */
    public FieldDescription getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<SubField> getSubFields(){
        return subfields;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getType().getTag());
        if(value != null){
            sb.append(" : ").append(value);
        }
        if(!fields.isEmpty() || !subfields.isEmpty()){
            final List lst = new  ArrayList();
            lst.addAll(subfields);
            lst.addAll(fields);
            sb.append(Trees.toString("", lst));
        }
        return sb.toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // IO operations ///////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Parse the given bytearray and rebuild subfields.
     * @param byteValues 
     */
    public void readValues(final byte[] byteValues){
        subfields.clear();
        if(type.getElementaryType() != null){
            //parse a single value
            final SubFieldDescription desc = type.getElementaryType();
            final SubField sf = new SubField(desc);
            sf.readValue(byteValues, 0);
            this.value = sf.getValue();
        }else{
            //parse subfields
            int offset = 0;
            for(SubFieldDescription desc : type.getSubFields()){
                final SubField sf = new SubField(desc);
                int length = sf.readValue(byteValues, offset);
                offset += length;
                subfields.add(sf);
            }
        }
    }
    
    /**
     * 
     * @param out 
     * @return int : number of bytes written
     */
    public int writeValues(final DataOutput out){
        throw new RuntimeException("No supported yet.");
    }
    
}
