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
import static org.geotoolkit.data.s57.model.S57ModelObject.*;

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
    
    public RecordType type;
    public long id;
    public ExchangePurpose purpose;
    public IntendedUsage usage;
    public String name;
    public String editionNumber;
    public String updateNumber;
    public String updateDate;
    public String issueDate;
    public double s57ediionNumber;
    public ProductSpecification specification;
    public String specificationDesc;
    public String specificationNumber;
    public ApplicationProfile applicationProfile;
    public Agency agency;    
    public String comment;
    public DataSetStructureInformation information;
    
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
        
        public DataStructure dataStructure;
        public LexicalLevel attfLexicalLevel;
        public LexicalLevel natfLexicalLevel;
        public int nbMeta;
        public int nbCarto;
        public int nbGeo;
        public int nbCollection;
        public int nbIsolatedNode;
        public int nbConnectedNode;
        public int nbEdge;
        public int nbFace;
        
        @Override
        public void read(Field isofield) throws IOException {
            for(SubField sf : isofield.getSubFields()){
                final String tag = sf.getType().getTag();
                final Object value = sf.getValue();
                     if(DSID_DSSI_DSTR.equalsIgnoreCase(tag)) dataStructure = DataStructure.valueOf(value);
                else if(DSID_DSSI_AALL.equalsIgnoreCase(tag)) attfLexicalLevel = LexicalLevel.valueOf(value);
                else if(DSID_DSSI_NALL.equalsIgnoreCase(tag)) natfLexicalLevel = LexicalLevel.valueOf(value);
                else if(DSID_DSSI_NOMR.equalsIgnoreCase(tag)) nbMeta = toInteger(value);
                else if(DSID_DSSI_NOCR.equalsIgnoreCase(tag)) nbCarto = toInteger(value);
                else if(DSID_DSSI_NOGR.equalsIgnoreCase(tag)) nbGeo = toInteger(value);
                else if(DSID_DSSI_NOLR.equalsIgnoreCase(tag)) nbCollection = toInteger(value);
                else if(DSID_DSSI_NOIN.equalsIgnoreCase(tag)) nbIsolatedNode = toInteger(value);
                else if(DSID_DSSI_NOCN.equalsIgnoreCase(tag)) nbConnectedNode = toInteger(value);
                else if(DSID_DSSI_NOED.equalsIgnoreCase(tag)) nbEdge = toInteger(value);
                else if(DSID_DSSI_NOFA.equalsIgnoreCase(tag)) nbFace = toInteger(value);
            }
        }
        
    }

    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
            if(DSID_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(value);
            else if(DSID_RCID.equalsIgnoreCase(tag)) id = toLong(value);
            else if(DSID_EXPP.equalsIgnoreCase(tag)) purpose = ExchangePurpose.valueOf(value);
            else if(DSID_INTU.equalsIgnoreCase(tag)) usage = IntendedUsage.valueOf(value);
            else if(DSID_DSNM.equalsIgnoreCase(tag)) name = toString(value);
            else if(DSID_EDTN.equalsIgnoreCase(tag)) editionNumber = toString(value);
            else if(DSID_UPDN.equalsIgnoreCase(tag)) updateNumber = toString(value);
            else if(DSID_UADT.equalsIgnoreCase(tag)) updateDate = toString(value);
            else if(DSID_ISDT.equalsIgnoreCase(tag)) issueDate = toString(value);
            else if(DSID_STED.equalsIgnoreCase(tag)) s57ediionNumber = toDouble(value);
            else if(DSID_PRSP.equalsIgnoreCase(tag)) specification = ProductSpecification.valueOf(value);
            else if(DSID_PSDN.equalsIgnoreCase(tag)) specificationDesc = toString(value);
            else if(DSID_PRED.equalsIgnoreCase(tag)) specificationNumber = toString(value);
            else if(DSID_PROF.equalsIgnoreCase(tag)) applicationProfile = ApplicationProfile.valueOf(value);
            else if(DSID_AGEN.equalsIgnoreCase(tag)) agency = Agency.valueOf(value);
            else if(DSID_COMT.equalsIgnoreCase(tag)) comment = toString(value);              
            
        }
        for(Field f : isofield.getFields()){
            final String tag = f.getType().getTag();
            if(DataSetStructureInformation.DSID_DSSI.equalsIgnoreCase(tag)){
                information = new DataSetStructureInformation();
                information.read(f);
            }
        }
    }
    
}
