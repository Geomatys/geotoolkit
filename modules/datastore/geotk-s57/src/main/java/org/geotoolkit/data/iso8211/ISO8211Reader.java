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
package org.geotoolkit.data.iso8211;

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
import static org.geotoolkit.data.iso8211.ISO8211Constants.*;
import static org.geotoolkit.data.iso8211.ISO8211Utilities.*;
import org.geotoolkit.io.LEDataInputStream;

/**
 * ISO8211 Reader.
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
        ddr.readFieldDescriptions(ds);
    }
    
    private DataRecord readRecord(final DataInput ds) throws IOException{
        final DataRecord dr = new DataRecord(ddr);
        try{
            dr.readDescription(ds);
        }catch(EOFException ex){
            //no more records
            return null;
        }
        dr.readFieldValues(ds);
        return dr;
    }
    
    public void dispose() throws IOException{
        if(closeOnDispose){
            stream.close();
        }
    }
    
}
