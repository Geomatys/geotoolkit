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
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractService;


/**
 * <p>Java class for anonymous complex type.
 * 
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
    "accessConstraints"
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
            final ContactInformation contactInformation, final String fees, final String accessConstraints) {
        
        this._abstract          = _abstract;
        this.name               = name;
        this.onlineResource     = onlineResource;
        this.accessConstraints  = accessConstraints;
        this.contactInformation = contactInformation;
        this.fees               = fees;
        this.keywordList        = keywordList;
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

    @Override
    public void setAccessConstraints(final String constraint) {
        this.accessConstraints = constraint;
    }
}
