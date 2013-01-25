/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.samplingspatial.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.observation.xml.v200.OMProcessPropertyType;
import org.geotoolkit.sampling.xml.v200.SFSamplingFeatureType;
import org.opengis.metadata.quality.PositionalAccuracy;


/**
 *  When observations are made to estimate properties of a geospatial
 * 				feature, in particular where the value of a property varies within the scope of the
 * 				feature, a spatial sampling feature is used. Depending on accessibility and on the
 * 				nature of the expected property variation, the sampling feature may be extensive in
 * 				one, two or three spatial dimensions. Processing and visualization methods are often
 * 				dependent on the topological dimension of the sampling manifold, so this provides a
 * 				natural classification system for sampling features. This classification follows
 * 				common practice in focussing on conventional spatial dimensions. Properties observed
 * 				on sampling features may be time-dependent, but the temporal axis does not generally
 * 				contribute to the classification of sampling feature classes. Sampling feature
 * 				identity is usually less time-dependent than is the property value. 
 * 
 * <p>Java class for SF_SpatialSamplingFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SF_SpatialSamplingFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/2.0}SF_SamplingFeatureType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/samplingSpatial/2.0}SF_SpatialCommonProperties"/>
 *         &lt;element ref="{http://www.opengis.net/samplingSpatial/2.0}shape"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SF_SpatialSamplingFeatureType", propOrder = {
    "hostedProcedure",
    "positionalAccuracy",
    "shape"
})
public class SFSpatialSamplingFeatureType extends SFSamplingFeatureType {

    private List<OMProcessPropertyType> hostedProcedure;
    private List<PositionalAccuracy> positionalAccuracy;
    @XmlElement(required = true)
    private ShapeType shape;

    /**
     * Gets the value of the hostedProcedure property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link OMProcessPropertyType }
     * 
     * 
     */
    public List<OMProcessPropertyType> getHostedProcedure() {
        if (hostedProcedure == null) {
            hostedProcedure = new ArrayList<OMProcessPropertyType>();
        }
        return this.hostedProcedure;
    }

    /**
     * Gets the value of the positionalAccuracy property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link DQPositionalAccuracyPropertyType }
     * 
     * 
     */
    public List<PositionalAccuracy> getPositionalAccuracy() {
        if (positionalAccuracy == null) {
            positionalAccuracy = new ArrayList<PositionalAccuracy>();
        }
        return this.positionalAccuracy;
    }

    /**
     *  The association Geometry shall link a
     *  SF_SpatialSamplingFeature to a GM_Object that describes its shape.
     * 							
     * 
     * @return
     *     possible object is
     *     {@link ShapeType }
     *     
     */
    public ShapeType getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link ShapeType }
     *     
     */
    public void setShape(ShapeType value) {
        this.shape = value;
    }

}
