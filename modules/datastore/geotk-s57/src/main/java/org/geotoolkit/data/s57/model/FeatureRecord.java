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
public class FeatureRecord extends S57ModelObject {
 
    //7.5.3.2 Feature record identifier field structure
    public static final String FRID = "FRID";
    public static final String FRID_RCNM = "RCNM"; 
    public static final String FRID_RCID = "RCID"; 
    public static final String FRID_PRIM = "PRIM"; 
    public static final String FRID_GRUP = "GRUP"; 
    public static final String FRID_OBJL = "OBJL"; 
    public static final String FRID_RVER = "RVER"; 
    public static final String FRID_RUIN = "RUIN"; 
    
    public RCNM type;
    public long id;
    public String primitiveType;
    public int group;
    public int code;
    public int version;
    public String updateInstruction;
    public Identifier identifier;
    public List<Attribute> attributes;
    public List<NationalAttribute> nattributes;
    public ObjectPointerControl objectControl;
    public List<ObjectPointer> objectPointers;
    public SpatialPointerControl spatialControl;
    public List<SpatialPointer> spatialPointers;
    
    public static class Identifier extends S57ModelObject {
        //7.6.2 Feature object identifier field structure
        public static final String FRID_FOID = "FOID"; 
        public static final String FRID_FOID_AGEN = "AGEN"; 
        public static final String FRID_FOID_FIDN = "FIDN"; 
        public static final String FRID_FOID_FIDS = "FIDS"; 
        
