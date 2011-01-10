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
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.data.osm.model.Node;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;
import org.opengis.geometry.Envelope;

/**
 * Abstract implementation of {@link ExpandChangeSetRequest}, which defines the
 * parameters for an expand change set request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractExpandChangeSet extends AbstractRequest implements ExpandChangeSetRequest{

    protected int id = -1;
    protected Envelope env = null;

    public AbstractExpandChangeSet(final String serverURL, final String subPath){
        super(serverURL, subPath);
    }

    @Override
    public int getChangeSetID() {
        return id;
    }

    @Override
    public void setChangeSetID(final int id) {
        this.id = id;
    }

    @Override
    public Envelope getEnvelope() {
        return env;
    }

    @Override
    public void setEnvelope(final Envelope env) {
        this.env = env;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        if(env == null){
            throw new IllegalArgumentException("Envelope was not defined.");
        }
        if(id <= 0){
            throw new IllegalArgumentException("ChangeSet ID is not defined.");
        }

        final URL url = getURL();
        final URLConnection conec = url.openConnection();

        final HttpURLConnection ht = (HttpURLConnection) conec;
        ht.setRequestMethod("POST");

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final Node node1 = new Node(env.getMinimum(1), env.getMinimum(0), -1, -1, -1, null, -1, null);
        final Node node2 = new Node(env.getMaximum(1), env.getMaximum(0), -1, -1, -1, null, -1, null);

        final OutputStream stream = conec.getOutputStream();
        try{
            final OSMXMLWriter writer = new OSMXMLWriter();
            writer.setOutput(stream);
            writer.writeStartDocument();
            writer.writeOSMTag();
            writer.writeNode(node1);
            writer.writeNode(node2);
            writer.writeEndDocument();
            writer.dispose();
        }catch(XMLStreamException ex){
            ex.printStackTrace();
        }finally{
            stream.close();
        }

        return openRichException(conec);
    }

}
