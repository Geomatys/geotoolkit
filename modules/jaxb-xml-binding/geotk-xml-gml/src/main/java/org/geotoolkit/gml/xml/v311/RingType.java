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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.filter.expression.ExpressionVisitor;


/**
 * A Ring is used to represent a single connected component of a surface boundary. It consists of a sequence of curves connected in a cycle (an object whose boundary is empty).
 * A Ring is structurally similar to a composite curve in that the endPoint of each curve in the sequence is the startPoint of the next curve in the Sequence. Since the sequence is circular, there is no exception to this rule. Each ring, like all boundaries, is a cycle and each ring is simple.
 * NOTE: Even though each Ring is simple, the boundary need not be simple. The easiest case of this is where one of the interior rings of a surface is tangent to its exterior ring.
 * 
 * <p>Java class for RingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractRingType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}curveMember" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RingType", propOrder = {
    "curveMember"
})
public class RingType extends AbstractRingType {

    @XmlElement(required = true)
    private List<CurvePropertyType> curveMember;

    /**
     * This element references or contains one curve in the composite curve.
     * The curves are contiguous, the collection of curves is ordered.
     * NOTE: This definition allows for a nested structure, i.e. a CompositeCurve may use,
     * for example, another CompositeCurve as a curve member.
     *
     * Gets the value of the curveMember property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link CurvePropertyType }
     * 
     * 
     */
    public List<CurvePropertyType> getCurveMember() {
        if (curveMember == null) {
            curveMember = new ArrayList<CurvePropertyType>();
        }
        return this.curveMember;
    }

    public void setCurveMember(List<CurvePropertyType> curveMember) {
        this.curveMember = curveMember;
    }

    public void setCurveMember(CurvePropertyType curveMember) {
        if (this.curveMember == null) {
            this.curveMember = new ArrayList<CurvePropertyType>();
        }
        this.curveMember.add(curveMember);
    }

    @Override
    public Object evaluate(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T evaluate(Object object, Class<T> context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[RingType]\n");
        if (curveMember != null) {
            sb.append("curveMember:").append('\n');
            for (CurvePropertyType cu : curveMember) {
                sb.append(cu).append('\n');
            }
        }
        return sb.toString();
    }
}
