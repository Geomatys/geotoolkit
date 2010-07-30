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
 * <p>This interface maps Delete element.</p>
 *
 * <pre>
 * &lt;element name="Delete" type="kml:DeleteType"/>
 * 
 * &lt;complexType name="DeleteType">
 *  &lt;sequence>
 *      &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Delete {

    /**
     *
     * @return AbstractFeature list.
     */
    List<Feature> getFeatures();

    /**
     * 
     * @param features
     */
    void setFeatures(List<Feature> features);

}
