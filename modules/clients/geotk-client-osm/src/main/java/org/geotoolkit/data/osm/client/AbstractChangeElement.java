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
import org.geotoolkit.data.osm.model.IdentifiedElement;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;

/**
 * Abstract implementation of {@link ChangeElementRequest}, which defines the
 * parameters for a change element request. the change may be Create/Update/Delete.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractChangeElement extends AbstractRequest implements ChangeElementRequest{

    protected final Type type;
    protected IdentifiedElement element = null;

    public AbstractChangeElement(final String serverURL, final String subPath, final Type type){
        super(serverURL, subPath);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public IdentifiedElement getElement() {
        return element;
    }

    @Override
    public void setElement(final IdentifiedElement element) {
        this.element = element;
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        final URLConnection conec = url.openConnection();

        final HttpURLConnection ht = (HttpURLConnection) conec;
        switch(type){
            case CREATE : ht.setRequestMethod("PUT");break;
            case UPDATE : ht.setRequestMethod("PUT");break;
            case DELETE : ht.setRequestMethod("DELETE");break;
        }

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();
        try{
            final OSMXMLWriter writer = new OSMXMLWriter();
            writer.setOutput(stream);
            writer.writeStartDocument();
            writer.writeOSMTag();
            writer.writeElement(element);
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
