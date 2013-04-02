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

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.geotoolkit.data.s57.iso8211.ISO8211Constants.FC_CONTROL;
import static org.geotoolkit.data.s57.iso8211.ISO8211Reader.expect;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FieldDescription {
        
    private String tag;
    
    //DDR location
    private int length;
    private int position;
    //sub fields, rebuilded when reading 0000 field description
    private final List<FieldDescription> fields = new ArrayList<FieldDescription>();

    //description
    
    //field control
    private FieldDataStructure structure;
    private int type;
    private SubFieldDescription datatype; //we reuse the structure, it's not really a subfield
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
            datatype = FieldDataType.get(parts[2]).get(0);
        }else{
            //read subfields definitions
            if(!parts[1].isEmpty()){
                final String[] subfieldnames = parts[1].split("!");
                //parse types
                final List<SubFieldDescription> types = FieldDataType.get(parts[2]);
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
        if(datatype!=null){
            sb.append(",datatype:").append(datatype.getType()).append("(").append(datatype.getLength()).append(")");
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
    
}
