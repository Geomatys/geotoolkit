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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDomain;
import org.geotoolkit.ows.xml.AbstractOperation;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;


/**
 * 
 * XML encoded WCS GetCapabilities operation response. 
 * The Capabilities document provides clients with service metadata about a specific service instance,
 * including metadata about the coverages served. 
 *       
 * WCS version 1.0.0
 * <p>Java class for WCSCapabilityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WCSCapabilityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Request">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GetCapabilities">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="DescribeCoverage">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="GetCoverage">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="DCPType" type="{http://www.opengis.net/wcs}DCPTypeType" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Exception">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Format" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VendorSpecificCapabilities" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" fixed="1.0.0" />
 *       &lt;attribute name="updateSequence" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WCSCapabilityType", propOrder = {
    "request",
    "exception",
    "vendorSpecificCapabilities"
})
public class WCSCapabilityType implements AbstractOperationsMetadata {

    @XmlElement(name = "Request", required = true)
    private Request request;
    @XmlElement(name = "Exception", required = true)
    private WCSCapabilityType.Exception exception;
    @XmlElement(name = "VendorSpecificCapabilities")
    private WCSCapabilityType.VendorSpecificCapabilities vendorSpecificCapabilities;
    @XmlAttribute
    private String version;
    @XmlAttribute
    private String updateSequence;

    public WCSCapabilityType() {
        
    }
    
    public WCSCapabilityType(final Request request, final Exception exption) {
        this.exception = exption;
        this.request = request;
    }
    
    public WCSCapabilityType(final Request request, final Exception exption, 
            final VendorSpecificCapabilities vCapa, final String version, final String upseq) {
        this.exception = exption;
        this.request = request;
        this.vendorSpecificCapabilities = vCapa;
        this.version = version;
        this.updateSequence = upseq;
    }
    
    /**
     * Gets the value of the request property.
     */
    public Request getRequest() {
        return request;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    /**
     * Gets the value of the exception property.
     * 
     */
    public WCSCapabilityType.Exception getException() {
        return exception;
    }

    /**
     * Gets the value of the vendorSpecificCapabilities property.
     * 
    */
    public WCSCapabilityType.VendorSpecificCapabilities getVendorSpecificCapabilities() {
        return vendorSpecificCapabilities;
    }

    /**
     * Gets the value of the version property.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the value of the updateSequence property.
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public void updateURL(final String url) {
        if (request != null) {
            request.updateURL(url);
        }
    }

    @Override
    public void addConstraint(final AbstractDomain domain) {
        //do nothing
    }

    @Override
    public AbstractOperation getOperation(final String operationName) {
        if (request != null) {
            return request.getOperation(operationName);
        }
        return null;
    }

    @Override
    public void removeOperation(final String operationName) {
        if ("GetCapabilities".equalsIgnoreCase(operationName)) {
            request.setGetCapabilities(null);
        } else if ("DescribeCoverage".equalsIgnoreCase(operationName)) {
            request.setDescribeCoverage(null);
        } else if ("GetCoverage".equalsIgnoreCase(operationName)) {
            request.setGetCoverage(null);
        }
    }

    @Override
    public List<? extends AbstractOperation> getOperation() {
        final List<AbstractOperation>  operations = new ArrayList<>();
        if (request.getDescribeCoverage() != null) {
            operations.add(request.getDescribeCoverage());
        }
        if (request.getGetCapabilities()!= null) {
            operations.add(request.getGetCapabilities());
        }
        if (request.getGetCoverage()!= null) {
            operations.add(request.getGetCoverage());
        }
        return operations;
    }

    @Override
    public AbstractDomain getParameter(String name) {
        //no constraint
        return null;
    }

    @Override
    public AbstractDomain getConstraint(String name) {
        //no constraint
        return null;
    }

    @Override
    public void removeConstraint(String name) {
        //no constraint
    }

    @Override
    public Object getExtendedCapabilities() {
        if (vendorSpecificCapabilities != null) {
            return vendorSpecificCapabilities.getAny();
        }
        return null;
    }

    @Override
    public void setExtendedCapabilities(Object extendedCapabilities) {
        if (vendorSpecificCapabilities != null) {
            vendorSpecificCapabilities.setAny(extendedCapabilities);
        }
    }

    @Override
    public AbstractOperationsMetadata clone() {
        Request r = null; 
        if (this.request != null) {
            r = this.request.clone();
        }
        Exception e = null; 
        if (this.exception != null) {
            e = new Exception(new ArrayList<>(this.exception.getFormat()));
        }
        VendorSpecificCapabilities v = null;
        if (this.vendorSpecificCapabilities != null) {
            v = new VendorSpecificCapabilities(this.vendorSpecificCapabilities.any);
        }
        return new WCSCapabilityType(r, e, v, this.version, this.updateSequence);
    }

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
     *         &lt;element name="Format" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "format"
    })
    public static class Exception {

        @XmlElement(name = "Format", required = true)
        private List<String> format;

        public Exception() {
            
        }
        
        public Exception(final List<String> format) {
            this.format = format;
        }
        
        /**
         * Gets the value of the format property.
         * 
         */
        public List<String> getFormat() {
            if (format == null) {
                format = new ArrayList<>();
            }
            return this.format;
        }

    }

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
     *         &lt;any/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class VendorSpecificCapabilities {

        @XmlAnyElement(lax = true)
        private Object any;

        public VendorSpecificCapabilities() {
        
        }
        
        public VendorSpecificCapabilities(final Object any) {
            this.any = any;
        }
        
        /**
         * Gets the value of the any property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setAny(final Object value) {
            this.any = value;
        }

    }

}
