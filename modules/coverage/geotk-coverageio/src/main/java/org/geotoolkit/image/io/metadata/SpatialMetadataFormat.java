/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.opengis.util.CodeList;
import org.opengis.annotation.Obligation;

// We use a lot of different metadata interfaces in this class.
// It is a bit too tedious to declare all of them.
import org.opengis.metadata.*;
import org.opengis.metadata.extent.*;
import org.opengis.metadata.spatial.*;
import org.opengis.metadata.quality.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.constraint.*;
import org.opengis.metadata.acquisition.*;
import org.opengis.metadata.maintenance.*;
import org.opengis.metadata.distribution.*;
import org.opengis.metadata.identification.*;
import org.opengis.metadata.identification.Identification; // Override the package class.
import org.opengis.metadata.content.Band; // Override the package class.

import org.geotoolkit.internal.CodeLists;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.metadata.KeyNamePolicy;
import org.geotoolkit.metadata.NullValuePolicy;
import org.geotoolkit.metadata.TypeValuePolicy;
import org.geotoolkit.metadata.ValueRestriction;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.gui.swing.tree.TreeTableNode;
import org.geotoolkit.gui.swing.tree.Trees;


/**
 * Describes the structure of {@linkplain SpatialMetadata spatial metadata}.
 * This class infers the tree structure from metadata objects defined by some
 * {@linkplain MetadataStandard metadata standard}, typically ISO 19115-2. New
 * metadata elements are declared by calls to the {@link #addTree addTree} method.
 *
 * {@section String formatting in attributes}
 * The following formatting rules apply:
 * <p>
 * <ul>
 *   <li>Numbers are formatted as in the {@linkplain Locale#US US locale}, i.e.
 *       as {@link Integer#toString(int)} or {@link Double#toString(double)}.</li>
 *   <li>Dates are formatted with the {@code "yyyy-MM-dd HH:mm:ss"}
 *       {@linkplain java.text.SimpleDateFormat pattern} in UTC
 *       {@linkplain java.util.TimeZone timezone}.</li>
 * </ul>
 *
 * {@section Default formats}
 * The default {@link #STREAM} and {@link #IMAGE} formats are inferred from a subset of the
 * {@link MetaData} and {@link ImageDescription} interfaces, respectively. Consequently those
 * instances can be considered as profiles of ISO 19115-2. The tree structures are as below:
 *
<blockquote><table border="1" cellpadding="12">
<tr bgcolor="lightblue"><th>Stream metadata</th><th>Image metadata</th></tr>
<tr><td nowrap valign="top" width="50%">
<pre>geotk-coverageio_3.05
└───MetaData
    ├───dateStamp
    ├───hierarchyLevels
    ├───hierarchyLevelNames
    ├───parentIdentifier
    ├───dataSetUri
    ├───fileIdentifier
    ├───metadataStandardName
    ├───metadataStandardVersion
    ├───referenceSystemInfo
    ├───identificationInfo
    │   ├───abstract
    │   ├───topicCategories
    │   ├───credits
    │   ├───environmentDescription
    │   ├───purpose
    │   ├───status
    │   ├───supplementalInformation
    │   ├───extent
    │   │   ├───description
    │   │   ├───geographicElement
    │   │   │   ├───eastBoundLongitude
    │   │   │   ├───northBoundLatitude
    │   │   │   ├───southBoundLatitude
    │   │   │   ├───westBoundLongitude
    │   │   │   └───inclusion
    │   │   └───verticalElement
    │   │       ├───maximumValue
    │   │       ├───minimumValue
    │   │       └───verticalCRS
    │   ├───descriptiveKeywords
    │   │   └───descriptiveKeywords element
    │   │       ├───keywords
    │   │       └───type
    │   └───spatialResolution
    │       ├───distance
    │       └───equivalentScale
    │           ├───doubleValue
    │           └───denominator
    └───spatialRepresentationInfo
        ├───cellGeometry
        ├───numberOfDimensions
        ├───transformationParameterAvailable
        └───axisDimensionProperties
            └───axisDimensionProperties element
                ├───dimensionName
                ├───dimensionSize
                └───resolution</pre>
</td><td nowrap valign="top" width="50%">
<pre>geotk-coverageio_3.05
└───ImageDescription
    ├───attributeDescription
    ├───contentType
    ├───illuminationElevationAngle
    ├───illuminationAzimuthAngle
    ├───imagingCondition
    ├───imageQualityCode
    │   └───code
    ├───cloudCoverPercentage
    ├───processingLevelCode
    │   └───code
    ├───compressionGenerationQuantity
    ├───triangulationIndicator
    ├───radiometricCalibrationDataAvailable
    ├───cameraCalibrationInformationAvailable
    ├───filmDistortionInformationAvailable
    ├───lensDistortionInformationAvailable
    ├───dimensions
    │   └───dimension
    │       ├───descriptor
    │       ├───sequenceIdentifier
    │       ├───maxValue
    │       ├───minValue
    │       ├───units
    │       ├───peakResponse
    │       ├───bitsPerValue
    │       ├───toneGradation
    │       ├───scaleFactor
    │       ├───offset
    │       ├───bandBoundaryDefinition
    │       ├───nominalSpatialResolution
    │       ├───transferFunctionType
    │       ├───transmittedPolarization
    │       └───detectedPolarization
    └───rangeElementDescriptions
        └───rangeElementDescription
            ├───name
            ├───definition
            └───rangeElements</pre>
</tr></table></blockquote>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 * @module
 */
