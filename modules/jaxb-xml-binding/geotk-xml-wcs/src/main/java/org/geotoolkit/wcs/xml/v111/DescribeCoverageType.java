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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wcs.xml.DescribeCoverage;
import org.geotoolkit.util.Version;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs/1.1.1}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/1.1.1}Identifier" maxOccurs="unbounded"/>
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
    "identifier"
})
@XmlRootElement(name = "DescribeCoverage")
public class DescribeCoverageType implements DescribeCoverage {

    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;
    @XmlElement(name = "Identifier", required = true)
    private List<String> identifier;

    /**
     * Empty constructor used by JAXB
     */
    DescribeCoverageType(){
    }
    
    /**
     * Build a new DescribeCoverage request.
     * 
     * @param version The version of the service.
     * @param listOfCoverage a string containing many coverage name separated by a colon.
     */
    public DescribeCoverageType(String listOfCoverage){
        this.service = "WCS";
        this.version = "1.1.1";
        identifier = new ArrayList<String>();
        final StringTokenizer tokens = new StringTokenizer(listOfCoverage, ",;");
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken().trim();
            identifier.add(token);
        }
    }
    
    /**
     * Build a new DescribeCoverage request.
     * 
     * @param version The version of the service.
     * @param coverages A list  of coverage name.
     */
    public DescribeCoverageType(List<String> identifier){
        this.service = "WCS";
        this.version = "1.1.1";
        this.identifier = identifier;
    }
    
    /**
     * Unordered list of identifiers of desired coverages. A client can obtain identifiers by a prior GetCapabilities request, or from a third-party source. Gets the value of the identifier property.
     */
    public List<String> getIdentifier() {
        if (identifier == null) {
            identifier = new ArrayList<String>();
        }
        return Collections.unmodifiableList(identifier);
    }
    
     /**
     * Gets the value of the service property.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Gets the value of the version property.
     */
    @Override
    public Version getVersion() {
        return new Version(version);
    }

    @Override
    public String toKvp() {
        final StringBuilder sb = new StringBuilder("request=DescribeCoverage&service=");
        sb.append(service).append("&version=").append(version).append("&identifier=");
        for (int i=0; i<identifier.size(); i++) {
            sb.append(identifier.get(i));
            if (i < identifier.size() - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DescribeCoverageType other = (DescribeCoverageType) obj;
        if ((this.service == null) ? (other.service != null) : !this.service.equals(other.service)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 31 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 31 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        return hash;
    }

}
