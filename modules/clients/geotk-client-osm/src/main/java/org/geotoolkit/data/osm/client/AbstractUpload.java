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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.data.osm.model.Transaction;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;
import org.geotoolkit.util.StringUtilities;

/**
 * Abstract implementation of {@link UploadRequest}, which defines the
 * parameters for a upload request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractUpload extends AbstractRequest implements UploadRequest{

    protected final List<Transaction> transactions = new ArrayList<Transaction>();
    protected int changesetId = -1;
    protected String generator = "GeotoolKit.org";
    protected String version = "3";

    public AbstractUpload(String serverURL, String subPath){
        super(serverURL, subPath);
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getGenerator() {
        return generator;
    }

    @Override
    public void setGenerator(String generator) {
        this.generator = generator;
    }

    @Override
    public void setChangeSetID(int id){
        this.changesetId = id;
    }

    @Override
    public int getChangeSetID(){
        return changesetId;
    }

    @Override
    public List<Transaction> transactions() {
        return transactions;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();
        try{
            final OSMXMLWriter writer = new OSMXMLWriter();
            writer.setOutput(stream);
            writer.writeStartDocument();
            writer.writeOSMChangeTag(version, generator);
            for(Transaction trs : transactions){
                writer.writeTransaction(trs);
            }
            writer.writeEndDocument();
            writer.dispose();
        }catch(XMLStreamException ex){
            throw new IOException(ex);
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
