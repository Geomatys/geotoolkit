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
package org.geotoolkit.swe.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.AbstractCase;
import org.geotoolkit.swe.xml.AbstractConditionalData;


/**
 * <p>Java class for ConditionalDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConditionalDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataRecordType">
 *       &lt;sequence>
 *         &lt;element name="case" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swe/1.0}ConditionalValue" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalDataType", propOrder = {
    "_case"
})
public class ConditionalDataType extends AbstractDataRecordType implements AbstractConditionalData {

    @XmlElement(name = "case", required = true)
    private List<ConditionalDataType.Case> _case;

    public ConditionalDataType() {

    }

    public ConditionalDataType(final AbstractConditionalData da) {
        super(da);
        if (da != null && da.getCase() != null) {
            this._case = new ArrayList<Case>();
            for (AbstractCase ac : da.getCase()) {
                this._case.add(new Case(ac));
            }
        }
    }

    /**
     * Gets the value of the case property.
     */
    public List<ConditionalDataType.Case> getCase() {
        if (_case == null) {
            _case = new ArrayList<ConditionalDataType.Case>();
        }
        return this._case;
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
     *         &lt;element ref="{http://www.opengis.net/swe/1.0}ConditionalValue" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "conditionalValue"
    })
    public static class Case implements AbstractCase {

        @XmlElement(name = "ConditionalValue")
        private ConditionalValueType conditionalValue;
        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        private String name;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        @XmlSchemaType(name = "anyURI")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;

        public Case() {

        }

        public Case(final AbstractCase ac) {
            if (ac != null)  {
                this.actuate = ac.getActuate();
                this.arcrole = ac.getArcrole();
                this.href = ac.getHref();
                this.remoteSchema = ac.getRemoteSchema();
                this.role = ac.getRole();
                this.show = ac.getShow();
                this.title = ac.getTitle();
                this.type = ac.getType();
                this.name = ac.getName();
                if (ac.getConditionalValue() != null) {
                    this.conditionalValue = new ConditionalValueType(ac.getConditionalValue());
                }
            }
        }

        /**
         * Gets the value of the conditionalValue property.
         */
        public ConditionalValueType getConditionalValue() {
            return conditionalValue;
        }

        /**
         * Sets the value of the conditionalValue property.
         */
        public void setConditionalValue(final ConditionalValueType value) {
            this.conditionalValue = value;
        }

        /**
         * Gets the value of the name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         */
        public void setName(final String value) {
            this.name = value;
        }

        /**
         * Gets the value of the remoteSchema property.
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         */
        public void setRemoteSchema(final String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the type property.
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         *
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the href property.
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         */
        public void setHref(final String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         */
        public void setRole(final String value) {
            this.role = value;
        }

        /**
         * Gets the value of the arcrole property.
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         */
        public void setArcrole(final String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the title property.
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         */
        public void setTitle(final String value) {
            this.title = value;
        }

        /**
         * Gets the value of the show property.
         *
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         */
        public void setShow(final String value) {
            this.show = value;
        }

        /**
         * Gets the value of the actuate property.
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         *
         */
        public void setActuate(final String value) {
            this.actuate = value;
        }

    }

}
