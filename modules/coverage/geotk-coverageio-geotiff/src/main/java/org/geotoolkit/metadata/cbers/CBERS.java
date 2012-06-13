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
import java.text.DateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.acquisition.DefaultAcquisitionInformation;
import org.geotoolkit.metadata.iso.acquisition.DefaultInstrument;
import org.geotoolkit.metadata.iso.acquisition.DefaultPlatform;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.citation.DefaultCitationDate;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.metadata.iso.constraint.DefaultConstraints;
import org.geotoolkit.metadata.iso.constraint.DefaultLegalConstraints;
import org.geotoolkit.metadata.iso.content.DefaultImageDescription;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.identification.DefaultDataIdentification;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.metadata.iso.lineage.DefaultAlgorithm;
import org.geotoolkit.metadata.iso.lineage.DefaultLineage;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessStep;
import org.geotoolkit.metadata.iso.lineage.DefaultProcessing;
import org.geotoolkit.metadata.iso.quality.DefaultDataQuality;
import org.geotoolkit.metadata.iso.quality.DefaultScope;
import org.geotoolkit.metadata.iso.spatial.AbstractSpatialRepresentation;
import org.geotoolkit.metadata.iso.spatial.DefaultDimension;
import org.geotoolkit.metadata.iso.spatial.DefaultGridSpatialRepresentation;
import org.geotoolkit.temporal.object.ISODateParser;
import org.geotoolkit.util.DomUtilities;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.content.CoverageContentType;
import org.opengis.metadata.identification.CharacterSet;
import org.opengis.metadata.identification.TopicCategory;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.metadata.spatial.CellGeometry;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.DimensionNameType;
import org.opengis.metadata.spatial.SpatialRepresentation;
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
        
        //Data needed        
        DefaultMetadata isoData = new DefaultMetadata();
        final Element root      = doc.getDocumentElement();
        Element tmp             = null;        
        final ISODateParser fp  = new ISODateParser();
        
        final DefaultDataIdentification identificationInfo  = new DefaultDataIdentification();
        final DefaultGridSpatialRepresentation spatialRep   = new DefaultGridSpatialRepresentation();
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
        
        metadata.getIdentificationInfo().add(identificationInfo);
        metadata.getDataQualityInfo().add(qualityInfo);
        qualityInfo.setLineage(lineage);
        lineage.getProcessSteps().add(processStep);
        
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
        isoData.setCharacterSet(CharacterSet.UTF_8);

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
        isoData.setSpatialRepresentationInfo(Collections.singletonList(spatialRep));

        //MI_Metadata/identificationInfo/citation/title : (defaultValue) ????
        
        //MI_Metadata/identificationInfo/citation/date/date : image/timeStamp/center        
        //MI_Metadata/identificationInfo/citation/date/dateType : (defaulValue) ??   
        final Element img         = DomUtilities.firstElement(doc.getDocumentElement(), "image");
        tmp                       = DomUtilities.firstElement(img, "timeStamp");
        tmp                       = DomUtilities.firstElement(tmp, "center");
        Date centerDate           = fp.parseToDate(tmp.getNodeValue());     
        DefaultCitationDate cDate = new DefaultCitationDate(centerDate, DateType.CREATION);
        citation.setDates(Collections.singleton(cDate));
        
        //MI_Metadata/identificationInfo/citedResponsibleParty/role : (defaultValue) Originator        
        responsibleParty.setRole(Role.ORIGINATOR);

        //MI_Metadata/IdentificationInfo/abtract : (defaultValue) CBERS_2B CCD REF

        //MI_Metadata/identificationInfo/graphOverView/fileName

        //MI_Metadata/identificationInfo/resourceConstraints/useConstraints : (defaultValue) otherConstraints
        final Constraints constraint = new DefaultLegalConstraints("otherConstraints");

        //MI_Metadata/identificationInfo/ topicCategory : (defaultValue) imageryBaseMapsEarthCover
        final TopicCategory category = TopicCategory.IMAGERY_BASE_MAPS_EARTH_COVER;

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

        //MI_Metadata/identificationInfo/extent/geographicElement/geographicIdentifier/code : image/path, image/row
        Element path = DomUtilities.firstElement(img, "path");
        Element row  = DomUtilities.firstElement(img, "row");
        String code = path.getNodeValue() + row.getNodeValue();
        DefaultExtent extent = new DefaultExtent();
        extent.getIdentifiers().add(new DefaultIdentifier(code));
        
        identificationInfo.setCitation(citation);
        identificationInfo.setPointOfContacts(Collections.singleton(responsibleParty));
        identificationInfo.setResourceConstraints(Collections.singleton(constraint));
        identificationInfo.setTopicCategories(Collections.singleton(category));
        identificationInfo.getExtents().add(extent);
        

        //MI_Metadata/contentInfo/ attributeDescription : (defaultValue) equivalent radiance (W.m-2.Sr-1.um-1)
        //TODO : not supported

        //MI_Metadata/contentInfo/contentType : (defaultVlaue) image
        contentInfo.setContentType(CoverageContentType.IMAGE);

        //MI_Metadata/contentInfo/illuminationElevationAngle : image/sunPosition/elevation
        final Element sun = DomUtilities.firstElement(img, "sunPosition");
        tmp = DomUtilities.firstElement(sun, "elevation");
        contentInfo.setIlluminationElevationAngle(Double.valueOf(tmp.getNodeValue()));

        //MI_Metadata/contentInfo/illuminationAzimuthAngle : image/sunPosition/sunAzimuth
        tmp = DomUtilities.firstElement(sun, "sunAzimuth");
        contentInfo.setIlluminationAzimuthAngle(Double.valueOf(tmp.getNodeValue()));

        //MI_Metadata/contentInfo/processingLevelCode/code : image/level
        tmp = DomUtilities.firstElement(img, "level");
        contentInfo.setProcessingLevelCode(new DefaultIdentifier(tmp.getNodeValue()));

        //contentInfo/dimension/sequenceIdentifier : ????
        //TODO

        //contentInfo/dimension/descriptor : (defaultValue) BAND1
        //TODO

        //contentInfo/dimension/units : (defaultValue) W.m-2.Sr-1.um-1
        //TODO

        //dataQualityInfo/scope : (defaultValue) dataset
        qualityInfo.setScope(new DefaultScope(ScopeCode.DATASET));

        //dataQualityInfo/lineage/processStep/description: (defaultValue) CBERS LEVEL 2 PRODUCT
        processStep.setDescription(new SimpleInternationalString("CBERS LEVEL 2 PRODUCT"));

        //dataQualityInfo/lineage/processStep/dateTime : image/processingTime
        tmp = DomUtilities.firstElement(img, "processingTime");
        final Date resultDate = fp.parseToDate(tmp.getNodeValue());
        processStep.setDate(resultDate);

        //dataQualityInfo/lineage/processStep/processor/OrganisationName : image/processingStation
        tmp = DomUtilities.firstElement(tmp, "processingStation");
        processor.setOrganisationName(new SimpleInternationalString(tmp.getNodeValue()));

        //dataQualityInfo/lineage/processStep/processor/role : (defaultValue) processor
        processor.setRole(Role.PROCESSOR);

        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/title : software
        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/edition : version
        processInfo.getSoftwareReferences().add(softwareReference);
        tmp = DomUtilities.firstElement(root, "software");
        softwareReference.setTitle(new SimpleInternationalString(tmp.getNodeValue()));
        processStep.setProcessingInformation(processInfo);
        tmp = DomUtilities.firstElement(root, "version");
        softwareReference.setEdition(new SimpleInternationalString(tmp.getNodeValue()));

        //dataQualityInfo/lineage/processStep/ processingInformation/softwareReference/date/date : (defaultValue) NilReason="missing"        
        //dataQualityInfo/lineage/processStep/processingInformation/ algorithm/date/date/dateType : (defaultValue) creation
        //TODO : No equivalent for "missing" tag.

        //dataQualityInfo/lineage/processStep/processingInformation/algorithm/description : algorithm/description
        DefaultAlgorithm algo = new DefaultAlgorithm();
        tmp = DomUtilities.firstElement(root, "algorithm");
        tmp = DomUtilities.firstElement(tmp, "description");
        algo.setDescription(new SimpleInternationalString(tmp.getNodeValue()));

        //acquisitionInformation/instrument/identifier : Satellite/instrument
        acquisitionInfo.getInstruments().add(instrument);
        Element sat = DomUtilities.firstElement(root, "Satellite");
        tmp = DomUtilities.firstElement(sat, "instrument");
        instrument.setIdentifier(new DefaultIdentifier(tmp.getNodeValue()));

        //acquisitionInformation/instrument/type : (defaultValue) Push-broom        
        instrument.setType(new SimpleInternationalString("Push-broom"));

        //acquisitionInformation/plateform/identifier : Concatenate {Satellite/name} and {Satellite/number} 
        //acquisitionInformation/plateform/description : (defaultValue) CBERS_2B       
        acquisitionInfo.getPlatforms().add(platform);
        final Element name   = DomUtilities.firstElement(sat, "name");
        final Element number = DomUtilities.firstElement(sat, "number");
        String concat        = name.getNodeValue() + number.getNodeValue();
        platform.setIdentifier(new DefaultIdentifier(concat));        
        platform.setDescription(new SimpleInternationalString(concat));


        //MI_Metadata/distributionInfo/transferOptions/onLine/linkage ???

        //TODO
        return metadata;
    }
}
