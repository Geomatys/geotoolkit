/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wfs.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="GetCapabilities" type="{http://www.opengis.net/wfs}GetCapabilitiesType"/>
 *         &lt;element name="DescribeFeatureType" type="{http://www.opengis.net/wfs}DescribeFeatureTypeType"/>
 *         &lt;element name="Transaction" type="{http://www.opengis.net/wfs}TransactionType"/>
 *         &lt;element name="GetFeature" type="{http://www.opengis.net/wfs}GetFeatureTypeType"/>
 *         &lt;element name="GetFeatureWithLock" type="{http://www.opengis.net/wfs}GetFeatureTypeType"/>
 *         &lt;element name="LockFeature" type="{http://www.opengis.net/wfs}LockFeatureTypeType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestType", propOrder = {
    "getCapabilitiesOrDescribeFeatureTypeOrTransaction"
})
public class RequestType {

    @XmlElementRefs({
        @XmlElementRef(name = "GetCapabilities", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "GetFeatureWithLock", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "DescribeFeatureType", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "GetFeature", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "LockFeature", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class),
        @XmlElementRef(name = "Transaction", namespace = "http://www.opengis.net/wfs", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> getCapabilitiesOrDescribeFeatureTypeOrTransaction;

    /**
     * Gets the value of the getCapabilitiesOrDescribeFeatureTypeOrTransaction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getCapabilitiesOrDescribeFeatureTypeOrTransaction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetCapabilitiesOrDescribeFeatureTypeOrTransaction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link LockFeatureTypeType }{@code >}
     * {@link JAXBElement }{@code <}{@link GetFeatureTypeType }{@code >}
     * {@link JAXBElement }{@code <}{@link DescribeFeatureTypeType }{@code >}
     * {@link JAXBElement }{@code <}{@link GetFeatureTypeType }{@code >}
     * {@link JAXBElement }{@code <}{@link GetCapabilitiesType }{@code >}
     * {@link JAXBElement }{@code <}{@link TransactionType }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getGetCapabilitiesOrDescribeFeatureTypeOrTransaction() {
        if (getCapabilitiesOrDescribeFeatureTypeOrTransaction == null) {
            getCapabilitiesOrDescribeFeatureTypeOrTransaction = new ArrayList<JAXBElement<?>>();
        }
        return this.getCapabilitiesOrDescribeFeatureTypeOrTransaction;
    }

}
