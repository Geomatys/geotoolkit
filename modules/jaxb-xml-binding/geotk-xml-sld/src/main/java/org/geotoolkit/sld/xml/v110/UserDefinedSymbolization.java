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
package org.geotoolkit.sld.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="SupportSLD" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="UserLayer" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="UserStyle" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="RemoteWFS" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="InlineFeature" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *       &lt;attribute name="RemoteWCS" type="{http://www.w3.org/2001/XMLSchema}boolean" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @auhor Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class UserDefinedSymbolization {

    @XmlAttribute(name = "SupportSLD")
    private Integer supportSLD;
    @XmlAttribute(name = "UserLayer")
    private Integer userLayer;
    @XmlAttribute(name = "UserStyle")
    private Integer userStyle;
    @XmlAttribute(name = "RemoteWFS")
    private Integer remoteWFS;
    @XmlAttribute(name = "InlineFeature")
    private Integer inlineFeature;
    @XmlAttribute(name = "RemoteWCS")
    private Integer remoteWCS;

    /**
     * Empty Constructor used by JAXB.
     */
    UserDefinedSymbolization() {
        
    }
    
    /**
     * Build a new User Defined Symbolization.
     */
    public UserDefinedSymbolization(final Integer supportSLD, final Integer userLayer, final Integer userStyle,
            final Integer remoteWFS, final Integer inlineFeature, final Integer remoteWCS) {
        this.inlineFeature = inlineFeature;
        this.supportSLD    = supportSLD;
        this.userLayer     = userLayer;
        this.userStyle     = userStyle;
        this.remoteWFS     = remoteWFS;
        this.remoteWCS     = remoteWCS;
    }
    
   /**
    * Gets the value of the supportSLD property.
    */
    public boolean isSupportSLD() {
        if (supportSLD == null) {
            return false;
        } else {
            return supportSLD == 1;
        }
    }

    /**
     * Gets the value of the userLayer property.
     */
    public boolean isUserLayer() {
        if (userLayer == null) {
            return false;
        } else {
            return userLayer == 1;
        }
    }

    /**
     * Gets the value of the userStyle property.
     */
    public boolean isUserStyle() {
        if (userStyle == null) {
            return false;
        } else {
            return userStyle == 1;
        }
    }

    /**
     * Gets the value of the remoteWFS property.
     */
    public boolean isRemoteWFS() {
        if (remoteWFS == null) {
            return false;
        } else {
            return remoteWFS == 1;
        }
    }

    /**
     * Gets the value of the inlineFeature property.
     */
    public boolean isInlineFeature() {
        if (inlineFeature == null) {
            return false;
        } else {
            return inlineFeature == 1;
        }
    }

    /**
     * Gets the value of the remoteWCS property.
     */
    public boolean isRemoteWCS() {
        if (remoteWCS == null) {
            return false;
        } else {
            return remoteWCS == 1;
        }
    }
}
