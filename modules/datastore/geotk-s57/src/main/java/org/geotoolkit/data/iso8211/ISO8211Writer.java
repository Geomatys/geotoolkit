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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.geotoolkit.io.LEDataOutputStream;

/**
 * ISO8211 Writer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ISO8211Writer {
    
    private Object output;
    private OutputStream stream;
    private DataOutput ds;
    private boolean closeOnDispose = false;

    public ISO8211Writer() {
    }

    public void reset(){
        output = null;
        stream = null;
        closeOnDispose = false;
    }
    
    public void setOutput(Object output) {
        this.output = output;
    }
    
    private DataOutput getDataOutput() throws IOException{
        if(ds!=null) return ds;
        
        if(output instanceof File){
            stream = new FileOutputStream((File)output);
            closeOnDispose = true;
        }else if(output instanceof OutputStream){
            stream = (OutputStream)output;
            closeOnDispose = false;
        }else{
            throw new IOException("Unsupported output : "+output);
        }
        ds = new LEDataOutputStream(stream);
        return ds;
    }
    
    /**
     * Write given DDR.
     * Warning : areaoffset and length will be updated on the record.
     * 
     * @param record
     * @throws IOException 
     */
    public void writeDataDescriptiveRecord(DataRecord record) throws IOException{
        final DataOutput ds = getDataOutput();
        
        //write the descriptions first to know the size of the field area.
//        final ByteArrayOutputStream tempstream = new ByteArrayOutputStream();
//        final DataOutput temp = new LEDataOutputStream(tempstream);
//        record.writeFieldDescriptions(temp);
//        final byte[] fieldArea = tempstream.toByteArray();
//        record.setRecordLength(recordLength);
//        record.setAreaAddress(areaAddress);
//        
//        final 
//        record.writeDescription(ds);
    }
    
    public void writeDataRecord(DataRecord record) throws IOException{
        final DataOutput ds = getDataOutput();
        record.writeDescription(ds);
        record.writeFieldValues(ds);
    }
    
    public void dispose() throws IOException{
        if(closeOnDispose){
            stream.close();
        }
    }
    
}
