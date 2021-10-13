/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.mathml.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;choice>
 *           &lt;element ref="{http://www.w3.org/1998/Math/MathML}ci"/>
 *           &lt;group ref="{http://www.w3.org/1998/Math/MathML}semantics-ci"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.w3.org/1998/Math/MathML}degree"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ciOrSemanticsOrDegree"
})
@XmlRootElement(name = "bvar")
public class Bvar {

    @XmlElements({
        @XmlElement(name = "ci", type = Ci.class),
        @XmlElement(name = "semantics", type = Bvar.Semantics.class),
        @XmlElement(name = "degree", type = Degree.class)
    })
    protected List<Object> ciOrSemanticsOrDegree;

    /**
     * Gets the value of the ciOrSemanticsOrDegree property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ciOrSemanticsOrDegree property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCiOrSemanticsOrDegree().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ci }
     * {@link Bvar.Semantics }
     * {@link Degree }
     *
     *
     */
    public List<Object> getCiOrSemanticsOrDegree() {
        if (ciOrSemanticsOrDegree == null) {
            ciOrSemanticsOrDegree = new ArrayList<Object>();
        }
        return this.ciOrSemanticsOrDegree;
    }


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
     *         &lt;choice>
     *           &lt;element ref="{http://www.w3.org/1998/Math/MathML}ci"/>
     *           &lt;group ref="{http://www.w3.org/1998/Math/MathML}semantics-ci"/>
     *         &lt;/choice>
     *         &lt;choice maxOccurs="unbounded" minOccurs="0">
     *           &lt;element ref="{http://www.w3.org/1998/Math/MathML}annotation"/>
     *           &lt;element ref="{http://www.w3.org/1998/Math/MathML}annotation-xml"/>
     *         &lt;/choice>
     *       &lt;/sequence>
     *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}semantics.attributes"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "BvarSemanticsType", propOrder = {
        "ci",
        "semantics",
        "annotationOrAnnotationXml"
    })
    public static class Semantics {

        protected Ci ci;
        protected Bvar.Semantics semantics;
        @XmlElements({
            @XmlElement(name = "annotation", type = Annotation.class),
            @XmlElement(name = "annotation-xml", type = AnnotationXml.class)
        })
        protected List<Object> annotationOrAnnotationXml;
        @XmlAttribute(name = "cd")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String cd;
        @XmlAttribute(name = "name")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String name;
        @XmlAttribute(name = "encoding")
        protected String encoding;
        @XmlAttribute(name = "definitionURL")
        @XmlSchemaType(name = "anyURI")
        protected String definitionURL;
        @XmlAttribute(name = "id")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;
        @XmlAttribute(name = "xref")
        @XmlSchemaType(name = "anySimpleType")
        protected String xref;
        @XmlAttribute(name = "class")
        @XmlSchemaType(name = "NMTOKENS")
        protected List<String> clazz;
        @XmlAttribute(name = "style")
        protected String style;
        @XmlAttribute(name = "href")
        @XmlSchemaType(name = "anyURI")
        protected String href;
        @XmlAttribute(name = "other")
        @XmlSchemaType(name = "anySimpleType")
        protected String other;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Obtient la valeur de la propriété ci.
         *
         * @return
         *     possible object is
         *     {@link Ci }
         *
         */
        public Ci getCi() {
            return ci;
        }

        /**
         * Définit la valeur de la propriété ci.
         *
         * @param value
         *     allowed object is
         *     {@link Ci }
         *
         */
        public void setCi(Ci value) {
            this.ci = value;
        }

        /**
         * Obtient la valeur de la propriété semantics.
         *
         * @return
         *     possible object is
         *     {@link Bvar.Semantics }
         *
         */
        public Bvar.Semantics getSemantics() {
            return semantics;
        }

        /**
         * Définit la valeur de la propriété semantics.
         *
         * @param value
         *     allowed object is
         *     {@link Bvar.Semantics }
         *
         */
        public void setSemantics(Bvar.Semantics value) {
            this.semantics = value;
        }

        /**
         * Gets the value of the annotationOrAnnotationXml property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the annotationOrAnnotationXml property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAnnotationOrAnnotationXml().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Annotation }
         * {@link AnnotationXml }
         *
         *
         */
        public List<Object> getAnnotationOrAnnotationXml() {
            if (annotationOrAnnotationXml == null) {
                annotationOrAnnotationXml = new ArrayList<Object>();
            }
            return this.annotationOrAnnotationXml;
        }

        /**
         * Obtient la valeur de la propriété cd.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getCd() {
            return cd;
        }

        /**
         * Définit la valeur de la propriété cd.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setCd(String value) {
            this.cd = value;
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
         * Obtient la valeur de la propriété encoding.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getEncoding() {
            return encoding;
        }

        /**
         * Définit la valeur de la propriété encoding.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setEncoding(String value) {
            this.encoding = value;
        }

        /**
         * Obtient la valeur de la propriété definitionURL.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDefinitionURL() {
            return definitionURL;
        }

        /**
         * Définit la valeur de la propriété definitionURL.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDefinitionURL(String value) {
            this.definitionURL = value;
        }

        /**
         * Obtient la valeur de la propriété id.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getId() {
            return id;
        }

        /**
         * Définit la valeur de la propriété id.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setId(String value) {
            this.id = value;
        }

        /**
         * Obtient la valeur de la propriété xref.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getXref() {
            return xref;
        }

        /**
         * Définit la valeur de la propriété xref.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setXref(String value) {
            this.xref = value;
        }

        /**
         * Gets the value of the clazz property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the clazz property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getClazz().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         *
         *
         */
        public List<String> getClazz() {
            if (clazz == null) {
                clazz = new ArrayList<String>();
            }
            return this.clazz;
        }

        /**
         * Obtient la valeur de la propriété style.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getStyle() {
            return style;
        }

        /**
         * Définit la valeur de la propriété style.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setStyle(String value) {
            this.style = value;
        }

        /**
         * Obtient la valeur de la propriété href.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getHref() {
            return href;
        }

        /**
         * Définit la valeur de la propriété href.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setHref(String value) {
            this.href = value;
        }

        /**
         * Obtient la valeur de la propriété other.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOther() {
            return other;
        }

        /**
         * Définit la valeur de la propriété other.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOther(String value) {
            this.other = value;
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

    }

}
