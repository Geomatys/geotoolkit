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
package org.geotoolkit.wmts.xml.v100;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="Format" type="{http://www.opengis.net/ows/1.1}MimeType"/>
 *         &lt;element name="PayloadContent" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
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
@XmlType(name = "BinaryPayloadType", propOrder = {
    "format",
    "payloadContent"
})
@XmlRootElement(name = "BinaryPayload")
public class BinaryPayload {

    @XmlElement(name = "Format", required = true)
    private String format;
    @XmlElement(name = "PayloadContent", required = true)
    private byte[] payloadContent;

    /**
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }

    /**
     * Gets the value of the payloadContent property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getPayloadContent() {
        return payloadContent;
    }

    /**
     * Sets the value of the payloadContent property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setPayloadContent(byte[] value) {
        this.payloadContent = ((byte[]) value);
    }

    /**
     * Convert the Tile image to a BufferedImage.
     *
     * @param format a String containing the informal name of a format (e.g., "jpeg" or "tiff").
     * @return
     * @throws IOException
     */
    public BufferedImage getBufferedImageByFormatName(final String format) throws IOException {

        ByteArrayInputStream in = new ByteArrayInputStream(payloadContent);
        ImageInputStream st = ImageIO.createImageInputStream(in);

        Iterator<ImageReader> rs = ImageIO.getImageReadersByFormatName(format);
        ImageReader r = rs.next();
        r.setInput(st, true, true);

        BufferedImage bi = r.read(0);

        in.close();//un-necessary but good practice.

        return bi;
    }

    /**
     * Convert the Tile image to a BufferedImage.
     *
     * @param format a String containing the mime type of a format (e.g., "image/png" or "image/tiff").
     * @return
     * @throws IOException
     */
    public BufferedImage getBufferedImageByMimeType(final String mimeType) throws IOException {

        ByteArrayInputStream in = new ByteArrayInputStream(payloadContent);
        ImageInputStream st = ImageIO.createImageInputStream(in);

        Iterator<ImageReader> rs = ImageIO.getImageReadersByMIMEType(mimeType);
        ImageReader r = rs.next();
        r.setInput(st, true, true);

        BufferedImage bi = r.read(0);

        in.close();//un-necessary but good practice.

        return bi;
    }
}
