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
import java.util.List;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import static org.geotoolkit.data.s57.S57Constants.*;
import static org.geotoolkit.data.s57.model.S57ModelObject.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataDictionaryDefinition extends S57ModelObject {
    
    //7.5.1.1 Data dictionary definition field structure
    public static final String DDDF = "DDDF";
    public static final String DDDF_RCNM = "RCNM";
    public static final String DDDF_RCID = "RCID";
    public static final String DDDF_OORA = "OORA";
    public static final String DDDF_OAAC = "OAAC";
    public static final String DDDF_OACO = "OACO";
    public static final String DDDF_OALL = "OALL";
    public static final String DDDF_OATY = "OATY";
    public static final String DDDF_DEFN = "DEFN";
    public static final String DDDF_AUTH = "AUTH";    
    public static final String DDDF_COMT = "COMT";
    
    public RecordType type;
    public long id;
    public ObjectOrAttribute objOrAtt;
    public String acronym;
    public int code;
    public String longLabel;
    public ObjectType objType;
    public String definition;
    public Agency agency;
    public String comment;
    public List<DataDictionaryDefinitionReference> references;
    
    public static class DataDictionaryDefinitionReference extends S57ModelObject {
        
        //7.5.1.2 Data dictionary definition reference field structure
        public static final String DDDF_DDDR = "DDDR";
        public static final String DDDF_DDDR_RFTP = "RFTP";
        public static final String DDDF_DDDR_RFVL = "RFVL";
        
        public ReferenceType type;
        public String value;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if (DDDF_DDDR_RFTP.equalsIgnoreCase(tag)) type = ReferenceType.valueOf(val);
                else if (DDDF_DDDR_RFVL.equalsIgnoreCase(tag)) value = toString(val);
            }
        }
    }
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (DDDF_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(value);
            else if (DDDF_RCID.equalsIgnoreCase(tag)) id = toInteger(value);
            else if (DDDF_OORA.equalsIgnoreCase(tag)) objOrAtt = ObjectOrAttribute.valueOf(value);
            else if (DDDF_OAAC.equalsIgnoreCase(tag)) acronym = toString(value);
            else if (DDDF_OACO.equalsIgnoreCase(tag)) code = toInteger(value);
            else if (DDDF_OALL.equalsIgnoreCase(tag)) longLabel = toString(value);
            else if (DDDF_OATY.equalsIgnoreCase(tag)) objType = ObjectType.valueOf(value);
            else if (DDDF_DEFN.equalsIgnoreCase(tag)) definition = toString(value);
            else if (DDDF_AUTH.equalsIgnoreCase(tag)) agency = Agency.valueOf(value);
            else if (DDDF_COMT.equalsIgnoreCase(tag)) comment = toString(value);
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(DataDictionaryDefinitionReference.DDDF_DDDR.equalsIgnoreCase(tag)){
                if(references==null) references = new ArrayList<DataDictionaryDefinitionReference>();
                final DataDictionaryDefinitionReference candidate = new DataDictionaryDefinitionReference();
                candidate.read(f);
                references.add(candidate);
            }
        }
    }
    
}
