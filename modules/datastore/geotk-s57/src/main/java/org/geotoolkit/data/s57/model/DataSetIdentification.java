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
public class DataSetIdentification extends S57ModelObject {
    
    //7.3.1.1 Data set identification field structure
    public static final String DSID = "DSID";
    public static final String DSID_RCNM = "RCNM";
    public static final String DSID_RCID = "RCID";
    public static final String DSID_EXPP = "EXPP";
    public static final String DSID_INTU = "INTU";
    public static final String DSID_DSNM = "DSNM";
    public static final String DSID_EDTN = "EDTN";
    public static final String DSID_UPDN = "UPDN";
    public static final String DSID_UADT = "UADT";
    public static final String DSID_ISDT = "ISDT";
    public static final String DSID_STED = "STED";
    public static final String DSID_PRSP = "PRSP";
    public static final String DSID_PSDN = "PSDN";
    public static final String DSID_PRED = "PRED";
    public static final String DSID_PROF = "PROF";
    public static final String DSID_AGEN = "AGEN";
    public static final String DSID_COMT = "COMT";
    
    public static class DataSetStructureInformation extends S57ModelObject{
        
        //7.3.1.2 Data set structure information field structure
        public static final String DSID_DSSI = "DSSI";
        public static final String DSID_DSSI_DSTR = "DSTR";
        public static final String DSID_DSSI_AALL = "AALL";
        public static final String DSID_DSSI_NALL = "NALL";
        public static final String DSID_DSSI_NOMR = "NOMR";
        public static final String DSID_DSSI_NOCR = "NOCR";
        public static final String DSID_DSSI_NOGR = "NOGR";
        public static final String DSID_DSSI_NOLR = "NOLR";
        public static final String DSID_DSSI_NOIN = "NOIN";
        public static final String DSID_DSSI_NOCN = "NOCN";
        public static final String DSID_DSSI_NOED = "NOED";
        public static final String DSID_DSSI_NOFA = "NOFA";
        
    }
    
}
