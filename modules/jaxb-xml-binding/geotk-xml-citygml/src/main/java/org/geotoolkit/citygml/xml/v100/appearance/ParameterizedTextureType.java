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
import javax.xml.bind.annotation.XmlType;


/**
 * Specialization for standard 2D textures. "target" provides the linking to surface geometry. Only
 *                 gml:MultiSurface and decendants of gml:AbstractSurfaceType are valid targets. As property of the link, a texture
 *                 parameterization either as set of texture coordinates or transformation matrix is given. 
 * 
 * <p>Java class for ParameterizedTextureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ParameterizedTextureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/citygml/appearance/1.0}AbstractTextureType">
 *       &lt;sequence>
 *         &lt;element name="target" type="{http://www.opengis.net/citygml/appearance/1.0}TextureAssociationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfParameterizedTexture" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParameterizedTextureType", propOrder = {
    "target",
    "genericApplicationPropertyOfParameterizedTexture"
})
public class ParameterizedTextureType extends AbstractTextureType {

    private List<TextureAssociationType> target;
    @XmlElement(name = "_GenericApplicationPropertyOfParameterizedTexture")
    private List<Object> genericApplicationPropertyOfParameterizedTexture;

    /**
     * Gets the value of the target property.
     * 
     */
    public List<TextureAssociationType> getTarget() {
        if (target == null) {
            target = new ArrayList<TextureAssociationType>();
        }
        return this.target;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfParameterizedTexture property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfParameterizedTexture() {
        if (genericApplicationPropertyOfParameterizedTexture == null) {
            genericApplicationPropertyOfParameterizedTexture = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfParameterizedTexture;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (target != null && target.size() > 0) {
            s.append("target:").append('\n');
            for (TextureAssociationType fp : target) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfParameterizedTexture != null && genericApplicationPropertyOfParameterizedTexture.size() > 0) {
            s.append("genericApplicationPropertyOfParameterizedTexture:").append('\n');
            for (Object fp : genericApplicationPropertyOfParameterizedTexture) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
