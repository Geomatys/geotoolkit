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
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.data.osm.model.ChangeSet;
import org.geotoolkit.data.osm.model.Tag;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;

/**
 * Abstract implementation of {@link UpdateChangeSetRequest}, which defines the
 * parameters for an update change set request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractUpdateChangeSet extends AbstractRequest implements UpdateChangeSetRequest{

    protected int id = -1;
    protected final List<Tag> tags = new ArrayList<Tag>();

    public AbstractUpdateChangeSet(final OpenStreetMapServer server, final String subPath){
        super(server, subPath);
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
    public List<Tag> tags() {
        return tags;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        final HttpURLConnection ht = (HttpURLConnection) conec;
        ht.setRequestMethod("PUT");

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final ChangeSet cs = new ChangeSet(id, null, null, null, null, tags);

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
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

        return openRichException(conec);
    }

}
