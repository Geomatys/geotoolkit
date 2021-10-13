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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractComponentList;
import org.geotoolkit.sml.xml.ComponentProperty;

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
 *         &lt;element name="component" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element ref="{http://www.opengis.net/sensorML/1.0}_Process"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *               &lt;/restriction>
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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "component"
})
public class ComponentList implements AbstractComponentList {

    @XmlElement(required = true)
    private List<ComponentPropertyType> component;

    public ComponentList() {

    }

    public ComponentList(final AbstractComponentList component) {
        if (component != null) {
            this.component = new ArrayList<>();
            for (ComponentProperty cp :component.getComponent()) {
                this.component.add(new ComponentPropertyType(cp));
            }
        }
    }

    public ComponentList(final List<ComponentPropertyType> component) {
        this.component = component;
    }

    /**
     * Gets the value of the component property.
     *
     */
    public List<ComponentPropertyType> getComponent() {
        if (component == null) {
            component = new ArrayList<>();
        }
        return this.component;
    }

    /**
     * Sets the value of the component property.
     *
     */
    public void setComponent(final List<ComponentPropertyType> component) {
        this.component = component;
    }

    /**
     * Sets the value of the component property.
     *
     * @param component
     */
    public void setComponent(final ComponentPropertyType component) {
        if (component != null) {
            if (this.component == null) {
                this.component = new ArrayList<>();
            }
            this.component.add(component);
        }
    }

    /**
     * Gets the value of the component property.
     *
     * @param component
     */
    public void setComponent(final ComponentType component) {
        if (component != null) {
            if (this.component == null) {
                this.component = new ArrayList<>();
            }
            this.component.add(new ComponentPropertyType(component));
        }
    }

    @Override
    public void removeComponent(final String href) {
        for (ComponentPropertyType compo : component) {
            if (href.equals(compo.getHref())) {
                component.remove(compo);
                return;
            }
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComponentList) {
            final ComponentList that = (ComponentList) object;
            return Objects.equals(this.component, that.component);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.component != null ? this.component.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ComponentList]").append("\n");
        if (component != null) {
            sb.append("component:").append('\n');
            int i = 0;
            for (ComponentPropertyType k : component) {
                sb.append(i).append(":").append(k).append('\n');
                i++;
            }
        }
        return sb.toString();
     }

}

