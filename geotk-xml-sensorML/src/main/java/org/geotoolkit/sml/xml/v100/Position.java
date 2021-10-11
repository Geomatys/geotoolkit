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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.sml.xml.AbstractPosition;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractProcessChain;
import org.geotoolkit.sml.xml.AbstractProcessModel;
import org.geotoolkit.sml.xml.Component;
import org.geotoolkit.sml.xml.System;
import org.geotoolkit.sml.xml.ComponentArray;
import org.geotoolkit.swe.xml.v100.PositionType;
import org.geotoolkit.swe.xml.v100.VectorType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Position"/>
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Vector"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
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
    "process",
    "position",
    "vector"
})
@XmlRootElement(name = "Position")
public class Position implements AbstractPosition {

    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractProcessType> process;
    @XmlElement(name = "Position", namespace = "http://www.opengis.net/swe/1.0")
    private PositionType position;
    @XmlElement(name = "Vector", namespace = "http://www.opengis.net/swe/1.0")
    private VectorType vector;
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

    public Position() {

    }

    public Position(final String name, final String href) {
        this.href = href;
        this.name = name;
    }

    public Position(final String name, final PositionType position) {
        this.name = name;
        this.position = position;
    }

    public Position(final AbstractPosition pos) {
        if (pos != null) {
            this.actuate = pos.getActuate();
            this.arcrole = pos.getArcrole();
            this.href    = pos.getHref();
            this.name    = pos.getName();
            this.remoteSchema = pos.getRemoteSchema();
            this.role    = pos.getRole();
            this.show    = pos.getShow();
            this.title   = pos.getTitle();
            this.type    = pos.getType();
            if (pos.getAbstractProcess() != null) {
                ObjectFactory facto = new ObjectFactory();
                AbstractProcess aProcess = pos.getAbstractProcess();
                if (aProcess instanceof AbstractDataSource) {
                    this.process = facto.createDataSource(new DataSourceType( (AbstractDataSource) aProcess));
                } else if (aProcess instanceof AbstractProcessModel) {
                    this.process = facto.createProcessModel(new ProcessModelType( (AbstractProcessModel) aProcess));
                } else if (aProcess instanceof AbstractProcessChain) {
                    this.process = facto.createProcessChain(new ProcessChainType( (AbstractProcessChain) aProcess));
                } else if (aProcess instanceof System) {
                    this.process = facto.createSystem(new SystemType((System)aProcess));
                } else if (aProcess instanceof Component) {
                    this.process = facto.createComponent(new ComponentType((Component) aProcess));
                } else if (aProcess instanceof ComponentArray) {
                    this.process = facto.createComponentArray(new ComponentArrayType((ComponentArray) aProcess));
                } else {
                    throw new IllegalArgumentException("unexepected process type:" + aProcess);
                }
            }
            if (pos.getPosition() != null) {
                this.position = new PositionType(pos.getPosition());
            }
            if (pos.getVector() != null) {
                this.vector = new VectorType(pos.getVector());
            }
        }
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
    public void setProcess(final JAXBElement<? extends AbstractProcessType> value) {
        this.process = ((JAXBElement<? extends AbstractProcessType> ) value);
    }

    /**
     * Gets the value of the position property.
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     */
    public void setPosition(final PositionType value) {
        this.position = value;
    }

    /**
     * Gets the value of the vector property.
      */
    public VectorType getVector() {
        return vector;
    }

    /**
     * Sets the value of the vector property.
     */
    public void setVector(final VectorType value) {
        this.vector = value;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Position]").append("\n");
        if (name != null) {
            sb.append("name: ").append(name).append('\n');
        }
        if (position != null) {
            sb.append("position: ").append(position).append('\n');
        }

        if (process != null) {
            sb.append("process: ").append(process.getValue()).append('\n');
        }

        if (vector != null) {
            sb.append("vector: ").append(vector).append('\n');
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

        if (object instanceof Position) {
            final Position that = (Position) object;

            boolean proc = false;
            if (this.process != null && that.process != null) {
                proc = Objects.equals(this.process.getValue(), that.process.getValue());
            } else if (this.process == null && that.process == null) {
                proc = true;
            }

            return Objects.equals(this.actuate, that.actuate)           &&
                   Objects.equals(this.href, that.href)                 &&
                   Objects.equals(this.name, that.name)                 &&
                   Objects.equals(this.position, that.position)         &&
                   Objects.equals(this.vector, that.vector)             &&
                   Objects.equals(this.nilReason, that.nilReason)       &&
                   Objects.equals(this.remoteSchema, that.remoteSchema) &&
                   Objects.equals(this.role, that.role)                 &&
                   Objects.equals(this.show, that.show)                 &&
                   Objects.equals(this.title, that.title)               &&
                   Objects.equals(this.type, that.type)                 &&
                   proc                                                   &&
                   Objects.equals(this.arcrole, that.arcrole);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.process != null ? this.process.hashCode() : 0);
        hash = 59 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 59 * hash + (this.vector != null ? this.vector.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 59 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 59 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 59 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 59 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 59 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 59 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

}
