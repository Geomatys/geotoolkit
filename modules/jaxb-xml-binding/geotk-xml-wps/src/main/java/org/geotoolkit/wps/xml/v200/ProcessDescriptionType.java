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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.wps.xml.ProcessDescription;


/**
 * Full description of a process.
 *
 *
 * In this use, the DescriptionType shall describe process properties.
 *
 *
 * <p>Java class for ProcessDescriptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DescriptionType">
 *       &lt;sequence>
 *         &lt;element name="Input" type="{http://www.opengis.net/wps/2.0}InputDescriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Output" type="{http://www.opengis.net/wps/2.0}OutputDescriptionType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessDescriptionType", propOrder = {
    "input",
    "output"
})
public class ProcessDescriptionType extends DescriptionType  implements ProcessDescription {

    @XmlElement(name = "Input")
    protected List<InputDescriptionType> input;
    @XmlElement(name = "Output", required = true)
    protected List<OutputDescriptionType> output;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;

    public ProcessDescriptionType() {

    }

    public ProcessDescriptionType(CodeType identifier, final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, List<InputDescriptionType> input, List<OutputDescriptionType> output) {
        super(identifier, title, _abstract, keywords);
        this.input = input;
        this.output = output;
    }
    /**
     * Gets the value of the input property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InputDescriptionType }
     *
     *
     */
    public List<InputDescriptionType> getInput() {
        if (input == null) {
            input = new ArrayList<>();
        }
        return this.input;
    }

    /**
     * Gets the value of the output property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the output property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
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

    /**
     *
     * Identifier of a language used by the data(set) contents.
     * This language identifier shall be as specified in IETF RFC 4646. The
     * language tags shall be either complete 5 character codes (e.g. "en-CA"),
     * or abbreviated 2 character codes (e.g. "en"). In addition to the RFC
     * 4646 codes, the server shall support the single special value "*" which
     * is used to indicate "any language".
     *
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLang(String value) {
        this.lang = value;
    }

}
