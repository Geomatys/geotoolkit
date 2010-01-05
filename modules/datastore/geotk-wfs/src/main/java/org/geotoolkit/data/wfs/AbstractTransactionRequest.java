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
import org.geotoolkit.wfs.xml.v110.TransactionType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class AbstractTransactionRequest extends TransactionType implements TransactionRequest{

    protected final String serverURL;
    protected final String version;

    protected AbstractTransactionRequest(String serverURL, String version){
        this.serverURL = serverURL;
        this.version = version;
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

        OutputStream stream = conec.getOutputStream();

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