public class SpatialMetadataFormat extends IIOMetadataFormatImpl {
    /**
     * The metadata format name, which is {@value}. The {@link javax.imageio.metadata} package
     * description requires that we provide a version number as part of the format name. The
     * version number provided in this constant is set to the last Geotk version when this
     * format has been modified, and may change in any future version.
     */
    public static final String FORMAT_NAME = "geotk-coverageio_3.05";

    /**
     * The policy for the names of the nodes to be inferred from the ISO objects.
     * We use JavaBeans names instead of UML identifiers in order to get the plural
     * form for collections.
     */
    private static KeyNamePolicy NAME_POLICY = KeyNamePolicy.JAVABEANS_PROPERTY;

    /**
     * The default instance for <cite>stream</cite> metadata format. This is the
     * metadata format that apply to file as a whole, which may contain more than
     * one image.
     *
     * @see #addTreeForStream()
     *
     * @since 3.05
     */
    public static final SpatialMetadataFormat STREAM = new SpatialMetadataFormat(MetadataStandard.ISO_19115, FORMAT_NAME);
    static {
        STREAM.addTreeForStream();
    }

    /**
     * The default instance for <cite>image</cite> metadata format. This
     * is the metadata format that apply to a particular image in a file.
     *
     * @see #addTreeForImage()
     *
     * @since 3.05
     */
    public static final SpatialMetadataFormat IMAGE = new SpatialMetadataFormat(MetadataStandard.ISO_19115, FORMAT_NAME);
    static {
        IMAGE.addTreeForImage();
    }

    /**
     * The default spatial metadata format instance.
     *
     * @deprecated Renamed as {@link #IMAGE}.
     */
    @Deprecated
    public static final SpatialMetadataFormat INSTANCE = IMAGE;

    /**
     * The metadata standard represented by this format.
     * This is usually {@link MetadataStandard#ISO_19115 ISO_19115}.
     */
    protected final MetadataStandard standard;

    /**
     * Creates a new format for the {@linkplain MetadataStandard#ISO_19115 ISO 19115}
     * metadata standard, and declare the default structure. If the default structure
     * (illustrated in the class javadoc) is not wanted, then callers should use the
     * {@link #SpatialMetadataFormat(MetadataStandard, String)} constructor instead.
     *
     * @param rootName the name of the root element.
     *
     * @deprecated Invoke {@link #addTreeForStream} or {@link #addTreeForImage}
     *             after construction instead.
     */
    @Deprecated
    protected SpatialMetadataFormat(final String rootName) {
        this(MetadataStandard.ISO_19115, rootName);
        addTreeForImage();
    }

    /**
     * Creates an initially empty format for the given standard. Subclasses shall invoke the
     * various {@code addFoo(...)} methods defined in this class or parent class for adding
     * new elements and attributes.
     *
     * @param standard The metadata standard represented by this format.
     *        This is usually {@link MetadataStandard#ISO_19115 ISO_19115}.
     * @param rootName the name of the root element.
     */
    protected SpatialMetadataFormat(final MetadataStandard standard, final String rootName) {
        super(rootName, CHILD_POLICY_SOME);
        this.standard = standard;
    }

