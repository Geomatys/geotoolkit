/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;
import org.geotoolkit.util.StringUtilities;

/**
 * Abstract implementation of {@link CreateChangeSetRequest}, which defines the
 * parameters for a create change set request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractCreateChangeSet extends AbstractRequest implements CreateChangeSetRequest{

    protected ChangeSet cs = null;

    public AbstractCreateChangeSet(String serverURL, String subPath){
        super(serverURL, subPath);
    }

    @Override
    public void setChangeSet(ChangeSet cs) {
        this.cs = cs;
    }

    @Override
    public ChangeSet getChangeSet() {
        return cs;
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        final URL url = getURL();
        final URLConnection conec = url.openConnection();

        final HttpURLConnection ht = (HttpURLConnection) conec;
        ht.setRequestMethod("PUT");

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();
        try{
            final OSMXMLWriter writer = new OSMXMLWriter();
            writer.setOutput(stream);
            writer.writeStartDocument();
            writer.writeOSMTag();
            writer.writeChangeSet(cs);
            writer.writeEndDocument();
            writer.dispose();
        }catch(XMLStreamException ex){
            ex.printStackTrace();
        }finally{
            stream.close();
        }

        try{
            return conec.getInputStream();
        }catch(IOException ex){
            for(Entry<String,List<String>> entry : conec.getHeaderFields().entrySet()){
                System.out.println(entry.getKey() + " : ");
                System.out.println(StringUtilities.toCommaSeparatedValues(entry.getValue()));
            }
            throw ex;
        }
        
    }

}
