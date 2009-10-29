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
package org.geotoolkit.sml.xml.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractIdentification;
import org.geotoolkit.sml.xml.AbstractIdentifier;
import org.geotoolkit.sml.xml.AbstractIdentifierList;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="IdentifierList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="identifier" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}Term"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "identifierList"
})
@XmlRootElement(name = "identification")
public class Identification implements AbstractIdentification {

    @XmlElement(name = "IdentifierList")
    private Identification.IdentifierList identifierList;
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

    public Identification () {

    }

    public Identification (IdentifierList identifierList) {
        this.identifierList = identifierList;
    }

    /**
     * Gets the value of the identifierList property.
     * 
     * @return
     *     possible object is
     *     {@link Identification.IdentifierList }
     *     
     */
    public Identification.IdentifierList getIdentifierList() {
        return identifierList;
    }

    /**
     * Sets the value of the identifierList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identification.IdentifierList }
     *     
     */
    public void setIdentifierList(Identification.IdentifierList value) {
        this.identifierList = value;
    }

    /**
     * Gets the value of the remoteSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Sets the value of the remoteSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSchema(String value) {
        this.remoteSchema = value;
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
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
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
     * Sets the value of the href property.
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
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the title property.
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
     * Sets the value of the title property.
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
     * Gets the value of the show property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShow() {
        return show;
    }

    /**
     * Sets the value of the show property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShow(String value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Sets the value of the actuate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActuate(String value) {
        this.actuate = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Identification]").append("\n");
        if (identifierList != null) {
            sb.append("identifierList: ").append(identifierList).append('\n');
        }

        if (remoteSchema != null) {
            sb.append("remoteSchema: ").append(remoteSchema).append('\n');
        }
        if (actuate != null) {
            sb.append("actuate: ").append(actuate).append('\n');
        }
        if (arcrole != null) {
            sb.append("actuate: ").append(arcrole).append('\n');
        }
        if (href != null) {
            sb.append("href: ").append(href).append('\n');
        }
        if (role != null) {
            sb.append("role: ").append(role).append('\n');
        }
        if (show != null) {
            sb.append("show: ").append(show).append('\n');
        }
        if (title != null) {
            sb.append("title: ").append(title).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
        }
        return sb.toString();
    }

     /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Identification) {
            final Identification that = (Identification) object;

            return Utilities.equals(this.actuate, that.actuate)           &&
                   Utilities.equals(this.href, that.href)                 &&
                   Utilities.equals(this.identifierList, that.identifierList) &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema) &&
                   Utilities.equals(this.role, that.role)                 &&
                   Utilities.equals(this.show, that.show)                 &&
                   Utilities.equals(this.title, that.title)               &&
                   Utilities.equals(this.type, that.type)                 &&
                   Utilities.equals(this.arcrole, that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.identifierList != null ? this.identifierList.hashCode() : 0);
        hash = 67 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 67 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 67 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 67 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 67 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 67 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 67 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
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
     *         &lt;element name="identifier" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}Term"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "identifier"
    })
    public static class IdentifierList implements AbstractIdentifierList {

        @XmlElement(required = true)
        private List<Identification.IdentifierList.Identifier> identifier;
        @XmlAttribute
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        private String id;

        public IdentifierList() {

        }

        public IdentifierList(String id, List<Identifier> identifiers) {
            this.id         = id;
            this.identifier = identifiers;
        }
        
        /**
         * Gets the value of the identifier property.
         */
        public List<Identification.IdentifierList.Identifier> getIdentifier() {
            if (identifier == null) {
                identifier = new ArrayList<Identification.IdentifierList.Identifier>();
            }
            return this.identifier;
        }

        /**
         * Gets the value of the id property.
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
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[IdentifierList]").append("\n");
            if (identifier != null) {
                for (Identification.IdentifierList.Identifier k: identifier) {
                    sb.append("identifier:").append(k).append('\n');
                }
            }
            if (id != null) {
                sb.append("id: ").append(id).append('\n');
            }
            return sb.toString();
        }

        /**
         * Verify if this entry is identical to specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof IdentifierList) {
                final IdentifierList that = (IdentifierList) object;

                return Utilities.equals(this.identifier, that.identifier) &&
                        Utilities.equals(this.id, that.id);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
            hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;

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
         *         &lt;element ref="{http://www.opengis.net/sensorML/1.0.1}Term"/>
         *       &lt;/sequence>
         *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}token" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "term"
        })
        public static class Identifier implements AbstractIdentifier {

            @XmlElement(name = "Term", required = true)
            private Term term;
            @XmlAttribute
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            private String name;

            public Identifier() {

            }

            public Identifier(String name, Term term) {
                this.name = name;
                this.term = term;
            }

            /**
             * Gets the value of the term property.
             * 
             * @return
             *     possible object is
             *     {@link Term }
             *     
             */
            public Term getTerm() {
                return term;
            }

            /**
             * Sets the value of the term property.
             * 
             * @param value
             *     allowed object is
             *     {@link Term }
             *     
             */
            public void setTerm(Term value) {
                this.term = value;
            }

            /**
             * Gets the value of the name property.
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
             * Sets the value of the name property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder("[Identifier]").append("\n");
                if (term != null) {
                    sb.append("term:").append(term).append('\n');
                }
                if (name != null) {
                    sb.append("name: ").append(name).append('\n');
                }
                return sb.toString();
            }

            /**
             * Verify if this entry is identical to specified object.
             */
            @Override
            public boolean equals(final Object object) {
                if (object == this) {
                    return true;
                }

                if (object instanceof Identifier) {
                    final Identifier that = (Identifier) object;

                    return Utilities.equals(this.name, that.name) &&
                            Utilities.equals(this.term, that.term);
                }
                return false;
            }

            @Override
            public int hashCode() {
                int hash = 5;
                hash = 13 * hash + (this.term != null ? this.term.hashCode() : 0);
                hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
                return hash;
            }

        }

    }

}