    /**
     * Adds an {@link MetaData} node at the root of the tree.
     * This is used for <cite>stream</cite> metadata.
     *
     * @see #STREAM
     *
     * @since 3.05
     */
    protected void addTreeForStream() {
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(20);
        /*
         * Metadata excluded because they are redundant with standard API.
         *
         * - The Locale is specified in ImageReader.
         * - The CharacterSet is fixed to Unicode (at least for String objects) by Java.
         * - The Format is redundant with ImageReaderWriterSpi.
         * - The BrowseGraphic is redundant with Image I/O Thumbnails
         */
        substitution.put(Format.class,        null);
        substitution.put(Locale.class,        null);
        substitution.put(CharacterSet.class,  null);
        substitution.put(BrowseGraphic.class, null);
        /*
         * Metadata excluded because we are not interrested in (at this time):
         *
         * - The ContentInformation is provided in image metadata.
         * - SpatialRepresentationType is fixed to "grid" for Image I/O, so is useless.
         * - The Citation is repeated in too many places with large dependencies tree.
         * - The ResponsibleParty is repeated in too many places.
         * - The DataQuality branch is very large and we have not yet determined what to keep.
         */
        substitution.put(Usage.class,                        null);
        substitution.put(Citation.class,                     null);
        substitution.put(ResponsibleParty.class,             null);
        substitution.put(Distribution.class,                 null);
        substitution.put(Constraints.class,                  null);
        substitution.put(MaintenanceInformation.class,       null);
        substitution.put(PortrayalCatalogueReference.class,  null);
        substitution.put(ApplicationSchemaInformation.class, null);
        substitution.put(MetadataExtensionInformation.class, null);
        substitution.put(SpatialRepresentationType.class,    null);
        substitution.put(ContentInformation.class,           null);
        substitution.put(AggregateInformation.class,         null);
        substitution.put(AcquisitionInformation.class,       null);
        substitution.put(RepresentativeFraction.class,       null);
        substitution.put(DataQuality.class,                  null);
        /*
         * Metadata excluded because not yet implemented.
         */
        substitution.put(TemporalExtent.class, null);
        /*
         * Collections replaced by singletons, because only one
         * instance is enough for the purpose of stream metadata.
         */
        substitution.put(Resolution[].class,            Resolution.class);
        substitution.put(Extent[].class,                Extent.class);
        substitution.put(VerticalExtent[].class,        VerticalExtent.class);
        substitution.put(GeographicExtent[].class,      GeographicExtent.class);
        substitution.put(SpatialRepresentation[].class, SpatialRepresentation.class);
        substitution.put(Identification[].class,        Identification.class);
        /*
         * Since this set of metadata is about gridded data,
         * replace the generic interfaces by specialized ones.
         */
        substitution.put(Identification.class,        DataIdentification.class);
        substitution.put(SpatialRepresentation.class, GridSpatialRepresentation.class);
        substitution.put(GeographicExtent.class,      GeographicBoundingBox.class);
        substitution.put(Result.class,                CoverageResult.class);
        /*
         * Build the tree.
         */
        addTree(MetaData.class, substitution);
    }

    /**
     * Adds an {@link ImageDescription} node at the root of the tree.
     * This is used for <cite>image</cite> metadata.
     *
     * @see #IMAGE
     *
     * @since 3.05
     */
    protected void addTreeForImage() {
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(4);
        substitution.put(Citation.class, null);
        substitution.put(RangeDimension.class, Band.class);
        addTree(ImageDescription.class, substitution);
    }

    /**
     * Adds a new element or attribute of the given type as a child of the root. This method
     * performs the same work than {@link #addTree(Class, String, String, Map)}, except that
     * the element is added at the root and the name is inferred from the given type for
     * convenience.
     *
     * @param type The type of the element or attribute to be added.
     * @param substitution The map of children types to substitute by other types, or {@code null}.
     */
    protected void addTree(final Class<?> type, final Map<Class<?>,Class<?>> substitution) {
        addTree(type, type.getSimpleName(), getRootName(), substitution);
    }

