/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.kml.model;

import java.util.List;
import org.opengis.feature.Feature;

/**
 * <p>This interface maps Create element.</p>
 *
 * <pre>
 * &lt;element name="Create" type="kml:CreateType"/>
 *
 * &lt;complexType name="CreateType">
 *  &lt;sequence>
 *      &lt;element ref="kml:AbstractContainerGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module pending
 */
public interface Create {

    /**
     *
     * @return the AbstractContainer list.
     */
    List<Feature> getContainers();

    /**
     * 
     * @param containers
     */
    void setContainers(List<Feature> containers);
}
