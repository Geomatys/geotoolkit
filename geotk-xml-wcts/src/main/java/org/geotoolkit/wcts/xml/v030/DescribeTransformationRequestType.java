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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to a WCTS to perform the {@code DescribeTransformation} operation ({@code wcts:DescribeTransformation}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeTransformationRequestType", propOrder = {"transformation"})
@XmlRootElement(name = "DescribeTransformation")
public class DescribeTransformationRequestType {

    @XmlAttribute(required = true)
    private String service = "WCTS";
    @XmlAttribute(required = true)
    private String version = "0.3.0";

    @XmlElement(name = "Transformation", required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> transformation;

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getTransformation() {
        if (transformation == null) {
            transformation = new ArrayList<>();
        }
        return transformation;
    }
}
