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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import static org.geotoolkit.data.s57.iso8211.ISO8211Constants.*;
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
    private DataDescriptiveRecord ddr;

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

    public DataDescriptiveRecord getDDR() throws IOException {
        if(ddr != null){
            return ddr;
        }
        final DataInput ds = getDataInput();
        readHeader(ds);
        return ddr;
    }
    
    private Record next() throws IOException {
        final DataInput ds = getDataInput();
        
        //read the header
        if(ddr == null){
            readHeader(ds);
        }
        
        
        return null;
    }
    
    private void readHeader(final DataInput ds) throws IOException {
        byte[] buffer = new byte[24];
        ds.readFully(buffer);
        
        //read header informations
        ddr = new DataDescriptiveRecord();
        ddr.setRecordLength(Integer.parseInt(new String(Arrays.copyOf(buffer, 5))));
        ddr.setInterchangeLevel(buffer[5]);
        ddr.setLeaderidentifier(buffer[6]);
        ddr.setExtensionIndicator(buffer[7]);
        ddr.setVersion(buffer[8]);
        ddr.setApplicationIndicator(buffer[9]);
        ddr.setFieldControlLength(Integer.parseInt(new String(Arrays.copyOfRange(buffer, 10, 12))));
        ddr.setAreaAddress(Integer.parseInt(new String(Arrays.copyOfRange(buffer, 12, 17))));
        ddr.setCharsetIndicator(Arrays.copyOfRange(buffer, 17, 20));
        
        //read field definition
        final int fieldLengthSize = Integer.parseInt(new String(Arrays.copyOfRange(buffer, 20, 21)));
        final int fieldPositionSize = Integer.parseInt(new String(Arrays.copyOfRange(buffer, 21, 22)));
        final int fieldReserved = Integer.parseInt(new String(Arrays.copyOfRange(buffer, 22, 23)));
        final int fieldSizeTag = Integer.parseInt(new String(Arrays.copyOfRange(buffer, 23, 24)));
        
        //count number of fields
        final int entryLength = fieldLengthSize + fieldPositionSize + fieldSizeTag;
        final int nbDirectory = (ddr.getAreaAddress()-24)/entryLength;
        
        //read directory definitions
        final List<Field> fields = ddr.getFields();
        buffer = new byte[entryLength];
        for(int i=0;i<nbDirectory;i++){
            ds.readFully(buffer);
            final Field field = new Field();
            field.setTag(new String(Arrays.copyOfRange(buffer, 0, fieldSizeTag)));
            field.setLength(Integer.parseInt(new String(Arrays.copyOfRange(buffer, fieldSizeTag, fieldSizeTag+fieldLengthSize))));
            field.setPosition(Integer.parseInt(new String(Arrays.copyOfRange(buffer, fieldSizeTag+fieldLengthSize, entryLength))));
            fields.add(field);            
        }        
        expect(ds,SFEND);
        
        //read field control field
        final Field fcf = ddr.getField("0000");
        final FieldDescription def = new FieldDescription();
        def.read(ds);
        expect(ds,FEND);
        //calculate number of pairs we will have
        final int nbPair = (fcf.getLenght()-11)/ (fieldSizeTag*2) ;
        buffer = new byte[fieldSizeTag];
        for(int i=0;i<nbPair;i++){
            ds.readFully(buffer);
            final String parentTag = new String(buffer);
            ds.readFully(buffer);
            final String childTag = new String(buffer);
            ddr.getField(parentTag).getSubFields().add(ddr.getField(childTag));
        }
        expect(ds,SFEND);
        
        
    }
    
    static void expect(final DataInput ds, final char val) throws IOException{
        char c = (char)ds.readByte();
        if(val!=c)throw new IOException("Unexpected value : "+c+" was expecting : "+val);
    }
    
    static void expect(final DataInput ds, final byte[] val) throws IOException{
        final byte[] buffer = new byte[val.length];        
        ds.readFully(buffer);
        if(!Arrays.equals(val, buffer))throw new IOException("Unexpected value : "+buffer+" was expecting : "+val);
    }
    
    public void dispose() throws IOException{
        if(closeOnDispose){
            stream.close();
        }
    }
    
}
