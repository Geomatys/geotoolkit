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

import java.util.Locale;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.geotoolkit.internal.io.JNDI;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.acquisition.AcquisitionInformation;
import org.opengis.metadata.acquisition.EnvironmentalRecord;
import org.opengis.metadata.acquisition.Instrument;
import org.opengis.metadata.acquisition.Platform;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.ImageDescription;
import org.opengis.metadata.content.RangeElementDescription;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.identification.Keywords;
import org.opengis.metadata.identification.Resolution;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.VerticalExtent;
import org.opengis.metadata.spatial.Georectified;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.metadata.ExtendedElementInformation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.Conversion;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;

import org.opengis.util.InternationalString;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.MetadataStandard;
import org.geotoolkit.referencing.cs.*;
import org.geotoolkit.referencing.crs.*;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.internal.image.io.DataTypes;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.CommonCRS;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Describes the structure of {@linkplain SpatialMetadata spatial metadata}.
 * The default {@linkplain #getStreamInstance(String) stream} and {@link #getImageInstance(String)
 * image} formats are inferred from a subset of the GeoAPI metadata interfaces, especially
 * {@link Metadata} and {@link ImageDescription}. Consequently those instances can be considered
 * as profiles of ISO 19115-2, with a few minor departures:
 * <p>
 * <ul>
 *   <li>The {@link Band} interface defined by ISO 19115-2 is used only when the values are
 *       measurements of wavelengths in the electromagnetic spectrum, as specified in the ISO
 *       specification. Otherwise the {@link SampleDimension} interface (which is very similar)
 *       is used.</li>
 * </ul>
 * <p>
 * <a name="default-formats">The tree structures are show below</a>. As a general rule, the name of
 * <em>elements</em> start with a upper case letter while the name of <em>attributes</em> start with
 * a lower case letter. The valid types of attributes values are <a href="package-summary.html#accessor-types">listed here</a>.
 * For browsing these trees in an applet together with additional information, see the
 * <a href="http://www.geotoolkit.org/demos/geotk-simples/applet/IIOMetadataPanel.html">IIOMetadataPanel applet</a>.
 *
<blockquote><table border="1" cellpadding="12">
<tr bgcolor="lightblue"><th>Stream metadata</th><th>Image metadata</th></tr>
<tr><td nowrap valign="top" width="50%">
<pre>geotk-coverageio_3.07
├───<b>DiscoveryMetadata</b> : {@linkplain DataIdentification}
│   ├───citation
│   ├───abstract
│   ├───purpose
│   ├───credits
│   ├───status
│   ├───<b>DescriptiveKeywords</b> : {@linkplain Keywords}[]
│   │   └───DescriptiveKeywordsEntry
│   │       ├───keywords
│   │       ├───thesaurusName
│   │       └───type
│   ├───<b>SpatialResolution</b> : {@linkplain Resolution}
│   │   ├───distance
│   │   └───EquivalentScale
│   │       └───denominator
│   ├───topicCategories
│   ├───environmentDescription
│   ├───<b>Extent</b> : {@linkplain Extent}
│   │   ├───description
│   │   ├───<b>GeographicElement</b> : {@linkplain GeographicBoundingBox}
│   │   │   ├───inclusion
│   │   │   ├───westBoundLongitude
│   │   │   ├───eastBoundLongitude
│   │   │   ├───southBoundLatitude
│   │   │   └───northBoundLatitude
│   │   └───<b>VerticalElement</b> : {@linkplain VerticalExtent}
│   │       ├───minimumValue
│   │       ├───maximumValue
│   │       └───verticalCRS
│   └───supplementalInformation
├───<b>AcquisitionMetadata</b> : {@linkplain AcquisitionInformation}
│   ├───<b>EnvironmentalConditions</b> : {@linkplain EnvironmentalRecord}
│   │   ├───averageAirTemperature
│   │   ├───maxRelativeHumidity
│   │   ├───maxAltitude
│   │   └───meteorologicalConditions
│   └───<b>Platform</b> : {@linkplain Platform}
│       ├───citation
│       ├───identifier
│       ├───description
│       └───Instruments
│           └───<b>Instrument</b> : {@linkplain Instrument}
│               ├───citation
│               ├───Identifier : {@linkplain Identifier}
│               │   ├───code
│               │   └───authority
│               ├───type
│               └───description
└───<b>QualityMetadata</b> : {@linkplain DataQuality}
    └───<b>Report</b> : {@linkplain Element}
        ├───namesOfMeasure
        ├───measureIdentification
        ├───measureDescription
        ├───evaluationMethodType
        ├───evaluationMethodDescription
        ├───evaluationProcedure
        └───date</pre>
