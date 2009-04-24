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
package org.geotoolkit.gml.xml.v311modified;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import org.geotoolkit.util.Utilities;


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
 *     &lt;extension base="&lt;http://www.opengis.net/gml>TimePositionUnion">
 *       &lt;attribute name="calendarEraName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="frame" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="#ISO-8601" />
 *       &lt;attribute name="indeterminatePosition" type="{http://www.opengis.net/gml}TimeIndeterminateValueType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimePositionType", propOrder = {
    "value"
})
public class TimePositionType {

    @XmlValue
    private String value;
    @XmlAttribute
    private String calendarEraName;
    @XmlAttribute
    private String frame;
    @XmlAttribute
    private TimeIndeterminateValueType indeterminatePosition;

    /**
     * empty constructor used by JAXB.
     */
    TimePositionType(){
        
    }
    
    /**
     * build a simple Timposition with only a value.
     * 
     * @param value a date.
     */
    public TimePositionType(String value){
        this.value = value;
    }
    
    /**
     * build a simple Timposition with an indeterminate value.
     * 
     */
    public TimePositionType(TimeIndeterminateValueType indeterminatePosition){
        this.indeterminatePosition = indeterminatePosition;
        value = "";
    }
    
    /**
     * build a simple Timposition with only a value from a timestamp.
     * 
     * @param value a date.
     */
    public TimePositionType(Timestamp time){
        this.value = time.toString();
    }
    
    /**
     * The simple type gml:TimePositionUnion is a union of XML Schema simple types 
     * which instantiate the subtypes for temporal position described in ISO 19108.
     * An ordinal era may be referenced via URI.  
     * A decimal value may be used to indicate the distance from the scale origin .  
     * time is used for a position that recurs daily (see ISO 19108:2002 5.4.4.2).
     * Finally, calendar and clock forms that support the representation of time in systems based on years,
     * months, days, hours, minutes and seconds, in a notation following ISO 8601, 
     * are assembled by gml:CalDate Gets the value of the value property.
     * 
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the value of the calendarEraName property.
     */
    public String getCalendarEraName() {
        return calendarEraName;
    }

    /**
     * Gets the value of the frame property.
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
     * Gets the value of the indeterminatePosition property.
     */
    public TimeIndeterminateValueType getIndeterminatePosition() {
        return indeterminatePosition;
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
        StringBuilder s = new StringBuilder("[TimePositionType] ");
        if (calendarEraName != null) {
            s.append("calendarEraName:").append(calendarEraName).append('\n');
        }
        if (frame != null) {
            s.append("frame:").append(frame).append('\n');
        }
        if (indeterminatePosition != null) {
            s.append("indeterminatePosition:").append(indeterminatePosition.value()).append('\n');
        }
        s.append("value = " + value);
               
        return s.toString();
    }

}
