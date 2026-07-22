/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.request.edr;

import java.util.List;
import org.opengis.geometry.Envelope;

/**
 * The {@code cube} EDR query
 * ({@code /collections/{collectionId}/cube}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/cube}).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCube extends AbstractEdrDataQuery {

    private Envelope bbox;

    private String z;

    private Double resolutionX;

    private Double resolutionY;

    private Double resolutionZ;

    public GetCube collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetCube instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetCube datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetCube parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetCube crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetCube f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return the bounding box defining the extent of the data cube.
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bounding box defining the extent of the data cube.
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    public GetCube bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @return the vertical level(s) filter, or {@code null}.
     */
    public String getZ() {
        return z;
    }

    /**
     * @param z the vertical level(s) filter.
     * @see #getZ()
     */
    public void setZ(String z) {
        this.z = z;
    }

    public GetCube z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @return the number of sample points in the response array along the x axis, or
     * {@code null}.
     */
    public Double getResolutionX() {
        return resolutionX;
    }

    /**
     * @param resolutionX the number of sample points in the response array along the x axis.
     * @see #getResolutionX()
     */
    public void setResolutionX(Double resolutionX) {
        this.resolutionX = resolutionX;
    }

    public GetCube resolutionX(Double resolutionX) {
        setResolutionX(resolutionX);
        return this;
    }

    /**
     * @return the number of sample points in the response array along the y axis, or
     * {@code null}.
     */
    public Double getResolutionY() {
        return resolutionY;
    }

    /**
     * @param resolutionY the number of sample points in the response array along the y axis.
     * @see #getResolutionY()
     */
    public void setResolutionY(Double resolutionY) {
        this.resolutionY = resolutionY;
    }

    public GetCube resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
        return this;
    }

    /**
     * @return the number of sample points in the response array along the z axis, or
     * {@code null}.
     */
    public Double getResolutionZ() {
        return resolutionZ;
    }

    /**
     * @param resolutionZ the number of sample points in the response array along the z axis.
     * @see #getResolutionZ()
     */
    public void setResolutionZ(Double resolutionZ) {
        this.resolutionZ = resolutionZ;
    }

    public GetCube resolutionZ(Double resolutionZ) {
        setResolutionZ(resolutionZ);
        return this;
    }
}
