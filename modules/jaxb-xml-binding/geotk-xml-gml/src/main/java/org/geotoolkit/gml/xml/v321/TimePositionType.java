/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.io.Serializable;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.gml.xml.AbstractTimePosition;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.Utilities;
import org.opengis.util.InternationalString;


/**
 * The method for identifying a temporal position is specific to each temporal reference system.  gml:TimePositionType supports the description of temporal position according to the subtypes described in ISO 19108.
 * Values based on calendars and clocks use lexical formats that are based on ISO 8601, as described in XML Schema Part 2:2001. A decimal value may be used with coordinate systems such as GPS time or UNIX time. A URI may be used to provide a reference to some era in an ordinal reference system .
 * In common with many of the components modelled as data types in the ISO 19100 series of International Standards, the corresponding GML component has simple content. However, the content model gml:TimePositionType is defined in several steps.
 * Three XML attributes appear on gml:TimePositionType:
 * A time value shall be associated with a temporal reference system through the frame attribute that provides a URI reference that identifies a description of the reference system. Following ISO 19108, the Gregorian calendar with UTC is the default reference system, but others may also be used. Components for describing temporal reference systems are described in 14.4, but it is not required that the reference system be described in this, as the reference may refer to anything that may be indentified with a URI.
 * For time values using a calendar containing more than one era, the (optional) calendarEraName attribute provides the name of the calendar era.
 * Inexact temporal positions may be expressed using the optional indeterminatePosition attribute.  This takes a value from an enumeration.
 *
 * <p>Java class for TimePositionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TimePositionType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.opengis.net/gml/3.2>TimePositionUnion">
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *       &lt;attribute name="calendarEraName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="indeterminatePosition" type="{http://www.opengis.net/gml/3.2}TimeIndeterminateValueType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimePositionType", propOrder = {
    "value"
})
public class TimePositionType extends AbstractTimePosition implements Serializable{

    @XmlValue
    private List<String> value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String frame;
    @XmlAttribute
    private String calendarEraName;
    @XmlAttribute
    private TimeIndeterminateValueType indeterminatePosition;

    /**
     * The simple type gml:TimePositionUnion is a union of XML Schema simple types which instantiate the subtypes for temporal position described in ISO 19108.
     *  An ordinal era may be referenced via URI.  A decimal value may be used to indicate the distance from the scale origin .  time is used for a position that recurs daily (see ISO 19108:2002 5.4.4.2).
     *  Finally, calendar and clock forms that support the representation of time in systems based on years, months, days, hours, minutes and seconds, in a notation following ISO 8601, are assembled by gml:CalDate Gets the value of the value property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getValue() {
        if (value == null) {
            value = new ArrayList<String>();
        }
        return this.value;
    }

    /**
     * Gets the value of the frame property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFrame() {
        if (frame == null) {
            return "#ISO-8601";
        } else {
            return frame;
        }
    }

    /**
     * Sets the value of the frame property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFrame(String value) {
        this.frame = value;
    }

    /**
     * Gets the value of the calendarEraName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCalendarEraName() {
        return calendarEraName;
    }

    /**
     * Sets the value of the calendarEraName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCalendarEraName(String value) {
        this.calendarEraName = value;
    }

    /**
     * Gets the value of the indeterminatePosition property.
     *
     * @return
     *     possible object is
     *     {@link TimeIndeterminateValueType }
     *
     */
    public TimeIndeterminateValueType getIndeterminatePosition() {
        return indeterminatePosition;
    }

    /**
     * Sets the value of the indeterminatePosition property.
     *
     * @param value
     *     allowed object is
     *     {@link TimeIndeterminateValueType }
     *
     */
    public void setIndeterminatePosition(TimeIndeterminateValueType value) {
        this.indeterminatePosition = value;
    }

    public Date getDate() {
        if (value != null && !value.isEmpty()) {
            try {
                synchronized (FORMATTER) {
                    return FORMATTER.parse(value.get(0));
                }
            } catch (ParseException ex) {
                LOGGER.log(Level.WARNING, "Parse exception while parsing date value:" + value, ex);
            }
        }
        return null;
    }

    public Time getTime() {
        if (value != null && !value.isEmpty()) {
            return Time.valueOf(value.get(0));
        }
        return null;
    }

    public InternationalString getDateTime() {
        if (value != null && !value.isEmpty()) {
            return new SimpleInternationalString(value.get(0));
        }
        return null;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TimePositionType) {
            final TimePositionType that = (TimePositionType) object;
            return Utilities.equals(this.calendarEraName,       that.calendarEraName)       &&
                   Utilities.equals(this.frame,                 that.frame)                 &&
                   Utilities.equals(this.indeterminatePosition, that.indeterminatePosition) &&
                   Utilities.equals(this.value,                 that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + (this.calendarEraName != null ? this.calendarEraName.hashCode() : 0);
        hash = 97 * hash + (this.frame != null ? this.frame.hashCode() : 0);
        hash = 97 * hash + (this.indeterminatePosition != null ? this.indeterminatePosition.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        if (calendarEraName != null) {
            s.append("calendarEraName:").append(calendarEraName).append('\n');
        }
        if (frame != null) {
            s.append("frame:").append(frame).append('\n');
        }
        if (indeterminatePosition != null) {
            s.append("indeterminatePosition:").append(indeterminatePosition.value()).append('\n');
        }

        if (value != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("d MMMMM yyyy HH:mm:ss z");
            for (String v : value) {
                try {
                    final Date date;
                    synchronized (FORMATTER) {
                        date = FORMATTER.parse(v);
                    }
                    s.append(sdf.format(date));
                } catch (ParseException ex) {
                   LOGGER.log(Level.WARNING, null, ex);
                }
            }
        }
        return s.toString();
    }
}
