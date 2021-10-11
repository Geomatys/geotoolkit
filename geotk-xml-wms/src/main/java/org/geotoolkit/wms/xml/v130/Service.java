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
package org.geotoolkit.wms.xml.v130;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractService;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="WMS"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/wms}Title"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}KeywordList" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}OnlineResource"/>
 *         &lt;element ref="{http://www.opengis.net/wms}ContactInformation" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}Fees" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}AccessConstraints" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}LayerLimit" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}MaxWidth" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wms}MaxHeight" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywordList",
    "onlineResource",
    "contactInformation",
    "fees",
    "accessConstraints",
    "layerLimit",
    "maxWidth",
    "maxHeight"
})
@XmlRootElement(name = "Service")
public class Service implements AbstractService {

    @XmlElement(name = "Name", required = true)
    private String name;
    @XmlElement(name = "Title", required = true)
    private String title;
    @XmlElement(name = "Abstract")
    private String _abstract;
    @XmlElement(name = "KeywordList")
    private KeywordList keywordList;
    @XmlElement(name = "OnlineResource", required = true)
    private OnlineResource onlineResource;
    @XmlElement(name = "ContactInformation")
    private ContactInformation contactInformation;
    @XmlElement(name = "Fees")
    private String fees;
    @XmlElement(name = "AccessConstraints")
    private String accessConstraints;
    @XmlElement(name = "LayerLimit")
    @XmlSchemaType(name = "positiveInteger")
    private Integer layerLimit;
    @XmlElement(name = "MaxWidth")
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxWidth;
    @XmlElement(name = "MaxHeight")
    @XmlSchemaType(name = "positiveInteger")
    private Integer maxHeight;

    /**
     * An empty constructor used by JAXB.
     */
     Service() {
     }

    /**
     * Build a new Service object.
     */
    public Service(final String name, final String title, final String _abstract,
            final KeywordList keywordList, final OnlineResource onlineResource,
            final ContactInformation contactInformation, final String fees, final String accessConstraints,
            final Integer layerLimit, final Integer maxWidth, final Integer maxHeight) {

        this._abstract          = _abstract;
        this.name               = name;
        this.onlineResource     = onlineResource;
        this.accessConstraints  = accessConstraints;
        this.contactInformation = contactInformation;
        this.fees               = fees;
        this.keywordList        = keywordList;
        this.layerLimit         = layerLimit;
        this.maxHeight          = maxHeight;
        this.maxWidth           = maxWidth;
        this.title              = title;
    }


    /**
     * Gets the value of the name property.
     *
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the title property.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

   /**
     * Gets the value of the abstract property.
     *
     */
    @Override
    public String getAbstract() {
        return _abstract;
    }

    /**
     * @param _abstract the _abstract to set
     */
    @Override
    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    /**
     * Gets the value of the keywordList property.
     */
    @Override
    public KeywordList getKeywordList() {
        return keywordList;
    }

    /**
     * @param keywordList the keywordList to set
     */
    public void setKeywordList(KeywordList keywordList) {
        this.keywordList = keywordList;
    }

    /**
     * Gets the value of the onlineResource property.
     */
    @Override
    public OnlineResource getOnlineResource() {
        return onlineResource;
    }

    /**
     * @param onlineResource the onlineResource to set
     */
    public void setOnlineResource(OnlineResource onlineResource) {
        this.onlineResource = onlineResource;
    }

    /**
     * Gets the value of the contactInformation property.
     *
     */
    @Override
    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    /**
     * @param contactInformation the contactInformation to set
     */
    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Gets the value of the fees property.
     */
    @Override
    public String getFees() {
        return fees;
    }

    /**
     * @param fees the fees to set
     */
    @Override
    public void setFees(String fees) {
        this.fees = fees;
    }

   /**
    * Gets the value of the accessConstraints property.
    *
    */
    @Override
    public String getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * Gets the value of the layerLimit property.
     */
    public Integer getLayerLimit() {
        return layerLimit;
    }

    /**
     * @param layerLimit the layerLimit to set
     */
    public void setLayerLimit(Integer layerLimit) {
        this.layerLimit = layerLimit;
    }

    /**
     * Gets the value of the maxWidth property.
     */
    public Integer getMaxWidth() {
        return maxWidth;
    }

    /**
     * @param maxWidth the maxWidth to set
     */
    public void setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
    }

   /**
    * Gets the value of the maxHeight property.
    *
    */
    public Integer getMaxHeight() {
        return maxHeight;
    }

    /**
     * @param maxHeight the maxHeight to set
     */
    public void setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public void setAccessConstraints(final String constraint) {
        this.accessConstraints = constraint;
    }
}
