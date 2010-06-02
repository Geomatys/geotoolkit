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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.v202.ElementSetNameType;
import org.geotoolkit.csw.xml.v202.GetRecordByIdType;
import org.geotoolkit.ebrim.xml.EBRIMClassesContext;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link GetRecordByIdRequest}, which defines the
 * parameters for a GetRecordById request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetRecordById extends AbstractRequest implements GetRecordByIdRequest {
    /**
     * Default logger for all GetRecordById requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetRecordById.class);

    private static final MarshallerPool POOL;
    static {
        MarshallerPool temp = null;
        try {
            temp = new MarshallerPool(EBRIMClassesContext.getAllClasses());
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
        }
        POOL = temp;
    }

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
    protected AbstractGetRecordById(final String serverURL, final String version){
        super(serverURL);
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
    public void setElementSetName(ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public void setIds(String... ids) {
        this.ids = ids;
    }

    @Override
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (ids == null) {
            throw new IllegalArgumentException("The parameter \"ID\" is not defined");
        }

        requestParameters.put("SERVICE", "CSW");
        requestParameters.put("REQUEST", "GetRecords");
        requestParameters.put("VERSION", version);
        requestParameters.put("ID",      StringUtilities.toCommaSeparatedValues(ids));

        if (elementSetName != null) {
            requestParameters.put("ELEMENTSETNAME", elementSetName.value());
        }
        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT", outputFormat);
        }
        if (outputSchema != null) {
            requestParameters.put("OUTPUTSCHEMA", outputSchema);
        }

        return super.getURL();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();
            final GetRecordByIdType recordByIdXml = new GetRecordByIdType("CSW", version,
                    new ElementSetNameType(elementSetName), outputFormat, outputSchema,
                    Arrays.asList(ids));
            marsh.marshal(recordByIdXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (POOL != null && marsh != null) {
                POOL.release(marsh);
            }
        }
        stream.close();
        return conec.getInputStream();
    }
}
