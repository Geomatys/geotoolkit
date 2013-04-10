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

    private static final Logger LOGGER = Logging.getLogger(S57ModelObjectReader.class);
    
    private ISO8211Reader isoReader;
    private S57ModelObject record;
    
    public S57ModelObjectReader() {
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
                final Field root = rec.getRootField();
                final Field firstField = root.getFields().get(0);
                final String tag = firstField.getType().getTag();
                
                //convert to an S-57 object                
                if(CatalogDirectory.CATD.equalsIgnoreCase(tag)){
                    final CatalogDirectory candidate = new CatalogDirectory();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataDictionaryDefinition.DDDF.equalsIgnoreCase(tag)){
                    final DataDictionaryDefinition candidate = new DataDictionaryDefinition();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataDictionaryDomainIdentifier.DDDI.equalsIgnoreCase(tag)){
                    final DataDictionaryDomainIdentifier candidate = new DataDictionaryDomainIdentifier();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataDictionarySchemaIdentifier.DDSI.equalsIgnoreCase(tag)){
                    final DataDictionarySchemaIdentifier candidate = new DataDictionarySchemaIdentifier();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataSetAccuracy.DSAC.equalsIgnoreCase(tag)){
                    final DataSetAccuracy candidate = new DataSetAccuracy();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataSetHistory.DSHT.equalsIgnoreCase(tag)){
                    final DataSetHistory candidate = new DataSetHistory();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataSetIdentification.DSID.equalsIgnoreCase(tag)){
                    final DataSetIdentification candidate = new DataSetIdentification();
                    candidate.read(firstField);
                    record = candidate;
                }else if(DataSetParameter.DSPM.equalsIgnoreCase(tag)){
                    final DataSetParameter candidate = new DataSetParameter();
                    candidate.read(firstField);
                    record = candidate;
                }else if(FeatureRecord.FRID.equalsIgnoreCase(tag)){
                    final FeatureRecord candidate = new FeatureRecord();
                    candidate.read(firstField);
                    record = candidate;
                }else if(VectorRecord.VRID.equalsIgnoreCase(tag)){
                    final VectorRecord candidate = new VectorRecord();
                    candidate.read(firstField);
                    record = candidate;
                }else{
                    LOGGER.log(Level.INFO, "Unknowned tag (possibly provider specific or unvalid S-57 file) : "+tag);
                    continue;
                }
        }
    }
    
    public void dispose() throws IOException{
        if(isoReader!= null){
            isoReader.dispose();
        }
    }
    
}
