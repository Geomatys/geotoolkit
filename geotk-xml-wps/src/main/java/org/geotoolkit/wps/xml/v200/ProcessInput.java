/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wps.xml.v200;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import org.geotoolkit.ows.xml.v200.CodeType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ProcessInput {

    @XmlElement(name = "Identifier", namespace = "http://www.opengis.net/ows/2.0", required = true)
    private CodeType identifier;

    private List<DataInput> input;

    public ProcessInput() {

    }

    public ProcessInput(CodeType identifier, List<DataInput> input) {
        this.identifier = identifier;
        this.input = input;
    }

    public CodeType getIdentifier() {
        return identifier;
    }

    public void setIdentifier(CodeType identifier) {
        this.identifier = identifier;
    }

    public List<DataInput> getInput() {
        return input;
    }

    public void setInput(List<DataInput> input) {
        this.input = input;
    }
}
