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
 * AdditionalParameters
 */
public class AdditionalParameters {

    private String role = null;

    private List<AdditionalParameter> parameters = null;

    public AdditionalParameters() {

    }

    public AdditionalParameters(AdditionalParameters that) {
        if (that != null) {
            this.role = that.role;
            if (that.parameters != null && !that.parameters.isEmpty()) {
                this.parameters = new ArrayList<>();
                for (AdditionalParameter p : that.parameters) {
                    this.parameters.add(new AdditionalParameter(p));
                }
            }
        }
    }

    public AdditionalParameters(String role, List<AdditionalParameter> parameters) {
        this.parameters = parameters;
        this.role = role;
    }

    public AdditionalParameters role(String role) {
        this.role = role;
        return this;
    }

    /**
     * Get role
     *
     * @return role
  *
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AdditionalParameters parameters(List<AdditionalParameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public AdditionalParameters addParametersItem(AdditionalParameter parametersItem) {

        if (this.parameters == null) {
            this.parameters = new ArrayList<>();
        }

        this.parameters.add(parametersItem);
        return this;
    }

    /**
     * Get parameters
     *
     * @return parameters
  *
     */
    public List<AdditionalParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<AdditionalParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdditionalParameters additionalParameters = (AdditionalParameters) o;
        return Objects.equals(this.role, additionalParameters.role)
                && Objects.equals(this.parameters, additionalParameters.parameters);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(role, parameters);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AdditionalParameters {\n");

        sb.append("    role: ").append(toIndentedString(role)).append("\n");
        sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
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
