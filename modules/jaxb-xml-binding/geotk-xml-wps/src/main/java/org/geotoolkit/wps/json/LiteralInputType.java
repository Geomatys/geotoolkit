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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/**
 * LiteralInputType unused anyùore
 */
public class LiteralInputType {

    @JsonProperty("literalDataDomains")
    private List<LiteralDataDomain> literalDataDomains = null;

    public LiteralInputType() {

    }

    /*public LiteralInputType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            List<FormatDescription> formats, Integer minOccurs, Object maxOccurs,
            LiteralDataDomain literalDataDomain) {
        super(id, title, _abstract, keywords, metadata, additionalParameters, formats);
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        this.literalDataDomain = literalDataDomain;

    }

    public LiteralInputType(InputDescription in) {
        super(in);
        if (in != null) {
            this.minOccurs = in.getMinOccurs();
            if (in.getMaxOccurs() == Integer.MAX_VALUE) {
                this.maxOccurs = "unbounded";
            } else {
                this.maxOccurs = Integer.toString(in.getMaxOccurs());
            }
            this.literalDataDomain = null; // TODO
        }
    }*/

    public LiteralInputType literalDataDomain(List<LiteralDataDomain> literalDataDomains) {
        this.literalDataDomains = literalDataDomains;
        return this;
    }

    /**
     * Get literalDataDomain
     *
     * @return literalDataDomain
  *
     */
    public List<LiteralDataDomain> getLiteralDataDomains() {
        return literalDataDomains;
    }

    public void setLiteralDataDomains(List<LiteralDataDomain> literalDataDomains) {
        this.literalDataDomains = literalDataDomains;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralInputType literalInputType = (LiteralInputType) o;
        return Objects.equals(this.literalDataDomains, literalInputType.literalDataDomains)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalDataDomains, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LiteralInputType {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    literalDataDomains: ").append(toIndentedString(literalDataDomains)).append("\n");
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
