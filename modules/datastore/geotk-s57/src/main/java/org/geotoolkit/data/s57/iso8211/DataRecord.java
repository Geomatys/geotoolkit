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
public class DataRecord {
    
    protected int recordLength;
    protected byte interchangeLevel;
    protected byte leaderidentifier;
    protected byte extensionIndicator;
    protected byte version;
    protected byte applicationIndicator;
    protected int fieldControlLength;
    protected int areaAddress;
    protected byte[] charsetIndicator;
    private final List<Field> fields = new ArrayList<Field>();

    public DataRecord() {
    }

    /**
     * @return the recordLength
     */
    public int getRecordLength() {
        return recordLength;
    }

    /**
     * @param recordLength the recordLength to set
     */
    public void setRecordLength(int recordLength) {
        this.recordLength = recordLength;
    }

    /**
     * @return the interchangeLevel
     */
    public byte getInterchangeLevel() {
        return interchangeLevel;
    }

    /**
     * @param interchangeLevel the interchangeLevel to set
     */
    public void setInterchangeLevel(byte interchangeLevel) {
        this.interchangeLevel = interchangeLevel;
    }

    /**
     * @return the leaderidentifier
     */
    public byte getLeaderidentifier() {
        return leaderidentifier;
    }

    /**
     * @param leaderidentifier the leaderidentifier to set
     */
    public void setLeaderidentifier(byte leaderidentifier) {
        this.leaderidentifier = leaderidentifier;
    }

    /**
     * @return the extensionIndicator
     */
    public byte getExtensionIndicator() {
        return extensionIndicator;
    }

    /**
     * @param extensionIndicator the extensionIndicator to set
     */
    public void setExtensionIndicator(byte extensionIndicator) {
        this.extensionIndicator = extensionIndicator;
    }

    /**
     * @return the version
     */
    public byte getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * @return the applicationIndicator
     */
    public byte getApplicationIndicator() {
        return applicationIndicator;
    }

    /**
     * @param applicationIndicator the applicationIndicator to set
     */
    public void setApplicationIndicator(byte applicationIndicator) {
        this.applicationIndicator = applicationIndicator;
    }

    /**
     * @return the fieldControlLength
     */
    public int getFieldControlLength() {
        return fieldControlLength;
    }

    /**
     * @param fieldControlLength the fieldControlLength to set
     */
    public void setFieldControlLength(int fieldControlLength) {
        this.fieldControlLength = fieldControlLength;
    }

    /**
     * @return the areaAddress
     */
    public int getAreaAddress() {
        return areaAddress;
    }

    /**
     * @param areaAddress the areaAddress to set
     */
    public void setAreaAddress(int areaAddress) {
        this.areaAddress = areaAddress;
    }

    /**
     * @return the charsetIndicator
     */
    public byte[] getCharsetIndicator() {
        return charsetIndicator;
    }

    /**
     * @param charsetIndicator the charsetIndicator to set
     */
    public void setCharsetIndicator(byte[] charsetIndicator) {
        this.charsetIndicator = charsetIndicator;
    }

    
    public List<Field> getFields() {
        return fields;
    }

    public Field getField(String tag){
        for(Field f : fields){
            if(tag.equalsIgnoreCase(f.getType().getTag())){
                return f;
            }
        }
        return null;
    }
    
    /**
     * the root field, is the field not contained in any other fields.
     * @return Field
     */
    public Field getRootField(){
        final Set<Field> allSubFields = new HashSet<Field>();
        for(Field f : fields){
            allSubFields.addAll(f.getSubFields());
        }
        for(Field f : fields){
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
        if(getRootField() != null){
            sb.append(getRootField());
        }
        return sb.toString();
    }

}
