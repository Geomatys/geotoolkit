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
    private FieldDataType type;
    private byte[] lexicalLevel;
    //property details
    private String description;
    
    //value types, can only be one of those
    private final List<SubFieldDescription> subfieldTypes = new ArrayList<SubFieldDescription>();
    private final List<String[]> subfieldNames = new ArrayList<String[]>();
    
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

    public List<FieldDescription> getFields() {
        return fields;
    }
    
    public List<SubFieldDescription> getSubFieldTypes() {
        return subfieldTypes;
    }

    public List<String[]> getSubfieldNames() {
        return subfieldNames;
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
    public FieldDataType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(FieldDataType type) {
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
        sb.append(structure).append(",").append(type).append("]");
        if(!subfieldTypes.isEmpty() || !fields.isEmpty()){
            final List lst = new ArrayList();
            lst.addAll(subfieldTypes);
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
        if(structure != FieldDataStructure.ELEMENTARY && !getSubFieldTypes().isEmpty()){
            for(SubFieldDescription sfd : getSubFieldTypes()){
                length += sfd.getTag().getBytes(US_ASCII).length;
            }
            length += getSubFieldTypes().size()-1; //field name separators
        }
        length += 1;
        
        //add elementary or subfields types + delimiter
        final String types = FieldValueType.write(getSubFieldTypes());
        length += types.getBytes(US_ASCII).length;
        length += 1;
        
        this.length = length;
    }
    
    
    public void readDescription(final DataInput ds) throws IOException{
        structure = FieldDataStructure.fromCode(ds.readByte());
        type = FieldDataType.fromCode(ds.readByte());
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
        
        if("0000".equals(getTag())){
            //no proper type on this field
            return;
        }
        
        subfieldTypes.addAll(FieldValueType.readTypes(parts[2]));
        
        if(structure == FieldDataStructure.ELEMENTARY){
            //unnamed
            subfieldNames.add(new String[]{""}); 
        }else if(structure == FieldDataStructure.LINEAR){            
            //rebuild complete types            
            final String[] subfieldnames = parts[1].split(DELIMITER_SUBFIELD);
            for(int i=0;i<subfieldnames.length;i++){
                final SubFieldDescription sft = subfieldTypes.get(i);
                sft.setTag(subfieldnames[i]);
            }
            subfieldNames.add(subfieldnames);
            
        }else if(structure == FieldDataStructure.CARTESIAN){
            final String[] dimensions = parts[1].split("\\"+DELIMITER_VECTOR);
            String[] subfieldnames = null;
            for(String dim : dimensions){
                subfieldnames = dim.split(DELIMITER_SUBFIELD);
                if(subfieldnames.length == 1 && subfieldnames[0].isEmpty()){
                    subfieldNames.add(new String[0]);
                }else{
                    subfieldNames.add(subfieldnames);
                }
            }
            
            //last subfield names are the actual encoded field values.
            for(int i=0;i<subfieldnames.length;i++){
                final SubFieldDescription sft = subfieldTypes.get(i);
                sft.setTag(subfieldnames[i]);
            }
            
        }else if(structure == FieldDataStructure.CONCATENATED){
            throw new IOException("Concatenate field type not supported : "+parts[1]+" "+parts[2]);
        }
    }

}
