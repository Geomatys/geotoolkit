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
package org.geotoolkit.ogc.xml.v110modified;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.DirectPositionType;
import org.geotoolkit.gml.xml.v311modified.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311modified.EnvelopeWithTimePeriodType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;


/**
 * <p>Java class for BBOXType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBOXType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element name="PropertyName" type="{http://www.opengis.net/ogc}PropertyNameType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *           &lt;element ref="{http://www.opengis.net/gml}EnvelopeWithTimePeriod"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBOXType", propOrder = {
    "propertyName",
    "envelope",
    "envelopeWithTimePeriod"
})
public class BBOXType extends SpatialOpsType implements BBOX {

    @XmlElement(name = "PropertyName")
    private String propertyName;
    @XmlElement(name = "Envelope", namespace = "http://www.opengis.net/gml")
    private EnvelopeEntry envelope;
    @XmlElement(name = "EnvelopeWithTimePeriod", namespace = "http://www.opengis.net/gml")
    private EnvelopeWithTimePeriodType envelopeWithTimePeriod;

    /**
     * An empty constructor used by JAXB
     */
    public BBOXType() {
        
    }
    
    /**
     * build a new BBox with an envelope.
     */
    public BBOXType(String propertyName, double minx, double miny, double maxx, double maxy, String srs) {
        this.propertyName = propertyName;
        DirectPositionType lower = new DirectPositionType(minx, miny);
        DirectPositionType upper = new DirectPositionType(maxx, maxy);
        this.envelope = new EnvelopeEntry(null, lower, upper, srs);
        
    }
    /**
     * Gets the value of the propertyName property.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the value of the envelope property.
     */
    public EnvelopeEntry getEnvelope() {
        return envelope;
    }

    /**
     * Gets the value of the envelopeWithTimePeriod property.
     */
    public EnvelopeWithTimePeriodType getEnvelopeWithTimePeriod() {
        return envelopeWithTimePeriod;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("PropertyName=").append(s).append('\n');
        if (envelope != null) {
            s.append("envelope= ").append(envelope.toString()).append('\n');
        } else {
            s.append("envelope null").append('\n');
        }
        if (envelopeWithTimePeriod != null) {
            s.append("envelope with time period= ").append(envelopeWithTimePeriod.toString()).append('\n');
        } else {
            s.append("envelope with time null").append('\n');
        }
        return s.toString();
    }

    public String getSRS() {
        if (envelope != null) {
            return envelope.getSrsName();
        } else if (envelopeWithTimePeriod != null) {
            return envelopeWithTimePeriod.getSrsName();
        }
        return null;
    }

    public double getMinX() {
        DirectPositionType pos = null;
        if (envelope != null) {
             pos = envelope.getLowerCorner();
        } else if (envelopeWithTimePeriod != null) {
            pos = envelopeWithTimePeriod.getLowerCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(0);
        }
        return -1;
    }

    public double getMinY() {
       DirectPositionType pos = null;
        if (envelope != null) {
             pos = envelope.getLowerCorner();
        } else if (envelopeWithTimePeriod != null) {
            pos = envelopeWithTimePeriod.getLowerCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(1);
        }
        return -1;
    }

    public double getMaxX() {
        DirectPositionType pos = null;
        if (envelope != null) {
            pos = envelope.getUpperCorner();
        } else if (envelopeWithTimePeriod != null) {
            pos = envelopeWithTimePeriod.getUpperCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(0);
        }
        return -1;
    }

    public double getMaxY() {
        DirectPositionType pos = null;
        if (envelope != null) {
            pos = envelope.getUpperCorner();
        } else if (envelopeWithTimePeriod != null) {
            pos = envelopeWithTimePeriod.getUpperCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(1);
        }
        return -1;
    }

    public Expression getExpression1() {
        return new PropertyNameType(propertyName);
    }

    public Expression getExpression2() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
    }
}
