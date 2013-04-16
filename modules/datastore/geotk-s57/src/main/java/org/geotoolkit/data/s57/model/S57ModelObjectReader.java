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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.iso8211.DataRecord;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.ISO8211Reader;
import org.geotoolkit.util.logging.Logging;

/**
 * Wrap an ISO 8211 Reader, returning S-57 objects for each field.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class S57ModelObjectReader {

    /**
     * Filter records.
     */
    public static interface Predicate{
        public boolean match(DataRecord record);
    }
    
    private static final Logger LOGGER = Logging.getLogger(S57ModelObjectReader.class);
    
    //used to pass lexical levels to other records.
    private DataSetIdentification dsid;
    private ISO8211Reader isoReader;
    private S57ModelObject record;
    
    //used to filter records
    private Predicate predicate;
    
    public S57ModelObjectReader() {
    }

    public void setDsid(DataSetIdentification dsid) {
        this.dsid = dsid;
    }
    
    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Predicate getPredicate() {
        return predicate;
    }
    
    public void setInput(Object input){
        if(input instanceof ISO8211Reader){
            this.isoReader = (ISO8211Reader) input;
        }else{
            this.isoReader = new ISO8211Reader();
            this.isoReader.setInput(input);
        }
    }
    
    public boolean hasNext() throws IOException{
        findNext();
        return record != null;
    }
    
    public S57ModelObject next() throws IOException{
        findNext();
        S57ModelObject r = record;
        record = null;
        return r;
        
    }
    
    private void findNext() throws IOException {
        if(record != null){
            //already found
            return;
        }
        
        while(isoReader.hasNext() && record==null){
                final DataRecord rec = isoReader.next();
                if(predicate!=null && !predicate.match(rec)) continue;
                
                final Field root = rec.getRootField();
                final Field firstField = root.getFields().get(0);
                final String tag = firstField.getType().getTag();
                
                //convert to an S-57 object
                if(CatalogDirectory.CATD.equalsIgnoreCase(tag)){
                    record = new CatalogDirectory();
                }else if(DataDictionaryDefinition.DDDF.equalsIgnoreCase(tag)){
                    record = new DataDictionaryDefinition();
                }else if(DataDictionaryDomainIdentifier.DDDI.equalsIgnoreCase(tag)){
                    record = new DataDictionaryDomainIdentifier();
                }else if(DataDictionarySchemaIdentifier.DDSI.equalsIgnoreCase(tag)){
                    record = new DataDictionarySchemaIdentifier();
                }else if(DataSetAccuracy.DSAC.equalsIgnoreCase(tag)){
                    record = new DataSetAccuracy();
                }else if(DataSetHistory.DSHT.equalsIgnoreCase(tag)){
                    record = new DataSetHistory();
                }else if(DataSetIdentification.DSID.equalsIgnoreCase(tag)){
                    record = new DataSetIdentification();
                    record.read(firstField);
                    this.dsid = (DataSetIdentification) record;
                }else if(DataSetParameter.DSPM.equalsIgnoreCase(tag)){
                    record = new DataSetParameter();
                }else if(FeatureRecord.FRID.equalsIgnoreCase(tag)){
                    record = new FeatureRecord();
                }else if(VectorRecord.VRID.equalsIgnoreCase(tag)){
                    record = new VectorRecord();
                }else{
                    LOGGER.log(Level.INFO, "Unknowned tag (possibly provider specific or unvalid S-57 file) : "+tag);
                    continue;
                }
                if(dsid!=null){
                    record.attfLexicalLevel = dsid.information.attfLexicalLevel;
                    record.natfLexicalLevel = dsid.information.natfLexicalLevel;
                }
                record.read(firstField);
        }
    }
    
    public void dispose() throws IOException{
        if(isoReader!= null){
            isoReader.dispose();
        }
    }
    
}
