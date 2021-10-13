/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.wms.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractContactInformation;
import org.geotoolkit.wms.xml.AbstractKeywordList;
import org.geotoolkit.wms.xml.AbstractOnlineResource;
import org.geotoolkit.wms.xml.AbstractService;
import org.geotoolkit.wms.xml.v111.OnlineResource;


/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "title",
    "_abstract",
    "keywords",
    "onlineResource",
    "fees",
    "accessConstraints"
})
@XmlRootElement(name = "Service")
public class Service implements AbstractService {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Title", required = true)
    protected String title;
    @XmlElement(name = "Abstract")
    protected String _abstract;
    @XmlElement(name = "Keywords")
    protected String keywords;
    @XmlElement(name = "OnlineResource", required = true)
    protected String onlineResource;
    @XmlElement(name = "Fees")
    protected String fees;
    @XmlElement(name = "AccessConstraints")
    protected String accessConstraints;

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété abstract.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAbstract() {
        return _abstract;
    }

    /**
     * Définit la valeur de la propriété abstract.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAbstract(String value) {
        this._abstract = value;
    }

    /**
     * Obtient la valeur de la propriété keywords.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Définit la valeur de la propriété keywords.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKeywords(String value) {
        this.keywords = value;
    }

    /**
     * Obtient la valeur de la propriété onlineResource.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public AbstractOnlineResource getOnlineResource() {
        return new OnlineResource(onlineResource);
    }

    /**
     * Définit la valeur de la propriété onlineResource.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOnlineResource(String value) {
        this.onlineResource = value;
    }

    /**
     * Obtient la valeur de la propriété fees.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFees() {
        return fees;
    }

    /**
     * Définit la valeur de la propriété fees.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFees(String value) {
        this.fees = value;
    }

    /**
     * Obtient la valeur de la propriété accessConstraints.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccessConstraints() {
        return accessConstraints;
    }

    /**
     * Définit la valeur de la propriété accessConstraints.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccessConstraints(String value) {
        this.accessConstraints = value;
    }

    @Override
    public AbstractKeywordList getKeywordList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AbstractContactInformation getContactInformation() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
