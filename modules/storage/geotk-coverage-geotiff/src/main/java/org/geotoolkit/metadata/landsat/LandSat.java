/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.metadata.landsat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.acquisition.DefaultAcquisitionInformation;
import org.apache.sis.metadata.iso.acquisition.DefaultInstrument;
import org.apache.sis.metadata.iso.acquisition.DefaultPlatform;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultCitationDate;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;
import org.apache.sis.metadata.iso.constraint.DefaultLegalConstraints;
import org.apache.sis.metadata.iso.content.DefaultImageDescription;
import org.apache.sis.metadata.iso.distribution.DefaultFormat;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.metadata.iso.lineage.DefaultProcessStep;
import org.apache.sis.metadata.iso.lineage.DefaultProcessing;
import org.apache.sis.metadata.iso.quality.DefaultDataQuality;
import org.apache.sis.metadata.iso.quality.DefaultScope;
import org.apache.sis.metadata.iso.spatial.DefaultGeorectified;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.util.FileUtilities;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;

/**
 * Convenient methods to manipulate LandSat informations.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class LandSat {

    private LandSat(){}

    public static LandSatMetaNode parseMetadata(final File file) throws IOException{

        final FileInputStream stream = new FileInputStream(file);
        try{
            return parseMetadata(stream);
        }finally{
            stream.close();
        }
    }

    public static LandSatMetaNode parseMetadata(final InputStream stream) throws IOException{

        final String metaFile = FileUtilities.getStringFromStream(stream);
        final String[] lines = metaFile.split("\n");


        //rebuild the metadata tree
        LandSatMetaNode root = null;
        LandSatMetaNode node = null;

        for(int i=0; i<lines.length;i++){
            String line = lines[i];
            line = line.trim();
            if(line.isEmpty()) continue;

            final int separator = line.indexOf('=');

            if(separator < 0){
                //might be the END tag
                if("END".equalsIgnoreCase(line)){
                    //ok we have finish
                    break;
                }else{
                    //unexpected
                    throw new IOException("Line "+i+" does not match metadata pattern {key = value} : "+ line);
                }
            }

            final String key = line.substring(0, separator).trim();
            final String value = line.substring(separator+1).trim();

            if("GROUP".equalsIgnoreCase(key)){
                //invert to have the group name as key
                final LandSatMetaNode candidate = new LandSatMetaNode(value, key);
                if(node != null){
                    node.add(candidate);
                }else{
                    root = candidate;
                }
                node = candidate;
            }else if("END_GROUP".equalsIgnoreCase(key)){

                //end this node, check the name match,
                //otherwise it means the file is incorrect
                if(!value.equalsIgnoreCase(String.valueOf(node.getKey()))){
                    throw new IOException("End Group line "+i+" does not match any previous group. "+ line);
                }
                node = (LandSatMetaNode) node.getParent();
            }else{
                //simple key=value pair
                final LandSatMetaNode candidate = new LandSatMetaNode(key, value);
                node.add(candidate);
            }
        }

        return root;
    }

    public static LandSatNomenclature parseNomenclature(final String name){
        if(name == null || name.length() < 24){
            throw new IllegalArgumentException("name is too short to match lansat naming convention");
        }

        //TODO
        throw new IllegalArgumentException("not coded yet.");
    }

    /**
     * Extract as much information from the landsat metadata and map it to
     * ISO 19115-2.
     *
     * @param LandSat Metadata
     * @return ISO19115 Metadata
     */
    public static Metadata toMetadata(LandSatMetaNode landsat, final String fileName){

        final DefaultMetadata metadata = new DefaultMetadata();

        //Default values
        metadata.setCharacterSet(StandardCharsets.UTF_8);
        metadata.setLanguage(Locale.ENGLISH);
        metadata.setDateStamp(new Date());

        LandSatMetaNode node1;
        LandSatMetaNode node2;
        LandSatMetaNode node3;

        final DefaultGeorectified spatialRepresentation = new DefaultGeorectified();
        metadata.getSpatialRepresentationInfo().add(spatialRepresentation);
        final DefaultDataIdentification identificationInfo = new DefaultDataIdentification();
        metadata.getIdentificationInfo().add(identificationInfo);
        final DefaultAcquisitionInformation aquisitionInfo = new DefaultAcquisitionInformation();
        metadata.getAcquisitionInformation().add(aquisitionInfo);
        final DefaultImageDescription contentInfo = new DefaultImageDescription();
        metadata.getContentInfo().add(contentInfo);
        final DefaultDataQuality qualityInfo = new DefaultDataQuality();
        metadata.getDataQualityInfo().add(qualityInfo);
        final DefaultLineage lineage = new DefaultLineage();
        qualityInfo.setLineage(lineage);
        final DefaultProcessStep processStep = new DefaultProcessStep();
        lineage.getProcessSteps().add(processStep);



        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/PRODUCT_TYPE
        // iso : MI_Metadata/spatialRepresentationInfo/checkPointAvailability
        // if PRODUCT_TYPE = «  L1T  » then checkPointAvailability = true, otherwise false
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","PRODUCT_TYPE");
        if(node1 != null){
            spatialRepresentation.setCheckPointAvailable(
                    "L1T".equalsIgnoreCase(node1.getValue()) );
        }


        // build title from band type, example : ETM+ REF
        // [acquisitionInformation/instrument/identifier] PAN
        // [acquisitionInformation/instrument/identifier] REF
        // [acquisitionInformation/instrument/identifier] THM
        // PAN = panchromatic : B80
        // REF = reflective : B10,B20,B30,B40,B50,B70
        // THM = thermal : B61,B62
        //
        // landsat : L1_METADATA_FILE/PRODUCT_METADATA/SENSOR_ID + type
        // iso : MI_Metadata/identificationInfo/citation/title
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","SENSOR_ID");
        if(node1 != null){
            String title = node1.getValue();
            if(fileName != null){
                if(fileName.contains("B80")){
                    title += " PAN";
                }else if(fileName.contains("B61") || fileName.contains("B62")){
                    title += " THM";
                }else if(fileName.contains("RGB")
                      || fileName.contains("B10")
                      || fileName.contains("B20")
                      || fileName.contains("B30")
                      || fileName.contains("B40")
                      || fileName.contains("B50")
                      || fileName.contains("B70")){
                    title += " REF";
                }
            }

            DefaultCitation citation = (DefaultCitation) identificationInfo.getCitation();
            if(citation == null){
                citation = new DefaultCitation();
                identificationInfo.setCitation(citation);
            }
            citation.setTitle(new SimpleInternationalString(title));

        }


        // Landsat :  L1_METADATA_FILE/PRODUCT_METADATA/ACQUISITION_DATE + SCENE_CENTER_SCAN_TIME
        // iso : MI_Metadata/identificationInfo/citation/date/date
        // iso : MI_Metadata/identificationInfo/citation/date/dateType value=creation
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","ACQUISITION_DATE");
        node2 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","SCENE_CENTER_SCAN_TIME");
        if(node1 != null && node2 != null){
            DefaultCitation citation = (DefaultCitation) identificationInfo.getCitation();
            if(citation == null){
                citation = new DefaultCitation();
                identificationInfo.setCitation(citation);
            }

            final String strdate = node1.getValue() +"T"+node2.getValue();
            final ISODateParser fp = new ISODateParser();
            final java.util.Date resultDate = fp.parseToDate(strdate);
            final CitationDate date = new DefaultCitationDate(resultDate, DateType.CREATION);
            citation.getDates().add(date);
        }


        // Landsat : L1_METADATA_FILE/METADATA_FILE_INFO/ORIGIN
        // iso : MI_Metadata/identificationInfo/citedResponsibleParty/organisationName
        // iso : MI_Metadata/identificationInfo/citedResponsibleParty/role value=originator
        node1 = landsat.search("L1_METADATA_FILE","METADATA_FILE_INFO","ORIGIN");
        if(node1 != null){
            final DefaultResponsibleParty responsibleParty = new DefaultResponsibleParty();
            responsibleParty.setOrganisationName(new SimpleInternationalString(node1.getValue()));
            responsibleParty.setRole(Role.ORIGINATOR);
            identificationInfo.getPointOfContacts().add(responsibleParty);
        }


        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/SENSOR_ID
        // iso : acquisitionInformation/instrument/identifier
        // iso : acquisitionInformation/instrument/type  value=Push-broom
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","SENSOR_ID");
        if(node1 != null){
            final DefaultInstrument instrument = new DefaultInstrument();
            aquisitionInfo.getInstruments().add(instrument);
            instrument.setIdentifier(new DefaultIdentifier(node1.getValue()));
            instrument.setType(new SimpleInternationalString("Push-broom"));
        }


        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/SPACECRAFT_ID
        // iso : acquisitionInformation/platform/identifier
        // iso : acquisitionInformation/platform/description
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","SPACECRAFT_ID");
        if(node1 != null){
            final DefaultPlatform platform = new DefaultPlatform();
            aquisitionInfo.getPlatforms().add(platform);
            platform.setIdentifier(new DefaultIdentifier(node1.getValue()));
            platform.setDescription(new SimpleInternationalString(node1.getValue()));
            platform.setCitation(new DefaultCitation(node1.getValue()));
        }


        // iso : MI_Metadata/identificationInfo/abstract
        // concatenate [acquisitionInformation/platform/identifier] et [MI_Metadata/identificationInfo/citation/title]
        String abs = "";
        if(!aquisitionInfo.getPlatforms().isEmpty()){
            abs += aquisitionInfo.getPlatforms().iterator().next().getIdentifier().toString();
        }
        if(identificationInfo.getCitation() != null){
            abs += " "+identificationInfo.getCitation().getTitle();
        }
        identificationInfo.setAbstract(new SimpleInternationalString(abs));


        // iso : MI_Metadata/identificationInfo/resourceFormat/name  value=geotiff
        final DefaultFormat format = new DefaultFormat();
        format.setName(new SimpleInternationalString("geotiff"));
        identificationInfo.getResourceFormats().add(format);


        // iso : MI_Metadata/identificationInfo/resourceConstraints/useConstraints  value=otherConstraints
        final Constraints constraint = new DefaultLegalConstraints("otherConstraints");
        identificationInfo.getResourceConstraints().add(constraint);


        // iso : MI_Metadata/identificationInfo/topicCategory  value=imageryBaseMapsEarthCover
        final TopicCategory category = TopicCategory.IMAGERY_BASE_MAPS_EARTH_COVER;
        identificationInfo.getTopicCategories().add(category);


        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/WRS_PATH + STARTING_ROW + ENDING_ROW
        // iso : MI_Metadata/identificationInfo/extent/geographicElement/geographicIdentifier/code
        // Concatenate path + starting row with format «  ppp_rrr  ».
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","WRS_PATH");
        node2 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","STARTING_ROW");
        if(node1 != null && node2 != null){
            final DefaultExtent extent = new DefaultExtent();
            extent.getIdentifiers().add(new DefaultIdentifier(node1.getValue()+"_"+node2.getValue()));
            identificationInfo.getExtents().add(extent);
        }


        // iso : MI_Metadata/contentInfo/attributeDescription  value=equivalent radiance (W.m-2.Sr-1.um-1)
        // TODO : not supported


        // iso : MI_Metadata/contentInfo/contentType  value=image
        contentInfo.setContentType(CoverageContentType.IMAGE);


        // Landsat : L1_METADATA_FILE/PRODUCT_PARAMETERS/SUN_ELEVATION
        // iso : MI_Metadata/contentInfo/illuminationElevationAngle
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_PARAMETERS","SUN_ELEVATION");
        if(node1 != null){
            contentInfo.setIlluminationElevationAngle(tryDouble(node1.getValue()));
        }


        // Landsat : L1_METADATA_FILE/PRODUCT_PARAMETERS/SUN_AZIMUTH
        // iso : MI_Metadata/contentInfo/illuminationAzimuthAngle
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_PARAMETERS","SUN_AZIMUTH");
        if(node1 != null){
            contentInfo.setIlluminationAzimuthAngle(tryDouble(node1.getValue()));
        }


        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/PRODUCT_TYPE
        // iso : MI_Metadata/contentInfo/processingLevelCode/code
        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","PRODUCT_TYPE");
        if(node1 != null){
            contentInfo.setProcessingLevelCode(new DefaultIdentifier(node1.getValue()));
        }


        // iso : contentInfo/dimension/sequenceIdentifier
        // iso : contentInfo/dimension/descriptor  value=BAND1
        // One occurrence of contentInfo/dimension/ for each spectral band.
        // loop on BAND  ?_FILE_NAME ou BAND  ??_FILE_NAME  trouvés dans le fichier MTL.txt.
        // example  :
        // BAND1, ,BAND2, BAND3, BAND4, BAND5, BAND61, BAND62, BAND7, BAND8  in case of L7 ETM+
        // BAND1, ,BAND2, BAND3, BAND4, BAND5, BAND6, BAND7 in case of L4 et L5 TM
        // BAND4, BAND5, BAND6, BAND7 in case of MSS

        // TODO filled later, depending on band merged operation this is different


        // Landsat : L1_METADATA_FILE/MIN_MAX_PIXEL_VALUE/QCALMIN_BAND1
        // iso : contentInfo/dimension/minValue
        // Caution, there is one value for each band.
        // So we have to link the value to the correct band.
        // elements BAND  ?? and BAND  ??_FILE_NAME allows to do it.

        // TODO filled later, depending on band merged operation this is different


        // Landsat : L1_METADATA_FILE/MIN_MAX_PIXEL_VALUE/QCALMAX_BAND1
        // iso : contentInfo/dimension/maxValue
        // same as above

        // TODO filled later, depending on band merged operation this is different


        // iso : contentInfo/dimension/units  value=W.m-2.Sr-1.um-1

        // TODO filled later, depending on band merged operation this is different


        // Landsat : L1_METADATA_FILE/MIN_MAX_RADIANCE/LMAX_BAND1 + LMIN_BAND1
        // Landsat : L1_METADATA_FILE/MIN_MAX_PIXEL_VALUE/QCALMAX_BAND1 + QCALMIN_BAND1
        // iso : contentInfo/dimension/scaleFactor
        // calculate : scaleFactor = (LMAX_BAND1 -  LMIN_BAND1) / (QCALMAX_BAND1 – QCALMIN_BAND1)

        // TODO ???


        // Landsat : L1_METADATA_FILE/MIN_MAX_RADIANCE/LMIN_BAND5
        // iso : contentInfo/dimension/offset
        // same as above

        // TODO ???


        // iso : dataQualityInfo/scope  value=dataset
        qualityInfo.setScope(new DefaultScope(ScopeCode.DATASET));


        // iso : dataQualityInfo/lineage/processStep/description  value=LANDSAT LEVEL 1 PRODUCT
        processStep.setDescription(new SimpleInternationalString("LANDSAT LEVEL 1 PRODUCT"));


        // Landsat : L1_METADATA_FILE/METADATA_FILE_INFO/PRODUCT_CREATION_TIME
        // iso : dataQualityInfo/lineage/processStep/dateTime
        node1 = landsat.search("L1_METADATA_FILE","METADATA_FILE_INFO","PRODUCT_CREATION_TIME");
        if(node1 != null){
            final ISODateParser fp = new ISODateParser();
            final java.util.Date resultDate = fp.parseToDate(node1.getValue());
            processStep.setDate(resultDate);
        }

        // iso : dataQualityInfo/lineage/processStep/processor/OrganisationName  value=USGS
        // iso : dataQualityInfo/lineage/processStep/processor/role  value=processor
        final DefaultResponsibleParty processor = new DefaultResponsibleParty();
        processor.setOrganisationName(new SimpleInternationalString("USGS"));
        processor.setRole(Role.PROCESSOR);
        processStep.getProcessors().add(processor);


        // Landsat : L1_METADATA_FILE/METADATA_FILE_ INFO/REQUEST_ID
        // iso : dataQualityInfo/lineage/processStep/processingInformation/identifier/code
        final DefaultProcessing processInfo = new DefaultProcessing();
        processStep.setProcessingInformation(processInfo);

        node1 = landsat.search("L1_METADATA_FILE","METADATA_FILE_INFO","REQUEST_ID");
        if(node1 != null){
            processInfo.setIdentifier(new DefaultIdentifier(node1.getValue()));
        }


        // Landsat : L1_METADATA_FILE/PRODUCT_METADATA/PROCESSING_SOFTWARE
        // iso : dataQualityInfo/lineage/processStep/processingInformation/softwareReference/title
        // iso : dataQualityInfo/lineage/processStep/processingInformation/softwareReference/edition
        final DefaultCitation softwareReference = new DefaultCitation();
        processInfo.getSoftwareReferences().add(softwareReference);

        node1 = landsat.search("L1_METADATA_FILE","PRODUCT_METADATA","PROCESSING_SOFTWARE");
        if(node1 != null){
            softwareReference.setTitle(new SimpleInternationalString(node1.getValue()));
            softwareReference.setEdition(new SimpleInternationalString(node1.getValue()));
        }


        // iso : dataQualityInfo/lineage/processStep/processingInformation/softwareReference/date/date  value=NilReason="missing"
        // iso : dataQualityInfo/lineage/processStep/processingInformation/softwareReference/date/date/dateType  value=creation
        // ISO conformance. value is missing but tag must exist with attribute NilReason with value "missing"
        // missing
        //final DefaultCitationDate scd = new DefaultCitationDate(null, DateType.CREATION);
        //softwareReference.getDates().add(scd);


        // iso : dataQualityInfo/lineage/processStep/processingInformation/algorithm/title
        // TODO no value ?

        // iso : dataQualityInfo/lineage/processStep/processingInformation/algorithm/date/date   value=NilReason="missing"
        // ISO conformance. value is missing but tag must exist with attribute NilReason with value "missing"
        // missing

        // iso : dataQualityInfo/lineage/processStep/processingInformation/algorithm/date/date/dateType  value=creation
        // missing

        // iso : dataQualityInfo/lineage/processStep/processingInformation/algorithm/description
        // TODO no value ?


        return metadata;
    }

    private static double tryDouble(String candidate){
        try{
            return Double.valueOf(candidate);
        }catch(NumberFormatException ex){
            return Double.NaN;
        }
    }


}
