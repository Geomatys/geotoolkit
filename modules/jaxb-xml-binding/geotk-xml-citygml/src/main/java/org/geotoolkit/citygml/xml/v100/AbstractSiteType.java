/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.citygml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Type describing the abstract superclass for buildings, facilities, etc.
 * Future extensions of CityGML like bridges and tunnels would be modelled as subclasses of _Site.
 * As subclass of _CityObject, a _Site inherits all attributes and relations,
 * in particular an id, names, external references, and generalization relations.
 *             
 * 
 * <p>Java class for AbstractSiteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSiteType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/1.0}AbstractCityObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/1.0}_GenericApplicationPropertyOfSite" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSiteType", propOrder = {
    "genericApplicationPropertyOfSite"
})
public abstract class AbstractSiteType extends AbstractCityObjectType {

    @XmlElement(name = "_GenericApplicationPropertyOfSite")
    private List<Object> genericApplicationPropertyOfSite;

    /**
     * Gets the value of the genericApplicationPropertyOfSite property.
     */
    public List<Object> getGenericApplicationPropertyOfSite() {
        if (genericApplicationPropertyOfSite == null) {
            genericApplicationPropertyOfSite = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfSite;
    }

}
