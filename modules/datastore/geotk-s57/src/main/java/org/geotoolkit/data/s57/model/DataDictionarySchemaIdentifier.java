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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import static org.geotoolkit.data.s57.S57Constants.*;
import static org.geotoolkit.data.s57.model.S57ModelObject.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataDictionarySchemaIdentifier extends S57ModelObject {
    
    //7.5.3.1 Data dictionary schema identifier field structure
    public static final String DDSI = "DDSI";
    public static final String DDSI_RCNM = "RCNM";
    public static final String DDSI_RCID = "RCID"; 
    public static final String DDSI_OBLB = "OBLB"; 
        
    public RecordType type;
    public long id;
    public int code;
    public final List<DataDictionarySchemaField> fields = new ArrayList<DataDictionarySchemaField>();
    
    public static class DataDictionarySchemaField extends S57ModelObject {
        //7.5.3.2 Data dictionary schema field structure
        public static final String DDSI_DDSC = "DDSC";
        public static final String DDSI_DDSC_ATLB = "ATLB";
        public static final String DDSI_DDSC_ASET = "ASET";
        public static final String DDSI_DDSC_AUTH = "AUTH";
        
        public int code;
        public AttributeSet set;
        public Agency agency;
        
        @Override
        public void read(Field isofield) throws IOException {
            read(isofield.getSubFields());
        }
        
        public void read(List<SubField> subFields) throws IOException {
            for(SubField sf : subFields){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if (DDSI_DDSC_ATLB.equalsIgnoreCase(tag)) code = toInteger(val);
                else if (DDSI_DDSC_ASET.equalsIgnoreCase(tag)) set = AttributeSet.valueOf(val);
                else if (DDSI_DDSC_AUTH.equalsIgnoreCase(tag)) agency = Agency.valueOf(val);
            }
        }
        
    }
    
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object val = sf.getValue();
                 if (DDSI_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(val);
            else if (DDSI_RCID.equalsIgnoreCase(tag)) id = toLong(val);
            else if (DDSI_OBLB.equalsIgnoreCase(tag)) code = toInteger(val);
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(DataDictionarySchemaField.DDSI_DDSC.equalsIgnoreCase(tag)){
                final Iterator<SubField> sfite = f.getSubFields().iterator();
                while(sfite.hasNext()){
                    final DataDictionarySchemaField candidate = new DataDictionarySchemaField();
                    candidate.read(Arrays.asList(sfite.next(),sfite.next(),sfite.next()));
                    fields.add(candidate);
                }
            }
        }
    }
}
