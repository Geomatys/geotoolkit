/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.kml.xml.v230;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour RegionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="RegionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/kml/2.2}AbstractObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}AbstractExtentGroup" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}Lod" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}RegionSimpleExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/kml/2.2}RegionObjectExtensionGroup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegionType", namespace = "http://www.opengis.net/kml/2.2", propOrder = {
    "abstractExtentGroup",
    "lod",
    "regionSimpleExtensionGroup",
    "regionObjectExtensionGroup"
})
public class RegionType
    extends AbstractObjectType
{

    @XmlElementRef(name = "AbstractExtentGroup", namespace = "http://www.opengis.net/kml/2.2", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends AbstractExtentType> abstractExtentGroup;
    @XmlElement(name = "Lod", namespace = "http://www.opengis.net/kml/2.2")
    protected LodType lod;
    @XmlElement(name = "RegionSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<Object> regionSimpleExtensionGroup;
    @XmlElement(name = "RegionObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObjectType> regionObjectExtensionGroup;

    /**
     * Obtient la valeur de la propriété abstractExtentGroup.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractExtentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonQuadType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonAltBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLatLonBoxType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractExtentType> getAbstractExtentGroup() {
        return abstractExtentGroup;
    }

    /**
     * Définit la valeur de la propriété abstractExtentGroup.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractExtentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonQuadType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link LatLonAltBoxType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractLatLonBoxType }{@code >}
     *
     */
    public void setAbstractExtentGroup(JAXBElement<? extends AbstractExtentType> value) {
        this.abstractExtentGroup = value;
    }

    /**
     * Obtient la valeur de la propriété lod.
     *
     * @return
     *     possible object is
     *     {@link LodType }
     *
     */
    public LodType getLod() {
        return lod;
    }

    /**
     * Définit la valeur de la propriété lod.
     *
     * @param value
     *     allowed object is
     *     {@link LodType }
     *
     */
    public void setLod(LodType value) {
        this.lod = value;
    }

    /**
     * Gets the value of the regionSimpleExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regionSimpleExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegionSimpleExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getRegionSimpleExtensionGroup() {
        if (regionSimpleExtensionGroup == null) {
            regionSimpleExtensionGroup = new ArrayList<Object>();
        }
        return this.regionSimpleExtensionGroup;
    }

    /**
     * Gets the value of the regionObjectExtensionGroup property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regionObjectExtensionGroup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegionObjectExtensionGroup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractObjectType }
     *
     *
     */
    public List<AbstractObjectType> getRegionObjectExtensionGroup() {
        if (regionObjectExtensionGroup == null) {
            regionObjectExtensionGroup = new ArrayList<AbstractObjectType>();
        }
        return this.regionObjectExtensionGroup;
    }

}
