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
package org.geotoolkit.s52.lookuptable;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * S-52 Lookup table reader.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TxtLookupTableReader {


    private Object input;
    private InputStream stream;
    private DataInput ds;
    private boolean closeOnDispose = false;

    public TxtLookupTableReader() {
    }

    /**
     * Reset reader for new usage.
     */
    public void reset() throws IOException{
        dispose();
        input = null;
        stream = null;
        ds = null;
        closeOnDispose = false;
    }

    /**
     * Set reader input.
     * @param input
     * @throws IOException
     */
    public void setInput(Object input) throws IOException {
        reset();
        this.input = input;
    }

    /**
     * Get reader current input.
     * @return Object can be null
     */
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
        final BufferedInputStream bufferStream = new BufferedInputStream(stream);
        stream = bufferStream;

        ds = new DataInputStream(stream);
        return ds;
    }

    public LookupTable read() throws IOException {

        final DataInput ds = getDataInput();
        final LookupTable table = new LookupTable("unnamed");

        //read all records
        for(String line=ds.readLine(); line!=null; line=ds.readLine()){

            //skip comment lines
            if(line.isEmpty() || line.charAt(0) == '*') continue;

            final TxtLookupRecord record = new TxtLookupRecord();
            record.read(line);
            table.getRecords().add(record);
        }

        return table;
    }

    /**
     * Close and release resources used by this reader.
     * @throws IOException
     */
    public void dispose() throws IOException{
        if(closeOnDispose){
            stream.close();
        }
    }


}
