/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.catalog.util;

import java.util.Objects;
import org.geotoolkit.resources.jaxb.feature.catalog.UnlimitedIntegerAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.internal.jaxb.gco.GO_Integer;
import org.opengis.feature.catalog.util.MultiplicityRange;


/**
 * A component of a multiplicity, consisting of an non-negative lower bound, and a potentially infinite upper bound.
 *
 * <p>Java class for MultiplicityRange_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MultiplicityRange_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="lower" type="{http://www.isotc211.org/2005/gco}Integer_PropertyType"/>
 *         &lt;element name="upper" type="{http://www.isotc211.org/2005/gco}UnlimitedInteger_PropertyType"/>
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
@XmlType(name = "MultiplicityRange_Type", propOrder = {
    "lower",
    "upper"
})
public class MultiplicityRangeImpl implements MultiplicityRange {

    @XmlJavaTypeAdapter(GO_Integer.class)
    @XmlElement(required = true)
    private Integer lower;

    @XmlJavaTypeAdapter(UnlimitedIntegerAdapter.class)
    @XmlElement(required = true)
    private Integer upper;

    /**
     * An empty constructor used by JAXB
     */
    public MultiplicityRangeImpl() {

    }

    public MultiplicityRangeImpl(final MultiplicityRange range) {
        if (range != null) {
            this.lower = range.getLower();
            this.upper = range.getUpper();
        }
    }
    
    /**
     * An empty constructor used by JAXB
     */
    public MultiplicityRangeImpl(final int lower, final Integer upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Gets the value of the lower property.
     */
    @Override
    public Integer getLower() {
        return lower;
    }

    /**
     * Sets the value of the lower property.
     *
    */
    public void setLower(final Integer value) {
        this.lower = value;
    }

    /**
     * Gets the value of the upper property.
     */
    @Override
    public Integer getUpper() {
        return upper;
    }

    /**
     * Sets the value of the upper property.
     *
     */
    public void setUpper(final Integer value) {
        this.upper = value;
    }

    @Override
    public String toString() {
        return "lower=" + lower + " upper=" + upper;
    }
    
    public String toString(final String margin) {
        return margin + "[MultiplicityRange]: lower=" + lower + " upper=" + upper;
    }


    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiplicityRangeImpl) {
            final MultiplicityRangeImpl that = (MultiplicityRangeImpl) object;
            return Objects.equals(this.lower, that.lower);
                //&& Objects.equals(this.upper, that.upper);
            // temporary patch TODO fix it  && Objects.equals(this.upper, that.upper);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.lower != null ? this.lower.hashCode() : 0);
        hash = 97 * hash + (this.upper != null ? this.upper.hashCode() : 0);
        return hash;
    }

}
