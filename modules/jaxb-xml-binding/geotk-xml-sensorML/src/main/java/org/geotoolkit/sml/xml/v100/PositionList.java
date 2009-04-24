/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractPositionList;
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
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}position" maxOccurs="unbounded"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}timePosition"/>
 *       &lt;/choice>
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
    "position",
    "timePosition"
})
public class PositionList implements AbstractPositionList {

    private List<Position> position;
    private TimePosition timePosition;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public PositionList() {

    }

    public PositionList(String id , TimePosition timePosition) {
        this.id           = id;
        this.timePosition = timePosition;
    }

    public PositionList(String id , List<Position> position) {
        this.position = position;
        this.id       = id;
    }

    public PositionList(AbstractPositionList pos) {
        this.id = pos.getId();
        this.timePosition = (TimePosition) pos.getTimePosition();
        this.position = (List<Position>) pos.getPosition();
    }

    /**
     * Gets the value of the position property.
     */
    public List<Position> getPosition() {
        if (position == null) {
            position = new ArrayList<Position>();
        }
        return this.position;
    }

    /**
     * Gets the value of the timePosition property.
     *
     * @return
     *     possible object is
     *     {@link TimePosition }
     *
     */
    public TimePosition getTimePosition() {
        return timePosition;
    }

    /**
     * Sets the value of the timePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimePosition }
     *
     */
    public void setTimePosition(TimePosition value) {
        this.timePosition = value;
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

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof PositionList) {
            final PositionList that = (PositionList) object;

            return Utilities.equals(this.position, that.position) &&
                   Utilities.equals(this.timePosition, that.timePosition) &&
                   Utilities.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 47 * hash + (this.timePosition != null ? this.timePosition.hashCode() : 0);
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[OutputList]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (timePosition != null) {
            sb.append("timePosition: ").append(timePosition).append('\n');
        }
        if (position != null) {
            sb.append("positionList:").append('\n');
            for (Position k : position) {
                sb.append("position: ").append(k).append('\n');
            }
        }
        return sb.toString();
    }
}

