/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
import net.jcip.annotations.ThreadSafe;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.metadata.maintenance.ScopeDescription;

import org.geotoolkit.metadata.iso.MetadataEntity;


/**
 * Description of the class of information covered by the information.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to the {@link org.apache.sis.metadata.iso} package.
 */
@ThreadSafe
@XmlType(name = "MD_ScopeDescription_Type", propOrder={
    "dataset",
    "other"
})
@XmlRootElement(name = "MD_ScopeDescription")
public class DefaultScopeDescription extends MetadataEntity implements ScopeDescription {
    /**
     * Serial number for inter-operability with different versions.
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
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultScopeDescription(final ScopeDescription source) {
        super(source);
    }

    /**
     * Returns a Geotk metadata implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object, using a <cite>shallow</cite> copy operation
     * (i.e. attributes are not cloned).
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultScopeDescription castOrCopy(final ScopeDescription object) {
        return (object == null) || (object instanceof DefaultScopeDescription)
                ? (DefaultScopeDescription) object : new DefaultScopeDescription(object);
    }

    /**
     * Returns the attributes to which the information applies.
     */
    @Override
    public synchronized Set<AttributeType> getAttributes() {
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
    public synchronized Set<FeatureType> getFeatures() {
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
    public synchronized Set<FeatureType> getFeatureInstances() {
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
    public synchronized Set<AttributeType> getAttributeInstances() {
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
    @XmlElement(name = "dataset")
    public synchronized String getDataset() {
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
    @XmlElement(name = "other")
    public synchronized String getOther() {
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
