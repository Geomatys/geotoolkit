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
package org.geotoolkit.image.io.plugin;

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
import javax.measure.unit.Unit;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;

import ucar.nc2.Group;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.VariableSimpleIF;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.CoordinateSystem;
import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.constants.AxisType;
import ucar.nc2.constants.CF;
import ucar.nc2.units.DateUnit;
import ucar.unidata.util.DateUtil;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.distribution.Distributor;
import org.opengis.metadata.distribution.Distribution;
import org.opengis.metadata.constraint.LegalConstraints;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.GridSpatialRepresentation;
import org.opengis.metadata.spatial.SpatialRepresentationType;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.identification.KeywordType;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicDescription;
import org.opengis.util.InternationalString;
import org.opengis.util.NameFactory;

import org.geotoolkit.measure.Units;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.image.io.metadata.MetadataTranscoder;
import org.geotoolkit.internal.CodeLists;
import org.geotoolkit.internal.image.io.Warnings;
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
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;

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
 * {@linkplain #LONGITUDE longitude} and {@linkplain #LATITUDE latitude} resolutions are
 * often more accurate in that group.
 *
 * {@section NetCDF attributes}
 * The NetCDF attributes parsed by this class are
 * {@value #ACCESS_CONSTRAINT}, {@value #ACKNOWLEDGMENT}, {@value #COMMENT},
 * {@linkplain #CONTRIBUTOR "contributor_email"},
 * {@linkplain #CONTRIBUTOR "contributor_name"},
 * {@linkplain #CONTRIBUTOR "contributor_role"},
 * {@linkplain #CONTRIBUTOR "contributor_url"},
 * {@linkplain #CREATOR     "creator_email"},
 * {@linkplain #CREATOR     "creator_name"},
 * {@linkplain #CREATOR     "creator_url"},
 * {@value #DATA_TYPE}, {@value #DATE_CREATED}, {@value #DATE_ISSUED}, {@value #DATE_MODIFIED},
 * {@value #FLAG_MASKS}, {@value #FLAG_MEANINGS}, {@value #FLAG_NAMES}, {@value #FLAG_VALUES},
 * {@linkplain #TITLE "full_name"},
 * {@linkplain #GEOGRAPHIC_IDENTIFIER "geographic_identifier"},
 * {@linkplain #LATITUDE  "geospatial_lat_max"},
 * {@linkplain #LATITUDE  "geospatial_lat_min"},
 * {@linkplain #LATITUDE  "geospatial_lat_resolution"},
 * {@linkplain #LATITUDE  "geospatial_lat_units"},
 * {@linkplain #LONGITUDE "geospatial_lon_max"},
 * {@linkplain #LONGITUDE "geospatial_lon_min"},
 * {@linkplain #LONGITUDE "geospatial_lon_resolution"},
 * {@linkplain #LONGITUDE "geospatial_lon_units"},
 * {@linkplain #VERTICAL  "geospatial_vertical_max"},
 * {@linkplain #VERTICAL  "geospatial_vertical_min"},
 * {@linkplain #VERTICAL  "geospatial_vertical_positive"},
 * {@linkplain #VERTICAL  "geospatial_vertical_resolution"},
 * {@linkplain #VERTICAL  "geospatial_vertical_units"},
 * {@value #HISTORY}, {@value #IDENTIFIER}, {@linkplain #CREATOR "institution"}, {@value #KEYWORDS},
 * {@value #LICENSE}, {@value #METADATA_CREATION}, {@linkplain #TITLE "name"}, {@value #NAMING_AUTHORITY},
 * {@value #PROCESSING_LEVEL}, {@value #PROJECT},
 * {@linkplain #PUBLISHER "publisher_email"},
 * {@linkplain #PUBLISHER "publisher_name"},
 * {@linkplain #PUBLISHER "publisher_url"},
 * {@value #PURPOSE}, {@value #REFERENCES}, {@value #STANDARD_NAME},
 * {@value #STANDARD_NAME_VOCABULARY}, {@value #SUMMARY},
 * {@linkplain #TIME "time_coverage_duration"},
 * {@linkplain #TIME "time_coverage_end"},
 * {@linkplain #TIME "time_coverage_resolution"},
 * {@linkplain #TIME "time_coverage_start"},
 * {@linkplain #TIME "time_coverage_units"},
 * {@value #TITLE}, {@value #TOPIC_CATEGORY} and {@value #VOCABULARY}.
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
public class NetcdfTranscoder extends MetadataTranscoder<Metadata> {
    /**
     * The {@value} attribute name for a short description of the dataset
     * (<em>Highly Recommended</em>). If no {@value} attribute is provided,
     * then {@code NetcdfTranscoder} will look for "full_name" and "name".
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getTitle() title}</li></ul>
     *
     * @see NetcdfFile#getTitle()
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#title_Attribute">UCAR reference</a>
     */
    public static final String TITLE = "title";

    /**
     * The {@value} attribute name for a paragraph describing the dataset
     * (<em>Highly Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getAbstract() abstract}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#summary_Attribute">UCAR reference</a>
     */
    public static final String SUMMARY = "summary";

    /**
     * The {@value} attribute name for an identifier (<em>Recommended</em>).
     * The combination of the {@value #NAMING_AUTHORITY} and the {@value}
     * should be a globally unique identifier for the dataset.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getFileIdentifier() fileIdentifier}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getIdentifiers() identifier} /
     * {@link Identifier#getCode() code}</li></ul>
     *
     * @see #getFileIdentifier()
     * @see NetcdfFile#getId()
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#id_Attribute">UCAR reference</a>
     */
    public static final String IDENTIFIER = "id";

    /**
     * The {@value} attribute name for the identifier authority (<em>Recommended</em>).
     * The combination of the {@value} and the {@value #IDENTIFIER} should be a globally
     * unique identifier for the dataset.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getFileIdentifier() fileIdentifier}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getIdentifiers() identifier} /
     * {@link Identifier#getAuthority() authority}</li></ul>
     *
     * @see #IDENTIFIER
     * @see #getFileIdentifier()
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#id_Attribute">UCAR reference</a>
     */
    public static final String NAMING_AUTHORITY = "naming_authority";

    /**
     * The {@value} attribute name for a long descriptive name for the variable taken from a controlled
     * vocabulary of variable names. This is actually a {@linkplain VariableSimpleIF variable} attribute,
     * but sometime appears also in {@linkplain NetcdfFile#findGlobalAttribute(String) global attributes}.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getKeywords() keyword} with {@link KeywordType#THEME}</li></ul>
     *
     * @see #STANDARD_NAME_VOCABULARY
     * @see #KEYWORDS
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#standard_name_Attribute">UCAR reference</a>
     */
    public static final String STANDARD_NAME = CF.STANDARD_NAME;

    /**
     * The {@value} attribute name for indicating which controlled list of variable names has been
     * used in the {@value #STANDARD_NAME} attribute.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getThesaurusName() thesaurusName} /
     * {@link Citation#getTitle() title}</li></ul>
     *
     * @see #STANDARD_NAME
     * @see #VOCABULARY
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#standard_name_vocabulary_Attribute">UCAR reference</a>
     */
    public static final String STANDARD_NAME_VOCABULARY = "standard_name_vocabulary";

    /**
     * The {@value} attribute name for a comma separated list of key words and phrases
     * (<em>Highly Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getKeywords() keyword} with {@link KeywordType#THEME}</li></ul>
     *
     * @see #VOCABULARY
     * @see #STANDARD_NAME
     * @see #getKeywordSeparator(Group)
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#keywords_Attribute">UCAR reference</a>
     */
    public static final String KEYWORDS = "keywords";

    /**
     * The {@value} attribute name for the guideline for the words/phrases in the
     * {@value #KEYWORDS} attribute (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getThesaurusName() thesaurusName} /
     * {@link Citation#getTitle() title}</li></ul>
     *
     * @see #KEYWORDS
     * @see #STANDARD_NAME_VOCABULARY
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#keywords_vocabulary_Attribute">UCAR reference</a>
     */
    public static final String VOCABULARY = "keywords_vocabulary";

    /**
     * The {@value} attribute name for a high-level geographic data thematic classification.
     * Typical values are {@code "farming"}, {@code "biota"}, {@code "boundaries"},
     * {@code "climatology meteorology atmosphere"}, {@code "economy"}, {@code "elevation"},
     * {@code "environment"}, {@code "geoscientific information"}, {@code "health"},
     * {@code "imagery base maps earth cover"}, {@code "intelligence military"},
     * {@code "inland waters"}, {@code "location"}, {@code "oceans"}, {@code "planning cadastre"},
     * {@code "society"}, {@code "structure"}, {@code "transportation"} and
     * {@code "utilitiesCommunication"}.
     *
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getTopicCategories() topicCategory}</li></ul>
     *
     * @see TopicCategory
     */
    public static final String TOPIC_CATEGORY = "topic_category";

    /**
     * The {@value} attribute name for the THREDDS data type appropriate for this dataset
     * (<em>Recommended</em>). Examples: {@code "Vector"}, {@code "TextTable"}, {@code "Grid"},
     * {@code "Image"}, {@code "Video"}, {@code "Tin"}, {@code "StereoModel"}, {@code "Station"},
     * {@code "Swath"} or {@code "Trajectory"}.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getSpatialRepresentationTypes() spatialRepresentationType}</li></ul>
     *
     * @see SpatialRepresentationType
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#cdm_data_type_Attribute">UCAR reference</a>
     */
    public static final String DATA_TYPE = "cdm_data_type";

    /**
     * The {@value} attribute name for providing an audit trail for modifications to the
     * original data (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getDataQualityInfo() dataQualityInfo} /
     * {@link DataQuality#getLineage() lineage} /
     * {@link Lineage#getStatement() statement}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#history_Attribute">UCAR reference</a>
     */
    public static final String HISTORY = "history";

    /**
     * The {@value} attribute name for miscellaneous information about the data
     * (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getSupplementalInformation() supplementalInformation}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#comment_Attribute">UCAR reference</a>
     */
    public static final String COMMENT = "comment";

    /**
     * The {@value} attribute name for the date on which the metadata was created
     * (<em>Suggested</em>). This is actually defined in the "{@code NCISOMetadata}"
     * subgroup.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getDateStamp() dateStamp}</li></ul>
     */
    public static final String METADATA_CREATION = "metadata_creation";

    /**
     * The {@value} attribute name for the date on which the data was created
     * (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getDates() date} /
     * {@link CitationDate#getDate() date} with {@link DateType#CREATION}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#date_created_Attribute">UCAR reference</a>
     */
    public static final String DATE_CREATED = "date_created";

    /**
     * The {@value} attribute name for the date on which this data was last modified
     * (<em>Suggested</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getDates() date} /
     * {@link CitationDate#getDate() date} with {@link DateType#REVISION}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#date_modified_Attribute">UCAR reference</a>
     */
    public static final String DATE_MODIFIED = "date_modified";

    /**
     * The {@value} attribute name for a date on which this data was formally issued
     * (<em>Suggested</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} /
     * {@link Citation#getDates() date} /
     * {@link CitationDate#getDate() date} with {@link DateType#PUBLICATION}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#date_issued_Attribute">UCAR reference</a>
     */
    public static final String DATE_ISSUED = "date_issued";

    /**
     * Holds the attribute names describing a responsible party.
     * Values are:
     * <p>
     * <table border="1" cellspacing="0"><tr bgcolor="lightblue">
     *   <th>Attribute</th>
     *   <th>{@link NetcdfTranscoder#CREATOR}</th>
     *   <th>{@link NetcdfTranscoder#CONTRIBUTOR}</th>
     *   <th>{@link NetcdfTranscoder#PUBLISHER}</th>
     * </tr><tr>
     *   <td>{@link #NAME}</td>
     *   <td>{@code "creator_name"}</td>
     *   <td>{@code "contributor_name"}</td>
     *   <td>{@code "publisher_name"}</td>
     * </tr><tr>
     *   <td>{@link #INSTITUTION}</td>
     *   <td>{@code "institution"}</td>
     *   <td></td>
     *   <td></td>
     * </tr><tr>
     *   <td>{@link #URL}</td>
     *   <td>{@code "creator_url"}</td>
     *   <td>{@code "contributor_url"}</td>
     *   <td>{@code "publisher_url"}</td>
     * </tr><tr>
     *   <td>{@link #EMAIL}</td>
     *   <td>{@code "creator_email"}</td>
     *   <td>{@code "contributor_email"}</td>
     *   <td>{@code "publisher_email"}</td>
     * </tr><tr>
     *   <td>{@link #ROLE}</td>
     *   <td></td>
     *   <td>{@code "contributor_role"}</td>
     *   <td></td>
     * </tr><tr>
     *   <td>{@link #DEFAULT_ROLE}</td>
     *   <td>{@link Role#ORIGINATOR}</td>
     *   <td></td>
     *   <td>{@link Role#PUBLISHER}</td>
     * </tr></table>
     *
     * {@note The member names in this class are upper-cases because they should be considered
     *        as constants. For example <code>NetcdfTranscoder.CREATOR.EMAIL</code> maps exactly to the
     *        <code>"creator_email"</code> string and nothing else. A lower-case <code>email</code>
     *        member name could be misleading since it would suggest that the field contains the
     *        actual name value rather than the key by which the value is identified in a NetCDF file.}
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20
     * @module
     */
    public static class Responsible {
        /**
         * The attribute name for the responsible's name. Possible values are
         * {@code "creator_name"}, {@code "contributor_name"} or {@code "publisher_name"}.
         * <p>
         * <b>Path:</b> <ul><li>{@link ResponsibleParty} /
         * {@link ResponsibleParty#getIndividualName() individualName}</li></ul>
         */
        public final String NAME;

        /**
         * The attribute name for the responsible's institution, or {@code null} if none.
         * Possible value is {@code "institution"}.
         * <p>
         * <b>Path:</b> <ul><li>{@link ResponsibleParty} /
         * {@link ResponsibleParty#getOrganisationName() organisationName}</li></ul>
         */
        public final String INSTITUTION;

        /**
         * The attribute name for the responsible's URL. Possible values are
         * {@code "creator_url"}, {@code "contributor_url"} or {@code "publisher_url"}.
         * <p>
         * <b>Path:</b> <ul><li>{@link ResponsibleParty} /
         * {@link ResponsibleParty#getContactInfo() contactInfo} /
         * {@link Contact#getOnlineResource() onlineResource} /
         * {@link OnlineResource#getLinkage() linkage}</li></ul>
         */
        public final String URL;

        /**
         * The attribute name for the responsible's email address. Possible values are
         * {@code "creator_email"}, {@code "contributor_email"} or {@code "publisher_email"}.
         * <p>
         * <b>Path:</b> <ul><li>{@link ResponsibleParty} /
         * {@link ResponsibleParty#getContactInfo() contactInfo} /
         * {@link Contact#getAddress() address} /
         * {@link Address#getElectronicMailAddresses() electronicMailAddress}</li></ul>
         */
        public final String EMAIL;

        /**
         * The attribute name for the responsible's role, or {@code null} if none.
         * Possible value is {@code "contributor_role"}.
         * <p>
         * <b>Path:</b> <ul><li>{@link ResponsibleParty} /
         * {@link ResponsibleParty#getRole()}</li></ul>
         *
         * @see Role
         */
        public final String ROLE;

        /**
         * The role to use as a fallback if no attribute value is associated to the {@link #ROLE} key.
         */
        public final Role DEFAULT_ROLE;

        /**
         * Creates a new set of attribute names. Any argument can be {@code null}
         * if not applicable.
         *
         * @param name        The attribute name for the responsible's name.
         * @param institution The attribute name for the responsible's institution.
         * @param url         The attribute name for the responsible's URL.
         * @param email       The attribute name for the responsible's email address.
         * @param role        The attribute name for the responsible's role.
         * @param defaultRole The role to use as a fallback if no attribute value is associated to the
         *                    {@code role} key.
         */
        public Responsible(final String name, final String institution, final String url, final String email,
                final String role, final Role defaultRole)
        {
            NAME         = name;
            INSTITUTION  = institution;
            URL          = url;
            EMAIL        = email;
            ROLE         = role;
            DEFAULT_ROLE = defaultRole;
        }
    }

    /**
     * The set of attribute names for the creator (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} with {@link Role#ORIGINATOR}</li></ul>
     *
     * @see #CONTRIBUTOR
     * @see #PUBLISHER
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#creator_name_Attribute">UCAR reference</a>
     */
    public static final Responsible CREATOR = new Responsible("creator_name",
            "institution", "creator_url", "creator_email", null, Role.ORIGINATOR);

    /**
     * The set of attribute names for the contributor (<em>Suggested</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation}</li></ul>
     *
     * @see #CREATOR
     * @see #PUBLISHER
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#contributor_name_Attribute">UCAR reference</a>
     */
    public static final Responsible CONTRIBUTOR = new Responsible("contributor_name",
            null, "contributor_url", "contributor_email", "contributor_role", null);

    /**
     * The set of attribute names for the publisher (<em>Suggested</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getDistributionInfo() distributionInfo} /
     * {@link Distribution#getDistributors() distributors} /
     * {@link Distributor#getDistributorContact() distributorContact} with {@link Role#PUBLISHER}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getKeywords() keyword} with the {@code "dataCenter"} {@link KeywordType}</li></ul>
     *
     * @see #CREATOR
     * @see #CONTRIBUTOR
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#publisher_name_Attribute">UCAR reference</a>
     */
    public static final Responsible PUBLISHER = new Responsible("publisher_name",
            null, "publisher_url", "publisher_email", null, Role.PUBLISHER);

    /**
     * The {@value} attribute name for the scientific project that produced the data
     * (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getDescriptiveKeywords() descriptiveKeywords} /
     * {@link Keywords#getKeywords() keyword} with the {@code "project"} {@link KeywordType}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#project_Attribute">UCAR reference</a>
     */
    public static final String PROJECT = "project";

    /**
     * The {@value} attribute name for the summary of the intentions with which the resource(s)
     * was developed.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getPurpose() purpose}</li></ul>
     */
    public static final String PURPOSE = "purpose";

    /**
     * The {@value} attribute name for bibliographical references.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCitation() citation} in its own instance</li></ul>
     */
    public static final String REFERENCES = "references";

    /**
     * The {@value} attribute name for a textual description of the processing (or quality control)
     * level of the data.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getContentInfo() contentInfo} /
     * {@link ImageDescription#getProcessingLevelCode() processingLevelCode}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#processing_level_Attribute">UCAR reference</a>
     */
    public static final String PROCESSING_LEVEL = "processing_level";

    /**
     * The {@value} attribute name for a place to acknowledge various type of support for
     * the project that produced this data (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getCredits() credit}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#acknowledgement_Attribute">UCAR reference</a>
     */
    public static final String ACKNOWLEDGMENT = "acknowledgment";

    /**
     * The {@value} attribute name for a description of the restrictions to data access
     * and distribution (<em>Recommended</em>).
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getResourceConstraints() resourceConstraints} /
     * {@link LegalConstraints#getUseLimitations() useLimitation}</li></ul>
     *
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#license_Attribute">UCAR reference</a>
     */
    public static final String LICENSE = "license";

    /**
     * The {@value} attribute name for the access constraints applied to assure the protection of
     * privacy or intellectual property. Typical values are {@code "copyright"}, {@code "patent"},
     * {@code "patent pending"}, {@code "trademark"}, {@code "license"},
     * {@code "intellectual property rights"} or {@code "restricted"}.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getResourceConstraints() resourceConstraints} /
     * {@link LegalConstraints#getAccessConstraints() accessConstraints}</li></ul>
     *
     * @see Restriction
     */
    public static final String ACCESS_CONSTRAINT = "acces_constraint";

    /**
     * The {@value} attribute name for an identifier of the geographic area.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getExtents() extent} /
     * {@link Extent#getGeographicElements() geographicElement} /
     * {@link GeographicDescription#getGeographicIdentifier() geographicIdentifier}</li></ul>
     */
    public static final String GEOGRAPHIC_IDENTIFIER = "geographic_identifier";

    /**
     * Holds the attribute names describing a simple latitude, longitude, and vertical bounding box.
     * Values are:
     * <p>
     * <table border="1" cellspacing="0"><tr bgcolor="lightblue">
     *   <th>Attributes</th>
     *   <th>{@link NetcdfTranscoder#LATITUDE}</th>
     *   <th>{@link NetcdfTranscoder#LONGITUDE}</th>
     *   <th>{@link NetcdfTranscoder#VERTICAL}</th>
     *   <th>{@link NetcdfTranscoder#TIME}</th>
     * </tr><tr>
     *   <td>{@link #MINIMUM}</td>
     *   <td>{@code "geospatial_lat_min"}</td>
     *   <td>{@code "geospatial_lon_min"}</td>
     *   <td>{@code "geospatial_vertical_min"}</td>
     *   <td>{@code "time_coverage_start"}</td>
     * </tr><tr>
     *   <td>{@link #MAXIMUM}</td>
     *   <td>{@code "geospatial_lat_max"}</td>
     *   <td>{@code "geospatial_lon_max"}</td>
     *   <td>{@code "geospatial_vertical_max"}</td>
     *   <td>{@code "time_coverage_end"}</td>
     * </tr><tr>
     *   <td>{@link #SPAN}</td>
     *   <td></td>
     *   <td></td>
     *   <td></td>
     *   <td>{@code "time_coverage_duration"}</td>
     * </tr><tr>
     *   <td>{@link #RESOLUTION}</td>
     *   <td>{@code "geospatial_lat_resolution"}</td>
     *   <td>{@code "geospatial_lon_resolution"}</td>
     *   <td>{@code "geospatial_vertical_resolution"}</td>
     *   <td>{@code "time_coverage_resolution"}</td>
     * </tr><tr>
     *   <td>{@link #UNITS}</td>
     *   <td>{@code "geospatial_lat_units"}</td>
     *   <td>{@code "geospatial_lon_units"}</td>
     *   <td>{@code "geospatial_vertical_units"}</td>
     *   <td>{@code "time_coverage_units"}</td>
     * </tr><tr>
     *   <td>{@link #POSITIVE}</td>
     *   <td></td>
     *   <td></td>
     *   <td>{@code "geospatial_vertical_positive"}</td>
     *   <td></td>
     * </tr></table>
     *
     * {@note The member names in this class are upper-cases because they should be considered
     *        as constants. For example <code>NetcdfTranscoder.LATITUDE.MINIMUM</code> maps exactly to
     *        the <code>"geospatial_lat_min"</code> string and nothing else. A lower-case
     *        <code>minimum</code> member name could be misleading since it would suggest that
     *        the field contains the actual name value rather than the key by which the value
     *        is identified in a NetCDF file.}
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.20
     *
     * @since 3.20
     * @module
     */
    public static class Dimension {
        /**
         * The attribute name for the minimal value of the bounding box (<em>Recommended</em>).
         * Possible values are {@code "geospatial_lat_min"}, {@code "geospatial_lon_min"},
         * {@code "geospatial_vertical_min"} and {@code "time_coverage_start"}.
         */
        public final String MINIMUM;

        /**
         * The attribute name for the maximal value of the bounding box (<em>Recommended</em>).
         * Possible values are {@code "geospatial_lat_max"}, {@code "geospatial_lon_max"},
         * {@code "geospatial_vertical_max"} and {@code "time_coverage_end"}.
         */
        public final String MAXIMUM;

        /**
         * The attribute name for the difference between the minimal and maximal values.
         * Possible value is {@code "time_coverage_duration"}.
         */
        public final String SPAN;

        /**
         * The attribute name for a further refinement of the geospatial bounding box
         * (<em>Suggested</em>). Possible values are {@code "geospatial_lat_resolution"},
         * {@code "geospatial_lon_resolution"}, {@code "geospatial_vertical_resolution"}
         * and {@code "time_coverage_resolution"}.
         */
        public final String RESOLUTION;

        /**
         * The attribute name for the bounding box units of measurement.
         * Possible values are {@code "geospatial_lat_units"}, {@code "geospatial_lon_units"},
         * {@code "geospatial_vertical_units"} and {@code "time_coverage_units"}.
         */
        public final String UNITS;

        /**
         * The attribute name for indicating which direction is positive (<em>Suggested</em>).
         * Possible value is {@code "geospatial_vertical_positive"}.
         */
        public final String POSITIVE;

        /**
         * Creates a new set of attribute names.
         *
         * @param min        The attribute name for the minimal value of the bounding box.
         * @param max        The attribute name for the maximal value of the bounding box.
         * @param span       The attribute name for the difference between the minimal and maximal values.
         * @param resolution The attribute name for a further refinement of the geospatial bounding box.
         * @param units      The attribute name for the bounding box units of measurement.
         * @param positive   The attribute name for indicating which direction is positive.
         */
        public Dimension(final String min, final String max, final String span, final String resolution,
                final String units, final String positive)
        {
            MINIMUM    = min;
            MAXIMUM    = max;
            SPAN       = span;
            RESOLUTION = resolution;
            UNITS      = units;
            POSITIVE   = positive;
        }
    }

    /**
     * The set of attribute names for the minimal and maximal latitudes of the bounding box,
     * resolution and units. Latitudes are assumed to be in decimal degrees north, unless a
     * units attribute is specified.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getExtents() extent} /
     * {@link Extent#getGeographicElements() geographicElement} /
     * {@link GeographicBoundingBox#getSouthBoundLatitude() southBoundLatitude} or
     * {@link GeographicBoundingBox#getNorthBoundLatitude() northBoundLatitude}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getSpatialRepresentationInfo() spatialRepresentationInfo} /
     * {@link GridSpatialRepresentation#getAxisDimensionProperties() axisDimensionProperties} /
     * {@link org.opengis.metadata.spatial.Dimension#getResolution() resolution}</li></ul>
     *
     * @see #LONGITUDE
     * @see #VERTICAL
     * @see #TIME
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#geospatial_lat_min_Attribute">UCAR reference</a>
     */
    public static final Dimension LATITUDE = new Dimension("geospatial_lat_min",
            "geospatial_lat_max", null, "geospatial_lat_resolution", "geospatial_lat_units", null);

    /**
     * The set of attribute names for the minimal and maximal longitudes of the bounding box,
     * resolution and units. Longitudes are assumed to be in decimal degrees east, unless a
     * units attribute is specified.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getExtents() extent} /
     * {@link Extent#getGeographicElements() geographicElement} /
     * {@link GeographicBoundingBox#getWestBoundLongitude() westBoundLongitude} or
     * {@link GeographicBoundingBox#getEastBoundLongitude() eastBoundLongitude}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getSpatialRepresentationInfo() spatialRepresentationInfo} /
     * {@link GridSpatialRepresentation#getAxisDimensionProperties() axisDimensionProperties} /
     * {@link org.opengis.metadata.spatial.Dimension#getResolution() resolution}</li></ul>
     *
     * @see #LATITUDE
     * @see #VERTICAL
     * @see #TIME
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#geospatial_lon_min_Attribute">UCAR reference</a>
     */
    public static final Dimension LONGITUDE = new Dimension("geospatial_lon_min",
            "geospatial_lon_max", null, "geospatial_lon_resolution", "geospatial_lon_units", null);

    /**
     * The set of attribute names for the minimal and maximal elevations of the bounding box,
     * resolution and units. Elevations are assumed to be in metres above the ground, unless a
     * units attribute is specified.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getExtents() extent} /
     * {@link Extent#getVerticalElements() verticalElement} /
     * {@link VerticalExtent#getMinimumValue() minimumValue} or
     * {@link VerticalExtent#getMaximumValue() maximumValue}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getSpatialRepresentationInfo() spatialRepresentationInfo} /
     * {@link GridSpatialRepresentation#getAxisDimensionProperties() axisDimensionProperties} /
     * {@link org.opengis.metadata.spatial.Dimension#getResolution() resolution}</li></ul>
     *
     * @see #LATITUDE
     * @see #LONGITUDE
     * @see #TIME
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#geospatial_vertical_min_Attribute">UCAR reference</a>
     */
    public static final Dimension VERTICAL = new Dimension("geospatial_vertical_min",
            "geospatial_vertical_max", null, "geospatial_vertical_resolution",
            "geospatial_vertical_units", "geospatial_vertical_positive");

    /**
     * The set of attribute names for the start and end times of the bounding box, resolution and
     * units.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getIdentificationInfo() identificationInfo} /
     * {@link DataIdentification#getExtents() extent} /
     * {@link Extent#getTemporalElements() temporalElement} /
     * {@link TemporalExtent#getExtent() extent}</li>
     * <li>{@link Metadata} /
     * {@link Metadata#getSpatialRepresentationInfo() spatialRepresentationInfo} /
     * {@link GridSpatialRepresentation#getAxisDimensionProperties() axisDimensionProperties} /
     * {@link org.opengis.metadata.spatial.Dimension#getResolution() resolution}</li></ul>
     *
     * @see #LATITUDE
     * @see #LONGITUDE
     * @see #VERTICAL
     * @see <a href="http://www.unidata.ucar.edu/software/netcdf-java/formats/DataDiscoveryAttConvention.html#time_coverage_start_Attribute">UCAR reference</a>
     */
    public static final Dimension TIME = new Dimension("time_coverage_start", "time_coverage_end",
            "time_coverage_duration", "time_coverage_resolution", "time_coverage_units", null);

    /**
     * The {@value} attribute name for the designation associated with a range element.
     * This attribute can be associated to {@linkplain VariableSimpleIF variables}. If
     * specified, they shall be one flag name for each {@linkplain #FLAG_MASKS flag mask},
     * {@linkplain #FLAG_VALUES flag value} and {@linkplain #FLAG_MEANINGS flag meaning}.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getContentInfo() contentInfo} /
     * {@link CoverageDescription#getRangeElementDescriptions() rangeElementDescription} /
     * {@link RangeElementDescription#getName() name}</li></ul>
     */
    public static final String FLAG_NAMES = "flag_names";

    /**
     * The {@value} attribute name for bitmask to apply on sample values before to compare
     * them to the {@linkplain #FLAG_VALUES flag values}.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getContentInfo() contentInfo} /
     * {@link CoverageDescription#getRangeElementDescriptions() rangeElementDescription} /
     * {@link RangeElementDescription#getRangeElements() rangeElement}</li></ul>
     */
    public static final String FLAG_MASKS = "flag_masks";

    /**
     * The {@value} attribute name for sample values to be flagged. The {@linkplain #FLAG_MASKS
     * flag masks}, flag values and {@linkplain #FLAG_MEANINGS flag meaning} attributes, used
     * together, describe a blend of independent boolean conditions and enumerated status codes.
     * A flagged condition is identified by a bitwise AND of the variable value and each flag masks
     * value; a result that matches the flag values value indicates a true condition.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getContentInfo() contentInfo} /
     * {@link CoverageDescription#getRangeElementDescriptions() rangeElementDescription} /
     * {@link RangeElementDescription#getRangeElements() rangeElement}</li></ul>
     */
    public static final String FLAG_VALUES = "flag_values";

    /**
     * The {@value} attribute name for the meaning of {@linkplain #FLAG_VALUES flag values}.
     * Each flag values and flag masks must coincide with a flag meanings.
     * <p>
     * <b>Path:</b> <ul><li>{@link Metadata} /
     * {@link Metadata#getContentInfo() contentInfo} /
     * {@link CoverageDescription#getRangeElementDescriptions() rangeElementDescription} /
     * {@link RangeElementDescription#getDefinition() definition}</li></ul>
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
     * The name factory, created when first needed.
     */
    private transient NameFactory nameFactory;

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
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageReader} or
     *              {@link org.geotoolkit.image.io.SpatialImageWriter} object using this
     *              transcoder, or {@code null}.
     */
    public NetcdfTranscoder(final NetcdfFile file, final WarningProducer owner) {
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
        this.groups = XArrays.resize(groups, count);
    }

    /**
     * Reports a warning.
     *
     * @param method    The method in which the warning occurred.
     * @param exception The exception to log.
     */
    private void warning(final String method, final Exception exception) {
        Warnings.log(this, Level.WARNING, NetcdfTranscoder.class, method, exception);
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
     * <p>
     * Subclasses can override this method if they need to complete the information.
     *
     * @return The globally unique identifier, or {@code null} if none.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    public Identifier getFileIdentifier() throws IOException {
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
     * Implementation note: this method tries to reuse the existing {@link #creator} instance,
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
    private ResponsibleParty createResponsibleParty(final Group group, final Responsible keys)
            throws IOException
    {
        final String individualName   = getStringValue(group, keys.NAME);
        final String organisationName = getStringValue(group, keys.INSTITUTION);
        final String email            = getStringValue(group, keys.EMAIL);
        final String url              = getStringValue(group, keys.URL);
        if (individualName == null && organisationName == null && email == null && url == null) {
            return null;
        }
        Role role = CodeLists.valueOf(Role.class, getStringValue(group, keys.ROLE));
        if (role == null) {
            role = keys.DEFAULT_ROLE;
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
     * is non-null. This method will reuse the {@link #creator} field, if non-null.
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
        if (title == null && identifier == null && creation == null && modified == null && issued == null && references == null) {
            return null;
        }
        final DefaultCitation citation = new DefaultCitation(title);
        if (identifier != null) {
            citation.getIdentifiers().add(identifier);
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
            final ResponsibleParty contributor = createResponsibleParty(group, CONTRIBUTOR);
            if (contributor != null && contributor != creator) {
                addIfAbsent(citation.getCitedResponsibleParties(), contributor);
            }
        }
        citation.setOtherCitationDetails(wrap(references));
        return citation;
    }

    /**
     * Creates a {@code DataIdentification} element if at least one of the required attributes
     * is non-null. This method will reuse the {@link #creator} field, if non-null.
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
                if (topic    != null) addIfAbsent(identification.getTopicCategories(), CodeLists.valueOf(TopicCategory.class, topic));
                if (type     != null) addIfAbsent(identification.getSpatialRepresentationTypes(), CodeLists.valueOf(SpatialRepresentationType.class, type));
                if (standard != null) addIfAbsent(identification.getDescriptiveKeywords(), standard);
                if (keywords != null) addIfAbsent(identification.getDescriptiveKeywords(), keywords);
                if (credits  != null) addIfAbsent(identification.getCredits(), credits);
                if (license  != null) addIfAbsent(identification.getResourceConstraints(), constraints = new DefaultLegalConstraints(license));
                if (access   != null) {
                    for (final String token : Strings.split(topic, ',')) {
                        if (!token.isEmpty()) {
                            if (constraints == null) {
                                identification.getResourceConstraints().add(constraints = new DefaultLegalConstraints());
                            }
                            addIfAbsent(constraints.getAccessConstraints(), CodeLists.valueOf(Restriction.class, token));
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
            if (citation==null && summary==null && purpose==null && project==null && publisher==null && creator==null) {
                return null;
            }
            identification = new DefaultDataIdentification();
        }
        identification.setCitation(citation);
        identification.setAbstract(wrap(summary));
        identification.setPurpose (wrap(purpose));
        if (creator != null) {
            identification.getPointOfContacts().add(creator);
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
            keywords.setType(CodeLists.valueOf(KeywordType.class, type));
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
     * @throws IOException If an I/O operation was necessary but failed.
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
                Dimension rsat = null;
                switch (at) {
                    case Lon:      rsat = LONGITUDE; // fallthrough
                    case GeoX:     type = DimensionNameType.COLUMN; break;
                    case Lat:      rsat = LATITUDE; // fallthrough
                    case GeoY:     type = DimensionNameType.ROW; break;
                    case Height:   rsat = VERTICAL;
                    case GeoZ:
                    case Pressure: type = DimensionNameType.VERTICAL; break;
                    case Time:     rsat = TIME; // fallthrough
                    case RunTime:  type = DimensionNameType.TIME; break;
                }
                if (rsat != null) {
                    final Number res = getNumericValue(rsat.RESOLUTION);
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
     * Creates a {@code <gmd:contentInfo>} element from all applicable NetCDF attributes.
     *
     * @return The content information.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    private CoverageDescription createContentInfo() throws IOException {
        final String processingLevel = getStringValue(PROCESSING_LEVEL);
        final DefaultCoverageDescription content;
        if (processingLevel != null) {
            content = new DefaultImageDescription();
            ((DefaultImageDescription) content).setProcessingLevelCode(new DefaultIdentifier(processingLevel));
        } else {
            content = new DefaultCoverageDescription();
        }
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
     * @throws IOException If an I/O operation was necessary but failed.
     */
    protected Band createSampleDimension(final VariableSimpleIF variable) throws IOException {
        final DefaultBand band = new DefaultBand();
        final String name = variable.getShortName();
        if (name != null) {
            if (nameFactory == null) {
                nameFactory = FactoryFinder.getNameFactory(null);
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
    @Override
    public Metadata readMetadata() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setMetadataStandardName("ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data");
        metadata.setMetadataStandardVersion("ISO 19115-2:2009(E)");
        final Identifier identifier = getFileIdentifier();
        if (identifier != null) {
            metadata.setFileIdentifier(identifier.getCode());
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
            final ResponsibleParty party = createResponsibleParty(group, CREATOR);
            if (party != null && party != creator) {
                addIfAbsent(metadata.getContacts(), party);
                if (creator == null) {
                    creator = party;
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
            final ResponsibleParty party = createResponsibleParty(group, PUBLISHER);
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
