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
package org.geotoolkit.ogcwxs.request.coverage;

import java.awt.Dimension;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * To retrieve a coverage, encoded in a given format.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCoverage extends RequestParameters {

    private WCSVersion version;
    private String coverage;
    private Envelope envelope;
    private Dimension dimension;
    private Double resX;
    private Double resY;
    private Double resZ;
    private Integer depth;
    private String format;
    private CoordinateReferenceSystem responseCRS;
    private String time;
    private String exceptions;

    /**
     * The WCS version to request. Defaults to {@link WCSVersion#v100} if left {@code null}.
     *
     * @return the version
     */
    public WCSVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public void setVersion(WCSVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     */
    public GetCoverage version(WCSVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Coverage to retrieve. (required)
     *
     * @return the coverage
     */
    public String getCoverage() {
        return coverage;
    }

    /**
     * @param coverage the coverage to set
     * @see #getCoverage()
     */
    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }

    /**
     * @param coverage the coverage to set
     * @see #getCoverage()
     */
    public GetCoverage coverage(String coverage) {
        setCoverage(coverage);
        return this;
    }

    /**
     * Bounding box of the requested coverage, carrying its coordinate reference system. Only its first
     * two (or three, if the coverage has a vertical dimension) dimensions are used. (required)
     *
     * @return the envelope
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * @param envelope the envelope to set
     * @see #getEnvelope()
     */
    public void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    /**
     * @param envelope the envelope to set
     * @see #getEnvelope()
     */
    public GetCoverage envelope(Envelope envelope) {
        setEnvelope(envelope);
        return this;
    }

    /**
     * Width and height in pixels of the returned coverage. Mutually exclusive with {@link #getResX()} /
     * {@link #getResY()}. (optional)
     * <p>
     * WCS 1.x only: the WCS 2.x core KVP binding has no Scaling-extension equivalent; setting this for
     * a 2.x request makes {@code WCSApi.getCoverage} throw {@link IllegalArgumentException}.
     * </p>
     *
     * @return the dimension
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     * @see #getDimension()
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * @param dimension the dimension to set
     * @see #getDimension()
     */
    public GetCoverage dimension(Dimension dimension) {
        setDimension(dimension);
        return this;
    }

    /**
     * Requested resolution along the first grid axis. Mutually exclusive with {@link #getDimension()}.
     * WCS 1.x only, see {@link #getDimension()}. (optional)
     *
     * @return the resX
     */
    public Double getResX() {
        return resX;
    }

    /**
     * @param resX the resX to set
     * @see #getResX()
     */
    public void setResX(Double resX) {
        this.resX = resX;
    }

    /**
     * @param resX the resX to set
     * @see #getResX()
     */
    public GetCoverage resX(Double resX) {
        setResX(resX);
        return this;
    }

    /**
     * Requested resolution along the second grid axis. Mutually exclusive with {@link #getDimension()}.
     * WCS 1.x only, see {@link #getDimension()}. (optional)
     *
     * @return the resY
     */
    public Double getResY() {
        return resY;
    }

    /**
     * @param resY the resY to set
     * @see #getResY()
     */
    public void setResY(Double resY) {
        this.resY = resY;
    }

    /**
     * @param resY the resY to set
     * @see #getResY()
     */
    public GetCoverage resY(Double resY) {
        setResY(resY);
        return this;
    }

    /**
     * Requested resolution along the third (vertical) grid axis, for coverages having one. WCS 1.x
     * only, see {@link #getDimension()}. (optional)
     *
     * @return the resZ
     */
    public Double getResZ() {
        return resZ;
    }

    /**
     * @param resZ the resZ to set
     * @see #getResZ()
     */
    public void setResZ(Double resZ) {
        this.resZ = resZ;
    }

    /**
     * @param resZ the resZ to set
     * @see #getResZ()
     */
    public GetCoverage resZ(Double resZ) {
        setResZ(resZ);
        return this;
    }

    /**
     * Number of slices to return along the vertical dimension, for coverages having one. WCS 1.x
     * only, see {@link #getDimension()}. (optional)
     *
     * @return the depth
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * @param depth the depth to set
     * @see #getDepth()
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    /**
     * @param depth the depth to set
     * @see #getDepth()
     */
    public GetCoverage depth(Integer depth) {
        setDepth(depth);
        return this;
    }

    /**
     * Output mime-type of the coverage. (required)
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public GetCoverage format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Coordinate reference system in which the coverage should be returned. Leave {@code null} to use
     * the native CRS of the coverage. (optional)
     *
     * @return the responseCRS
     */
    public CoordinateReferenceSystem getResponseCRS() {
        return responseCRS;
    }

    /**
     * @param responseCRS the responseCRS to set
     * @see #getResponseCRS()
     */
    public void setResponseCRS(CoordinateReferenceSystem responseCRS) {
        this.responseCRS = responseCRS;
    }

    /**
     * @param responseCRS the responseCRS to set
     * @see #getResponseCRS()
     */
    public GetCoverage responseCRS(CoordinateReferenceSystem responseCRS) {
        setResponseCRS(responseCRS);
        return this;
    }

    /**
     * Time value or period to request on coverages declaring a temporal dimension. WCS 1.x only,
     * see {@link #getDimension()}. (optional)
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     * @see #getTime()
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @param time the time to set
     * @see #getTime()
     */
    public GetCoverage time(String time) {
        setTime(time);
        return this;
    }

    /**
     * Format in which to report exceptions. (optional)
     *
     * @return the exceptions
     */
    public String getExceptions() {
        return exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     * @see #getExceptions()
     */
    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     * @see #getExceptions()
     */
    public GetCoverage exceptions(String exceptions) {
        setExceptions(exceptions);
        return this;
    }

}
