/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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
package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.csw.xml.GetDomain;
import org.geotoolkit.ogc.xml.v200.FilterType;


/**
 * <p>Classe Java pour GetDomainType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetDomainType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}RequestBaseType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element name="ValueReference">
 *               &lt;complexType>
 *                 &lt;simpleContent>
 *                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                     &lt;attribute name="resultType" type="{http://www.opengis.net/cat/csw/3.0}ResultTypeType" default="available" />
 *                   &lt;/extension>
 *                 &lt;/simpleContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element ref="{http://www.opengis.net/fes/2.0}Filter" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element name="ParameterName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetDomainType", propOrder = {
    "valueReferenceAndFilterOrParameterName"
})
@XmlRootElement(name="GetDomain")
public class GetDomainType extends RequestBaseType implements GetDomain {

    @XmlElements({
        @XmlElement(name = "ValueReference", type = GetDomainType.ValueReference.class),
        @XmlElement(name = "Filter", namespace = "http://www.opengis.net/fes/2.0", type = FilterType.class),
        @XmlElement(name = "ParameterName", type = String.class)
    })
    protected List<Object> valueReferenceAndFilterOrParameterName;

    /**
     * An empty constructor used by JAXB
     */
    public GetDomainType() {

    }

    /**
     * Build a new GetDomain request. One of propertyName or parameterName must
     * be null
     *
     * @param service
     * @param version
     * @param propertyName
     */
    public GetDomainType(final String service, final String version, final String propertyName, final String parameterName) {
        super(service, version);
        if (propertyName != null && parameterName != null) {
            throw new IllegalArgumentException("One of propertyName or parameterName must be null");
        }
        this.valueReferenceAndFilterOrParameterName = new ArrayList<>();
        if (parameterName != null) {
            this.valueReferenceAndFilterOrParameterName.add(parameterName);
        }
        if (propertyName != null) {
            this.valueReferenceAndFilterOrParameterName.add(new ValueReference(propertyName));
        }
    }

    @Override
    public String getParameterName() {
        if (valueReferenceAndFilterOrParameterName != null) {
            for (Object o : valueReferenceAndFilterOrParameterName) {
                if (o instanceof String) {
                    return (String) o;
                }
            }
        }
        return null;
    }

    @Override
    public String getPropertyName() {
        if (valueReferenceAndFilterOrParameterName != null) {
            for (Object o : valueReferenceAndFilterOrParameterName) {
                if (o instanceof GetDomainType.ValueReference) {
                    return ((GetDomainType.ValueReference) o).getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getOutputFormat() {
        return "application/xml";
    }

    @Override
    public void setOutputFormat(final String value) {}


    /**
     * Gets the value of the valueReferenceAndFilterOrParameterName property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link GetDomainType.ValueReference }
     * {@link FilterType }
     * {@link String }
     *
     *
     */
    public List<Object> getValueReferenceAndFilterOrParameterName() {
        if (valueReferenceAndFilterOrParameterName == null) {
            valueReferenceAndFilterOrParameterName = new ArrayList<>();
        }
        return this.valueReferenceAndFilterOrParameterName;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="resultType" type="{http://www.opengis.net/cat/csw/3.0}ResultTypeType" default="available" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class ValueReference {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "resultType")
        protected ResultTypeType resultType;

        public ValueReference() {

        }

        public ValueReference(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété value.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Obtient la valeur de la propriété resultType.
         *
         * @return
         *     possible object is
         *     {@link ResultTypeType }
         *
         */
        public ResultTypeType getResultType() {
            if (resultType == null) {
                return ResultTypeType.AVAILABLE;
            } else {
                return resultType;
            }
        }

        /**
         * Définit la valeur de la propriété resultType.
         *
         * @param value
         *     allowed object is
         *     {@link ResultTypeType }
         *
         */
        public void setResultType(ResultTypeType value) {
            this.resultType = value;
        }

    }

}
