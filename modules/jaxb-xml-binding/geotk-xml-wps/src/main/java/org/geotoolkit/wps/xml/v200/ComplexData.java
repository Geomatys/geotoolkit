/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.w3c.dom.Element;


/**
 * <p>Java class for ComplexData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComplexData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}DataDescription">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "ComplexDataType", propOrder = {
    "legacyMaxMb",
    "legacyDefault",
    "legacySupported",
    "any"
})
@XmlRootElement(name = "ComplexData")
public class ComplexData extends DataDescription {

    /**
     * This is an extension point for description of new data types in WPS. IT
     * IS NOT DESTINED TO HANDLE VALUES !
     */
    @XmlMixed
    @XmlElementRefs({
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = AbstractGeometryType.class),
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml", type = org.geotoolkit.gml.xml.v311.AbstractGeometryType.class),
        @XmlElementRef(name = "math", namespace = "http://www.w3.org/1998/Math/MathML", type = org.geotoolkit.mathml.xml.Math.class)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public ComplexData() {}

    public ComplexData(List<Format> format) {
        super(format);
    }

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     *
     */
    public List<Object> getContent() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (any != null) {
            sb.append("any:\n");
            for (Object out : any) {
                sb.append(out).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ComplexData && super.equals(object)) {
            final ComplexData that = (ComplexData) object;
            return Objects.equals(this.any, that.any);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.any);
        return hash;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private Integer legacyMaxMb;

    @XmlAttribute(name = "mimeType")
    protected String mimeType;
    @XmlAttribute(name = "encoding")
    @XmlSchemaType(name = "anyURI")
    protected String encoding;
    @XmlAttribute(name = "schema")
    @XmlSchemaType(name = "anyURI")
    protected String schema;

    @Deprecated
    public ComplexData(List<Format> format, Integer legacyMaxMb) {
        super(format);
        this.legacyMaxMb = legacyMaxMb;
    }

    @XmlAttribute(name="maximumMegabytes")
    private Integer getLegacyMaxMb() {
        if (FilterByVersion.isV2())
            return null;

        if (legacyMaxMb == null) {
            final OptionalInt min = getFormat().stream()
                    .map(Format::getMaximumMegabytes)
                    .filter(Objects::nonNull)
                    .mapToInt(i -> i)
                    .min();
            if (min.isPresent())
                legacyMaxMb = min.getAsInt();
        }

        return legacyMaxMb;
    }

    private void setLegacyMaxMb(Integer maxMb) {
        legacyMaxMb = maxMb;
    }

    @XmlElement(name="Default")
    private Default getLegacyDefault() {
        if (!FilterByVersion.isV1())
            return null;
        for (final Format f : getFormat()) {
            if (f.isDefault())
                return new Default(f);
        }

        return null;
    }

    private void setLegacyDefault(Default d) {
        if (d != null && d.f != null) {
            checkLegacyLimit(d.f);
            getFormat().add(d.f);
        }
    }

    private void checkLegacyLimit(final Format f) {
        if (legacyMaxMb != null) {
            Integer fMax = f.getMaximumMegabytes();
            if (fMax == null || fMax > legacyMaxMb) {
                f.setMaximumMegabytes(legacyMaxMb);
            }
        }
    }

    @XmlElement(name="Supported")
    private Supported getLegacySupported() {
        if (!FilterByVersion.isV1() || getFormat().isEmpty())
            return null;
        final Supported s = new Supported();
        s.getFormats().addAll(getFormat());
        return s;
    }

    private void setLegacySupported(Supported legacy) {
        List<Format> f = legacy.getFormats();
        f.forEach(this::checkLegacyLimit);
        getFormat().addAll(f);
    }

    private static class Default {

        Format f;

        Default() {}

        Default(final Format f) {
            this.f = f;
        }

        @XmlElementRef
        Format getFormat() {
            return f;
        }

        void setFormat(final Format f) {
            f.setDefault(Boolean.TRUE);
            this.f = f;
        }
    }

    private static class Supported {

        List<Format> formats;

        @XmlElementRef
        List<Format> getFormats() {
            if (formats == null) {
                formats = new ArrayList<>();
            }

            return formats;
        }
    }
}
