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


//        //write the namespaces
//        final StringBuilder sbNS = new StringBuilder("{");
//
//        sbNS.append("xmlns(").append(JAXPStreamTransactionWriter.GML_PREFIX).append('=').append(JAXPStreamTransactionWriter.GML_NAMESPACE).append(')').append(',');
//        sbNS.append("xmlns(").append(JAXPStreamTransactionWriter.OGC_PREFIX).append('=').append(JAXPStreamTransactionWriter.OGC_NAMESPACE).append(')').append(',');
//        sbNS.append("xmlns(").append(JAXPStreamTransactionWriter.WFS_PREFIX).append('=').append(JAXPStreamTransactionWriter.WFS_NAMESPACE).append(')').append(',');
//
//
//        if(sbNS.length() > 0 && sbNS.charAt(sbNS.length()-1) == ','){
//            sbNS.deleteCharAt(sbNS.length()-1);
//        }
//
//        sbNS.append("}");
//
//
//        conec.addRequestProperty("NAMESPACE", sbNS.toString());


        final OutputStream stream = conec.getOutputStream();

        //write the transaction xml content
        JAXPStreamTransactionWriter jaxp = new JAXPStreamTransactionWriter();
        try {
            jaxp.write(stream, this);
            //todo write request in this
        } catch (Exception ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }


        //todo write request in this


//        OutputStreamWriter wr = new OutputStreamWriter(conec.getOutputStream());
//        final InputStream is  = Util.getResourceAsStream("org/constellation/xml/Insert-SamplingPoint-1.xml");
//        StringWriter sw       = new StringWriter();
//        BufferedReader in     = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//        char[] buffer         = new char[1024];
//        int size;
//        while ((size = in.read(buffer, 0, 1024)) > 0) {
//            sw.append(new String(buffer, 0, size));
//        }
//
//        wr.write(sw.toString());
//        wr.flush();

        return conec.getInputStream();
    }

}