</td><td nowrap valign="top" width="50%">
<pre>geotk-coverageio_3.07
├───<b>ImageDescription</b> : {@linkplain ImageDescription}
│   ├───contentType
│   ├───illuminationElevationAngle
│   ├───illuminationAzimuthAngle
│   ├───imagingCondition
│   ├───ImageQualityCode : {@linkplain Identifier}
│   │   ├───code
│   │   └───authority
│   ├───cloudCoverPercentage
│   ├───ProcessingLevelCode : {@linkplain Identifier}
│   │   ├───code
│   │   └───authority
│   ├───compressionGenerationQuantity
│   ├───triangulationIndicator
│   ├───radiometricCalibrationDataAvailable
│   ├───cameraCalibrationInformationAvailable
│   ├───filmDistortionInformationAvailable
│   ├───lensDistortionInformationAvailable
│   ├───<b>Dimensions</b> : {@linkplain SampleDimension}[]
│   │   └───Dimension
│   │       ├───descriptor
│   │       ├───sequenceIdentifier
│   │       ├───validSampleValues
│   │       ├───fillSampleValues
│   │       ├───minValue
│   │       ├───maxValue
│   │       ├───units
│   │       ├───peakResponse
│   │       ├───bitsPerValue
│   │       ├───toneGradation
│   │       ├───scaleFactor
│   │       ├───offset
│   │       ├───bandBoundaryDefinition
│   │       ├───nominalSpatialResolution
│   │       ├───transferFunctionType
│   │       ├───transmittedPolarization
│   │       └───detectedPolarization
│   └───<b>RangeElementDescriptions</b> : {@linkplain RangeElementDescription}
│       └───RangeElementDescription
│           ├───name
│           ├───definition
│           └───rangeElements
├───<b>SpatialRepresentation</b> : {@linkplain Georectified}
│   ├───numberOfDimensions
│   ├───cellGeometry
│   ├───centerPoint
│   └───pointInPixel
└───<b>RectifiedGridDomain</b> : {@linkplain RectifiedGrid}
    ├───<b>Limits</b> : {@linkplain GridEnvelope}
    │   ├───low
    │   └───high
    ├───origin
    ├───<b>OffsetVectors</b>
    │   └───OffsetVector
    │       └───values
    └───<b>CoordinateReferenceSystem</b> : {@linkplain CoordinateReferenceSystem}
        ├───name
        ├───type
        ├───<b>CoordinateSystem</b> : {@linkplain CoordinateSystem}
        │   ├───name
        │   ├───type
        │   ├───dimension
        │   └───Axes
        │       └───<b>CoordinateSystemAxis</b> : {@linkplain CoordinateSystemAxis}
        │           ├───name
        │           ├───direction
        │           ├───minimumValue
        │           ├───maximumValue
        │           ├───rangeMeaning
        │           └───unit
        ├───<b>Datum</b> : {@linkplain Datum}
        │   ├───name
        │   ├───type
        │   ├───<b>Ellipsoid</b> : {@linkplain Ellipsoid}
        │   │   ├───name
        │   │   ├───axisAbbrev
        │   │   ├───axisUnit
        │   │   ├───semiMajorAxis
        │   │   ├───semiMinorAxis
        │   │   └───inverseFlattening
        │   └───<b>PrimeMeridian</b> : {@linkplain PrimeMeridian}
        │       ├───name
        │       ├───greenwichLongitude
        │       └───angularUnit
        └───<b>Conversion</b> : {@linkplain Conversion}
            ├───name
            ├───method
            └───Parameters : {@linkplain ParameterValueGroup}
                └───ParameterValue : {@linkplain ParameterValue}
                    ├───name
                    └───value</pre>
</tr></table></blockquote>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see SpatialMetadata
 *
 * @since 3.04 (derived from 2.4)
 * @module
 */
public class SpatialMetadataFormat extends IIOMetadataFormatImpl {
    /**
     * The metadata format name, which is {@value}. The {@link javax.imageio.metadata} package
     * description requires that we provide a version number as part of the format name. The
     * version number provided in this constant is set to the last Geotk version when this
     * format has been modified, and may change in any future version.
     *
     * @since 3.20 (derived from 2.4)
     */
    public static final String GEOTK_FORMAT_NAME = "geotk-coverageio_3.07";

    /**
     * The ISO-19115 format name, which is {@value}. This metadata format is big and supported
     * only by a few plugins like {@link org.geotoolkit.image.io.plugin.NetcdfImageReader}.
     * For applications that don't need to full verbosity of ISO 19115, consider using the
     * {@linkplain #getStreamInstance(String) stream metadata instance} identified by the
     * {@value #GEOTK_FORMAT_NAME} name instead.
     *
     * {@note The 3.0 version number is the GeoAPI version that define the format used here.}
     *
     * @since 3.20
     */
    public static final String ISO_FORMAT_NAME = "ISO-19115_3.0";

    /**
     * The policy for the names of the nodes to be inferred from the ISO objects.
     * We use JavaBeans names instead of UML identifiers in order to get the plural
     * form for collections.
     */
    static final KeyNamePolicy NAME_POLICY = KeyNamePolicy.JAVABEANS_PROPERTY;

