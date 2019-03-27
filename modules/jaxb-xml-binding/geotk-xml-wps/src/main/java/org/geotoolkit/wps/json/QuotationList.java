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
import java.util.ArrayList;
import java.util.List;

/**
 * QuotationList
 */
public class QuotationList implements WPSJSONResponse {

    private List<String> quotations = null;

    public QuotationList() {

    }

    public QuotationList(List<String> quotations) {
        this.quotations = quotations;
    }

    public QuotationList quotations(List<String> quotations) {
        this.quotations = quotations;
        return this;
    }

    public QuotationList addQuotationsItem(String quotationsItem) {

        if (this.quotations == null) {
            this.quotations = new ArrayList<>();
        }

        this.quotations.add(quotationsItem);
        return this;
    }

    /**
     * Get quotations
     *
     * @return quotations
  *
     */
    public List<String> getQuotations() {
        return quotations;
    }

    public void setQuotations(List<String> quotations) {
        this.quotations = quotations;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuotationList quotationList = (QuotationList) o;
        return Objects.equals(this.quotations, quotationList.quotations);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(quotations);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class QuotationList {\n");

        sb.append("    quotations: ").append(toIndentedString(quotations)).append("\n");
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
