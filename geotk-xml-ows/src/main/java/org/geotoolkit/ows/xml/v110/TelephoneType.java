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
package org.geotoolkit.ows.xml.v110;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractTelephone;


/**
 * Telephone numbers for contacting the responsible individual or organization.
 *
 * <p>Java class for TelephoneType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TelephoneType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Voice" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Facsimile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TelephoneType", propOrder = {
    "voice",
    "facsimile"
})
public class TelephoneType implements AbstractTelephone {

    @XmlElement(name = "Voice")
    private List<String> voice;
    @XmlElement(name = "Facsimile")
    private List<String> facsimile;

    /**
     * Empty constructor used by JAXB.
     */
    TelephoneType(){
    }

    /**
     * Build a new telephone object.
     */
    public TelephoneType(final List<String> voice, final List<String> facsimile){
        this.facsimile = facsimile;
        this.voice     = voice;
    }

     /**
     * Build a single new telephone object.
     */
    public TelephoneType(final String voice, final String facsimile){
        this.facsimile = new ArrayList<>();
        this.facsimile.add(facsimile);
        this.voice     = new ArrayList<>();
        this.voice.add(voice);
    }

    /**
     * Gets the value of the voice property.
     */
    @Override
    public List<String> getVoice() {
        if (voice == null) {
            voice = new ArrayList<>();
        }
        return Collections.unmodifiableList(voice);
    }

    /**
     * Gets the value of the facsimile property.
     *
     */
    @Override
    public List<String> getFacsimile() {
        if (facsimile == null) {
            facsimile = new ArrayList<>();
        }
        return Collections.unmodifiableList(facsimile);
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof TelephoneType) {
            final TelephoneType that = (TelephoneType) object;

            return Objects.equals(this.facsimile, that.facsimile) &&
                   Objects.equals(this.voice,     that.voice);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.voice != null ? this.voice.hashCode() : 0);
        hash = 89 * hash + (this.facsimile != null ? this.facsimile.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        return "class:TelephoneType  voice=" + voice + " facsimile=" + facsimile;
    }


}