        public String agency;
        public int number;
        public int subdivision;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if (FRID_FOID_AGEN.equalsIgnoreCase(tag)) agency = toString(value);
                else if (FRID_FOID_FIDN.equalsIgnoreCase(tag)) number = toInteger(value);
                else if (FRID_FOID_FIDS.equalsIgnoreCase(tag)) subdivision = toInteger(value);
            }
        }
        
    }
    
    public static class Attribute extends S57ModelObject {
        //7.6.3 Feature record attribute field structure
        public static final String FRID_ATTF = "ATTF"; 
        public static final String FRID_ATTF_ATTL = "ATTL"; 
        public static final String FRID_ATTF_ATVL = "ATVL"; 
        
        public int code;
        public String value;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_ATTF_ATTL.equalsIgnoreCase(tag)) code = toInteger(val);
                else if(FRID_ATTF_ATVL.equalsIgnoreCase(tag)) value = toString(val);
            }
        }
    }

    public static class NationalAttribute extends S57ModelObject {
        //7.6.4 Feature record national attribute field structure
        public static final String FRID_NATF = "NATF"; 
        public static final String FRID_NATF_ATTL = "ATTL"; 
        public static final String FRID_NATF_ATVL = "ATVL"; 
        
        public int code;
        public String value;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_NATF_ATTL.equalsIgnoreCase(tag)) code = toInteger(val);
                else if(FRID_NATF_ATVL.equalsIgnoreCase(tag)) value = toString(val);
            }
        }
    }
    
    public static class ObjectPointerControl extends S57ModelObject {
        //7.6.5 Feature record to feature object pointer control field structure
        public static final String FRID_FFPC = "FFPC"; 
        public static final String FRID_FFPC_FFUI = "FFUI"; 
        public static final String FRID_FFPC_FFIX = "FFIX"; 
        public static final String FRID_FFPC_NFPT = "NFPT"; 
        
        public String updateInstruction;
        public int pointerIndex;
        public int nbPointers;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FFPC_FFUI.equalsIgnoreCase(tag)) updateInstruction = toString(val);
                else if(FRID_FFPC_FFIX.equalsIgnoreCase(tag)) pointerIndex = toInteger(val);
                else if(FRID_FFPC_NFPT.equalsIgnoreCase(tag)) nbPointers = toInteger(val);
            }
        }
        
    }
    
    public static class ObjectPointer extends S57ModelObject {
        //7.6.6 Feature record to feature object pointer field structure
        public static final String FRID_FFPT = "FFPT"; 
        public static final String FRID_FFPT_LNAM = "LNAM"; 
        public static final String FRID_FFPT_RIND = "RIND"; 
        public static final String FRID_FFPT_COMT = "COMT"; 
                
        public String name;
        public String relationship;
        public String comment;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FFPT_LNAM.equalsIgnoreCase(tag)) name = toString(val);
                else if(FRID_FFPT_RIND.equalsIgnoreCase(tag)) relationship = toString(val);
                else if(FRID_FFPT_COMT.equalsIgnoreCase(tag)) comment = toString(val);
            }
        }
        
    }
    
    public static class SpatialPointerControl extends S57ModelObject {
        //7.6.7 Feature record to spatial record pointer control field structure
        public static final String FRID_FSPC = "FSPC"; 
        public static final String FRID_FSPC_FSUI = "FSUI"; 
        public static final String FRID_FSPC_FSIX = "FSIX"; 
        public static final String FRID_FSPC_NSPT = "NSPT"; 
                
        public String updateInstruction;
        public int pointerIndex;
        public int nbPointers;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FSPC_FSUI.equalsIgnoreCase(tag)) updateInstruction = toString(val);
                else if(FRID_FSPC_FSIX.equalsIgnoreCase(tag)) pointerIndex = toInteger(val);
                else if(FRID_FSPC_NSPT.equalsIgnoreCase(tag)) nbPointers = toInteger(val);
            }
        }
        
    }
    
    public static class SpatialPointer extends S57ModelObject {
        //7.6.8 Feature record to spatial record pointer field structure
        public static final String FRID_FSPT = "FSPT"; 
        public static final String FRID_FSPT_NAME = "NAME";
        public static final String FRID_FSPT_ORNT = "ORNT"; 
        public static final String FRID_FSPT_USAG = "USAG"; 
        public static final String FRID_FSPT_MASK = "MASK";
             
        public String name;
        public String orientation;
        public String usage;
        public String mask;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FSPT_NAME.equalsIgnoreCase(tag)) name = toString(val);
                else if(FRID_FSPT_ORNT.equalsIgnoreCase(tag)) orientation = toString(val);
                else if(FRID_FSPT_USAG.equalsIgnoreCase(tag)) usage = toString(val);
                else if(FRID_FSPT_MASK.equalsIgnoreCase(tag)) mask = toString(val);
            }
        }
        
    }
    
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (FRID_RCNM.equalsIgnoreCase(tag)) type = RCNM.read(value);
            else if (FRID_RCID.equalsIgnoreCase(tag)) id = toLong(value);
            else if (FRID_PRIM.equalsIgnoreCase(tag)) primitiveType = toString(value);
            else if (FRID_GRUP.equalsIgnoreCase(tag)) group = toInteger(value);
            else if (FRID_OBJL.equalsIgnoreCase(tag)) code = toInteger(value);
            else if (FRID_RVER.equalsIgnoreCase(tag)) version = toInteger(value);
            else if (FRID_RUIN.equalsIgnoreCase(tag)) updateInstruction = toString(value);
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(Identifier.FRID_FOID.equalsIgnoreCase(tag)){
                identifier = new Identifier();
                identifier.read(f);
            }else if(Attribute.FRID_ATTF.equalsIgnoreCase(tag)){
                if(attributes==null) attributes = new ArrayList<Attribute>();
                final Attribute candidate = new Attribute();
                candidate.read(f);
                attributes.add(candidate);
            }else if(NationalAttribute.FRID_NATF.equalsIgnoreCase(tag)){
                if(nattributes==null) nattributes = new ArrayList<NationalAttribute>();
                final NationalAttribute candidate = new NationalAttribute();
                candidate.read(f);
                nattributes.add(candidate);
            }else if(ObjectPointerControl.FRID_FFPC.equalsIgnoreCase(tag)){
                objectControl = new ObjectPointerControl();
                objectControl.read(f);
            }else if(ObjectPointer.FRID_FFPT.equalsIgnoreCase(tag)){
                if(objectPointers==null) objectPointers = new ArrayList<ObjectPointer>();                
                final ObjectPointer candidate = new ObjectPointer();
                candidate.read(f);
                objectPointers.add(candidate);
            }else if(SpatialPointerControl.FRID_FSPC.equalsIgnoreCase(tag)){
                spatialControl = new SpatialPointerControl();
                spatialControl.read(f);
            }else if(SpatialPointer.FRID_FSPT.equalsIgnoreCase(tag)){
                if(spatialPointers==null) spatialPointers = new ArrayList<SpatialPointer>();                
                final SpatialPointer candidate = new SpatialPointer();
                candidate.read(f);
                spatialPointers.add(candidate);
            }
        }
    }
    
}