    /**
     * Adds a new element or attribute of the given type and name as a child of the given node. If
     * the given type is a metadata, then that child is {@linkplain #addElement(String,String,int)
     * added as an element} and all its children are added recursively. Otherwise the type is
     * {@linkplain #addAttribute(String,String,int,boolean,String) added as an attribute}.
     * <p>
     * This method can be given an optional <cite>substitution map</cite>. If this map is non
     * null, then every occurence of a class in the set of keys is replaced by the associated
     * class in the set of values. The purpose of this map is to:
     *
     * <ul>
     *   <li><p>Replace a base class by some specialized subclass. Since {@code IIOMetadata} are
     *   about grided data (not generic {@code Feature}s), the exact subtype is often known and
     *   we want the additional attributes to be declared inconditionnaly. Example:</p>
     *
     * <blockquote><pre>substitution.put({@linkplain RangeDimension}.class, {@linkplain Band}.class);</pre></blockquote></li>
     *
     *   <li><p>Exclude a particular class. This is conceptually equivalent to setting the target
     *   type to {@code null}. This is used typically for excluding {@code Citation.class}, because
     *   introducing this metadata type brings a large tree of dependencies. Example:</p>
     *
     * <blockquote><pre>substitution.put({@linkplain Citation}.class, null);</pre></blockquote></li>
     *
     *   <li><p>Replace a collection by a singleton. This is conceptually equivalent to setting the
     *   source type to an array, and the target type to the element of that array. This is useful
     *   when a collection seems an overkill for the specific case of stream or image metadata.
     *   Example:</p>
     *
     * <blockquote><pre>substitution.put({@linkplain Identification}[].class, {@linkplain Identification}.class);</pre></blockquote></li>
     * </ul>
     *
     * The substitution map applies only to childs (if any), not to the type given directly to this
     * method.
     *
     * @param type          The type of the element or attribute to be added.
     * @param elementName   The name of the element or attribute node to be added.
     * @param parentName    The name of the parent node to where to add the child.
     * @param substitution  The map of children types to substitute by other types, or {@code null}.
     */
    protected void addTree(final Class<?> type, final String elementName, final String parentName,
            final Map<Class<?>,Class<?>> substitution)
    {
        final Set<Class<?>> exclude = new HashSet<Class<?>>();
        if (substitution != null) {
            for (final Map.Entry<Class<?>,Class<?>> entry : substitution.entrySet()) {
                if (entry.getValue() == null) {
                    exclude.add(entry.getKey());
                }
            }
        }
        addTree(type, null, elementName, parentName, 0, 1, null, exclude, substitution);
    }

