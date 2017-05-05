/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.ows.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="ServiceType" type="{http://www.opengis.net/ows/2.0}CodeType"/>
 *         &lt;element name="ServiceTypeVersion" type="{http://www.opengis.net/ows/2.0}VersionType" maxOccurs="unbounded"/>
 *         &lt;element name="Profile" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Fees" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}AccessConstraints" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceType",
    "serviceTypeVersion",
    "profile",
    "fees",
    "accessConstraints"
})
@XmlRootElement(name = "ServiceIdentification")
public class ServiceIdentification extends DescriptionType implements AbstractServiceIdentification {

    @XmlElement(name = "ServiceType", required = true)
    private CodeType serviceType;
    @XmlElement(name = "ServiceTypeVersion", required = true)
    private List<String> serviceTypeVersion;
    @XmlElement(name = "Profile")
    @XmlSchemaType(name = "anyURI")
    private List<String> profile;
    @XmlElement(name = "Fees")
    private String fees;
    @XmlElement(name = "AccessConstraints")
    private List<String> accessConstraints;

    /**
     * Empty constructor used by JAXB.
     */
    ServiceIdentification(){
    }

    /**
     * Build a new Service identification (full version).
     */
    public ServiceIdentification(final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, final CodeType serviceType, final List<String> serviceTypeVersion, final List<String> profile,
            final String fees, final List<String> accessConstraints){
        super(title, _abstract, keywords);
        this.accessConstraints  = accessConstraints;
        this.fees               = fees;
        this.profile            = profile;
        this.serviceType        = serviceType;
        this.serviceTypeVersion = serviceTypeVersion;
    }

    /**
     * Build a new Service identification (light version).
     */
    public ServiceIdentification(final LanguageStringType title,  final LanguageStringType _abstract,
            final KeywordsType keywords, final CodeType serviceType, final List<String> serviceTypeVersion, final String fees, final List<String> accessConstraints){
        super(title, _abstract, keywords);
        this.accessConstraints  = accessConstraints;
        this.fees               = fees;
        this.serviceType        = serviceType;
        this.serviceTypeVersion = serviceTypeVersion;
    }

    /**
     * Gets the value of the serviceType property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    @Override
    public CodeType getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setServiceType(CodeType value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the serviceTypeVersion property.
     *
     */
    @Override
    public List<String> getServiceTypeVersion() {
        if (serviceTypeVersion == null) {
            serviceTypeVersion = new ArrayList<>();
        }
        return this.serviceTypeVersion;
    }

    /**
     * Gets the value of the profile property.
     *
     */
    @Override
    public List<String> getProfile() {
        if (profile == null) {
            profile = new ArrayList<>();
        }
        return this.profile;
    }

    @Override
    public void setProfile(final List<String> profile) {
        this.profile = profile;
    }

    /**
     * If this element is omitted, no meaning is
     *                 implied.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getFees() {
        return fees;
    }

    /**
     * Sets the value of the fees property.
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
     * Unordered list of access constraints applied to
     *                 assure the protection of privacy or intellectual property, and
     *                 any other restrictions on retrieving or using data from or
     *                 otherwise using this server. The reserved value NONE (case
     *                 insensitive) shall be used to mean no access constraints are
     *                 imposed. When this element is omitted, no meaning is
     *                 implied.Gets the value of the accessConstraints property.
     *
     */
    @Override
    public List<String> getAccessConstraints() {
        if (accessConstraints == null) {
            accessConstraints = new ArrayList<>();
        }
        return this.accessConstraints;
    }

}
