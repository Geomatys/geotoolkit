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
package org.geotoolkit.data.s57.iso8211;

import java.io.DataInput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.geotoolkit.data.s57.iso8211.ISO8211Constants.*;
import static org.geotoolkit.data.s57.iso8211.ISO8211Utilities.*;
import org.geotoolkit.io.LEDataInputStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISO8211Reader {
    
    private Object input;
    private InputStream stream;
    private DataInput ds;
    private boolean closeOnDispose = false;
    private DataRecord ddr;
    private DataRecord record = null;

    public ISO8211Reader() {
    }

    public void reset(){
        input = null;
        stream = null;
        closeOnDispose = false;
        ddr = null;
    }
    
    public void setInput(Object input) {
        reset();
        this.input = input;
    }

    public Object getInput() {
        return input;
    }
    
    private DataInput getDataInput() throws IOException{
        if(ds!=null) return ds;
        
        if(input instanceof File){
            stream = new FileInputStream((File)input);
            closeOnDispose = true;
        }else if(input instanceof URL){
            stream = ((URL)input).openStream();
            closeOnDispose = true;
        }else if(input instanceof InputStream){
            stream = (InputStream)input;
            closeOnDispose = false;
        }else{
            throw new IOException("Unsupported input : "+input);
        }
        ds = new LEDataInputStream(stream);
        return ds;
    }

    public DataRecord getDDR() throws IOException {
        if(ddr != null){
            return ddr;
        }
        final DataInput ds = getDataInput();
        readHeader(ds);
        return ddr;
    }
    
//    public FeatureType getFeatureType() throws IOException {
//        getDDR();
//        
//    }
    
    public boolean hasNext() throws IOException{
        findNext();
        return record != null;
    }
    
    public DataRecord next() throws IOException{
        findNext();
        DataRecord r = record;
        record = null;
        return r;
        
    }
    
    private void findNext() throws IOException {
        if(record != null){
            //already found
            return;
        }
        
        final DataInput ds = getDataInput();
        
        //read the header
        if(ddr == null){
            readHeader(ds);
        }
        
        record = readRecord(ds);
    }
    
    private void readHeader(final DataInput ds) throws IOException {
        ddr = new DataRecord();
        ddr.readDescription(ds);
        
        //read each field description
        final List<FieldDescription> sortedFields = ddr.getFieldDescriptions();
        for(FieldDescription field : sortedFields){
            field.readDescription(ds);
                
            if("0000".equals(field.getTag())){
                //first field, contains the tree structure
                expect(ds,FEND);
                //calculate number of pairs we will have, rebuild tree structure
                final int nbPair = (field.getLenght()-11)/ (ddr.getFieldSizeTag()*2) ;
                byte[] buffer = new byte[ddr.getFieldSizeTag()];
                for(int i=0;i<nbPair;i++){
                    ds.readFully(buffer);
                    final String parentTag = new String(buffer);
                    ds.readFully(buffer);
                    final String childTag = new String(buffer);
                    final FieldDescription child = ddr.getFieldDescription(childTag);
                    final FieldDescription parent = ddr.getFieldDescription(parentTag);
                    child.setParent(parent);
                    parent.getFields().add(child);
                }
                expect(ds,SFEND);
            }else{
                //description field
                field.readModel(ds);
            }
        }
        
    }
    
    private DataRecord readRecord(final DataInput ds) throws IOException{
        final DataRecord dr = new DataRecord(ddr);
        try{
            dr.readDescription(ds);
        }catch(EOFException ex){
            //no more records
            return null;
        }
        
        
        //read each field value
        final List<FieldDescription> sortedFields = new ArrayList<FieldDescription>(dr.getFieldDescriptions());
        for(FieldDescription field : sortedFields){
            final byte[] value = new byte[field.getLenght()];
            ds.readFully(value);
            //get the full field description from DDR
            final FieldDescription desc = dr.getDescriptor().getFieldDescription(field.getTag());
            final Field f = new Field(desc);
            f.setValueAsByte(value);
            if(desc.getParent()!=null){
                //add field in it's parent
                final Field parent = dr.getField(desc.getParent().getTag());
                parent.getFields().add(f);
            }
            dr.getFields().add(f);
        }
        
        return dr;
    }
    
    public void dispose() throws IOException{
        if(closeOnDispose){
            stream.close();
        }
    }
    
}
