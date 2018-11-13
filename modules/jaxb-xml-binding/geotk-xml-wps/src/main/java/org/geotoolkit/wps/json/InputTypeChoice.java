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
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * InputTypeChoice
 */
public class InputTypeChoice {

    @JsonProperty("literalDataDomains")
    private List<LiteralDataDomain> literalDataDomain = null;

    private List<String> supportedCRS = null;

    public InputTypeChoice() {

    }

    public InputTypeChoice(InputTypeChoice that) {
        if (that != null) {
            if (that.supportedCRS != null && !that.supportedCRS.isEmpty()) {
                this.supportedCRS = new ArrayList<>(that.supportedCRS);
            }
            if (that.literalDataDomain != null && !that.literalDataDomain.isEmpty()) {
                this.literalDataDomain = new ArrayList<>();
                for (LiteralDataDomain litDomain : that.literalDataDomain) {
                    this.literalDataDomain.add(new LiteralDataDomain(litDomain));
                }
            }
        }

    }

    public InputTypeChoice(LiteralDataDomain literalDataDomain) {
        if (literalDataDomain != null) {
            this.literalDataDomain = Arrays.asList(literalDataDomain);
        }
    }

    public InputTypeChoice(List<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }


    /**
     * Get literalDataDomain
     *
     * @return literalDataDomain
  *
     */
    public List<LiteralDataDomain> getLiteralDataDomain() {
        return literalDataDomain;
    }

    public void setLiteralDataDomain(List<LiteralDataDomain> literalDataDomain) {
        this.literalDataDomain = literalDataDomain;
    }

    public InputTypeChoice supportedCRS(List<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
        return this;
    }

    public InputTypeChoice addSupportedCRSItem(String supportedCRSItem) {

        this.supportedCRS.add(supportedCRSItem);
        return this;
    }

    /**
     * Get supportedCRS
     *
     * @return supportedCRS
  *
     */
    public List<String> getSupportedCRS() {
        return supportedCRS;
    }

    public void setSupportedCRS(List<String> supportedCRS) {
        this.supportedCRS = supportedCRS;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputTypeChoice inputTypeChoice = (InputTypeChoice) o;
        return Objects.equals(this.literalDataDomain, inputTypeChoice.literalDataDomain)
                && Objects.equals(this.supportedCRS, inputTypeChoice.supportedCRS);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(literalDataDomain, supportedCRS);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InputTypeChoice {\n");

        sb.append("    literalDataDomain: ").append(toIndentedString(literalDataDomain)).append("\n");
        sb.append("    supportedCRS: ").append(toIndentedString(supportedCRS)).append("\n");
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
