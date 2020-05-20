/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.wps.xml.v200.LiteralData;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LiteralInputDescription extends InputDescriptionBase {

    private List<LiteralDataDomain> literalDataDomains;

    public LiteralInputDescription() {

    }

    public LiteralInputDescription(List<LiteralDataDomain> literalDataDomain) {
        this.literalDataDomains = literalDataDomain;
    }

    public LiteralInputDescription(LiteralInputDescription that) {
        if (that != null && that.literalDataDomains != null) {
            this.literalDataDomains = new ArrayList<>(that.literalDataDomains);
        }
    }

    public LiteralInputDescription(LiteralData lit) {
        if (lit != null) {
            List<LiteralDataDomain> jsonLits = new ArrayList<>();
            for (org.geotoolkit.wps.xml.v200.LiteralDataDomain litDom : lit.getLiteralDataDomain()) {
                jsonLits.add(new LiteralDataDomain(litDom));
            }
            this.literalDataDomains = jsonLits;
        }

    }

    public List<LiteralDataDomain> getLiteralDataDomains() {
        return literalDataDomains;
    }

    public void setLiteralDataDomains(List<LiteralDataDomain> literalDataDomain) {
        this.literalDataDomains = literalDataDomain;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralInputDescription inputType = (LiteralInputDescription) o;
        return Objects.equals(this.literalDataDomains, inputType.literalDataDomains);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(literalDataDomains);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LiteralInputDescription {\n");
        sb.append("    literalDataDomain: ").append(toIndentedString(literalDataDomains)).append("\n");
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
