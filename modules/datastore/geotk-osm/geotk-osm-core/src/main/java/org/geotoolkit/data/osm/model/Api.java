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

    public double getAreaMaximum() {
        return areaMaximum;
    }

    public int getChangesetMaximum() {
        return changesetMaximum;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getTracePointsPerPage() {
        return tracePointsPerPage;
    }

    public String getVersionMaximum() {
        return versionMaximum;
    }

    public String getVersionMinimum() {
        return versionMinimum;
    }

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
