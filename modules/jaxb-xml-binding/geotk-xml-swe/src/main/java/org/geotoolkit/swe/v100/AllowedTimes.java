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

package org.geotoolkit.swe.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;choice>
 *           &lt;element name="min" type="{http://www.opengis.net/swe/1.0}timePositionType"/>
 *           &lt;element name="max" type="{http://www.opengis.net/swe/1.0}timePositionType"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="interval" type="{http://www.opengis.net/swe/1.0}timePair"/>
 *           &lt;element name="valueList" type="{http://www.opengis.net/swe/1.0}timeList"/>
 *         &lt;/choice>
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
    "min",
    "max",
    "intervalOrValueList"
})
@XmlRootElement(name = "AllowedTimes")
public class AllowedTimes {

    @XmlList
    private List<String> min;
    @XmlList
    private List<String> max;
    @XmlElementRefs({
        @XmlElementRef(name = "interval", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class),
        @XmlElementRef(name = "valueList", namespace = "http://www.opengis.net/swe/1.0", type = JAXBElement.class)
    })
    private List<JAXBElement<List<String>>> intervalOrValueList;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * Gets the value of the min property.
     */
    public List<String> getMin() {
        if (min == null) {
            min = new ArrayList<String>();
        }
        return this.min;
    }

    /**
     * Gets the value of the max property.
     */
    public List<String> getMax() {
        if (max == null) {
            max = new ArrayList<String>();
        }
        return this.max;
    }

    /**
     * Gets the value of the intervalOrValueList property.
     * 
     */
    public List<JAXBElement<List<String>>> getIntervalOrValueList() {
        if (intervalOrValueList == null) {
            intervalOrValueList = new ArrayList<JAXBElement<List<String>>>();
        }
        return this.intervalOrValueList;
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
    public void setId(String value) {
        this.id = value;
    }

}
