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
package org.geotoolkit.ogcapi.request.styles;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To retrieve the list of styles currently available for a base resource (the dataset, or a
 * collection).
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetStylesList extends RequestParameters {

    private String collectionId;
    private String format;

    /**
     * Local identifier of a collection. Set to retrieve the styles of a single collection
     * ({@code /collections/{collectionId}/styles}). Leave {@code null} to retrieve the styles of the
     * whole dataset ({@code /styles}). (optional)
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
    public GetStylesList collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to determine the
     * format. (optional)
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
    public GetStylesList format(String format) {
        setFormat(format);
        return this;
    }

}
