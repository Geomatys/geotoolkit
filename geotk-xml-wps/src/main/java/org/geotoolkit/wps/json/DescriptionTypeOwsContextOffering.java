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
package org.geotoolkit.wps.json;

import java.util.Objects;

/**
 * DescriptionTypeOwsContextOffering
 */
public class DescriptionTypeOwsContextOffering {

    private String code = null;

    private DescriptionTypeOwsContextOfferingContent content = null;

    public DescriptionTypeOwsContextOffering() {

    }

    public DescriptionTypeOwsContextOffering(DescriptionTypeOwsContextOffering that) {
        if (that != null) {
            this.code = that.code;
            if (that.content != null) {
                this.content = new DescriptionTypeOwsContextOfferingContent(that.content.getHref());
            }
        }
    }

    public DescriptionTypeOwsContextOffering(String code, String href) {
        this.code = code;
        if (href != null) {
            this.content = new DescriptionTypeOwsContextOfferingContent(href);
        }
    }

    public DescriptionTypeOwsContextOffering code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Get code
     *
     * @return code
  *
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DescriptionTypeOwsContextOffering content(DescriptionTypeOwsContextOfferingContent content) {
        this.content = content;
        return this;
    }

    /**
     * Get content
     *
     * @return content
  *
     */
    public DescriptionTypeOwsContextOfferingContent getContent() {
        return content;
    }

    public void setContent(DescriptionTypeOwsContextOfferingContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DescriptionTypeOwsContextOffering descriptionTypeOwsContextOffering = (DescriptionTypeOwsContextOffering) o;
        return Objects.equals(this.code, descriptionTypeOwsContextOffering.code)
                && Objects.equals(this.content, descriptionTypeOwsContextOffering.content);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(code, content);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DescriptionTypeOwsContextOffering {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    content: ").append(toIndentedString(content)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
