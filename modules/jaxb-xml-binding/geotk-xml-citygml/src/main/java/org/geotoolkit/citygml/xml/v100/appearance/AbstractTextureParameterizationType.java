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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractGMLType;


/**
 * Base class for augmenting a link "texture->surface" with texture parameterization. Subclasses of
 *                 this class define concrete parameterizations. Currently, texture coordinates and texture coordinate generation
 *                 using a transformation matrix are available. 
 * 
 * <p>Java class for AbstractTextureParameterizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractTextureParameterizationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfTextureParameterization" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractTextureParameterizationType", propOrder = {
    "genericApplicationPropertyOfTextureParameterization"
})
@XmlSeeAlso({
    TexCoordListType.class,
    TexCoordGenType.class
})
public abstract class AbstractTextureParameterizationType extends AbstractGMLType {

    @XmlElement(name = "_GenericApplicationPropertyOfTextureParameterization")
    private List<Object> genericApplicationPropertyOfTextureParameterization;

    /**
     * Gets the value of the genericApplicationPropertyOfTextureParameterization property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfTextureParameterization() {
        if (genericApplicationPropertyOfTextureParameterization == null) {
            genericApplicationPropertyOfTextureParameterization = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfTextureParameterization;
    }
    
     @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (genericApplicationPropertyOfTextureParameterization != null && genericApplicationPropertyOfTextureParameterization.size() > 0) {
            s.append("genericApplicationPropertyOfTextureParameterization:").append('\n');
            for (Object fp : genericApplicationPropertyOfTextureParameterization) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }

}
