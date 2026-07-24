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
package org.geotoolkit.ogcapi.dto.connectedsystems.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * A web link, as used throughout OGC API - Connected Systems (Parts 1 and 2).
 *
 * <p>Distinct from {@link org.geotoolkit.ogcapi.dto.common.Link}: here only {@code href} is
 * required (not {@code href}+{@code rel}), and this type additionally carries {@code uid},
 * {@code rt} and {@code if}.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class Link extends DataTransferObject {

    public static final String JSON_PROPERTY_HREF = "href";
    private String href;

    public static final String JSON_PROPERTY_REL = "rel";
    private String rel;

    public static final String JSON_PROPERTY_TYPE = "type";
    private String type;

    public static final String JSON_PROPERTY_HREFLANG = "hreflang";
    private String hreflang;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    public static final String JSON_PROPERTY_UID = "uid";
    private String uid;

    public static final String JSON_PROPERTY_RT = "rt";
    private String rt;

    public static final String JSON_PROPERTY_IF = "if";
    private String ifRelation;

    /**
     * URL of the target resource. (required)
     */
    @JsonProperty(JSON_PROPERTY_HREF)
    public String getHref() {
        return href;
    }

    @JsonProperty(JSON_PROPERTY_HREF)
    public void setHref(String href) {
        this.href = href;
    }

    public Link href(String href) {
        setHref(href);
        return this;
    }

    /**
     * Relation type of the link. (optional)
     */
    @JsonProperty(JSON_PROPERTY_REL)
    public String getRel() {
        return rel;
    }

    @JsonProperty(JSON_PROPERTY_REL)
    public void setRel(String rel) {
        this.rel = rel;
    }

    public Link rel(String rel) {
        setRel(rel);
        return this;
    }

    /**
     * Media type of the target resource. (optional)
     */
    @JsonProperty(JSON_PROPERTY_TYPE)
    public String getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    public void setType(String type) {
        this.type = type;
    }

    public Link type(String type) {
        setType(type);
        return this;
    }

    /**
     * Language of the target resource. (optional)
     */
    @JsonProperty(JSON_PROPERTY_HREFLANG)
    public String getHreflang() {
        return hreflang;
    }

    @JsonProperty(JSON_PROPERTY_HREFLANG)
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public Link hreflang(String hreflang) {
        setHreflang(hreflang);
        return this;
    }

    /**
     * Human readable title of the target resource. (optional)
     */
    @JsonProperty(JSON_PROPERTY_TITLE)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    public void setTitle(String title) {
        this.title = title;
    }

    public Link title(String title) {
        setTitle(title);
        return this;
    }

    /**
     * Unique identifier (URI) of the target resource. (optional)
     */
    @JsonProperty(JSON_PROPERTY_UID)
    public String getUid() {
        return uid;
    }

    @JsonProperty(JSON_PROPERTY_UID)
    public void setUid(String uid) {
        this.uid = uid;
    }

    public Link uid(String uid) {
        setUid(uid);
        return this;
    }

    /**
     * Semantic type (URI) of the target resource, per RFC 6690. (optional)
     */
    @JsonProperty(JSON_PROPERTY_RT)
    public String getRt() {
        return rt;
    }

    @JsonProperty(JSON_PROPERTY_RT)
    public void setRt(String rt) {
        this.rt = rt;
    }

    public Link rt(String rt) {
        setRt(rt);
        return this;
    }

    /**
     * Interface (URI) used to access the target resource, per RFC 6690. (optional)
     */
    @JsonProperty(JSON_PROPERTY_IF)
    public String getIfRelation() {
        return ifRelation;
    }

    @JsonProperty(JSON_PROPERTY_IF)
    public void setIfRelation(String ifRelation) {
        this.ifRelation = ifRelation;
    }

    public Link ifRelation(String ifRelation) {
        setIfRelation(ifRelation);
        return this;
    }

}
