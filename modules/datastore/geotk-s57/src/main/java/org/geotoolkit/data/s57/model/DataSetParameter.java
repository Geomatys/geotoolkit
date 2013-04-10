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
public class DataSetParameter extends S57ModelObject {
    
    //7.3.2.1 Data set parameter field structure
    public static final String DSPM = "DSPM";
    public static final String DSPM_RCNM = "RCNM";
    public static final String DSPM_RCID = "RCID";
    public static final String DSPM_HDAT = "HDAT";
    public static final String DSPM_VDAT = "VDAT";
    public static final String DSPM_SDAT = "SDAT";
    public static final String DSPM_CSCL = "CSCL";
    public static final String DSPM_DUNI = "DUNI";
    public static final String DSPM_HUNI = "HUNI";
    public static final String DSPM_PUNI = "PUNI";
    public static final String DSPM_COUN = "COUN";
    public static final String DSPM_COMF = "COMF";
    public static final String DSPM_SOMF = "SOMF";
    public static final String DSPM_COMT = "COMT";
    
    public static class DataSetProjection extends S57ModelObject {
        //7.3.2.2 Data set projection field structure
        public static final String DSPM_DSPR = "DSPR";
        public static final String DSPM_DSPR_PROJ = "PROJ";
        public static final String DSPM_DSPR_PRP1 = "PRP1";
        public static final String DSPM_DSPR_PRP2 = "PRP2";
        public static final String DSPM_DSPR_PRP3 = "PRP3";
        public static final String DSPM_DSPR_PRP4 = "PRP4";
        public static final String DSPM_DSPR_FEAS = "FEAS";
        public static final String DSPM_DSPR_FNOR = "FNOR";
        public static final String DSPM_DSPR_FPMF= "FPMF";
        public static final String DSPM_DSPR_COMT = "COMT";
    }
    
    public static class DataSetRegistration extends S57ModelObject {
        //7.3.2.3 Data set registration control field structure
        public static final String DSPM_DSRC = "DSRC";
        public static final String DSPM_DSRC_RPID = "RPID";
        public static final String DSPM_DSRC_RYCO = "RYCO";
        public static final String DSPM_DSRC_RXCO = "RXCO";
        public static final String DSPM_DSRC_CURP = "CURP";
        public static final String DSPM_DSRC_FPMF = "FPMF";
        public static final String DSPM_DSRC_RXVL = "RXVL";
        public static final String DSPM_DSRC_RYVL = "RYVL";
        public static final String DSPM_DSRC_COMT = "COMT";
    }
    
    
}
