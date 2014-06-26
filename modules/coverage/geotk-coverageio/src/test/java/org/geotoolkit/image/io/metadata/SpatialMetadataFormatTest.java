/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.util.Arrays;
import java.util.Locale;
import java.io.Writer;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javax.imageio.metadata.IIOMetadataFormat;

import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.operation.Conversion;

import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;

import org.junit.*;

import static org.junit.Assert.*;
import static javax.imageio.metadata.IIOMetadataFormat.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests {@link SpatialMetadataFormat}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.04
 */
public final strictfp class SpatialMetadataFormatTest {
    /**
     * Tests the elements in the stream metadata format instance.
     *
     * @since 3.06
     */
    @Test
    public void testStreamMetadataFormat() {
        final IIOMetadataFormat f = SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);

        assertEquals(DataIdentification.class,      f.getObjectClass           ("DiscoveryMetadata"));
        assertEquals(CHILD_POLICY_SOME,             f.getChildPolicy           ("DiscoveryMetadata"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "citation"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DiscoveryMetadata", "citation"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "abstract"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DiscoveryMetadata", "abstract"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "purpose"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DiscoveryMetadata", "purpose"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "credits"));
        assertEquals(VALUE_LIST,                    f.getAttributeValueType    ("DiscoveryMetadata", "credits"));
        assertEquals(0,                             f.getAttributeListMinLength("DiscoveryMetadata", "credits"));
        assertEquals(Integer.MAX_VALUE,             f.getAttributeListMaxLength("DiscoveryMetadata", "credits"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "status"));
        assertEquals(VALUE_ENUMERATION,             f.getAttributeValueType    ("DiscoveryMetadata", "status"));
        assertIsEnum("planned",                     f.getAttributeEnumerations ("DiscoveryMetadata", "status"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "topicCategories"));
        assertEquals(VALUE_ENUMERATION,             f.getAttributeValueType    ("DiscoveryMetadata", "topicCategories"));
        assertIsEnum("environment",                 f.getAttributeEnumerations ("DiscoveryMetadata", "topicCategories"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "environmentDescription"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DiscoveryMetadata", "environmentDescription"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DiscoveryMetadata", "supplementalInformation"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DiscoveryMetadata", "supplementalInformation"));
        assertEquals(CHILD_POLICY_REPEAT,           f.getChildPolicy           ("DescriptiveKeywords"));
        assertEquals(Keywords.class,                f.getObjectClass           ("DescriptiveKeywordsEntry"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DescriptiveKeywordsEntry", "keywords"));
        assertEquals(VALUE_LIST,                    f.getAttributeValueType    ("DescriptiveKeywordsEntry", "keywords"));
        assertEquals(1,                             f.getAttributeListMinLength("DescriptiveKeywordsEntry", "keywords"));
        assertEquals(Integer.MAX_VALUE,             f.getAttributeListMaxLength("DescriptiveKeywordsEntry", "keywords"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DescriptiveKeywordsEntry", "thesaurusName"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("DescriptiveKeywordsEntry", "thesaurusName"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("DescriptiveKeywordsEntry", "type"));
        assertEquals(VALUE_ENUMERATION,             f.getAttributeValueType    ("DescriptiveKeywordsEntry", "type"));
        assertIsEnum("place",                       f.getAttributeEnumerations ("DescriptiveKeywordsEntry", "type"));
        assertEquals(Resolution.class,              f.getObjectClass           ("SpatialResolution"));
        assertEquals(CHILD_POLICY_SOME,             f.getChildPolicy           ("SpatialResolution"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("SpatialResolution", "distance"));
        assertEquals(VALUE_RANGE,                   f.getAttributeValueType    ("SpatialResolution", "distance"));
        assertEquals("0.0",                         f.getAttributeMinValue     ("SpatialResolution", "distance"));
        assertNull  (                               f.getAttributeMaxValue     ("SpatialResolution", "distance"));
        assertEquals(Extent.class,                  f.getObjectClass           ("Extent"));
        assertEquals(CHILD_POLICY_SOME,             f.getChildPolicy           ("Extent"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("Extent", "description"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("Extent", "description"));
        assertEquals(GeographicBoundingBox.class,   f.getObjectClass           ("GeographicElement"));
        assertEquals(CHILD_POLICY_EMPTY,            f.getChildPolicy           ("GeographicElement"));
        assertEquals(DATATYPE_BOOLEAN,              f.getAttributeDataType     ("GeographicElement", "inclusion"));
        assertEquals(VALUE_ENUMERATION,             f.getAttributeValueType    ("GeographicElement", "inclusion"));
        assertIsEnum("TRUE",                        f.getAttributeEnumerations ("GeographicElement", "inclusion"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("GeographicElement", "westBoundLongitude"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE, f.getAttributeValueType    ("GeographicElement", "westBoundLongitude"));
        assertEquals("-180.0",                      f.getAttributeMinValue     ("GeographicElement", "westBoundLongitude"));
        assertEquals("180.0",                       f.getAttributeMaxValue     ("GeographicElement", "westBoundLongitude"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("GeographicElement", "eastBoundLongitude"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE, f.getAttributeValueType    ("GeographicElement", "eastBoundLongitude"));
        assertEquals("-180.0",                      f.getAttributeMinValue     ("GeographicElement", "eastBoundLongitude"));
        assertEquals("180.0",                       f.getAttributeMaxValue     ("GeographicElement", "eastBoundLongitude"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("GeographicElement", "southBoundLatitude"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE, f.getAttributeValueType    ("GeographicElement", "southBoundLatitude"));
        assertEquals("-90.0",                       f.getAttributeMinValue     ("GeographicElement", "southBoundLatitude"));
        assertEquals("90.0",                        f.getAttributeMaxValue     ("GeographicElement", "southBoundLatitude"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("GeographicElement", "northBoundLatitude"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE, f.getAttributeValueType    ("GeographicElement", "northBoundLatitude"));
        assertEquals("-90.0",                       f.getAttributeMinValue     ("GeographicElement", "northBoundLatitude"));
        assertEquals("90.0",                        f.getAttributeMaxValue     ("GeographicElement", "northBoundLatitude"));
        assertEquals(VerticalExtent.class,          f.getObjectClass           ("VerticalElement"));
        assertEquals(CHILD_POLICY_EMPTY,            f.getChildPolicy           ("VerticalElement"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("VerticalElement", "minimumValue"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("VerticalElement", "minimumValue"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("VerticalElement", "maximumValue"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("VerticalElement", "maximumValue"));
        assertEquals(AcquisitionInformation.class,  f.getObjectClass           ("AcquisitionMetadata"));
        assertEquals(CHILD_POLICY_SOME,             f.getChildPolicy           ("AcquisitionMetadata"));
        assertEquals(EnvironmentalRecord.class,     f.getObjectClass           ("EnvironmentalConditions"));
        assertEquals(CHILD_POLICY_EMPTY,            f.getChildPolicy           ("EnvironmentalConditions"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("EnvironmentalConditions", "averageAirTemperature"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("EnvironmentalConditions", "averageAirTemperature"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("EnvironmentalConditions", "maxRelativeHumidity"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE, f.getAttributeValueType    ("EnvironmentalConditions", "maxRelativeHumidity"));
        assertEquals("0.0",                         f.getAttributeMinValue     ("EnvironmentalConditions", "maxRelativeHumidity"));
        assertEquals("100.0",                       f.getAttributeMaxValue     ("EnvironmentalConditions", "maxRelativeHumidity"));
        assertEquals(DATATYPE_DOUBLE,               f.getAttributeDataType     ("EnvironmentalConditions", "maxAltitude"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("EnvironmentalConditions", "maxAltitude"));
        assertEquals(DATATYPE_STRING,               f.getAttributeDataType     ("EnvironmentalConditions", "meteorologicalConditions"));
        assertEquals(VALUE_ARBITRARY,               f.getAttributeValueType    ("EnvironmentalConditions", "meteorologicalConditions"));
    }

    /**
     * Tests the elements in the image metadata format instance.
     */
    @Test
    public void testImageMetadataFormat() {
        final IIOMetadataFormat f = SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);

        assertEquals(ImageDescription.class,          f.getObjectClass           ("ImageDescription"));
        assertEquals(CHILD_POLICY_SOME,               f.getChildPolicy           ("ImageDescription"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ImageDescription", "contentType"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "contentType"));
        assertIsEnum("physicalMeasurement",           f.getAttributeEnumerations ("ImageDescription", "contentType"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("ImageDescription", "illuminationElevationAngle"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("ImageDescription", "illuminationAzimuthAngle"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ImageDescription", "imagingCondition"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "imagingCondition"));
        assertIsEnum("cloud",                         f.getAttributeEnumerations ("ImageDescription", "imagingCondition"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("ImageDescription", "cloudCoverPercentage"));
        assertEquals(VALUE_RANGE_MIN_MAX_INCLUSIVE,   f.getAttributeValueType    ("ImageDescription", "cloudCoverPercentage"));
        assertEquals("0.0",                           f.getAttributeMinValue     ("ImageDescription", "cloudCoverPercentage"));
        assertEquals("100.0",                         f.getAttributeMaxValue     ("ImageDescription", "cloudCoverPercentage"));
        assertEquals(DATATYPE_BOOLEAN,                f.getAttributeDataType     ("ImageDescription", "radiometricCalibrationDataAvailable"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "radiometricCalibrationDataAvailable"));
        assertIsEnum("TRUE",                          f.getAttributeEnumerations ("ImageDescription", "radiometricCalibrationDataAvailable"));
        assertEquals(DATATYPE_BOOLEAN,                f.getAttributeDataType     ("ImageDescription", "cameraCalibrationInformationAvailable"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "cameraCalibrationInformationAvailable"));
        assertIsEnum("TRUE",                          f.getAttributeEnumerations ("ImageDescription", "cameraCalibrationInformationAvailable"));
        assertEquals(DATATYPE_BOOLEAN,                f.getAttributeDataType     ("ImageDescription", "filmDistortionInformationAvailable"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "filmDistortionInformationAvailable"));
        assertIsEnum("TRUE",                          f.getAttributeEnumerations ("ImageDescription", "filmDistortionInformationAvailable"));
        assertEquals(DATATYPE_BOOLEAN,                f.getAttributeDataType     ("ImageDescription", "lensDistortionInformationAvailable"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("ImageDescription", "lensDistortionInformationAvailable"));
        assertIsEnum("TRUE",                          f.getAttributeEnumerations ("ImageDescription", "lensDistortionInformationAvailable"));
        assertEquals(Identifier.class,                f.getObjectClass           ("ImageQualityCode"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ImageQualityCode", "code"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ImageQualityCode", "code"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ImageQualityCode", "authority"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ImageQualityCode", "authority"));
        assertEquals(Identifier.class,                f.getObjectClass           ("ProcessingLevelCode"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ProcessingLevelCode", "code"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ProcessingLevelCode", "code"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ProcessingLevelCode", "authority"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ProcessingLevelCode", "authority"));
        assertEquals(CHILD_POLICY_REPEAT,             f.getChildPolicy           ("Dimensions"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("Dimension"));
        assertEquals(SampleDimension.class,           f.getObjectClass           ("Dimension"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Dimension", "descriptor"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "descriptor"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Dimension", "sequenceIdentifier"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "sequenceIdentifier"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "minValue"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "minValue"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "maxValue"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "maxValue"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "fillSampleValues"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("Dimension", "fillSampleValues"));
        assertEquals(0,                               f.getAttributeListMinLength("Dimension", "fillSampleValues"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("Dimension", "fillSampleValues"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Dimension", "units"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "units"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "peakResponse"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "peakResponse"));
        assertEquals(DATATYPE_INTEGER,                f.getAttributeDataType     ("Dimension", "bitsPerValue"));
        assertEquals(VALUE_RANGE_MIN_INCLUSIVE,       f.getAttributeValueType    ("Dimension", "bitsPerValue"));
        assertEquals("1",                             f.getAttributeMinValue     ("Dimension", "bitsPerValue"));
        assertNull  (                                 f.getAttributeMaxValue     ("Dimension", "bitsPerValue"));
        assertEquals(DATATYPE_INTEGER,                f.getAttributeDataType     ("Dimension", "toneGradation"));
        assertEquals(VALUE_RANGE_MIN_INCLUSIVE,       f.getAttributeValueType    ("Dimension", "toneGradation"));
        assertEquals("0",                             f.getAttributeMinValue     ("Dimension", "toneGradation"));
        assertNull  (                                 f.getAttributeMaxValue     ("Dimension", "toneGradation"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "minValue"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "minValue"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "scaleFactor"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "scaleFactor"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "offset"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Dimension", "offset"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Dimension", "bandBoundaryDefinition"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("Dimension", "bandBoundaryDefinition"));
        assertIsEnum("fiftyPercent",                  f.getAttributeEnumerations ("Dimension", "bandBoundaryDefinition"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Dimension", "nominalSpatialResolution"));
        assertEquals(VALUE_RANGE,                     f.getAttributeValueType    ("Dimension", "nominalSpatialResolution"));
        assertEquals("0.0",                           f.getAttributeMinValue     ("Dimension", "nominalSpatialResolution"));
        assertNull  (                                 f.getAttributeMaxValue     ("Dimension", "nominalSpatialResolution"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Dimension", "transferFunctionType"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("Dimension", "transferFunctionType"));
        assertIsEnum("logarithmic",                   f.getAttributeEnumerations ("Dimension", "transferFunctionType"));
        assertEquals(CHILD_POLICY_REPEAT,             f.getChildPolicy           ("RangeElementDescriptions"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("RangeElementDescription"));
        assertEquals(RangeElementDescription.class,   f.getObjectClass           ("RangeElementDescription"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("RangeElementDescription", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("RangeElementDescription", "name"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("RangeElementDescription", "definition"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("RangeElementDescription", "definition"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("RangeElementDescription", "rangeElements"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("RangeElementDescription", "rangeElements"));
        assertEquals(1,                               f.getAttributeListMinLength("RangeElementDescription", "rangeElements"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("RangeElementDescription", "rangeElements"));
        assertEquals(Georectified.class,              f.getObjectClass           ("SpatialRepresentation"));
        assertEquals(DATATYPE_INTEGER,                f.getAttributeDataType     ("SpatialRepresentation", "numberOfDimensions"));
        assertEquals(VALUE_RANGE_MIN_INCLUSIVE,       f.getAttributeValueType    ("SpatialRepresentation", "numberOfDimensions"));
        assertEquals("0",                             f.getAttributeMinValue     ("SpatialRepresentation", "numberOfDimensions"));
        assertNull  (                                 f.getAttributeMaxValue     ("SpatialRepresentation", "numberOfDimensions"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("SpatialRepresentation", "cellGeometry"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("SpatialRepresentation", "cellGeometry"));
        assertIsEnum("area",                          f.getAttributeEnumerations ("SpatialRepresentation", "cellGeometry"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("SpatialRepresentation", "pointInPixel"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("SpatialRepresentation", "pointInPixel"));
        assertIsEnum("upperLeft",                     f.getAttributeEnumerations ("SpatialRepresentation", "pointInPixel"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("SpatialRepresentation", "centerPoint"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("SpatialRepresentation", "centerPoint"));
        assertEquals(0,                               f.getAttributeListMinLength("SpatialRepresentation", "centerPoint"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("SpatialRepresentation", "centerPoint"));
        assertEquals(RectifiedGrid.class,             f.getObjectClass           ("RectifiedGridDomain"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("RectifiedGridDomain", "origin"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("RectifiedGridDomain", "origin"));
        assertEquals(1,                               f.getAttributeListMinLength("RectifiedGridDomain", "origin"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("RectifiedGridDomain", "origin"));
        assertEquals(GridEnvelope.class,              f.getObjectClass           ("Limits"));
        assertEquals(DATATYPE_INTEGER,                f.getAttributeDataType     ("Limits", "low"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("Limits", "low"));
        assertEquals(1,                               f.getAttributeListMinLength("Limits", "low"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("Limits", "low"));
        assertEquals(DATATYPE_INTEGER,                f.getAttributeDataType     ("Limits", "high"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("Limits", "high"));
        assertEquals(1,                               f.getAttributeListMinLength("Limits", "high"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("Limits", "high"));
        assertEquals(CHILD_POLICY_REPEAT,             f.getChildPolicy           ("OffsetVectors"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("OffsetVector"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("OffsetVector", "values"));
        assertEquals(VALUE_LIST,                      f.getAttributeValueType    ("OffsetVector", "values"));
        assertEquals(1,                               f.getAttributeListMinLength("OffsetVector", "values"));
        assertEquals(Integer.MAX_VALUE,               f.getAttributeListMaxLength("OffsetVector", "values"));
        assertEquals(CoordinateReferenceSystem.class, f.getObjectClass           ("CoordinateReferenceSystem"));
        assertEquals(CHILD_POLICY_SOME,               f.getChildPolicy           ("CoordinateReferenceSystem"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("CoordinateReferenceSystem", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("CoordinateReferenceSystem", "name"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("CoordinateReferenceSystem", "type"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("CoordinateReferenceSystem", "type"));
        assertEquals(CoordinateSystem.class,          f.getObjectClass           ("CoordinateSystem"));
        assertEquals(CHILD_POLICY_SOME,               f.getChildPolicy           ("CoordinateSystem"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("CoordinateSystem", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("CoordinateSystem", "name"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("CoordinateSystem", "type"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("CoordinateSystem", "type"));
        assertEquals(CHILD_POLICY_REPEAT,             f.getChildPolicy           ("Axes"));
        assertEquals(CoordinateSystemAxis.class,      f.getObjectClass           ("CoordinateSystemAxis"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("CoordinateSystemAxis"));
        assertEquals(Datum.class,                     f.getObjectClass           ("Datum"));
        assertEquals(CHILD_POLICY_SOME,               f.getChildPolicy           ("Datum"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Datum", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Datum", "name"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Datum", "type"));
        assertEquals(VALUE_ENUMERATION,               f.getAttributeValueType    ("Datum", "type"));
        assertEquals(Ellipsoid.class,                 f.getObjectClass           ("Ellipsoid"));
        assertEquals(DefaultEllipsoid.WGS84,          f.getObjectDefaultValue    ("Ellipsoid"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("Ellipsoid"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Ellipsoid", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Ellipsoid", "name"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Ellipsoid", "semiMajorAxis"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Ellipsoid", "semiMajorAxis"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Ellipsoid", "semiMinorAxis"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Ellipsoid", "semiMinorAxis"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("Ellipsoid", "inverseFlattening"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Ellipsoid", "inverseFlattening"));
        assertEquals(PrimeMeridian.class,             f.getObjectClass           ("PrimeMeridian"));
        assertEquals(DefaultPrimeMeridian.GREENWICH,  f.getObjectDefaultValue    ("PrimeMeridian"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("PrimeMeridian"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("PrimeMeridian", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("PrimeMeridian", "name"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("PrimeMeridian", "greenwichLongitude"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("PrimeMeridian", "greenwichLongitude"));
        assertEquals(Conversion.class,                f.getObjectClass           ("Conversion"));
        assertEquals(CHILD_POLICY_SOME,               f.getChildPolicy           ("Conversion"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("Conversion", "method"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("Conversion", "method"));
        assertEquals(ParameterValueGroup.class,       f.getObjectClass           ("Parameters"));
        assertEquals(CHILD_POLICY_REPEAT,             f.getChildPolicy           ("Parameters"));
        assertEquals(ParameterValue.class,            f.getObjectClass           ("ParameterValue"));
        assertEquals(CHILD_POLICY_EMPTY,              f.getChildPolicy           ("ParameterValue"));
        assertEquals(DATATYPE_STRING,                 f.getAttributeDataType     ("ParameterValue", "name"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ParameterValue", "name"));
        assertEquals(DATATYPE_DOUBLE,                 f.getAttributeDataType     ("ParameterValue", "value"));
        assertEquals(VALUE_ARBITRARY,                 f.getAttributeValueType    ("ParameterValue", "value"));
    }

    /**
     * Asserts that the given enumeration contains the given value.
     */
    private static void assertIsEnum(final String value, final String[] enumeration) {
        for (int i=0; i<enumeration.length; i++) {
            if (value.equals(enumeration[i])) {
                return;
            }
        }
        fail("Value \"" + value + "\" is not found in the following enumeration: " + Arrays.toString(enumeration));
    }

    /**
     * Tests the descriptions in the image metadata format instance.
     */
    @Test
    public void testImageDescriptions() {
        final IIOMetadataFormat format = SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
        assertEquals("Identifier for the level of processing that has been applied to the resource.",
                format.getElementDescription("ProcessingLevelCode", Locale.ENGLISH));
        assertEquals("Area of the dataset obscured by clouds, expressed as a percentage of the spatial extent.",
                format.getAttributeDescription("ImageDescription", "cloudCoverPercentage", Locale.ENGLISH));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#getElementParent(String)} method on stream metadata.
     *
     * @since 3.06
     */
    @Test
    public void testStreamParents() {
        final SpatialMetadataFormat format = SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);
        assertNull(format.getElementParent(GEOTK_FORMAT_NAME));

        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("DiscoveryMetadata"));
        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("AcquisitionMetadata"));
        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("QualityMetadata"));
        assertNull  ("'citation' is an attribute, not an element.", format.getElementParent("citation"));
        assertEquals("DiscoveryMetadata",   format.getElementParent("DescriptiveKeywords"));
        assertEquals("DescriptiveKeywords", format.getElementParent("DescriptiveKeywordsEntry"));
        assertEquals("DiscoveryMetadata",   format.getElementParent("SpatialResolution"));
        assertEquals("DiscoveryMetadata",   format.getElementParent("Extent"));
        assertEquals("Extent",              format.getElementParent("GeographicElement"));
        assertEquals("Extent",              format.getElementParent("VerticalElement"));
        assertEquals("AcquisitionMetadata", format.getElementParent("EnvironmentalConditions"));
        assertEquals("AcquisitionMetadata", format.getElementParent("Platform"));
        assertEquals("Platform",            format.getElementParent("Instruments"));
        assertEquals("Instruments",         format.getElementParent("Instrument"));
        assertEquals("Instrument",          format.getElementParent("Identifier"));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#getElementParent(String)} method on image metadata.
     *
     * @since 3.06
     */
    @Test
    public void testImageParents() {
        final SpatialMetadataFormat format = SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
        assertNull(format.getElementParent(GEOTK_FORMAT_NAME));

        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("ImageDescription"));
        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("SpatialRepresentation"));
        assertEquals(GEOTK_FORMAT_NAME, format.getElementParent("RectifiedGridDomain"));
        assertNull  ("'contentType' is an attribute, not an element.", format.getElementParent("contentType"));
        assertEquals("ImageDescription",    format.getElementParent("ImageQualityCode"));
        assertEquals("ImageDescription",    format.getElementParent("Dimensions"));
        assertEquals("Dimensions",          format.getElementParent("Dimension"));
        assertEquals("RectifiedGridDomain", format.getElementParent("Limits"));
        assertEquals("RectifiedGridDomain", format.getElementParent("OffsetVectors"));
        assertEquals("OffsetVectors",       format.getElementParent("OffsetVector"));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#getElementPath(String)} method on stream metadata.
     *
     * @since 3.06
     */
    @Test
    public void testStreamPaths() {
        final SpatialMetadataFormat format = SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME);
        assertNull(format.getElementPath(GEOTK_FORMAT_NAME));

        assertNull  ("'citation' is an attribute, not an element.",                   format.getElementPath("citation"));
        assertEquals("DiscoveryMetadata",                                             format.getElementPath("DiscoveryMetadata"));
        assertEquals("DiscoveryMetadata/DescriptiveKeywords",                         format.getElementPath("DescriptiveKeywords"));
        assertEquals("DiscoveryMetadata/DescriptiveKeywords/DescriptiveKeywordsEntry",format.getElementPath("DescriptiveKeywordsEntry"));
        assertEquals("DiscoveryMetadata/SpatialResolution",                           format.getElementPath("SpatialResolution"));
        assertEquals("DiscoveryMetadata/Extent",                                      format.getElementPath("Extent"));
        assertEquals("DiscoveryMetadata/Extent/GeographicElement",                    format.getElementPath("GeographicElement"));
        assertEquals("DiscoveryMetadata/Extent/VerticalElement",                      format.getElementPath("VerticalElement"));
        assertEquals("AcquisitionMetadata",                                           format.getElementPath("AcquisitionMetadata"));
        assertEquals("AcquisitionMetadata/EnvironmentalConditions",                   format.getElementPath("EnvironmentalConditions"));
        assertEquals("AcquisitionMetadata/Platform",                                  format.getElementPath("Platform"));
        assertEquals("AcquisitionMetadata/Platform/Instruments",                      format.getElementPath("Instruments"));
        assertEquals("AcquisitionMetadata/Platform/Instruments/Instrument",           format.getElementPath("Instrument"));
        assertEquals("AcquisitionMetadata/Platform/Instruments/Instrument/Identifier",format.getElementPath("Identifier"));
        assertEquals("QualityMetadata",                                               format.getElementPath("QualityMetadata"));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#getElementPath(String)} method on image metadata.
     *
     * @since 3.06
     */
    @Test
    public void testImagePaths() {
        final SpatialMetadataFormat format = SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME);
        assertNull(format.getElementPath(GEOTK_FORMAT_NAME));

        assertNull  ("'contentType' is an attribute, not an element.", format.getElementPath("contentType"));
        assertEquals("ImageDescription",                               format.getElementPath("ImageDescription"));
        assertEquals("ImageDescription/ImageQualityCode",              format.getElementPath("ImageQualityCode"));
        assertEquals("ImageDescription/Dimensions",                    format.getElementPath("Dimensions"));
        assertEquals("ImageDescription/Dimensions/Dimension",          format.getElementPath("Dimension"));
        assertEquals("SpatialRepresentation",                          format.getElementPath("SpatialRepresentation"));
        assertEquals("RectifiedGridDomain",                            format.getElementPath("RectifiedGridDomain"));
        assertEquals("RectifiedGridDomain/Limits",                     format.getElementPath("Limits"));
        assertEquals("RectifiedGridDomain/OffsetVectors",              format.getElementPath("OffsetVectors"));
        assertEquals("RectifiedGridDomain/OffsetVectors/OffsetVector", format.getElementPath("OffsetVector"));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#toString()} method.
     * This is also used for producing the tree to copy in the javadoc.
     *
     * @throws IOException If an I/O error occurred while writing the tree to disk.
     *         This is normally not enabled.
     */
    @Test
    public void testToString() throws IOException {
        final String stream = SpatialMetadataFormat.getStreamInstance(GEOTK_FORMAT_NAME).toString();
        final String image  = SpatialMetadataFormat.getImageInstance (GEOTK_FORMAT_NAME).toString();
        assertTrue(!stream.isEmpty()); // Dummy check. The real interesting part is the write to a file.
        assertTrue(!image .isEmpty());
        if (false) {
            try (Writer out = new OutputStreamWriter(new FileOutputStream("SpatialMetadataFormat.txt"), "UTF-8")) {
                out.write(stream);
                out.write(System.lineSeparator());
                out.write(image);
            }
        }
    }
}
