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

package org.geotoolkit.gmlcov.xml.v100;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractGeneralParameterValueType;
import org.geotoolkit.gml.xml.v321.DMSAngleType;
import org.geotoolkit.gml.xml.v321.GeometryPropertyType;
import org.geotoolkit.gml.xml.v321.MeasureListType;
import org.geotoolkit.gml.xml.v321.MeasureType;
import org.geotoolkit.gml.xml.v321.OperationParameterPropertyType;
import org.geotoolkit.gml.xml.v321.VectorType;


/**
 * <p>Java class for ParameterValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParameterValueType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGeneralParameterValueType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}value"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}dmsAngleValue"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}stringValue"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}integerValue"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}booleanValue"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}valueList"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}integerValueList"/>
 *           &lt;element ref="{http://www.opengis.net/gml/3.2}valueFile"/>
 *           &lt;element ref="{http://www.opengis.net/gmlcov/1.0}geometryValue"/>
 *           &lt;element ref="{http://www.opengis.net/gmlcov/1.0}vectorValue"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}operationParameterProperty"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParameterValueType", propOrder = {
    "value",
    "dmsAngleValue",
    "stringValue",
    "integerValue",
    "booleanValue",
    "valueList",
    "integerValueList",
    "valueFile",
    "geometryValue",
    "vectorValue",
    "operationParameterProperty"
})
public class ParameterValueType extends AbstractGeneralParameterValueType {

    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private MeasureType value;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private DMSAngleType dmsAngleValue;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private String stringValue;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    @XmlSchemaType(name = "positiveInteger")
    private BigInteger integerValue;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private Boolean booleanValue;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private MeasureListType valueList;
    @XmlList
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private List<BigInteger> integerValueList;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    @XmlSchemaType(name = "anyURI")
    private String valueFile;
    private GeometryPropertyType geometryValue;
    private VectorType vectorValue;
    @XmlElementRef(name = "operationParameterProperty", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<OperationParameterPropertyType> operationParameterProperty;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setValue(MeasureType value) {
        this.value = value;
    }

    /**
     * Gets the value of the dmsAngleValue property.
     * 
     * @return
     *     possible object is
     *     {@link DMSAngleType }
     *     
     */
    public DMSAngleType getDmsAngleValue() {
        return dmsAngleValue;
    }

    /**
     * Sets the value of the dmsAngleValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link DMSAngleType }
     *     
     */
    public void setDmsAngleValue(DMSAngleType value) {
        this.dmsAngleValue = value;
    }

    /**
     * Gets the value of the stringValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the value of the stringValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStringValue(String value) {
        this.stringValue = value;
    }

    /**
     * Gets the value of the integerValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIntegerValue() {
        return integerValue;
    }

    /**
     * Sets the value of the integerValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIntegerValue(BigInteger value) {
        this.integerValue = value;
    }

    /**
     * Gets the value of the booleanValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBooleanValue() {
        return booleanValue;
    }

    /**
     * Sets the value of the booleanValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBooleanValue(Boolean value) {
        this.booleanValue = value;
    }

    /**
     * Gets the value of the valueList property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureListType }
     *     
     */
    public MeasureListType getValueList() {
        return valueList;
    }

    /**
     * Sets the value of the valueList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureListType }
     *     
     */
    public void setValueList(MeasureListType value) {
        this.valueList = value;
    }

    /**
     * Gets the value of the integerValueList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the integerValueList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntegerValueList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    public List<BigInteger> getIntegerValueList() {
        if (integerValueList == null) {
            integerValueList = new ArrayList<BigInteger>();
        }
        return this.integerValueList;
    }

    /**
     * Gets the value of the valueFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueFile() {
        return valueFile;
    }

    /**
     * Sets the value of the valueFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueFile(String value) {
        this.valueFile = value;
    }

    /**
     * Gets the value of the geometryValue property.
     * 
     * @return
     *     possible object is
     *     {@link GeometryPropertyType }
     *     
     */
    public GeometryPropertyType getGeometryValue() {
        return geometryValue;
    }

    /**
     * Sets the value of the geometryValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometryPropertyType }
     *     
     */
    public void setGeometryValue(GeometryPropertyType value) {
        this.geometryValue = value;
    }

    /**
     * Gets the value of the vectorValue property.
     * 
     * @return
     *     possible object is
     *     {@link VectorType }
     *     
     */
    public VectorType getVectorValue() {
        return vectorValue;
    }

    /**
     * Sets the value of the vectorValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link VectorType }
     *     
     */
    public void setVectorValue(VectorType value) {
        this.vectorValue = value;
    }

    /**
     * Gets the value of the operationParameterProperty property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link OperationParameterPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OperationParameterPropertyType }{@code >}
     *     
     */
    public JAXBElement<OperationParameterPropertyType> getOperationParameterProperty() {
        return operationParameterProperty;
    }

    /**
     * Sets the value of the operationParameterProperty property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link OperationParameterPropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link OperationParameterPropertyType }{@code >}
     *     
     */
    public void setOperationParameterProperty(JAXBElement<OperationParameterPropertyType> value) {
        this.operationParameterProperty = ((JAXBElement<OperationParameterPropertyType> ) value);
    }

}
