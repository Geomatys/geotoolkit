/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.metalinker;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour audioType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="audioType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bitrate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codec" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resolution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="artist" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="album" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "audioType", propOrder = {
    "bitrate",
    "codec",
    "duration",
    "resolution",
    "artist",
    "album"
})
public class AudioType {

    protected String bitrate;
    protected String codec;
    protected String duration;
    protected String resolution;
    protected String artist;
    protected String album;

    /**
     * Obtient la valeur de la propriété bitrate.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBitrate() {
        return bitrate;
    }

    /**
     * Définit la valeur de la propriété bitrate.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBitrate(String value) {
        this.bitrate = value;
    }

    /**
     * Obtient la valeur de la propriété codec.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodec() {
        return codec;
    }

    /**
     * Définit la valeur de la propriété codec.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodec(String value) {
        this.codec = value;
    }

    /**
     * Obtient la valeur de la propriété duration.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Définit la valeur de la propriété duration.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDuration(String value) {
        this.duration = value;
    }

    /**
     * Obtient la valeur de la propriété resolution.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * Définit la valeur de la propriété resolution.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResolution(String value) {
        this.resolution = value;
    }

    /**
     * Obtient la valeur de la propriété artist.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Définit la valeur de la propriété artist.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArtist(String value) {
        this.artist = value;
    }

    /**
     * Obtient la valeur de la propriété album.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Définit la valeur de la propriété album.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlbum(String value) {
        this.album = value;
    }

}
