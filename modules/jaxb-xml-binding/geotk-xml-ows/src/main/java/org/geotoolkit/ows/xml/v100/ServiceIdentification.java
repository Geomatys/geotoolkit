/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="ServiceType" type="{http://www.opengis.net/ows}CodeType"/>
 *         &lt;element name="ServiceTypeVersion" type="{http://www.opengis.net/ows}VersionType" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/ows}Fees" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows}AccessConstraints" maxOccurs="unbounded" minOccurs="0"/>
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
    "fees",
    "accessConstraints"
})
@XmlRootElement(name = "ServiceIdentification")
public class ServiceIdentification extends DescriptionType {

    @XmlElement(name = "ServiceType", required = true)
    private CodeType serviceType;
    @XmlElement(name = "ServiceTypeVersion", required = true)
    private List<String> serviceTypeVersion;
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
    public ServiceIdentification(String title, String _abstract,
            List<KeywordsType> keywords, CodeType serviceType, List<String> serviceTypeVersion, 
            String fees, List<String> accessConstraints){
        super(title, _abstract, keywords);
        this.accessConstraints  = accessConstraints;
        this.fees               = fees;
        this.serviceType        = serviceType;
        this.serviceTypeVersion = serviceTypeVersion;
    }
    
    /**
     * Build a new Service identification (light version).
     */
    public ServiceIdentification(String title, String _abstract,
            KeywordsType keywords, CodeType serviceType, List<String> serviceTypeVersion, String fees, String accessConstraints){
        super(title, _abstract, keywords);
        this.accessConstraints  = new ArrayList<String>();
        this.accessConstraints.add(accessConstraints);
        this.fees               = fees;
        this.serviceType        = serviceType;
        this.serviceTypeVersion = serviceTypeVersion;
    }
    
    /**
     * Gets the value of the serviceType property.
     */
    public CodeType getServiceType() {
        return serviceType;
    }

    /**
     * Gets the value of the serviceTypeVersion property.
     */
    public List<String> getServiceTypeVersion() {
        if (serviceTypeVersion == null) {
            serviceTypeVersion = new ArrayList<String>();
        }
        return this.serviceTypeVersion;
    }

    /**
     * If this element is omitted, no meaning is implied. 
     */
    public String getFees() {
        return fees;
    }

    /**
     * Unordered list of access constraints applied to assure the protection of privacy or intellectual property, 
     * and any other restrictions on retrieving or using data from or otherwise using this server. 
     * The reserved value NONE (case insensitive) shall be used to mean no access constraints are imposed.
     * If this element is omitted, no meaning is implied. Gets the value of the accessConstraints property.
     */
    public List<String> getAccessConstraints() {
        if (accessConstraints == null) {
            accessConstraints = new ArrayList<String>();
        }
        return this.accessConstraints;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ServiceIdentification && super.equals(object)) {
            final ServiceIdentification that = (ServiceIdentification) object;
            return Utilities.equals(this.accessConstraints,  that.accessConstraints) &&
                   Utilities.equals(this.fees,               that.fees)              &&
                   Utilities.equals(this.serviceType,        that.serviceType)       &&
                   Utilities.equals(this.serviceTypeVersion, that.serviceTypeVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.serviceType != null ? this.serviceType.hashCode() : 0);
        hash = 37 * hash + (this.serviceTypeVersion != null ? this.serviceTypeVersion.hashCode() : 0);
        hash = 37 * hash + (this.fees != null ? this.fees.hashCode() : 0);
        hash = 37 * hash + (this.accessConstraints != null ? this.accessConstraints.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (serviceType != null) {
            sb.append("serviceType:").append(serviceType).append('\n');
        }
        if (serviceTypeVersion != null) {
            sb.append("serviceTypeVersion:").append('\n');
            for (String k : serviceTypeVersion) {
                sb.append(k).append('\n');
            }
        }
        if (accessConstraints != null) {
            sb.append("accessConstraints:").append('\n');
            for (String k : accessConstraints) {
                sb.append(k).append('\n');
            }
        }
        if (fees != null) {
            sb.append("fees:").append(fees).append('\n');
        }
        return sb.toString();
    }
}
