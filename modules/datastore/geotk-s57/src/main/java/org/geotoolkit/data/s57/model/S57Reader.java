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
import org.apache.sis.util.logging.Logging;

/**
 * REader returning S-57 objects for each field.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class S57Reader {

    /**
     * Filter records.
     */
    public static interface Predicate{
        public boolean match(DataRecord record);
    }

    static final Logger LOGGER = Logging.getLogger(S57Reader.class);

    //used to pass lexical levels to other records.
    protected DataSetIdentification dsid;
    protected S57Object record;

    //used to filter records
    protected Predicate predicate;

    public S57Reader() {
    }

    public final void setDsid(DataSetIdentification dsid) {
        this.dsid = dsid;
    }

    public final void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public final Predicate getPredicate() {
        return predicate;
    }

    public abstract void setInput(Object input) throws IOException;

    public final boolean hasNext() throws IOException{
        findNext();
        return record != null;
    }

    public final S57Object next() throws IOException{
        findNext();
        S57Object r = record;
        record = null;
        return r;
    }

    protected abstract void findNext() throws IOException;

    /**
     * Transform the given ISO8211 object in an S-57 object.
     * @param rec DataRecord
     * @return S57Object, may return null of ag is unknowned.
     */
    protected final S57Object toS57Object(final DataRecord rec) throws IOException{

        final Field root = rec.getRootField();
        final Field firstField = root.getFields().get(0);
        final String tag = firstField.getType().getTag();

        final S57Object record;

        //convert to an S-57 object
        if(CatalogDirectory.CATD.equals(tag)){
            record = new CatalogDirectory();
        }else if(DataDictionaryDefinition.DDDF.equals(tag)){
            record = new DataDictionaryDefinition();
        }else if(DataDictionaryDomainIdentifier.DDDI.equals(tag)){
            record = new DataDictionaryDomainIdentifier();
        }else if(DataDictionarySchemaIdentifier.DDSI.equals(tag)){
            record = new DataDictionarySchemaIdentifier();
        }else if(DataSetAccuracy.DSAC.equals(tag)){
            record = new DataSetAccuracy();
        }else if(DataSetHistory.DSHT.equals(tag)){
            record = new DataSetHistory();
        }else if(DataSetIdentification.DSID.equals(tag)){
            record = new DataSetIdentification();
            record.read(firstField);
            this.dsid = (DataSetIdentification) record;
        }else if(DataSetParameter.DSPM.equals(tag)){
            record = new DataSetParameter();
        }else if(FeatureRecord.FRID.equals(tag)){
            record = new FeatureRecord();
        }else if(VectorRecord.VRID.equals(tag)){
            record = new VectorRecord();
        }else{
            LOGGER.log(Level.INFO, "Unknowned tag (possibly provider specific or unvalid S-57 file) : "+tag);
            return null;
        }
        if(dsid!=null){
            record.attfLexicalLevel = dsid.information.attfLexicalLevel;
            record.natfLexicalLevel = dsid.information.natfLexicalLevel;
        }
        record.read(firstField);

        return record;
    }

    public void dispose() throws IOException{
    }

}
