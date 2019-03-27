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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.ListOfValues;
import org.w3c.dom.Element;


/**
 * <p>Classe Java pour ListOfValuesType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="ListOfValuesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Value" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="isDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                 &lt;attribute name="count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *                 &lt;attribute name="uom" type="{http://www.opengis.net/gml/3.2}UomIdentifier" />
 *                 &lt;anyAttribute processContents='skip'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfValuesType", propOrder = {
    "value"
})
public class ListOfValuesType implements ListOfValues {

    @XmlElement(name = "Value", required = true)
    protected List<ListOfValuesType.Value> value;

    /**
     * An empty constructor used by JAXB
     */
     public ListOfValuesType(){

     }

     /**
      * Build a new List of values
      */
     public ListOfValuesType(final List<Object> values){
         if (values != null) {
            this.value = new ArrayList<>();
            for (Object v : values) {
                if (v instanceof Value) {
                    this.value.add((Value) v);
                } else {
                    this.value.add(new Value(v));
                }
            }
         }
     }

    /**
     * Gets the value of the value property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfValuesType.Value }
     *
     *
     */
    @Override
    public List<ListOfValuesType.Value> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ListOfValuesType]").append('\n');
        if (value != null) {
            sb.append("values:").append('\n');
            for (Value v : value) {
                sb.append(v).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ListOfValuesType) {
            final ListOfValuesType that = (ListOfValuesType) object;

            return  Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     *
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="isDefault" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *       &lt;attribute name="count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *       &lt;attribute name="uom" type="{http://www.opengis.net/gml/3.2}UomIdentifier" />
     *       &lt;anyAttribute processContents='skip'/>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class Value {

        // change in string instead of List<Element> for now... TODO
        @XmlValue
        protected String any;
        @XmlAttribute(name = "isDefault")
        protected Boolean isDefault;
        @XmlAttribute(name = "count")
        @XmlSchemaType(name = "nonNegativeInteger")
        protected Integer count;
        @XmlAttribute(name = "uom")
        protected String uom;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<>();

        public Value() {

        }

        public Value(Object value) {
            if (value != null) {
                if (value instanceof String) {
                    any = (String) value;
                } else {
                    throw new UnsupportedOperationException("Unsupported for now");
                }
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
         *
         *
         */
        public List<String> getAny() {
            if (any != null) {
                return Arrays.asList(this.any);
            }
            return null;
        }

        /**
         * Obtient la valeur de la propriété isDefault.
         *
         * @return
         *     possible object is
         *     {@link Boolean }
         *
         */
        public boolean isIsDefault() {
            if (isDefault == null) {
                return false;
            } else {
                return isDefault;
            }
        }

        /**
         * Définit la valeur de la propriété isDefault.
         *
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *
         */
        public void setIsDefault(Boolean value) {
            this.isDefault = value;
        }

        /**
         * Obtient la valeur de la propriété count.
         *
         * @return
         *     possible object is
         *     {@link Integer }
         *
         */
        public Integer getCount() {
            return count;
        }

        /**
         * Définit la valeur de la propriété count.
         *
         * @param value
         *     allowed object is
         *     {@link Integer }
         *
         */
        public void setCount(Integer value) {
            this.count = value;
        }

        /**
         * Obtient la valeur de la propriété uom.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getUom() {
            return uom;
        }

        /**
         * Définit la valeur de la propriété uom.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setUom(String value) {
            this.uom = value;
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
            StringBuilder sb = new StringBuilder("[Value]").append('\n');
            if (any != null) {
                sb.append("any:").append(any).append('\n');
                /*for (Object v : any) {
                    sb.append(v).append('\n');
                }*/
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to the specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Value) {
                final Value that = (Value) object;

                return  Objects.equals(this.any, that.any);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + (this.any != null ? this.any.hashCode() : 0);
            return hash;
        }

    }

}
