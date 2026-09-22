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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CRSRefType;

/**
 * A coordinate operation that can transform coordinates from a {@code sourceCRS} to a
 * {@code targetCRS} (WCTS {@code transformation} element).
 *
 * <p>
 * The full {@code wcts:transformation} element extends {@code gml:CoordinateOperationRefType},
 * which is not available in {@code geotk-xml-gml} (no {@code coordinateOperations.xsd} bindings
 * generated there). WCTS 05-013 itself notes that in practice this element is normally just a
 * reference to a well-known coordinate operation, so this type is limited to that reference use
 * case, exposed through the {@code xlink:href} attribute.
 * </p>
 *
 * Note: no support for inline coordinate-operation definitions, add {@code CC_CoordinateOperation}
 * modelling if a server that returns inline definitions is encountered.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformationRefType", propOrder = {"sourceCRS", "targetCRS"})
@XmlRootElement(name = "transformation")
public class TransformationRefType {

    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlElement
    private CRSRefType sourceCRS;
    @XmlElement
    private CRSRefType targetCRS;

    public TransformationRefType() {
    }

    public TransformationRefType(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public CRSRefType getSourceCRS() {
        return sourceCRS;
    }

    public void setSourceCRS(CRSRefType sourceCRS) {
        this.sourceCRS = sourceCRS;
    }

    public CRSRefType getTargetCRS() {
        return targetCRS;
    }

    public void setTargetCRS(CRSRefType targetCRS) {
        this.targetCRS = targetCRS;
    }
}
