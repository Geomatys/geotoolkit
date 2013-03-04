/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.logging.Level;
import java.io.IOException;
import javax.measure.unit.Unit;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;

import ucar.nc2.Group;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.VariableIF;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants.CF;
import ucar.nc2.units.DateUnit;
import ucar.nc2.units.DateFormatter;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.extent.Extent;
import org.opengis.util.InternationalString;
import org.opengis.util.NameFactory;

import org.apache.sis.measure.Units;
import org.geotoolkit.util.Strings;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.image.io.WarningProducer;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.internal.image.io.NetcdfVariable;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.*;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.content.DefaultBand;
import org.geotoolkit.metadata.iso.content.DefaultRangeElementDescription;
import org.geotoolkit.metadata.iso.content.DefaultCoverageDescription;
import org.geotoolkit.metadata.iso.content.DefaultImageDescription;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.metadata.iso.distribution.DefaultDistributor;
import org.geotoolkit.metadata.iso.distribution.DefaultDistribution;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicDescription;
import org.geotoolkit.metadata.iso.extent.DefaultVerticalExtent;
import org.geotoolkit.metadata.iso.extent.DefaultTemporalExtent;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.referencing.adapters.NetcdfCRSBuilder;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.SimpleInternationalString.wrap;


/**
 * Mapping from NetCDF metadata to ISO 19115-2 metadata. The {@link String} constants declared in
 * the {@linkplain NetcdfMetadata parent class} are the name of attributes examined by this class.
 * The attribute values are extracted using the {@link NetcdfFile#findGlobalAttributeIgnoreCase(String)}
 * or {@link Group#findAttributeIgnoreCase(String)} methods. The current implementation searches the
 * attribute values in the following places, in that order:
 * <p>
 * <ol>
 *   <li>{@code "NCISOMetadata"} group</li>
 *   <li>{@code "CFMetadata"} group</li>
 *   <li>Global attributes</li>
 *   <li>{@code "THREDDSMetadata"} group</li>
 * </ol>
 * <p>
 * The {@code "CFMetadata"} group has precedence over the global attributes because the
 * {@linkplain #LONGITUDE longitude} and {@linkplain #LATITUDE latitude} resolutions are
 * often more accurate in that group.
 *
 * {@section Known limitations}
 * <ul>
 *   <li>{@code "degrees_west"} and {@code "degrees_south"} units not correctly handled</li>
 *   <li>Units of measurement not yet declared in the {@link Band} elements.</li>
 *   <li>{@link #FLAG_VALUES} and {@link #FLAG_MASKS} not yet included in the
 *       {@link RangeElementDescription} elements.</li>
 *   <li>Services (WMS, WCS, OPeNDAP, THREDDS) <i>etc.</i>) and transfer options not yet declared.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfMetadataReader extends NetcdfMetadata {
    /**
     * Names of groups where to search for metadata, in precedence order.
     * The {@code null} value stands for global attributes.
     * <p>
     * REMINDER: if modified, update class javadoc too.
     */
    private static final String[] GROUP_NAMES = {"NCISOMetadata", "CFMetadata", null, "THREDDSMetadata"};

    /**
     * The NetCDF file from which to extract ISO metadata.
     * This file is set at construction time.
     * <p>
     * This {@code NetcdfMetadataReader} class does <strong>not</strong> close this file.
     * Closing this file after usage is the user responsibility.
     */
    protected final NetcdfFile file;

    /**
     * The groups where to look for metadata, in precedence order. The first group shall be
     * {@code null}, which stands for global attributes. All other groups shall be non-null
     * values for the {@code "NCISOMetadata"}, {@code "THREDDSMetadata"} and {@code "CFMetadata"}
     * groups, if they exist.
     */
    private final Group[] groups;

    /**
     * The object to use for parsing dates, created when first needed.
     */
    private transient DateFormatter dateFormat;

    /**
     * The name factory, created when first needed.
     */
    private transient NameFactory nameFactory;

    /**
     * The contact, used at metadata creation time for avoiding to construct identical objects
     * more than once.
     *
     * <p>The point of contact is stored in two places. The semantic of those two methods is not
     * strictly identical, but the distinction is not used in NetCDF file.</p>
     * <ul>
     *   <li>{@link DefaultMetadata#getContacts()}</li>
     *   <li>{@link DefaultDataIdentification#getPointOfContacts()}</li>
     * </ul>
     * <p>An object very similar is used as the creator. The point of contact and the creator
     * are practically identical except for their role attribute.</p>
     */
    private transient ResponsibleParty pointOfContact;

    /**
     * Creates a new <cite>NetCDF to ISO</cite> mapper for the given file. While this constructor
     * accepts arbitrary {@link NetcdfFile} instance, the {@link NetcdfDataset} subclass is
     * necessary in order to get coordinate system information.
     *
     * @param file  The NetCDF file from which to parse metadata.
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageReader} instance
     *              using this decoder, or {@code null}.
     */
    public NetcdfMetadataReader(final NetcdfFile file, final WarningProducer owner) {
        super(owner);
        ArgumentChecks.ensureNonNull("file", file);
        this.file  = file;
        final Group[] groups = new Group[GROUP_NAMES.length];
        int count = 0;
        for (final String name : GROUP_NAMES) {
            if (name != null) {
                final Group group = file.findGroup(name);
                if (group == null) {
                    continue; // Group not found - do not increment the counter.
                }
                groups[count] = group;
            }
            count++;
        }
        this.groups = ArraysExt.resize(groups, count);
    }

    /**
     * Reports a warning.
     *
     * @param method    The method in which the warning occurred.
     * @param exception The exception to log.
     */
    private void warning(final String method, final Exception exception) {
        Warnings.log(this, Level.WARNING, NetcdfMetadataReader.class, method, exception);
    }

    /**
     * Returns the NetCDF attribute of the given name in the given group, or {@code null} if none.
     * This method is invoked for every global and group attributes to be read by this class (but
     * not {@linkplain VariableSimpleIF variable} attributes), thus providing a single point where
     * subclasses can filter the attributes to be read. The {@code name} argument is typically (but
     * is not restricted too) one of the constants defined in this class.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search (can not be null).
     * @return The attribute, or {@code null} if none.
     */
    protected Attribute getAttribute(final Group group, final String name) {
        return (group != null) ? group.findAttributeIgnoreCase(name) : file.findGlobalAttributeIgnoreCase(name);
    }

    /**
     * Returns the attribute of the given name in the given group, as a string.
     * This method considers empty strings as {@code null}.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none or empty.
     */
    private String getStringValue(final Group group, final String name) {
        if (name != null) { // For createResponsibleParty(...) convenience.
            final Attribute attribute = getAttribute(group, name);
            if (attribute != null && attribute.isString()) {
                String value = attribute.getStringValue();
                if (value != null && !(value = value.trim()).isEmpty()) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Returns the attribute of the given name, searching in all groups.
     *
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private String getStringValue(final String name) {
        for (final Group group : groups) {
            final String value = getStringValue(group, name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Returns the attribute of the given name in the given group, as a number.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none or unparseable.
     */
    private Number getNumericValue(final Group group, final String name) {
        final Attribute attribute = getAttribute(group, name);
        if (attribute != null) {
            Number value = attribute.getNumericValue();
            if (value == null) {
                String asString = attribute.getStringValue();
                if (asString != null) {
                    asString = asString.trim();
                    final int s = asString.indexOf(' ');
                    if (s >= 0) {
                        // Sometime, numeric values as string are followed by
                        // a unit of measurement. We ignore that unit for now...
                        asString = asString.substring(0, s);
                    }
                    try {
                        value = Double.valueOf(asString);
                    } catch (NumberFormatException e) {
                        warning("getNumericValue", e);
                    }
                }
            }
            return value;
        }
        return null;
    }

    /**
     * Returns the attribute of the given name, searching in all groups.
     *
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private Number getNumericValue(final String name) {
        for (final Group group : groups) {
            final Number value = getNumericValue(group, name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Returns the attribute of the given name in the given group, as a date.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none or unparseable.
     */
    private Date getDateValue(final Group group, final String name) {
        final String date = getStringValue(group, name);
        if (date != null) {
            if (dateFormat == null) {
                dateFormat = new DateFormatter();
            }
            final Date result = dateFormat.getISODate(date);
            if (result == null) {
                Warnings.log(this, Level.WARNING, NetcdfMetadataReader.class, "getDateValue",
                        Errors.Keys.UNPARSABLE_ATTRIBUTE_$2, name, date);
            }
            return result;
        }
        return null;
    }

    /**
     * Returns the attribute of the given name, searching in all groups.
     *
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private Date getDateValue(final String name) {
        for (final Group group : groups) {
            final Date value = getDateValue(group, name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Returns the attribute of the given name in the given group, as a unit of measurement.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none or unparseable.
     *
     * @todo Current Units.valueOf(String) implementation ignore direction in "degrees_east" or
     *       "degrees_west". We need to take that in account (with "degrees_west" to "degrees_east"
     *       converter that reverse the sign).
     */
    private Unit<?> getUnitValue(final Group group, final String name) {
        final String unit = getStringValue(group, name);
        if (unit != null) try {
            return Units.valueOf(unit);
        } catch (IllegalArgumentException e) {
            warning("getUnitValue", e);
        }
        return null;
    }

    /**
     * Adds the given element in the given collection if the element is not already present in the
     * collection. We define this method because the metadata API use collections while the Geotk
     * implementation uses lists. The lists are usually very short (typically 0 or 1 element), so
     * the call to {@link List#contains(Object)} should be cheap.
     */
    private static <T> void addIfAbsent(final Collection<T> collection, final T element) {
        if (!collection.contains(element)) {
            collection.add(element);
        }
    }

    /**
     * Adds the given element in the given collection if the element is non-null.
     * If the element is non-null and the collection is null, a new collection is
     * created. The given collection, or the new collection if it has been created,
     * is returned.
     */
    private static <T> Set<T> addIfNonNull(Set<T> collection, final T element) {
        if (element != null) {
            if (collection == null) {
                collection = new LinkedHashSet<T>(4);
            }
            collection.add(element);
        }
        return collection;
    }

    /**
     * Returns {@code true} if the given NetCDF attribute is either null or equals to the
     * string value of the given metadata value.
     *
     * @param metadata  The value stored in the metadata object.
     * @param attribute The value parsed from the NetCDF file.
     */
    private static boolean isDefined(final CharSequence metadata, final String attribute) {
        return (attribute == null) || (metadata != null && metadata.toString().equals(attribute));
    }

    /**
     * Returns {@code true} if the given NetCDF attribute is either null or equals to one
     * of the values in the given collection.
     *
     * @param metadata  The value stored in the metadata object.
     * @param attribute The value parsed from the NetCDF file.
     */
    private static boolean isDefined(final Collection<String> metadata, final String attribute) {
        return (attribute == null) || metadata.contains(attribute);
    }

    /**
     * Returns {@code true} if the given URL is null, or if the given resource contains that URL.
     *
     * @param resource  The value stored in the metadata object.
     * @param url       The value parsed from the NetCDF file.
     */
    private static boolean isDefined(final OnlineResource resource, final String url) {
        return (url == null) || (resource != null && isDefined(resource.getLinkage().toString(), url));
    }

    /**
     * Returns {@code true} if the given email is null, or if the given address contains that email.
     *
     * @param address  The value stored in the metadata object.
     * @param email    The value parsed from the NetCDF file.
     */
    private static boolean isDefined(final Address address, final String email) {
        return (email == null) || (address != null && isDefined(address.getElectronicMailAddresses(), email));
    }

    /**
     * Creates an {@code OnlineResource} element if the given URL is not null. Since ISO 19115
     * declares the URL as a mandatory attribute, this method will ignore all other attributes
     * if the given URL is null.
     *
     * @param  url The URL (mandatory - if {@code null}, no resource will be created).
     * @return The online resource, or {@code null} if the URL was null.
     */
    private OnlineResource createOnlineResource(final String url) {
        if (url != null) try {
            final DefaultOnlineResource resource = new DefaultOnlineResource(new URI(url));
            resource.setProtocol("http");
            resource.setApplicationProfile("web browser");
            resource.setFunction(OnLineFunction.INFORMATION);
            return resource;
        } catch (URISyntaxException e) {
            warning("createOnlineResource", e);
        }
        return null;
    }

    /**
     * Creates an {@code Address} element if at least one of the given attributes is non-null.
     */
    private static Address createAddress(final String email) {
        if (email != null) {
            final DefaultAddress address = new DefaultAddress();
            address.getElectronicMailAddresses().add(email);
            return address;
        }
        return null;
    }

    /**
     * Creates a {@code Contact} element if at least one of the given attributes is non-null.
     */
    private static Contact createContact(final Address address, final OnlineResource url) {
        if (address != null || url != null) {
            final DefaultContact contact = new DefaultContact();
            contact.setAddress(address);
            contact.setOnlineResource(url);
            return contact;
        }
        return null;
    }

    /**
     * Returns a globally unique identifier for the current NetCDF {@linkplain #file}.
     * The default implementation builds the identifier from the following attributes:
     * <p>
     * <ul>
     *   <li>{@value #NAMING_AUTHORITY} used as the {@linkplain Identifier#getAuthority() authority}.</li>
     *   <li>{@value #IDENTIFIER}, or {@link NetcdfFile#getId()} if no identifier attribute was found.</li>
     * </ul>
     *
     * @return The globally unique identifier, or {@code null} if none.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private Identifier getFileIdentifier() throws IOException {
        String identifier = getStringValue(IDENTIFIER);
        if (identifier == null) {
            identifier = file.getId();
            if (identifier == null) {
                return null;
            }
        }
        final String namespace  = getStringValue(NAMING_AUTHORITY);
        return new DefaultIdentifier((namespace != null) ? new DefaultCitation(namespace) : null, identifier);
    }

    /**
     * Creates a {@code ResponsibleParty} element if at least one of the name, email or URL
     * attributes is defined.
     * <p>
     * Implementation note: this method tries to reuse the existing {@link #pointOfContact} instance,
     * or part of it, if it is suitable.
     *
     * @param  keys  The group of attribute names to use for fetching the values.
     * @param  group The group in which to read the attributes values.
     * @return The responsible party, or {@code null} if none.
     * @throws IOException If an I/O operation was necessary but failed.
     *
     * @see #CREATOR
     * @see #CONTRIBUTOR
     * @see #PUBLISHER
     */
    private ResponsibleParty createResponsibleParty(final Group group, final Responsible keys,
            final boolean isPointOfContact) throws IOException
    {
        final String individualName   = getStringValue(group, keys.NAME);
        final String organisationName = getStringValue(group, keys.INSTITUTION);
        final String email            = getStringValue(group, keys.EMAIL);
        final String url              = getStringValue(group, keys.URL);
        if (individualName == null && organisationName == null && email == null && url == null) {
            return null;
        }
        Role role = Types.forCodeName(Role.class, getStringValue(group, keys.ROLE), true);
        if (role == null) {
            role = isPointOfContact ? Role.POINT_OF_CONTACT : keys.DEFAULT_ROLE;
        }
        ResponsibleParty party    = pointOfContact;
        Contact          contact  = null;
        Address          address  = null;
        OnlineResource   resource = null;
        if (party != null) {
            contact = party.getContactInfo();
            if (contact != null) {
                address  = contact.getAddress();
                resource = contact.getOnlineResource();
            }
            if (!isDefined(resource, url)) {
                resource = null;
                contact  = null; // Clear the parents all the way up to the root.
                party    = null;
            }
            if (!isDefined(address, email)) {
                address = null;
                contact = null; // Clear the parents all the way up to the root.
                party   = null;
            }
            if (party != null) {
                if (!isDefined(party.getOrganisationName(), organisationName) ||
                    !isDefined(party.getIndividualName(),   individualName))
                {
                    party = null;
                }
            }
        }
        if (party == null) {
            if (contact == null) {
                if (address  == null) address  = createAddress(email);
                if (resource == null) resource = createOnlineResource(url);
                contact = createContact(address, resource);
            }
            if (individualName != null || organisationName != null || contact != null) { // Do not test role.
                final DefaultResponsibleParty np = new DefaultResponsibleParty(role);
                np.setIndividualName(individualName);
                np.setOrganisationName(wrap(organisationName));
                np.setContactInfo(contact);
                party = np;
            }
        }
        return party;
    }

    /**
     * Creates a {@code Citation} element if at least one of the required attributes
     * is non-null. This method will reuse the {@link #pointOfContact} field, if non-null.
     *
     * @param identifier The citation {@code <gmd:identifier> attribute.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private Citation createCitation(final Identifier identifier) throws IOException {
        String title = getStringValue(TITLE);
        if (title == null) {
            title = getStringValue("full_name"); // THREDDS attribute documented in TITLE javadoc.
            if (title == null) {
                title = getStringValue("name"); // THREDDS attribute documented in TITLE javadoc.
                if (title == null) {
                    title = file.getTitle();
                }
            }
        }
        final Date   creation   = getDateValue(DATE_CREATED);
        final Date   modified   = getDateValue(DATE_MODIFIED);
        final Date   issued     = getDateValue(DATE_ISSUED);
        final String references = getStringValue(REFERENCES);
        final DefaultCitation citation = new DefaultCitation(title);
        if (identifier != null) {
            citation.getIdentifiers().add(identifier);
        }
        if (creation != null) citation.getDates().add(new DefaultCitationDate(creation, DateType.CREATION));
        if (modified != null) citation.getDates().add(new DefaultCitationDate(modified, DateType.REVISION));
        if (issued   != null) citation.getDates().add(new DefaultCitationDate(issued,   DateType.PUBLICATION));
        if (pointOfContact != null) {
            // Same responsible party than the contact, except for the role.
            final DefaultResponsibleParty np = new DefaultResponsibleParty(Role.ORIGINATOR);
            np.setIndividualName  (pointOfContact.getIndividualName());
            np.setOrganisationName(pointOfContact.getOrganisationName());
            np.setContactInfo     (pointOfContact.getContactInfo());
            citation.getCitedResponsibleParties().add(np);
        }
        for (final Group group : groups) {
            final ResponsibleParty contributor = createResponsibleParty(group, CONTRIBUTOR, false);
            if (contributor != null && contributor != pointOfContact) {
                addIfAbsent(citation.getCitedResponsibleParties(), contributor);
            }
        }
        citation.setOtherCitationDetails(wrap(references));
        return citation.isEmpty() ? null : citation;
    }

    /**
     * Creates a {@code DataIdentification} element if at least one of the required attributes
     * is non-null. This method will reuse the {@link #pointOfContact} field, if non-null.
     *
     * @param identifier The citation {@code <gmd:identifier> attribute.
     * @param publisher  The publisher names, built by the caller in an opportunist way.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private DataIdentification createIdentificationInfo(final Identifier identifier,
            final Set<InternationalString> publisher) throws IOException
    {
        DefaultDataIdentification identification = null;
        Set<InternationalString>  project        = null;
        DefaultLegalConstraints   constraints    = null;
        boolean hasExtent = false;
        for (final Group group : groups) {
            final Keywords standard = createKeywords(group, KeywordType.THEME, true);
            final Keywords keywords = createKeywords(group, KeywordType.THEME, false);
            final String   topic    = getStringValue(group, TOPIC_CATEGORY);
            final String   type     = getStringValue(group, DATA_TYPE);
            final String   credits  = getStringValue(group, ACKNOWLEDGMENT);
            final String   license  = getStringValue(group, LICENSE);
            final String   access   = getStringValue(group, ACCESS_CONSTRAINT);
            final Extent   extent   = hasExtent ? null : createExtent(group);
            if (standard!=null || keywords!=null || topic != null || type!=null || credits!=null || license!=null || access!= null || extent!=null) {
                if (identification == null) {
                    identification = new DefaultDataIdentification();
                }
                if (topic    != null) addIfAbsent(identification.getTopicCategories(), Types.forCodeName(TopicCategory.class, topic, true));
                if (type     != null) addIfAbsent(identification.getSpatialRepresentationTypes(), Types.forCodeName(SpatialRepresentationType.class, type, true));
                if (standard != null) addIfAbsent(identification.getDescriptiveKeywords(), standard);
                if (keywords != null) addIfAbsent(identification.getDescriptiveKeywords(), keywords);
                if (credits  != null) addIfAbsent(identification.getCredits(), credits);
                if (license  != null) addIfAbsent(identification.getResourceConstraints(), constraints = new DefaultLegalConstraints(license));
                if (access   != null) {
                    for (final String token : Strings.split(access, ',')) {
                        if (!token.isEmpty()) {
                            if (constraints == null) {
                                identification.getResourceConstraints().add(constraints = new DefaultLegalConstraints());
                            }
                            addIfAbsent(constraints.getAccessConstraints(), Types.forCodeName(Restriction.class, token, true));
                        }
                    }
                }
                if (extent != null) {
                    // Takes only ONE extent, because a NetCDF file may declare many time the same
                    // extent with different precision. The groups are ordered in such a way that
                    // the first extent should be the most accurate one.
                    identification.getExtents().add(extent);
                    hasExtent = true;
                }
            }
            project = addIfNonNull(project, wrap(getStringValue(group, PROJECT)));
        }
        final Citation citation = createCitation(identifier);
        final String   summary  = getStringValue(SUMMARY);
        final String   purpose  = getStringValue(PURPOSE);
        if (identification == null) {
            if (citation==null && summary==null && purpose==null && project==null && publisher==null && pointOfContact==null) {
                return null;
            }
            identification = new DefaultDataIdentification();
        }
        identification.setCitation(citation);
        identification.setAbstract(wrap(summary));
        identification.setPurpose (wrap(purpose));
        if (pointOfContact != null) {
            identification.getPointOfContacts().add(pointOfContact);
        }
        addKeywords(identification, project,   "project"); // Not necessarily the same string than PROJECT.
        addKeywords(identification, publisher, "dataCenter");
        identification.setSupplementalInformation(wrap(getStringValue(COMMENT)));
        return identification;
    }

    /**
     * Adds the given keywords to the given identification info if the given set is non-null.
     */
    private static void addKeywords(final DefaultDataIdentification addTo,
            final Set<InternationalString> words, final String type)
    {
        if (words != null) {
            final DefaultKeywords keywords = new DefaultKeywords(words);
            keywords.setType(Types.forCodeName(KeywordType.class, type, true));
            addTo.getDescriptiveKeywords().add(keywords);
        }
    }

    /**
     * Returns the keywords if at least one required attribute is found, or {@code null} otherwise.
     *
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private Keywords createKeywords(final Group group, final KeywordType type, final boolean standard)
            throws IOException
    {
        final String list = getStringValue(group, standard ? STANDARD_NAME : KEYWORDS);
        DefaultKeywords keywords = null;
        if (list != null) {
            final Set<InternationalString> words = new LinkedHashSet<InternationalString>();
            for (String keyword : list.split(getKeywordSeparator(group))) {
                keyword = keyword.trim();
                if (!keyword.isEmpty()) {
                    words.add(wrap(keyword));
                }
            }
            if (!words.isEmpty()) {
                keywords = new DefaultKeywords(words);
                keywords.setType(type);
                final String vocabulary = getStringValue(group, standard ? STANDARD_NAME_VOCABULARY : VOCABULARY);
                if (vocabulary != null) {
                    keywords.setThesaurusName(new DefaultCitation(vocabulary));
                }
            }
        }
        return keywords;
    }

    /**
     * Returns the string to use as a keyword separator. This separator is used for parsing
     * the {@value org.geotoolkit.metadata.netcdf.NetcdfMetadata#KEYWORDS} attribute value.
     * The default implementation returns {@code ","}. Subclasses can override this method
     * in an other separator (possibly determined from the file content) is desired.
     *
     * @param  group The NetCDF group from which keywords are read.
     * @return The string to use as a keyword separator, as a regular expression.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    protected String getKeywordSeparator(final Group group) throws IOException {
        return ",";
    }

    /**
     * Creates a {@code <gmd:spatialRepresentationInfo>} element from the given NetCDF coordinate
     * system. Subclasses can override this method if they need to complete the information
     * provided in the returned object.
     *
     * @param  cs The NetCDF coordinate system.
     * @return The grid spatial representation info.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    @SuppressWarnings("fallthrough")
    protected GridSpatialRepresentation createSpatialRepresentationInfo(final CoordinateSystem cs) throws IOException {
        final DefaultGridSpatialRepresentation grid = new DefaultGridSpatialRepresentation();
        grid.setNumberOfDimensions(cs.getRankDomain());
        /*
         * The caller (which is the read() method) has verified that the file is an instance
         * of NetcdfDataset.
         */
        final NetcdfCRSBuilder builder = new NetcdfCRSBuilder((NetcdfDataset) file, owner);
        builder.setCoordinateSystem(cs);
        for (final Map.Entry<ucar.nc2.Dimension,CoordinateAxis> entry : builder.getAxesDomain().entrySet()) {
            final CoordinateAxis axis = entry.getValue();
            final int i = axis.getDimensions().indexOf(entry.getKey());
            Dimension rsat = null;
            Double resolution = null;
            final AxisType at = axis.getAxisType();
            if (at != null) {
                boolean valid = false;
                switch (at) {
                    case Lon:      valid = true; // fallthrough
                    case GeoX:     rsat  = LONGITUDE; break;
                    case Lat:      valid = true; // fallthrough
                    case GeoY:     rsat  = LATITUDE; break;
                    case Height:   valid = true; // fallthrough
                    case GeoZ:
                    case Pressure: rsat  = VERTICAL; break;
                    case Time:     valid = true; // fallthrough
                    case RunTime:  rsat  = TIME; break;
                }
                if (valid) {
                    final Number res = getNumericValue(rsat.RESOLUTION);
                    if (res != null) {
                        resolution = (res instanceof Double) ? (Double) res : res.doubleValue();
                    }
                }
            }
            final DefaultDimension dimension = new DefaultDimension();
            if (rsat != null) {
                dimension.setDimensionName(rsat.TYPE);
                dimension.setResolution(resolution);
            }
            dimension.setDimensionSize(axis.getShape(i));
            grid.getAxisDimensionProperties().add(dimension);
        }
        grid.setCellGeometry(CellGeometry.AREA);
        return grid;
    }

    /**
     * Returns the extent declared in the given group, or {@code null} if none.
     */
    private Extent createExtent(final Group group) {
        DefaultExtent extent = null;
        final Number xmin = getNumericValue(group, LONGITUDE.MINIMUM);
        final Number xmax = getNumericValue(group, LONGITUDE.MAXIMUM);
        final Number ymin = getNumericValue(group, LATITUDE .MINIMUM);
        final Number ymax = getNumericValue(group, LATITUDE .MAXIMUM);
        final Number zmin = getNumericValue(group, VERTICAL .MINIMUM);
        final Number zmax = getNumericValue(group, VERTICAL .MAXIMUM);
        if (xmin != null || xmax != null || ymin != null || ymax != null) {
            extent = new DefaultExtent();
            final UnitConverter cλ = getConverterTo(getUnitValue(group, LONGITUDE.UNITS), NonSI.DEGREE_ANGLE);
            final UnitConverter cφ = getConverterTo(getUnitValue(group, LATITUDE .UNITS), NonSI.DEGREE_ANGLE);
            extent.getGeographicElements().add(new DefaultGeographicBoundingBox(
                    valueOf(xmin, cλ), valueOf(xmax, cλ),
                    valueOf(ymin, cφ), valueOf(ymax, cφ)));
        }
        if (zmin != null || zmax != null) {
            if (extent == null) {
                extent = new DefaultExtent();
            }
            final UnitConverter c = getConverterTo(getUnitValue(group, VERTICAL.UNITS), SI.METRE);
            double min = valueOf(zmin, c);
            double max = valueOf(zmax, c);
            if (CF.POSITIVE_DOWN.equals(getStringValue(group, VERTICAL.POSITIVE))) {
                final double tmp = min;
                min = -max;
                max = -tmp;
            }
            extent.getVerticalElements().add(new DefaultVerticalExtent(min, max, DefaultVerticalCRS.GEOIDAL_HEIGHT));
        }
        /*
         * Temporal extent.
         */
        Date startTime = getDateValue(group, TIME.MINIMUM);
        Date endTime   = getDateValue(group, TIME.MAXIMUM);
        if (startTime == null && endTime == null) {
            final Number tmin = getNumericValue(group, TIME.MINIMUM);
            final Number tmax = getNumericValue(group, TIME.MAXIMUM);
            if (tmin != null || tmax != null) {
                final Attribute attribute = getAttribute(group, TIME.UNITS);
                if (attribute != null) {
                    final String symbol = attribute.getStringValue();
                    if (symbol != null) try {
                        final DateUnit unit = new DateUnit(symbol);
                        if (tmin != null) startTime = unit.makeDate(tmin.doubleValue());
                        if (tmax != null)   endTime = unit.makeDate(tmax.doubleValue());
                    } catch (Exception e) { // Declared by the DateUnit constructor.
                        warning("createExtent", e);
                    }
                }
            }
        }
        if (startTime != null || endTime != null) {
            if (extent == null) {
                extent = new DefaultExtent();
            }
            extent.getTemporalElements().add(new DefaultTemporalExtent(startTime, endTime));
        }
        final String identifier = getStringValue(GEOGRAPHIC_IDENTIFIER);
        if (identifier != null) {
            if (extent == null) {
                extent = new DefaultExtent();
            }
            extent.getGeographicElements().add(new DefaultGeographicDescription(identifier));
        }
        return extent;
    }

    /**
     * Returns the converter from the given source unit (which may be {@code null}) to the
     * given target unit, or {@code null} if none or incompatible.
     */
    private UnitConverter getConverterTo(final Unit<?> source, final Unit<?> target) {
        if (source != null) try {
            return source.getConverterToAny(target);
        } catch (ConversionException e) {
            warning("getConverterTo", e);
        }
        return null;
    }

    /**
     * Returns the values of the given number if non-null, or NaN if null. If the given
     * converter is non-null, it is applied.
     */
    private static double valueOf(final Number value, final UnitConverter converter) {
        double n = Double.NaN;
        if (value != null) {
            n = value.doubleValue();
            if (converter != null) {
                n = converter.convert(n);
            }
        }
        return n;
    }

    /**
     * Creates a {@code <gmd:contentInfo>} elements from all applicable NetCDF attributes.
     *
     * @return The content information.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private Collection<DefaultCoverageDescription> createContentInfo() throws IOException {
        final Map<List<ucar.nc2.Dimension>, DefaultCoverageDescription> contents =
                new HashMap<List<ucar.nc2.Dimension>, DefaultCoverageDescription>(4);
        final String processingLevel = getStringValue(PROCESSING_LEVEL);
        final List<? extends VariableIF> variables = file.getVariables();
        for (final VariableSimpleIF variable : variables) {
            if (!NetcdfVariable.isCoverage(variable, variables, 2)) {
                // Same exclusion criterion than the one applied in NetcdfImageReader.getImageNames().
                continue;
            }
            /*
             * Instantiate a CoverageDescription for each distinct set of NetCDF dimensions
             * (e.g. longitude,latitude,time). This separation is based on the fact that a
             * coverage has only one domain for every range of values.
             */
            final List<ucar.nc2.Dimension> vardim = variable.getDimensions();
            DefaultCoverageDescription content = contents.get(vardim);
            if (content == null) {
                /*
                 * If there is some NetCDF attributes that can be stored only in the ImageDescription
                 * subclass, instantiate that subclass. Otherwise instantiate the more generic class.
                 */
                if (processingLevel != null) {
                    content = new DefaultImageDescription();
                    ((DefaultImageDescription) content).setProcessingLevelCode(new DefaultIdentifier(processingLevel));
                } else {
                    content = new DefaultCoverageDescription();
                }
                contents.put(vardim, content);
            }
            content.getDimensions().add(createSampleDimension(variable));
            final Object[] names    = getSequence(variable, FLAG_NAMES,    false);
            final Object[] meanings = getSequence(variable, FLAG_MEANINGS, false);
            final Object[] masks    = getSequence(variable, FLAG_MASKS,    true);
            final Object[] values   = getSequence(variable, FLAG_VALUES,   true);
            final int length = Math.max(masks.length, Math.max(values.length, Math.max(names.length, meanings.length)));
            for (int i=0; i<length; i++) {
                final RangeElementDescription element = createRangeElementDescription(variable,
                        i < names   .length ? (String) names   [i] : null,
                        i < meanings.length ? (String) meanings[i] : null,
                        i < masks   .length ? (Number) masks   [i] : null,
                        i < values  .length ? (Number) values  [i] : null);
                if (element != null) {
                    content.getRangeElementDescriptions().add(element);
                }
            }
        }
        return contents.values();
    }

    /**
     * Returns the sequence of string values for the given attribute, or an empty array if none.
     */
    private static Object[] getSequence(final VariableSimpleIF variable, final String name, final boolean numeric) {
        final Attribute attribute = variable.findAttributeIgnoreCase(name);
        if (attribute != null) {
            boolean hasValues = false;
            final Object[] values = new Object[attribute.getLength()];
            for (int i=0; i<values.length; i++) {
                if (numeric) {
                    if ((values[i] = attribute.getNumericValue(i)) != null) {
                        hasValues = true;
                    }
                } else {
                    String value = attribute.getStringValue(i);
                    if (value != null && !(value = value.trim()).isEmpty()) {
                        values[i] = value.replace('_', ' ');
                        hasValues = true;
                    }
                }
            }
            if (hasValues) {
                return values;
            }
        }
        return Strings.EMPTY;
    }

    /**
     * Creates a {@code <gmd:dimension>} element from the given NetCDF variable. Subclasses can
     * override this method if they need to complete the information provided in the returned
     * object.
     *
     * @param  variable The NetCDF variable.
     * @return The sample dimension information.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    protected Band createSampleDimension(final VariableSimpleIF variable) throws IOException {
        final DefaultBand band = new DefaultBand();
        String name = variable.getShortName();
        if (name != null && !(name = name.trim()).isEmpty()) {
            if (nameFactory == null) {
                nameFactory = FactoryFinder.getNameFactory(null);
            }
            final StringBuilder type = new StringBuilder(variable.getDataType().getPrimitiveClassType().getSimpleName());
            for (int i=variable.getShape().length; --i>=0;) {
                type.append("[]");
            }
            // TODO: should be band.setName(...) with ISO 19115:2011.
            // Sequence identifiers are supposed to be numbers only.
            band.setSequenceIdentifier(nameFactory.createMemberName(null, name,
                    nameFactory.createTypeName(null, type.toString())));
        }
        String descriptor = variable.getDescription();
        if (descriptor != null && !(descriptor = descriptor.trim()).isEmpty() && !descriptor.equals(name)) {
            band.setDescriptor(wrap(descriptor));
        }
//TODO: Can't store the units, because the Band interface restricts it to length.
//      We need the SampleDimension interface proposed in ISO 19115 revision draft.
//      band.setUnits(Units.valueOf(variable.getUnitsString()));
        return band;
    }

    /**
     * Creates a {@code <gmd:rangeElementDescription>} elements from the given information.
     * <p>
     * <b>Note:</b> ISO 19115 range elements are approximatively equivalent to
     * {@link org.geotoolkit.coverage.Category} in the {@code geotk-coverage} module.
     *
     * @param  variable The NetCDF variable.
     * @param  name     One of the elements in the {@value #FLAG_NAMES} attribute, or {@code null}.
     * @param  meaning  One of the elements in the {@value #FLAG_MEANINGS} attribute or {@code null}.
     * @param  mask     One of the elements in the {@value #FLAG_MASKS} attribute or {@code null}.
     * @param  value    One of the elements in the {@value #FLAG_VALUES} attribute or {@code null}.
     * @return The sample dimension information or {@code null} if none.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private RangeElementDescription createRangeElementDescription(final VariableSimpleIF variable,
            final String name, final String meaning, final Number mask, final Number value) throws IOException
    {
        if (name != null && meaning != null) {
            final DefaultRangeElementDescription element = new DefaultRangeElementDescription();
            element.setName(wrap(name));
            element.setDefinition(wrap(meaning));
            // TODO: create a record from values (and possibly from the masks).
            //       if (pixel & mask == value) then we have that range element.
            return element;
        }
        return null;
    }

    /**
     * Creates an ISO {@code Metadata} object from the information found in the NetCDF file.
     *
     * @return The ISO metadata object.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    public Metadata read() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMetadataStandardName("ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data");
        metadata.setMetadataStandardVersion("ISO 19115-2:2009(E)");
        final Identifier identifier = getFileIdentifier();
        if (identifier != null) {
            String code = identifier.getCode();
            final Citation authority = identifier.getAuthority();
            if (authority != null) {
                final InternationalString title = authority.getTitle();
                if (title != null) {
                    code = title.toString() + DefaultNameSpace.DEFAULT_SEPARATOR + code;
                }
            }
            metadata.setFileIdentifier(code);
        }
        metadata.setDateStamp(getDateValue(METADATA_CREATION));
        metadata.getHierarchyLevels().add(ScopeCode.DATASET);
        final String wms = getStringValue("wms_service");
        final String wcs = getStringValue("wcs_service");
        if (wms != null || wcs != null) {
            metadata.getHierarchyLevels().add(ScopeCode.SERVICE);
        }
        /*
         * Add the ResponsibleParty which is declared in global attributes, or in
         * the THREDDS attributes if no information was found in global attributes.
         */
        for (final Group group : groups) {
            final ResponsibleParty party = createResponsibleParty(group, CREATOR, true);
            if (party != null && party != pointOfContact) {
                addIfAbsent(metadata.getContacts(), party);
                if (pointOfContact == null) {
                    pointOfContact = party;
                }
            }
        }
        /*
         * Add the publisher AFTER the creator, because this method may
         * reuse the 'creator' field (if non-null and if applicable).
         */
        Set<InternationalString> publisher = null;
        DefaultDistribution distribution   = null;
        for (final Group group : groups) {
            final ResponsibleParty party = createResponsibleParty(group, PUBLISHER, false);
            if (party != null) {
                if (distribution == null) {
                    distribution = new DefaultDistribution();
                    metadata.setDistributionInfo(distribution);
                }
                final DefaultDistributor distributor = new DefaultDistributor(party);
                // TODO: There is some transfert option, etc. that we could set there.
                // See UnidataDD2MI.xsl for options for OPeNDAP, THREDDS, etc.
                addIfAbsent(distribution.getDistributors(), distributor);
                publisher = addIfNonNull(publisher, wrap(party.getIndividualName()));
            }
            // Also add history.
            final String history = getStringValue(HISTORY);
            if (history != null) {
                final DefaultDataQuality quality = new DefaultDataQuality();
                final DefaultLineage lineage = new DefaultLineage();
                lineage.setStatement(wrap(history));
                quality.setLineage(lineage);
                addIfAbsent(metadata.getDataQualityInfo(), quality);
            }
        }
        /*
         * Add the identification info AFTER the responsible parties (both creator and publisher),
         * because this method will reuse the 'creator' and 'publisher' information (if non-null).
         */
        final DataIdentification identification = createIdentificationInfo(identifier, publisher);
        if (identification != null) {
            metadata.getIdentificationInfo().add(identification);
        }
        metadata.setContentInfo(createContentInfo());
        /*
         * Add the dimension information, if any. This metadata node
         * is built from the NetCDF CoordinateSystem objects.
         */
        if (file instanceof NetcdfDataset) {
            final NetcdfDataset ds = (NetcdfDataset) file;
            final EnumSet<NetcdfDataset.Enhance> mode = EnumSet.copyOf(ds.getEnhanceMode());
            if (mode.add(NetcdfDataset.Enhance.CoordSystems)) {
                ds.enhance(mode);
            }
            for (final CoordinateSystem cs : ds.getCoordinateSystems()) {
                if (cs.getRankDomain() >= NetcdfVariable.MIN_DIMENSION && cs.getRankRange() >= NetcdfVariable.MIN_DIMENSION) {
                    metadata.getSpatialRepresentationInfo().add(createSpatialRepresentationInfo(cs));
                }
            }
        }
        return metadata;
    }
}
