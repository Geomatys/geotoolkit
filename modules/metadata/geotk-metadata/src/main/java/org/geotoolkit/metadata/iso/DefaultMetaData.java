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
package org.geotoolkit.metadata.iso;

import java.util.Date;
import java.util.Locale;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.ApplicationSchemaInformation;
import org.opengis.metadata.MetadataExtensionInformation;
import org.opengis.metadata.PortrayalCatalogueReference;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.distribution.Distribution;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.spatial.SpatialRepresentation;
import org.opengis.referencing.ReferenceSystem;

import org.geotoolkit.internal.jaxb.MarshalContext;
import org.geotoolkit.internal.jaxb.gmd.PT_Locale;
import org.geotoolkit.xml.Namespaces;


/**
 * Root entity which defines metadata about a resource or resources.
 *
 * {@section Localization}
 * When this object is marshalled as an ISO 19139 compliant XML document, the value
 * given to the {@link #setLanguage(Locale)} method will be used for the localization
 * of {@link org.opengis.util.InternationalString} and {@link org.opengis.util.CodeList}
 * instances of in this {@code DefaultMetadata} object and every children, as required by
 * INSPIRE rules. If no language were specified, then the default locale will be the one
 * defined in the {@link org.geotoolkit.xml.XML#LOCALE} marshaller property, if any.
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
@XmlType(name = "MD_Metadata_Type", propOrder={
    "fileIdentifier",
    "language",
    "characterSet",
    "parentIdentifier",
    "hierarchyLevels",
    "hierarchyLevelNames",
    "contacts",
    "dateStamp",
    "metadataStandardName",
    "metadataStandardVersion",
    "dataSetUri",
    "locales",
    "spatialRepresentationInfo",
    "referenceSystemInfo",
    "metadataExtensionInfo",
    "identificationInfo",
    "contentInfo",
    "distributionInfo",
    "dataQualityInfo",
    "portrayalCatalogueInfo",
    "metadataConstraints",
    "applicationSchemaInfo",
    "metadataMaintenance",
    "acquisitionInformation"
})
@XmlRootElement(name = "MD_Metadata")
@XmlSeeAlso(org.geotoolkit.internal.jaxb.gmi.MI_Metadata.class)
public class DefaultMetadata extends MetadataEntity implements Metadata {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7254025230235329493L;

    /**
     * Unique identifier for this metadata file, or {@code null} if none.
     */
    private String fileIdentifier;

    /**
     * Language used for documenting metadata.
     */
    private Locale language;

    /**
     * Information about an alternatively used localized character
     * strings for linguistic extensions.
     */
    private Collection<Locale> locales;

    /**
     * Full name of the character coding standard used for the metadata set.
     */
    private CharacterSet characterSet;

    /**
     * File identifier of the metadata to which this metadata is a subset (child).
     */
    private String parentIdentifier;

    /**
     * Scope to which the metadata applies.
     */
    private Collection<ScopeCode> hierarchyLevels;

    /**
     * Name of the hierarchy levels for which the metadata is provided.
     */
    private Collection<String> hierarchyLevelNames;

    /**
     * Parties responsible for the metadata information.
     */
    private Collection<ResponsibleParty> contacts;

    /**
     * Uniformed Resource Identifier (URI) of the dataset to which the metadata applies.
     */
    private String dataSetUri;

    /**
     * Date that the metadata was created, in milliseconds elapsed since January 1st, 1970.
     * If not defined, then then value is {@link Long#MIN_VALUE}.
     */
    private long dateStamp;

    /**
     * Name of the metadata standard (including profile name) used.
     */
    private String metadataStandardName;

    /**
     * Version (profile) of the metadata standard used.
     */
    private String metadataStandardVersion;

    /**
     * Digital representation of spatial information in the dataset.
     */
    private Collection<SpatialRepresentation> spatialRepresentationInfo;

    /**
     * Description of the spatial and temporal reference systems used in the dataset.
     */
    private Collection<ReferenceSystem> referenceSystemInfo;

    /**
     * Information describing metadata extensions.
     */
    private Collection<MetadataExtensionInformation> metadataExtensionInfo;

    /**
     * Basic information about the resource(s) to which the metadata applies.
     */
    private Collection<Identification> identificationInfo;

    /**
     * Provides information about the feature catalogue and describes the coverage and
     * image data characteristics.
     */
    private Collection<ContentInformation> contentInfo;

    /**
     * Provides information about the distributor of and options for obtaining the resource(s).
     */
    private Distribution distributionInfo;

    /**
     * Provides overall assessment of quality of a resource(s).
     */
    private Collection<DataQuality> dataQualityInfo;

    /**
     * Provides information about the catalogue of rules defined for the portrayal of a resource(s).
     */
    private Collection<PortrayalCatalogueReference> portrayalCatalogueInfo;

    /**
     * Provides restrictions on the access and use of data.
     */
    private Collection<Constraints> metadataConstraints;

    /**
     * Provides information about the conceptual schema of a dataset.
     */
    private Collection<ApplicationSchemaInformation> applicationSchemaInfo;

    /**
     * Provides information about the frequency of metadata updates, and the scope of those updates.
     */
    private MaintenanceInformation metadataMaintenance;

    /**
     * Provides information about the acquisition of the data.
     */
    private Collection<AcquisitionInformation> acquisitionInformation;

    /**
     * Creates an initially empty metadata.
     */
    public DefaultMetadata() {
        dateStamp = Long.MIN_VALUE;
    }

    /**
     * Constructs a metadata entity initialized with the values from the specified metadata.
     *
     * @param source The metadata to copy, or {@code null} if none.
     *
     * @since 2.4
     */
    public DefaultMetadata(final Metadata source) {
        super(source);
        if (source != null) {
            // Be careful to not overwrite date value (GEOTK-170).
            if (dateStamp == 0 && source.getDateStamp() == null) {
                dateStamp = Long.MIN_VALUE;
            }
        }
    }

    /**
     * Creates a meta data initialized to the specified values.
     *
     * @param contact   Party responsible for the metadata information.
     * @param dateStamp Date that the metadata was created.
     * @param identificationInfo Basic information about the resource
     *        to which the metadata applies.
     */
    public DefaultMetadata(final ResponsibleParty contact,
                           final Date             dateStamp,
                           final Identification   identificationInfo)
    {
        this(); // Initialize the date field.
        setContacts          (Collections.singleton(contact));
        setDateStamp         (dateStamp);
        setIdentificationInfo(Collections.singleton(identificationInfo));
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
    public static DefaultMetadata castOrCopy(final Metadata object) {
        return (object == null) || (object instanceof DefaultMetadata)
                ? (DefaultMetadata) object : new DefaultMetadata(object);
    }

    /**
     * Returns the unique identifier for this metadata file, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "fileIdentifier")
    public synchronized String getFileIdentifier() {
        return fileIdentifier;
    }

    /**
     * Sets the unique identifier for this metadata file, or {@code null} if none.
     *
     * @param newValue The new identifier.
     */
    public synchronized void setFileIdentifier(final String newValue) {
        checkWritePermission();
        fileIdentifier = newValue;
    }

    /**
     * Returns the language used for documenting metadata. This {@code DefaultMetadata} object and
     * its children will use that locale for marshalling {@link org.opengis.util.InternationalString}
     * and {@link org.opengis.util.CodeList} instances in ISO 19139 compliant XML documents.
     */
    @Override
    @XmlElement(name = "language")
    public synchronized Locale getLanguage() {
        return language;
    }

    /**
     * Sets the language used for documenting metadata. This {@code DefaultMetadata} object and its
     * children will use the given locale for marshalling {@link org.opengis.util.InternationalString}
     * and {@link org.opengis.util.CodeList} instances in ISO 19139 compliant XML documents.
     *
     * @param newValue The new language.
     *
     * @see org.geotoolkit.xml.XML#LOCALE
     */
    public synchronized void setLanguage(final Locale newValue) {
        checkWritePermission();
        language = newValue;
        // The "magik" applying this language to every children
        // is performed by the 'beforeMarshal(Marshaller)' method.
    }

    /**
     * Returns the full name of the character coding standard used for the metadata set.
     */
    @Override
    @XmlElement(name = "characterSet")
    public synchronized CharacterSet getCharacterSet()  {
        return characterSet;
    }

    /**
     * Sets the full name of the character coding standard used for the metadata set.
     *
     * @param newValue The new character set.
     */
    public synchronized void setCharacterSet(final CharacterSet newValue) {
        checkWritePermission();
        characterSet = newValue;
    }

    /**
     * Returns the file identifier of the metadata to which this metadata is a subset (child).
     */
    @Override
    @XmlElement(name = "parentIdentifier")
    public synchronized String getParentIdentifier() {
        return parentIdentifier;
    }

    /**
     * Sets the file identifier of the metadata to which this metadata is a subset (child).
     *
     * @param newValue The new parent identifier.
     */
    public synchronized void setParentIdentifier(final String newValue) {
        checkWritePermission();
        parentIdentifier = newValue;
    }

    /**
     * Returns the scope to which the metadata applies.
     */
    @Override
    @XmlElement(name = "hierarchyLevel")
    public synchronized Collection<ScopeCode> getHierarchyLevels() {
        return hierarchyLevels = nonNullCollection(hierarchyLevels, ScopeCode.class);
    }

    /**
     * Sets the scope to which the metadata applies.
     *
     * @param newValues The new hierarchy levels.
     */
    public synchronized void setHierarchyLevels(final Collection<? extends ScopeCode> newValues) {
        hierarchyLevels = copyCollection(newValues, hierarchyLevels, ScopeCode.class);
    }

    /**
     * Returns the name of the hierarchy levels for which the metadata is provided.
     */
    @Override
    @XmlElement(name = "hierarchyLevelName")
    public synchronized Collection<String> getHierarchyLevelNames() {
        return hierarchyLevelNames = nonNullCollection(hierarchyLevelNames, String.class);
    }

    /**
     * Sets the name of the hierarchy levels for which the metadata is provided.
     *
     * @param newValues The new hierarchy level names.
     */
    public synchronized void setHierarchyLevelNames(final Collection<? extends String> newValues) {
        hierarchyLevelNames = copyCollection(newValues, hierarchyLevelNames, String.class);
    }

    /**
     * Returns the parties responsible for the metadata information.
     */
    @Override
    @XmlElement(name = "contact", required = true)
    public synchronized Collection<ResponsibleParty> getContacts() {
        return contacts = nonNullCollection(contacts, ResponsibleParty.class);
    }

    /**
     * Sets the parties responsible for the metadata information.
     *
     * @param newValues The new contacts.
     */
    public synchronized void setContacts(final Collection<? extends ResponsibleParty> newValues) {
        checkWritePermission();
        contacts = copyCollection(newValues, contacts, ResponsibleParty.class);
    }

    /**
     * Returns the date that the metadata was created.
     */
    @Override
    @XmlElement(name = "dateStamp", required = true)
    public synchronized Date getDateStamp() {
        return (dateStamp != Long.MIN_VALUE) ? new Date(dateStamp) : (Date)null;
    }

    /**
     * Sets the date that the metadata was created.
     *
     * @param newValue The new date stamp.
     */
    public synchronized void setDateStamp(final Date newValue) {
        checkWritePermission();
        dateStamp = (newValue != null) ? newValue.getTime() : Long.MIN_VALUE;
    }

    /**
     * Returns the name of the metadata standard (including profile name) used.
     */
    @Override
    @XmlElement(name = "metadataStandardName")
    public synchronized String getMetadataStandardName() {
        return metadataStandardName;
    }

    /**
     * Name of the metadata standard (including profile name) used.
     *
     * @param newValue The new metadata standard name.
     */
    public synchronized void setMetadataStandardName(final String newValue) {
        checkWritePermission();
        metadataStandardName = newValue;
    }

    /**
     * Returns the version (profile) of the metadata standard used.
     */
    @Override
    @XmlElement(name = "metadataStandardVersion")
    public synchronized String getMetadataStandardVersion() {
        return metadataStandardVersion;
    }

    /**
     * Sets the version (profile) of the metadata standard used.
     *
     * @param newValue The new metadata standard version.
     */
    public synchronized void setMetadataStandardVersion(final String newValue) {
        checkWritePermission();
        metadataStandardVersion = newValue;
    }

    /**
     * Returns the digital representation of spatial information in the dataset.
     */
    @Override
    @XmlElement(name = "spatialRepresentationInfo")
    public synchronized Collection<SpatialRepresentation> getSpatialRepresentationInfo() {
        return spatialRepresentationInfo = nonNullCollection(spatialRepresentationInfo, SpatialRepresentation.class);
    }

    /**
     * Sets the digital representation of spatial information in the dataset.
     *
     * @param newValues The new spatial representation info.
     */
    public synchronized void setSpatialRepresentationInfo(
            final Collection<? extends SpatialRepresentation> newValues)
    {
        spatialRepresentationInfo = copyCollection(newValues, spatialRepresentationInfo, SpatialRepresentation.class);
    }

    /**
     * Returns the description of the spatial and temporal reference systems used in the dataset.
     */
    @Override
    @XmlElement(name = "referenceSystemInfo")
    public synchronized Collection<ReferenceSystem> getReferenceSystemInfo() {
        return referenceSystemInfo = nonNullCollection(referenceSystemInfo, ReferenceSystem.class);
    }

    /**
     * Sets the description of the spatial and temporal reference systems used in the dataset.
     *
     * @param newValues The new reference system info.
     */
    public synchronized void setReferenceSystemInfo(
            final Collection<? extends ReferenceSystem> newValues)
    {
        referenceSystemInfo = copyCollection(newValues, referenceSystemInfo, ReferenceSystem.class);
    }

    /**
     * Returns information describing metadata extensions.
     */
    @Override
    @XmlElement(name = "metadataExtensionInfo")
    public synchronized Collection<MetadataExtensionInformation> getMetadataExtensionInfo() {
        return metadataExtensionInfo = nonNullCollection(metadataExtensionInfo, MetadataExtensionInformation.class);
    }

    /**
     * Sets information describing metadata extensions.
     *
     * @param newValues The new metadata extension info.
     */
    public synchronized void setMetadataExtensionInfo(
            final Collection<? extends MetadataExtensionInformation> newValues)
    {
        metadataExtensionInfo = copyCollection(newValues, metadataExtensionInfo, MetadataExtensionInformation.class);
    }

    /**
     * Returns basic information about the resource(s) to which the metadata applies.
     */
    @Override
    @XmlElement(name = "identificationInfo", required = true)
    public synchronized Collection<Identification> getIdentificationInfo() {
        return identificationInfo = nonNullCollection(identificationInfo, Identification.class);
    }

    /**
     * Sets basic information about the resource(s) to which the metadata applies.
     *
     * @param newValues The new identification info.
     */
    public synchronized void setIdentificationInfo(
            final Collection<? extends Identification> newValues)
    {
        identificationInfo = copyCollection(newValues, identificationInfo, Identification.class);
    }

    /**
     * Provides information about the feature catalogue and describes the coverage and
     * image data characteristics.
     */
    @Override
    @XmlElement(name = "contentInfo")
    public synchronized Collection<ContentInformation> getContentInfo() {
        return contentInfo = nonNullCollection(contentInfo, ContentInformation.class);
    }

    /**
     * Sets information about the feature catalogue and describes the coverage and
     * image data characteristics.
     *
     * @param newValues The new content info.
     */
    public synchronized void setContentInfo(final Collection<? extends ContentInformation> newValues) {
        contentInfo = copyCollection(newValues, contentInfo, ContentInformation.class);
    }

    /**
     * Provides information about the distributor of and options for obtaining the resource(s).
     */
    @Override
    @XmlElement(name = "distributionInfo")
    public synchronized Distribution getDistributionInfo() {
        return distributionInfo;
    }

    /**
     * Provides information about the distributor of and options for obtaining the resource(s).
     *
     * @param newValue The new distribution info.
     */
    public synchronized void setDistributionInfo(final Distribution newValue) {
        checkWritePermission();
        distributionInfo = newValue;
    }

    /**
     * Provides overall assessment of quality of a resource(s).
     */
    @Override
    @XmlElement(name = "dataQualityInfo")
    public synchronized Collection<DataQuality> getDataQualityInfo() {
        return dataQualityInfo = nonNullCollection(dataQualityInfo, DataQuality.class);
    }

    /**
     * Sets overall assessment of quality of a resource(s).
     *
     * @param newValues The new data quality info.
     */
    public synchronized void setDataQualityInfo(final Collection<? extends DataQuality> newValues) {
        dataQualityInfo = copyCollection(newValues, dataQualityInfo, DataQuality.class);
    }

    /**
     * Provides information about the catalogue of rules defined for the portrayal of a
     * resource(s).
     */
    @Override
    @XmlElement(name = "portrayalCatalogueInfo")
    public synchronized Collection<PortrayalCatalogueReference> getPortrayalCatalogueInfo() {
        return portrayalCatalogueInfo = nonNullCollection(portrayalCatalogueInfo, PortrayalCatalogueReference.class);
    }

    /**
     * Sets information about the catalogue of rules defined for the portrayal of a resource(s).
     *
     * @param newValues The new portrayal catalog info.
     */
    public synchronized void setPortrayalCatalogueInfo(
            final Collection<? extends PortrayalCatalogueReference> newValues)
    {
        portrayalCatalogueInfo = copyCollection(newValues, portrayalCatalogueInfo, PortrayalCatalogueReference.class);
    }

    /**
     * Provides restrictions on the access and use of data.
     */
    @Override
    @XmlElement(name = "metadataConstraints")
    public synchronized Collection<Constraints> getMetadataConstraints() {
        return metadataConstraints = nonNullCollection(metadataConstraints, Constraints.class);
    }

    /**
     * Sets restrictions on the access and use of data.
     *
     * @param newValues The new metadata constraints.
     */
    public synchronized void setMetadataConstraints(
            final Collection<? extends Constraints> newValues)
    {
        metadataConstraints = copyCollection(newValues, metadataConstraints, Constraints.class);
    }

    /**
     * Provides information about the conceptual schema of a dataset.
     */
    @Override
    @XmlElement(name = "applicationSchemaInfo")
    public synchronized Collection<ApplicationSchemaInformation> getApplicationSchemaInfo() {
        return applicationSchemaInfo = nonNullCollection(applicationSchemaInfo, ApplicationSchemaInformation.class);
    }

    /**
     * Provides information about the conceptual schema of a dataset.
     *
     * @param newValues The new application schema info.
     */
    public synchronized void setApplicationSchemaInfo(
            final Collection<? extends ApplicationSchemaInformation> newValues)
    {
        applicationSchemaInfo = copyCollection(newValues, applicationSchemaInfo, ApplicationSchemaInformation.class);
    }

    /**
     * Provides information about the frequency of metadata updates, and the scope of those updates.
     */
    @Override
    @XmlElement(name = "metadataMaintenance")
    public synchronized MaintenanceInformation getMetadataMaintenance() {
        return metadataMaintenance;
    }

    /**
     * Sets information about the frequency of metadata updates, and the scope of those updates.
     *
     * @param newValue The new metadata maintenance.
     */
    public synchronized void setMetadataMaintenance(final MaintenanceInformation newValue) {
        checkWritePermission();
        metadataMaintenance = newValue;
    }

    /**
     * Provides information about an alternatively used localized character
     * string for a linguistic extension.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "locale")
    @XmlJavaTypeAdapter(PT_Locale.class)
    public synchronized Collection<Locale> getLocales() {
        return locales = nonNullCollection(locales, Locale.class);
    }

    /**
     * Sets information about an alternatively used localized character
     * string for a linguistic extension.
     *
     * @param newValues The new locales.
     *
     * @since 2.4
     */
    public synchronized void setLocales(final Collection<? extends Locale> newValues) {
        locales = copyCollection(newValues, locales, Locale.class);
    }

    /**
     * Provides the URI of the dataset to which the metadata applies.
     *
     * @since 2.4
     */
    @Override
    @XmlElement(name = "dataSetURI")
    public synchronized String getDataSetUri() {
        return dataSetUri;
    }

    /**
     * Sets the URI of the dataset to which the metadata applies.
     *
     * @param newValue The new data set URI.
     *
     * @since 2.4
     */
    public synchronized void setDataSetUri(final String newValue) {
        checkWritePermission();
        dataSetUri = newValue;
    }

    /**
     * Provides information about the acquisition of the data.
     *
     * @since 3.03
     */
    @Override
    @XmlElement(name = "acquisitionInformation", namespace = Namespaces.GMI)
    public synchronized Collection<AcquisitionInformation> getAcquisitionInformation() {
        return acquisitionInformation = nonNullCollection(acquisitionInformation, AcquisitionInformation.class);
    }

    /**
     * Sets information about the acquisition of the data.
     *
     * @param newValues The new acquisition information.
     *
     * @since 3.03
     */
    public synchronized void setAcquisitionInformation(
            final Collection<? extends AcquisitionInformation> newValues)
    {
        acquisitionInformation = copyCollection(newValues, acquisitionInformation, AcquisitionInformation.class);
    }

    /**
     * Invoked by JAXB {@link javax.xml.bind.Marshaller} before this object is marshalled to XML.
     * This method sets the locale to be used for XML marshalling to the metadata language.
     *
     * @since 3.17
     */
    private void beforeMarshal(final Marshaller marshaller) {
        MarshalContext.pushLocale(language);
    }

    /**
     * Invoked by JAXB {@link javax.xml.bind.Marshaller} after this object has been marshalled to
     * XML. This method restores the locale to be used for XML marshalling to its previous value.
     *
     * @since 3.17
     */
    private void afterMarshal(final Marshaller marshaller) {
        MarshalContext.pullLocale();
    }
}
