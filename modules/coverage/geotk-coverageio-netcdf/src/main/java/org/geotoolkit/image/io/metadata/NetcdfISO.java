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
import org.opengis.util.InternationalString;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.image.io.WarningProducer;
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
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultKeywords;

import static org.geotoolkit.util.SimpleInternationalString.wrap;


/**
 * Mapping from NetCDF metadata to ISO 19115-2 metadata.
 * The mapping is defined in the following web pages:
 * <p>
 * <ul>
 *   <li><a href="http://ngdc.noaa.gov/metadata/published/xsl/nciso2.0/UnidataDD2MI.xsl">UnidataDD2MI.xsl</a> file</li>
 *   <li><a href="https://geo-ide.noaa.gov/wiki/index.php?title=NetCDF_Attribute_Convention_for_Dataset_Discovery">NetCDF
 *       Attribute Convention for Dataset Discovery</a> wiki</li>
 * </ul>
 * <p>
 * The following attributes or elements are not included in the image metadata:
 * <p>
 * <ul>
 *   <li>{@code <xsl:attribute name="xsi:schemaLocation"/>} because this apply only to XML marshalling.</li>
 *   <li>{@code <gml:language/>} because the language is not necessarily English.</li>
 *   <li>{@code <gml:characterSet/>} because this apply only to XML marshalling.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfISO {
    /**
     * The NetCDF file from which to extract ISO metadata.
     * This file is set at construction time.
     */
    protected final NetcdfFile file;

    /**
     * Names of groups where to search for metadata, in precedence order.
     * The {@code null} value stands for global attributes.
     */
    private static final String[] GROUP_NAMES = {"NCISOMetadata", "CFMetadata", null, "THREDDSMetadata"};

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
     * Returns the string to use as a keyword separator. The default implementation returns
     * {@code ","}. Subclasses can override this method in an other separator (possibly
     * determined from the file content) is desired.
     *
     * @param  group The NetCDF group from which keywords are read.
     * @return The string to use as a keyword separator, as a regular expression.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    public String getKeywordSeparator(final Group group) throws IOException {
        return ",";
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
     * Returns the attribute of the given name in the given group.
     *
     * @param  group The group in which to search the attribute, or {@code null} for global attributes.
     * @param  name  The name of the attribute to search (can not be null).
     * @return The attribute, or {@code null} if none.
     */
    private Attribute getAttribute(final Group group, final String name) {
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
        final Attribute attribute = getAttribute(group, name);
        if (attribute != null) {
            final String value = attribute.getStringValue();
            if (value != null) {
                return value.trim();
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
     * Creates a {@code ResponsibleParty} element if at least one of the attributes is non-null,
     * except {@code role} which is not tested. The {@code role} is intentionally not tested
     * because it is sometime hard-coded by callers in this class, in which case it can't be null.
     * <p>
     * This method tries to reuse the existing {@link #creator} instance, or part of it,
     * if it is suitable.
     *
     * @param individualName   The {@code "creator_name"}  attribute value, or {@code null}.
     * @param organisationName The {@code "institution"}   attribute value, or {@code null}.
     * @param email            The {@code "creator_email"} attribute value, or {@code null}.
     * @param url              The {@code "creator_url"}   attribute value, or {@code null}.
     * @param role             May be hard-coded by the caller.
     */
    private ResponsibleParty createResponsibleParty(final String individualName,
            final String organisationName, final String email, final String url, final Role role)
    {
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
     * Creates a {@code ResponsibleParty} element if at least one of the attributes is non-null,
     * except {@code role} which is not tested. The {@code role} is intentionally not tested
     * because it is sometime hard-coded by callers in this class, in which case it can't be null.
     * <p>
     * This method tries to reuse the existing {@link #creator} instance, or part of it,
     * if it is suitable.
     *
     * @param role May be hard-coded by the caller.
     */
    private ResponsibleParty createResponsibleParty(final Group group, final Role role) {
        return createResponsibleParty(getStringValue(group, "creator_name"),
                                      getStringValue(group, "institution"),
                                      getStringValue(group, "creator_email"),
                                      getStringValue(group, "creator_url"), role);
    }

    /**
     * Creates a {@code Citation} element if at least one of the required attributes
     * is non-null. This method will reuse the {@link #creator} field, if non-null.
     *
     * @param id The {@code <gmd:fileIdentifier> attribute.
     */
    private Citation createCitation(final String id) {
        String title = getStringValue("title");
        if (title == null) {
            title = getStringValue("name");
        }
        final Date creation  = getDateValue  ("date_created");
        final Date modified  = getDateValue  ("date_modified");
        final Date issued    = getDateValue  ("date_issued");
        final String comment = getStringValue("comment");
        if (title == null && id == null && creation == null && modified == null && issued == null && comment == null) {
            return null;
        }
        final DefaultCitation citation = new DefaultCitation(title);
        if (id != null) {
            final String namespace = getStringValue("naming_authority");
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
            final ResponsibleParty contributor = createResponsibleParty(
                    getStringValue(group, "contributor_name"), null,
                    getStringValue(group, "contributor_email"),
                    getStringValue(group, "contributor_url"), CodeLists.valueOf(Role.class,
                    getStringValue(group, "contributor_role")));
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
        final Citation citation = createCitation(id);
        final String   summary  = getStringValue("summary");
        final Set<InternationalString> project = new LinkedHashSet<>(4);
        for (final Group group : groups) {
            final String p = getStringValue(group, "project");
            if (p != null) {
                project.add(wrap(p));
            }
            final Keywords keywords = createKeywords(group, KeywordType.THEME);
            final String   credits  = getStringValue(group, "acknowledgment");
            if (keywords != null || credits != null) {
                if (identification == null) {
                    identification = new DefaultDataIdentification();
                }
                if (keywords != null) addIfAbsent(identification.getDescriptiveKeywords(), keywords);
                if (credits  != null) addIfAbsent(identification.getCredits(), credits);
            }
        }
        if (identification == null) {
            if (citation == null && summary == null && creator == null && project.isEmpty()) {
                return null;
            }
            identification = new DefaultDataIdentification();
        }
        if (!project.isEmpty()) {
            final DefaultKeywords keywords = new DefaultKeywords(project);
            keywords.setType(CodeLists.valueOf(KeywordType.class, "project"));
            identification.getDescriptiveKeywords().add(keywords);
        }
        identification.setCitation(citation);
        identification.setAbstract(wrap(summary));
        if (creator != null) {
            identification.getPointOfContacts().add(creator);
        }
        return identification;
    }

    /**
     * Returns the keywords if at least one required attribute is found, or {@code null} otherwise.
     */
    private Keywords createKeywords(final Group group, final KeywordType type) throws IOException {
        final String list = getStringValue(group, "keywords");
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
                final String vocabulary = getStringValue(group, "keywords_vocabulary");
                if (vocabulary != null) {
                    keywords.setThesaurusName(new DefaultCitation(vocabulary));
                }
            }
        }
        return keywords;
    }

    /**
     * Creates an ISO {@code Metadata} object from the information found in the NetCDF file.
     *
     * @return The ISO metadata object.
     * @throws IOException If an I/O operation was required but failed.
     */
    public Metadata createMetadata() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final String id = getStringValue("id");
        metadata.setMetadataStandardName("ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data");
        metadata.setMetadataStandardVersion("ISO 19115-2:2009(E)");
        metadata.setDateStamp(getDateValue("metadata_creation"));
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
            final ResponsibleParty party = createResponsibleParty(group, Role.POINT_OF_CONTACT);
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
                    case Lon:      rsat = "geospatial_lon_resolution"; // fallthrough
                    case GeoX:     type = DimensionNameType.COLUMN; break;
                    case Lat:      rsat = "geospatial_lat_resolution"; // fallthrough
                    case GeoY:     type = DimensionNameType.ROW; break;
                    case Height:   rsat = "geospatial_vertical_resolution";
                    case GeoZ:
                    case Pressure: type = DimensionNameType.VERTICAL; break;
                    case Time:     rsat = "time_coverage_resolution"; // fallthrough
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
}
