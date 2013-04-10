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
public class VectorRecord extends S57ModelObject {
    
    //7.7.1.1 Vector record identifier field structure
    public static final String VRID = "VRID";
    public static final String VRID_RCNM = "RCNM"; 
    public static final String VRID_RCID = "RCID"; 
    public static final String VRID_RVER = "RVER"; 
    public static final String VRID_RUIN = "RUIN"; 
    
    public static class Attribute extends S57ModelObject {
        //7.7.1.2 Vector record attribute field structure
        public static final String VRID_ATTV = "ATTV"; 
        public static final String VRID_ATTV_ATTL = "ATTL"; 
        public static final String VRID_ATTV_ATVL = "ATVL";
    }
    
    public static class RecordPointerControl extends S57ModelObject {
        //7.7.1.3 Vector record pointer control field structure
        public static final String VRID_VRPC = "VRPC"; 
        public static final String VRID_VRPC_VPUI = "VPUI"; 
        public static final String VRID_VRPC_VPIX = "VPIX";
        public static final String VRID_VRPC_NVPT = "NVPT";
    }
    
    public static class RecordPointer extends S57ModelObject {
        //7.7.1.4 Vector record pointer field structure
        public static final String VRID_VRPT = "VRPT"; 
        public static final String VRID_VRPT_NAME = "NAME"; 
        public static final String VRID_VRPT_ORNT = "ORNT"; 
        public static final String VRID_VRPT_USAG = "USAG"; 
        public static final String VRID_VRPT_TOPI = "TOPI"; 
        public static final String VRID_VRPT_MASK = "MASK"; 
    }
    
    public static class CoordinateControl extends S57ModelObject {
        //7.7.1.5 Coordinate control field structure
        public static final String VRID_SGCC = "SGCC"; 
        public static final String VRID_SGCC_CCUI = "CCUI"; 
        public static final String VRID_SGCC_CCIX = "CCIX"; 
        public static final String VRID_SGCC_CCNG = "CCNG"; 
    }
    
    public static class Coordinate2D extends S57ModelObject {
        //7.7.1.6 2-D Coordinate field structure
        public static final String VRID_SG2D = "SG2D"; 
        public static final String VRID_SG2D_YCOO = "YCOO"; 
        public static final String VRID_SG2D_XCOO = "XCOO"; 
    }
    
    public static class Coordinate3D extends S57ModelObject {
        //7.7.1.7 3-D Coordinate field structure
        public static final String VRID_SG3D = "SG3D";
        public static final String VRID_SG3D_YCOO = "YCOO";
        public static final String VRID_SG3D_XCOO = "XCOO";
        public static final String VRID_SG3D_VE3D = "VE3D";
    }
    
    public static class Arc extends S57ModelObject {
        //7.7.1.8 Arc/Curve definition field structure
        public static final String VRID_ARCC = "ARCC";
        public static final String VRID_ARCC_ATYP = "ATYP";
        public static final String VRID_ARCC_SURF = "SURF";
        public static final String VRID_ARCC_ORDR = "ORDR";
        public static final String VRID_ARCC_RESO = "RESO";
        public static final String VRID_ARCC_FPMF = "FPMF";
        
        public static class Arc2D extends S57ModelObject {
            //7.7.1.9 Arc coordinates field structure
            public static final String VRID_ARCC_AR2D = "AR2D";
            public static final String VRID_ARCC_AR2D_ENPT = "STPT";
            public static final String VRID_ARCC_AR2D_CDPM = "CTPT";
            public static final String VRID_ARCC_AR2D_CDPR = "ENPT";
            public static final String VRID_ARCC_AR2D_YCOO = "YCOO";
            public static final String VRID_ARCC_AR2D_XCOO = "XCOO";
        }

        public static class Ellipse2D extends S57ModelObject {
            //7.7.1.10 Ellipse coordinates field structure
            public static final String VRID_ARCC_EL2D = "EL2D";
            public static final String VRID_ARCC_EL2D_STPT = "STPT";
            public static final String VRID_ARCC_EL2D_CTPT = "CTPT";
            public static final String VRID_ARCC_EL2D_ENPT = "ENPT";
            public static final String VRID_ARCC_EL2D_CDPM = "CDPM";
            public static final String VRID_ARCC_EL2D_CDPR = "CDPR";
            public static final String VRID_ARCC_EL2D_YCOO = "YCOO";
            public static final String VRID_ARCC_EL2D_XCOO = "XCOO";
        }

        public static class Curve2D extends S57ModelObject {
            //7.7.1.11 Curve coordinates field structure
            public static final String VRID_ARCC_CT2D = "CT2D";
            public static final String VRID_ARCC_CT2D_YCOO = "YCOO";
            public static final String VRID_ARCC_CT2D_XCOO = "XCOO";
        }
        
    }
    
    
}
