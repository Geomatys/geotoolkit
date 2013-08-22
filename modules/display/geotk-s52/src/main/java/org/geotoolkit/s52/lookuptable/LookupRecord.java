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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class LookupRecord {

    public static enum Radar{
        O("O"),
        S("S"),
        NULL("");

        public String code;

        private Radar(String code) {
            this.code = code;
        }

        public static Radar fromCode(String code){
            if("O".equals(code)) return O;
            if("S".equals(code)) return S;
            if(code.isEmpty()) return NULL;
            throw new IllegalArgumentException("No rader for code : '"+code+"'");
        }
    }

    /**
     * code of the object class
     */
    public String objectClass;
    /**
     * attribute combination
     */
    public String atttributeCombination;
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
        atttributeCombination = parts[1];
        symbolInstruction     = parts[2];
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
