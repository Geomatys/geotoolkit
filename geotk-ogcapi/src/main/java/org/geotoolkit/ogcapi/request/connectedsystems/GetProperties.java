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
package org.geotoolkit.ogcapi.request.connectedsystems;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code GET} operation on {@code /properties}: lists Property definition resources.
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GetProperties extends RequestParameters {

    private List<String> id;
    private List<String> keyword;
    private List<String> baseProperty;
    private List<String> objectType;
    private Integer limit;

    /**
     * Only resources with one of the given identifiers are selected. (optional)
     *
     * @return the id
     */
    public List<String> getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public void setId(List<String> id) {
        this.id = id;
    }

    /**
     * @param id the id to set
     * @see #getId()
     */
    public GetProperties id(List<String> id) {
        setId(id);
        return this;
    }

    /**
     * Only resources that match one of the given keywords are selected. (optional)
     *
     * @return the keyword
     */
    public List<String> getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     * @see #getKeyword()
     */
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    /**
     * @param keyword the keyword to set
     * @see #getKeyword()
     */
    public GetProperties keyword(List<String> keyword) {
        setKeyword(keyword);
        return this;
    }

    /**
     * Only properties derived from one of the given base property URIs are selected. (optional)
     *
     * @return the baseProperty
     */
    public List<String> getBaseProperty() {
        return baseProperty;
    }

    /**
     * @param baseProperty the baseProperty to set
     * @see #getBaseProperty()
     */
    public void setBaseProperty(List<String> baseProperty) {
        this.baseProperty = baseProperty;
    }

    /**
     * @param baseProperty the baseProperty to set
     * @see #getBaseProperty()
     */
    public GetProperties baseProperty(List<String> baseProperty) {
        setBaseProperty(baseProperty);
        return this;
    }

    /**
     * Only properties applicable to one of the given object types are selected. (optional)
     *
     * @return the objectType
     */
    public List<String> getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     * @see #getObjectType()
     */
    public void setObjectType(List<String> objectType) {
        this.objectType = objectType;
    }

    /**
     * @param objectType the objectType to set
     * @see #getObjectType()
     */
    public GetProperties objectType(List<String> objectType) {
        setObjectType(objectType);
        return this;
    }

    /**
     * The optional limit parameter limits the number of items presented in the response. (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetProperties limit(Integer limit) {
        setLimit(limit);
        return this;
    }

}
