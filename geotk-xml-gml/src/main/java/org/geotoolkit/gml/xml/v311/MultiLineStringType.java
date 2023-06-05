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
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.MultiLineString;
import org.apache.sis.util.ComparisonMode;


/**
 * A MultiLineString is defined by one or more LineCharSequences, referenced through lineStringMember elements. Deprecated with GML version 3.0. Use MultiCurveType instead.
 *
 * <p>Java class for MultiLineStringType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MultiLineStringType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometricAggregateType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}lineStringMember" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiLineStringType", propOrder = {
    "lineStringMember"
})
@XmlRootElement(name="MultiLineString")
public class MultiLineStringType extends AbstractGeometricAggregateType implements MultiLineString {

    private List<LineStringPropertyType> lineStringMember;

    public MultiLineStringType() {
    }

    public MultiLineStringType(final String srsName, final List<LineStringPropertyType> lineStringMember) {
        super(srsName);
        this.lineStringMember = lineStringMember;
    }

    /**
     * Gets the value of the lineStringMember property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link LineStringPropertyType }
     */
    public List<LineStringPropertyType> getLineStringMember() {
        if (lineStringMember == null) {
            lineStringMember = new ArrayList<LineStringPropertyType>();
        }
        return this.lineStringMember;
    }

    public void setLineStringMember(final List<LineStringPropertyType> lineStringMember) {
        this.lineStringMember = lineStringMember;
    }

    public void setLineStringMember(final LineStringPropertyType lineStringMember) {
        if (lineStringMember != null) {
            if (this.lineStringMember == null) {
                this.lineStringMember = new ArrayList<LineStringPropertyType>();
            }
            this.lineStringMember.add(lineStringMember);
        }
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiLineStringType && super.equals(object, mode)) {
            final MultiLineStringType that = (MultiLineStringType) object;

            return Objects.equals(this.lineStringMember, that.lineStringMember);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.lineStringMember != null ? this.lineStringMember.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (lineStringMember != null) {
            sb.append("lineStringMember:").append('\n');
            for (LineStringPropertyType sp : lineStringMember) {
                sb.append(sp).append('\n');
            }
        }
        return sb.toString();
    }
}
