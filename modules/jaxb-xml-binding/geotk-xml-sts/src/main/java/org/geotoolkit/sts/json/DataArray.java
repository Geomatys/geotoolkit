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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DataArray implements STSResponse {

    @JsonProperty("Datastream@iot.navigationLink")
    private String datastreamIotNavigationLink = null;

    @JsonProperty("MultiDatastream@iot.navigationLink")
    protected String multiDatastreamIotNavigationLink = null;

    @JsonProperty("dataArray@iot.count")
    private BigDecimal iotCount = null;

    private List<String> components;

    private List<Object> dataArray;

    /**
     * @return the iotCount
     */
    public BigDecimal getIotCount() {
        return iotCount;
    }

    /**
     * @param iotCount the iotCount to set
     */
    public void setIotCount(BigDecimal iotCount) {
        this.iotCount = iotCount;
    }

    /**
     * @return the components
     */
    public List<String> getComponents() {
        if (components == null) {
            components = new ArrayList<>();
        }
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(List<String> components) {
        this.components = components;
    }

    /**
     * @return the dataArray
     */
    public List<Object> getDataArray() {
        if (dataArray == null) {
            dataArray = new ArrayList<>();
        }
        return dataArray;
    }

    /**
     * @param dataArray the dataArray to set
     */
    public void setDataArray(List<Object> dataArray) {
        this.dataArray = dataArray;
    }

    /**
     * @return the datastreamIotNavigationLink
     */
    public String getDatastreamIotNavigationLink() {
        return datastreamIotNavigationLink;
    }

    /**
     * @param datastreamIotNavigationLink the datastreamIotNavigationLink to set
     */
    public void setDatastreamIotNavigationLink(String datastreamIotNavigationLink) {
        this.datastreamIotNavigationLink = datastreamIotNavigationLink;
    }

    /**
     * @return the multiDatastreamIotNavigationLink
     */
    public String getMultiDatastreamIotNavigationLink() {
        return multiDatastreamIotNavigationLink;
    }

    /**
     * @param multiDatastreamIotNavigationLink the multiDatastreamIotNavigationLink to set
     */
    public void setMultiDatastreamIotNavigationLink(String multiDatastreamIotNavigationLink) {
        this.multiDatastreamIotNavigationLink = multiDatastreamIotNavigationLink;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataArray da = (DataArray) o;
        return Objects.equals(this.components, da.components)
                && Objects.equals(this.dataArray, da.dataArray)
                && Objects.equals(this.datastreamIotNavigationLink, da.datastreamIotNavigationLink)
                && Objects.equals(this.iotCount, da.iotCount)
                && Objects.equals(this.multiDatastreamIotNavigationLink, da.multiDatastreamIotNavigationLink);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(components, dataArray, datastreamIotNavigationLink, iotCount, multiDatastreamIotNavigationLink);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataArray {\n");

        sb.append("    iotCount: ").append(toIndentedString(iotCount)).append("\n");
        sb.append("    dataArray: ").append(toIndentedString(dataArray)).append("\n");
        sb.append("    datastreamIotNavigationLink: ").append(toIndentedString(datastreamIotNavigationLink)).append("\n");
        sb.append("    multiDatastreamIotNavigationLink: ").append(toIndentedString(multiDatastreamIotNavigationLink)).append("\n");
        sb.append("    components: ").append(toIndentedString(components)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    protected String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
