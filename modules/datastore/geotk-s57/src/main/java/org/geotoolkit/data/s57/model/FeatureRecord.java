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
    /** object geometric primitive*/
    public static final String FRID_PRIM = "PRIM"; 
    /** The “Group” [GRUP] subfield is used to separate feature objects into groups. The definition of groups is
     * dependent on the product specification (see Appendix B – Product Specifications). If a feature object does
     * not belong to a group, the subfield must be left empty (see clause 2.1). */
    public static final String FRID_GRUP = "GRUP"; 
    /** The numeric object label/code of the object class from the IHO Object Catalogue is encoded in the “Object
     * Label/Code” [OBJL] subfield. */
    public static final String FRID_OBJL = "OBJL"; 
    /** record version */
    public static final String FRID_RVER = "RVER"; 
    /** record update instruction */
    public static final String FRID_RUIN = "RUIN"; 
    
    public RecordType type;
    public long id;
    public Primitive primitiveType;
    public int group;
    public int code;
    public int version;
    public UpdateInstruction updateInstruction;
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
        /** The “Feature Object Identification Number” ranges from 1 to (2^32)-2. The “Feature Object Identification
        * Subdivision” ranges from 1 to (2^16)-2. Both subfields are used to create an unique key for a feature object
        * produced by the agency encoded in the AGEN subfield. The usage of the FIDN and FIDS subfields is not
        * constrained and must be defined by the encoder. */
        public static final String FRID_FOID_FIDN = "FIDN"; 
        public static final String FRID_FOID_FIDS = "FIDS"; 
        
        public Agency agency;
        public int number;
        public int subdivision;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if (FRID_FOID_AGEN.equalsIgnoreCase(tag)) agency = Agency.valueOf(value);
                else if (FRID_FOID_FIDN.equalsIgnoreCase(tag)) number = toInteger(value);
                else if (FRID_FOID_FIDS.equalsIgnoreCase(tag)) subdivision = toInteger(value);
            }
        }
        
    }
    
    public static class Attribute extends S57ModelObject {
        //7.6.3 Feature record attribute field structure
        /** 4.4
        * Attributes of feature objects must be encoded in the “Feature Record Attribute” [ATTF] field (see clause
        * 7.6.3). The numeric attribute label/code of the attribute from the IHO Object Catalogue is encoded in the
        * “Attribute Label/Code” [ATTL] subfield. In both the ASCII and binary implementations, the “Attribute Value”
        * subfield [ATVL] must be a string of characters terminated by the subfield terminator (1/15). Lexical level
        * 0 or 1 may be used for the general text in the ATTF field (see clause 2.4). */
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
        /** 4.5
        * National attributes of feature objects must be encoded in the “Feature Record National Attribute” [NATF]
        * field (see clause 7.6.4). The numeric attribute label/code of the national attribute from the IHO Object
        * Catalogue is encoded in the “Attribute Label/Code” [ATTL] subfield. In both the ASCII and binary
        * implementations, the “Attribute Value” subfield [ATVL] must be a string of characters terminated by the
        * appropriate subfield terminator (see clause 2.5). All lexical levels may be used for the general text in the
        * NATF field (see clause 2.4). */
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
        
        public UpdateInstruction update;
        public int index;
        public int number;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FFPC_FFUI.equalsIgnoreCase(tag)) update = UpdateInstruction.valueOf(val);
                else if(FRID_FFPC_FFIX.equalsIgnoreCase(tag)) index = toInteger(val);
                else if(FRID_FFPC_NFPT.equalsIgnoreCase(tag)) number = toInteger(val);
            }
        }
        
    }
    
    public static class ObjectPointer extends S57ModelObject {
        //7.6.6 Feature record to feature object pointer field structure
        /** 4.6
        * The “Feature Record to Feature Object Pointer” [FFPT] field is used to establish a relationship between
        * feature objects. Relationships between feature objects are discussed in detail in chapter 6.
        * The main element of the pointer field is the LNAM subfield (see clause 4.3). The LNAM subfield contains
        * the key of the feature object being referenced (foreign key). The “Relationship Indicator” [RIND] subfield
        * can be used to qualify a relationship (e.g. master or slave relationship) or to add a stacking order to a
        * relationship. */
        public static final String FRID_FFPT = "FFPT"; 
        public static final String FRID_FFPT_LNAM = "LNAM"; 
        public static final String FRID_FFPT_RIND = "RIND"; 
        public static final String FRID_FFPT_COMT = "COMT"; 
                
        public String name;
        public RelationShip relationship;
        public String comment;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FFPT_LNAM.equalsIgnoreCase(tag)) name = toString(val);
                else if(FRID_FFPT_RIND.equalsIgnoreCase(tag)) relationship = RelationShip.valueOf(val);
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
                
        public UpdateInstruction update;
        public int index;
        public int number;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FSPC_FSUI.equalsIgnoreCase(tag)) update = UpdateInstruction.valueOf(val);
                else if(FRID_FSPC_FSIX.equalsIgnoreCase(tag)) index = toInteger(val);
                else if(FRID_FSPC_NSPT.equalsIgnoreCase(tag)) number = toInteger(val);
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
        public Orientation orientation;
        public Usage usage;
        public Mask mask;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object val = sf.getValue();
                     if(FRID_FSPT_NAME.equalsIgnoreCase(tag)) name = toString(val);
                else if(FRID_FSPT_ORNT.equalsIgnoreCase(tag)) orientation = Orientation.valueOf(val);
                else if(FRID_FSPT_USAG.equalsIgnoreCase(tag)) usage = Usage.valueOf(val);
                else if(FRID_FSPT_MASK.equalsIgnoreCase(tag)) mask = Mask.valueOf(val);
            }
        }
        
    }
    
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (FRID_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(value);
            else if (FRID_RCID.equalsIgnoreCase(tag)) id = toLong(value);
            else if (FRID_PRIM.equalsIgnoreCase(tag)) primitiveType = Primitive.valueOf(value);
            else if (FRID_GRUP.equalsIgnoreCase(tag)) group = toInteger(value);
            else if (FRID_OBJL.equalsIgnoreCase(tag)) code = toInteger(value);
            else if (FRID_RVER.equalsIgnoreCase(tag)) version = toInteger(value);
            else if (FRID_RUIN.equalsIgnoreCase(tag)) updateInstruction = UpdateInstruction.valueOf(value);
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
