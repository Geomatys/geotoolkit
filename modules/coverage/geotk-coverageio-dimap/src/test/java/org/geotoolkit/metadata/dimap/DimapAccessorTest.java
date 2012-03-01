/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.metadata.dimap;

import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.distribution.Format;
import org.opengis.metadata.lineage.Processing;
import java.text.ParseException;
import org.opengis.metadata.citation.Citation;
import java.util.Collection;
import org.geotoolkit.gui.swing.tree.Trees;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.geotoolkit.util.DomUtilities;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.acquisition.Operation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.constraint.LegalConstraints;
import org.opengis.metadata.constraint.Restriction;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.identification.Identification;
import org.opengis.metadata.lineage.Algorithm;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.lineage.ProcessStep;
import org.opengis.metadata.quality.DataQuality;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 * Test extracting informations from dimap document in properly done.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapAccessorTest {

    private final Element docSample;
    private final Element docMapping;

    public DimapAccessorTest() throws ParserConfigurationException, SAXException, IOException {
        Document doc = DomUtilities.read(DimapAccessorTest.class.getResourceAsStream("/org/geotoolkit/image/dimap/spotscene.xml"));
        docSample = doc.getDocumentElement();
        doc = DomUtilities.read(DimapAccessorTest.class.getResourceAsStream("/org/geotoolkit/image/dimap/propertymapping.xml"));
        docMapping = doc.getDocumentElement();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadCRS() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = DimapAccessor.readCRS(docSample);

        assertNotNull(crs);
        assertEquals(crs, CRS.decode("EPSG:32738"));
    }

    @Test
    public void testReadAffine() throws FactoryException, TransformException{
        final AffineTransform trs = DimapAccessor.readGridToCRS(docSample);

        final AffineTransform expected = new AffineTransform(10, 0, 0, -20, 300, 700);

        assertNotNull(trs);
        assertEquals(expected, trs);
    }

    @Test
    public void testReadColorBandsMapping(){
        final int[] mapping = DimapAccessor.readColorBandMapping(docSample);

        assertNotNull(mapping);
        assertEquals(3, mapping.length);

        //band index must start at 0 not like in the xml where it starts at one.
        assertEquals(0, mapping[0]);
        assertEquals(1, mapping[1]);
        assertEquals(2, mapping[2]);
    }

    @Test
    public void testReadRasterDimension(){
        final int[] dims = DimapAccessor.readRasterDimension(docSample);

        assertNotNull(dims);
        assertEquals(3, dims.length);

        assertEquals(7308, dims[0]); //rows
        assertEquals(7762, dims[1]); // cols
        assertEquals(4, dims[2]); //bands
    }

    @Test
    public void testDimapToISO() throws ParseException, IOException{

        DefaultMetadata metadata = new DefaultMetadata();

        metadata = DimapAccessor.fillMetadata(docMapping, metadata);
        
        final TreeModel model = metadata.asTree();
        System.out.println(Trees.toString(model));


        //<xsd:element minOccurs="0" ref="Dataset_Id"/> ------------------------
        //DATASET_NAME  → Dataset title (MD_Metadata.fileIdentifier)
        //COPYRIGHT     → RestrictionCode ( MD_Metadata > MD_Constraints > MD_LegalConstraints.accessConstraints)
        assertEquals("Testing Scene 2".replaceAll(":", "_").replaceAll(" ", "_").replaceAll("/", "_"), metadata.getFileIdentifier());
        final Collection<Constraints> constraints = metadata.getMetadataConstraints();
        assertNotNull(constraints);
        assertEquals(1, constraints.size());
        final Constraints constraint = constraints.iterator().next();
        assertTrue(constraint instanceof LegalConstraints);
        final LegalConstraints legalConstraint = (LegalConstraints) constraint;
        assertNotNull(legalConstraint.getAccessConstraints());
        assertEquals(0, legalConstraint.getAccessConstraints().size());
        assertEquals(1, legalConstraint.getOtherConstraints().size());
        assertEquals(1, legalConstraint.getUseConstraints().size());
        assertEquals(Restriction.COPYRIGHT, legalConstraint.getUseConstraints().iterator().next());


        //<xsd:element minOccurs="0" ref="Dataset_Frame"/> ---------------------
        //filled and tested by geotiff module
        //FRAME_LON                         → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_LAT                         → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_ROW                         → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_COL                         → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_X                           → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_Y                           → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.cornerPoints )
        //FRAME_LON (Scene Center)          → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.centerPoints )
        //FRAME_LAT (Scene Center)          → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.centerPoints )
        //FRAME_ROW (Scene Center)          → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.centerPoints )
        //FRAME_COL (Scene Center)          → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.centerPoints )
        //SCENE_ORIENTATION (Scene Center)  → ( MD_Metadata > MD_SpatialRepresentation > MD_GridSpatialReprensentation > MD_Georectified.centerPoints )
        
        //<xsd:element minOccurs="0" ref="Dataset_Use"/> -----------------------


        //<xsd:element minOccurs="0" ref="Production"/> ------------------------
        //PRODUCT_TYPE            → Type of product ( MD_Metada > identificationInfo > MD_DataIdentification.citation > CI_Citation.presentationForm > CI_PresentationFormCode )
        //PRODUCT_INFO            → Product title (MD_Metadata > identificationInfo > MD_DataIdentification.citation > CI_Citation.title)
        //DATASET_PRODUCER_NAME   → Producer Name (MD_Metadata > identificationInfo >MD_DataIdentification.citation > CI_Citation > CI_ResponsibleParty.organisationName )
        //DATASET_PRODUCER_URL    → URL Producer (MD_Metada > identificationInfo > MD_DataIdentification.citation >CI_Citation > CI_ResponsibleParty CI_Contact > CI_Address.electronicMailAddress
        //DATASET_PRODUCTION_DATE → Date de production (MD_Metadata > identificationInfo > MD_DataIdentification.citation > CI_Citation > CI_Date.date
        //SOFTWARE_NAME           → Software name (DQ_DataQuality > LI_Lineage > LI_ProcessStepL.E_ProcessStep > LE_Processing > CI_Citation.title)
        //SOFTWARE_VERSION        → Software version (DQ_DataQuqlity > LI_Lineage > LI_ProcessStepL.E_ProcessStep > LE_Processing > CI_Citation.edition)
        //PROCESSING_CENTER       → Processing center (DQ_DataQuqlity > LI_Lineage > LI_ProcessStepL.E_ProcessStep > LE_Processing > CI_Citation.citedResponsibleParty > CI_ResponsibleParty.OrganisationName)

        final Collection<Identification> identifications = metadata.getIdentificationInfo();
        assertNotNull(identifications);
        assertFalse(identifications.isEmpty());

        final Identification identification = identifications.iterator().next();
//        final Citation citation = identification.getCitation();
//        assertNotNull(citation);
//        final Collection<PresentationForm> forms = citation.getPresentationForms();
//        assertFalse(forms.isEmpty());
//        final PresentationForm form = forms.iterator().next();
//        assertEquals("SCSI1R20N", form.name());
//        assertEquals("Spot SYSTEM SCENE level 2A", citation.getTitle().toString());
//
//        final Collection<? extends ResponsibleParty> parties = citation.getCitedResponsibleParties();
//        assertNotNull(parties);
//        assertFalse(parties.isEmpty());
//        final ResponsibleParty party = parties.iterator().next();
//        assertEquals("SPOT_IMAGE",party.getOrganisationName().toString());
//
//        final Contact contact = party.getContactInfo();
//        assertNotNull(contact);
//        assertEquals("http://www.spotimage.fr",contact.getOnlineResource().getLinkage().toString());
//        assertEquals(TemporalUtilities.parseDate("2008-12-12T13:33:28.158000"),citation.getDates().iterator().next().getDate());

        final Collection<DataQuality> qualities = metadata.getDataQualityInfo();
        assertNotNull(qualities);
        assertFalse(qualities.isEmpty());
        final DataQuality quality = qualities.iterator().next();
        final Lineage lineage = quality.getLineage();
        final Collection<? extends ProcessStep> steps = lineage.getProcessSteps();
        final ProcessStep step = steps.iterator().next();
        final Processing processing = step.getProcessingInformation();
        assertNotNull(processing);
        final Collection<? extends Citation> softRefs = processing.getSoftwareReferences();
        assertNotNull(softRefs);
        assertFalse(softRefs.isEmpty());
        final Citation software = softRefs.iterator().next();
        assertEquals("CAP_T", software.getTitle().toString());
        assertEquals("SPOT5_V07_03", software.getEdition().toString());

        final Collection<? extends ResponsibleParty> citeds = software.getCitedResponsibleParties();
        assertNotNull(citeds);
        assertFalse(citeds.isEmpty());
        final ResponsibleParty cited = citeds.iterator().next();
        assertEquals("SLS_TOULOUSE", cited.getOrganisationName().toString());

        //<xsd:element minOccurs="0" ref="Dataset_Components"/> ----------------


        //<xsd:element minOccurs="0" ref="Quality_Assessment"/> ----------------


        //<xsd:element minOccurs="0" ref="Coordinate_Reference_System"/> -------
        //filled and tested by geotiff module
        //GEO_TABLES            → ( MD_METADATA > MD_ReferenceSystem.referenceSystemIdentifier >  RS_identifier.codeSpace and version )
        //HORIZONTAL_CS_CODE    → Reference Projection Système code (MD_Metadata > referenceSystemInfo > MD_ReferenceSystem.referenceSystemIdentifier > RS_Identifier.codeSpace)
        //HORIZONTAL_CS_TYPE    → ?
        //HORIZONTAL_CS_NAME    → Reference Projection Système name (MD_Metadata > referenceSystemInfo > MD_ReferenceSystem.referenceSystemIdentifier > RS_ReferenceSystem.name)
        

        //<xsd:element minOccurs="0" ref="Raster_CS"/> -------------------------
        //MAPPING
        //RASTER_CS_TYPE    → ?
        //PIXEL_ORIGIN      → ?


        //<xsd:element minOccurs="0" ref="Geoposition"/> -----------------------
        //filled and tested by geotiff module
        //ULXMAP    → ?
        //ULYMAP    → ?
        //XDIM      → X Resolution (MD_Metadata > spatialRepresentationInfo> MD_GridSpatialRepresentation.axisDimensionsProperties > MD_Dimension.resolution)
        //YDIM      → Y Resolution (MD_Metadata > spatialRepresentationInfo> MD_GridSpatialRepresentation.axisDimensionsProperties > MD_Dimension.resolution)
        
        //<xsd:element minOccurs="0" ref="Map_Declination"/> -------------------
        //<xsd:element minOccurs="0" ref="Raster_Dimensions"/> -----------------
        //filled and tested by geotiff module
        //NCOLS     → Number of COLUMN (MD_Metadata > spatialRepresentationInfo > MD_GridSpatialRepresentation.axisDimensionsProperties >MD_Dimension.dimensionSize)
        //NROWS     → Number of ROWS (MD_Metadata > spatialRepresentationInfo > MD_GridSpatialRepresentation.axisDimensionsProperties >MD_Dimension.dimensionSize)
        //NBANDS    → ?


        //<xsd:element minOccurs="0" ref="Raster_Encoding"/> -------------------
        //NBITS               → ?
        //BYTEORDER           → ?
        //COMPRESSION_NAME    → ?
        //DATA_TYPE           → ?


        //<xsd:element minOccurs="0" ref="Data_Processing"/> -------------------
        //PROCESSING_LEVEL            → ( DQ_DATAQUALITY > LI_LINEAGE > LI_Source.LE_Source.processedLEvel > MD_Identifier.abstract )
        //GEOMETRIC_PROCESSING        → ( DQ_DATAQUALITY > LI_LINEAGE > LI_Source.LE_Source.processedLEvel > MD_Identifier.abstract )
        //RADIOMETRIC_PROCESSING      → ( DQ_DATAQUALITY > LI_LINEAGE > LI_Source.LE_Source.processedLEvel > MD_Identifier.abstract )
        //MEAN_RECTIFICATION_ELEVATION→ ?
        //BAND_INDEX                  → ?
        //LOW_THRESHOLD               → ?
        //HIGH_THRESHOLD              → ?
        //LINE_SHIFT                  → ?
        //DECOMPRESSION_TYPE          → ?
        //KERNEL_ID                   → ?
        //KERNEL_DATE                 → ?
        //SAMPLING_STEP_X             → ?
        //SAMPLING_STEP_Y             → ?
        //SWIR_BAND_REGISTRATION_FLAG → ?
        //X_BANDS_REGISTRATION_FLAG   → ?
        //ALGORITHM_TYPE              → ( DQ_DATAQUALITY > LI_LINEAGE > LI_ProcessStep.LE_ProcessStep > LE_Processing > LE_Algorithm.description )
        //ALGORITHM_NAME              → ( DQ_DATAQUALITY > LI_LINEAGE > LI_ProcessStep.LE_ProcessStep > LE_Processing > LE_Algorithm > CI_Citation.title )
        //ALGORITHM_ACTIVATION        → ( DQ_DATAQUALITY > LI_LINEAGE > LI_ProcessStep.LE_ProcessStep > LE_Processing > LE_Algorithm )

        final Collection<? extends Algorithm> algos = processing.getAlgorithms();
        assertNotNull(algos);
        assertFalse(algos.isEmpty());

        final Algorithm algo = algos.iterator().next();
        assertEquals("DCT_COMPRESSION_CORRECTION_TYPE", algo.getDescription().toString());
        assertEquals("DCT_COMPRESSION_CORRECTION_NAME", algo.getCitation().getTitle().toString());


        //<xsd:element minOccurs="0" ref="Data_Access"/> -----------------------
        //DATA_FILE_ORGANISATION    → ?
        //DATA_FILE_FORMAT          → Data Format (MD_Metadata > IdentificationInfo > DataIdentification.resourceFormat > MD_Format.name et MD_Format.version)
        //DATA_FILE_FORMAT_DESC*    →
        //DATA_FILE_PATH            → ?
        final Collection<? extends Format> formats = identification.getResourceFormats();
        assertNotNull(formats);
        assertFalse(formats.isEmpty());
        final Format format = formats.iterator().next();
        assertEquals("GEOTIFF", format.getName().toString());
        assertEquals("1.0", format.getVersion().toString());


        //<xsd:element minOccurs="0" ref="Image_Display"/> ---------------------
        //filled and tested by geotiff module
        //ULXMAP    → ?
        //ULYMAP    → ?
        //XDIM      → X Resolution (MD_Metadata > spatialRepresentationInfo> MD_GridSpatialRepresentation.axisDimensionsProperties > MD_Dimension.resolution)
        //YDIM      → Y Resolution (MD_Metadata > spatialRepresentationInfo> MD_GridSpatialRepresentation.axisDimensionsProperties > MD_Dimension.resolution)


        //<xsd:element minOccurs="0" ref="Image_Interpretation"/> --------------
        //BAND_DESCRIPTION            → ?
        //PHYSICAL_UNIT               → ?
        //PHYSICAL_BIAS               → ?
        //PHYSICAL_GAIN               → ?
        //PHYSICAL_CALIBRATION_DATE   → ?
        //BAND_INDEX                  → ?
        //DATA_STRIP_ID               → ?


        //<xsd:element minOccurs="0" ref="Dataset_Sources"/> -------------------
        //SOURCE_TYPE *             → ?
        //SOURCE_ID *               → ?
        //SOURCE_DESCRIPTION *      → Abstract ( MD_Metadata > identificationInfo > MD_DataIdentification.abstract )
        //GRID_REFERENCE            → ?
        //SHIFT_VALUE               → ?
        //IMAGING_DATE              → Acquisition date ( MI_AcquisitionInformation > MI_Operation.citation > MD_Citation.date)
        //IMAGING_TIME              → ?
        //MISSION                   → Mission ( MI_AcquisitionInformation > MI_Operation.description)
        //MISSION_INDEX             → Mission index (MI_AcquisitionInformation > MI_Operation.description.identifier > MD_identifier.code)
        //INSTRUMENT                → instrument ( MI_AcquisitionInformation > MI_Instrument.description.type )
        //INSTRUMENT_INDEX          → instrument description ( MI_AcquisitionInformation > MI_Instrument.type.description )
        //SENSOR_CODE
        //SCENE_PROCESSING_LEVEL    → ( MD_Metadata > MD_ContentInformation.MD_CoverageDescription.MD_ImageDescription )
        //INCIDENCE_ANGLE           → ( MD_Metadata > MD_ContentInformation.MD_CoverageDescription.MD_ImageDescription )
        //VIEWING_ANGLE             → ( MD_Metadata > MD_ContentInformation.MD_CoverageDescription.MD_ImageDescription )
        //SUN_AZIMUTH               → Sun Azimut ( MD_Metadata > MD_ContentInformation > MD_CoverageDescription > MD_ImageDescription.illuminationAzimutAngle)
        //SUN_ELEVATION             → Sun elevation ( MD_Metadata > MD_ContentInformation > MD_CoverageDescription > MD_ImageDescription.illuminationElevationAngle)
        //REVOLUTION_NUMBER         → ?
        //COMPRESSION_MODE          → ?
        //DIRECT_PLAYBACK_INDICATOR → ?
        //REFOCUSING_STEP_NUM       → ?
        //SWATH_MODE                → ?

        assertEquals("SCENE HRG1 J", identification.getAbstract().toString());

        final Collection<? extends AcquisitionInformation> acquis = metadata.getAcquisitionInformation();
        assertNotNull(acquis);
        assertFalse(acquis.isEmpty());
        final AcquisitionInformation acqui = acquis.iterator().next();

        final Collection<? extends Operation> operations = acqui.getOperations();
        assertNotNull(operations);
        assertFalse(operations.isEmpty());
        final Operation operation = operations.iterator().next();
//        assertEquals(
//            TemporalUtilities.parseDate("2007-03-01"),
//            operation.getCitation().getDates().iterator().next().getDate());
        assertEquals("SPOT", operation.getDescription().toString());
        assertEquals("5", operation.getIdentifier().getCode());

        final Collection<? extends Instrument> insts = acqui.getInstruments();
        assertNotNull(insts);
        assertFalse(insts.isEmpty());
        final Instrument inst = insts.iterator().next();
        assertEquals("HRG", inst.getDescription().toString());
        assertEquals("HRG1", inst.getIdentifier().getCode());


        final Collection<ContentInformation> infos = metadata.getContentInfo();
        assertNotNull(infos);
        assertFalse(infos.isEmpty());
        final ContentInformation info = infos.iterator().next();
        assertTrue(info instanceof ImageDescription);
        final ImageDescription imgDesc = (ImageDescription) info;
        assertEquals(66.084528d,imgDesc.getIlluminationAzimuthAngle().doubleValue(), 0.00000001d);
        assertEquals(59.429144d,imgDesc.getIlluminationElevationAngle().doubleValue(), 0.00000001d);


        //Satellite_Time -------------------------------------------------------
        //UT_DATE         → ?
        //CLOCK_VALUE     → ?
        //CLOCK_PERIOD    → ?
        //BOARD_TIME      → ?
        //TAI_TUC         → ?


        //<xsd:element minOccurs="0" ref="Vector_Attributes"/> -----------------

        
    }

}
