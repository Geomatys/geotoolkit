/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ncwms;

import java.net.URL;

import org.geotoolkit.ncwms.v111.NcGetFeatureInfo111;
import org.geotoolkit.ncwms.v111.NcGetLegend111;
import org.geotoolkit.ncwms.v111.NcGetMap111;
import org.geotoolkit.ncwms.v111.NcGetTimeseries111;
import org.geotoolkit.ncwms.v130.NcGetFeatureInfo130;
import org.geotoolkit.ncwms.v130.NcGetLegend130;
import org.geotoolkit.ncwms.v130.NcGetMap130;
import org.geotoolkit.ncwms.v130.NcGetTimeseries130;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSVersion;
import org.geotoolkit.wms.WebMapClient;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Generates ncWMS requests objects.
 *
 * @author Olivier Terral (Geomatys)
 * @module pending
 */
public class NcWebMapClient extends WebMapClient{

    /**
     * {@inheritDoc }
     */
    public NcWebMapClient(final URL serverURL, final String version) {
        super(serverURL, WMSVersion.getVersion(version));
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapClient(final URL serverURL, final WMSVersion version) {
        super(serverURL, version, null);
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapClient(final URL serverURL, final String version, final AbstractWMSCapabilities capabilities) {
        super(serverURL, WMSVersion.getVersion(version), capabilities);
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapClient(final URL serverURL, final WMSVersion version, final AbstractWMSCapabilities capabilities) {
        super(serverURL, version, capabilities);
    }

    /**
     * {@inheritDoc }
     */
    public NcWebMapClient(final URL serverURL, final ClientSecurity security,
            final WMSVersion version, final AbstractWMSCapabilities capabilities) {
        super(serverURL, security, version, capabilities);
    }

    public NcWebMapClient(ParameterValueGroup params){
        super(params);
    }

    /**
     * Create a NcWebMapServer from a WebMapServer
     *
     * @param wms a WebMapServer
     */
    public NcWebMapClient(final WebMapClient wms) {
        super(wms.getURL(), wms.getVersion());
    }

    /**
     * Create a NcWebMapServer from a WebMapServer and a Getcapabilities
     *
     * @param wms a WebMapServer
     */
    public NcWebMapClient(final WebMapClient wms, final AbstractWMSCapabilities cap) {
        super(wms.getURL(), wms.getVersion(), cap);
    }

    @Override
    protected CoverageReference createReference(Name name) throws DataStoreException{
        return new NcWMSCoverageReference(this,name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetMapRequest createGetMap() {

        switch (getVersion()) {
            case v111:
                return new NcGetMap111(getURI().toString(),getClientSecurity());
            case v130:
                return new NcGetMap130(getURI().toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetLegendRequest createGetLegend(){

        switch (getVersion()) {
            case v111:
                return new NcGetLegend111(getURI().toString(),getClientSecurity());
            case v130:
                return new NcGetLegend130(getURI().toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public NcGetFeatureInfoRequest createGetFeatureInfo() {

        switch (getVersion()) {
            case v111:
                return new NcGetFeatureInfo111(getURI().toString(),getClientSecurity());
            case v130:
                return new NcGetFeatureInfo130(getURI().toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }

    /**
     * Returns the GetMetadata request object.
     * @return
     */
    public NcGetMetadataRequest createGetMetadata() {
        return new NcGetMetadata(getURI().toString());
    }

    /**
     * Returns the GetMetadata?item=menu request object.
     * @return
     */
    public NcGetMetadataRequest createGetMetadataMenu()  {
        final NcGetMetadataRequest request = createGetMetadata();
        request.setItem("menu");
        return request;
    }

    /**
     * Returns the GetMetadata request object.
     * @return
     */
    public NcGetMetadataMinMaxRequest  createGetMetadataMinMax() {
        final NcGetMetadataMinMaxRequest request = new NcGetMetadataMinMax(getURI().toString());
        request.setItem("minmax");
        return request;
    }

    /**
     * Returns the GetTransect request object.
     * @return
     */
    public NcGetTransectRequest createGetTransect() {
        return new NcGetTransect(getURI().toString());
    }

    /**
     * Returns the GetVerticalProfile request object.
     * @return
     */
    public NcGetVerticalProfileRequest createGetVerticalProfile() {
        return new NcGetVerticalProfile(getURI().toString());
    }

    /**
     * Returns the GetTimeseries request object.
     * @return
     */
    public NcGetTimeseriesRequest createGetTimeseries() {

        switch (getVersion()) {
            case v111:
                return new NcGetTimeseries111(getURI().toString(),getClientSecurity());
            case v130:
                return new NcGetTimeseries130(getURI().toString(),getClientSecurity());
            default:
                throw new IllegalArgumentException("Version was not defined");
        }
    }
}
