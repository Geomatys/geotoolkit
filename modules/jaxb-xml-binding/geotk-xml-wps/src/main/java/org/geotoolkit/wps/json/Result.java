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
 * Result
 */
public class Result implements WPSJSONResponse {

    private List<OutputInfo> outputs = new ArrayList<>();

    private List<JsonLink> links;

    public Result outputs(List<OutputInfo> outputs) {
        this.outputs = outputs;
        return this;
    }

    public Result addOutputsItem(OutputInfo outputsItem) {

        this.outputs.add(outputsItem);
        return this;
    }

    /**
     * Get outputs
     *
     * @return outputs
  *
     */
    public List<OutputInfo> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputInfo> outputs) {
        this.outputs = outputs;
    }

    public List<JsonLink> getLinks() {
        return links;
    }

    public void setLinks(List<JsonLink> link) {
        this.links = link;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Result result = (Result) o;
        return Objects.equals(this.outputs, result.outputs) &&
               Objects.equals(this.links, result.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputs, links);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Result {\n");

        sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
        sb.append("    links: ").append(toIndentedString(links)).append("\n");
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
