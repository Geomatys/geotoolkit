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
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import static org.geotoolkit.data.s57.S57Constants.*;
import static org.geotoolkit.data.s57.model.S57Object.*;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataSetParameter extends S57Object {
    
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
    
    public int horizontalDatum;
    public int verticalDatum;
    public int soundingDatum;
    public int dataScale;
    public int depthUnit;
    public int heightUnit;
    public int positionAccuracyUnit;
    public Unit coordUnit;
    public int coordFactor;
    public int soundingFactor;
    public String comment;
    public DataSetProjection projection;
    public DataSetRegistration registration;
    
    public static class DataSetProjection extends S57Object {
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
        
        public Projection projection;
        public Double param1;
        public Double param2;
        public Double param3;
        public Double param4;
        public Double falseEasting;
        public Double falseNorthin;
        public int factor;
        public String comment;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if(DSPM_DSPR_PROJ.equals(tag)) projection = Projection.valueOf(value);
                else if(DSPM_DSPR_PRP1.equals(tag)) param1 = toDouble(value);              
                else if(DSPM_DSPR_PRP2.equals(tag)) param2 = toDouble(value);              
                else if(DSPM_DSPR_PRP3.equals(tag)) param3 = toDouble(value);              
                else if(DSPM_DSPR_PRP4.equals(tag)) param4 = toDouble(value);              
                else if(DSPM_DSPR_FEAS.equals(tag)) falseEasting = toDouble(value);              
                else if(DSPM_DSPR_FNOR.equals(tag)) falseNorthin = toDouble(value);              
                else if(DSPM_DSPR_FPMF.equals(tag)) factor = toInteger(value);              
                else if(DSPM_DSPR_COMT.equals(tag)) comment = toString(value);              
            }
            
            //apply factor on double values
            if(param1!=null)param1 = param1/factor;
            if(param2!=null)param2 = param2/factor;
            if(param3!=null)param3 = param3/factor;
            if(param4!=null)param4 = param4/factor;
        }
        
    }
    
    public static class DataSetRegistration extends S57Object {
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
        
        public String id;
        public Double latNorth;
        public Double lonEast;
        public String unit;
        public int factor;
        public Double x;
        public Double y;
        public String comment;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if(DSPM_DSRC_RPID.equals(tag)) id = toString(value);
                else if(DSPM_DSRC_RYCO.equals(tag)) latNorth = toDouble(value);              
                else if(DSPM_DSRC_RXCO.equals(tag)) lonEast = toDouble(value);              
                else if(DSPM_DSRC_CURP.equals(tag)) unit = toString(value);              
                else if(DSPM_DSRC_FPMF.equals(tag)) factor = toInteger(value);              
                else if(DSPM_DSRC_RXVL.equals(tag)) x = toDouble(value);              
                else if(DSPM_DSRC_RYVL.equals(tag)) y = toDouble(value);              
                else if(DSPM_DSRC_COMT.equals(tag)) comment = toString(value);              
            }
            
            if(latNorth!=null)latNorth = latNorth/factor;
            if(lonEast!=null)lonEast = lonEast/factor;
        }
        
    }
    
    /**
     * Rebuild coordinate reference system from parameters.
     * @return CoordinateReferenceSystem , never null
     */
    public CoordinateReferenceSystem buildCoordinateReferenceSystem() {
        //TODO by martin
        return DefaultGeographicCRS.WGS84;
    }
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if(DSPM_RCNM.equals(tag)) type = RecordType.valueOf(value);
            else if(DSPM_RCID.equals(tag)) id = toLong(value);
            else if(DSPM_HDAT.equals(tag)) horizontalDatum = toInteger(value);
            else if(DSPM_VDAT.equals(tag)) verticalDatum = toInteger(value);
            else if(DSPM_SDAT.equals(tag)) soundingDatum = toInteger(value);
            else if(DSPM_CSCL.equals(tag)) dataScale = toInteger(value);
            else if(DSPM_DUNI.equals(tag)) depthUnit = toInteger(value);
            else if(DSPM_HUNI.equals(tag)) heightUnit = toInteger(value);
            else if(DSPM_PUNI.equals(tag)) positionAccuracyUnit = toInteger(value);
            else if(DSPM_COUN.equals(tag)) coordUnit = Unit.valueOf(value);
            else if(DSPM_COMF.equals(tag)) coordFactor = toInteger(value);
            else if(DSPM_SOMF.equals(tag)) soundingFactor = toInteger(value);
            else if(DSPM_COMT.equals(tag)) comment = toString(value);              
            
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(DataSetProjection.DSPM_DSPR.equals(tag)){
                projection = new DataSetProjection();
                projection.read(f);
            }else if(DataSetRegistration.DSPM_DSRC.equals(tag)){
                registration = new DataSetRegistration();
                registration.read(f);
                if(registration.x!=null)registration.x = registration.x/coordFactor;
                if(registration.y!=null)registration.y = registration.y/coordFactor;
            }
        }
    }
    
    
}
