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
package org.geotoolkit.s52.dai;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * S-52 Digital presentation library file reader.
 * See S-52 Annex A Part 1 Chapter 10 (p.84)
 *
 * @author Johann Sorel (Geomatys)
 */
public class DAIReader {

    private static final Map<String,DAIField> FIELDS = new HashMap<>();
    static {
        final DAIField[] array = new DAIField[]{
            new AttributeCombination(),
            new ColorDefinitionCIE(),
            new ColorTableIdentifier(),
            new DisplayCategory(),
            new Instruction(),
            new LibraryIdentification(),
            new LinestyleColorReference(),
            new LinestyleDefinition(),
            new LinestyleExposition(),
            new LinestyleIdentifier(),
            new LinestyleVector(),
            new LookupComment(),
            new LookupTableEntryIdentifier(),
            new PatternBitmap(),
            new PatternColorReference(),
            new PatternDefinition(),
            new PatternExposition(),
            new PatternIdentifier(),
            new PatternVector(),
            new SymbolBitmap(),
            new SymbolColorReference(),
            new SymbolDefinition(),
            new SymbolExposition(),
            new SymbolIdentifier(),
            new SymbolVector()
        };
        for(DAIField df : array){
            FIELDS.put(df.getCode(), df);
        }
    }


    private Object input;
    private InputStream stream;
    private DataInput ds;
    private boolean closeOnDispose = false;

    private DAIModuleRecord next = null;

    public DAIReader() {
    }

    /**
     * Reset reader for new usage.
     */
    public void reset() throws IOException{
        dispose();
        input = null;
        stream = null;
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

    public boolean hasNext() throws IOException {
        findNext();
        return next != null;
    }

    public DAIModuleRecord next() throws IOException {
        findNext();
        DAIModuleRecord record = next;
        if(record == null){
            throw new IOException("No more records");
        }
        next = null;
        return record;
    }

    private void findNext() throws IOException {
        if(next!=null) return;

        final DataInput ds = getDataInput();
        String line = ds.readLine();
        if(line== null){
            //end of file
            return;
        }

        if(!line.startsWith("0001")){
            throw new IOException("Unvalid record ,expected line starting with '0001' but was : "+line);
        }

        final int recordId = Integer.valueOf(line.substring(4).trim());
        final DAIModuleRecord record = new DAIModuleRecord(recordId);

        //read all fields
        do{
            line = ds.readLine();
            if(line == null){
                throw new IOException("Reached end of file without record end.");
            }else if(line.startsWith("****")){
                //end of record
                next = record;
                return;
            }else{
                //read field
                final String code = line.substring(0, 4);
                DAIField field = FIELDS.get(code);
                if(field==null){
                    throw new IOException("Unknowned field code : "+code);
                }
                field = field.newInstance();
                field.read(line);
                record.getFields().add(field);
            }

        }while(true);


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
