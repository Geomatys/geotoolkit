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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractProcessChain;
import org.geotoolkit.sml.xml.AbstractProcessModel;
import org.geotoolkit.sml.xml.Component;
import org.geotoolkit.sml.xml.ComponentArray;
import org.geotoolkit.sml.xml.ComponentProperty;
import org.geotoolkit.sml.xml.System;
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
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "process"
})
public class ComponentPropertyType implements ComponentProperty {

    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractProcessType> process;
    @XmlAttribute // (required = true)
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

    public ComponentPropertyType() {

    }

    public ComponentPropertyType(final ComponentProperty cp) {
        if (cp != null) {
            this.actuate       = cp.getActuate();
            this.arcrole       = cp.getArcrole();
            if (cp.getAbstractProcess() != null) {
                ObjectFactory factory = new ObjectFactory();
                AbstractProcess process = cp.getAbstractProcess();
                if (process instanceof System) {
                    this.process = factory.createSystem(new SystemType((System)process));
                } else if (process instanceof Component) {
                    this.process = factory.createComponent(new ComponentType((Component) process));
                } else if (process instanceof AbstractDataSource) {
                    this.process = factory.createDataSource( new DataSourceType((AbstractDataSource) process));
                } else if (process instanceof AbstractProcessChain) {
                    this.process = factory.createProcessChain(new ProcessChainType((AbstractProcessChain) process));
                } else if (process instanceof AbstractProcessModel) {
                    this.process = factory.createProcessModel(new ProcessModelType((AbstractProcessModel) process));
                } else if (process instanceof ComponentArray) {
                    this.process = factory.createComponentArray(new ComponentArrayType((ComponentArray) process));
                } else {
                    java.lang.System.out.println("Unexpected AbstractProcessType:" + process);
                }
            }
            this.href          = cp.getHref();
            this.remoteSchema  = cp.getRemoteSchema();
            this.role          = cp.getRole();
            this.show          = cp.getShow();
            this.title         = cp.getTitle();
            this.type          = cp.getType();
            this.name          = cp.getName();
        }
    }

    public ComponentPropertyType(final String href) {
        this.href = href;
    }

    public ComponentPropertyType(final String name, final JAXBElement<? extends AbstractProcessType> process) {
        this.name    = name;
        this.process = process;
    }

    public ComponentPropertyType(final ComponentType process) {
        ObjectFactory factory = new ObjectFactory();
        this.process = factory.createComponent(process);
    }

    public ComponentPropertyType(final String name, final String role, final String href) {
        this.name    = name;
        this.href    = href;
        this.role    = role;
    }

    /**
     * Gets the value of the process property.
     */
    public JAXBElement<? extends AbstractProcessType> getProcess() {
        return process;
    }

    public AbstractProcessType getAbstractProcess() {
        if (process != null) {
            return process.getValue();
        }
        return null;
    }
    
    /**
     * Sets the value of the process property.
     */
    public void setJBProcess(final JAXBElement<? extends AbstractProcessType> value) {
        this.process = ((JAXBElement<? extends AbstractProcessType>) value);
    }

    /**
     * Sets the value of the process property.
     */
    public void setProcess(final AbstractProcessType value) {
        ObjectFactory factory = new ObjectFactory();
        if (value instanceof System) {
            this.process = factory.createSystem((SystemType) value);
        } else if (value instanceof Component) {
            this.process = factory.createComponent((ComponentType) value);
        } else if (value instanceof AbstractDataSource) {
            this.process = factory.createDataSource((DataSourceType) value);
        } else if (value instanceof AbstractProcessChain) {
            this.process = factory.createProcessChain((ProcessChainType) value);
        } else if (value instanceof AbstractProcessModel) {
            this.process = factory.createProcessModel((ProcessModelType) value);
        } else if (value instanceof ComponentArray) {
            this.process = factory.createComponentArray((ComponentArrayType) value);
        } else {
            java.lang.System.out.println("Unexpected AbstractProcessType:" + value);
        }
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
     *
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
     *
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
     *
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

        if (object instanceof ComponentPropertyType) {
            final ComponentPropertyType that = (ComponentPropertyType) object;
           boolean absDataArr = false;
            if (this.process != null && that.process != null) {
                absDataArr = Utilities.equals(this.process.getValue(), that.process.getValue());
            } else if (this.process == null && that.process == null) {
                absDataArr = true;
            }
            return Utilities.equals(this.actuate,      that.actuate)       &&
                   Utilities.equals(this.arcrole,      that.arcrole)       &&
                   Utilities.equals(this.href,         that.href)          &&
                   absDataArr                                              &&
                   Utilities.equals(this.remoteSchema, that.remoteSchema)  &&
                   Utilities.equals(this.role,         that.role)          &&
                   Utilities.equals(this.show,         that.show)          &&
                   Utilities.equals(this.title,        that.title)         &&
                   Utilities.equals(this.name,         that.name)          &&
                   Utilities.equals(this.nilReason,    that.nilReason)     &&
                   Utilities.equals(this.type,         that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.process != null ? this.process.hashCode() : 0);
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 19 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 19 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 19 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 19 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 19 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 19 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 19 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 19 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 19 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Components]").append("\n");
        if (process != null) {
            sb.append("process: ").append(process.getValue()).append('\n');
        }
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
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
