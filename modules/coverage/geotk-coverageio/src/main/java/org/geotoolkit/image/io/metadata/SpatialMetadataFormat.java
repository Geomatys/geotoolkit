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
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.content.Band;
import org.opengis.metadata.content.RangeDimension;
import org.opengis.metadata.content.ImageDescription;

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
 * {@section Default format}
 * The default {@link #INSTANCE} is inferred from the {@link ImageDescription} metadata,
 * which is defined in ISO 19115-2. Its tree structure is as below:
 *
 * {@preformat text
geotk-coverageio
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
            └───rangeElements
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
public class SpatialMetadataFormat extends IIOMetadataFormatImpl {
    /**
     * The metadata format name, which is {@value}.
     */
    public static final String FORMAT_NAME = "geotk-coverageio";

    /**
     * The policy for the names of the nodes to be inferred from the ISO objects.
     * We use JavaBeans names instead of UML identifiers in order to get the plural
     * form for collections.
     */
    private static KeyNamePolicy NAME_POLICY = KeyNamePolicy.JAVABEANS_PROPERTY;

    /**
     * The default spatial metadata format instance.
     */
    public static final SpatialMetadataFormat INSTANCE = new SpatialMetadataFormat(FORMAT_NAME);

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
     */
    protected SpatialMetadataFormat(final String rootName) {
        this(MetadataStandard.ISO_19115, rootName);
        final Map<Class<?>,Class<?>> substitution = new HashMap<Class<?>,Class<?>>(4);
        substitution.put(Citation.class, null);
        substitution.put(RangeDimension.class, Band.class);
        addTree(ImageDescription.class, substitution);
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
     * class in the set of values. This is useful mostly for replacing a base class by some
     * specialized subclass, for example {@link RangeDimension} by {@link Band}. This substitution
     * applies only to childs (if any), not to the type given directly to this method.
     * <p>
     * Values in the substitution map can also be null, which has the effect to exclude the
     * type in the key. This is used typically for excluding {@code Citation.class}, because
     * introducing this metadata type brings a large tree of dependencies.
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
         */
        if (maxOccurence != 1) {
            addElement(elementName, parentName, minOccurence, maxOccurence);
            parentName = elementName;
            if (identifier != null && !identifier.equals(elementName)) {
                elementName = identifier;
            } else {
                // This is used only as a fallback.
                elementName = identifier + " element";
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
         * Adds the child elements. The loop below invokes this method recursively.
         */
        addElement(elementName, parentName, childPolicy);
        addObjectValue(elementName, type, false, null);
        for (final Map.Entry<String,Class<?>> entry : propertyTypes.entrySet()) {
            final String childName = entry.getKey();
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
                /*
                 * Found a collection. Set the maximum number of occurence,
                 * then get the type of elements in that collection.
                 */
                max = Integer.MAX_VALUE;
                childType = elementTypes.get(childName);
            }
            if (substitution != null) {
                final Class<?> sub = substitution.get(childType);
                if (sub != null) {
                    childType = sub;
                }
            }
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
    private static int typeOf(final Class<?> type) {
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
