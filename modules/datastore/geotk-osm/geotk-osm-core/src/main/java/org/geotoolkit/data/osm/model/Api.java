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

package org.geotoolkit.data.osm.model;

/**
 * Response object in the OSM server capabilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Api {

    private final String versionMinimum;
    private final String versionMaximum;
    private final double areaMaximum;
    private final int tracePointsPerPage;
    private final int wayNodeMaximum;
    private final int changesetMaximum;
    private final int timeout;

    public Api(String versionMinimum, String versionMaximum, double areaMaximum,
            int tracePointsPerPage, int wayNodeMaximum, int changesetMaximum, int timeout) {
        this.versionMinimum = versionMinimum;
        this.versionMaximum = versionMaximum;
        this.areaMaximum = areaMaximum;
        this.tracePointsPerPage = tracePointsPerPage;
        this.wayNodeMaximum = wayNodeMaximum;
        this.changesetMaximum = changesetMaximum;
        this.timeout = timeout;
    }

    /**
     * @return double the maximum area in square degrees that can be queried by API calls
     */
    public double getAreaMaximum() {
        return areaMaximum;
    }

    /**
     * @return maximum changeset number of elements that can be returned.
     */
    public int getChangesetMaximum() {
        return changesetMaximum;
    }

    /**
     * @return server time out
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @return number of trace points per page returned for GPX queries.
     */
    public int getTracePointsPerPage() {
        return tracePointsPerPage;
    }

    /**
     * @return maximum api version supported by this osm server.
     */
    public String getVersionMaximum() {
        return versionMaximum;
    }

    /**
     * @return minimum api version supported by this osm server.
     */
    public String getVersionMinimum() {
        return versionMinimum;
    }

    /**
     * @return number of points per way returned for way elements.
     */
    public int getWayNodeMaximum() {
        return wayNodeMaximum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("API : ");
        sb.append(" versionMinimum = ").append(versionMinimum);
        sb.append(" versionMaximum = ").append(versionMaximum);
        sb.append(" areaMaximum = ").append(areaMaximum);
        sb.append(" tracePointsPerPage = ").append(tracePointsPerPage);
        sb.append(" wayNodeMaximum = ").append(wayNodeMaximum);
        sb.append(" changesetMaximum = ").append(changesetMaximum);
        sb.append(" timeout = ").append(timeout);
        return sb.toString();
    }

}
