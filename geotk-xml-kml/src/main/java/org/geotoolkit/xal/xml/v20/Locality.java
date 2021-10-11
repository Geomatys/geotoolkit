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
package org.geotoolkit.xal.xml.v20;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


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
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}AddressLine" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="LocalityName" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostBox"/>
 *           &lt;element name="LargeMailUser" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}LargeMailUserType"/>
 *           &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostOffice"/>
 *           &lt;element name="PostalRoute" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalRouteType"/>
 *         &lt;/choice>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Thoroughfare" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}Premise" minOccurs="0"/>
 *         &lt;element name="DependentLocality" type="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}DependentLocalityType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}PostalCode" minOccurs="0"/>
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="UsageType" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="Indicator" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "localityName",
    "postBox",
    "largeMailUser",
    "postOffice",
    "postalRoute",
    "thoroughfare",
    "premise",
    "dependentLocality",
    "postalCode",
    "any"
})
@XmlRootElement(name = "Locality")
public class Locality {

    @XmlElement(name = "AddressLine")
    private List<AddressLine> addressLine;
    @XmlElement(name = "LocalityName")
    private List<Locality.LocalityName> localityName;
    @XmlElement(name = "PostBox")
    private PostBox postBox;
    @XmlElement(name = "LargeMailUser")
    private LargeMailUserType largeMailUser;
    @XmlElement(name = "PostOffice")
    private PostOffice postOffice;
    @XmlElement(name = "PostalRoute")
    private PostalRouteType postalRoute;
    @XmlElement(name = "Thoroughfare")
    private Thoroughfare thoroughfare;
    @XmlElement(name = "Premise")
    private Premise premise;
    @XmlElement(name = "DependentLocality")
    private DependentLocalityType dependentLocality;
    @XmlElement(name = "PostalCode")
    private PostalCode postalCode;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    private String type;
    @XmlAttribute(name = "UsageType")
    @XmlSchemaType(name = "anySimpleType")
    private String usageType;
    @XmlAttribute(name = "Indicator")
    @XmlSchemaType(name = "anySimpleType")
    private String indicator;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the addressLine property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressLine property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressLine().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressLine }
     *
     *
     */
    public List<AddressLine> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<AddressLine>();
        }
        return this.addressLine;
    }

    /**
     * Gets the value of the localityName property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localityName property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalityName().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Locality.LocalityName }
     *
     *
     */
    public List<Locality.LocalityName> getLocalityName() {
        if (localityName == null) {
            localityName = new ArrayList<Locality.LocalityName>();
        }
        return this.localityName;
    }

    /**
     * Gets the value of the postBox property.
     *
     * @return
     *     possible object is
     *     {@link PostBox }
     *
     */
    public PostBox getPostBox() {
        return postBox;
    }

    /**
     * Sets the value of the postBox property.
     *
     * @param value
     *     allowed object is
     *     {@link PostBox }
     *
     */
    public void setPostBox(final PostBox value) {
        this.postBox = value;
    }

    /**
     * Gets the value of the largeMailUser property.
     *
     * @return
     *     possible object is
     *     {@link LargeMailUserType }
     *
     */
    public LargeMailUserType getLargeMailUser() {
        return largeMailUser;
    }

    /**
     * Sets the value of the largeMailUser property.
     *
     * @param value
     *     allowed object is
     *     {@link LargeMailUserType }
     *
     */
    public void setLargeMailUser(final LargeMailUserType value) {
        this.largeMailUser = value;
    }

    /**
     * Gets the value of the postOffice property.
     *
     * @return
     *     possible object is
     *     {@link PostOffice }
     *
     */
    public PostOffice getPostOffice() {
        return postOffice;
    }

    /**
     * Sets the value of the postOffice property.
     *
     * @param value
     *     allowed object is
     *     {@link PostOffice }
     *
     */
    public void setPostOffice(final PostOffice value) {
        this.postOffice = value;
    }

    /**
     * Gets the value of the postalRoute property.
     *
     * @return
     *     possible object is
     *     {@link PostalRouteType }
     *
     */
    public PostalRouteType getPostalRoute() {
        return postalRoute;
    }

    /**
     * Sets the value of the postalRoute property.
     *
     * @param value
     *     allowed object is
     *     {@link PostalRouteType }
     *
     */
    public void setPostalRoute(final PostalRouteType value) {
        this.postalRoute = value;
    }

    /**
     * Gets the value of the thoroughfare property.
     *
     * @return
     *     possible object is
     *     {@link Thoroughfare }
     *
     */
    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    /**
     * Sets the value of the thoroughfare property.
     *
     * @param value
     *     allowed object is
     *     {@link Thoroughfare }
     *
     */
    public void setThoroughfare(final Thoroughfare value) {
        this.thoroughfare = value;
    }

    /**
     * Gets the value of the premise property.
     *
     * @return
     *     possible object is
     *     {@link Premise }
     *
     */
    public Premise getPremise() {
        return premise;
    }

    /**
     * Sets the value of the premise property.
     *
     * @param value
     *     allowed object is
     *     {@link Premise }
     *
     */
    public void setPremise(final Premise value) {
        this.premise = value;
    }

    /**
     * Gets the value of the dependentLocality property.
     *
     * @return
     *     possible object is
     *     {@link DependentLocalityType }
     *
     */
    public DependentLocalityType getDependentLocality() {
        return dependentLocality;
    }

    /**
     * Sets the value of the dependentLocality property.
     *
     * @param value
     *     allowed object is
     *     {@link DependentLocalityType }
     *
     */
    public void setDependentLocality(final DependentLocalityType value) {
        this.dependentLocality = value;
    }

    /**
     * Gets the value of the postalCode property.
     *
     * @return
     *     possible object is
     *     {@link PostalCode }
     *
     */
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     *
     * @param value
     *     allowed object is
     *     {@link PostalCode }
     *
     */
    public void setPostalCode(final PostalCode value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(final String value) {
        this.type = value;
    }

    /**
     * Gets the value of the usageType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUsageType() {
        return usageType;
    }

    /**
     * Sets the value of the usageType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUsageType(final String value) {
        this.usageType = value;
    }

    /**
     * Gets the value of the indicator property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIndicator() {
        return indicator;
    }

    /**
     * Sets the value of the indicator property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIndicator(final String value) {
        this.indicator = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Locality]\n");
        if (addressLine != null) {
            sb.append("addressLine:\n");
            for (AddressLine a : addressLine) {
                sb.append(a).append('\n');
            }
        }
        if (any != null) {
            sb.append("any:\n");
            for (Object o : any) {
                sb.append(o).append('\n');
            }
        }
        if (dependentLocality != null) {
            sb.append("dependentLocality:").append(dependentLocality).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (indicator != null) {
            sb.append("indicator:").append(indicator).append('\n');
        }
        if (largeMailUser != null) {
            sb.append("largeMailUser:").append(largeMailUser).append('\n');
        }
        if (localityName != null) {
            sb.append("localityName:\n");
            for (LocalityName o : localityName) {
                sb.append(o).append('\n');
            }
        }
        if (otherAttributes != null) {
            sb.append("otherAttributes:\n");
            for (Entry entry : otherAttributes.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        if (postBox != null) {
            sb.append("postBox:").append(postBox).append('\n');
        }
        if (postOffice != null) {
            sb.append("postOffice:").append(postOffice).append('\n');
        }
        if (postalRoute != null) {
            sb.append("postalRoute:").append(postalRoute).append('\n');
        }
        if (premise != null) {
            sb.append("premise:").append(premise).append('\n');
        }
        if (thoroughfare != null) {
            sb.append("thoroughfare:").append(thoroughfare).append('\n');
        }
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (usageType != null) {
            sb.append("usageType:").append(usageType).append('\n');
        }
        return sb.toString();
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
     *       &lt;attGroup ref="{urn:oasis:names:tc:ciq:xsdschema:xAL:2.0}grPostal"/>
     *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    public static class LocalityName {

        @XmlValue
        private String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        private String type;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        private String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Gets the value of the content property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getContent() {
            return content;
        }

        /**
         * Sets the value of the content property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setContent(final String value) {
            this.content = value;
        }

        /**
         * Gets the value of the type property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the code property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the value of the code property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCode(final String value) {
            this.code = value;
        }

        /**
         * Gets a map that contains attributes that aren't bound to any typed property on this class.
         *
         * <p>
         * the map is keyed by the name of the attribute and
         * the value is the string value of the attribute.
         *
         * the map returned by this method is live, and you can add new attribute
         * by updating the map directly. Because of this design, there's no setter.
         *
         *
         * @return
         *     always non-null
         */
        public Map<QName, String> getOtherAttributes() {
            return otherAttributes;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[LocalityName]\n");
            if (code != null) {
                sb.append("code:").append(code).append('\n');
            }
            if (content != null) {
                sb.append("content:").append(content).append('\n');
            }
            if (type != null) {
                sb.append("type:").append(type).append('\n');
            }
            if (otherAttributes != null) {
                sb.append("otherAttributes:\n");
                for (Entry entry : otherAttributes.entrySet()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
            return sb.toString();
        }
    }
}
