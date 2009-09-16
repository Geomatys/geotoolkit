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
package org.geotoolkit.sampling.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * SpatiallyExtensiveSamplingFeature is the root class for sampling manifolds that are extensive in 1- 2- or 3-D.
 * 
 * <p>Java class for SpatiallyExtensiveSamplingFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpatiallyExtensiveSamplingFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SamplingFeatureType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpatiallyExtensiveSamplingFeatureType")
@XmlSeeAlso({
    SamplingCurveType.class,
    SamplingSolidType.class,
    SamplingSurfaceType.class
})
public class SpatiallyExtensiveSamplingFeatureType extends SamplingFeatureEntry
{


}
