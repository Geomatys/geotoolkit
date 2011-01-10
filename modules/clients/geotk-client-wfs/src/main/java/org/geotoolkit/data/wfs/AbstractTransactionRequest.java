/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.data.wfs.xml.JAXPStreamTransactionWriter;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class AbstractTransactionRequest extends AbstractRequest implements TransactionRequest{

    protected final List<TransactionElement> elements = new ArrayList<TransactionElement>();
    protected final String version;
    protected String lockId = null;
    protected ReleaseAction release = null;

    protected AbstractTransactionRequest(final String serverURL, final String version){
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLockId() {
        return lockId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLockId(final String value) {
        this.lockId = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TransactionElement> elements() {
        return elements;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReleaseAction getReleaseAction() {
        return release;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setReleaseAction(final ReleaseAction value) {
        this.release = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {

        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        //write the transaction xml content
        final JAXPStreamTransactionWriter jaxp = new JAXPStreamTransactionWriter();
        try {
            jaxp.write(stream, this);
            //todo write request in this
        } catch (Exception ex) {
            throw new IOException(ex);
        }

        return conec.getInputStream();
    }

}
