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
package org.geotoolkit.sml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.sml.xml.AbstractPhone;
import org.geotoolkit.util.Utilities;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="voice" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="facsimile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "voice",
    "facsimile"
})
public class PhoneType implements AbstractPhone {

    private List<String> voice;
    private List<String> facsimile;

    public PhoneType() {
    }

    public PhoneType(List<String> voice, List<String> facsimile) {
        this.facsimile = facsimile;
        this.voice = voice;
    }

    public PhoneType(String voice, String facsimile) {
        this.facsimile = new ArrayList<String>();
        this.voice = new ArrayList<String>();
        if (facsimile != null) {
            this.facsimile.add(facsimile);
        }
        if (voice != null) {
            this.voice.add(voice);
        }

    }

    /**
     * Gets the value of the voice property.
     */
    public List<String> getVoice() {
        if (voice == null) {
            voice = new ArrayList<String>();
        }
        return this.voice;
    }

    /**
     * Gets the value of the facsimile property.
     *
     */
    public List<String> getFacsimile() {
        if (facsimile == null) {
            facsimile = new ArrayList<String>();
        }
        return this.facsimile;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Phone]").append("\n");
        if (voice != null) {
            for (String d : voice) {
                sb.append("voice: ").append(d).append('\n');
            }
        }
        if (facsimile != null) {
            for (String d : facsimile) {
                sb.append("facsimile: ").append(d).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof PhoneType) {
            final PhoneType that = (PhoneType) object;
            return Utilities.equals(this.facsimile, that.facsimile) &&
                    Utilities.equals(this.voice, that.voice);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.voice != null ? this.voice.hashCode() : 0);
        hash = 37 * hash + (this.facsimile != null ? this.facsimile.hashCode() : 0);
        return hash;
    }
}
