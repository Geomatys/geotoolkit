/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.xml.DataDescription;
import org.geotoolkit.wps.xml.OutputDescription;


/**
 * Description of a process Output. 
 * 
 * In this use, the DescriptionType shall describe a process output.
 * 					
 * 
 * <p>Java class for OutputDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutputDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DescriptionType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}DataDescription"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDescriptionType" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutputDescriptionType", propOrder = {
    "dataDescription",
    "output"
})
public class OutputDescriptionType extends DescriptionType implements OutputDescription {

    @XmlElementRef(name = "DataDescription", namespace = "http://www.opengis.net/wps/2.0", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends DataDescriptionType> dataDescription;
    @XmlElement(name = "Output")
    protected List<OutputDescriptionType> output;

    public OutputDescriptionType() {
        
    }
    
    public OutputDescriptionType(CodeType identifier, List<LanguageStringType> title, List<LanguageStringType> _abstract, 
           List<KeywordsType>keywords, DataDescription dataDescription) {
        super(identifier, title, _abstract, keywords);
        final ObjectFactory factory = new ObjectFactory();
        if (dataDescription instanceof ComplexDataType) {
            this.dataDescription = factory.createComplexData((ComplexDataType) dataDescription);
        } else if (dataDescription instanceof LiteralDataType) {
            this.dataDescription = factory.createLiteralData((LiteralDataType) dataDescription);
        } else if (dataDescription instanceof BoundingBoxData) {
            this.dataDescription = factory.createBoundingBoxData((BoundingBoxData) dataDescription);
        } else if (dataDescription != null) {
            throw new IllegalArgumentException("unecpected data description type:" + dataDescription.getClass().getName());
        }
    }
    /**
     * Gets the value of the dataDescription property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BoundingBoxData }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComplexDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataDescriptionType }{@code >}
     *     
     */
    public JAXBElement<? extends DataDescriptionType> getDataDescription() {
        return dataDescription;
    }

    /**
     * Sets the value of the dataDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BoundingBoxData }{@code >}
     *     {@link JAXBElement }{@code <}{@link LiteralDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComplexDataType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataDescriptionType }{@code >}
     *     
     */
    public void setDataDescription(JAXBElement<? extends DataDescriptionType> value) {
        this.dataDescription = value;
    }

    /**
     * Gets the value of the output property.
     * 
     * @return Objects of the following type(s) are allowed in the list
     * {@link OutputDescriptionType }
     * 
     * 
     */
    public List<OutputDescriptionType> getOutput() {
        if (output == null) {
            output = new ArrayList<>();
        }
        return this.output;
    }

}
