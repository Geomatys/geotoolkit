/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.xmlstore;

import java.awt.image.DataBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.sis.coverage.Category;
import org.geotoolkit.storage.coverage.CoverageUtilities;
import org.apache.sis.coverage.SampleDimension;
import org.opengis.coverage.SampleDimensionType;
import org.apache.sis.measure.Units;
import org.apache.sis.util.iso.Names;

/**
 * Permit to marshall / unmarshall {@link SampleDimension}.
 *
 * @author Johann Sorel  (Geomatys)
 * @author Rémi Marechal (Geomatys)
 */
@XmlRootElement(name="SampleDimension")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLSampleDimension {
    /**
     * Name of this {@link XMLSampleDimension}.
     */
    @XmlElement(name="name")
    public String name;

    /**
     * String which define in which unit is exprimate this {@link SampleDimension}.
     */
    @XmlElement(name="unit")
    public String unit;

    /**
     * String which define internal {@link SampleDimension} datatype.
     * @see #getSampleType()
     */
    @XmlElement(name="type")
    public String type;

    /**
     * Define all internal {@link XMLCategory}.
     */
    @XmlElement(name="category")
    public List<XMLCategory> categories;


    /**
     * Set {@link Unit} in relation with this {@link SampleDimension}.
     *
     * @param unit the unit of this {@link SampleDimension}, may be {@code null}.
     */
    public void setUnit(Unit unit) {
        this.unit = (unit == null) ? null : unit.toString();
    }

    /**
     * Returns the {@link Unit} in relation with this {@link SampleDimension}.<br><br>
     * In case where internal {@link XMLSampleDimension#unit} is not defined
     * the default returned value is {@link Units#UNITY}.
     *
     * @return the sample {@link Unit} or {@link Units#UNITY} if it is not defined.
     * @see Unit#valueOf(java.lang.CharSequence)
     * @see Units#UNITY
     */
    public Unit getUnit() {
        if (this.unit == null || unit.isEmpty()) return Units.UNITY;
        return Units.valueOf(unit);
    }

    /**
     * Set type of this sample dimension.
     *
     * @see SampleDimensionType
     */
    public void setSampleType(SampleDimensionType sdt) {
        type = sdt.name();
    }

    /**
     * Returns the data type of this {@link SampleDimension}.
     *
     * @return the data type of this {@link SampleDimension}.
     * @throws IllegalArgumentException if {@link XMLSampleDimension#type} is not known.
     */
    public SampleDimensionType getSampleType() {
        for (SampleDimensionType sdt : SampleDimensionType.values()) {
            if (sdt.name().equals(type)) return sdt;
            //keep for retro-compatibility with old pyramids
            if (sdt.identifier().equals(type)) return sdt;
        }
        throw  new IllegalArgumentException("Unexpected type : "+type);
    }

    /**
     * Returns the equivalent of this internal datatype exprimate by an integer
     * in correlation with {@link DataBuffer} static values.
     *
     * @return Returns the equivalent of this internal datatype exprimate by an integer.
     * @see DataBuffer#TYPE_BYTE
     * @see DataBuffer#TYPE_DOUBLE
     * @see DataBuffer#TYPE_FLOAT
     * @see DataBuffer#TYPE_INT
     * @see DataBuffer#TYPE_SHORT
     * @see DataBuffer#TYPE_USHORT
     */
    public int getDataType() {
        return CoverageUtilities.getDataType(getSampleType());
    }

    /**
     * Returns {@link SampleDimension} from internal marshalled values.
     *
     * @see #categories
     * @see #name
     * @see #type
     * @see #unit
     */
    public SampleDimension buildSampleDimension() {
        List<Category> cats = Collections.emptyList();
        if (categories != null) {
            cats = new ArrayList<>(categories.size());
            for (XMLCategory c : categories) {
                cats.add(c.buildCategory(getDataType()));
            }
        }
        return new SampleDimension(Names.createLocalName(null, null, name), null, cats);
    }

    /**
     * Copy and fill information from given {@link SampleDimension}.
     *
     * @param gsd {@link SampleDimension} reference.
     */
    public void fill(final SampleDimension gsd) {
        if (categories == null) categories = new ArrayList<>();
        categories.clear();
        name = gsd.getName().toString();
        setUnit(gsd.getUnits().orElse(null));
        if (gsd.getCategories() != null) {
            for (Category cat : gsd.getCategories()) {
                final XMLCategory xcat = new XMLCategory();
                xcat.fill(cat);
                categories.add(xcat);
            }
        }
    }
}
