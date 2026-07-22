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
import org.locationtech.jts.geom.Geometry;

/**
 * The {@code corridor} EDR query
 * ({@code /collections/{collectionId}/corridor}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/corridor}).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCorridor extends AbstractEdrDataQuery {

    private Geometry coords;

    private Double corridorWidth;

    private Double corridorHeight;

    private String widthUnits;

    private String heightUnits;

    private Double resolutionX;

    private Double resolutionY;

    private String z;

    public GetCorridor collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetCorridor instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetCorridor datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetCorridor parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetCorridor crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetCorridor f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return a WKT {@code LINESTRING} geometry defining the corridor centerline.
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @param coords the corridor centerline.
     * @see #getCoords()
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    public GetCorridor coords(Geometry coords) {
        setCoords(coords);
        return this;
    }

    /**
     * @return the width of the corridor, in {@link #getWidthUnits()}.
     */
    public Double getCorridorWidth() {
        return corridorWidth;
    }

    /**
     * @param corridorWidth the width of the corridor.
     * @see #getCorridorWidth()
     */
    public void setCorridorWidth(Double corridorWidth) {
        this.corridorWidth = corridorWidth;
    }

    public GetCorridor corridorWidth(Double corridorWidth) {
        setCorridorWidth(corridorWidth);
        return this;
    }

    /**
     * @return the height (vertical extent) of the corridor, or {@code null}.
     */
    public Double getCorridorHeight() {
        return corridorHeight;
    }

    /**
     * @param corridorHeight the height (vertical extent) of the corridor.
     * @see #getCorridorHeight()
     */
    public void setCorridorHeight(Double corridorHeight) {
        this.corridorHeight = corridorHeight;
    }

    public GetCorridor corridorHeight(Double corridorHeight) {
        setCorridorHeight(corridorHeight);
        return this;
    }

    /**
     * @return the unit of {@link #getCorridorWidth()}.
     */
    public String getWidthUnits() {
        return widthUnits;
    }

    /**
     * @param widthUnits the unit of {@link #getCorridorWidth()}.
     * @see #getWidthUnits()
     */
    public void setWidthUnits(String widthUnits) {
        this.widthUnits = widthUnits;
    }

    public GetCorridor widthUnits(String widthUnits) {
        setWidthUnits(widthUnits);
        return this;
    }

    /**
     * @return the unit of {@link #getCorridorHeight()}, or {@code null}.
     */
    public String getHeightUnits() {
        return heightUnits;
    }

    /**
     * @param heightUnits the unit of {@link #getCorridorHeight()}.
     * @see #getHeightUnits()
     */
    public void setHeightUnits(String heightUnits) {
        this.heightUnits = heightUnits;
    }

    public GetCorridor heightUnits(String heightUnits) {
        setHeightUnits(heightUnits);
        return this;
    }

    /**
     * @return the resolution of the grid along the corridor path (x-axis), or {@code null}.
     */
    public Double getResolutionX() {
        return resolutionX;
    }

    /**
     * @param resolutionX the resolution of the grid along the corridor path (x-axis).
     * @see #getResolutionX()
     */
    public void setResolutionX(Double resolutionX) {
        this.resolutionX = resolutionX;
    }

    public GetCorridor resolutionX(Double resolutionX) {
        setResolutionX(resolutionX);
        return this;
    }

    /**
     * @return the resolution of the grid across the corridor path (y-axis), or {@code null}.
     */
    public Double getResolutionY() {
        return resolutionY;
    }

    /**
     * @param resolutionY the resolution of the grid across the corridor path (y-axis).
     * @see #getResolutionY()
     */
    public void setResolutionY(Double resolutionY) {
        this.resolutionY = resolutionY;
    }

    public GetCorridor resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
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

    public GetCorridor z(String z) {
        setZ(z);
        return this;
    }
}
