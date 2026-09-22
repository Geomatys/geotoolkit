/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * Format (mime type) supported by a WCTS server for inputs and/or outputs, as encoded by
 * {@code owsAdditions.xsd/InputOutputFormatType} (private WCTS extension namespace).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InputOutputFormatType", namespace = "http://www.opengeospatial.net/ows")
public class InputOutputFormatType {

    @XmlValue
    private String value;
    @XmlAttribute
    private Boolean input;
    @XmlAttribute
    private Boolean output;

    public InputOutputFormatType() {
    }

    public InputOutputFormatType(String value, Boolean input, Boolean output) {
        this.value = value;
        this.input = input;
        this.output = output;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isInput() {
        return input == null ? true : input;
    }

    public void setInput(Boolean input) {
        this.input = input;
    }

    public boolean isOutput() {
        return output == null ? true : output;
    }

    public void setOutput(Boolean output) {
        this.output = output;
    }
}
