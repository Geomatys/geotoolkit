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

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.geotoolkit.data.iso8211.ISO8211Constants.*;
import static org.geotoolkit.data.iso8211.ISO8211Utilities.*;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FieldDescription {
        
    private String tag;
    private FieldDescription parent;
    
    //DDR location
    private int length;
    private int position;
    //sub fields, rebuilded when reading 0000 field description
    private final List<FieldDescription> fields = new ArrayList<FieldDescription>();

    //description
    
    //field control
    private FieldDataStructure structure;
    private int type;
    private SubFieldDescription elementaryType; //we reuse the structure, it's not really a subfield
    private byte[] lexicalLevel;
    //property details
    private String description;
    private final List<SubFieldDescription> subfields = new ArrayList<SubFieldDescription>();
    
    public FieldDescription() {
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    /**
     * @return the parent field, can be null
     */
    public FieldDescription getParent() {
        return parent;
    }

    /**
     * @param tag the parent field to set
     */
    public void setParent(FieldDescription parent) {
        this.parent = parent;
    }

    /**
     * @return the lenght
     */
    public int getLenght() {
        return length;
    }

    /**
     * @param length the lenght to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    public SubFieldDescription getElementaryType() {
        return elementaryType;
    }

    public List<FieldDescription> getFields() {
        return fields;
    }

    public List<SubFieldDescription> getSubFields() {
        return subfields;
    }
    
    /**
     * @return the structure
     */
    public FieldDataStructure getStructure() {
        return structure;
    }

    /**
     * @param structure the structure to set
     */
    public void setStructure(FieldDataStructure structure) {
        this.structure = structure;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the lexicalLevel
     */
    public byte[] getLexicalLevel() {
        return lexicalLevel;
    }

    /**
     * @param lexicalLevel the lexicalLevel to set
     */
    public void setLexicalLevel(byte[] lexicalLevel) {
        this.lexicalLevel = lexicalLevel;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(tag);
        sb.append(" (").append(description).append(") [");
        sb.append("type:").append(type);
        if(elementaryType!=null){
            sb.append(",datatype:").append(elementaryType.getType()).append("(").append(elementaryType.getLength()).append(")");
        }
        //sb.append(",length:").append(length);
        //sb.append(",position:").append(position);
        sb.append("]");
        if(!subfields.isEmpty() || !fields.isEmpty()){
            final List lst = new ArrayList();
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
     * Recalculate byte metrics based on current definition.
     * Recalculated fields are :
     * - length
     */
    public void recalculateMetrics(){
        int length = 0;
        //add field control length size + delimiter
        length += 9;
        length += 1;
        
        //add descriptionlenght + delimiter
        if(description!=null){
            length += description.getBytes(US_ASCII).length;
        }
        length += 1;
        
        //add subfields names + delimiter
        if(structure != FieldDataStructure.ELEMENTARY && !getSubFields().isEmpty()){
            for(SubFieldDescription sfd : getSubFields()){
                length += sfd.getTag().getBytes(US_ASCII).length;
            }
            length += getSubFields().size()-1; //field name separators
        }
        length += 1;
        
        //add elementary or subfields types + delimiter
        if(structure == FieldDataStructure.ELEMENTARY){
            final String types = FieldDataType.write(Collections.singletonList(elementaryType));
            length += types.getBytes(US_ASCII).length;
        }else{
            final String types = FieldDataType.write(getSubFields());
            length += types.getBytes(US_ASCII).length;
        }
        length += 1;
        
        this.length = length;
    }
    
    
    public void readDescription(final DataInput ds) throws IOException{
        structure = FieldDataStructure.get(Integer.parseInt(new String(new byte[]{ds.readByte()})));
        type = Integer.parseInt(new String(new byte[]{ds.readByte()}));
        expect(ds, FC_CONTROL);
        lexicalLevel = new byte[3];
        ds.readFully(lexicalLevel);
    }
    
    public void readModel(final DataInput ds) throws IOException{
        final byte[] buffer = new byte[length-9-1];
        ds.readFully(buffer);
        ds.readByte();//we skip the last SFEND
        final String str = new String(buffer);
        final String[] parts = str.split(ISO8211Constants.FEND+"");
        description = parts[0];
        
        if(structure == FieldDataStructure.ELEMENTARY){
            elementaryType = FieldDataType.read(parts[2]).get(0);
        }else{
            //read subfields definitions
            if(!parts[1].isEmpty()){
                final String[] subfieldnames = parts[1].split("!");
                //parse types
                final List<SubFieldDescription> types = FieldDataType.read(parts[2]);
                if(subfieldnames.length != types.size()){
                    throw new IOException("number of field do not match number of given types : "+parts[1]+" "+parts[2]);
                }

                //rebuild complete types
                for(int i=0;i<subfieldnames.length;i++){
                    String subFieldName = subfieldnames[i];
                    final SubFieldDescription sft = types.get(i);
                    if(subFieldName.charAt(0) == '*'){
                        //mandatory
                        subFieldName = subFieldName.substring(1);
                        sft.setMandatory(true);
                    }else{
                        sft.setMandatory(false);
                    }
                    sft.setTag(subFieldName);
                }
                subfields.addAll(types);
            }
        }
    }

    
}
