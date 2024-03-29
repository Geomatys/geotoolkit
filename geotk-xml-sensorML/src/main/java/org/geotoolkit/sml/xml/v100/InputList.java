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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractInputList;
import org.geotoolkit.swe.xml.v100.ObservableProperty;

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
 *         &lt;element name="input" type="{http://www.opengis.net/sensorML/1.0}IoComponentPropertyType" maxOccurs="unbounded"/>
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
    "input"
})
public class InputList implements AbstractInputList {

    @XmlElement(required = true)
    private List<IoComponentPropertyType> input;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public InputList() {
    }

    public InputList(final List<IoComponentPropertyType> input) {
        this.input = input;
    }

    public InputList(final AbstractInputList inputList) {
        this.input = (List<IoComponentPropertyType>) inputList.getInput();
        this.id    = inputList.getId();
    }

    /**
     * Gets the value of the input property.
     */
    public List<IoComponentPropertyType> getInput() {
        if (input == null) {
            input = new ArrayList<IoComponentPropertyType>();
        }
        return this.input;
    }

    /**
     * Gets the value of the input property.
     */
    public void setInput(final List<IoComponentPropertyType> input) {
        this.input = input;
    }

    /**
     * Gets the value of the input property.
     */
    public void setInput(final IoComponentPropertyType input) {
        if (this.input == null) {
            this.input = new ArrayList<IoComponentPropertyType>();
        }
        this.input.add(input);
    }

    /**
     * Gets the value of the input property.
     */
    public void setInput(final ObservableProperty input) {
        if (this.input == null) {
            this.input = new ArrayList<IoComponentPropertyType>();
        }
        this.input.add(new IoComponentPropertyType(input));
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

        if (object instanceof InputList) {
            final InputList that = (InputList) object;

            return Objects.equals(this.input, that.input) &&
                    Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.input != null ? this.input.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[InputList]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (input != null) {
            sb.append("intput:").append('\n');
            for (IoComponentPropertyType k : input) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }
}
