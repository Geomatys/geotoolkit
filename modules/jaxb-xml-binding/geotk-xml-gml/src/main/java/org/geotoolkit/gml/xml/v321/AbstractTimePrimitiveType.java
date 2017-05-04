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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalPrimitive;


/**
 * <p>Java class for AbstractTimePrimitiveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractTimePrimitiveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractTimeObjectType">
 *       &lt;sequence>
 *         &lt;element name="relatedTime" type="{http://www.opengis.net/gml/3.2}RelatedTimeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTimePrimitiveType", propOrder = {
    "relatedTime"
})
@XmlSeeAlso({
    AbstractTimeGeometricPrimitiveType.class,
    AbstractTimeTopologyPrimitiveType.class
})
public abstract class AbstractTimePrimitiveType extends AbstractTimeObjectType implements TemporalPrimitive {

    private List<RelatedTimeType> relatedTime;

    public AbstractTimePrimitiveType() {

    }

    public AbstractTimePrimitiveType(final String id) {
        super(id);
    }

    public AbstractTimePrimitiveType(final AbstractTimePrimitiveType that) {
        super(that);
        if (that != null && that.relatedTime != null) {
            this.relatedTime = new ArrayList<RelatedTimeType>();
            for (RelatedTimeType r : that.relatedTime) {
                this.relatedTime.add(new RelatedTimeType(r));
            }
        }
    }

    /**
     * Gets the value of the relatedTime property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link RelatedTimeType }
     *
     *
     */
    public List<RelatedTimeType> getRelatedTime() {
        if (relatedTime == null) {
            relatedTime = new ArrayList<RelatedTimeType>();
        }
        return this.relatedTime;
    }

    @Override
    public RelativePosition relativePosition(final TemporalPrimitive tp) {
        return null;
    }
}
