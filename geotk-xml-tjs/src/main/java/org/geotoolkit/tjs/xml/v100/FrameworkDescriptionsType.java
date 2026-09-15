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

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Response containing full descriptions of all spatial frameworks for which data is available.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrameworkDescriptionsType", propOrder = {"framework"})
@XmlRootElement(name = "FrameworkDescriptions", namespace = "http://www.opengis.net/tjs/1.0")
public class FrameworkDescriptionsType extends TJSResponseType {

    @XmlElement(name = "Framework", required = true)
    private List<FrameworkDescriptionType> framework;

    public List<FrameworkDescriptionType> getFramework() {
        return framework;
    }

    public void setFramework(List<FrameworkDescriptionType> framework) {
        this.framework = framework;
    }
}
