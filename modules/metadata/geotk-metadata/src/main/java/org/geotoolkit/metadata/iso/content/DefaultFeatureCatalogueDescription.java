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
package org.geotoolkit.metadata.iso.content;

import java.util.Locale;
import java.util.Collection;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.content.FeatureCatalogueDescription;


/**
 * Information identifying the feature catalogue.
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
@Deprecated
@ThreadSafe
@XmlType(name = "MD_FeatureCatalogueDescription_Type", propOrder={
    "compliant",
    "languages",
    "includedWithDataset",
    "featureTypes",
    "featureCatalogueCitations"
})
@XmlRootElement(name = "MD_FeatureCatalogueDescription")
public class DefaultFeatureCatalogueDescription extends AbstractContentInformation
        implements FeatureCatalogueDescription
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1984922846251567908L;

    /**
     * Indication of whether or not the cited feature catalogue complies with ISO 19110.
     */
    private Boolean compliant;

    /**
     * Language(s) used within the catalogue
     */
    private Collection<Locale> languages;

    /**
     * Indication of whether or not the feature catalogue is included with the dataset.
     */
    private boolean includedWithDataset;

    /**
     * Subset of feature types from cited feature catalogue occurring in dataset.
     */
    private Collection<GenericName> featureTypes;

    /**
     * Complete bibliographic reference to one or more external feature catalogues.
     */
    private Collection<Citation> featureCatalogueCitations;

    /**
     * Constructs an initially empty feature catalogue description.
     */
    public DefaultFeatureCatalogueDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultFeatureCatalogueDescription(final FeatureCatalogueDescription source) {
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
    public static DefaultFeatureCatalogueDescription castOrCopy(final FeatureCatalogueDescription object) {
        return (object == null) || (object instanceof DefaultFeatureCatalogueDescription)
                ? (DefaultFeatureCatalogueDescription) object : new DefaultFeatureCatalogueDescription(object);
    }

    /**
     * Returns whether or not the cited feature catalogue complies with ISO 19110.
     */
    @Override
    @XmlElement(name = "complianceCode")
    public synchronized Boolean isCompliant() {
        return compliant;
    }

    /**
     * Sets whether or not the cited feature catalogue complies with ISO 19110.
     *
     * @param newValue The new compliance value.
     */
    public synchronized void setCompliant(final Boolean newValue) {
        checkWritePermission();
        compliant = newValue;
    }

    /**
     * Returns the language(s) used within the catalogue
     */
    @Override
    @XmlElement(name = "language")
    public synchronized Collection<Locale> getLanguages() {
        return languages = nonNullCollection(languages, Locale.class);
    }

    /**
     * Sets the language(s) used within the catalogue
     *
     * @param newValues The new languages.
     */
    public synchronized void setLanguages(final Collection<? extends Locale> newValues) {
        languages = copyCollection(newValues, languages, Locale.class);
    }

    /**
     * Returns whether or not the feature catalogue is included with the dataset.
     */
    @Override
    @XmlElement(name = "includedWithDataset", required = true)
    public synchronized boolean isIncludedWithDataset() {
        return includedWithDataset;
    }

    /**
     * Sets whether or not the feature catalogue is included with the dataset.
     *
     * @param newValue {@code true} if the feature catalogue is included.
     */
    public synchronized void setIncludedWithDataset(final boolean newValue) {
        checkWritePermission();
        includedWithDataset = newValue;
    }

    /**
     * Returns the subset of feature types from cited feature catalogue occurring in dataset.
     */
    @Override
    @XmlElement(name = "featureTypes")
    public synchronized Collection<GenericName> getFeatureTypes() {
        return featureTypes = nonNullCollection(featureTypes, GenericName.class);
    }

    /**
     * Sets the subset of feature types from cited feature catalogue occurring in dataset.
     *
     * @param newValues The new feature types.
     */
    public synchronized void setFeatureTypes(final Collection<? extends GenericName> newValues) {
        featureTypes = copyCollection(newValues, featureTypes, GenericName.class);
    }

    /**
     * Returns the complete bibliographic reference to one or more external feature catalogues.
     */
    @Override
    @XmlElement(name = "featureCatalogueCitation", required = true)
    public synchronized Collection<Citation> getFeatureCatalogueCitations() {
        return featureCatalogueCitations = nonNullCollection(featureCatalogueCitations, Citation.class);
    }

    /**
     * Sets the complete bibliographic reference to one or more external feature catalogues.
     *
     * @param newValues The feature catalogue citations.
     */
    public synchronized void setFeatureCatalogueCitations(final Collection<? extends Citation> newValues) {
        featureCatalogueCitations = copyCollection(newValues, featureCatalogueCitations, Citation.class);
    }
}
