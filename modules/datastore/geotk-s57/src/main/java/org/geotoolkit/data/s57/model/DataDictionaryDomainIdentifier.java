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
public class DataDictionaryDomainIdentifier extends S57ModelObject {
    
    //7.5.2.1 Data dictionary domain identifier field structure
    public static final String DDDI = "DDDI";
    public static final String DDDI_RCNM = "RCNM";
    public static final String DDDI_RCID = "RCID"; 
    public static final String DDDI_ATLB = "ATLB";
    public static final String DDDI_ATDO = "ATDO";
    public static final String DDDI_ADMU = "ADMU";
    public static final String DDDI_ADFT = "ADFT";
    public static final String DDDI_AUTH = "AUTH";
    public static final String DDDI_COMT = "COMT";
    
    public RecordType type;
    public long id;
    public int code;
    public AttributeDomain domainType;
    public String unit;
    public String domainFormat;
    public Agency agency;
    public String comment;
    public List<DataDictionaryDomainField> fields = new ArrayList<DataDictionaryDomainField>();
    
    public static class DataDictionaryDomainField extends S57ModelObject {
        //7.5.2.2 Data dictionary domain field structure
        public static final String DDDI_DDOM = "DDOM";
        public static final String DDDI_DDOM_RAVA = "RAVA";
        public static final String DDDI_DDOM_DVAL = "DVAL";
        public static final String DDDI_DDOM_DVSD = "DVSD";
        public static final String DDDI_DDOM_DEFN = "DEFN";
        public static final String DDDI_DDOM_AUTH = "AUTH";
        
        public RangeOrValue rangeOrValue;
        public String domainValue;
        public String description;
        public String definition;
        public Agency agency;
        public List<DataDictionaryDomainReference> references = new ArrayList<DataDictionaryDomainReference>();
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if (DDDI_DDOM_RAVA.equalsIgnoreCase(tag)) rangeOrValue = RangeOrValue.valueOf(value);
                else if (DDDI_DDOM_DVAL.equalsIgnoreCase(tag)) domainValue = toString(value);
                else if (DDDI_DDOM_DVSD.equalsIgnoreCase(tag)) description = toString(value);
                else if (DDDI_DDOM_DEFN.equalsIgnoreCase(tag)) definition = toString(value);
                else if (DDDI_DDOM_AUTH.equalsIgnoreCase(tag)) agency = Agency.valueOf(value);
            }
            for(Field f : isofield.getFields()){
                final String tag = f.getType().getTag();
                if(DataDictionaryDomainReference.DDDI_DDOM_DDRF.equalsIgnoreCase(tag)){
                    if(references==null) references = new ArrayList<DataDictionaryDomainReference>();
                    final DataDictionaryDomainReference candidate = new DataDictionaryDomainReference();
                    candidate.read(f);
                    references.add(candidate);
                }
            }
        }
        
    }
    
    public static class DataDictionaryDomainReference extends S57ModelObject{
        //7.5.2.3 Data dictionary domain reference field structure
        public static final String DDDI_DDOM_DDRF = "DDRF";
        public static final String DDDI_DDOM_DDRF_RFTP = "RFTP";
        public static final String DDDI_DDOM_DDRF_RFVL = "RFVL";
        
        public ReferenceType type;
        public String value;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if (DDDI_DDOM_DDRF_RFTP.equalsIgnoreCase(tag)) type = ReferenceType.valueOf(val);
                else if (DDDI_DDOM_DDRF_RFVL.equalsIgnoreCase(tag)) value = toString(val);
                
            }
        }
    }
    
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (DDDI_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(value);
            else if (DDDI_RCID.equalsIgnoreCase(tag)) id = toInteger(value);
            else if (DDDI_ATLB.equalsIgnoreCase(tag)) code = toInteger(value);
            else if (DDDI_ATDO.equalsIgnoreCase(tag)) domainType = AttributeDomain.valueOf(value);
            else if (DDDI_ADMU.equalsIgnoreCase(tag)) unit = toString(value);
            else if (DDDI_ADFT.equalsIgnoreCase(tag)) domainFormat = toString(value);
            else if (DDDI_AUTH.equalsIgnoreCase(tag)) agency = Agency.valueOf(value);
            else if (DDDI_COMT.equalsIgnoreCase(tag)) comment = toString(value);
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(DataDictionaryDomainField.DDDI_DDOM.equalsIgnoreCase(tag)){
                if(fields==null) fields = new ArrayList<DataDictionaryDomainField>();
                final DataDictionaryDomainField candidate = new DataDictionaryDomainField();
                candidate.read(f);
                fields.add(candidate);
            }
        }
    }
    
}
