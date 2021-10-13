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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractParameterList;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.v100.DataComponentPropertyType;

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
 *         &lt;element name="parameter" type="{http://www.opengis.net/swe/1.0}DataComponentPropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
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
    "parameter"
})
public class ParameterList implements AbstractParameterList {

    @XmlElement(required = true)
    private List<DataComponentPropertyType> parameter;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public ParameterList() {
    }

    public ParameterList(final List<DataComponentPropertyType> parameter) {
        this.parameter = parameter;
    }

    public ParameterList(final AbstractParameterList paramList) {
        if (paramList != null) {
            this.id = paramList.getId();
            if (paramList.getParameter() != null) {
                this.parameter = new ArrayList<DataComponentPropertyType>();
                for (DataComponentProperty p : paramList.getParameter()) {
                    this.parameter.add(new DataComponentPropertyType(p));
                }
            }
        }
    }

    /**
     * Gets the value of the parameter property.
     */
    public List<DataComponentPropertyType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<DataComponentPropertyType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the parameter property.
     */
    public void setParameter(final List<DataComponentPropertyType> parameter) {
        this.parameter = parameter;
    }
    /**
     * Gets the value of the parameter property.
     */
    public void setParameter(final DataComponentPropertyType parameter) {
        if (this.parameter == null) {
            this.parameter = new ArrayList<DataComponentPropertyType>();
        }
        this.parameter.add(parameter);
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

        if (object instanceof ParameterList) {
            final ParameterList that = (ParameterList) object;

            return Objects.equals(this.parameter, that.parameter) &&
                    Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ParameterList]").append("\n");
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (parameter != null) {
            sb.append("parameters:").append('\n');
            for (DataComponentPropertyType k :parameter) {
                sb.append("parameter: ").append(k).append('\n');
            }
        }
        return sb.toString();
     }
}