    /**
     * Holder for the default instance for <cite>ISO 19115</cite> metadata format.
     * Applies the <cite>Initialization-on-demand holder</cite> idiom, because the
     * ISO metadata format is very large and only occasionally used.
     */
    private static final class ISO {
        private ISO() {}

        /** The ISO-19115 instance for <cite>stream</cite> metadata format. */
        static final SpatialMetadataFormat INSTANCE;
        static {
            final SpatialMetadataFormatBuilder builder = new SpatialMetadataFormatBuilder(ISO_FORMAT_NAME);
            builder.addTreeForISO19115(null);
            INSTANCE = builder.build();
        }
    }

    /**
     * Holder for the default instances of Geotk metadata format.
     * Applies the <cite>Initialization-on-demand holder</cite> idiom,
     * because the builder class is relatively large.
     */
    private static final class Geotk {
        private Geotk() {}

        /** The default instance for <cite>stream</cite> metadata format. */
        static final SpatialMetadataFormat STREAM;
        static {
            SpatialMetadataFormatBuilder builder = new SpatialMetadataFormatBuilder(GEOTK_FORMAT_NAME);
            builder.addTreeForStream(null);
            STREAM = builder.build();
        }

        /** The default instance for <cite>image</cite> metadata format. */
        static final SpatialMetadataFormat IMAGE;
        static {
            final SpatialMetadataFormatBuilder builder = new SpatialMetadataFormatBuilder(GEOTK_FORMAT_NAME);
            builder.addTreeForImage(null);
            builder.addTreeForCRS("RectifiedGridDomain");
            IMAGE = builder.build();
        }
    }

