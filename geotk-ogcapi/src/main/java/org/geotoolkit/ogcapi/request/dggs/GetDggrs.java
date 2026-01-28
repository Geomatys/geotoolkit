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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetDggrs extends RequestParameters {

    private String collectionId;
    private String dggrsId;
    private String format;

    /**
     * Local identifier of a collection (required if against a collection)
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
    public GetDggrs collectionId(String collectionId) {
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
    public GetDggrs dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
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
    public GetDggrs format(String format) {
        setFormat(format);
        return this;
    }
}
