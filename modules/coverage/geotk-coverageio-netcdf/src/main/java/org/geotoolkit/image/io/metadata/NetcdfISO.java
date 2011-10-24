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
import java.util.Collection;
import java.util.EnumSet;
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
import org.opengis.metadata.citation.OnLineFunction;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.GridSpatialRepresentation;

import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.internal.image.io.Warnings;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.DefaultAddress;
import org.geotoolkit.metadata.iso.citation.DefaultContact;
import org.geotoolkit.metadata.iso.citation.DefaultOnlineResource;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;

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
     * Constants for the {@code getAttribute(...)} methods.
     */
    private static final int GLOBAL=0, GROUP=1, GLOBAL_OR_GROUP=2, GROUP_OR_GLOBAL=3;

    /**
     * The NetCDF file from which to extract ISO metadata.
     */
    private final NetcdfFile file;

    /**
     * The {@code "NCISOMetadata"} metadata group, or {@code null} if none.
     */
    private final Group ncISO;

    /**
     * The {@code "CFMetadata"} metadata group, or {@code null} if none.
     */
    private final Group ncCF;

    /**
     * The {@code "THREDDSMetadata"} group, or {@code null} if none.
     */
    private final Group thredds;

    /**
     * The {@code "THREDDSMetadata/services"} sub-group, or {@code null} if none.
     */
    private final Group services;

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
        this.file = file;
        thredds = file.findGroup("THREDDSMetadata");
        if (thredds != null) {
            services = thredds.findGroup("services");
        } else {
            services = null;
        }
        ncISO = file.findGroup("NCISOMetadata");
        ncCF  = file.findGroup("CFMetadata");
        this.owner = owner;
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
     * Returns the attribute of the given name, either as a global attribute or in the given
     * group. The place where to look for the attribute is determined by the given {@code flag}:
     * <p>
     * <ul>
     *   <li>{@link #GLOBAL} for searching only in global attributes - the given group is ignored.</li>
     *   <li>{@link #GROUP} for searching only in the attributes of the given group.</li>
     *   <li>{@link #GLOBAL_OR_GROUP} for searching in global attributes first, then in the given group.</li>
     *   <li>{@link #GROUP_OR_GLOBAL} for searching in the given group first, then in global attributes.</li>
     * </ul>
     *
     * @param  group The group in which to search the attribute, or {@code null}.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private String getStringValue(final Group group, final String name, final int flag) {
        final Attribute attribute = getAttribute(group, name, flag);
        return (attribute != null) ? attribute.getStringValue() : null;
    }

    /**
     * Same {@link #getStringValue(Group, String, int)}, except that the value is returned
     * as a number.
     *
     * @param  group The group in which to search the attribute, or {@code null}.
     * @param  name  The name of the attribute to search.
     * @return The attribute value, or {@code null} if none.
     */
    private Number getNumericValue(final Group group, final String name, final int flag) {
        final Attribute attribute = getAttribute(group, name, flag);
        if (attribute != null) {
            Number value = attribute.getNumericValue();
            if (value == null) {
                final String asString = attribute.getStringValue();
                if (asString != null) try {
                    value = Double.valueOf(asString);
                } catch (NumberFormatException e) {
                    warning("getNumericValue", e);
                }
            }
            return value;
        }
        return null;
    }

    /**
     * For implementation of {@link #getStringValue(Group, String, int)} and
     * {@link #getNumericValue(Group, String, int)}.
     */
    @SuppressWarnings("fallthrough")
    private Attribute getAttribute(final Group group, final String name, final int flag) {
        Attribute attribute;
        switch (flag) {
            case GLOBAL:
            case GLOBAL_OR_GROUP: {
                attribute = file.findGlobalAttributeIgnoreCase(name);
                if (attribute != null) {
                    return attribute;
                } else if (flag != GLOBAL_OR_GROUP) {
                    break;
                }
                // Fallthrough
            }
            case GROUP:
            case GROUP_OR_GLOBAL: {
                if (group != null) {
                    attribute = group.findAttributeIgnoreCase(name);
                    if (attribute != null) {
                        return attribute;
                    }
                }
                if (flag == GROUP_OR_GLOBAL) {
                    attribute = file.findGlobalAttributeIgnoreCase(name);
                    if (attribute != null) {
                        return attribute;
                    }
                }
                break;
            }
            default: throw new AssertionError(flag); // Should never happen.
        }
        return null;
    }

    /**
     * Returns the given string as a date.
     *
     * @param  name  The date to parse (can be {@code null}).
     * @return The attribute value, or {@code null} if none or unparseable.
     */
    private Date parseDate(final String date) {
        if (date != null) try {
            return DateUtil.parse(date);
        } catch (ParseException e) {
            warning("getDateAttribute", e);
        }
        return null;
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
     *
     * @param email Read from global or THREDDS metadata.
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
     *
     * @param email Read from global or THREDDS metadata.
     * @param url   Read from global or THREDDS metadata.
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
     * Creates a {@code ResponsibleParty} element if at least one of the attributes is
     * non-null, except {@code role} which is not tested. The {@code role} is intentionally
     * not tested because it is hard-coded by callers in this class, so it is never null.
     * <p>
     * This method tries to reuse the existing {@link #creator} instance, or part of it,
     * if it is suitable.
     *
     * @param role Hard-coded by the caller.
     */
    private ResponsibleParty createResponsibleParty(final Group group, final int flag, final Role role) {
        final String individualName   = getStringValue(group, "creator_name",  flag);
        final String organisationName = getStringValue(group, "institution",   flag);
        final String email            = getStringValue(group, "creator_email", flag);
        final String url              = getStringValue(group, "creator_url",   flag);
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
                final DefaultResponsibleParty np = new DefaultResponsibleParty();
                np.setIndividualName(individualName);
                np.setOrganisationName(wrap(organisationName));
                np.setContactInfo(contact);
                np.setRole(role);
                party = np;
            }
        }
        return party;
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
     * Returns {@code true} if the given URL is null, or if the given resource contains that URL.
     *
     * @param resource  The value stored in the metadata object.
     * @param url       The value parsed from the NetCDF file.
     */
    private static boolean isDefined(final OnlineResource resource, final String url) {
        return (url == null) || (resource != null && isDefined(resource.getLinkage().toString(), url));
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
     * Creates an ISO {@code Metadata} object from the information found in the NetCDF file.
     *
     * @return The ISO metadata object.
     * @throws IOException If an I/O operation was required but failed.
     */
    public Metadata createMetadata() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMetadataStandardName("ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data");
        metadata.setMetadataStandardVersion("ISO 19115-2:2009(E)");
        metadata.setDateStamp(parseDate(getStringValue(ncISO, "metadata_creation", GROUP_OR_GLOBAL)));
        metadata.setFileIdentifier(getStringValue(thredds, "id", GROUP_OR_GLOBAL));
        metadata.getHierarchyLevels().add(ScopeCode.DATASET);
        if (services != null) {
            metadata.getHierarchyLevels().add(ScopeCode.SERVICE);
        }
        /*
         * Add the ResponsibleParty which is declared in global attributes, or in
         * the THREDDS attributes if no information was found in global attributes.
         */
        ResponsibleParty other;
        creator = createResponsibleParty(null,    GLOBAL, Role.POINT_OF_CONTACT);
        other   = createResponsibleParty(thredds, GROUP,  Role.POINT_OF_CONTACT);
        if (creator != null) {
            metadata.getContacts().add(creator);
            if (creator == other) {
                other = null;
            }
        } else {
            creator = other;
        }
        if (other != null) {
            metadata.getContacts().add(other);
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
                    final Number res = getNumericValue(ncCF, rsat, GROUP_OR_GLOBAL);
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
