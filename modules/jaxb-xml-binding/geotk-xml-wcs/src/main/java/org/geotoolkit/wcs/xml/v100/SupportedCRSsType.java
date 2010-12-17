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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CodeListType;


/**
 * Unordered list(s) of identifiers of Coordinate Reference Systems (CRSs) supported in server operation requests and responses. 
 * 
 * <p>Java class for SupportedCRSsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SupportedCRSsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="requestResponseCRSs" type="{http://www.opengis.net/gml}CodeListType" maxOccurs="unbounded"/>
 *           &lt;sequence>
 *             &lt;element name="requestCRSs" type="{http://www.opengis.net/gml}CodeListType" maxOccurs="unbounded"/>
 *             &lt;element name="responseCRSs" type="{http://www.opengis.net/gml}CodeListType" maxOccurs="unbounded"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element name="nativeCRSs" type="{http://www.opengis.net/gml}CodeListType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedCRSsType", propOrder = {
    "requestResponseCRSs",
    "requestCRSs",
    "responseCRSs",
    "nativeCRSs"
})
public class SupportedCRSsType {

    private List<CodeListType> requestResponseCRSs;
    private List<CodeListType> requestCRSs;
    private List<CodeListType> responseCRSs;
    private List<CodeListType> nativeCRSs;
    
    
    /**
     * An empty constructor used by JAXB
     */
    SupportedCRSsType(){
    }
    
    /**
     * Build a new light Supported Crs element with only the request/reponse CRS accepted.
     */
    public SupportedCRSsType(List<CodeListType> requestResponseCRSs){
        this.requestResponseCRSs = requestResponseCRSs;
    }
    
    /**
     * Build a new light Supported Crs element with only the request/reponse CRS accepted.
     * all the element of the list of codeList are in the parameters.
     */
    public SupportedCRSsType(CodeListType... requestResponseCRS){
        this.requestResponseCRSs = new ArrayList<CodeListType>();
        for (CodeListType element:requestResponseCRS) {
            requestResponseCRSs.add(element);
        }
    }
    
    /**
     * Build a new full Supported Crs element.
     */
    public SupportedCRSsType(List<CodeListType> requestResponseCRSs, List<CodeListType> requestCRSs,
            List<CodeListType> responseCRSs, List<CodeListType> nativeCRSs){
        this.nativeCRSs          = nativeCRSs;
        this.requestCRSs         = requestCRSs;
        this.requestResponseCRSs = requestResponseCRSs;
        this.responseCRSs        = responseCRSs;
    }
    
    /**
     * Gets the value of the requestResponseCRSs property (unmodifiable).
     */
    public List<CodeListType> getRequestResponseCRSs() {
        return requestResponseCRSs;
    }

    public void setRequestResponseCRSs(List<CodeListType> requestResponseCRSs) {
        this.requestResponseCRSs = requestResponseCRSs;
    }

    public void addRequestResponseCRSs(CodeListType requestResponseCRSs) {
        if (this.requestResponseCRSs == null) {
            this.requestResponseCRSs = new ArrayList<CodeListType>();
        }
        this.requestResponseCRSs.add(requestResponseCRSs);
    }

    /**
     * Gets the value of the requestCRSs property (unmodifiable).
     */
    public List<CodeListType> getRequestCRSs() {
        return requestCRSs;
    }

    public void setRequestCRSs(List<CodeListType> requestCRSs) {
        this.requestCRSs = requestCRSs;
    }

    public void addRequestCRSs(CodeListType requestCRSs) {
        if (this.requestCRSs == null) {
            this.requestCRSs = new ArrayList<CodeListType>();
        }
        this.requestCRSs.add(requestCRSs);
    }
    
    /**
     * Gets the value of the responseCRSs property (unmodifiable).
     * 
     */
    public List<CodeListType> getResponseCRSs() {
        return responseCRSs;
    }

    public void setResponseCRSs(List<CodeListType> responseCRSs) {
        this.responseCRSs = responseCRSs;
    }

    public void addResponseCRSs(CodeListType responseCRSs) {
        if (this.responseCRSs == null) {
            this.responseCRSs = new ArrayList<CodeListType>();
        }
        this.responseCRSs.add(responseCRSs);
    }

    /**
     * Gets the value of the nativeCRSs property (unmodifiable).
     * 
     */
    public List<CodeListType> getNativeCRSs() {
        return nativeCRSs;
    }

    public void setNativeCRSs(List<CodeListType> nativeCRSs) {
        this.nativeCRSs = nativeCRSs;
    }

    public void addNativeCRSs(CodeListType nativeCRSs) {
        if (this.nativeCRSs == null) {
            this.nativeCRSs = new ArrayList<CodeListType>();
        }
        this.nativeCRSs.add(nativeCRSs);
    }
}
