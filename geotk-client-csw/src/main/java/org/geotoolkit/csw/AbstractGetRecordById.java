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
package org.geotoolkit.csw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import static org.geotoolkit.csw.AbstractCSWRequest.POOL;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.csw.xml.CswXmlFactory;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ElementSetName;
import org.geotoolkit.csw.xml.GetRecordById;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.util.StringUtilities;


/**
 * Abstract implementation of {@link GetRecordByIdRequest}, which defines the
 * parameters for a GetRecordById request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public abstract class AbstractGetRecordById extends AbstractCSWRequest implements GetRecordByIdRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private ElementSetType elementSetName = null;
    private String[] ids = null;
    private String outputFormat = null;
    private String outputSchema = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetRecordById(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security);
        this.version = version;
    }

    @Override
    public ElementSetType getElementSetName() {
        return elementSetName;
    }

    @Override
    public String[] getIds() {
        return ids;
    }

    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    @Override
    public void setElementSetName(final ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public void setIds(final String... ids) {
        this.ids = ids;
    }

    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setOutputSchema(final String outputSchema) {
        this.outputSchema = outputSchema;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (ids == null) {
            throw new IllegalArgumentException("The parameter \"ID\" is not defined");
        }

        requestParameters.put("SERVICE", "CSW");
        requestParameters.put("REQUEST", "GetRecordById");
        requestParameters.put("VERSION", version);
        requestParameters.put("ID",      StringUtilities.toCommaSeparatedValues((Object[])ids));

        if (elementSetName != null) {
            requestParameters.put("ELEMENTSETNAME", elementSetName.value());
        }
        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT", outputFormat);
        }
        if (outputSchema != null) {
            requestParameters.put("OUTPUTSCHEMA", outputSchema);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        URLConnection conec = url.openConnection();
        conec = security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        try {
            final Marshaller marsh = POOL.acquireMarshaller();
            final ElementSetName set = CswXmlFactory.createElementSetName(version, elementSetName);
            final GetRecordById recordByIdXml = CswXmlFactory.createGetRecordById(version, "CSW",
                     set, outputFormat, outputSchema, Arrays.asList(ids));
            marsh.marshal(recordByIdXml, stream);
            POOL.recycle(marsh);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
}
