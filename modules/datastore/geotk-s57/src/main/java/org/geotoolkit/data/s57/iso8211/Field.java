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
package org.geotoolkit.data.s57.iso8211;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Field {
    
    private final List<Field> fields = new ArrayList<Field>();
    private final FieldDescription type;
    private byte[] byteValues;

    public Field(FieldDescription type) {
        this.type = type;
    }

    /**
     * 
     * @return true if the field is simple/elementary (doesn't have subfields)
     */
    public boolean isSimple(){
        return type.getDatatype() != null;
    }
    
    /**
     * @return the type
     */
    public FieldDescription getType() {
        return type;
    }

    /**
     * @return the value as Byte
     */
    public byte[] getValueAsByte() {
        return byteValues;
    }
    
    public void setValueAsByte(byte[] byteValues) {
        this.byteValues = byteValues;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<SubField> getSubFields(){
        final List<SubField> subs = new ArrayList<SubField>();
        int offset = 0;
        for(SubFieldDescription desc : type.getSubFields()){
            final SubField sf = new SubField(desc);
            int length = sf.setValue(byteValues, offset);
            offset += length;
            subs.add(sf);
        }
        return subs;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getType().getTag());
        if(isSimple()){
            sb.append(" ").append(byteValues);
        }
        final List<SubField> subfields = getSubFields();
        if(!fields.isEmpty() || !subfields.isEmpty()){
            final List lst = new  ArrayList();
            lst.addAll(subfields);
            lst.addAll(fields);
            sb.append(Trees.toString("", lst));
        }
        return sb.toString();
    }
    
}
