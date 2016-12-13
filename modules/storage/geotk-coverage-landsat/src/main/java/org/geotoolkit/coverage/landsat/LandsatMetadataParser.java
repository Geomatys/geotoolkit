/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.coverage.landsat;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.opengis.coverage.Coverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.content.AttributeGroup;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.TransferFunctionType;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.util.FactoryException;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.internal.util.Constants;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.acquisition.DefaultAcquisitionInformation;
import org.apache.sis.metadata.iso.acquisition.DefaultInstrument;
import org.apache.sis.metadata.iso.acquisition.DefaultPlatform;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultCitationDate;
import org.apache.sis.metadata.iso.content.DefaultAttributeGroup;
import org.apache.sis.metadata.iso.content.DefaultBand;
import org.apache.sis.metadata.iso.content.DefaultImageDescription;
import org.apache.sis.metadata.iso.distribution.DefaultDistribution;
import org.apache.sis.metadata.iso.distribution.DefaultFormat;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.extent.DefaultTemporalExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultResolution;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.metadata.iso.lineage.DefaultProcessStep;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.crs.DefaultProjectedCRS;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.DefaultInternationalString;
import org.apache.sis.util.logging.Logging;

import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.referencing.operation.builder.LocalizationGrid;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.util.TimeParser;

import static org.geotoolkit.coverage.landsat.LandsatConstants.*;

