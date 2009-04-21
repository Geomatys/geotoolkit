/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso;

import java.util.Collection;
import java.util.Locale;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opengis.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.content.FeatureCatalogueDescription;
import org.geotoolkit.metadata.iso.content.AbstractContentInformation;


/**
 * Location of the responsible individual or organization.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Touraïvane (IRD)
 * @author Cédric Briançon (Geomatys)
 * @version 3.0
 *
 * @since 2.1
 * @module
 */
@XmlType(propOrder={
    "compliant",
    "language",
    "includeWithDataset",
    "featureTypes",
    "featureCatalogueCitations"
})
@XmlRootElement(name = "MD_FeatureCatalogueDescription")
public class DefaultFeatureCatalogueDescription extends AbstractContentInformation
       implements FeatureCatalogueDescription
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -5361236546997056467L;

    /**
     * Indication of whether or not the cited feature catalogue complies with ISO 19110.
     */
    private Boolean compliant;

    /**
     * Language(s) used within the catalogue
     */
    private Collection<Locale> language;

    /**
     * Indication of whether or not the feature catalogue is included with the dataset.
     */
    private Boolean includeWithDataset;

    /**
     * Subset of feature types from cited feature catalogue occurring in dataset.
     */
    private Collection<GenericName> featureTypes;

    /**
     * Complete bibliographic reference to one or more external feature catalogues.
     */
    private Collection<Citation> featureCatalogueCitations;

    /**
     * Construct an initially empty feature catalogue description.
     */
    public DefaultFeatureCatalogueDescription() {
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy.
     *
     * @since 2.4
     */
    public DefaultFeatureCatalogueDescription(final FeatureCatalogueDescription source) {
        super(source);
    }

    /**
     * Returns whether or not the cited feature catalogue complies with ISO 19110.
     */
    @Override
    @XmlElement(name = "complianceCode")
    public Boolean isCompliant() {
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
        return xmlOptional(language = nonNullCollection(language, Locale.class));
    }

    /**
     * Returns the language(s) used within the catalogue
     *
     * @param newValues The new languages.
     */
    public synchronized void setLanguages(
            final Collection<? extends Locale> newValues)
    {
        language = copyCollection(newValues, language, Locale.class);
    }

    /**
     * Returns whether or not the feature catalogue is included with the dataset.
     */
    @Override
    @XmlElement(name = "includeWithDataset", required = true)
    public boolean isIncludedWithDataset() {
        return includeWithDataset.booleanValue();
    }

    /**
     * Sets whether or not the feature catalogue is included with the dataset.
     *
     * @param newValue {@code true} if feature catalogue is included with the dataset.
     */
    public synchronized void setIncludedWithDataset(final Boolean newValue) {
        checkWritePermission();
        includeWithDataset = newValue;
    }

    /**
     * Returns the subset of feature types from cited feature catalogue occurring in dataset.
     *
     * @todo needs to annotate the package org.geotoolkit.util before.
     */
    @Override
    public synchronized Collection<GenericName> getFeatureTypes() {
        return featureTypes = nonNullCollection(featureTypes, GenericName.class);
    }

    /**
     * Sets the subset of feature types from cited feature catalogue occurring in dataset.
     *
     * @param newValues The new feature types.
     */
    public synchronized void setFeatureTypes(
            final Collection<? extends GenericName> newValues)
    {
        featureTypes = copyCollection(newValues, featureTypes, GenericName.class);
    }

    /**
     * Returns the complete bibliographic reference to one or more external feature catalogues.
     */
    @Override
    @XmlElement(name = "featureCatalogueCitation")
    public synchronized Collection<Citation> getFeatureCatalogueCitations() {
        return xmlOptional(featureCatalogueCitations = nonNullCollection(featureCatalogueCitations, Citation.class));
    }

    /**
     * Sets the complete bibliographic reference to one or more external feature catalogues.
     *
     * @param newValues The new feature catalogue citations.
     */
    public synchronized void setFeatureCatalogueCitations(
            final Collection<? extends Citation> newValues)
    {
        featureCatalogueCitations = copyCollection(newValues, featureCatalogueCitations, Citation.class);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code true}, since the marshalling
     * process is going to be done. This method is automatically called by JAXB
     * when the marshalling begins.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void beforeMarshal(Marshaller marshaller) {
        xmlMarshalling(true);
    }

    /**
     * Sets the {@code xmlMarshalling} flag to {@code false}, since the marshalling
     * process is finished. This method is automatically called by JAXB when the
     * marshalling ends.
     *
     * @param marshaller Not used in this implementation.
     */
    @SuppressWarnings("unused")
    private void afterMarshal(Marshaller marshaller) {
        xmlMarshalling(false);
    }
}
