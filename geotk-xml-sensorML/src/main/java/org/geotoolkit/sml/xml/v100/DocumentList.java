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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v311.StringOrRefType;
import org.geotoolkit.sml.xml.AbstractDocumentList;
import org.geotoolkit.sml.xml.AbstractDocumentListMember;


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
 *         &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *         &lt;element name="member" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}Document"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "member"
})
@XmlRootElement(name = "DocumentList")
public class DocumentList implements AbstractDocumentList {

    @XmlElement(namespace = "http://www.opengis.net/gml")
    private StringOrRefType description;
    @XmlElement(required = true)
    private List<DocumentList.Member> member;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public DocumentList() {

    }

    public DocumentList(final AbstractDocumentList dl) {
        if (dl != null) {
            this.description = dl.getDescription();
            this.id          = dl.getId();
            if (dl.getMember() != null) {
                this.member = new ArrayList<Member>();
                for (AbstractDocumentListMember m : dl.getMember()) {
                    this.member.add(new Member(m));
                }
            }
        }
    }

    /**
     * Gets the value of the description property.
     */
    public StringOrRefType getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     */
    public void setDescription(final StringOrRefType value) {
        this.description = value;
    }

    /**
     * Gets the value of the member property.
     */
    public List<DocumentList.Member> getMember() {
        if (member == null) {
            member = new ArrayList<DocumentList.Member>();
        }
        return this.member;
    }

    public void setMember(final DocumentList.Member member) {
        if (this.member == null) {
            this.member = new ArrayList<DocumentList.Member>();
        }
        this.member.add(member);
    }

    public void setMember(final List<DocumentList.Member> member) {
        this.member = member;
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof DocumentList) {
            final DocumentList that = (DocumentList) object;
            return Objects.equals(this.id,          that.id)          &&
                   Objects.equals(this.member,      that.member)      &&
                   Objects.equals(this.description, that.description);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 53 * hash + (this.member != null ? this.member.hashCode() : 0);
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[DocumentList]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (member != null) {
            sb.append("member:").append('\n');
            for (Member k : member) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
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
     *       &lt;sequence minOccurs="0">
     *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}Document"/>
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
        "document"
    })
    public static class Member implements AbstractDocumentListMember {

        @XmlElement(name = "Document")
        private Document document;
        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        private String name;
        @XmlAttribute
        private List<String> nilReason;
        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;

        public Member() {

        }

        public Member(final AbstractDocumentListMember m) {
            if (m != null) {
                this.actuate = m.getActuate();
                this.arcrole = m.getArcrole();
                if (m.getDocument() != null) {
                    this.document = new Document(m.getDocument());
                }
                this.href = m.getHref();
                this.name = m.getName();
                this.remoteSchema = m.getRemoteSchema();
                this.role = m.getRole();
                this.show = m.getShow();
                this.title = m.getTitle();
                this.type = m.getType();
            }
        }

        /**
         * Gets the value of the document property.
         */
        public Document getDocument() {
            return document;
        }

        /**
         * Sets the value of the document property.
         */
        public void setDocument(final Document value) {
            this.document = value;
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
         * Gets the value of the nilReason property.
         */
        public List<String> getNilReason() {
            if (nilReason == null) {
                nilReason = new ArrayList<String>();
            }
            return this.nilReason;
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
         * Gets the value of the actuate property.
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         */
        public void setActuate(final String value) {
            this.actuate = value;
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
         * Gets the value of the type property.
         */
        public String getType() {
        return type;
     }

        /**
         * Sets the value of the type property.
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Member) {
            final Member that = (Member) object;
            return Objects.equals(this.actuate,      that.actuate)       &&
                   Objects.equals(this.arcrole,      that.arcrole)       &&
                   Objects.equals(this.href,         that.href)          &&
                   Objects.equals(this.nilReason,    that.nilReason)     &&
                   Objects.equals(this.remoteSchema, that.remoteSchema)  &&
                   Objects.equals(this.role,         that.role)          &&
                   Objects.equals(this.show,         that.show)          &&
                   Objects.equals(this.title,        that.title)         &&
                   Objects.equals(this.document,     that.document)   &&
                   Objects.equals(this.name,         that.name)   &&
                   Objects.equals(this.type,         that.type);
        }
        return false;
    }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 79 * hash + (this.document != null ? this.document.hashCode() : 0);
            hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 79 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
            hash = 79 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
            hash = 79 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
            hash = 79 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
            hash = 79 * hash + (this.href != null ? this.href.hashCode() : 0);
            hash = 79 * hash + (this.role != null ? this.role.hashCode() : 0);
            hash = 79 * hash + (this.show != null ? this.show.hashCode() : 0);
            hash = 79 * hash + (this.title != null ? this.title.hashCode() : 0);
            hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[DocumentList.member]").append("\n");
            if (document != null) {
                sb.append("document: ").append(document).append('\n');
            }
            if (nilReason != null) {
                sb.append("nilReason:").append('\n');
                for (String k : nilReason) {
                    sb.append("nilReason: ").append(k).append('\n');
                }
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
    }

}
