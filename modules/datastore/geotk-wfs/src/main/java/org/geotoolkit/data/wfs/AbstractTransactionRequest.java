/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import org.geotoolkit.data.wfs.xml.JAXPStreamTransactionWriter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractTransactionRequest implements TransactionRequest{

    protected final List<TransactionElement> elements = new ArrayList<TransactionElement>();
    protected final String serverURL;
    protected final String version;
    protected String lockId = null;
    protected ReleaseAction release = null;

    protected AbstractTransactionRequest(String serverURL, String version){
        this.serverURL = serverURL;
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
    public void setLockId(String value) {
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
    public void setReleaseAction(ReleaseAction value) {
        this.release = value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponse() throws IOException{

        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream debug = System.out;
        final OutputStream stream = conec.getOutputStream();

        //write the transaction xml content
        JAXPStreamTransactionWriter jaxp = new JAXPStreamTransactionWriter();
        try {
            System.out.println("--------------------------------------------");
            jaxp.write(debug, this);
            debug.flush();
            System.out.println("-----------------------------------------------");
            jaxp.write(stream, this);
            //todo write request in this
        } catch (Exception ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }

        return conec.getInputStream();
    }

}
