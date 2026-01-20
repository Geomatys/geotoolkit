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
package org.geotoolkit.ogcapi.request.dggs;

import org.geotoolkit.ogcapi.request.RequestParameters;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetZone extends RequestParameters {

    private String collectionId;
    private String dggrsId;
    private String zoneId;
    private String format;
    private List<String> collections;
    private String datetime;

    /**
     * Local identifier of a collection (required if request on a collection)
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
    public GetZone collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Identifier for a supported Discrete Global Grid System (required)
     *
     * @return the dggrsId
     */
    public String getDggrsId() {
        return dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     * @see #getDggrsId()
     */
    public void setDggrsId(String dggrsId) {
        this.dggrsId = dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     * @see #getDggrsId()
     */
    public GetZone dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     *
     * @return the zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     * @see #getZoneId()
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     * @see #getZoneId()
     */
    public GetZone zoneId(String zoneId) {
        setZoneId(zoneId);
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
    public GetZone format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     *
     * @return the collections
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * @param collections the collections to set
     * @see #getCollections()
     */
    public GetZone collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetZone datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }
}
