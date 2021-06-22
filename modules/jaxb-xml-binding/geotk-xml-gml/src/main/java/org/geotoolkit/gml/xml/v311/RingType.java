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
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.Ring;
import org.apache.sis.util.ComparisonMode;


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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RingType", propOrder = {
    "curveMember"
})
@XmlRootElement(name = "Ring")
public class RingType extends AbstractRingType implements Ring{

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
    @Override
    public List<CurvePropertyType> getCurveMember() {
        if (curveMember == null) {
            curveMember = new ArrayList<>();
        }
        return this.curveMember;
    }

    public void setCurveMember(final List<CurvePropertyType> curveMember) {
        this.curveMember = curveMember;
    }

    public void setCurveMember(final CurvePropertyType curveMember) {
        if (this.curveMember == null) {
            this.curveMember = new ArrayList<>();
        }
        this.curveMember.add(curveMember);
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

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof RingType && super.equals(object, mode)) {
            final RingType that = (RingType) object;

            return Objects.equals(this.curveMember, that.curveMember);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.curveMember != null ? this.curveMember.hashCode() : 0);
        return hash;
    }
}
