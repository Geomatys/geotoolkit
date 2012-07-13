/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
 */
package org.geotoolkit.metadata.netcdf;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Date;
import java.io.IOException;
import java.net.URI;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.units.DateFormatter;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.extent.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.constraint.*;
import org.opengis.metadata.identification.*;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.util.InternationalString;
import org.opengis.util.CodeList;

import org.geotoolkit.util.Strings;
import org.geotoolkit.util.Version;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.internal.CodeLists;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.XArrays;


/**
 * Mapping from ISO 19115-2 metadata to NetCDF metadata. The {@link String} constants declared in
 * the {@linkplain NetcdfMetadata parent class} are the name of attributes to be written by this
 * class.
 *
 * {@section Multi-occurrences}
 * <p>Multi-occurrences is allowed only for the following attribute values:</p>
 * <ul>
 *   <li>{@value #LICENSE},
 *       formatted as a multi-lines string.</li>
 *   <li>{@value #ACCESS_CONSTRAINT},
 *       formatted as a comma-separated string.</li>
 *   <li>{@value #HISTORY},
 *       formatted as a multi-lines string.</li>
 *   <li>{@value #KEYWORDS},
 *       formatted as a comma-separated list. However only the keywords belonging to the
 *       first vocabulary found will be formatted. The vocabulary name will be stored in
 *       the {@value #VOCABULARY} attribute.</li>
 *   <li>Latitude and longitude extremum, as the union of all geographic extents.</li>
 * </ul>
 * <p>For every attributes not in the above list, only the first occurrence will be written in the NetCDF file.
 * For example if the ISO-19115 metadata define many {@linkplain Citation#getIdentifiers() identifiers},
 * then only the first one will be stored in the {@value #IDENTIFIER} attribute.</p>
 *
 * {@section Known limitations}
 * <p>The current implementation does not set the {@value #STANDARD_NAME} and
 * {@value #STANDARD_NAME_VOCABULARY} attributes. This is because both {@value #STANDARD_NAME} and
 * {@value #KEYWORDS} take their values from a {@link Keywords} object having {@link KeywordType#THEME},
 * so we don't have a way to differentiate them at this stage.</p>
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfMetadataWriter extends NetcdfMetadata {
    /**
     * The NetCDF file where to write ISO metadata.
     * This file is set at construction time.
     * <p>
     * This {@code NetcdfMetadataReader} class does <strong>not</strong> close this file.
     * Closing this file after usage is the user responsibility.
     */
    protected final NetcdfFileWriteable file;

    /**
     * The set of attribute values already defined by this writer.
     * This is used in order to avoid overwriting a value written in a previous pass.
     */
    private final Set<String> defined;

    /**
     * The values to write in the {@value #KEYWORDS} attribute.
     * This is created only when first needed and will be formatted as a comma-separated list.
     */
    private Set<String> keywords;

    /**
     * The values to write in the {@value #LICENSE} attribute.
     * This is created only when first needed and will be formatted as a multi-lines string.
     */
    private Set<String> licenses;

    /**
     * The values to write in the {@value #ACCESS_CONSTRAINT} attribute.
     * This is created only when first needed and will be formatted as a comma-separated list.
     */
    private Set<String> restrictions;

    /**
     * The vocabulary of the {@linkplain #keywords}, or {@code null} if not yet determined.
     * This field is used both for providing the content of the {@value #VOCABULARY} attribute,
     * and for filtering the keywords in order to retain only the ones using the same vocabulary.
     */
    private String vocabulary;

    /**
     * The union of all geographic extent, or {@code null} if unknown.
     */
    private DefaultGeographicBoundingBox bbox;

    /**
     * The name of the next attribute value to set in a call to {@link #setAttribute(String)}.
     */
    private transient String attributeName;

    /**
     * The object to use for formatting date, created when first needed.
     */
    private transient DateFormatter dateFormatter;

    /**
     * Creates a new <cite>ISO to NetCDF</cite> mapper for the given file.
     *
     * @param file  The NetCDF file where to write metadata.
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageWriter} instance
     *              using this encoder, or {@code null}.
     */
    public NetcdfMetadataWriter(final NetcdfFileWriteable file, final WarningProducer owner) {
        super(owner);
        ArgumentChecks.ensureNonNull("file", file);
        this.file  = file;
        defined = new HashSet<>();
    }

    /**
     * Adds the given element in the given set, if non-null. If the given set is null,
     * then a new set will be created and returned.
     */
    private static <E> Set<E> addTo(Set<E> set, final E element) {
        if (element != null) {
            if (set == null) {
                set = new LinkedHashSet<>();
            }
            set.add(element);
        }
        return set;
    }

    /**
     * Returns the given collection if non-null, or an empty set otherwise.
     */
    private static <E> Collection<E> nonNull(Collection<E> collection) {
        if (collection == null) {
            collection = Collections.emptySet();
        }
        return collection;
    }

    /**
     * Formats the given units as a NetCDF unit. This method handles a few units in a special way
     * in order to match the NetCDF conventions. For example the {@linkplain NonSI#DEGREE_ANGLE
     * angular degrees} are formatted as {@code "degrees"} instead than {@code "°"}.
     *
     * @param  unit The unit to format, or {@code null}.
     * @return A string representation of the given units,
     *         or {@code null} if the given unit was null.
     */
    private static String toString(final Unit<?> unit) {
        if (unit == null) {
            return null;
        }
        if (unit.equals(NonSI.DEGREE_ANGLE)) {
            return "degrees";
        }
        return unit.toString();
    }

    /**
     * Returns the first non-null localized text in the given collection.
     */
    private String toString(final Collection<? extends InternationalString> elements) {
        for (final InternationalString element : nonNull(elements)) {
            final String text = toString(element);
            if (text != null) {
                return text;
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the given text if non-null and non-empty,
     * or {@code null} otherwise.
     */
    private String toString(final InternationalString text) {
        if (text != null) {
            String s = text.toString(getLocale());
            if (s != null && !((s = s.trim()).isEmpty())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Adds the given ({@linkplain #attributeName}, <var>value</var>) pair to the global attributes.
     * If the given value is {@code null}, then this method does nothing and returns {@code false}.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final InternationalString value) throws IOException {
        return (value != null) && setAttribute(value.toString(getLocale()));
    }

    /**
     * Adds the ({@linkplain #attributeName}, <var>title</var>) pair to the global attributes.
     * If the given value is {@code null}, then this method does nothing and returns {@code false}.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final Citation value) throws IOException {
        return (value != null) && setAttribute(value.getTitle());
    }

    /**
     * Adds the given ({@linkplain #attributeName}, <var>code</var>) pair to the global attributes.
     * If the given identifier is {@code null}, then this method does nothing and returns {@code false}.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final Identifier identifier) throws IOException {
        return (identifier != null) && setAttribute(identifier.getCode());
    }

    /**
     * Adds the given ({@linkplain #attributeName}, <var>code</var>) pair to the global attributes.
     * If the given code is {@code null}, then this method does nothing and returns {@code false}.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final CodeList<?> code) throws IOException {
        return (code != null) && setAttribute(CodeLists.identifier(code));
    }

    /**
     * Adds the ({@linkplain #attributeName}, <var>date</var>) pair to the global attributes.
     * If the given value is {@code null}, then this method does nothing and returns {@code false}.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final Date date) throws IOException {
        if (date == null) {
            return false;
        }
        if (dateFormatter == null) {
            dateFormatter = new DateFormatter();
        }
        return setAttribute(dateFormatter.toDateTimeString(date));
    }

    /**
     * Adds the given ({@linkplain #attributeName}, <var>value</var>) pair to the global attributes.
     * If the protected {@linkplain #setAttribute(String, String)} method returns {@code false},
     * then this method does nothing.
     *
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    private boolean setAttribute(final String value) throws IOException {
        final String key = attributeName;
        if (!setAttribute(key, value)) {
            return false;
        }
        if (!defined.add(key)) { // Must be 'key' even if subclass used a different attribute name.
            throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, key));
        }
        return true;
    }

    /**
     * Adds the given (<var>key</var>, <var>value</var>) pair to the global attributes.
     * If the given value is {@code null} or {@linkplain String#isEmpty() empty} (ignoring
     * leading and trailing spaces), then this method does nothing and returns {@code false}.
     * <p>
     * This method is invoked for every non-numerical attributes to be defined in the NetCDF file.
     * Subclasses can override this method if they want to alter the values, store it under a
     * different key, or skip this value.
     *
     * @param  key   The key of the attribute to add.
     * @param  value The attribute value to add, or {@code null} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    protected boolean setAttribute(final String key, String value) throws IOException {
        if (value == null || (value = value.trim()).isEmpty()) {
            return false;
        }
        file.addGlobalAttribute(key, value);
        return true;
    }

    /**
     * Adds the given (<var>key</var>, <var>value</var>) pair to the global attributes.
     * If the given value is {@code NaN}, then this method does nothing and returns
     * {@code false}.
     * <p>
     * This method is invoked for every numerical attributes to be defined in the NetCDF file.
     * Subclasses can override this method if they want to alter the values, store it under a
     * different key, or skip this value.
     *
     * @param  key   The key of the attribute to add.
     * @param  value The attribute value to add, or {@code NaN} if none.
     * @return {@code true} if the value has been added, or {@code false} otherwise.
     * @throws IOException If an I/O operation was required and failed.
     */
    protected boolean setAttribute(final String key, final double value) throws IOException {
        if (Double.isNaN(value)) {
            return false;
        }
        file.addGlobalAttribute(key, value);
        return true;
    }

    /**
     * Returns {@code true} if an attribute value is already defined for the given key.
     * This method sets the {@link #attributeName} field to the given key, so one of the private
     * {@code setAttribute(...)} methods can be invoked right after for setting the actual value.
     * <p>
     * As a special case, this method returns {@code true} if the given key is null. This should
     * cause {@code NetcdfMetadataWriter} to skip the ISO 19115 metadata that are not associated
     * to NetCDF attributes, which are indicated by null values in the {@link Responsible} object
     * for instance.
     */
    private boolean isDefined(final String key) {
        attributeName = key;
        return (key == null) || defined.contains(key);
    }

    /**
     * Writes the given creator, contributor or publisher in the given set of attributes.
     *
     * @param  author The responsible party to write.
     * @param  role The set of attribute where to write the responsible party.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void write(final ResponsibleParty author, final Responsible role) throws IOException {
        if (author == null) {
            return;
        }
        if (!isDefined(role.NAME)) {
            setAttribute(author.getIndividualName());
        }
        if (!isDefined(role.INSTITUTION)) {
            setAttribute(author.getOrganisationName());
        }
        final Contact contact = author.getContactInfo();
        if (contact != null) {
            if (!isDefined(role.URL)) {
                final OnlineResource resource = contact.getOnlineResource();
                if (resource != null) {
                    final URI linkage = resource.getLinkage();
                    if (linkage != null) {
                        setAttribute(linkage.toString());
                    }
                }
            }
            if (!isDefined(role.EMAIL)) {
                final Address address = contact.getAddress();
                if (address != null) {
                    for (final String mail : nonNull(address.getElectronicMailAddresses())) {
                        if (setAttribute(mail)) {
                            break;
                        }
                    }
                }
            }
        }
        if (!isDefined(role.ROLE)) {
            setAttribute(author.getRole());
        }
    }

    /**
     * Writes the {@value #IDENTIFIER}, {@value #TITLE}, {@value #SUMMARY}, {@value #DATE_CREATED},
     * {@value #DATA_TYPE} and more attributes from the given identification info.
     * <p>
     * This method also build the set of {@linkplain #keywords} and {@linkplain #licenses}
     * information, but does not write their content. The caller is responsible for writing
     * the content of the above-cited fields after this method call.
     *
     * @param  info The identification info to write, or {@code null} if none.
     * @param  fileIdentifier The file identifier (used only as a fallback), or {@code null}.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void write(final Identification info, String fileIdentifier) throws IOException {
        if (info == null) {
            return;
        }
        final boolean isData = (info instanceof DataIdentification);
        final Citation citation = info.getCitation();
        if (!isDefined(IDENTIFIER)) {
            if (citation != null) {
                for (final Identifier id : nonNull(citation.getIdentifiers())) {
                    if (setAttribute(id)) {
                        // Unconditionally set the naming authority
                        // in order to be consistent with the code.
                        attributeName = NAMING_AUTHORITY;
                        setAttribute(id.getAuthority());
                        fileIdentifier = null;
                        break;
                    }
                }
            }
            if (fileIdentifier != null) {
                setAttribute(fileIdentifier);
            }
        }
        if (!isDefined(TITLE)) {
            setAttribute(citation);
        }
        if (!isDefined(SUMMARY)) {
            setAttribute(info.getAbstract());
        }
        if (!isDefined(PURPOSE)) {
            setAttribute(info.getPurpose());
        }
        if (!isDefined(COMMENT) && isData) {
            setAttribute(((DataIdentification) info).getSupplementalInformation());
        }
        /*
         * There is 3 types of keyword which are recognized by this code.
         * All other keyword types are silently ignored.
         *
         *   1) The first keyword of type "project" is stored in the "project" attribute.
         *      This attribute will appear below the above title, summary and comments ones.
         *
         *   2) The first keyword of type "dataCenter" is saved in the 'dataCenter' variable
         *      in order to be used as a fallback later if no publisher were found.
         *
         *   3) All keywords of type "theme" are saved in a 'keywords' set, for later processing.
         *      The caller will need to write the "keywords" attribute himself, preferably right
         *      after the identification info in order to keep the keywords close to the topic
         *      category.
         */
        String dataCenter = null;
        for (final Keywords kset : nonNull(info.getDescriptiveKeywords())) {
            final KeywordType type = kset.getType();
            if (type != null) {
                if (type.equals(KeywordType.THEME)) {
                    final Citation vk = kset.getThesaurusName();
                    if (vk != null) {
                        final String title = toString(vk.getTitle());
                        if (title != null) {
                            if (vocabulary == null) {
                                vocabulary = title;
                            } else if (!title.equalsIgnoreCase(vocabulary)) {
                                continue;
                            }
                        }
                    }
                    for (final InternationalString keyword : kset.getKeywords()) {
                        keywords = addTo(keywords, toString(keyword));
                    }
                } else {
                    final String[] names = type.names();
                    if (!isDefined(PROJECT) && XArrays.containsIgnoreCase(names, "project")) {
                        setAttribute(toString(kset.getKeywords()));
                    }
                    if (dataCenter == null && XArrays.containsIgnoreCase(names, "dataCenter")) {
                        dataCenter = toString(kset.getKeywords());
                    }
                }
            }
        }
        /*
         * Write the author information and data creation/revision/publishing dates.
         * If an author role is unspecified, then it will be assumed to be the creator
         * unless a creator has already been found, in which case the unspecified role
         * will be assumed to be a contributor.
         */
        if (citation != null) {
            if (!isDefined(REFERENCES)) {
                setAttribute(citation.getOtherCitationDetails());
            }
            final Collection<? extends ResponsibleParty> authors = nonNull(citation.getCitedResponsibleParties());
            if (!authors.isEmpty()) {
                boolean foundCreator = false;
                final List<ResponsibleParty> deferred = new ArrayList<>(authors.size());
                for (final ResponsibleParty author : authors) {
                    if (author != null) {
                        if (Role.ORIGINATOR.equals(author.getRole())) {
                            write(author, CREATOR);
                            foundCreator = true;
                        } else {
                            deferred.add(author);
                        }
                    }
                }
                for (final ResponsibleParty author : deferred) {
                    write(author, Role.PUBLISHER.equals(author.getRole()) ?
                            PUBLISHER : foundCreator ? CONTRIBUTOR : CREATOR);
                }
            }
            if (!isDefined(PUBLISHER.NAME)) {
                setAttribute(dataCenter); // Possible fallback extracted from the keywords.
            }
            if (!isDefined(ACKNOWLEDGMENT)) {
                Collection<String> credits = nonNull(info.getCredits());
                if (credits.size() >= 2) {
                    credits = new LinkedHashSet<>(credits); // Avoid duplicated values.
                }
                setAttribute(Strings.formatList(credits, "\n"));
            }
            int undefined = 0;
            if (!isDefined(DATE_CREATED))  undefined |= 1;
            if (!isDefined(DATE_MODIFIED)) undefined |= 2;
            if (!isDefined(DATE_ISSUED))   undefined |= 4;
            if (undefined != 0) {
nextDate:       for (final CitationDate date : nonNull(citation.getDates())) {
                    final DateType type = date.getDateType();
                    for (int flag=1; ; flag <<= 1) {
                        if ((undefined & flag) != 0) {
                            final DateType forType;
                            switch (flag) {
                                case 1: forType=DateType.CREATION;    attributeName=DATE_CREATED;  break;
                                case 2: forType=DateType.REVISION;    attributeName=DATE_MODIFIED; break;
                                case 4: forType=DateType.PUBLICATION; attributeName=DATE_ISSUED;   break;
                                default: continue nextDate;
                            }
                            if (forType.equals(type)) {
                                if (setAttribute(date.getDate())) {
                                    if ((undefined &= ~flag) == 0) {
                                        break nextDate; // Continuing the loop would be useless.
                                    }
                                }
                                break; // No need to compare the others DateType.
                            }
                        }
                    }
                }
            }
        }
        /*
         * Write data type and topic category last in order to keep them close to the keywords,
         * which should be written by the caller soon after this write(Identification) method call.
         */
        if (isData) {
            final DataIdentification dataInfo = (DataIdentification) info;
            if (!isDefined(DATA_TYPE)) {
                for (final SpatialRepresentationType type : nonNull(dataInfo.getSpatialRepresentationTypes())) {
                    if (setAttribute(type)) break; // Write only the first type.
                }
            }
            if (!isDefined(TOPIC_CATEGORY)) {
                for (final TopicCategory topic : nonNull(dataInfo.getTopicCategories())) {
                    if (setAttribute(topic)) break; // Write only the first topic.
                }
            }
        }
        /*
         * Following code store license information, but do not write them yet.
         * They will be written by the caller.
         */
        for (final Constraints constraint : nonNull(info.getResourceConstraints())) {
            for (final InternationalString c : nonNull(constraint.getUseLimitations())) {
                licenses = addTo(licenses, toString(c));
            }
            if (constraint instanceof LegalConstraints) {
                for (final Restriction r : nonNull(((LegalConstraints) constraint).getAccessConstraints())) {
                    restrictions = addTo(restrictions, CodeLists.identifier(r));
                }
            }
        }
    }

    /**
     * Writes the NetCDF attributes for the given extent information.
     * This method does not write bounding box information immediately, but instead stores the
     * information in the {@link #bbox} field. It is caller responsibility to write that field
     * after this method invocation.
     *
     * @param  content The extent information to write, or {@code null}.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void write(final Extent extent) throws IOException {
        if (extent == null) {
            return;
        }
        boolean hasIdentifier = isDefined(GEOGRAPHIC_IDENTIFIER);
        for (final GeographicExtent geo : nonNull(extent.getGeographicElements())) {
            if (!hasIdentifier && (geo instanceof GeographicDescription)) {
                hasIdentifier = setAttribute(((GeographicDescription) geo).getGeographicIdentifier());
            }
            if (geo instanceof GeographicBoundingBox) {
                final GeographicBoundingBox b = (GeographicBoundingBox) geo;
                if (!Boolean.FALSE.equals(b.getInclusion())) {
                    if (bbox == null) {
                        bbox = new DefaultGeographicBoundingBox(b);
                    } else {
                        bbox.add(b);
                    }
                }
            }
        }
    }

    /**
     * Writes the given axis bounds in the given set of attributes.
     *
     * @param  dim The set of attribute where to write the axis bounds.
     * @throws IOException If an I/O operation was required and failed.
     */
    private void write(final Dimension dim, final double min, final double max, final Unit<?> unit)
            throws IOException
    {
        setAttribute(dim.MINIMUM, min);
        setAttribute(dim.MAXIMUM, max);
        setAttribute(dim.UNITS, toString(unit));
    }

    /**
     * Writes NetCDF attribute values for the given metadata object.
     * If this method is invoked more than once, then subsequent invocations will add
     * the values of new attributes but will not alter the old attributes, unless the
     * values can be appended (e.g. in a comma-separated list).
     *
     * @param  metadata The metadata object to write, or {@code null}.
     * @throws IOException If an error occurred while writing the attribute values.
     */
    public void write(final Metadata metadata) throws IOException {
        if (metadata == null) {
            return;
        }
        final String fileIdentifier = metadata.getFileIdentifier();
        for (final Identification info : nonNull(metadata.getIdentificationInfo())) {
            write(info, fileIdentifier);
        }
        /*
         * Unconditionally write the "keywords" attribute, overwriting the old attribute if any.
         * This is okay because we accumulated the keywords in a class field, so the previous
         * attribute values are not lost. Similar argument applies to the legal information.
         */
        if (setAttribute(KEYWORDS, Strings.formatList(keywords, ", "))) {
            setAttribute(VOCABULARY, vocabulary); // Must be consistent with the keywords.
        }
        if (!isDefined(PROCESSING_LEVEL)) {
            for (final ContentInformation content : nonNull(metadata.getContentInfo())) {
                if (content instanceof ImageDescription) {
                    if (setAttribute(((ImageDescription) content).getProcessingLevelCode())) break;
                }
            }
        }
        /*
         * Computes, then write the geographic extent.
         */
        for (final Identification info : nonNull(metadata.getIdentificationInfo())) {
            if (info instanceof DataIdentification) {
                for (final Extent extent : nonNull(((DataIdentification) info).getExtents())) {
                    write(extent);
                }
            }
        }
        if (bbox != null) {
            write(LATITUDE,  bbox.getSouthBoundLatitude(), bbox.getNorthBoundLatitude(), null);
            write(LONGITUDE, bbox.getWestBoundLongitude(), bbox.getEastBoundLongitude(), null);
        }
        /*
         * Unconditionally write the legal information. This is okay because we accumulated
         * those information in class fields, so the previous attribute values are not lost.
         */
        setAttribute(LICENSE, Strings.formatList(licenses, "\n"));
        setAttribute(ACCESS_CONSTRAINT, Strings.formatList(restrictions, ", "));
        /*
         * Write history-related information last.
         */
        if (!isDefined(METADATA_CREATION)) {
            setAttribute(metadata.getDateStamp());
        }
        if (!isDefined(HISTORY)) {
            final StringBuilder history = new StringBuilder(80);
            for (final DataQuality quality : nonNull(metadata.getDataQualityInfo())) {
                final Lineage lineage = quality.getLineage();
                if (lineage != null) {
                    if (history.length() != 0) {
                        history.append('\n');
                    }
                    final String s = toString(lineage.getStatement());
                    if (s != null) {
                        history.append(s);
                    }
                }
            }
            if (history.length() == 0) {
                history.append("Created by Geotoolkit.org version ").append(Version.GEOTOOLKIT);
            }
            setAttribute(history.toString());
        }
    }
}
