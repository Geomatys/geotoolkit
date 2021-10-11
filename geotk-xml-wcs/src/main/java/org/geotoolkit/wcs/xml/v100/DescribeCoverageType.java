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
package org.geotoolkit.wcs.xml.v100;

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
import org.apache.sis.util.Version;


/**
 * <p>An xml binding classe for a DescribeCoverage request.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Coverage" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="service" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="WCS" />
 *       &lt;attribute name="version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @author Cédric Briançon
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"coverage"})
@XmlRootElement(name = "DescribeCoverage")
public class DescribeCoverageType implements DescribeCoverage {

    @XmlAttribute(required = true)
    private String service;
    @XmlAttribute(required = true)
    private String version;

    /*
     * WCS 1.0.0
     */
    @XmlElement(name = "Coverage")
    private List<String> coverage;

    /**
     * Empty constructor used by JAXB
     */
    DescribeCoverageType(){
    }

    /**
     * Build a new DescribeCoverage request.
     *
     * @param listOfCoverage a string containing many coverage name separated by a colon.
     */
    public DescribeCoverageType(final String listOfCoverage){
        this.service = "WCS";
        this.version = "1.0.0";
        coverage = new ArrayList<>();
        final StringTokenizer tokens = new StringTokenizer(listOfCoverage, ",;");
        while (tokens.hasMoreTokens()) {
            final String token = tokens.nextToken().trim();
            coverage.add(token);
        }
    }

    /**
     * Build a new DescribeCoverage request.
     *
     * @param coverages A list  of coverage name.
     */
    public DescribeCoverageType(final List<String> coverages){
        this.service = "WCS";
        this.version = "1.0.0";
        this.coverage = coverages;
    }

    /**
     * Return the list of requested coverage name.
     * (unmodifiable)
     */
    public List<String> getCoverage() {
        if (coverage == null) {
            coverage = new ArrayList<>();
        }
        return Collections.unmodifiableList(coverage);
    }

    @Override
    public List<String> getIdentifier() {
        return getCoverage();
    }

    /**
     * return the service type here always WCS.
     */
    @Override
    public String getService() {
        return this.service;
    }

    @Override
    public void setService(final String value) {
        this.service = value;
    }

    /**
     * Return the version of the service.
     */
    @Override
    public Version getVersion() {
        if (version != null) {
            return new Version(version);
        }
        return null;
    }

    @Override
    public void setVersion(final String value) {
        this.version = value;
    }

    @Override
    public String toKvp() {
        final StringBuilder sb = new StringBuilder("request=DescribeCoverage&service=");
        sb.append(service).append("&version=").append(version).append("&coverage=");
        for (int i=0; i<coverage.size(); i++) {
            sb.append(coverage.get(i));
            if (i < coverage.size() - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.service != null ? this.service.hashCode() : 0);
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 17 * hash + (this.coverage != null ? this.coverage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
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
        if (this.coverage != other.coverage && (this.coverage == null || !this.coverage.equals(other.coverage))) {
            return false;
        }
        return true;
    }

}
