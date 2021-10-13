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
import org.geotoolkit.ows.xml.AbstractOwsContextDescription;

/**
 * DescriptionTypeOwsContext
 */
public class DescriptionTypeOwsContext {

    private DescriptionTypeOwsContextOffering offering = null;

    public DescriptionTypeOwsContext() {

    }

    public DescriptionTypeOwsContext(DescriptionTypeOwsContext that) {
        if (that != null && that.offering != null) {
            this.offering = new DescriptionTypeOwsContextOffering(that.offering);
        }
    }

    public DescriptionTypeOwsContext(AbstractOwsContextDescription desc) {
        if (desc != null && desc.getOffering() != null) {
            this.offering = new DescriptionTypeOwsContextOffering(desc.getOffering().getCode(), desc.getOffering().getContentRef());
        }
    }

    public DescriptionTypeOwsContext offering(DescriptionTypeOwsContextOffering offering) {
        this.offering = offering;
        return this;
    }

    /**
     * Get offering
     *
     * @return offering
     *
     */
    public DescriptionTypeOwsContextOffering getOffering() {
        return offering;
    }

    public void setOffering(DescriptionTypeOwsContextOffering offering) {
        this.offering = offering;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DescriptionTypeOwsContext descriptionTypeOwsContext = (DescriptionTypeOwsContext) o;
        return Objects.equals(this.offering, descriptionTypeOwsContext.offering);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(offering);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DescriptionTypeOwsContext {\n");

        sb.append("    offering: ").append(toIndentedString(offering)).append("\n");
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
