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

import org.opengis.feature.Feature;

/**
 * <p>This interface maps a kml element</p>
 *
 * <pre>
 * &lt;element name="kml" type="kml:KmlType"/>
 *
 * &lt;complexType name="KmlType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:NetworkLinkControl" minOccurs="0"/>
 *      &lt;element ref="kml:AbstractFeatureGroup" minOccurs="0"/>
 *      &lt;element ref="kml:KmlSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:KmlObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="hint" type="string"/>
 * &lt;/complexType>
 *
 * &lt;lement name="KmlSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="KmlObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Kml {

    /**
     *
     * @return
     */
    public String getVersion();

    /**
     *
     * @return The Kml NetworkLinkControl.
     */
    public NetworkLinkControl getNetworkLinkControl();

    /**
     *
     * @return The Kml AbstractFeature.
     */
    public Feature getAbstractFeature();

    /**
     * 
     * @param version
     */
    public void setVersion(String version) throws KmlException;

    /**
     * 
     * @param networkLinkCOntrol
     */
    public void setNetworkLinkControl(NetworkLinkControl networkLinkCOntrol);

    /**
     * 
     * @param feature
     */
    public void setAbstractFeature(Feature feature);

    /**
     * 
     * @return
     */
    public Extensions extensions();

}
