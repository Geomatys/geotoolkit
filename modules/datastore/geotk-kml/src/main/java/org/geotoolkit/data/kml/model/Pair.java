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

import java.net.URI;

/**
 * <p>This interface maps Pair element.</p>
 *
 * <pre>
 * &lt;element name="Pair" type="kml:PairType" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * &lt;complexType name="PairType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *          &lt;element ref="kml:key" minOccurs="0"/>
 *          &lt;element ref="kml:styleUrl" minOccurs="0"/>
 *          &lt;element ref="kml:AbstractStyleSelectorGroup" minOccurs="0"/>
 *          &lt;element ref="kml:PairSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;element ref="kml:PairObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="PairSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="PairObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Pair extends AbstractObject {

    /**
     *
     * @return
     */
    StyleState getKey();

    /**
     *
     * @return
     */
    URI getStyleUrl();

    /**
     *
     * @return
     */
    AbstractStyleSelector getAbstractStyleSelector();

    /**
     *
     * @param key
     */
    void setKey(StyleState key);

    /**
     *
     * @param styleUrl
     */
    void setStyleUrl(URI styleUrl);

    /**
     * 
     * @param styleSelector
     */
    void setAbstractStyleSelector(AbstractStyleSelector styleSelector);

}
