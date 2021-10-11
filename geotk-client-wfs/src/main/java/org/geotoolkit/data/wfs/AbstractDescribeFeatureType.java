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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.geotoolkit.wfs.xml.DescribeFeatureType;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.geotoolkit.wfs.xml.WFSXmlFactory;


/**
 * Abstract describe feature request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class AbstractDescribeFeatureType extends AbstractRequest implements DescribeFeatureTypeRequest{

    protected final WFSVersion version;

    private List<QName> typeNames;

    private String outputFormat;

    public AbstractDescribeFeatureType(final String serverURL, final WFSVersion version, final ClientSecurity security){
        super(serverURL,security,null);
        this.version = version;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<QName> getTypeNames() {
        return typeNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTypeNames(final List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getOutputFormat() {
       return outputFormat;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        requestParameters.put("SERVICE",    "WFS");
        requestParameters.put("REQUEST",    "DescribeFeatureType");
        requestParameters.put("VERSION",    version.getCode());

        if (typeNames != null && !typeNames.isEmpty()) {
            final StringBuilder sbN = new StringBuilder();
            final StringBuilder sbNS = new StringBuilder();
            for(QName q : typeNames){
                sbN.append(q.getPrefix()).append(':').append(q.getLocalPart()).append(',');
                sbNS.append("xmlns(").append(q.getPrefix()).append('=').append(q.getNamespaceURI()).append(')').append(',');
            }

            if(sbN.charAt(sbN.length()-1) == ','){
                sbN.deleteCharAt(sbN.length()-1);
            }

            if(sbNS.charAt(sbNS.length()-1) == ','){
                sbNS.deleteCharAt(sbNS.length()-1);
            }

            requestParameters.put("TYPENAME",sbN.toString());
            requestParameters.put("NAMESPACE",sbNS.toString());
        }

        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT",outputFormat);
        }

        return super.getURL();
    }

    @Override
    public InputStream getResponseStream() throws IOException {
        DescribeFeatureType request = WFSXmlFactory.buildDecribeFeatureType(version.getCode(), "WFS", null, typeNames, outputFormat);

        final URL url = new URL(serverURL);
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);
        try (final OutputStream toClose = stream) {
            final Marshaller marshaller = WFSMarshallerPool.getInstance(version).acquireMarshaller();
            marshaller.marshal(request, stream);
            WFSMarshallerPool.getInstance().recycle(marshaller);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

        return security.decrypt(conec.getInputStream());
    }


}
