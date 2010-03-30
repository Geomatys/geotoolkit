/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.SMLMember;
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
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}DocumentList"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}ContactList"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "process",
    "documentList",
    "contactList"
})
public class Member implements SMLMember {

    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractProcessType> process;
    @XmlElement(name = "DocumentList")
    private DocumentList documentList;
    @XmlElement(name = "ContactList")
    private ContactList contactList;
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

    public Member(AbstractProcessType process) {
        ObjectFactory factory = new ObjectFactory();
        if (process instanceof SystemType) {
            this.process = factory.createSystem((SystemType) process);
        } else if (process instanceof ComponentType) {
            this.process = factory.createComponent((ComponentType) process);
        } else if (process instanceof DataSourceType) {
            this.process = factory.createDataSource((DataSourceType) process);
        } else if (process instanceof ProcessChainType) {
            this.process = factory.createProcessChain((ProcessChainType) process);
        } else if (process instanceof ProcessModelType) {
            this.process = factory.createProcessModel((ProcessModelType) process);
        } else if (process instanceof ComponentArrayType) {
            this.process = factory.createComponentArray((ComponentArrayType) process);
        } else {
            System.out.println("Unexpected AbstractProcessType:" + process);
        }
    }

    public Member(SystemType system) {
        ObjectFactory factory = new ObjectFactory();
        this.process = factory.createSystem(system);
    }

    public Member(ComponentType compo) {
        ObjectFactory factory = new ObjectFactory();
        this.process = factory.createComponent(compo);
    }

    /**
     * Gets the value of the process property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractProcessType> getProcess() {
        return process;
    }

    public AbstractProcess getRealProcess() {
        if (process != null) {
            return process.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the process property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SystemType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessChainType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractProcessType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataSourceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProcessModelType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ComponentArrayType }{@code >}
     *
     */
    public void setProcess(JAXBElement<? extends AbstractProcessType> value) {
        this.process = value;
    }

    /**
     * Sets the value of the process property.
     *
     * @param value
     *     allowed object is
     *    {@link ComponentType }
     *    {@link SystemType }
     *    {@link ProcessChainType }
     *    {@link AbstractProcessType }
     *    {@link DataSourceType }
     *    {@link ProcessModelType }
     *    {@link ComponentArrayType }
     *
     */
    public void setProcess(AbstractProcessType value) {
        ObjectFactory factory = new ObjectFactory();
        if (value instanceof ComponentType) {
            this.process = factory.createComponent((ComponentType) value);
        } else if (value instanceof SystemType) {
            this.process = factory.createSystem((SystemType) value);
        } else if (value instanceof ProcessChainType) {
            this.process = factory.createProcessChain((ProcessChainType) value);
        } else if (value instanceof DataSourceType) {
            this.process = factory.createDataSource((DataSourceType) value);
        } else if (value instanceof ComponentArrayType) {
            this.process = factory.createComponentArray((ComponentArrayType) value);
        }
    }

    /**
     * Gets the value of the documentList property.
     *
     * @return
     *     possible object is
     *     {@link DocumentList }
     *
     */
    public DocumentList getDocumentList() {
        return documentList;
    }

    /**
     * Sets the value of the documentList property.
     *
     * @param value
     *     allowed object is
     *     {@link DocumentList }
     *
     */
    public void setDocumentList(DocumentList value) {
        this.documentList = value;
    }

    /**
     * Gets the value of the contactList property.
     *
     * @return
     *     possible object is
     *     {@link ContactList }
     *
     */
    public ContactList getContactList() {
        return contactList;
    }

    /**
     * Sets the value of the contactList property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactList }
     *
     */
    public void setContactList(ContactList value) {
        this.contactList = value;
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
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[SensorML Member]").append("\n");
        if (process != null) {
            sb.append("process: ").append(process.getValue()).append('\n');
        }
        if (documentList != null) {
            sb.append("documentList: ").append(documentList).append('\n');
        }
        if (contactList != null) {
            sb.append("contactList: ").append(contactList).append('\n');
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
            boolean proc = false;
            if (this.process != null && that.process != null) {
                proc = Utilities.equals(this.process.getValue(), that.process.getValue());
            } else if (this.process == null && that.process == null) {
                proc = true;
            }

            return Utilities.equals(this.actuate, that.actuate)
                    && Utilities.equals(this.arcrole, that.arcrole)
                    && Utilities.equals(this.contactList, that.contactList)
                    && Utilities.equals(this.documentList, that.documentList)
                    && Utilities.equals(this.href, that.href)
                    && Utilities.equals(this.nilReason, that.nilReason)
                    && proc
                    && Utilities.equals(this.remoteSchema, that.remoteSchema)
                    && Utilities.equals(this.role, that.role)
                    && Utilities.equals(this.show, that.show)
                    && Utilities.equals(this.title, that.title)
                    && Utilities.equals(this.type, that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        Object proc = null;
        if (process != null) {
            proc = process.getValue();
        }
        hash = 71 * hash + (proc != null ? proc.hashCode() : 0);
        hash = 71 * hash + (this.documentList != null ? this.documentList.hashCode() : 0);
        hash = 71 * hash + (this.contactList != null ? this.contactList.hashCode() : 0);
        hash = 71 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 71 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 71 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 71 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 71 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 71 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 71 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 71 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 71 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
