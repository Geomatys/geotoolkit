/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.dif.xml.v102;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *
 *                 | DIF 9      | ECHO 10         | UMM                  | DIF 10           | Notes                                                              |
 *                 | ---------- | --------------- | -------------------- | ---------------- | ------------------------------------------------------------------ |
 *                 | Parameters | ScienceKeywords | ScienceKeywords      | Science_Keywords | Aligned the DIF with how Science Keywords are used everywhere else |
 *
 *
 * <p>Classe Java pour ScienceKeywordsType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ScienceKeywordsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Category" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Topic" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Term" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Variable_Level_1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Variable_Level_2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Variable_Level_3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Detailed_Variable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uuid" type="{http://gcmd.gsfc.nasa.gov/Aboutus/xml/dif/}UuidType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScienceKeywordsType", propOrder = {
    "category",
    "topic",
    "term",
    "variableLevel1",
    "variableLevel2",
    "variableLevel3",
    "detailedVariable"
})
public class ScienceKeywordsType {

    @XmlElement(name = "Category", required = true)
    protected String category;
    @XmlElement(name = "Topic", required = true)
    protected String topic;
    @XmlElement(name = "Term", required = true)
    protected String term;
    @XmlElement(name = "Variable_Level_1")
    protected String variableLevel1;
    @XmlElement(name = "Variable_Level_2")
    protected String variableLevel2;
    @XmlElement(name = "Variable_Level_3")
    protected String variableLevel3;
    @XmlElement(name = "Detailed_Variable")
    protected String detailedVariable;
    @XmlAttribute(name = "uuid")
    protected String uuid;

    /**
     * Obtient la valeur de la propriété category.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCategory() {
        return category;
    }

    /**
     * Définit la valeur de la propriété category.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Obtient la valeur de la propriété topic.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Définit la valeur de la propriété topic.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTopic(String value) {
        this.topic = value;
    }

    /**
     * Obtient la valeur de la propriété term.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTerm() {
        return term;
    }

    /**
     * Définit la valeur de la propriété term.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTerm(String value) {
        this.term = value;
    }

    /**
     * Obtient la valeur de la propriété variableLevel1.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVariableLevel1() {
        return variableLevel1;
    }

    /**
     * Définit la valeur de la propriété variableLevel1.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVariableLevel1(String value) {
        this.variableLevel1 = value;
    }

    /**
     * Obtient la valeur de la propriété variableLevel2.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVariableLevel2() {
        return variableLevel2;
    }

    /**
     * Définit la valeur de la propriété variableLevel2.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVariableLevel2(String value) {
        this.variableLevel2 = value;
    }

    /**
     * Obtient la valeur de la propriété variableLevel3.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVariableLevel3() {
        return variableLevel3;
    }

    /**
     * Définit la valeur de la propriété variableLevel3.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVariableLevel3(String value) {
        this.variableLevel3 = value;
    }

    /**
     * Obtient la valeur de la propriété detailedVariable.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDetailedVariable() {
        return detailedVariable;
    }

    /**
     * Définit la valeur de la propriété detailedVariable.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDetailedVariable(String value) {
        this.detailedVariable = value;
    }

    /**
     * Obtient la valeur de la propriété uuid.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Définit la valeur de la propriété uuid.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUuid(String value) {
        this.uuid = value;
    }

}
