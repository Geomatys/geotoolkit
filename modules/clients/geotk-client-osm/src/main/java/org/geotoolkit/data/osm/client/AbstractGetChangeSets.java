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
package org.geotoolkit.data.osm.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.DateRange;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;


/**
 * Abstract implementation of {@link GetChangeSetsRequest}, which defines the
 * parameters for a get changesets request.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGetChangeSets extends AbstractRequest implements GetChangeSetsRequest{

    private Envelope envelope = null;
    private long userId = -1;
    private String userName = null;
    private DateRange dateRange = null;
    private boolean onlyClosed = false;
    private boolean onlyOpen = false;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     */
    protected AbstractGetChangeSets(final String serverURL, String subpath){
        super(serverURL, subpath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setEnvelope(Envelope env) {
        this.envelope = env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserId(long id) {
        this.userId = id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getUserName() {
        return userName;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setUserName(String name) {
        this.userName = name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DateRange getTimeRange() {
        return dateRange;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setTimeRange(DateRange range) {
        this.dateRange = range;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isOnlyOpenChangeSets() {
        return onlyOpen;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOnlyOpenChangeSets(boolean open) {
        this.onlyOpen = open;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isOnlyClosedChangeSets() {
        return onlyClosed;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setOnlyClosedChangeSets(boolean closed) {
        this.onlyClosed = closed;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
        if(envelope != null){
            final Envelope geoEnv;
            try {
                geoEnv = CRS.transform(envelope, DefaultGeographicCRS.WGS84);
            } catch (TransformException ex) {
                throw new IllegalArgumentException("Could not reproject given envelope to OSM projection", ex);
            }

            // bbox=left,bottom,right,top
            final StringBuilder sb = new StringBuilder();
            sb.append(geoEnv.getMinimum(0)).append(',').append(geoEnv.getMinimum(1)).append(',');
            sb.append(geoEnv.getMaximum(0)).append(',').append(geoEnv.getMaximum(1));
            requestParameters.put("bbox",sb.toString());
        }
        if(userId > 0 && userName != null){
            throw new IllegalArgumentException("Can not define both userId and userName at the same time");
        }
        if(userId > 0){
            requestParameters.put("user",String.valueOf(userId));
        }
        if(userName != null){
            requestParameters.put("display_name",userName);
        }

        if(dateRange != null){
            final Date min = dateRange.getMinValue();
            final Date max = dateRange.getMaxValue();
            if(min == null){
                throw new IllegalArgumentException("Date range is not valid, contain no starting date.");
            }
            final StringBuilder sb = new StringBuilder();
                sb.append(Converters.convert(min, String.class));
            if(max != null){
                sb.append(',');
                sb.append(Converters.convert(max, String.class));
            }
            requestParameters.put("time",sb.toString());
        }

        if(onlyOpen && onlyClosed){
            throw new IllegalArgumentException("Both open and only closed parameters can not be set to True.");
        }
        if(onlyOpen){
            requestParameters.put("open",DONT_ENCODE_EQUAL);
        }
        if(onlyClosed){
            requestParameters.put("closed",DONT_ENCODE_EQUAL);
        }

        return super.getURL();
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
