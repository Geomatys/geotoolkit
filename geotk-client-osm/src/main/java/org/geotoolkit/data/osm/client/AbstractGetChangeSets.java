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

import java.util.Date;

import org.geotoolkit.client.AbstractRequest;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.util.DateRange;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.geometry.Envelopes;


/**
 * Abstract implementation of {@link GetChangeSetsRequest}, which defines the
 * parameters for a get changesets request.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
     * @param server The server.
     */
    protected AbstractGetChangeSets(final OpenStreetMapClient server, final String subpath){
        super(server, subpath);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setEnvelope(final Envelope env) {
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
    public void setUserId(final long id) {
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
    public void setUserName(final String name) {
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
    public void setTimeRange(final DateRange range) {
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
    public void setOnlyOpenChangeSets(final boolean open) {
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
    public void setOnlyClosedChangeSets(final boolean closed) {
        this.onlyClosed = closed;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void prepareParameters() {
        if(envelope != null){
            final Envelope geoEnv;
            try {
                geoEnv = Envelopes.transform(envelope, CommonCRS.WGS84.normalizedGeographic());
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
                sb.append(ObjectConverters.convert(min, String.class));
            if(max != null){
                sb.append(',');
                sb.append(ObjectConverters.convert(max, String.class));
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

    }

}