    /**
     * Returns the <cite>stream</cite> metadata format for the given name. This is the metadata
     * format that apply to a file as a whole, which may contain more than one image. The
     * tree structure is documented in the <a href="#default-formats">class javadoc</a>.
     *
     * @param  name The {@value #GEOTK_FORMAT_NAME} or {@value #ISO_FORMAT_NAME} constant.
     * @return The stream metadata format for the given name.
     * @throws IllegalArgumentException If the given name is not one of the supported constants.
     *
     * @since 3.20
     */
    public static SpatialMetadataFormat getStreamInstance(final String name) {
        if (name.equalsIgnoreCase(GEOTK_FORMAT_NAME)) return Geotk.STREAM;
        if (name.equalsIgnoreCase(ISO_FORMAT_NAME))   return ISO.INSTANCE;
        throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "name", name));
    }

    /**
     * Returns the <cite>image</cite> metadata format for the given name.
     * This is the metadata format that apply to a particular image in a file.
     * The tree structure is documented in the <a href="#default-formats">class javadoc</a>.
     *
     * @param  name The {@value #GEOTK_FORMAT_NAME} constant.
     * @return The image metadata format for the given name.
     * @throws IllegalArgumentException If the given name is not one of the supported constants.
     *
     * @since 3.20
     */
    public static SpatialMetadataFormat getImageInstance(final String name) {
        if (name.equalsIgnoreCase(GEOTK_FORMAT_NAME)) return Geotk.IMAGE;
        // More formats may be added later (e.g. GML in JPEG2000).
        throw new IllegalArgumentException(Errors.format(Errors.Keys.IllegalArgument_2, "name", name));
    }

    /**
     * The metadata standards represented by each node. The most common standard is
     * {@link MetadataStandard#ISO_19115 ISO_19115}. This information is used for
     * {@linkplain #getDescription(String, String, Locale)} implementation.
     */
    private final Map<String,MetadataStandard> standards = new HashMap<>();

    /**
     * The mapping from method names to attribute or child element names for a given element.
     */
    private final Map<String, Map<String,String>> namesMapping = new HashMap<>();

    /**
     * The last value returned by {@link #getDescription(String, String, Locale)}, cached on
     * the assumption that the description of different attributes of the same element are
     * likely to be asked a few consecutive time.
     */
    private transient volatile MetadataDescriptions descriptions;

    /**
     * Creates an initially empty format. Subclasses shall invoke the various
     * {@code addFoo(...)} methods defined in this class or parent class for
     * adding new elements and attributes.
     *
     * @param rootName the name of the root element.
     */
    protected SpatialMetadataFormat(final String rootName) {
        super(rootName, CHILD_POLICY_SOME);
    }

    /**
     * Adds a new element type to this metadata document format with a
     * child policy of {@link #CHILD_POLICY_REPEAT CHILD_POLICY_REPEAT}.
     * <p>
     * This method is defined mostly in order to give access to protected methods
     * from {@link SpatialMetadataFormatBuilder}.
     *
     * @param <T>           The compile-time type of the {@code type} argument.
     * @param standard      The standard from which the new element is derived, or {@code null}.
     * @param type          The legal class of the object value, or {@code null}.
     * @param elementName   The name of the new element.
     * @param parentName    The name of the element that will be the parent of the new element.
     * @param childPolicy   One of the {@code CHILD_POLICY_*} constants indicating the child policy
     *                      of the new element.
     * @param minOccurrence The minimum number of children of the node.
     * @param maxOccurrence The maximum number of children of the node.
     */
    @SuppressWarnings("fallthrough")
    final <T> void addElement(final MetadataStandard standard, final Class<T> type,
            final String elementName, final String parentName, int childPolicy,
            final int minOccurrence, final int maxOccurrence)
    {
        switch (maxOccurrence) {
            case 0:  childPolicy = CHILD_POLICY_EMPTY; // Fallthrough
            case 1:  addElement(elementName, parentName, childPolicy); break;
            default: addElement(elementName, parentName, minOccurrence, maxOccurrence); break;
        }
        if (standard != null) {
            standards.put(elementName, standard);
        }
        if (type != null) {
            addObjectValue(elementName, type);
            addCustomAttributes(elementName, type);
        }
    }

    /**
     * Adds a reference to an existing child element. This method is defined here only in
     * order to give access to the protected method from {@link SpatialMetadataFormatBuilder}.
     * This method is defined mostly in order to give access to a protected method from
     * {@link SpatialMetadataFormatBuilder}.
     */
    final void addExistingElement(final String elementName, final String parentName) {
        addChildElement(elementName, parentName);
    }

    /**
     * Allows an {@code Object} reference of a given class type to be stored in nodes implementing
     * the named element. This method delegates to one of the {@link #addObjectValue(String, Class,
     * boolean, Object) addObjectValue} methods defined in the super-class. The current
     * implementation is as below:
     *
     * {@preformat java
     *     addObjectValue(elementName, classType, mandatory, getDefaultValue(classType));
     * }
     *
     * @param <T> The compile-time type of {@code classType}.
     * @param elementName The name of the element for which to add an object value.
     * @param classType The legal class type of the object value.
     */
    final <T> void addObjectValue(final String elementName, final Class<T> classType) {
        addObjectValue(elementName, classType, false, getDefaultValue(classType));
    }

    /**
     * Adds a wrapper for a list of attributes. This is usually not needed, since attributes
     * can be declared with the {@link #VALUE_LIST VALUE_LIST} type. However in the case of
     * two-dimensional arrays (or lists of lists), the second dimension needs to be represented
     * by a wrapper element. The main use case if the list of offset vectors in a
     * {@linkplain RectifiedGrid rectified grid domain}, which can be represented as below:
     *
     * {@preformat text
     *     RectifiedGridDomain  : RectifiedGrid
     *     └───OffsetVectors    : List<double[]>
     *         └───OffsetVector : double[]
     *             └───values
     * }
     *
     * In the above example, the name of the {@code RectifiedGridDomain}, {@code OffsetVectors}
     * and {@code OffsetVector} nodes shall be specified by the {@code parentName},
     * {@code elementName} and {@code componentName} arguments respectively. The creation of
     * the {@code values} attribute is caller responsibility.
     *
     * @see #addElement(String, String, int, int)
     * @see #addElement(String, String, int)
     * @see #addObjectValue(String, Class, int, int)
     */
    final void addListWrapper(final MetadataStandard standard,
            final String parentName, final String elementName, final String componentName,
            final Class<?> componentType, final int minOccurrence, final int maxOccurrence)
    {
        addElement(elementName, parentName, minOccurrence, maxOccurrence);
        standards.put(elementName, standard);

        // The repeated element with no child, only a single attribute.
        addElement(componentName, elementName, CHILD_POLICY_EMPTY);
        addObjectValue(componentName, componentType, 0, Integer.MAX_VALUE);
        standards.put(componentName, standard);
    }

    /**
     * Adds an attribute for a code list or an enumeration. The attribute has no
     * default value and its type is {@link #DATATYPE_STRING DATATYPE_STRING}.
     * <p>
     * This method is defined mostly in order to give access to protected methods
     * from {@link SpatialMetadataFormatBuilder}.
     *
     * @param elementName   The name of the node where to add the attribute.
     * @param attributeName The name of the attribute to add in the given element.
     * @param mandatory     {@code true} if the attribute is mandatory, or {@code false} for optional.
     * @param codes         The enumeration of valid attribute values.
     *
     * @see #addAttribute(String, String, int, boolean, String, List)
     */
    final void addEnumeration(final String elementName, final String attributeName,
            final boolean mandatory, final String... codes)
    {
        addAttribute(elementName, attributeName, DATATYPE_STRING, mandatory, null, Arrays.asList(codes));
    }

    /**
     * Adds a new attribute of the given type. This method delegates to one of the {@code addAttribute}
     * methods defined in the super-class. The choice of method and parameters is performed according
     * the following rules (non-exhaustive list):
     * <p>
     * <ul>
     *   <li>The attribute is declared mandatory if {@code minOccurrence} is different than zero.</li>
     *   <li>The value type is set to {@link #VALUE_RANGE} if the {@code range} parameter is non-null.</li>
     *   <li>The value type is set to {@link #VALUE_LIST} if the {@code maxOccurrence} parameter is different than 1.</li>
     *   <li>The value type is set to {@link #VALUE_ARBITRARY} otherwise (except for boolean values).</li>
     * <p>
     * This method is defined mostly in order to give access to protected methods
     * from {@link SpatialMetadataFormatBuilder}.
     *
     * @param elementName   The name of the node where to add the attribute.
     * @param attributeName The name of the attribute to add in the given element.
     * @param dataType      The type of the attribute to add.
     * @param minOccurrence the smallest legal number of list items (typically 0 or 1).
     * @param maxOccurrence the largest legal number of list items (typically 1 or more).
     * @param range         The range of valid values, or {@code null} if none.
     *
     * @see #addBooleanAttribute(String, String, boolean, boolean)
     * @see #addAttribute(String, String, int, boolean, int, int)
     * @see #addAttribute(String, String, int, boolean, String, List)
     * @see #addAttribute(String, String, int, boolean, String, String, String, boolean, boolean)
     */
    final void addAttribute(final String elementName, final String attributeName,
            int dataType, final int minOccurrence, final int maxOccurrence, final NumberRange<?> range)
    {
        if (dataType == IIOMetadataFormat.DATATYPE_BOOLEAN) {
            /*
             * Boolean  ⇒  Attribute VALUE_ENUMERATION
             *
             * A default value (false) is provided only if the attribute is
             * not mandatory (minOccurrence == 0), otherwise we will require
             * the user to specify a value explicitly.
             */
            addBooleanAttribute(elementName, attributeName, minOccurrence == 0, false);
        } else if (range != null) {
            /*
             * Number  ⇒  Attribute VALUE_RANGE[_?_INCLUSIVE]
             */
            addAttribute(elementName, attributeName, dataType, minOccurrence != 0, null,
                    toString(range.getMinValue()), toString(range.getMaxValue()),
                    range.isMinIncluded(), range.isMaxIncluded());
        } else if (maxOccurrence == 1) {
            /*
             * Object  ⇒  Attribute VALUE_ARBITRARY
             */
            addAttribute(elementName, attributeName, dataType, minOccurrence != 0, null);
        } else {
            /*
             * Object  ⇒  Attribute VALUE_LIST
             */
            addAttribute(elementName, attributeName, dataType, minOccurrence != 0, minOccurrence, maxOccurrence);
        }
    }

    /**
     * Invoked when a metadata element is about to be added to the tree. This is a hook for adding
     * custom attributes which may not be in the UML or excluded. The main attributes of interest
     * are {@code "name"} and {@code "type"} for referencing objects.
     */
    final void addCustomAttributes(final String elementName, final Class<?> type) {
        if (IdentifiedObject.class.isAssignableFrom(type)) {
            addAttribute(elementName, "name", DATATYPE_STRING, true, null);
        }
        if (CoordinateSystemAxis.class.isAssignableFrom(type)) {
            addAttribute(elementName, "axisAbbrev", DATATYPE_STRING, true, null);
        }
        final List<String> types;
        if (CoordinateReferenceSystem.class.isAssignableFrom(type)) {
            types = DataTypes.CRS_TYPES;
        } else if (CoordinateSystem.class.isAssignableFrom(type)) {
            types = DataTypes.CS_TYPES;
        } else if (Datum.class.isAssignableFrom(type)) {
            types = DataTypes.DATUM_TYPES;
        } else {
            return;
        }
        addAttribute(elementName, "type", DATATYPE_STRING, true, null, types);
    }

    /**
     * Remembers the name of child element or attribute for the given method.
     * This information is required by {@link MetadataProxy}.
     *
     * @param  parentName The name of the parent element.
     * @param  The name of the method which is mapped to an element/attribute in the parent element.
     * @param  elementName The name of the element/attribute which represents the method value.
     * @throws IllegalArgumentException If the parent element doesn't exist, or if the given method
     *         is already defined for the given parent.
     */
    final void mapName(final String parentName, final String methodName, final String elementName)
            throws IllegalArgumentException
    {
        // Actually 'methodName' is the only parameter which has not been verified
        // by the caller, but we nevertheless verify all parameters as a safety.
        ensureNonNull("parentName",  parentName);
        ensureNonNull("methodName",  methodName);
        ensureNonNull("elementName", elementName);
        Map<String, String> map = namesMapping.get(parentName);
        if (map == null) {
            map = new HashMap<>();
            namesMapping.put(parentName, map);
        }
        final String old = map.put(methodName, elementName);
        if (old != null && !old.equals(elementName)) {
            map.put(methodName, old); // Preserve the previous value.
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.DuplicatedValuesForKey_1, methodName));
        }
    }

    /**
     * Makes the first character an upper-case letter. This is used for element names,
     * which typically starts with an upper-case letter in Image I/O metadata.
     *
     * @param  elementName The element name, or {@code null}.
     * @return The given name with the first character converted to an upper-case letter,
     *         or {@code null} if the given argument was null.
     *
     * @since 3.06
     */
    static String toElementName(String elementName) {
        if (elementName != null && !(elementName = elementName.trim()).isEmpty()) {
            final char c = elementName.charAt(0);
            final char u = Character.toUpperCase(c);
            if (c != u) {
                final StringBuilder buffer = new StringBuilder(elementName);
                buffer.setCharAt(0, u);
                elementName = buffer.toString();
            }
        }
        return elementName;
    }

    /**
     * Returns a string representation of the given value, or
     * {@code null} if that value is null (unbounded range).
     */
    private static String toString(final Comparable<?> value) {
        return (value != null) ? value.toString() : null;
    }

    /**
     * Removes an attribute from a previously defined element. If no attribute with the given
     * name was present in the given element, nothing happens and no exception is thrown.
     *
     * @param elementName The name of the element.
     * @param attributeName The name of the attribute being removed.
     */
    @Override
    protected void removeAttribute(final String elementName, final String attributeName) {
        // This method is overriden only in order to allow SpatialMetadataFormatBuilder to access it.
        super.removeAttribute(elementName, attributeName);
    }

    /**
     * Removes an element from the format. If no element with the given
     * name was present, nothing happens and no exception is thrown.
     *
     * @param elementName the name of the element to be removed.
     */
    @Override
    protected void removeElement(final String elementName) {
        super.removeElement(elementName);
        standards   .remove(elementName);
        namesMapping.remove(elementName);
    }

    /**
     * Returns {@code true} if the element (and the subtree below it) is allowed to appear
     * in a metadata document for an image of the given type. The default implementation
     * always returns {@code true}.
     */
    @Override
    public boolean canNodeAppear(final String elementName, final ImageTypeSpecifier imageType) {
        return true;
    }

    /**
     * Returns the element which is the parent of the named element, or {@code null} if none.
     * For example if this metadata format is the {@linkplain #getStreamInstance(String) stream}
     * instance, then:
     * <p>
     * <ul>
     *   <li>The path to {@code "GeographicElement"} is {@code "DiscoveryMetadata/Extent/GeographicElement"}.</li>
     *   <li>The parent of {@code "GeographicElement"} returned by this method is {@code "Extent"}.</li>
     * </ul>
     *
     * {@note An element may have more than one parent, since the same element can be copied under
     *        many nodes using <code>addChildElement(...)</code>. In such case, this method returns
     *        only the first path. Such cases do not occur with the Geotk formats identified by
     *        <code>GEOTK_FORMAT_NAME</code> in this class, but occur with the more complex ISO-19115
     *        format.}
     *
     * @param  elementName The element for which the parent is desired.
     * @return The parent of the given element, or {@code null}.
     *
     * @see #getElementPath(String)
     *
     * @since 3.06
     */
    public String getElementParent(final String elementName) {
        ensureNonNull("elementName", elementName);
        return getElementParent(getRootName(), elementName, null);
    }

    /**
     * Returns the element which is the parent of the named element, or {@code null} if none.
     * <p>
     * <b>Note:</b> Current implementation is somewhat inefficient.  We could maintain a map of
     * parents when new elements are added, but {@link IIOMetadataFormatImpl} already maintains
     * such map - I'm not sure why they do no provide API for getting that info. This API could
     * have been implemented as:
     *
     * {@preformat java
     *     public String[] getElementParents(String elementName) {
     *         List<String> parents = getElement(elementName).parentList;
     *         return parents.toString(new String[parents.size()]);
     *     }
     * }
     *
     * @param  root The root element from which to starts the scan.
     * @param  elementName The element for which the parent is desired.
     * @param  path If non-null, a buffer where to append the path before the node.
     * @return The parent of the given element, or {@code null}.
     */
    private String getElementParent(final String root, final String elementName, final StringBuilder path) {
        final String[] childs = getChildNames(root);
        if (childs != null) {
            for (final String child : childs) {
                if (child.equals(elementName)) {
                    return root;
                }
            }
            // Do recursive call only after we checked every childs at the root. If a name
            // appears twice (it should not), we will favor the one at the lowest depth.
            for (final String child : childs) {
                final String candidate = getElementParent(child, elementName, path);
                if (candidate != null) {
                    if (path != null) {
                        path.insert(0, '/').insert(0, child);
                    }
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Returns the path to the named element, or {@code null} if none. For example if this
     * metadata format is the {@linkplain #getStreamInstance stream} instance, then the path to the
     * {@code "GeographicElement"} is {@code "DiscoveryMetadata/Extent/GeographicElement"}.
     *
     * {@note An element may have more than one path, since the same element can be copied under
     *        many nodes using <code>addChildElement(...)</code>. In such case, this method returns
     *        only the first path. Such cases do not occur with the Geotk formats identified by
     *        <code>GEOTK_FORMAT_NAME</code> in this class, but occur with the more complex ISO-19115
     *        format.}
     *
     * @param  elementName The element for which the path is desired.
     * @return The path to the given element, or {@code null}.
     *
     * @see #getElementParent(String)
     *
     * @since 3.06
     */
    public String getElementPath(final String elementName) {
        ensureNonNull("elementName", elementName);
        final StringBuilder path = new StringBuilder(64);
        final String parent = getElementParent(getRootName(), elementName, path);
        if (parent != null) {
            // The parent is already in the path at this point.
            return path.append(elementName).toString();
        }
        return null;
    }

    /**
     * Returns the metadata standard implemented by the element of the given name.
     * If the given element does not implement a standard (which may happen if the
     * element has not been added by {@link SpatialMetadataFormatBuilder} method),
     * then this method returns {@code null}.
     *
     * @param  elementName The element for which the standard is desired.
     * @return The standard implemented by the given element, or {@code null}.
     *
     * @since 3.06
     */
    public MetadataStandard getElementStandard(final String elementName) {
        return standards.get(elementName);
    }

    /**
     * Returns the mapping from method names to element/attribute names, or {@code null} if this
     * mapping is unknown. Keys are method names, and values are the attribute name as determined
     * by {@link SpatialMetadataFormat#NAME_POLICY}.
     * <p>
     * This method returns a direct reference to the internal map.
     * <strong>Do not modify the map content!</strong>
     */
    final Map<String, String> getElementNames(final String elementName) {
        return namesMapping.get(elementName);
    }

    /**
     * Returns a description of the named element, or {@code null}. The description will be
     * localized for the supplied locale if possible.
     * <p>
     * The default implementation first queries the
     * {@linkplain MetadataStandard#asDescriptionMap description map} associated with the
     * {@linkplain #getElementStandard metadata standard}. If no description is found, then the
     * {@linkplain IIOMetadataFormatImpl#getElementDescription super-class implementation}
     * is used.
     *
     * @param  elementName The name of the element.
     * @param  locale The Locale for which localization will be attempted, or null.
     * @return The attribute description.
     *
     * @since 3.05
     */
    @Override
    public String getElementDescription(final String elementName, final Locale locale) {
        ensureNonNull("elementName", elementName);
        String description = getDescription(elementName, null, locale);
        if (description == null) {
            description = super.getElementDescription(elementName, locale);
        }
        return description;
    }

    /**
     * Returns a description of the named attribute, or {@code null}. The description will be
     * localized for the supplied locale if possible.
     * <p>
     * The default implementation first queries the
     * {@linkplain MetadataStandard#asDescriptionMap description map} associated with the
     * {@linkplain #getElementStandard metadata standard}. If no description is found, then the
     * {@linkplain IIOMetadataFormatImpl#getAttributeDescription super-class implementation}
     * is used.
     *
     * @param  elementName The name of the element.
     * @param  attrName    The name of the attribute.
     * @param  locale      The Locale for which localization will be attempted, or null.
     * @return The attribute description.
     *
     * @since 3.05
     */
    @Override
    public String getAttributeDescription(final String elementName, final String attrName, final Locale locale) {
        ensureNonNull("elementName", elementName);
        ensureNonNull("attrName",    attrName);
        String description = getDescription(elementName, attrName, locale);
        if (description == null) {
            description = super.getAttributeDescription(elementName, attrName, locale);
        }
        return description;
    }

    /**
     * Returns the description of the given attribute of the given element, in the given locale.
     * If the attribute is null, then this method assumes that the caller want the description
     * of the element itself. If there is no description available, returns {@code null}.
     *
     * @param  elementName The name of the element in which to search for attributes.
     * @param  attrName The name of the attribute for which the descriptions is desired, or {@code null}.
     * @param  locale The locale of the descriptions, or {@code null} for the default.
     * @return The requested description, or {@code null} if none.
     */
    private String getDescription(String elementName, String attrName, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault(Locale.Category.DISPLAY);
        }
        if (attrName == null) {
            attrName = elementName;
            elementName = getElementParent(elementName);
            if (elementName == null) {
                return null;
            }
        }
        MetadataDescriptions candidate = descriptions;
        if (candidate == null || !locale.equals(candidate.locale) || !elementName.equals(candidate.elementName)) {
            Class<?> type = null;
            try {
                type = getObjectClass(elementName);
            } catch (IllegalArgumentException e) {
                // The given element does not allow the storage of objects.
                // We will set the description map to an empty map.
            }
            Map<String, ExtendedElementInformation> desc = Collections.emptyMap();
            if (type != null) {
                final MetadataStandard standard = getElementStandard(elementName);
                if (standard != null) try {
                    desc = standard.asInformationMap(type, NAME_POLICY);
                } catch (ClassCastException e) {
                    // The element type is not an instance of the expected standard.
                    // We will set the description map to an empty map.
                }
            }
            candidate = new MetadataDescriptions(desc, elementName, locale);
            descriptions = candidate;
        }
        final ExtendedElementInformation info = candidate.descriptions.get(attrName);
        if (info != null) {
            final InternationalString definition = info.getDefinition();
            if (definition != null) {
                return definition.toString(locale);
            }
        }
        return null;
    }

    /**
     * Returns the default value for an object reference of the given type. This method is
     * invoked automatically by {@link SpatialMetadataFormatBuilder} for determining the value
     * of the {@code defaultValue} argument in the call to the {@link #addObjectValue(String,
     * Class, boolean, Object) addObjectValue} method.
     * <p>
     * This method is also invoked by {@link ReferencingBuilder#getDefault(Class)}, which does not
     * rely on {@link IIOMetadataFormat#getObjectDefaultValue(String)} because the default value of
     * some referencing objects depends on the type of the enclosing element. For example the default
     * coordinate system shall be ellipsoidal for a geographic CRS and Cartesian for a projected
     * CRS.
     * <p>
     * The default implementation returns a value determined from the table below.
     * Subclasses can override this method for providing different default values.
     * <p>
     * <table border="1" cellspacing="0">
     * <tr bgcolor="lightblue">
     *   <th>Type</th>
     *   <th>Default value</th>
     * </tr><tr>
     *   <td>&nbsp;{@link PrimeMeridian}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultPrimeMeridian#GREENWICH}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link Ellipsoid}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoid#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeodeticDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeodeticDatum#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link VerticalDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultVerticalDatum#GEOIDAL}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EngineeringDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEngineeringDatum#UNKNOWN}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EllipsoidalCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoidalCS#GEODETIC_2D}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link CartesianCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultCartesianCS#GENERIC_2D}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeographicCRS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeographicCRS#WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeocentricCRS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeocentricCRS#CARTESIAN}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;All other type&nbsp;</td>
     *   <td>&nbsp;{@code null}&nbsp;</td>
     * </tr>
     * </table>
     *
     * @param  <T> The compile-time type of {@code classType}.
     * @param  type The class type of the object for which to get a default value.
     * @return The default value for an object of the given type, or {@code null} if none.
     *
     * @see ReferencingBuilder#getDefault(Class)
     * @see #getObjectDefaultValue(String)
     *
     * @since 3.08
     */
    public <T> T getDefaultValue(final Class<T> type) {
        final IdentifiedObject object;
        if (PrimeMeridian.class.isAssignableFrom(type)) {
            object = CommonCRS.WGS84.primeMeridian();
        } else if (Ellipsoid.class.isAssignableFrom(type)) {
            object = CommonCRS.WGS84.ellipsoid();
        } else if (GeodeticDatum.class.isAssignableFrom(type)) {
            object = CommonCRS.WGS84.datum();
        } else if (VerticalDatum.class.isAssignableFrom(type)) {
            object = CommonCRS.Vertical.MEAN_SEA_LEVEL.datum();
        } else if (EngineeringDatum.class.isAssignableFrom(type)) {
            object = PredefinedCRS.CARTESIAN_2D.getDatum(); // Unknown datum.
        } else if (EllipsoidalCS.class.isAssignableFrom(type)) {
            object = PredefinedCS.GEODETIC_2D;
        } else if (CartesianCS.class.isAssignableFrom(type)) {
            object = PredefinedCS.CARTESIAN_2D;
        } else if (GeographicCRS.class.isAssignableFrom(type)) {
            object = CommonCRS.WGS84.normalizedGeographic();
        } else if (GeocentricCRS.class.isAssignableFrom(type)) {
            object = PredefinedCRS.GEOCENTRIC;
        } else {
            object = null;
        }
        return type.cast(object);
    }

    /**
     * Returns a <cite>tree table</cite> representation of this metadata standard.
     * This convenience method delegates the work to {@link MetadataTreeTable}.
     *
     * @param  locale The locale for which localization will be attempted, or {@code null}.
     * @return A tree representation of this metadata standard.
     */
    public TreeTableNode toTreeTable(final Locale locale) {
        final MetadataTreeTable tree = new MetadataTreeTable(this);
        if (locale != null) {
            tree.setLocale(locale);
        }
        return tree.getRootNode();
    }

    /**
     * Returns a string representation of this format.
     * The default implementation formats this object as a tree.
     */
    @Override
    public String toString() {
        return Trees.toString(toTreeTable(null));
    }
}
