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
package org.geotoolkit.sos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.sos.xml.v100.DescribeSensor;
import org.geotoolkit.xml.MarshallerPool;


/**
 * Abstract implementation of {@link DescribeSensorRequest}, which defines the
 * parameters for a DescribeSensor request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractDescribeSensor extends AbstractRequest implements DescribeSensorRequest {
    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String outputFormat = null;
    private String sensorId = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractDescribeSensor(final String serverURL, final String version) {
        super(serverURL);
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if (outputFormat == null) {
            throw new IllegalArgumentException("The parameter \"outputFormat\" is not defined");
        }
        if (sensorId == null) {
            throw new IllegalArgumentException("The parameter \"sensorId\" is not defined");
        }
        requestParameters.put("SERVICE", "SOS");
        requestParameters.put("REQUEST", "GetCapabilities");
        requestParameters.put("VERSION", version);
        requestParameters.put("OUTPUTFORMAT", outputFormat);
        requestParameters.put("SENSORID", sensorId);
        return super.getURL();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        if (outputFormat == null) {
            throw new IllegalArgumentException("The parameter \"outputFormat\" is not defined");
        }
        if (sensorId == null) {
            throw new IllegalArgumentException("The parameter \"sensorId\" is not defined");
        }
        final URL url = new URL(serverURL);
        final URLConnection conec = url.openConnection();

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        final OutputStream stream = conec.getOutputStream();

        MarshallerPool pool = null;
        Marshaller marsh = null;
        try {
            pool = new MarshallerPool("org.geotoolkit.sos.xml.v100:" +
                                      "org.geotoolkit.gml.xml.v311:" +
                                      "org.geotoolkit.swe.xml.v100:" +
                                      "org.geotoolkit.swe.xml.v101:" +
                                      "org.geotoolkit.observation.xml.v100:" +
                                      "org.geotoolkit.sampling.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v100:" +
                                      "org.geotoolkit.sml.xml.v101");
            marsh = pool.acquireMarshaller();
            final DescribeSensor descSensorXml = new DescribeSensor(version, "SOS",
                    sensorId, outputFormat);
            marsh.marshal(descSensorXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (pool != null && marsh != null) {
                pool.release(marsh);
            }
        }
        stream.close();
        return conec.getInputStream();
    }

}
