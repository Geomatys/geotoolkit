/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2016, Geomatys
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
package org.geotoolkit.wps.client;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.client.AbstractClient;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.ows.xml.ExceptionResponse;
import org.geotoolkit.ows.xml.v200.AcceptVersionsType;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Capabilities;
import org.geotoolkit.wps.xml.v200.DescribeProcess;
import org.geotoolkit.wps.xml.v200.Execute;
import org.geotoolkit.wps.xml.v200.GetCapabilities;
import org.geotoolkit.wps.xml.v200.GetStatus;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * WPS server, used to acquire capabilities and requests process.
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WebProcessingClient extends AbstractClient {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wps");
    private static final int TIMEOUT_CAPS = 10000;

    private Capabilities capabilities;
    private boolean forceGET = true;

    /**
     * Constructor
     */
    public WebProcessingClient(final URL serverURL, final String version) {
        this(serverURL, null, version, true, null, false);
    }

    /**
     * Costructor with forceGET tunning.
     *
     * @param forceGET if true, GetCapabilities and DescribeProcess will be request in GET, otherwise POST is used.
     */
    public WebProcessingClient(final URL serverURL, final String version, final boolean forceGET) {
        this(serverURL, null, version, forceGET, null, false);
    }

    /**
     * Constructor
     * Auto detect version.
     */
    public WebProcessingClient(final URL serverURL) {
        this(serverURL,null,null,true, null, false);
    }

    /**
     * Constructor
     * Auto detect version.
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security) {
        this(serverURL,security,null,true, null, false);
    }

    /**
     * Constructor
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final String version) {
        this(serverURL,security,version,true, null, false);
    }

    /**
     * Constructor
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final WPSVersion version) {
        this(serverURL, security, version==null?null:version.getCode(), true, null, false);
    }

    /**
     * Constructor
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final WPSVersion version, final Integer timeout) {
        this(serverURL, security, version==null?null:version.getCode(), true, timeout, false);
    }

    /**
     * Constructor
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, final WPSVersion version, final Integer timeout, final Boolean dynamicLoading) {
        this(serverURL, security, version==null?null:version.getCode(), true, timeout, dynamicLoading);
    }

    /**
     * Constructor
     *
     * @param forceGET if true, GetCapabilities and DescribeProcess will be request in GET, otherwise POST is used.
     */
    public WebProcessingClient(final URL serverURL, final ClientSecurity security, String version, final boolean forceGET, final Integer timeout, final Boolean dynamicLoading) {
        super(create(WPSProvider.PARAMETERS, serverURL, security, timeout));

        if(version==null || "auto".equalsIgnoreCase(version)){
            //if version is null, call getCapabilities to found service version
            if(LOGGER.isLoggable(Level.FINE)){
                LOGGER.log(Level.FINE, "No version define : search it on getCapabilities");
            }
            try {
                this.capabilities = getServiceCapabilities();
                //set version
                version = WPSVersion.getVersion(this.capabilities.getVersion()).getCode();
            } catch (CapabilitiesException e) {
                LOGGER.log(Level.WARNING,  e.getLocalizedMessage(), e);
                version = WPSVersion.v200.getCode();
            }
        }
        if (dynamicLoading != null) {
            Parameters.castOrWrap(parameters).getOrCreate(WPSProvider.DYNAMIC_LOADING).setValue(dynamicLoading);
        }
        if (version.equals("1.0.0")) {
            Parameters.castOrWrap(parameters).getOrCreate(WPSProvider.VERSION).setValue(WPSVersion.v100.getCode());
        } else if (version.equals("2.0.0")) {
            Parameters.castOrWrap(parameters).getOrCreate(WPSProvider.VERSION).setValue(WPSVersion.v200.getCode());
        } else {
            throw new IllegalArgumentException("Unknown version : " + version);
        }
        this.forceGET = forceGET;

        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }


    public WebProcessingClient(ParameterValueGroup params) {
        super(params);
        LOGGER.log(Level.INFO, "Web processing client initialization complete.");
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(WPSProvider.NAME);
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.empty();
    }

    /**
     * @return WPSVersion : currently used version for this server
     */
    public WPSVersion getVersion() {
        return WPSVersion.getVersion(Parameters.castOrWrap(parameters).getValue(WPSProvider.VERSION));
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    /**
     * @return WPSCapabilitiesType : WPS server capabilities
     */
    public Capabilities getServiceCapabilities() throws CapabilitiesException {
        return getServiceCapabilities(false);
    }

    /**
     * @param refresh if set to true, the cached capabilities document will be renewed.
     *
     * @return WPSCapabilitiesType : WPS server capabilities
     */
    public Capabilities getServiceCapabilities(boolean refresh) throws CapabilitiesException {
        if (capabilities != null && !refresh) {
            return capabilities;
        }

        final GetCapabilitiesRequest capaReq = createGetCapabilities();
        capaReq.setTimeout(TIMEOUT_CAPS);
        try (final InputStream is = capaReq.getResponseStream()) {
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = unmarshaller.unmarshal(is);
            if(obj instanceof JAXBElement) {
                obj = ((JAXBElement)obj).getValue();
            }
            if(obj instanceof ExceptionResponse) {
                final Exception er = ((ExceptionResponse) obj).toException();
                throw new CapabilitiesException(er.getMessage(), er);
            } else if (obj instanceof Capabilities) {
                capabilities = (Capabilities) obj;
            } else {
                throw new CapabilitiesException("Unexpected jaxb mapping for capabilities: "+(obj == null ? "null" : obj.getClass()));
            }
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
        } catch (Exception ex) {
            capabilities = null;
            throw new CapabilitiesException(ex.getMessage(), ex);
        }

        if (capabilities == null) {
            throw new CapabilitiesException("A problem occured while getting Service capabilities.");
        }
        LOGGER.log(Level.INFO, "GetCapabilities request succeed.");
        return capabilities;
    }

    /**
     * Create a getCapabilities request.
     *
     * @return GetCapabilitiesRequest : getCapabilities request.
     */
    public GetCapabilitiesRequest createGetCapabilities() {
        GetCapabilities cap = new GetCapabilities();
        cap.setService("WPS");
        final WPSVersion version = getVersion();
        if (version == null || WPSVersion.auto.equals(version)) {
            final String[] availableVersions = Arrays.stream(WPSVersion.values())
                    .filter(v -> !WPSVersion.auto.equals(v))
                    .map(WPSVersion::getCode)
                    .toArray(size -> new String[size]);
            cap.setAcceptVersions(new AcceptVersionsType(availableVersions));
        } else {
            cap.setAcceptVersions(new AcceptVersionsType(version.getCode()));
        }

        final GetCapabilitiesRequest request = new GetCapabilitiesRequest(serverURL.toString(), getClientSecurity(), forceGET, getTimeOutValue());
        request.setContent(cap);

        return request;
    }

    /**
     * Create a describe process request
     *
     * @return DescribeProcessRequest : describe process request.
     */
    public DescribeProcessRequest createDescribeProcess() {
        final WPSVersion version = ensureVersionSet();
        final DescribeProcessRequest request = new DescribeProcessRequest(serverURL.toString(), getClientSecurity(), forceGET, getTimeOutValue());
        DescribeProcess content = new DescribeProcess();
        content.setService("WPS");
        content.setVersion(version.getCode());
        request.setContent(content);

        return request;
    }

    /**
     * Perform a DescribeProcess request on the specified identifiers.
     *
     * @param processIDs List of process Identifiers
     * @return ProcessDescriptions : WPS process description
     */
    public ProcessOfferings getDescribeProcess(final List<String> processIDs) throws Exception {

        ProcessOfferings description;

        //Thread to prevent infinite request on a server
        final DescribeProcessRequest describe = createDescribeProcess();
        describe.setTimeout(getTimeOutValue());
        describe.getContent().setIdentifier(processIDs);
        try (final InputStream request = describe.getResponseStream()) {
            final Unmarshaller unmarshaller = WPSMarshallerPool.getInstance().acquireUnmarshaller();
            Object response = unmarshaller.unmarshal(request);
            WPSMarshallerPool.getInstance().recycle(unmarshaller);
            if (response instanceof ProcessOfferings) {
                description = (ProcessOfferings) response;
            } else if (response instanceof ExceptionResponse) {
                ExceptionResponse report = (ExceptionResponse) response;
                throw report.toException();
            } else {
                throw new Exception("Unexpected response type from the WPS server: "+(response == null? "null" : response.getClass()));
            }
        }
        return description;
    }

    /**
     * Create an execute request
     *
     * @return Execute : execute request.
     */
    public ExecuteRequest createExecute() {
        final WPSVersion version = ensureVersionSet();
        final Execute content = new Execute();
        content.setService("WPS");
        content.setVersion(version.getCode());

        return new ExecuteRequest(content, serverURL.toString(), getClientSecurity(), getTimeOutValue());
    }

    /**
     * Create a GetStatus request
     *
     * @param jobId The job identifier.
     */
    public GetStatusRequest createGetStatus(final String jobId) {
        final WPSVersion version = ensureVersionSet();
        final GetStatusRequest request = new GetStatusRequest(serverURL.toString(), getClientSecurity(), true, getTimeOutValue());

        switch (version) {
            case v100:
                throw new IllegalArgumentException("GetStatus requests are not available in WPS 1.0.0");
            default:
                final GetStatus content = new GetStatus("WPS", "2.0.0", jobId);
                content.setVersion(version.getCode());
                request.setContent(content);
        }
        return request;
    }

    /**
     * Create a GetResult request
     *
     * @param jobId The job identifier.
     */
    public GetResultRequest createGetResult(final String jobId) {

        final GetResultRequest request = new GetResultRequest(serverURL.toString(), getClientSecurity(), true, getTimeOutValue());

        switch (getVersion()) {
            case v100:
                throw new IllegalArgumentException("GetResult requests are not available in WPS 1.0.0");
            case v200: {
                final org.geotoolkit.wps.xml.v200.GetResult content = new org.geotoolkit.wps.xml.v200.GetResult();
                content.setService("WPS");
                content.setVersion("2.0.0");
                content.setJobID(jobId);
                request.setContent(content);
                } break;
            default:
                throw new IllegalArgumentException("Version was not defined or unsupported.");
        }
        return request;
    }

    /**
     * Create a Dismiss request
     *
     * @param jobId The job identifier.
     */
    public DismissRequest createDismiss(final String jobId) {

        final DismissRequest request = new DismissRequest(serverURL.toString(), getClientSecurity(), true, getTimeOutValue());

        switch (getVersion()) {
            case v100:
                throw new IllegalArgumentException("Dismiss requests are not available in WPS 1.0.0");
            case v200: {
                final org.geotoolkit.wps.xml.v200.Dismiss content = new org.geotoolkit.wps.xml.v200.Dismiss();
                content.setService("WPS");
                content.setVersion("2.0.0");
                content.setJobID(jobId);
                request.setContent(content);
                } break;
            default:
                throw new IllegalArgumentException("Version was not defined or unsupported.");
        }
        return request;
    }

    /**
     * Verify this client is configured with a well-known WPS version. Note that
     * {@link WPSVersion#auto} is considered as illegal by this method.
     *
     * @return The currently set version, once checked.
     */
    private WPSVersion ensureVersionSet() {
        final WPSVersion version = getVersion();
        if (version == null || WPSVersion.auto.equals(version)) {
            throw new IllegalArgumentException("Version not defined (auto is not allowed here)");
        }

        return version;
    }
}
