/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.GridType;

/**
 * A reference to a remote resource or local payload used as {@code Transform} input data, or
 * returned as {@code TransformedData} output ({@code wcts:Reference}, extending the private
 * {@code ows:ReferenceType} of WCTS 05-013 with an optional {@code gml:Grid}, used to describe the
 * geometry of a georeference-less image such as a WMS-produced PNG/JPEG).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", propOrder = {"abstractText", "format", "requestMessage", "grid"})
public class ReferenceType {

    @XmlElement(name = "Abstract", namespace = "http://www.opengeospatial.net/ows")
    private String abstractText;
    @XmlElement(name = "Format", namespace = "http://www.opengeospatial.net/ows")
    private String format;
    @XmlElement(name = "RequestMessage", namespace = "http://www.opengeospatial.net/ows")
    @XmlSchemaType(name = "anyURI")
    private String requestMessage;
    private GridType grid;

    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;

    public ReferenceType() {
    }

    public ReferenceType(String href) {
        this.href = href;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public GridType getGrid() {
        return grid;
    }

    public void setGrid(GridType grid) {
        this.grid = grid;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
