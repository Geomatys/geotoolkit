/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.image.io.metadata;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.logging.Level;
import java.text.ParseException;
import java.io.IOException;

import ucar.nc2.Group;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.constants.AxisType;
import ucar.unidata.util.DateUtil;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.Address;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.Strings;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.naming.DefaultNameFactory;
import org.geotoolkit.internal.CodeLists;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;
import org.geotoolkit.metadata.iso.content.DefaultBand;
import org.geotoolkit.metadata.iso.content.DefaultRangeElementDescription;
import org.geotoolkit.metadata.iso.content.DefaultCoverageDescription;

import static org.geotoolkit.util.SimpleInternationalString.wrap;


/**
 * Mapping from NetCDF metadata to ISO 19115-2 metadata.
 * The mapping is defined in the following web pages:
 * <p>
 * <ul>
 *   <li><a href="https://geo-ide.noaa.gov/wiki/index.php?title=NetCDF_Attribute_Convention_for_Dataset_Discovery">NetCDF
 *       Attribute Convention for Dataset Discovery</a> wiki</li>
 *   <li><a href="http://ngdc.noaa.gov/metadata/published/xsl/nciso2.0/UnidataDD2MI.xsl">UnidataDD2MI.xsl</a> file</li>
 * </ul>
 * <p>
 * The {@link String} constants declared in this class are the name of attributes examined by this class.
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
 * {@linkplain #LONGITUDE_RESOLUTION longitude resolution} and {@linkplain #LATITUDE_RESOLUTION
 * latitude resolution} are often more accurate in that group.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfISO {
    // TODO: document link to ISO metadata when we will implement writer.
    /**
     * The {@value} attribute name for a short description of the dataset
     * (<em>Highly Recommended</em>).
     */
    public static final String TITLE = "title";

    /**
     * The {@value} attribute name for a paragraph describing the dataset
     * (<em>Highly Recommended</em>).
     */
    public static final String SUMMARY = "summary";

    /**
     * The {@value} attribute name for an identifier (<em>Recommended</em>).
     * The combination of the {@value #NAMING_AUTHORITY} and the {@value}
     * should be a globally unique identifier for the dataset.
     */
    public static final String IDENTIFIER = "id";

    /**
     * The {@value} attribute name for a long descriptive name for the variable taken from a controlled
     * vocabulary of variable names. This is actually a {@linkplain VariableSimpleIF variable} attribute,
     * but sometime appears also in {@linkplain NetcdfFile#findGlobalAttribute(String) global attributes}.
     */
    public static final String STANDARD_NAME = "standard_name";

    /**
     * The {@value} attribute name for the identifier authority (<em>Recommended</em>).
     * The combination of the {@value} and the {@value #IDENTIFIER} should be a globally
     * unique identifier for the dataset.
     */
    public static final String NAMING_AUTHORITY = "naming_authority";

    /**
     * The {@value} attribute name for a comma separated list of key words and phrases
     * (<em>Highly Recommended</em>).
     *
     * @see #getKeywordSeparator(Group)
     */
    public static final String KEYWORDS = "keywords";

    /**
     * The {@value} attribute name for the guideline for the words/phrases in the
     * {@value #KEYWORDS} attribute (<em>Recommended</em>).
     */
    public static final String VOCABULARY = "keywords_vocabulary";

    /**
     * The {@value} attribute name for miscellaneous information about the data
     * (<em>Recommended</em>).
     */
    public static final String COMMENT = "comment";

    /**
     * The {@value} attribute name for the date on which the metadata was created
     * (<em>Suggested</em>). This is actually defined in the "{@code NCISOMetadata}"
     * subgroup.
     */
    public static final String METADATA_CREATION = "metadata_creation";

    /**
     * The {@value} attribute name for the date on which the data was created
     * (<em>Recommended</em>).
     */
    public static final String DATE_CREATED = "date_created";

    /**
     * The {@value} attribute name for the date on which this data was last modified
     * (<em>Suggested</em>).
     */
    public static final String DATE_MODIFIED = "date_modified";

    /**
     * The {@value} attribute name for a date on which this data was formally issued
     * (<em>Suggested</em>).
     */
    public static final String DATE_ISSUED = "date_issued";

    /**
     * The {@value} attribute name for the creator's name (<em>Recommended</em>).
     */
    public static final String CREATOR_NAME = "creator_name";

    /**
     * The {@value} attribute name for the creator's URL (<em>Recommended</em>).
     */
    public static final String CREATOR_URL = "creator_url";

    /**
     * The {@value} attribute name for the creator's email address (<em>Recommended</em>).
     */
    public static final String CREATOR_EMAIL = "creator_email";

    /**
     * The {@value} attribute name for the role of any individuals or institutions that contributed
     * to the creation of this data (<em>Suggested</em>). The name, URL and email attribute names
     * will be the same than for the creator, with the {@code "creator"} prefix replaced by
     * {@code "contributor"}.
     */
    public static final String CONTRIBUTOR_ROLE = "contributor_role";

    /**
     * The {@value} attribute name for creator's institution (<em>Recommended</em>).
     */
    public static final String INSTITUTION = "institution";

    /**
     * The {@value} attribute name for the scientific project that produced the data
     * (<em>Recommended</em>).
     */
    public static final String PROJECT = "project";

    /**
     * The {@value} attribute name for a place to acknowledge various type of support for
     * the project that produced this data (<em>Recommended</em>).
     */
    public static final String ACKNOWLEDGMENT = "acknowledgment";

    /**
     * The {@value} attribute name for a description of the restrictions to data access
     * and distribution (<em>Recommended</em>).
     */
    public static final String LICENSE = "license";

    /**
     * The {@value} attribute name for a further refinement of the geospatial bounding box
     * (<em>Suggested</em>).
     */
    public static final String LATITUDE_RESOLUTION = "geospatial_lat_resolution";

    /**
     * The {@value} attribute name for a further refinement of the geospatial bounding box
     * (<em>Suggested</em>).
     */
    public static final String LONGITUDE_RESOLUTION = "geospatial_lon_resolution";

    /**
     * The {@value} attribute name for a further refinement of the geospatial bounding box
     * (<em>Suggested</em>).
     */
    public static final String VERTICAL_RESOLUTION = "geospatial_vertical_resolution";

    /**
     * The {@value} attribute name for a further refinement of the geospatial bounding box
     * (<em>Suggested</em>).
     */
    public static final String TIME_RESOLUTION = "time_coverage_resolution";

    /**
     * The {@value} attribute name for the designation associated with a range element.
     * This attribute can be associated to {@linkplain VariableSimpleIF variables}. If
     * specified, they shall be one flag name for each {@linkplain #FLAG_MASKS flag mask},
     * {@linkplain #FLAG_VALUES flag value} and {@linkplain #FLAG_MEANINGS flag meaning}.
     */
    public static final String FLAG_NAMES = "flag_names";

    /**
     * The {@value} attribute name for bitmask to apply on sample values before to compare
     * them to the {@linkplain #FLAG_VALUES flag values}.
     */
    public static final String FLAG_MASKS = "flag_masks";

    /**
     * The {@value} attribute name for sample values to be flagged. The {@linkplain #FLAG_MASKS
     * flag masks}, flag values and {@linkplain #FLAG_MEANINGS flag meaning} attributes, used
     * together, describe a blend of independent boolean conditions and enumerated status codes.
     * A flagged condition is identified by a bitwise AND of the variable value and each flag masks
     * value; a result that matches the flag values value indicates a true condition.
     */
    public static final String FLAG_VALUES = "flag_values";

    /**
     * The {@value} attribute name for the meaning of {@linkplain #FLAG_VALUES flag values}.
     * Each flag values and flag masks must coincide with a flag meanings.
     */
    public static final String FLAG_MEANINGS = "flag_meanings";

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
     * Were to send the warnings, or {@code null} if none.
     */
    private final WarningProducer owner;

    /**
     * The name factory, created when first needed.
     *
     * @todo Use the GeoAPI interface after a {@code createMemberName(...)} method has been added.
     */
    private transient DefaultNameFactory nameFactory;

    /**
     * The creator, used at metadata creation time for avoiding to declare
     * the same creator more than once.
     */
    private transient ResponsibleParty creator;

    /**
     * Creates a new <cite>NetCDF to ISO</cite> mapper for the given file. While this constructor
     * accepts arbitrary {@link NetcdfFile} instance, the {@link NetcdfDataset} subclass is
     * necessary in order to get coordinate system information.
     *
     * @param file  The NetCDF file from which to parse metadata.
     * @param owner The caller, or {@code null} if it does not implement the
     *              {@code WarningProducer} interface.
     */
    public NetcdfISO(final NetcdfFile file, final WarningProducer owner) {
        ArgumentChecks.ensureNonNull("file", file);
        this.file  = file;
        this.owner = owner;
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
        this.groups = XArrays.resize(groups, count);
    }

    /**
     * Reports a warning.
     *
     * @param method    The method in which the warning occurred.
     * @param exception The exception to log.
     */
    private void warning(final String method, final Exception exception) {
        Warnings.log(owner, Level.WARNING, NetcdfISO.class, method, exception);
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
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private String getStringValue(final Group group, final String name) {
        if (name != null) { // For createResponsibleParty(...) convenience.
            final Attribute attribute = getAttribute(group, name);
            if (attribute != null) {
                final String value = attribute.getStringValue();
                if (value != null) {
                    return value.trim();
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
        if (date != null) try {
            return DateUtil.parse(date);
        } catch (ParseException e) {
            warning("getDateValue", e);
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
                collection = new LinkedHashSet<>(4);
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
     * @param  url         The URL (mandatory - if {@code null}, no resource will be created).
     * @param  name        The resource name (optional).
     * @param  description The resource description (optional).
     * @return The online resource, or {@code null} if the URL was null.
     */
    private OnlineResource createOnlineResource(final String url, final String name, final String description) {
        if (url != null) try {
            final DefaultOnlineResource resource = new DefaultOnlineResource(new URI(url));
            resource.setProtocol("http");
            resource.setApplicationProfile("web browser");
            resource.setName(name);
            resource.setDescription(wrap(description));
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
     * Replaces the {@code "creator"} prefix by the given one if non-null.
     * If the substitution can not be performed, returns {@code null}.
     */
    private static String replace(String key, final String prefix) {
        if (prefix != null) {
            key = key.startsWith("creator") ? prefix.concat(key.substring(7)) : null;
        }
        return key;
    }

    /**
     * Creates a {@code ResponsibleParty} element if at least one of the attributes is non-null,
     * except {@code role} which is not tested. The {@code role} is intentionally not tested
     * because it is sometime hard-coded by callers in this class, in which case it can't be null.
     * <p>
     * This method tries to reuse the existing {@link #creator} instance, or part of it,
     * if it is suitable.
     *
     * @param role May be hard-coded by the caller.
     */
    private ResponsibleParty createResponsibleParty(final Group group, final String prefix, final Role role) {
        final String individualName   = getStringValue(group, replace(CREATOR_NAME,  prefix));
        final String organisationName = getStringValue(group, replace(INSTITUTION,   prefix));
        final String email            = getStringValue(group, replace(CREATOR_EMAIL, prefix));
        final String url              = getStringValue(group, replace(CREATOR_URL,   prefix));
        if (individualName == null && organisationName == null && email == null && url == null) {
            return null;
        }
        ResponsibleParty party    = creator;
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
                if (resource == null) resource = createOnlineResource(url, null, null);
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
     * is non-null. This method will reuse the {@link #creator} field, if non-null.
     *
     * @param id The {@code <gmd:fileIdentifier> attribute.
     */
    private Citation createCitation(final String id) {
        String title = getStringValue(TITLE);
        if (title == null) {
            title = getStringValue("full_name"); // THREDDS attribute.
            if (title == null) {
                title = getStringValue("name"); // THREDDS attribute.
            }
        }
        final Date creation  = getDateValue  (DATE_CREATED);
        final Date modified  = getDateValue  (DATE_MODIFIED);
        final Date issued    = getDateValue  (DATE_ISSUED);
        final String comment = getStringValue(COMMENT);
        if (title == null && id == null && creation == null && modified == null && issued == null && comment == null) {
            return null;
        }
        final DefaultCitation citation = new DefaultCitation(title);
        if (id != null) {
            final String namespace = getStringValue(NAMING_AUTHORITY);
            citation.getIdentifiers().add(new DefaultIdentifier((namespace != null) ? new DefaultCitation(namespace) : null, id));
        }
        if (creation != null) citation.getDates().add(new DefaultCitationDate(creation, DateType.CREATION));
        if (modified != null) citation.getDates().add(new DefaultCitationDate(modified, DateType.REVISION));
        if (issued   != null) citation.getDates().add(new DefaultCitationDate(issued,   DateType.PUBLICATION));
        if (creator != null) {
            // Same contact than the creator, except for the role.
            final DefaultResponsibleParty np = new DefaultResponsibleParty(Role.ORIGINATOR);
            np.setIndividualName  (creator.getIndividualName());
            np.setOrganisationName(creator.getOrganisationName());
            np.setContactInfo     (creator.getContactInfo());
            citation.getCitedResponsibleParties().add(np);
        }
        for (final Group group : groups) {
            final ResponsibleParty contributor = createResponsibleParty(group, "contributor",
                    CodeLists.valueOf(Role.class, getStringValue(group, CONTRIBUTOR_ROLE)));
            if (contributor != null && contributor != creator) {
                addIfAbsent(citation.getCitedResponsibleParties(), contributor);
            }
        }
        citation.setOtherCitationDetails(wrap(comment));
        return citation;
    }

    /**
     * Creates a {@code DataIdentification} element if at least one of the required attributes
     * is non-null. This method will reuse the {@link #creator} field, if non-null.
     *
     * @param id The {@code <gmd:fileIdentifier> attribute.
     */
    private DataIdentification createIdentificationInfo(final String id) throws IOException {
        DefaultDataIdentification identification = null;
        Set<InternationalString> project   = null;
        Set<InternationalString> publisher = null;
        Set<InternationalString> standards = null;
        for (final Group group : groups) {
            final Keywords keywords = createKeywords(group, KeywordType.THEME);
            final String   credits  = getStringValue(group, ACKNOWLEDGMENT);
            final String   license  = getStringValue(group, LICENSE);
            if (keywords != null || credits != null || license != null) {
                if (identification == null) {
                    identification = new DefaultDataIdentification();
                }
                if (keywords != null) addIfAbsent(identification.getDescriptiveKeywords(), keywords);
                if (credits  != null) addIfAbsent(identification.getCredits(), credits);
                if (license  != null) addIfAbsent(identification.getResourceConstraints(), new DefaultLegalConstraints(license));
            }
            project   = addIfNonNull(project,   wrap(getStringValue(group, PROJECT)));
            publisher = addIfNonNull(publisher, wrap(getStringValue(group, replace(CREATOR_NAME, "publisher"))));
            standards = addIfNonNull(standards, wrap(getStringValue(group, STANDARD_NAME)));
        }
        final Citation citation = createCitation(id);
        final String   summary  = getStringValue(SUMMARY);
        if (identification == null) {
            if (citation == null && summary == null && project == null && publisher == null && creator == null) {
                return null;
            }
            identification = new DefaultDataIdentification();
        }
        identification.setCitation(citation);
        identification.setAbstract(wrap(summary));
        if (creator != null) {
            identification.getPointOfContacts().add(creator);
        }
        addKeywords(identification, project,   "project"); // Not necessarily the same string than PROJECT.
        addKeywords(identification, publisher, "dataCenter");
        addKeywords(identification, standards, "theme");
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
            keywords.setType(CodeLists.valueOf(KeywordType.class, type));
            addTo.getDescriptiveKeywords().add(keywords);
        }
    }

    /**
     * Returns the keywords if at least one required attribute is found, or {@code null} otherwise.
     */
    private Keywords createKeywords(final Group group, final KeywordType type) throws IOException {
        final String list = getStringValue(group, KEYWORDS);
        DefaultKeywords keywords = null;
        if (list != null) {
            final Set<InternationalString> words = new LinkedHashSet<>();
            for (String keyword : list.split(getKeywordSeparator(group))) {
                keyword = keyword.trim();
                if (!keyword.isEmpty()) {
                    words.add(wrap(keyword));
                }
            }
            if (!words.isEmpty()) {
                keywords = new DefaultKeywords(words);
                keywords.setType(type);
                final String vocabulary = getStringValue(group, VOCABULARY);
                if (vocabulary != null) {
                    keywords.setThesaurusName(new DefaultCitation(vocabulary));
                }
            }
        }
        return keywords;
    }

    /**
     * Returns the string to use as a keyword separator. This separator is used for parsing
     * the {@value #KEYWORDS} attribute value. The default implementation returns {@code ","}.
     * Subclasses can override this method in an other separator (possibly determined from
     * the file content) is desired.
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
     * @throws IOException If an I/O operation was required but failed.
     */
    @SuppressWarnings("fallthrough")
    protected GridSpatialRepresentation createSpatialRepresentationInfo(final CoordinateSystem cs) throws IOException {
        final DefaultGridSpatialRepresentation grid = new DefaultGridSpatialRepresentation();
        grid.setNumberOfDimensions(cs.getRankDomain());
        final List<CoordinateAxis> axes = cs.getCoordinateAxes();
        for (int i=axes.size(); --i>=0;) { // We need to iterate in reverse order.
            final CoordinateAxis axis = axes.get(i);
            DimensionNameType type = null;
            Double resolution = null;
            final AxisType at = axis.getAxisType();
            if (at != null) {
                String rsat = null;
                switch (at) {
                    case Lon:      rsat = LONGITUDE_RESOLUTION; // fallthrough
                    case GeoX:     type = DimensionNameType.COLUMN; break;
                    case Lat:      rsat = LATITUDE_RESOLUTION; // fallthrough
                    case GeoY:     type = DimensionNameType.ROW; break;
                    case Height:   rsat = VERTICAL_RESOLUTION;
                    case GeoZ:
                    case Pressure: type = DimensionNameType.VERTICAL; break;
                    case Time:     rsat = TIME_RESOLUTION; // fallthrough
                    case RunTime:  type = DimensionNameType.TIME; break;
                }
                if (rsat != null) {
                    final Number res = getNumericValue(rsat);
                    if (res != null) {
                        resolution = (res instanceof Double) ? (Double) res : res.doubleValue();
                    }
                }
            }
            for (int j=axis.getRank(); --j>=0;) { // Reverse order again.
                final DefaultDimension dimension = new DefaultDimension();
                dimension.setDimensionName(type);
                dimension.setResolution(resolution);
                dimension.setDimensionSize(axis.getShape(j));
                grid.getAxisDimensionProperties().add(dimension);
            }
        }
        grid.setCellGeometry(CellGeometry.AREA);
        return grid;
    }

    /**
     * Creates a {@code <gmd:contentInfo>} element from all NetCDF variables.
     *
     * @return The content information.
     * @throws IOException If an I/O operation was required but failed.
     */
    private CoverageDescription createContentInfo() throws IOException {
        final DefaultCoverageDescription content = new DefaultCoverageDescription();
        for (final VariableSimpleIF variable : file.getVariables()) {
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
        return content;
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
     * @throws IOException If an I/O operation was required but failed.
     */
    protected Band createSampleDimension(final VariableSimpleIF variable) throws IOException {
        final DefaultBand band = new DefaultBand();
        String name = variable.getShortName();
        if (name == null) {
            name = variable.getName();
        }
        if (name != null) {
            if (nameFactory == null) {
                nameFactory = (DefaultNameFactory) FactoryFinder.getNameFactory(
                        new Hints(Hints.NAME_FACTORY, DefaultNameFactory.class));
            }
            final StringBuilder type = new StringBuilder(variable.getDataType().getPrimitiveClassType().getSimpleName());
            for (int i=variable.getShape().length; --i>=0;) {
                type.append("[]");
            }
            band.setSequenceIdentifier(nameFactory.createMemberName(null, name,
                    nameFactory.createTypeName(null, type.toString())));
        }
        final String descriptor = variable.getDescription();
        if (descriptor != null && !descriptor.equals(name)) {
            band.setDescriptor(wrap(descriptor));
        }
//TODO: Can't store the units, because the Band interface restricts it to length.
//      We need the SampleDimension interface proposed in ISO 19115 revision draft.
//      band.setUnits(Units.valueOf(variable.getUnitsString()));
        return band;
    }

    /**
     * Creates a {@code <gmd:rangeElementDescription>} elements from the given information.
     * Subclasses can override this method if they need to complete the information provided
     * in the returned object.
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
     * @throws IOException If an I/O operation was required but failed.
     */
    protected RangeElementDescription createRangeElementDescription(final VariableSimpleIF variable,
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
     * @throws IOException If an I/O operation was required but failed.
     */
    public Metadata createMetadata() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final String id = getStringValue(IDENTIFIER);
        metadata.setMetadataStandardName("ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data");
        metadata.setMetadataStandardVersion("ISO 19115-2:2009(E)");
        metadata.setDateStamp(getDateValue(METADATA_CREATION));
        metadata.setFileIdentifier(id);
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
            final ResponsibleParty party = createResponsibleParty(group, null, Role.POINT_OF_CONTACT);
            if (party != null && party != creator) {
                addIfAbsent(metadata.getContacts(), party);
                if (creator == null) {
                    creator = party;
                }
            }
        }
        /*
         * Add the identification info AFTER the responsible parties, because this method
         * will reuse the 'creator' field (if non-null).
         */
        final DataIdentification identification = createIdentificationInfo(id);
        if (identification != null) {
            metadata.getIdentificationInfo().add(identification);
        }
        metadata.getContentInfo().add(createContentInfo());
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
                metadata.getSpatialRepresentationInfo().add(createSpatialRepresentationInfo(cs));
            }
        }
        return metadata;
    }
}
