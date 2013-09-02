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
import org.geotoolkit.data.iso8211.ISO8211Reader;

/**
 * Wrap an ISO 8211 Reader, returning S-57 objects for each field.
 *
 * Not thread safe.
 *
 * @author Johann Sorel (Geomatys)
 */
public class S57FileReader extends S57Reader {

    protected ISO8211Reader isoReader;

    public S57FileReader() {
    }

    @Override
    public void setInput(Object input){
        if(input instanceof ISO8211Reader){
            this.isoReader = (ISO8211Reader) input;
        }else{
            this.isoReader = new ISO8211Reader();
            this.isoReader.setInput(input);
        }
    }

    @Override
    protected void findNext() throws IOException {
        if(record != null){
            //already found
            return;
        }

        while(isoReader.hasNext() && record==null){
            final DataRecord rec = isoReader.next();
            if(predicate!=null && !predicate.match(rec)) continue;
            record = toS57Object(rec);
        }
    }

    @Override
    public void dispose() throws IOException{
        if(isoReader!= null){
            try {
                isoReader.close();
            } catch (Exception ex) {
               if(ex instanceof IOException){
                   throw (IOException)ex;
               }else{
                   throw new IOException(ex);
               }
            }
        }
    }

}
