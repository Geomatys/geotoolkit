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
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * <p>This interface maps BasicLinkType type.</p>
 * 
 * <p>This element is an extension of AbstractObject but is not member of
 * the substitution group of this abstract element. So it cannot replace it
 * as java inheritance would allow.</p>
 * 
 * <p>This interface is not an extension of AbstractObject, and 
 * redefines AbstractObject fields.</p>
 *
 * <pre>
 * &lt;complexType name="BasicLinkType">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:href" minOccurs="0"/>
 *              &lt;element ref="kml:BasicLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:BasicLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="BasicLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="BasicLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface BasicLink {

    /**
     *
     * @return the identification attributes.
     */
    public IdAttributes getIdAttributes();

    /**
     *
     * @return the href.
     */
    public String getHref();

    /**
     *
     * @param idAttributes
     */
    public void setIdAttributes(IdAttributes idAttributes);

    /**
     *
     * @param href
     */
    public void setHref(String href);

    /**
     * 
     * @return
     */
    public Extensions extensions();
}
