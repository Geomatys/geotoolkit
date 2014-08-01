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
package org.geotoolkit.metadata.cbers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
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
import org.apache.sis.metadata.iso.content.DefaultRangeDimension;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.lineage.DefaultAlgorithm;
import org.apache.sis.metadata.iso.lineage.DefaultLineage;
import org.apache.sis.metadata.iso.lineage.DefaultProcessStep;
import org.apache.sis.metadata.iso.lineage.DefaultProcessing;
import org.apache.sis.metadata.iso.quality.DefaultDataQuality;
import org.apache.sis.metadata.iso.quality.DefaultScope;
import org.apache.sis.metadata.iso.spatial.DefaultDimension;
import org.apache.sis.metadata.iso.spatial.DefaultGeorectified;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.util.DomUtilities;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.DimensionNameType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Convenient methods to manipulate CBERS informations.
 * @author Alexis MANIN (Geomatys)
 */
public class CBERS {

    private CBERS(){};

    /**
     * Extract as much information as possible from CBERS metadata and map it to
     * ISO 19115-2.
     *
     * @param cbersData Metadata
     * @return ISO19115 Metadata
     */
    public static Metadata toMetadata(File cbersData) throws ParserConfigurationException, IOException, SAXException{
        final DefaultMetadata metadata = new DefaultMetadata();
        //Initialisation of DOM document.
        Document doc = DomUtilities.read(cbersData);

        //////////////////////////////////
       //     Data initialisation      //
      //////////////////////////////////

        //Dom
        final Element root = doc.getDocumentElement();
        Element tmp        = null;
        final Element img  = DomUtilities.firstElement(doc.getDocumentElement(), "image");

        //metadata
        final DefaultDataIdentification identificationInfo  = new DefaultDataIdentification();
        final DefaultGeorectified spatialRep                = new DefaultGeorectified();
        final DefaultCitation citation                      = new DefaultCitation();
        final DefaultResponsibleParty responsibleParty      = new DefaultResponsibleParty();
        final DefaultImageDescription contentInfo           = new DefaultImageDescription();
        final DefaultDataQuality qualityInfo                = new DefaultDataQuality();
        final DefaultLineage lineage                        = new DefaultLineage();
        final DefaultProcessStep processStep                = new DefaultProcessStep();
        final DefaultProcessing processInfo                 = new DefaultProcessing();
        final DefaultCitation softwareReference             = new DefaultCitation();
        final DefaultResponsibleParty processor             = new DefaultResponsibleParty();
        final DefaultAcquisitionInformation acquisitionInfo = new DefaultAcquisitionInformation();
        final DefaultInstrument instrument                  = new DefaultInstrument();
        final DefaultPlatform platform                      = new DefaultPlatform();

        DefaultMetadata isoData = new DefaultMetadata();
        metadata.getSpatialRepresentationInfo().add(spatialRep);
        metadata.getIdentificationInfo().add(identificationInfo);
        metadata.getDataQualityInfo().add(qualityInfo);
        metadata.getAcquisitionInformation().add(acquisitionInfo);
        qualityInfo.setLineage(lineage);
        lineage.getProcessSteps().add(processStep);
        identificationInfo.setCitation(citation);
        identificationInfo.getPointOfContacts().add(responsibleParty);
        processInfo.getSoftwareReferences().add(softwareReference);
        processStep.setProcessingInformation(processInfo);
        acquisitionInfo.getInstruments().add(instrument);
        acquisitionInfo.getPlatforms().add(platform);
        platform.setCitation(new DefaultCitation("CBERS"));
        //other
        final ISODateParser fp  = new ISODateParser();

        ///////////////////////////////////////
       //           STEP LISTING            //
      ///////////////////////////////////////
        /**
         * The following comments describe how to fill ISO 19115 metadata from
         * CBERS xml elements. Syntax is : ISO 19115 xml tag to set : CBERS
         * element or default value to use.
         */
        //MI_Metadata/fileIdentifier : FileName
        String fileName = cbersData.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        isoData.setFileIdentifier(fileName);

        //MI_Metadata/language : (default value) english
        isoData.setLanguage(Locale.ENGLISH);

        //MI_Metadata/characterSet : (default value) utf8
        isoData.setCharacterSet(StandardCharsets.UTF_8);

        //MI_Metadata/dateStamp : get current date
        isoData.setDateStamp(new Date());

        //MI_Metadata/contact/role : (defaultValue) ??

        //MI_Metadata/contact/organisationName :(defaultValue) AGEOS (IRD ???)

        //MI_Metadata/contact/electronicMailAdress : (defaultValue) Institutional e-mail of AGEOS (IRD ??)

        //MI_Metadata/SpatialRepresentationInfo/numberOfDimensions : (defaultValue) 2
        spatialRep.setNumberOfDimensions(2);

        //MI_Metadata/spatialRepresentationInfo/axisDimensionProperties/dimensionName : (defaultValue) row
        final List<Dimension> dims = new ArrayList<Dimension>(2);
        final DefaultDimension dim1 = new DefaultDimension();
        dim1.setDimensionName(DimensionNameType.ROW);
        dims.add(dim1);

        //MI_Metadata/spatialRepresentationInfo/axisDimensionProperties/dimensionName : (defaultValue) column
        final DefaultDimension dim2 = new DefaultDimension();
        dim2.setDimensionName(DimensionNameType.COLUMN);
        dims.add(dim2);
        spatialRep.setAxisDimensionProperties(dims);

        //MI_Metadata/spatialRepresentationInfo/cellGeometry : GeoKeyDirectory.GTRasterTypeGeoKey
        //spatialRep.setCellGeometry(CellGeometry.POINT);

        //MI_Metadata/spatialRepresentationInfo/transformationParameterAvailability : (defaultValue) FALSE
        spatialRep.setTransformationParameterAvailable(false);

        //MI_Metadata/spatialRepresentationInfo/checkPointAvailability : (defaultValue) FALSE
        spatialRep.asMap().put("checkPointAvailability", false);
        isoData.getSpatialRepresentationInfo().add(spatialRep);

        //MI_Metadata/identificationInfo/citation/date/date : image/timeStamp/center
        //MI_Metadata/identificationInfo/citation/date/dateType : (defaulValue) Creation
        if (img != null) {
            tmp = DomUtilities.firstElement(img, "timeStamp");
            if (tmp != null) {
                tmp = DomUtilities.firstElement(tmp, "center");
                if (tmp != null) {
                    Date centerDate = fp.parseToDate(tmp.getTextContent());
                    DefaultCitationDate cDate = new DefaultCitationDate(centerDate, DateType.CREATION);
                    citation.getDates().add(cDate);
                }
            }
        }

        //MI_Metadata/identificationInfo/citedResponsibleParty/role : (defaultValue) Originator
        responsibleParty.setRole(Role.ORIGINATOR);

        //MI_Metadata/identificationInfo/resourceConstraints/useConstraints : (defaultValue) otherConstraints
        final Constraints constraint = new DefaultLegalConstraints("otherConstraints");
        identificationInfo.getResourceConstraints().add(constraint);

        //MI_Metadata/identificationInfo/ topicCategory : (defaultValue) imageryBaseMapsEarthCover
        final TopicCategory category = TopicCategory.IMAGERY_BASE_MAPS_EARTH_COVER;
        identificationInfo.getTopicCategories().add(category);

        //Do not work on the bounds elements because it should be set with GeoTIFF data
        //-----------------------------------------------------------------------------
        /*
         * MI_Metadata/identificationInfo/extent/geographicElement/polygon (can
         * be retrieve from GeoTIFF : image/boundingBox/UL/latitude
         * image/boundingBox/UL/longitude image/boundingBox/UR/...
         * image/boundingBox/LL/...
         */

        /*
         * MI_Metadata/identificationInfo/extent/geographicElement/(westBoundLongitude
         * || eastBoundLongitude || southBoundLatitude || northBoundLatitude) :
         * image/boundingBox/UL/longitude image/boundingBox/UL/latitude
         * image/boundingBox/UR/... image/boundingBox/LL/...
         */

        //MI_Metadata/identificationInfo/extent/geographicElement/extentTypeCode : (defaultValue) TRUE
        //-----------------------------------------------------------------------------

        //MI_Metadata/identificationInfo/extent/geographicElement/geographicIdentifier/code : image/path, image/row
        if (img != null) {
            Element path = DomUtilities.firstElement(img, "path");
            Element row  = DomUtilities.firstElement(img, "row");
            if (path != null && row != null) {
                String code = path.getTextContent() + '-' + row.getTextContent();
                DefaultExtent extent = new DefaultExtent();
                extent.getIdentifiers().add(new DefaultIdentifier(code));
                identificationInfo.getExtents().add(extent);
            }
        }

        //MI_Metadata/contentInfo/ attributeDescription : (defaultValue) equivalent radiance (W.m-2.Sr-1.um-1)
        //TODO : not supported

        //MI_Metadata/contentInfo/contentType : (defaultValue) image
        contentInfo.setContentType(CoverageContentType.IMAGE);

        if (img != null) {
            final Element sun = DomUtilities.firstElement(img, "sunPosition");
            if (sun != null) {
                //MI_Metadata/contentInfo/illuminationElevationAngle : image/sunPosition/elevation
                tmp = DomUtilities.firstElement(sun, "elevation");
                if (tmp != null) {
                    contentInfo.setIlluminationElevationAngle(Double.valueOf(tmp.getTextContent()));
                }

                //MI_Metadata/contentInfo/illuminationAzimuthAngle : image/sunPosition/sunAzimuth
                tmp = DomUtilities.firstElement(sun, "sunAzimuth");
                if (tmp != null) {
                    contentInfo.setIlluminationAzimuthAngle(Double.valueOf(tmp.getTextContent()));
                }
            }

            //MI_Metadata/contentInfo/processingLevelCode/code : image/level
            tmp = DomUtilities.firstElement(img, "level");
            if (tmp != null) {
                contentInfo.setProcessingLevelCode(new DefaultIdentifier(tmp.getTextContent()));
            }
        }

        //contentInfo/dimension/sequenceIdentifier : get BANDx string
        //contentInfo/dimension/descriptor : (defaultValue) BAND1
        final int start   = fileName.lastIndexOf("BAND");
        final String band = fileName.substring(start, start+5);
        final int bandNum = Integer.valueOf(band.substring(band.length()-1));
        DefaultRangeDimension dim = new DefaultRangeDimension();
        //TODO : set sequence identifier
        dim.setDescriptor(new SimpleInternationalString(band));
        contentInfo.getDimensions().add(dim);

        //contentInfo/dimension/units : (defaultValue) W.m-2.Sr-1.um-1
        //TODO : No equivalent for default value.

        //dataQualityInfo/scope : (defaultValue) dataset
        qualityInfo.setScope(new DefaultScope(ScopeCode.DATASET));

        //dataQualityInfo/lineage/processStep/description: (defaultValue) CBERS LEVEL 2 PRODUCT
        processStep.setDescription(new SimpleInternationalString("CBERS LEVEL 2 PRODUCT"));

        if (img != null) {
            //dataQualityInfo/lineage/processStep/dateTime : image/processingTime
            tmp = DomUtilities.firstElement(img, "processingTime");
            if (tmp != null) {
                final Date resultDate = fp.parseToDate(tmp.getTextContent());
                processStep.setDate(resultDate);
            }

            //dataQualityInfo/lineage/processStep/processor/OrganisationName : image/processingStation
            tmp = DomUtilities.firstElement(img, "processingStation");
            if (tmp != null) {
                processor.setOrganisationName(new SimpleInternationalString(tmp.getTextContent()));
            }
        }

        //dataQualityInfo/lineage/processStep/processor/role : (defaultValue) processor
        processor.setRole(Role.PROCESSOR);

        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/title : software
        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/edition : version
        tmp = DomUtilities.firstElement(root, "software");
        if (tmp != null) {
            softwareReference.setTitle(new SimpleInternationalString(tmp.getTextContent()));
        }
        if (tmp != null) {
            tmp = DomUtilities.firstElement(root, "version");
        }
        softwareReference.setEdition(new SimpleInternationalString(tmp.getTextContent()));

        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/date/date : (defaultValue) NilReason="missing"
        //dataQualityInfo/lineage/processStep/processingInformation/ algorithm/date/date/dateType : (defaultValue) creation
        //TODO : No equivalent for "missing" tag.

        //dataQualityInfo/lineage/processStep/processingInformation/algorithm/description : algorithm/description
        DefaultAlgorithm algo = new DefaultAlgorithm();
        tmp = DomUtilities.firstElement(root, "algorithm");
        if (tmp != null) {
            tmp = DomUtilities.firstElement(tmp, "description");
            if (tmp != null) {
                algo.setDescription(new SimpleInternationalString(tmp.getTextContent()));
            }
        }


        //acquisitionInformation/instrument/type : (defaultValue) Push-broom
        instrument.setType(new SimpleInternationalString("Push-broom"));

        Element sat = DomUtilities.firstElement(root, "Satellite");
        if (sat != null) {
            String platformId = null;
            String cTitle = null;

            tmp = DomUtilities.firstElement(sat, "instrument");
            if (tmp != null) {
                //acquisitionInformation/instrument/identifier : Satellite/instrument
                final String instrumentId = tmp.getTextContent();
                instrument.setIdentifier(new DefaultIdentifier(instrumentId));

                //MI_Metadata/identificationInfo/citation/title : concatenate acquisitionInformation/instrument/identifier and (if id is CCD) PAN or REF
                cTitle = instrumentId;
                if (cTitle.equals("CCD")) {
                    if (bandNum < 5) {
                        cTitle += "REF";
                    } else {
                        cTitle += "PAN";
                    }
                }
                citation.setTitle(new SimpleInternationalString(cTitle));
            }

            final Element name = DomUtilities.firstElement(sat, "name");
            final Element number = DomUtilities.firstElement(sat, "number");
            if (name != null && number != null) {
                //acquisitionInformation/plateform/identifier : Concatenate {Satellite/name} and {Satellite/number}
                //acquisitionInformation/plateform/description : (defaultValue) CBERS_2B
                platformId = name.getTextContent() + '_' + number.getTextContent();
                platform.setIdentifier(new DefaultIdentifier(platformId));
                platform.setDescription(new SimpleInternationalString(platformId));
            }

            if (platformId != null && cTitle != null) {
                //MI_Metadata/IdentificationInfo/abstract : concatenate acquisitionInformation/platform/identifier and identificationInfo/citation/title
                identificationInfo.setAbstract(new SimpleInternationalString(platformId + ' ' + cTitle));
            }
        }

        //MI_Metadata/distributionInfo/transferOptions/onLine/linkage ???
        //TODO

        return metadata;
    }
}