/**
 * A helper class to parse Landsat metadata to build appropriate {@linkplain DefaultMetadata ISO19115 Metadata}
 * and also define if the current analysed file is {@linkplain #isValid() is valid} to build an appropriate
 * Landsat {@link Coverage} from its metadata file.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatMetadataParser {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.coverage");

    /**
     * {@link Path} link to Landsat Metadatas.
     */
    private final Path metadataPath;

    /**
     * The metadata ISO19115.
     *
     */
    private DefaultMetadata isoMetadata, reflectiveMetadatas, panchromaticMetadatas, thermalMetadatas;

    /**
     * {@link Map} which regroup all read metadatas fields.
     */
    private final Map<String, String> metaGroups;

    /**
     * 2D part of Projected CRS.
     */
    private CoordinateReferenceSystem projectedCRS2D;

    /**
     * Multidimensionnal Projected CRS.
     */
    private CoordinateReferenceSystem projectedCRS;

    /**
     * The acquisition data {@link Date}.
     *
     */
    private Date date;

    /**
     * Build a metadata parser for Landsat.
     *
     * Read the metadata file with the {@link StandardCharsets#US_ASCII} default charset.
     *
     * @param metadataPath path where the metadata file is stored.
     * @throws IOException
     */
    public LandsatMetadataParser(final Path metadataPath) throws IOException {
        this(metadataPath, StandardCharsets.US_ASCII);
    }

    /**
     * Build a metadata parser for Landsat.
     *
     * @param metadataPath path where the metadata file is stored.
     * @param charset
     * @throws IOException
     */
    public LandsatMetadataParser(final Path metadataPath, final Charset charset) throws IOException {
        ArgumentChecks.ensureNonNull("metadata Path", metadataPath);
        if (!Files.exists(metadataPath)){
            throw new IllegalArgumentException("metadata Path doesn't exist at path : "+metadataPath.toString());
        }
        this.metadataPath = metadataPath;
        metaGroups        = getMetadataGroups(charset);
    }

    /**
     * Travel metadatas and return {@code true} if metadata are adapted for Landsat
     * reading behavior else return {@code false}.<br><br>
     *
     * Validity criterion are :<br>
     * - band 2, 3 and 4 are referenced for RGB.<br>
     * - ellipsoid, datum, utm area for projected CRS.<br>
     * - latitude and longitudinal coordinates values for projected bounding box.
     *
     * @return {@code true} if minimum metadatas values  are referenced else return {@code false}.
     */
    public final boolean isValid() {
        //-- check minimum band existence.
        for (int i = 2; i < 5; i++) {
            //-- mandatory false to avoid unexpected search exception.
            final String value = getValue(false, "FILE_NAME_BAND_"+i);
            if (value == null) return false;
        }

        //-- crs labels
        //-- Datum
        final String datum = getValue(false, "DATUM");
        if (datum == null) return false;
        //-- Ellipsoid
        final String ellips = getValue(false, "ELLIPSOID");
        if (ellips == null) return false;
        //-- Map Projection Type
        final String mapProj = getValue(false, "MAP_PROJECTION");
        if (mapProj == null) return false;

        //-- projected Bounding box coordinates values.
        //-- longitude
        final String projWest = getValue(true, "CORNER_LL_PROJECTION_X_PRODUCT", "CORNER_UL_PROJECTION_X_PRODUCT");
        if (projWest == null) return false;
        final String projEst  = getValue(true, "CORNER_UR_PROJECTION_X_PRODUCT", "CORNER_LR_PROJECTION_X_PRODUCT");
        if (projEst == null) return false;

        //-- lattitude
        final String projSouth = getValue(true, "CORNER_LL_PROJECTION_Y_PRODUCT", "CORNER_LR_PROJECTION_Y_PRODUCT");
        if (projSouth == null) return false;
        final String projNorth = getValue(true, "CORNER_UR_PROJECTION_Y_PRODUCT", "CORNER_UL_PROJECTION_Y_PRODUCT");
        if (projNorth == null) return false;

        return true;
    }

    /**
     * Returns Landsat ISO19115 metadatas.
     *
     * @return
     * @throws FactoryException
     * @throws ParseException
     */
    public final DefaultMetadata getMetadata(final String groupName) throws FactoryException, ParseException {
        ArgumentChecks.ensureNonNull("Metadata group name", groupName);
        final DefaultMetadata filledMetadata = new DefaultMetadata();
        switch(groupName) {
            case GENERAL_LABEL : {
                if (isoMetadata != null) {
                    return isoMetadata;
                } else {
                    isoMetadata = filledMetadata;
                }
                break;
            }

            case REFLECTIVE_LABEL : {
                if (reflectiveMetadatas != null) {
                    return reflectiveMetadatas;
                } else {
                    reflectiveMetadatas = filledMetadata;
                }
                break;
            }

            case PANCHROMATIC_LABEL : {
                if (panchromaticMetadatas != null) {
                    return panchromaticMetadatas;
                } else {
                    panchromaticMetadatas = filledMetadata;
                }
                break;
            }

            case THERMAL_LABEL : {
                if (thermalMetadatas != null) {
                    return thermalMetadatas;
                } else {
                    thermalMetadatas = filledMetadata;
                }
                break;
            }
        }


        assert metaGroups != null;

        //----------------------------------------------------------------------//
        //------------------------ Mandatory metadata --------------------------//
        //----------------------------------------------------------------------//

        //-- set CRS
        filledMetadata.setReferenceSystemInfo(Collections.singleton(getCRS()));
        final Date metadataPublicationDate =  getDateInfo();
        if (metadataPublicationDate != null)
            filledMetadata.setDateStamp(metadataPublicationDate);
        //-- unique file identifier
        filledMetadata.setFileIdentifier(UUID.randomUUID().toString());
        //-- Iso metadatas 19115 generation date.
        filledMetadata.setDateStamp(new Date());

        //-- set bounding box
        final double[] bbCoords = getProjectedBound2D();
        final DefaultGeographicBoundingBox geo = new DefaultGeographicBoundingBox(bbCoords[0], bbCoords[1],  //-- long
                                                                                  bbCoords[2], bbCoords[3]); //-- lat

        final DefaultExtent ex = new DefaultExtent();
        ex.setGeographicElements(Arrays.asList(geo));

        //-- acquisition date
        final DefaultTemporalExtent tex = new DefaultTemporalExtent();
        final Date acquisitionDate = getAcquisitionDate();
        tex.setBounds(acquisitionDate, acquisitionDate);
        ex.setTemporalElements(Arrays.asList(tex));

        //-- temporal extent
        final NamedIdentifier extentName = new NamedIdentifier(Citations.CRS, "Landsat extent");
        final Map<String, Object> propertiesExtent = new HashMap<>();
        propertiesExtent.put(IdentifiedObject.NAME_KEY, extentName);

        final NamedIdentifier extentBeginName = new NamedIdentifier(Citations.CRS, "Landsat extent");
        final Map<String, Object> propertiesBegin = new HashMap<>();
        propertiesBegin.put(IdentifiedObject.NAME_KEY, extentBeginName);

        final NamedIdentifier extentEnd = new NamedIdentifier(Citations.CRS, "Landsat extent");
        final Map<String, Object> propertiesEnd = new HashMap<>();
        propertiesEnd.put(IdentifiedObject.NAME_KEY, extentEnd);
        tex.setExtent(new DefaultPeriod(propertiesExtent, new DefaultInstant(propertiesBegin, acquisitionDate), new DefaultInstant(propertiesEnd, acquisitionDate)));


        //-- all minimum mandatory metadatas.
        //-- geographic extent
        final DefaultDataIdentification ddii = new DefaultDataIdentification();
        ddii.setExtents(Arrays.asList(ex));
        //-- comment about data
        final String abstractComment = getValue(true, "ORIGIN");
        ddii.setAbstract(new DefaultInternationalString(abstractComment));
        //-- scene title
        final String title = "Generales Landsat8 metadatas for : "+getValue(true, LandsatConstants.SCENE_ID);
        final DefaultCitation titleCitation = new DefaultCitation(title);
        //-- dates
        Set<DefaultCitationDate> dateset = new HashSet<>();
        dateset.add(new DefaultCitationDate(acquisitionDate, DateType.CREATION));
        dateset.add(new DefaultCitationDate(metadataPublicationDate, DateType.PUBLICATION));
        titleCitation.setDates(dateset);
        ddii.setCitation(titleCitation);

        //-- Resolution
        if (!groupName.equalsIgnoreCase(GENERAL_LABEL)) {
            final String reres = getValue(false, RESOLUTION_LABEL+groupName);
            if (reres != null) {
                HashSet<Resolution> res = new HashSet<Resolution>();
                final DefaultResolution defaultResolution = new DefaultResolution();
                defaultResolution.setDistance(Double.valueOf(reres));
                res.add(defaultResolution);
                ddii.setSpatialResolutions(res);
            }
        }

        filledMetadata.setIdentificationInfo(Arrays.asList(ddii));

        /**
         * Three different Images Descriptions.
         * - Reflective
         * - Panchromatic
         * - Thermal
         */
        //-- Reflective description.
        final DefaultImageDescription reflectiveImgDesc = new DefaultImageDescription();

        final DefaultAttributeGroup dAGReflectiveRef = new DefaultAttributeGroup();
        dAGReflectiveRef.setAttributes(getBandsInfos("REFLECTIVE", "REFLECTANCE"));
        final DefaultAttributeGroup dAGReflectiveRad = new DefaultAttributeGroup();
        dAGReflectiveRad.setAttributes(getBandsInfos("REFLECTIVE", "RADIANCE"));
        final Set<AttributeGroup> reflectiveInfos = new HashSet<>();
        reflectiveInfos.add(dAGReflectiveRef);
        reflectiveInfos.add(dAGReflectiveRad);
        reflectiveImgDesc.setAttributeGroups(reflectiveInfos);

        //-- Panchromatic image description.
        final DefaultImageDescription panchroImgDesc = new DefaultImageDescription();
        final DefaultAttributeGroup dAGPanchromaRef  = new DefaultAttributeGroup();
        dAGPanchromaRef.setAttributes(getBandsInfos("PANCHROMATIC", "REFLECTANCE"));
        final DefaultAttributeGroup dAGPanchromaRad  = new DefaultAttributeGroup();
        dAGPanchromaRad.setAttributes(getBandsInfos("PANCHROMATIC", "RADIANCE"));
        final Set<AttributeGroup> panchroInfos = new HashSet<>();
        panchroInfos.add(dAGPanchromaRef);
        panchroInfos.add(dAGPanchromaRad);
        panchroImgDesc.setAttributeGroups(panchroInfos);

        //-- Thermal descriptions. (only define with Radiance)
        final DefaultImageDescription thermalImgDesc = new DefaultImageDescription();
        final DefaultAttributeGroup dAGThermalRad    = new DefaultAttributeGroup();
        dAGThermalRad.setAttributes(getBandsInfos("THERMAL", "RADIANCE"));
        thermalImgDesc.setAttributeGroups(Collections.singleton(dAGThermalRad));

        //-- image description
        final String cloud = getValue(false, "CLOUD_COVER");
        if (cloud != null) {
            final double val = Double.valueOf(cloud);
            reflectiveImgDesc.setCloudCoverPercentage(val);
            panchroImgDesc.setCloudCoverPercentage(val);
            thermalImgDesc.setCloudCoverPercentage(val);
        }

        final String sunAz = getValue(false, "SUN_AZIMUTH");
        if (sunAz != null) {
            final double val = Double.valueOf(sunAz);
            reflectiveImgDesc.setIlluminationAzimuthAngle(val);
            panchroImgDesc.setIlluminationAzimuthAngle(val);
            thermalImgDesc.setIlluminationAzimuthAngle(val);
        }

        final String sunEl = getValue(false, "SUN_ELEVATION");
        if (sunEl != null) {
            final double val = Double.valueOf(sunEl);
            reflectiveImgDesc.setIlluminationElevationAngle(val);
            panchroImgDesc.setIlluminationElevationAngle(val);
            thermalImgDesc.setIlluminationElevationAngle(val);
        }

        final HashSet<ImageDescription> imgDescriptions = new HashSet<>();
        switch (groupName) {
            case REFLECTIVE_LABEL   : imgDescriptions.add(reflectiveImgDesc);break;
            case PANCHROMATIC_LABEL : imgDescriptions.add(panchroImgDesc);break;
            case THERMAL_LABEL      : imgDescriptions.add(thermalImgDesc);break;
            default : {
                imgDescriptions.add(reflectiveImgDesc);
                imgDescriptions.add(panchroImgDesc);
                imgDescriptions.add(thermalImgDesc);
            }
        }

        filledMetadata.setContentInfo(imgDescriptions);


        //----------------------------------------------------------------------//
        //------------------------- optional metadatas -------------------------//
        //----------------------------------------------------------------------//

        //-- set metadata Date publication
        filledMetadata.setDateInfo(Collections.singleton(new DefaultCitationDate(metadataPublicationDate, DateType.PUBLICATION)));

        //-- Distribution informations
        final DefaultDistribution distribution = new DefaultDistribution();
        final String origin = getValue(false, "ORIGIN");
        if (origin != null) distribution.setDescription(new DefaultInternationalString(origin));

        final String outputFormat       = getValue(false, "OUTPUT_FORMAT");
        final String processSoftVersion = getValue(false, "PROCESSING_SOFTWARE_VERSION");
        if ((outputFormat != null) && (processSoftVersion != null)){
            distribution.setDistributionFormats(Collections.singleton(new DefaultFormat(outputFormat, processSoftVersion)));
        }

        filledMetadata.setDistributionInfo(Collections.singleton(distribution));

        //-- Aquisition informations
        final DefaultAcquisitionInformation dAI = new DefaultAcquisitionInformation();
        //-- platform
        final DefaultPlatform platform = new DefaultPlatform();
        final String platF = getValue(false, "SPACECRAFT_ID");
        if (platF != null){
            platform.setCitation(new DefaultCitation());
        }

        //-- instrument
        final DefaultInstrument instru = new DefaultInstrument();
        final String instrum = getValue(false, "SENSOR_ID");
        if (instrum != null) {
            instru.setType(new DefaultInternationalString(instrum));
        }

        if (platF != null && instrum != null) {
            //-- set related founded instrument and platform
            //*****************************************************************//
            //-- a cycle is define here, platform -> instru and instru -> platform
            //-- like a dad know his son and a son know his dad.
            //-- during xml binding a cycle is not supported for the current Apach SIS version
            //-- decomment this row when upgrade SIS version
            //instru.setMountedOn(platform);
            //*****************************************************************//

            platform.setInstruments(Collections.singleton(instru));

            dAI.setPlatforms(Collections.singleton(platform));
            dAI.setInstruments(Collections.singleton(instru));

            filledMetadata.setAcquisitionInformation(Collections.singleton(dAI));
        }

        //-- additionnal informations about thermic band metadatas.
        if (groupName.equalsIgnoreCase(THERMAL_LABEL)) {
            final Set<ProcessStep> extendedInfos = getThermicInfos();
            if (!extendedInfos.isEmpty()) {
                final DefaultLineage defaultLineage = new DefaultLineage();
                defaultLineage.setProcessSteps(extendedInfos);
                filledMetadata.setResourceLineages(Collections.singleton(defaultLineage));
            }
        }

        return filledMetadata;
    }

    /**
     * Return a map of the metadata fields.
     * @return
     */
    public Map<String, String> getKeyValueMap(){
        return Collections.unmodifiableMap(metaGroups);
    }

    //-------------------------------------------------------------------------//
    //                          Mandatory metadatas                            //
    //-------------------------------------------------------------------------//

    /**
     * Returns {@link Collections} of Landsat Reflective {@link Band}.
     * Means metadatas about band (1 to 7 and 9).
     *
     * @param groupName name of group, expected name are : PANCHROMATIC or REFLECTIVE or THERMAL.
     * @param reflectanceOrRadiance band metadata for REFLECTANCE or RADIANCE band attributs.
     * @return {@link Collections} of Landsat Reflective {@link Band}.
     */
    private Set<DefaultBand> getBandsInfos(final String groupName, final String reflectanceOrRadiance) {
        int[] indexBands = null;

        switch(groupName) {
            case PANCHROMATIC_LABEL : {
                indexBands = new int[]{8};
                break;
            }
            case REFLECTIVE_LABEL : {
                indexBands = new int[]{1, 2, 3, 4, 5, 6, 7, 9};
                break;
            }
            case THERMAL_LABEL : {
                indexBands = new int[]{10, 11};
                break;
            }
            default : throw new IllegalStateException("Group Name should be : PANCHROMATIC or REFLECTIVE or THERMAL.");
        }
        return getBandsInfos(groupName, reflectanceOrRadiance, indexBands);
    }

    /**
     * Returns all bands informations from metadata text file.
     *
     * @param groupName
     * @param reflectanceOrRadiance
     * @param bandsIndex
     * @return
     */
    private Set<DefaultBand> getBandsInfos(final String groupName, final String reflectanceOrRadiance, final int ...bandsIndex) {
        final HashSet<DefaultBand> bands = new HashSet<>();
        for (int i : bandsIndex) {
            bands.add(getBandInfos(i, groupName, reflectanceOrRadiance));
        }
        return bands;
    }

    /**
     * Build an appropriate metadatas {@link DefaultBand} properties.
     *
     * @param bandIndex Landsat band index.
     * @param reflectanceOrRadiance metadata for RADIANCE or REFLECTANCE case.
     * @return appropriate metadatas {@link DefaultBand} properties.
     */
    private DefaultBand getBandInfos(final int bandIndex, final String groupName, final String reflectanceOrRadiance) {
        final DefaultBand df = new DefaultBand();
        df.setTransferFunctionType(TransferFunctionType.LINEAR);

        final String reflectBandLabel = BAND_NAME_LABEL+bandIndex;
        final String reflectBandName = getValue(true, reflectBandLabel);
        df.setNames(Collections.singleton(new DefaultIdentifier(reflectBandName)));

        //-- minimum pixel band value
        final String minPBLabel = SAMPLE_MIN_LABEL+bandIndex;
        final String minPBVal   =  getValue(false, minPBLabel);
        if (minPBVal != null) df.setMinValue(Double.valueOf(minPBVal));

        //-- maximum pixel band value
        final String maxPBLabel = SAMPLE_MAX_LABEL+bandIndex;
        final String maxPBVal   =  getValue(false, maxPBLabel);
        if (maxPBVal != null) df.setMaxValue(Double.valueOf(maxPBVal));

        //-- scale factor
        final String sfRBLabel = reflectanceOrRadiance+SCALE_LABEL+bandIndex;
        final String sfRBVal   =  getValue(false, sfRBLabel);
        if (sfRBVal != null) df.setScaleFactor(Double.valueOf(sfRBVal));

        //-- offset
        final String offRBLabel = reflectanceOrRadiance+OFFSET_LABEL+bandIndex;
        final String offRBVal   = getValue(false, offRBLabel);
        if (offRBVal != null) df.setOffset(Double.valueOf(offRBVal));

        //-- resolution
        final String gridCellSizeLabel = RESOLUTION_LABEL+groupName;
        final String gridCellSizeValue = getValue(false, gridCellSizeLabel);
        if (gridCellSizeValue != null) df.setNominalSpatialResolution(Double.valueOf(gridCellSizeValue));

        return df;
    }

    /**
     * Extract from given metadatas {@link Map} needed value to compute an
     * appropriate2D part of {@link CoordinateReferenceSystem}.
     *
     * @param metaMap {@link Map} which contain all metadatas fields.
     * @return stipuled metadata CRS.
     * @throws FactoryException if impossible to compute CRS.
     */
    private CoordinateReferenceSystem getCRS2D() throws FactoryException {

        if (projectedCRS2D != null)
            return projectedCRS2D;

        //-- Datum
        final String datum = getValue(true, "DATUM");

        //-- Ellipsoid
        final String ellips = getValue(true, "ELLIPSOID");

        if (!(("WGS84".equalsIgnoreCase(datum)) && ("WGS84".equalsIgnoreCase(ellips)))){
            throw new IllegalStateException("Comportement not supported : expected Datum and Ellipsoid value WGS84, found Datum = "+datum+", Ellipsoid : "+ellips);
        }

        final String projType = getValue(true, "MAP_PROJECTION");

        switch (projType) {
            case "UTM" : {
                /**
                 * From Landsat specification, normaly Datum and ellipsoid are always WGS84.
                 * UTM area is the only thing which change.
                 * Thereby we build a CRS from basic 32600 and we add read UTM area.
                 * For example if UTM area is 45 we decode 32645 CRS from EPSG database.
                 */
                final String utm_Zone = getValue(true, "UTM_ZONE");

                final Integer utm = Integer.valueOf(utm_Zone);
                ArgumentChecks.ensureBetween(datum, 0, 60, utm);
                final NumberFormat nf = new DecimalFormat("##");
                final String utmFormat = nf.format(utm);

                projectedCRS2D = CRS.forCode("EPSG:326"+utmFormat);
                break;
            }
            case "PS" : {

                final String originLongitude   = getValue(true, "VERTICAL_LON_FROM_POLE");
                final String trueLatitudeScale = getValue(true, "TRUE_SCALE_LAT");
                final String falseEasting      = getValue(true, "FALSE_EASTING");
                final String falseNorthing     = getValue(true, "FALSE_NORTHING");

                final OperationMethod method = DefaultFactories.forBuildin(CoordinateOperationFactory.class)
                    .getOperationMethod("Polar Stereographic (variant B)");

                final ParameterValueGroup psParameters = method.getParameters().createValue();

                psParameters.parameter(Constants.STANDARD_PARALLEL_1).setValue(Double.valueOf(trueLatitudeScale));
                psParameters.parameter(Constants.CENTRAL_MERIDIAN).setValue(Double.valueOf(originLongitude));
                psParameters.parameter(Constants.FALSE_EASTING).setValue(Double.valueOf(falseEasting));
                psParameters.parameter(Constants.FALSE_NORTHING).setValue(Double.valueOf(falseNorthing));

                final Map<String, String> properties = Collections.singletonMap("name", "Landsat 8 polar stereographic");

                //-- define mathematical formula to pass from Geographic Base CRS to projected Coordinate space.
                final DefaultConversion projection = new DefaultConversion(properties, method, null, psParameters);

                projectedCRS2D = new DefaultProjectedCRS(properties, CommonCRS.WGS84.normalizedGeographic(), projection, PredefinedCS.PROJECTED);
                break;
            }
            default : throw new IllegalStateException("Comportement not supported : expected MAP_PROJECTION values are : PS or UTM, found : "+projType);
        }

        return projectedCRS2D;
    }

    /**
     * Returns the data acquisition {@link Date}.<br>
     *
     * May returns {@code null} if no date are stipulate from metadata file.
     *
     * @return the data acquisition {@link Date}.
     * @throws ParseException if problem during Date parsing.
     */
    private Date getAcquisitionDate() throws ParseException {
        if (date != null)
            return date;

        /**
         * Get temporales acquisition informations.
         */
        //-- year month day
        final String dateAcquired  = getValue(false, "DATE_ACQUIRED");

        if (dateAcquired == null)
            return null;

        //-- hh mm ss:ms
        final String sceneCenterTime = getValue(false, "SCENE_CENTER_TIME");

        String strDate = dateAcquired;
        if (sceneCenterTime != null)
            strDate = dateAcquired+"T"+sceneCenterTime;

        date = TimeParser.toDate(strDate);
        return date;
    }

    /**
     * Returns the internaly extent bounding box coordinates.
     * Coordinates are organize as follow : {west, est, south, north}.
     *
     * @return the internaly extent bounding box coordinates.
     */
    private double[] getProjectedBound2D() {

        //-- longitude
        final String lowWest = getValue(true, "CORNER_LL_LON_PRODUCT", "CORNER_UL_LON_PRODUCT");
        final String upWest  = getValue(true, "CORNER_UL_LON_PRODUCT", "CORNER_LL_LON_PRODUCT");
        final String lowEst  = getValue(true, "CORNER_LR_LON_PRODUCT", "CORNER_UR_LON_PRODUCT");
        final String upEst   = getValue(true, "CORNER_UR_LON_PRODUCT", "CORNER_LR_LON_PRODUCT");

        final double west = Math.min(Double.valueOf(lowWest), Double.valueOf(upWest));
        final double est  = Math.max(Double.valueOf(lowEst), Double.valueOf(upEst));

        //-- lattitude
        final String westSouth = getValue(true, "CORNER_LL_LAT_PRODUCT", "CORNER_LR_LAT_PRODUCT");
        final String estSouth  = getValue(true, "CORNER_LR_LAT_PRODUCT", "CORNER_LL_LAT_PRODUCT");
        final String westNorth = getValue(true, "CORNER_UL_LAT_PRODUCT", "CORNER_UR_LAT_PRODUCT");
        final String estNorth  = getValue(true, "CORNER_UR_LAT_PRODUCT", "CORNER_UL_LAT_PRODUCT");

        final double south = Math.min(Double.valueOf(westSouth), Double.valueOf(estSouth));
        final double north = Math.max(Double.valueOf(westNorth), Double.valueOf(estNorth));

        return new double[]{west, est, south, north};
    }

    /**
     * Returns built {@link CoordinateReferenceSystem} from metadata file.<br>
     *
     * Note : if no Date are specifiedthereturned CRS is the same than the {@link #getCRS2D() } result.
     *
     * @return {@link CoordinateReferenceSystem} from metadata file.
     * @throws FactoryException if impossible to compute CRS.
     */
    CoordinateReferenceSystem getCRS() throws FactoryException {

        if (projectedCRS != null) {
            return projectedCRS;
        }

        final CoordinateReferenceSystem crs2D = getCRS2D();

        //-- add temporal part if Date exist
        final TemporalCRS temporalCRS = CommonCRS.Temporal.JAVA.crs();

        projectedCRS = new GeodeticObjectBuilder()
                .addName(crs2D.getName().getCode() + '/' + temporalCRS.getName().getCode())
                .createCompoundCRS(crs2D, temporalCRS);
        return projectedCRS;
    }

    /**
     * Returns the internal Grid Extent from metadata for given Landsat group (Reflective, Panchromatic or Thermal).
     *
     * @param groupName Reflective, Panchromatic or Thermal
     * @return Grid Extent from metadata for given Landsat group
     * @throws FactoryException if impossible to compute CRS.
     */
    GridEnvelope getGridExtent(final String groupName) throws FactoryException {

        //-- grid coordinates
        final String width   = getValue(true, groupName+SAMPLES_LABEL);
        final String height  = getValue(true, groupName+LINES_LABEL);

        final CoordinateSystem sysAxxes = getCRS().getCoordinateSystem();
        final int dim = sysAxxes.getDimension();
        final int[] upper = new int[dim];
        Arrays.fill(upper, 1);
        upper[0] = Integer.valueOf(width);
        upper[1] = Integer.valueOf(height);

        return new GeneralGridEnvelope(new int[dim], upper, false);
    }

    /**
     * Build {@linkplain MathTransform GridToCRS} from internal grid extent,
     * fourth projected envelope corners and also {@link Date} if exist.
     *
     * @param groupName Reflective, Panchromatic or Thermal
     * @return Grid to CRS from metadata for given Landsat group
     * @throws ParseException if problem during Date parsing.
     * @throws FactoryException  if impossible to compute CRS.
     */
    MathTransform getGridToCRS(final String groupName) throws ParseException, FactoryException {
        final Date acquisitionDate = getAcquisitionDate();
        final MathTransform gridToCRS2D = MathTransforms.linear(AffineTransforms2D.toMatrix(getGridToCRS2D(groupName)));
        if (acquisitionDate == null)
            return gridToCRS2D;

        final LinearTransform linearTime = MathTransforms.linear(0, acquisitionDate.getTime());
        return MathTransforms.compound(gridToCRS2D, linearTime);
    }

    /**
     * Build grid to CRS from internal grid extent and fourth projected envelope corners.
     * The returned {@link AffineTransform} is built from {@link LocalizationGrid#getAffineTransform() }.
     *
     * @param groupName Reflective, Panchromatic or Thermal
     * @return Grid to CRS from metadata for given Landsat group
     * @throws FactoryException if impossible to compute CRS.
     */
    private AffineTransform getGridToCRS2D(final String groupName) throws FactoryException {

        final GridEnvelope gridExtent = getGridExtent(groupName);

        //-- longitude
        final String lowWest = getValue(true, "CORNER_LL_LON_PRODUCT", "CORNER_UL_LON_PRODUCT");
        final String upWest  = getValue(true, "CORNER_UL_LON_PRODUCT", "CORNER_LL_LON_PRODUCT");
        final String lowEst  = getValue(true, "CORNER_LR_LON_PRODUCT", "CORNER_UR_LON_PRODUCT");
        final String upEst   = getValue(true, "CORNER_UR_LON_PRODUCT", "CORNER_LR_LON_PRODUCT");

        //-- lattitude
        final String westSouth = getValue(true, "CORNER_LL_LAT_PRODUCT", "CORNER_LR_LAT_PRODUCT");
        final String estSouth  = getValue(true, "CORNER_LR_LAT_PRODUCT", "CORNER_LL_LAT_PRODUCT");
        final String westNorth = getValue(true, "CORNER_UL_LAT_PRODUCT", "CORNER_UR_LAT_PRODUCT");
        final String estNorth  = getValue(true, "CORNER_UR_LAT_PRODUCT", "CORNER_UL_LAT_PRODUCT");

        final double minLong  = Math.min(Double.valueOf(lowWest), Double.valueOf(upWest));
        final double spanLong = Math.max(Double.valueOf(lowEst), Double.valueOf(upEst)) - minLong;

        final double maxLat   = Math.max(Double.valueOf(westNorth), Double.valueOf(estNorth));
        final double spanLat  = maxLat - Math.min(Double.valueOf(westSouth), Double.valueOf(estSouth));

        return new AffineTransform2D(spanLong / gridExtent.getSpan(0), 0, 0, - spanLat / gridExtent.getSpan(1), minLong, maxLat);
    }

    /**
     * Returns Landsat projected {@link Envelope} with the additionnal temporal
     * axis related with the {@linkplain #getAcquisitionDate() date} image creation.
     *
     * @return projected {@link Envelope}.
     * @throws FactoryException if problem during CRS decoding
     * @throws ParseException if problem during date parsing
     */
    Envelope getProjectedEnvelope() throws FactoryException, ParseException {

        final CoordinateReferenceSystem projCRS = getCRS();
        assert projCRS != null;

        //-- {west, est, south, north}
        final double[] projectedBound2D = getProjectedBound2D();

        final GeneralEnvelope projEnvelope = new GeneralEnvelope(projCRS);
        projEnvelope.setRange(0, projectedBound2D[0], projectedBound2D[1]);
        projEnvelope.setRange(1, projectedBound2D[2], projectedBound2D[3]);
        final Date dat = getAcquisitionDate();
        if (dat != null)
            projEnvelope.setRange(2, dat.getTime(), dat.getTime());

        return projEnvelope;
    }

    /**
     * Returns related metadata {@link Path}.
     *
     * @return metadata path.
     */
    Path getPath(){
        return metadataPath;
    }

    //-------------------------------------------------------------------------//
    //                          Optional metadatas                             //
    //-------------------------------------------------------------------------//

    /**
     * Returns the metadata date built.
     *
     * @return the metadata date built.
     */
    private Date getDateInfo() throws ParseException {
        final String dateInfo = getValue(false, "FILE_DATE");
        return TimeParser.toDate(dateInfo);
    }

    /**
     * Returns if exist K1 and K2 thermic formula constants.
     * May return {@code null}.
     *
     * @return if exist K1 and K2 thermic formula constants.
     */
    private Set<ProcessStep> getThermicInfos() {
        final HashSet<ProcessStep> extendedInfos = new HashSet<>();
        final ProcessStep k110 = getKConstant(1, 10);
        if (k110 != null) extendedInfos.add(k110);
        final ProcessStep k210 = getKConstant(2, 10);
        if (k210 != null) extendedInfos.add(k210);
        final ProcessStep k111 = getKConstant(1, 11);
        if (k111 != null) extendedInfos.add(k111);
        final ProcessStep k211 = getKConstant(2, 11);
        if (k211 != null) extendedInfos.add(k211);
        return extendedInfos;
    }

    /**
     * Returns if exist K constant for one thermic band.
     *
     * @param kVal 1 or 2 for K1 or K2.
     * @param bandIndex thermic band index. (10 or 11 for Landsat)
     * @return
     */
    private ProcessStep getKConstant(final int kVal, final int bandIndex) {

        final String kLabel = "K"+kVal+"_CONSTANT_BAND_"+bandIndex;
        final String k = getValue(false, kLabel);
        if (k != null) {
            final DefaultProcessStep kProcessStep = new DefaultProcessStep();
            final String description = kLabel+" = "+k;
            kProcessStep.setDescription(new DefaultInternationalString(description));
            return kProcessStep;
        }
        return null;
    }

    //-------------------------------------------------------------------------//
    //                            Utility methods                              //
    //-------------------------------------------------------------------------//

    /**
     * Returns the same thing than {@link #getValue(boolean, java.lang.String, java.lang.String) }
     * with fallback label at {@code null} value.
     *
     * @param isMandatory define log or throw exception if value is not founded.
     * @param label the requested metadata field name.
     * @return requested value from given metadata field name.
     */
    String getValue(final boolean isMandatory, final String label) {
        return getValue(isMandatory, label, null);
    }

    /**
     *
     * @param isMandatory
     * @param label
     * @param labelRescue
     * @return
     */
    String getValue(final boolean isMandatory, final String label, final String labelRescue) {
        ArgumentChecks.ensureNonNull("Label", label);
        String labelValue  = metaGroups.get(label);
        if (labelValue == null) {
            final String labelRescueValue  = (labelRescue == null) ? null : metaGroups.get(labelRescue);
            if (labelRescueValue == null) {
                final String errorLabel = (labelRescue == null) ? label : label+" and "+labelRescue;
                if (isMandatory) {
                    throw new IllegalStateException("Landsat "+errorLabel+" metadata "
                            + "informations are missing impossible to define appropriate value.");
                } else {
                    LOGGER.log(Level.FINEST, "The metadata label(s) : {0} is(are) missing.", errorLabel);
                }
            }
            labelValue = labelRescueValue;
        }
        return (labelValue != null) ? labelValue.toUpperCase(Locale.US) : null;
    }

    /**
     * Travel all metadata file and fill a {@link String} map to access more easily to metadatas values.
     *
     * @return
     * @throws IOException if problem during file reading.
     */
    private Map<String, String> getMetadataGroups(final Charset charset) throws IOException {

        final Map<String, String> metaGroup = new HashMap<>();
        final Iterator<String> iterator = Files.readAllLines(metadataPath, charset).iterator();

        while (iterator.hasNext()) {
            final String currentValue = iterator.next();
            final int id     = currentValue.indexOf("=");
            if (id > 0) {
                final String key = currentValue.substring(0, id).trim().toUpperCase();
                String value = currentValue.substring(id+1, currentValue.length()).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length()-1);
                }
                metaGroup.put(key, value);
            }
        }
        return metaGroup;
    }
}
