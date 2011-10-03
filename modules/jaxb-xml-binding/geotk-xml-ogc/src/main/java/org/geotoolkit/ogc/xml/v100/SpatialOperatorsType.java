/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ogc.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Spatial_OperatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Spatial_OperatorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.opengis.net/ogc}BBOX"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Equals"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Disjoint"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Intersect"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Touches"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Crosses"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Within"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Contains"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Overlaps"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}Beyond"/>
 *         &lt;element ref="{http://www.opengis.net/ogc}DWithin"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Spatial_OperatorsType", propOrder = {
    "bboxOrEqualsOrDisjoint"
})
public class SpatialOperatorsType {

    @XmlElements({
        @XmlElement(name = "Intersect", type = Intersect.class),
        @XmlElement(name = "Touches", type = Touches.class),
        @XmlElement(name = "DWithin", type = DWithin.class),
        @XmlElement(name = "Beyond", type = Beyond.class),
        @XmlElement(name = "Contains", type = Contains.class),
        @XmlElement(name = "Within", type = Within.class),
        @XmlElement(name = "Disjoint", type = Disjoint.class),
        @XmlElement(name = "Overlaps", type = Overlaps.class),
        @XmlElement(name = "Equals", type = Equals.class),
        @XmlElement(name = "Crosses", type = Crosses.class),
        @XmlElement(name = "BBOX", type = BBOX.class)
    })
    protected List<Object> bboxOrEqualsOrDisjoint;

    /**
     * Gets the value of the bboxOrEqualsOrDisjoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bboxOrEqualsOrDisjoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBBOXOrEqualsOrDisjoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Intersect }
     * {@link Touches }
     * {@link DWithin }
     * {@link Beyond }
     * {@link Contains }
     * {@link Within }
     * {@link Disjoint }
     * {@link Overlaps }
     * {@link Equals }
     * {@link Crosses }
     * {@link BBOX }
     * 
     * 
     */
    public List<Object> getBBOXOrEqualsOrDisjoint() {
        if (bboxOrEqualsOrDisjoint == null) {
            bboxOrEqualsOrDisjoint = new ArrayList<Object>();
        }
        return this.bboxOrEqualsOrDisjoint;
    }

}
