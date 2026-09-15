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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Response containing full description of the FrameworkKey of a spatial framework housed
 * on this server.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrameworkKeyDescriptionType", propOrder = {"framework"})
@XmlRootElement(name = "FrameworkKeyDescription", namespace = "http://www.opengis.net/tjs/1.0")
public class FrameworkKeyDescriptionType extends TJSResponseType {

    @XmlElement(name = "Framework", required = true)
    private DescribeFrameworkKeyType framework;

    public DescribeFrameworkKeyType getFramework() {
        return framework;
    }

    public void setFramework(DescribeFrameworkKeyType framework) {
        this.framework = framework;
    }
}