    /**
     * Implementation of {@link #addTree(Class,String,String, Map)} with a set of attribute type
     * to exclude. This method invokes itself recursively. The given set will be modified in order
     * to avoid infinite recursivity (e.g. {@code Identifier.getAuthority().getIdentifiers()}).
     *
     * @param type         The type of the element or attribute to be added.
     * @param identifier   The UML identifier, or {@code null} if unknown.
     * @param elementName  The name of the element or attribute node to be added.
     * @param parentName   The name of the parent node to where to add the child.
     * @param minOccurence Minimal occurence of the element or attribute in the parent node.
     * @param maxOccurence Maximal occurence of the element or attribute in the parent node.
     * @param restriction  The restriction on the valid values, or {@code null} if none.
     * @param exclude      The attribute types to exclude. This set will be modified.
     * @param substitution The classes to substitute by other classes.
     *        This user-supplied map applies only on childs and is not modified.
     */
    private void addTree(final Class<?> type, final String identifier, String elementName,
            String parentName, final int minOccurence, final int maxOccurence,
            final ValueRestriction restriction, final Set<Class<?>> exclude,
            final Map<Class<?>,Class<?>> substitution)
    {
        if (maxOccurence == 0) return;
        final boolean mandatory = (minOccurence != 0);
         /*
         * CodeList    ⇒    Attribute VALUE_ENUMERATION
         *
         * The enums are the code list elements. There is no default value.
         */
        if (CodeList.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            final Class<CodeList<?>> codeType = (Class<CodeList<?>>) type;
            final List<String> codes = Arrays.asList(CodeLists.identifiers(codeType));
            addAttribute(parentName, elementName, DATATYPE_STRING, mandatory, null, codes);
            return;
        }
        /*
         * JSE type    ⇒    Attribute VALUE_ARBITRARY | VALUE_LIST | VALUE_ENUMERATION
         *
         * If the element is not an other object from the same metadata standard, handles it as
         * an attribute. Everything which can not be handled by one of the DATATYPE_* constants
         * is handled as a String.
         */
        if (!standard.isMetadata(type)) {
            final int dataType = typeOf(type);
            if (maxOccurence != 1) {
                /*
                 * Collection  ⇒  Attribute VALUE_LIST
                 */
                addAttribute(parentName, elementName, dataType, mandatory, minOccurence, maxOccurence);
            } else if (dataType == IIOMetadataFormat.DATATYPE_BOOLEAN) {
                /*
                 * Boolean  ⇒  Attribute VALUE_ENUMERATION
                 *
                 * A default value (false) is provided only if the attribute is
                 * not mandatory, otherwise we will require the user to specify
                 * a value explicitly.
                 */
                addBooleanAttribute(parentName, elementName, !mandatory, false);
            } else if (restriction != null && restriction.range != null) {
                /*
                 * Number  ⇒  Attribute VALUE_RANGE[_?_INCLUSIVE]
                 */
                final NumberRange<?> range = restriction.range;
                addAttribute(parentName, elementName, dataType, mandatory, null,
                        toString(range.getMinValue()), toString(range.getMaxValue()),
                        range.isMinIncluded(), range.isMaxIncluded());
            } else {
                /*
                 * Object  ⇒  Attribute VALUE_ARBITRARY
                 */
                addAttribute(parentName, elementName, dataType, mandatory, null);
            }
            return;
        }
        /*
         * Collection of Metadata    ⇒    Element CHILD_POLICY_EMPTY
         *
         * The 'elementName' is inferred from the method name and is typically in plural
         * form (at least in GeoAPI interfaces).  We add a node for 'elementName', which
         * can contain many occurences of the actual metadata structure. The new node is
         * set as the parent of the actual metadata structure. The name of that metadata
         * structure is set to the UML identifier, which is typically the same name than
         * 'elementName' except that it is in singular form.
         */
        if (maxOccurence != 1) {
            addElement(elementName, parentName, minOccurence, maxOccurence);
            parentName = elementName;
            if (identifier != null && !identifier.equals(elementName)) {
                elementName = identifier;
            } else {
                // This is used only as a fallback.
                elementName = identifier + " entry";
            }
        }
        /*
         * Metadata singleton    ⇒    Element CHILD_POLICY_SOME|ALL|CHOICE|EMPTY
         *
         * If every childs have the same obligation,
         * then we will apply the following mapping:
         *
         *   MANDATORY   ⇒ CHILD_POLICY_ALL
         *   CONDITIONAL ⇒ CHILD_POLICY_CHOICE  (this is assuming that XOR is the condition)
         *   FORBIDDEN   ⇒ CHILD_POLICY_EMPTY
         *
         * Otherwise the policy is CHILD_POLICY_SOME.
         */
        boolean hasChilds = false;
        Obligation obligation = Obligation.FORBIDDEN; // If there is no child.
        final Map<String,String> identifiers;
        final Map<String,ValueRestriction> restrictions;
        final Map<String,Class<?>> propertyTypes, elementTypes;
        identifiers   = standard.asNameMap       (type, KeyNamePolicy.  UML_IDENTIFIER, NAME_POLICY);
        propertyTypes = standard.asTypeMap       (type, TypeValuePolicy.PROPERTY_TYPE,  NAME_POLICY);
        elementTypes  = standard.asTypeMap       (type, TypeValuePolicy.ELEMENT_TYPE,   NAME_POLICY);
        restrictions  = standard.asRestrictionMap(type, NullValuePolicy.NON_NULL,       NAME_POLICY);
        for (final Map.Entry<String,Class<?>> entry : elementTypes.entrySet()) {
            final Class<?> candidate = entry.getValue();
            if (standard.isMetadata(candidate) && !CodeList.class.isAssignableFrom(candidate)) {
                final ValueRestriction vr = restrictions.get(entry.getKey());
                if (vr != null) {
                    final Obligation c = vr.obligation;
                    if (c != null) {
                        if (!hasChilds) {
                            hasChilds = true;
                            obligation = c;
                            continue;
                        }
                        if (c.equals(obligation)) {
                            continue;
                        }
                    }
                }
                // Found an obligation which is unknown or different than the previous ones.
                obligation = null;
                hasChilds = true;
                break;
            }
        }
        if (obligation == null) {
            // The obligation is not the same for every child.
            obligation = Obligation.OPTIONAL;
        }
        final int childPolicy;
        switch (obligation) {
            case MANDATORY:   childPolicy = CHILD_POLICY_ALL;    break;
            case CONDITIONAL: childPolicy = CHILD_POLICY_CHOICE; break;
            case FORBIDDEN:   childPolicy = CHILD_POLICY_EMPTY;  break;
            default:          childPolicy = CHILD_POLICY_SOME;   break;
        }
        /*
         * At this point we have determined the child policy to apply to the new node.
         * Now add the child elements. The loop below invokes this method recursively
         * for each attribute of the metadata object that we are adding.
         */
        addElement(elementName, parentName, childPolicy);
        addObjectValue(elementName, type, false, null);
        for (final Map.Entry<String,Class<?>> entry : propertyTypes.entrySet()) {
            String childName = entry.getKey();
            final ValueRestriction vr = restrictions.get(childName);
            int min = 0, max = 1;
            if (vr != null && vr.obligation != null) {
                switch (vr.obligation) {
                    case MANDATORY: min = 1; break;
                    case FORBIDDEN: max = 0; break;
                }
            }
            Class<?> childType = entry.getValue();
            if (Collection.class.isAssignableFrom(childType)) {
                // Replace the collection type by the type of elements in that collection.
                childType = elementTypes.get(childName);
                max = Integer.MAX_VALUE;
            }
            /*
             * If the caller specified a substitution map, then we perform two checks:
             *
             * 1) If we have a collection (max > 1), then check if the caller wants to
             *    replace the collection (identified by an array type) by a singleton.
             *
             * 2) Then check if we want to replace the element type by an other element
             *    type. No array type is allowed at this stage.
             */
            if (substitution != null) {
                Class<?> replacement = null;
                if (max > 1) { // Collection case.
                    replacement = substitution.get(Classes.changeArrayDimension(childType, 1));
                    if (replacement != null) {
                        childType = replacement;    // Typically, the replacement type is the same.
                        childName = identifiers.get(childName); // Replace plural by singular form.
                        max = 1;
                    }
                }
                replacement = substitution.get(childType);
                if (replacement != null) {
                    childType = replacement;
                }
            }
            /*
             * We now have all the properties for the child that we want to add. Invoke this method
             * recursively for proceding to the addition, with guard against infinite recursivity.
             */
            if (exclude.add(childType)) {
                addTree(childType, identifiers.get(childName), childName, elementName,
                        min, max, vr, exclude, substitution);
                if (!exclude.remove(childType)) {
                    throw new AssertionError(childType);
                }
            }
        }
    }

    /**
     * Returns one of the {@code DATATYPE_*} constant for the given class. If no constant
     * matches, then returns {@code DATATYPE_STRING} on the assumption that all attributes
     * have a sensible {@link Object#toString()} implementation.
     *
     * @param  type The class for which the {@code DATATYPE_*} constant is desired.
     * @return The {@code DATATYPE_*} constant for the given class.
     */
    private static int typeOf(Class<?> type) {
        type = Classes.primitiveToWrapper(type);
        if (Number.class.isAssignableFrom(type)) {
            if (Classes.isInteger(type)) {
                return IIOMetadataFormat.DATATYPE_INTEGER;
            }
            if (Float.class.isAssignableFrom(type)) {
                return IIOMetadataFormat.DATATYPE_FLOAT;
            }
            return IIOMetadataFormat.DATATYPE_DOUBLE;
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return IIOMetadataFormat.DATATYPE_BOOLEAN;
        }
        return IIOMetadataFormat.DATATYPE_STRING;
    }

    /**
     * Returns a string representation of the given value, or
     * {@code null} if that value is null (unbounded range).
     */
    private static String toString(final Comparable<?> value) {
        return (value != null) ? value.toString() : null;
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
