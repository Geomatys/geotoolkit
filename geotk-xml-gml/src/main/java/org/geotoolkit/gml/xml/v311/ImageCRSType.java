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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An engineering coordinate reference system applied to locations in images. Image coordinate reference systems are treated as a separate sub-type because a separate user community exists for images with its own terms of reference.
 *
 * <p>Java class for ImageCRSType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ImageCRSType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractReferenceSystemType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}usesCartesianCS"/>
 *           &lt;element ref="{http://www.opengis.net/gml}usesObliqueCartesianCS"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/gml}usesImageDatum"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImageCRSType", propOrder = {
    "usesCartesianCS",
    "usesObliqueCartesianCS",
    "usesImageDatum"
})
public class ImageCRSType extends AbstractReferenceSystemType {

    private CartesianCSRefType usesCartesianCS;
    private ObliqueCartesianCSRefType usesObliqueCartesianCS;
    @XmlElement(required = true)
    private ImageDatumRefType usesImageDatum;

    /**
     * An empty constructor used by JAXB.
     */
    ImageCRSType() {
    }

    /**
     * Build a new Image CRS with cartesian CS.
     */
    public ImageCRSType(final ImageDatumRefType usesImageDatum, final CartesianCSRefType usesCartesianCS) {
        this.usesImageDatum         = usesImageDatum;
        this.usesCartesianCS        = usesCartesianCS;
        this.usesObliqueCartesianCS = null;
    }

     /**
     * Build a new Image CRS with oblique cartesian CS.
     */
    public ImageCRSType(final ImageDatumRefType usesImageDatum, final ObliqueCartesianCSRefType usesObliqueCartesianCS) {
        this.usesImageDatum         = usesImageDatum;
        this.usesObliqueCartesianCS = usesObliqueCartesianCS;
        this.usesCartesianCS        = null;
    }

    /**
     * Gets the value of the usesCartesianCS property.
     */
    public CartesianCSRefType getUsesCartesianCS() {
        return usesCartesianCS;
    }

    /**
     * Gets the value of the usesObliqueCartesianCS property.
     */
    public ObliqueCartesianCSRefType getUsesObliqueCartesianCS() {
        return usesObliqueCartesianCS;
    }

    /**
     * Gets the value of the usesImageDatum property.
     */
    public ImageDatumRefType getUsesImageDatum() {
        return usesImageDatum;
    }
}
