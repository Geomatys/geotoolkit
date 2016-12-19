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
package org.geotoolkit.wps.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.LanguageString;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.wps.xml.ProcessOffering;
import org.geotoolkit.wps.xml.ProcessSummary;


/**
 * <p>Java class for ProcessBriefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessBriefType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/1.0.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="Profile" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wps/1.0.0}WSDL" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/wps/1.0.0}processVersion use="required""/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessBriefType", propOrder = {
    "profile",
    "wsdl"
})
@XmlSeeAlso({
    ProcessDescriptionType.class
})
public class ProcessBriefType extends DescriptionType implements ProcessSummary, ProcessOffering {

    @XmlElement(name = "Profile")
    @XmlSchemaType(name = "anyURI")
    protected List<String> profile;
    @XmlElement(name = "WSDL")
    protected WSDL wsdl;
    @XmlAttribute(namespace = "http://www.opengis.net/wps/1.0.0", required = true)
    protected String processVersion;

    public ProcessBriefType() {
        
    }
    
    public ProcessBriefType(CodeType identifier, LanguageStringType title, LanguageStringType _abstract, String processVersion) {
        super(identifier, title, _abstract);
        this.processVersion = processVersion;
    }

    @Override
    public LanguageString getSingleAbstract() {
        return getAbstract();
    }
    
    /**
     * Gets the value of the profile property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getProfile() {
        if (profile == null) {
            profile = new ArrayList<>();
        }
        return this.profile;
    }

    /**
     * Location of a WSDL document which describes this process.
     * 
     * @return
     *     possible object is
     *     {@link WSDL }
     *     
     */
    public WSDL getWSDL() {
        return wsdl;
    }

    /**
     * Location of a WSDL document which describes this process.
     * 
     * @param value
     *     allowed object is
     *     {@link WSDL }
     *     
     */
    public void setWSDL(final WSDL value) {
        this.wsdl = value;
    }

    /**
     * Gets the value of the processVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessVersion() {
        return processVersion;
    }

    /**
     * Sets the value of the processVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessVersion(final String value) {
        this.processVersion = value;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (processVersion != null) {
            sb.append("Process version:").append(processVersion).append('\n');
        }
        if (wsdl != null) {
            sb.append("wsdl:").append(wsdl).append('\n');
        }
        if (profile != null) {
            sb.append("Profiles:\n");
            for (String out : profile) {
                sb.append(out).append('\n');
            }
        }
        return sb.toString();
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ProcessBriefType && super.equals(object)) {
            final ProcessBriefType that = (ProcessBriefType) object;
            return Objects.equals(this.processVersion, that.processVersion) &&
                   Objects.equals(this.profile, that.profile) &&
                   Objects.equals(this.wsdl, that.wsdl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.profile);
        hash = 43 * hash + Objects.hashCode(this.wsdl);
        hash = 43 * hash + Objects.hashCode(this.processVersion);
        return hash;
    }

}
