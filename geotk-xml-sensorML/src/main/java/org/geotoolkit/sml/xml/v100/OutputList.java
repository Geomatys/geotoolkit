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
import org.geotoolkit.sml.xml.AbstractOutputList;
import org.geotoolkit.swe.xml.v100.DataRecordType;
import org.geotoolkit.swe.xml.v100.ObservableProperty;
import org.geotoolkit.swe.xml.v100.QuantityType;

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
 *         &lt;element name="output" type="{http://www.opengis.net/sensorML/1.0}IoComponentPropertyType" maxOccurs="unbounded"/>
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
    "output"
})
public class OutputList implements AbstractOutputList {

    @XmlElement(required = true)
    private List<IoComponentPropertyType> output;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public OutputList() {
    }

    public OutputList(final List<IoComponentPropertyType> output) {
        this.output = output;
    }

    public OutputList(final AbstractOutputList outputList) {
        this.output = (List<IoComponentPropertyType>) outputList.getOutput();
        this.id    = outputList.getId();
    }

    /**
     * Gets the value of the output property.
     */
    @Override
    public List<IoComponentPropertyType> getOutput() {
        if (output == null) {
            output = new ArrayList<IoComponentPropertyType>();
        }
        return this.output;
    }

    /**
     * Gets the value of the output property.
     */
    public void setOutput(final List<IoComponentPropertyType> output) {
        this.output = output;
    }

    /**
     * Gets the value of the output property.
     */
    public void setOutput(final IoComponentPropertyType output) {
        if (this.output == null) {
            this.output = new ArrayList<IoComponentPropertyType>();
        }
        this.output.add(output);
    }

    /**
     * Gets the value of the output property.
     */
    public void setOutput(final ObservableProperty output) {
        if (this.output == null) {
            this.output = new ArrayList<IoComponentPropertyType>();
        }
        this.output.add(new IoComponentPropertyType(output));
    }

    /**
     * Gets the value of the output property.
     */
    public void setOutput(final QuantityType output) {
        if (this.output == null) {
            this.output = new ArrayList<IoComponentPropertyType>();
        }
        this.output.add(new IoComponentPropertyType(output));
    }

    /**
     * Gets the value of the output property.
     */
    public void setOutput(final DataRecordType output) {
        if (this.output == null) {
            this.output = new ArrayList<IoComponentPropertyType>();
        }
        this.output.add(new IoComponentPropertyType(output));
    }

    /**
     * Gets the value of the id property.
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    @Override
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

        if (object instanceof OutputList) {
            final OutputList that = (OutputList) object;

            return Objects.equals(this.output, that.output) &&
                    Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.output != null ? this.output.hashCode() : 0);
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[OutputList]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (output != null) {
            sb.append("output:").append('\n');
            for (IoComponentPropertyType k : output) {
                sb.append(k).append('\n');
            }
        }
        return sb.toString();
    }
}

