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
    
    public static class Identifier extends S57ModelObject {
        //7.6.2 Feature object identifier field structure
        public static final String FRID_FOID = "FOID"; 
        public static final String FRID_FOID_AGEN = "AGEN"; 
        public static final String FRID_FOID_FIDN = "FIDN"; 
        public static final String FRID_FOID_FIDS = "FIDS"; 
    }
    
    public static class Attribute extends S57ModelObject {
        //7.6.3 Feature record attribute field structure
        public static final String FRID_ATTF = "ATTF"; 
        public static final String FRID_ATTF_ATTL = "ATTL"; 
        public static final String FRID_ATTF_ATVL = "ATVL"; 
    }

    public static class NationalAttribute extends S57ModelObject {
        //7.6.4 Feature record national attribute field structure
        public static final String FRID_NATF = "NATF"; 
        public static final String FRID_NATF_ATTL = "ATTL"; 
        public static final String FRID_NATF_ATVL = "ATVL"; 
    }
    
    public static class ObjectPointerControl extends S57ModelObject {
        //7.6.5 Feature record to feature object pointer control field structure
        public static final String FRID_FFPC = "FFPC"; 
        public static final String FRID_FFPC_FFUI = "FFUI"; 
        public static final String FRID_FFPC_FFIX = "FFIX"; 
        public static final String FRID_FFPC_NFPT = "NFPT"; 
    }
    
    public static class FeaturePointer extends S57ModelObject {
        //7.6.6 Feature record to feature object pointer field structure
        public static final String FRID_FFPT = "FFPT"; 
        public static final String FRID_FFPT_LNAM = "LNAM"; 
        public static final String FRID_FFPT_RIND = "RIND"; 
        public static final String FRID_FFPT_COMT = "COMT"; 
    }
    
    public static class SpatialPointerControl extends S57ModelObject {
        //7.6.7 Feature record to spatial record pointer control field structure
        public static final String FRID_FSPC = "FSPC"; 
        public static final String FRID_FSPC_FSUI = "FSUI"; 
        public static final String FRID_FSPC_FSIX = "FSIX"; 
        public static final String FRID_FSPC_NSPT = "NSPT"; 
    }
    
    public static class SpatialPointer extends S57ModelObject {
        //7.6.8 Feature record to spatial record pointer field structure
        public static final String FRID_FSPT = "FSPT"; 
        public static final String FRID_FSPT_NAME = "NAME";
        public static final String FRID_FSPT_ORNT = "ORNT"; 
        public static final String FRID_FSPT_USAG = "USAG"; 
        public static final String FRID_FSPT_MASK = "MASK";
    }
    
}
