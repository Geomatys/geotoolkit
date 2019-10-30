/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.sts.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ObservationsResponse
 */
public class ObservationsResponse implements STSResponse {

    @JsonProperty("@iot.count")
    private BigDecimal iotCount = null;

    @JsonProperty("value")
    private List<Observation> value = null;

    @JsonProperty("@iot.nextLink")
    private String iotNextLink = null;

    public ObservationsResponse() {

    }

    public ObservationsResponse(List<Observation> value) {
        this.value = value;
    }

    public ObservationsResponse iotCount(BigDecimal iotCount) {
        this.iotCount = iotCount;
        return this;
    }

    /**
     * Get iotCount
     *
     * @return iotCount
  *
     */
    @Override
    public BigDecimal getIotCount() {
        return iotCount;
    }

    @Override
    public void setIotCount(BigDecimal iotCount) {
        this.iotCount = iotCount;
    }

    public ObservationsResponse value(List<Observation> value) {
        this.value = value;
        return this;
    }

    public ObservationsResponse addValueItem(Observation valueItem) {

        if (this.value == null) {
            this.value = new ArrayList<>();
        }

        this.value.add(valueItem);
        return this;
    }

    /**
     * Get value
     *
     * @return value
  *
     */
    public List<Observation> getValue() {
        return value;
    }

    public void setValue(List<Observation> value) {
        this.value = value;
    }

    public ObservationsResponse iotNextLink(String iotNextLink) {
        this.iotNextLink = iotNextLink;
        return this;
    }

    /**
     * Get iotNextLink
     *
     * @return iotNextLink
  *
     */
    @Override
    public String getIotNextLink() {
        return iotNextLink;
    }

    @Override
    public void setIotNextLink(String iotNextLink) {
        this.iotNextLink = iotNextLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObservationsResponse observationsResponse = (ObservationsResponse) o;
        return Objects.equals(this.iotCount, observationsResponse.iotCount)
                && Objects.equals(this.value, observationsResponse.value)
                && Objects.equals(this.iotNextLink, observationsResponse.iotNextLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(iotCount, value, iotNextLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ObservationsResponse {\n");
        sb.append("    iotCount: ").append(toIndentedString(iotCount)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("    iotNextLink: ").append(toIndentedString(iotNextLink)).append("\n");
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
