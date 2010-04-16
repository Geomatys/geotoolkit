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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractDataSource;
import org.geotoolkit.sml.xml.AbstractProcess;
import org.geotoolkit.sml.xml.AbstractProcessChain;
import org.geotoolkit.sml.xml.AbstractProcessModel;
import org.geotoolkit.sml.xml.Component;
import org.geotoolkit.sml.xml.System;
import org.geotoolkit.sml.xml.AbstractTimePosition;
import org.geotoolkit.sml.xml.ComponentArray;
import org.geotoolkit.swe.xml.v100.TimeType;


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
 *         &lt;element ref="{http://www.opengis.net/swe/1.0}Time"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *       &lt;/choice>
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
    "time",
    "process"
})
@XmlRootElement(name = "timePosition")
public class TimePosition implements AbstractTimePosition {

    @XmlElement(name = "Time", namespace = "http://www.opengis.net/swe/1.0")
    private TimeType time;
    @XmlElementRef(name = "AbstractProcess", namespace = "http://www.opengis.net/sensorML/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractProcessType> process;
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

    public TimePosition() {

    }

    public TimePosition(AbstractTimePosition tp) {
        if (tp != null) {
            this.actuate      = tp.getActuate();
            this.arcrole      = tp.getArcrole();
            this.href         = tp.getHref();
            this.remoteSchema = tp.getRemoteSchema();
            this.role         = tp.getRole();
            this.show         = tp.getShow();
            this.title        = tp.getTitle();
            this.type         = tp.getType();
            if (tp.getTime() != null) {
                this.time         = new TimeType(tp.getTime());
            }

            if (tp.getProcess() != null) {
                ObjectFactory facto = new ObjectFactory();
                AbstractProcess aProcess = tp.getProcess();
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
        }
    }
    
    /**
     * Gets the value of the time property.
     */
    public TimeType getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     */
    public void setTime(TimeType value) {
        this.time = value;
    }

    /**
     * Gets the value of the process property.
     */
    public JAXBElement<? extends AbstractProcessType> getRealProcess() {
        return process;
    }

    public AbstractProcessType getProcess() {
        if (process != null) {
            return process.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the process property.
     */
    public void setProcess(JAXBElement<? extends AbstractProcessType> value) {
        this.process = ((JAXBElement<? extends AbstractProcessType> ) value);
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
    public void setName(String value) {
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
    public void setRemoteSchema(String value) {
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
    public void setActuate(String value) {
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
    public void setArcrole(String value) {
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
    public void setHref(String value) {
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
    public void setRole(String value) {
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
    public void setShow(String value) {
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
    public void setTitle(String value) {
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
    public void setType(String value) {
        this.type = value;
    }

}
