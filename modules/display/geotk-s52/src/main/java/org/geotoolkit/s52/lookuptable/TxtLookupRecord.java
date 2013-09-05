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
package org.geotoolkit.s52.lookuptable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TxtLookupRecord extends LookupRecord{

    /**
     * code of the object class
     */
    public String objectClass;
    /**
     * attribute combination
     */
    public String attributeCombination;
    /**
     * symbolization instruction
     */
    public String symbolInstruction;
    /**
     * display priority.
     * May be null.
     */
    public Integer displayPriority;
    /**
     * radar.
     */
    public Radar radar;
    /**
     * IMO display category
     */
    public IMODisplayCategory imoDisplayCategory;
    /**
     * viewing group (optional).
     * May be null.
     */
    public Integer viewingGroup;

    public TxtLookupRecord() {
    }

    public TxtLookupRecord(String objectClass, String attributeCombination, String symbolInstruction) {
        this.objectClass = objectClass;
        this.attributeCombination = attributeCombination;
        this.symbolInstruction = symbolInstruction;
    }

    @Override
    public String getObjectClass() {
        return objectClass;
    }

    @Override
    public Map<String,String> getAttributeCombinaison() {
        final Map<String,String> map = new HashMap<>();

        String str = (attributeCombination==null) ? null : attributeCombination.trim();
        // S-52 Annex A part I p.66  8.3.3.4
        if(str == null || str.isEmpty()){
            //all match
        }else{
            try{
                while(!str.isEmpty()){
                    final String attName = str.substring(0, 6);
                    final char val = (str.length()>6) ? str.charAt(6) : 'A';
                    if(val == '?'){
                        //not a number, means value must be null
                        map.put(attName, ""+val);
                        str = str.substring(7);
                    }else if(Character.isDigit(val)){
                        //fix value
                        //find the value end
                        int start = 6;
                        int end = 6;
                        boolean hasNextValue = false;
                        StringBuilder value = new StringBuilder();
                        do{
                            for(int i=start,n=str.length();i<n;i++){
                                if(Character.isDigit(str.charAt(i))){
                                    end = i;
                                }else{
                                    break;
                                }
                            }
                            value.append(str.substring(start, end+1));
                            str = str.substring(end+1);

                            //check for more values
                            hasNextValue = str.startsWith(",");
                            if(hasNextValue){
                                value.append(',');
                                start = 0;
                                end = 0;
                            }
                        }while(hasNextValue);

                        map.put(attName, value.toString());

                    }else{
                        //not a number, means value must not be null
                        map.put(attName, ""+val);
                        str = str.substring(6);
                    }
                }
            }catch(Exception ex){
                System.err.println(">>>>>>>"+str);
                ex.printStackTrace();
            }

        }

        return map;
    }

    @Override
    public String getSymbolInstructions() {
        return symbolInstruction;
    }

    @Override
    public Integer getPriority() {
        return displayPriority;
    }

    @Override
    public IMODisplayCategory getDisplayCaegory() {
        return imoDisplayCategory;
    }

    @Override
    public Radar getRadar() {
        return radar;
    }

    @Override
    public Integer getViewingGroup() {
        return viewingGroup;
    }

    /**
     * Read record from given string.
     * @param str
     */
    public void read(final String str) throws IOException{
        final String[] parts = split(str);
        parts[3] = parts[3].trim();
        parts[5] = parts[5].trim();
        parts[6] = parts[6].trim();

        objectClass           = parts[0];
        attributeCombination = parts[1];
        symbolInstruction     = parts[2].trim();
        displayPriority       = (parts[3].isEmpty()) ? null : Integer.valueOf(parts[3]);
        radar                 = Radar.fromCode(parts[4]);
        imoDisplayCategory    = IMODisplayCategory.getOrCreate(parts[5]);
        viewingGroup          = (parts[6].isEmpty()) ? null : Integer.valueOf(parts[6]);
    }

    private static String[] split(String str) throws IOException{
        final String[] parts = new String[7];

        int offset = 0;
        int sep1 = 0;
        int sep2 = 0;
        for(int i=0;i<7;i++){
            sep1 = str.indexOf('"',offset);
            sep2 = str.indexOf('"',sep1+1);

            parts[i] = str.substring(sep1+1, sep2);
            if(i<6 && str.charAt(sep2+1)!= ','){
                //ensure there is a comma separator
                throw new IOException("missing ',' separator between fields");
            }
            offset = sep2+1;
        }
        return parts;
    }

}
