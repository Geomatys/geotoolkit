/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.citygml.xml.v100.appearance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;


/**
 * Specialization for georeferenced textures, i.e. textures using a planimetric projection. Such
 *                 textures contain an implicit parameterization (either stored within the image file, in an acompanying world file,
 *                 or using the "referencePoint" and "orientation"-elements). A georeference provided by "referencePoint" and
 *                 "orientation" always takes precedence. The search order for an external georeference is determined by the boolean
 *                 flag preferWorldFile. If this flag is set to true (its default value), a world file is looked for first and only
 *                 if it is not found the georeference from the image data is used. If preferWorldFile is false, the world file is
 *                 used only if no georeference from the image data is available. The "boundedBy"-property should contain the
 *                 bounding box of the projected image data. Since a georeferenced texture has a unique parameterization, "target"
 *                 only provides links to surface geometry without any additional texture parameterization. Only gml:MultiSurface or
 *                 decendants of gml:AbstractSurfaceType are valid targets.
 * 
 * <p>Java class for GeoreferencedTextureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeoreferencedTextureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractTextureType">
 *       &lt;sequence>
 *         &lt;element name="preferWorldFile" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="referencePoint" type="{http://www.opengis.net/gml}PointPropertyType" minOccurs="0"/>
 *         &lt;element name="orientation" type="{http://www.opengis.net/citygml/1.0}TransformationMatrix2x2Type" minOccurs="0"/>
 *         &lt;element name="target" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfGeoreferencedTexture" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeoreferencedTextureType", propOrder = {
    "preferWorldFile",
    "referencePoint",
    "orientation",
    "target",
    "genericApplicationPropertyOfGeoreferencedTexture"
})
public class GeoreferencedTextureType extends AbstractTextureType {

    @XmlElement(defaultValue = "true")
    private Boolean preferWorldFile;
    private PointPropertyType referencePoint;
    @XmlList
    @XmlElement(type = Double.class)
    private List<Double> orientation;
    @XmlSchemaType(name = "anyURI")
    private List<String> target;
    @XmlElement(name = "_GenericApplicationPropertyOfGeoreferencedTexture")
    private List<Object> genericApplicationPropertyOfGeoreferencedTexture;

    /**
     * Gets the value of the preferWorldFile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPreferWorldFile() {
        return preferWorldFile;
    }

    /**
     * Sets the value of the preferWorldFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPreferWorldFile(Boolean value) {
        this.preferWorldFile = value;
    }

    /**
     * Gets the value of the referencePoint property.
     * 
     * @return
     *     possible object is
     *     {@link PointPropertyType }
     *     
     */
    public PointPropertyType getReferencePoint() {
        return referencePoint;
    }

    /**
     * Sets the value of the referencePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointPropertyType }
     *     
     */
    public void setReferencePoint(PointPropertyType value) {
        this.referencePoint = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     */
    public List<Double> getOrientation() {
        if (orientation == null) {
            orientation = new ArrayList<Double>();
        }
        return this.orientation;
    }

    /**
     * Gets the value of the target property.
     * 
     */
    public List<String> getTarget() {
        if (target == null) {
            target = new ArrayList<String>();
        }
        return this.target;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfGeoreferencedTexture property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfGeoreferencedTexture() {
        if (genericApplicationPropertyOfGeoreferencedTexture == null) {
            genericApplicationPropertyOfGeoreferencedTexture = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfGeoreferencedTexture;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (referencePoint != null) {
            s.append("referencePoint:").append(referencePoint).append('\n');
        }
        if (orientation != null && orientation.size() > 0) {
            s.append("orientation:").append('\n');
            for (Double fp : orientation) {
                s.append(fp).append('\n');
            }
        }
        if (target != null && target.size() > 0) {
            s.append("target:").append('\n');
            for (String fp : target) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfGeoreferencedTexture != null && genericApplicationPropertyOfGeoreferencedTexture.size() > 0) {
            s.append("genericApplicationPropertyOfGeoreferencedTexture:").append('\n');
            for (Object fp : genericApplicationPropertyOfGeoreferencedTexture) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
