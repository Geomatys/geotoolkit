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
public final class GetDggrsDefinition extends RequestParameters {

    private String collectionId;
    private String dggrsId;

    /**
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     */
    public GetDggrsDefinition collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @return the dggrsId
     */
    public String getDggrsId() {
        return dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     */
    public void setDggrsId(String dggrsId) {
        this.dggrsId = dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     */
    public GetDggrsDefinition dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }
}
