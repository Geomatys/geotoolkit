/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.metadata.iso.maintenance;

import java.util.Set;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.metadata.maintenance.ScopeDescription;
import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of the class of information covered by the information.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.02
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "dataset",
    "other"
})
@XmlRootElement(name = "MD_ScopeDescription")
public class DefaultScopeDescription extends MetadataEntity implements ScopeDescription {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5671299759930976286L;

    /**
     * The attributes to which the information applies.
     */
    private Set<AttributeType> attributes;

    /**
     * The features to which the information applies.
     */
    private Set<FeatureType> features;

    /**
     * The feature instances to which the information applies.
     */
    private Set<FeatureType> featureInstances;

    /**
     * The attribute instances to which the information applies.
     */
    private Set<AttributeType> attributeInstances;

    /**
     * Dataset to which the information applies.
     */
    private String dataset;

    /**
     * Class of information that does not fall into the other categories to
     * which the information applies.
     */
    private String other;

    /**
     * Creates an initially empty scope description.
     */
    public DefaultScopeDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultScopeDescription(final ScopeDescription source) {
        super(source);
    }

    /**
     * Returns the attributes to which the information applies.
     */
    @Override
    public Set<AttributeType> getAttributes() {
        return attributes = nonNullSet(attributes, AttributeType.class);
    }

    /**
     * Sets the attributes to which the information applies.
     *
     * @param newValues The new attributes.
     * @since 2.5
     */
    public synchronized void setAttributes(final Set<? extends AttributeType> newValues) {
        attributes = copySet(newValues, attributes, AttributeType.class);
    }

    /**
     * Returns the features to which the information applies.
     */
    @Override
    public Set<FeatureType> getFeatures() {
        return features = nonNullSet(features, FeatureType.class);
    }

    /**
     * Sets the features to which the information applies.
     *
     * @param newValues The new features.
     *
     * @since 2.5
     */
    public synchronized void setFeatures(final Set<? extends FeatureType> newValues) {
        features = copySet(newValues, features, FeatureType.class);
    }

    /**
     * Returns the feature instances to which the information applies.
     */
    @Override
    public Set<FeatureType> getFeatureInstances() {
        return featureInstances = nonNullSet(featureInstances, FeatureType.class);
    }

    /**
     * Sets the feature instances to which the information applies.
     *
     * @param newValues The new feature instances.
     *
     * @since 2.5
     */
    public synchronized void setFeatureInstances(final Set<? extends FeatureType> newValues) {
        featureInstances = copySet(newValues, featureInstances, FeatureType.class);
    }

    /**
     * Returns the attribute instances to which the information applies.
     *
     * @since 2.4
     */
    @Override
    public Set<AttributeType> getAttributeInstances() {
        return attributeInstances = nonNullSet(attributeInstances, AttributeType.class);
    }

    /**
     * Sets the attribute instances to which the information applies.
     *
     * @param newValues The new attribute instances.
     *
     * @since 2.5
     */
    public synchronized void setAttributeInstances(final Set<? extends AttributeType> newValues) {
        attributeInstances = copySet(newValues, attributeInstances, AttributeType.class);
    }

    /**
     * Returns the dataset to which the information applies.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "dataset", required = true)
    public String getDataset() {
        return dataset;
    }

    /**
     * Sets the dataset to which the information applies.
     *
     * @param newValue The new dataset.
     *
     * @since 2.4
     */
    public synchronized void setDataset(final String newValue) {
        checkWritePermission();
        dataset = newValue;
    }

    /**
     * Returns the class of information that does not fall into the other categories to
     * which the information applies.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "other", required = true)
    public String getOther() {
        return other;
    }

    /**
     * Sets the class of information that does not fall into the other categories to
     * which the information applies.
     *
     * @param newValue Other class of information.
     *
     * @since 2.4
     */
    public synchronized void setOther(final String newValue) {
        checkWritePermission();
        other = newValue;
    }
}
