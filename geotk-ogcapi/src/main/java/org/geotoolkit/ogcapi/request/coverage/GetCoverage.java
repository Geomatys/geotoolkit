/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.request.coverage;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCoverage extends RequestParameters {

    private String collectionId;
    private String format;
    private List<Double> bbox;
    private String bboxCrs;
    private List<String> properties;
    private String datetime;

    /**
     * Local identifier of a collection (required)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetCoverage collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     */
    public GetCoverage format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Only coverage data within this bounding box shall be returned.
     *
     * @return the bbox
     */
    public List<Double> getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetCoverage bbox(List<Double> bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * Coordinate reference system of the bbox parameter.
     *
     * @return the bboxCrs
     */
    public String getBboxCrs() {
        return bboxCrs;
    }

    /**
     * @param bboxCrs the bboxCrs to set
     * @see #getBboxCrs()
     */
    public void setBboxCrs(String bboxCrs) {
        this.bboxCrs = bboxCrs;
    }

    /**
     * @param bboxCrs the bboxCrs to set
     * @see #getBboxCrs()
     */
    public GetCoverage bboxCrs(String bboxCrs) {
        setBboxCrs(bboxCrs);
        return this;
    }

    /**
     * Sub-set of range type fields to return.
     *
     * @return the properties
     */
    public List<String> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     * @see #getProperties()
     */
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    /**
     * @param properties the properties to set
     * @see #getProperties()
     */
    public GetCoverage properties(List<String> properties) {
        setProperties(properties);
        return this;
    }

    /**
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     */
    public GetCoverage datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }
}
