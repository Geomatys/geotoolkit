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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataDescriptiveRecord extends DataRecord{
    
    private final List<FieldDescription> fieldDescriptions = new ArrayList<FieldDescription>();

    public DataDescriptiveRecord() {
    }

    public List<FieldDescription> getFieldDescriptions() {
        return fieldDescriptions;
    }

    public FieldDescription getFieldDescription(String tag){
        for(FieldDescription f : fieldDescriptions){
            if(tag.equalsIgnoreCase(f.getTag())){
                return f;
            }
        }
        return null;
    }
    
    /**
     * the root field, is the field not contained in any other fields.
     * @return Field
     */
    public FieldDescription getRootFieldDescription(){
        final Set<FieldDescription> allSubFields = new HashSet<FieldDescription>();
        allSubFields.add(getFieldDescription("0000")); //field control field is not the root 
        for(FieldDescription f : fieldDescriptions){
            allSubFields.addAll(f.getFields());
        }
        for(FieldDescription f : fieldDescriptions){
            if(!allSubFields.contains(f)){
                //found the root
                return f;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Header\n");
        sb.append("-recordLength:").append(recordLength).append("\n");
        sb.append("-interchangeLevel:").append(interchangeLevel).append("\n");
        sb.append("-leaderidentifier:").append(leaderidentifier).append("\n");
        sb.append("-extensionIndicator:").append(extensionIndicator).append("\n");
        sb.append("-version:").append(version).append("\n");
        sb.append("-applicationIndicator:").append(applicationIndicator).append("\n");
        sb.append("-fieldControlLength:").append(fieldControlLength).append("\n");
        sb.append("-areaAddress:").append(areaAddress).append("\n");
        sb.append("-charsetIndicator:").append(charsetIndicator).append("\n");
        sb.append(getRootFieldDescription());
        return sb.toString();
    }

}
