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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * An XLink-style by-reference pointer, used wherever the standard allows a component or its
 * values to be given by reference instead of inline.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class AssociationAttributeGroup extends DataTransferObject {

    public static final String JSON_PROPERTY_HREF = "href";
    private String href;

    public static final String JSON_PROPERTY_ROLE = "role";
    private String role;

    public static final String JSON_PROPERTY_ARCROLE = "arcrole";
    private String arcrole;

    public static final String JSON_PROPERTY_TITLE = "title";
    private String title;

    /**
     * URI reference to the target. (required)
     */
    @JsonProperty(JSON_PROPERTY_HREF)
    public String getHref() {
        return href;
    }

    @JsonProperty(JSON_PROPERTY_HREF)
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    public String getRole() {
        return role;
    }

    @JsonProperty(JSON_PROPERTY_ROLE)
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty(JSON_PROPERTY_ARCROLE)
    public String getArcrole() {
        return arcrole;
    }

    @JsonProperty(JSON_PROPERTY_ARCROLE)
    public void setArcrole(String arcrole) {
        this.arcrole = arcrole;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    public String getTitle() {
        return title;
    }

    @JsonProperty(JSON_PROPERTY_TITLE)
    public void setTitle(String title) {
        this.title = title;
    }

}
