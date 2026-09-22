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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Response to a valid {@code DescribeMethod} operation request ({@code wcts:MethodDescription}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MethodDescriptionType", propOrder = {"method"})
@XmlRootElement(name = "MethodDescription")
public class MethodDescriptionType {

    @XmlElement(name = "method", required = true)
    private List<MethodRefType> method;

    public List<MethodRefType> getMethod() {
        if (method == null) {
            method = new ArrayList<>();
        }
        return method;
    }
}
