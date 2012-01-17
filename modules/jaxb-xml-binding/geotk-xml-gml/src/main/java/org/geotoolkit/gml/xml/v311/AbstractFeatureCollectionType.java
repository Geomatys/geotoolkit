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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * A feature collection contains zero or more features.
 * 
 * <p>Java class for AbstractFeatureCollectionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractFeatureCollectionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}featureMember" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}featureMembers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractFeatureCollectionType", propOrder = {
    "featureMember",
    "featureMembers"
})
@XmlSeeAlso({
    FeatureCollectionType.class
})
public abstract class AbstractFeatureCollectionType extends AbstractFeatureType {

    private List<FeaturePropertyType> featureMember;
    private FeatureArrayPropertyType featureMembers;

    public AbstractFeatureCollectionType() {

    }

    public AbstractFeatureCollectionType(final String id, final String name, final String description, final List<FeaturePropertyType> featureMember) {
        super(id, name, description);
        this.featureMember = featureMember;
    }

    /**
     * Gets the value of the featureMember property.
     */
    public List<FeaturePropertyType> getFeatureMember() {
        if (featureMember == null) {
            featureMember = new ArrayList<FeaturePropertyType>();
        }
        return this.featureMember;
    }

    /**
     * Gets the value of the featureMember property.
     */
    public void addFeatureMember(final FeaturePropertyType feature) {
        if (featureMember == null) {
            featureMember = new ArrayList<FeaturePropertyType>();
        }
        this.featureMember.add(feature);
    }

    /**
     * Return true if the feature colelction contains the specified feature.
     *
     * @param featureId
     * @return
     */
    public boolean containsFeature(final String featureId) {
        if (featureMember != null) {
            for (FeaturePropertyType feat : featureMember) {
                if (feat.getHref() != null && feat.getHref().equals(featureId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the value of the featureMembers property.
     * 
     * @return
     *     possible object is
     *     {@link FeatureArrayPropertyType }
     *     
     */
    public FeatureArrayPropertyType getFeatureMembers() {
        return featureMembers;
    }

    /**
     * Sets the value of the featureMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeatureArrayPropertyType }
     *     
     */
    public void setFeatureMembers(final FeatureArrayPropertyType value) {
        this.featureMembers = value;
    }
    
    public void computeBounds() {
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;
        
        for (FeaturePropertyType memberProp : featureMember) {
            final AbstractFeatureType member = memberProp.getAbstractFeature();
            if (member != null) {
                final BoundingShapeType bound = member.getBoundedBy();
                if (bound != null) {
                    if (bound.getEnvelope() != null) {
                        if (bound.getEnvelope().getLowerCorner() != null
                            && bound.getEnvelope().getLowerCorner().getValue() != null
                            && bound.getEnvelope().getLowerCorner().getValue().size() > 1 ) {
                            final List<Double> lower = bound.getEnvelope().getLowerCorner().getValue();
                            if (lower.get(0) < minx) {
                                minx = lower.get(0);
                            }
                            if (lower.get(1) < miny) {
                                miny = lower.get(1);
                            }
                        }
                        if (bound.getEnvelope().getUpperCorner() != null
                            && bound.getEnvelope().getUpperCorner().getValue() != null
                            && bound.getEnvelope().getUpperCorner().getValue().size() > 1 ) {
                            final List<Double> upper = bound.getEnvelope().getUpperCorner().getValue();
                            if (upper.get(0) > maxx) {
                                maxx = upper.get(0);
                            }
                            if (upper.get(1) > maxy) {
                                maxy = upper.get(1);
                            }
                        }
                    }
                }
            }
        }
        
        if (minx == Double.MAX_VALUE) {
            minx = -180.0;
        }
        if (miny == Double.MAX_VALUE) {
            miny = -90.0;
        }
        if (maxx == (-Double.MAX_VALUE)) {
            maxx = 180.0;
        }
        if (maxy == (-Double.MAX_VALUE)) {
            maxy = 90.0;
        }
        final EnvelopeType env =  new EnvelopeType(null, new DirectPositionType(minx, miny), new DirectPositionType(maxx, maxy), "EPSG:4326");
        env.setSrsDimension(2);
        env.setAxisLabels("Y X");
        setBoundedBy(new BoundingShapeType(env));
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractFeatureCollectionType && super.equals(object, mode)) {
            final AbstractFeatureCollectionType that = (AbstractFeatureCollectionType) object;

            return Utilities.equals(this.featureMember,  that.featureMember) &&
                   Utilities.equals(this.featureMembers, that.featureMembers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.featureMember != null ? this.featureMember.hashCode() : 0);
        hash = 23 * hash + (this.featureMembers != null ? this.featureMembers.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (featureMember != null && featureMember.size() > 0) {
            s.append("featureMember:").append('\n');
            for (FeaturePropertyType fp : featureMember) {
                s.append(fp).append('\n');
            }
        }
        if (featureMembers != null) {
            s.append("featureMembers:").append(featureMembers).append('\n');
        }
        return s.toString();
    }

}
