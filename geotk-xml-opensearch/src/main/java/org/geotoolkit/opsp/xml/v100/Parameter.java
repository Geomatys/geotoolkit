/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.opsp.xml.v100;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="minimum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maximum" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="minInclusive" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="maxInclusive" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="minExclusive" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="maxExclusive" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="step" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "any",
    "options"
})
@XmlRootElement(name = "Parameter")
public class Parameter {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "name")
    @XmlSchemaType(name = "anySimpleType")
    protected String name;
    @XmlAttribute(name = "value")
    @XmlSchemaType(name = "anySimpleType")
    protected String value;
    @XmlAttribute(name = "minimum")
    protected Integer minimum;
    @XmlAttribute(name = "maximum")
    protected Integer maximum;
    @XmlAttribute(name = "pattern")
    @XmlSchemaType(name = "anySimpleType")
    protected String pattern;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;
    @XmlAttribute(name = "minInclusive")
    protected Integer minInclusive;
    @XmlAttribute(name = "maxInclusive")
    protected Integer maxInclusive;
    @XmlAttribute(name = "minExclusive")
    protected Integer minExclusive;
    @XmlAttribute(name = "maxExclusive")
    protected Integer maxExclusive;
    @XmlAttribute(name = "step")
    protected Integer step;

    @XmlElement(name = "Option", namespace = "http://a9.com/-/spec/opensearch/extensions/parameters/1.0/")
    private List<Parameter> options;

    public Parameter() {

    }

    public Parameter(String value) {
        this.value = value;
    }

    public Parameter(String name, String value, String title) {
        this.name = name;
        this.title = title;
        this.value = value;
    }

    public Parameter(Parameter that) {
        if (that != null) {
            this.name= that.name;
            this.maxExclusive= that.maxExclusive;
            this.maxInclusive= that.maxInclusive;
            this.maximum= that.maximum;
            this.minExclusive= that.minExclusive;
            this.minInclusive= that.minInclusive;
            this.minimum= that.minimum;
            this.pattern= that.pattern;
            this.step= that.step;
            this.title= that.title;
            this.value= that.value;
            if (that.options != null) {
                this.options = new ArrayList<>();
                for (Parameter u : that.options) {
                    this.options.add(new Parameter(u));
                }
            }

            // unable to clone any property
            this.any = that.any;
        }
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
     * {@link Element }
     * {@link Object }
     *
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Obtient la valeur de la propriété name.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
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
     * Obtient la valeur de la propriété minimum.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMinimum() {
        return minimum;
    }

    /**
     * Définit la valeur de la propriété minimum.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMinimum(Integer value) {
        this.minimum = value;
    }

    /**
     * Obtient la valeur de la propriété maximum.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaximum() {
        return maximum;
    }

    /**
     * Définit la valeur de la propriété maximum.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaximum(Integer value) {
        this.maximum = value;
    }

    /**
     * Obtient la valeur de la propriété pattern.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Définit la valeur de la propriété pattern.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPattern(String value) {
        this.pattern = value;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtient la valeur de la propriété minInclusive.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMinInclusive() {
        return minInclusive;
    }

    /**
     * Définit la valeur de la propriété minInclusive.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMinInclusive(Integer value) {
        this.minInclusive = value;
    }

    /**
     * Obtient la valeur de la propriété maxInclusive.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxInclusive() {
        return maxInclusive;
    }

    /**
     * Définit la valeur de la propriété maxInclusive.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxInclusive(Integer value) {
        this.maxInclusive = value;
    }

    /**
     * Obtient la valeur de la propriété minExclusive.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMinExclusive() {
        return minExclusive;
    }

    /**
     * Définit la valeur de la propriété minExclusive.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMinExclusive(Integer value) {
        this.minExclusive = value;
    }

    /**
     * Obtient la valeur de la propriété maxExclusive.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getMaxExclusive() {
        return maxExclusive;
    }

    /**
     * Définit la valeur de la propriété maxExclusive.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setMaxExclusive(Integer value) {
        this.maxExclusive = value;
    }

    /**
     * Obtient la valeur de la propriété step.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getStep() {
        return step;
    }

    /**
     * Définit la valeur de la propriété step.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setStep(Integer value) {
        this.step = value;
    }

    /**
     * @return the options
     */
    public List<Parameter> getOptions() {
        if (options == null) {
            options = new ArrayList<>();
        }
        return options;
    }

    public void addOption(String option) {
        if (option != null) {
            getOptions().add(new Parameter(option));
        }
    }

    public void addOption(Parameter option) {
        if (option != null) {
            getOptions().add(option);
        }
    }

    /**
     * @param options the options to set
     */
    public void setOptions(List<Parameter> options) {
        this.options = options;
    }

}
