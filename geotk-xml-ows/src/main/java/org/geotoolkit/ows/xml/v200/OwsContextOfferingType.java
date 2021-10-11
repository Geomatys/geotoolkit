/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v200;

import java.util.Objects;
import org.geotoolkit.ows.xml.AbstractOwsContextOffering;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OwsContextOfferingType implements AbstractOwsContextOffering {

    private String code = null;

    private ReferenceType content = null;

    public OwsContextOfferingType() {

    }

    public OwsContextOfferingType(String code, ReferenceType content) {
        this.code = code;
        this.content = content;
    }

    public OwsContextOfferingType(String code, String contentHref) {
        this.code = code;
        if (contentHref != null) {
            this.content = new ReferenceType(contentHref);
        }
    }
    /**
     * Get code
     *
     * @return code
     *
     */
    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get content
     *
     * @return content
     *
     */
    public ReferenceType getContent() {
        return content;
    }

    public void setContent(ReferenceType content) {
        this.content = content;
    }

    @Override
    public String getContentRef() {
        if (content != null) {
            return content.getHref();
        }
        return null;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OwsContextOfferingType descriptionTypeOwsContextOffering = (OwsContextOfferingType) o;
        return Objects.equals(this.code, descriptionTypeOwsContextOffering.code)
                && Objects.equals(this.content, descriptionTypeOwsContextOffering.content);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(code, content);
    }
}
