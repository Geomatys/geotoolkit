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

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import javax.imageio.metadata.IIOMetadataFormatImpl;

import org.opengis.util.CodeList;
import org.opengis.util.RecordType;

// We use a lot of different metadata interfaces in this class.
// It is a bit too tedious to declare all of them.
import org.opengis.metadata.*;
import org.opengis.metadata.extent.*;
import org.opengis.metadata.spatial.*;
import org.opengis.metadata.quality.*;
import org.opengis.metadata.lineage.*;
import org.opengis.metadata.content.*;
import org.opengis.metadata.citation.*;
import org.opengis.metadata.constraint.*;
import org.opengis.metadata.acquisition.*;
import org.opengis.metadata.maintenance.*;
import org.opengis.metadata.distribution.*;
import org.opengis.metadata.identification.*;

import org.opengis.parameter.*;
import org.opengis.referencing.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.annotation.Obligation;

import org.opengis.coverage.grid.GridCell;
import org.opengis.coverage.grid.GridPoint;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.RectifiedGrid;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Point;
import org.opengis.util.InternationalString;
import org.opengis.util.GenericName;

import org.geotoolkit.lang.Builder;
import org.apache.sis.util.Classes;
import org.apache.sis.util.Numbers;
import org.apache.sis.metadata.KeyNamePolicy;
import org.apache.sis.metadata.ValueExistencePolicy;
import org.apache.sis.metadata.TypeValuePolicy;
import org.geotoolkit.metadata.ValueRestriction;
import org.geotoolkit.metadata.MetadataStandard;
import org.geotoolkit.metadata.UnmodifiableMetadataException;
import org.geotoolkit.resources.Errors;

import static javax.imageio.metadata.IIOMetadataFormat.*;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.toElementName;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.NAME_POLICY;
import static org.geotoolkit.internal.image.io.GridDomainAccessor.ARRAY_ATTRIBUTE_NAME;
import org.apache.sis.util.iso.Types;


/**
 * Creates new {@linkplain SpatialMetadataFormat spatial metadata format} instances. This class
 * infers the tree structure from metadata objects defined by some {@linkplain MetadataStandard
 * metadata standard}, typically ISO 19115-2. New metadata elements are declared by calls to the
 * generic {@link #addTree addTree} method, or one of its specialized variants like
 * {@link #addTreeForImage(String)}.
 * <p>
 * This builder is used for creating the default
 * <a href="SpatialMetadataFormat.html#default-formats">trees documented here</a>.
 * Users can leverage this builder for creating trees based on interfaces from other
 * standards.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.05)
 * @module
 */
public class SpatialMetadataFormatBuilder extends Builder<SpatialMetadataFormat> {
    /**
     * The instance being built.
     */
    private final SpatialMetadataFormat metadata;

    /**
     * Sets to {@code true} after {@link #build()} has been invoked,
     * in order to ensure that we do not modify a published metadata.
     */
    private boolean done;

    /**
     * The substitution map, or {@code null} if none. The content is provided by the
     * user and id not modified by the {@code addTree} methods (but this is modified
     * by the more specialized {@code addTreeForXXX} methods). See the public javadoc
     * in {@link #substitutions()} for an explanation of this map content.
     *
     * @see #substitutions()
     */
    private Map<Class<?>,Class<?>> substitutions;

    /**
     * The attribute types to exclude. On first {@link #addTreeRecursively} invocation, this
     * is the set of {@link #substitutions} keys having a null value. On recursive invocations,
     * this set is modified in order to avoid infinite recursivity
     * (e.g. {@code Identifier.getAuthority().getIdentifiers()}).
     * <p>
     * New elements in this set are added before entering the {@code addTreeRecursively} method,
     * and removed after exiting.
     */
    private final Set<Class<?>> excludes = new HashSet<>();

    /**
     * Set of types that we plan to complete manually later. Special value:
     * <p>
     * <ul>
     *   <li>{@code null} for declaring all types as incomplete.</li>
     *   <li>{@link Collections#emptySet()} for declaring all types as complete.</li>
     * </ul>
     * <p>
     * When computing the node child policy ({@code CHILD_POLICY_CHOICE}, <i>etc.</i>) from
     * the child restrictions, the children types enumerated in the {@link #excludes} set will
     * be ignored only if the given {@code type} is not incomplete. Not ignoring an "excluded"
     * type may lead to relaxed child policy. This is needed when a "excluded" type is actually
     * going to be added manually by the caller after the {@code addTree} method call.
     */
    private Set<Class<?>> incompletes = Collections.emptySet();

    /**
     * The name of nodes added in previous iterations. The existing nodes will be added to the new
     * parent using the {@link #addChildElement(String, String)} instead than creating the tree
     * again. This {@link #addTreeRecursively} method will add new elements in this map, but never
     * remove existing elements. The class value is used for checking purpose only.
     */
    private final Map<String,Class<?>> existings = new HashMap<>();

    /**
     * Creates a builder for a metadata format of the given name.
     * The metadata format will be an instance of {@link SpatialMetadataFormat}.
     *
     * @param rootName the name of the root element.
     */
    public SpatialMetadataFormatBuilder(final String rootName) {
        metadata = new SpatialMetadataFormat(rootName);
    }

    /**
     * Creates a builder for a metadata format of the given type. The given implementation
     * class must have an accessible no-argument constructor.
     *
     * {@note We do not provide a constructor accepting directly an instance, because it would
     * provide a way to alter an already published <code>SpatialMetadataFormat</code> instance.}
     *
     * @param  type The type of the metadata format to instantiate.
     * @throws IllegalAccessException If the class or its no-argument constructor is not accessible.
     * @throws InstantiationException If the instantiation fails.
     */
    public SpatialMetadataFormatBuilder(final Class<? extends SpatialMetadataFormat> type)
            throws InstantiationException, IllegalAccessException
    {
        metadata = type.newInstance();
    }

    /**
     * The map of children types to substitute by other types. This map is initially empty.
     * Users can add or remove entries in this map before to invoke any {@code addTree} method.
     * <p>
     * If this map is non empty, then every occurrence of a class in the set of keys is replaced
     * by the associated class in the collection of values. The purpose of this map is to:
     *
     * <ul>
     *   <li><p>Replace a base class by some specialized subclass. Since {@code IIOMetadata} is
     *   about grided data (not generic {@code Feature}s), the exact subtype is often known at
     *   compile time, and we want the additional attributes to be declared unconditionally.
     *   Example:</p>
     *
     * <blockquote><pre>substitutions().put({@linkplain RangeDimension}.class, {@linkplain Band}.class);</pre></blockquote></li>
     *
     *   <li><p>Exclude a particular class by setting the replacement to {@code null}. This is used
     *   for excluding large tree of metadata which may not be applicable. Example:</p>
     *
     * <blockquote><pre>substitutions().put({@linkplain Objective}.class, null);</pre></blockquote></li>
     *
     *   <li><p>Replace an element class (including the whole tree behind it) by a single attribute.
     *   This simplification is especially useful for {@code Citation} because they typically appear
     *   in many different places with the same name ("<cite>citation</cite>"), while Image I/O does
     *   not allow many elements to have the same name (actually this is not strictly forbidden, but
     *   the getter methods return information only about the first occurrence of a given name).
     *   Converting an element to an attribute allow it to appear with the same name under different
     *   nodes, and can make the tree considerably simpler (at the cost of losing all the sub-tree
     *   below the converted element). Example:</p>
     *
     * <blockquote><pre>substitutions().put({@linkplain Citation}.class, String.class);</pre></blockquote></li>
     *
     *   <li><p>Replace a collection by a singleton, by setting the source type to an array and the
     *   target type to the element of that array. This is useful when a collection seems an overkill
     *   for the specific case of stream or image metadata. Example:</p>
     *
     * <blockquote><pre>substitutions().put({@linkplain Identification}[].class, {@linkplain Identification}.class);</pre></blockquote></li>
     * </ul>
     *
     * @return The substitution map (never {@code null}).
     */
    public Map<Class<?>,Class<?>> substitutions() {
        if (substitutions == null) {
            substitutions = new HashMap<>();
        }
        return substitutions;
    }

    /**
     * Verifies that the metadata instance has not yet been published.
     */
    private void ensureModifiable() {
        if (done) {
            throw new UnmodifiableMetadataException(Errors.format(
                    Errors.Keys.UNMODIFIABLE_OBJECT_$1, metadata.getClass()));
        }
    }

    /**
     * Adds a new optional element or attribute of the given type as a child of the root. This method
     * performs the same work than {@link #addTree(MetadataStandard, Class, String, String, boolean)},
     * except that the element is added at the root and the name is inferred from the given type
     * for convenience.
     *
     * @param standard The metadata standard of the element or attribute to be added.
     * @param type     The type of the element or attribute to be added.
     */
    public void addTree(final MetadataStandard standard, final Class<?> type) {
        ensureNonNull("standard", standard);
        ensureNonNull("type",     type);
        addTree(standard, type, type.getSimpleName(), metadata.getRootName(), false);
    }

    /**
     * Adds a new element or attribute of the given type and name as a child of the given node.
     * <p>
     * <ul>
     *   <li>If the given type is a metadata, then it is
     *       {@linkplain IIOMetadataFormatImpl#addElement(String,String,int) added as an element}
     *       and all its children are added recursively.</li>
     *   <li>Otherwise the type is
     *      {@linkplain IIOMetadataFormatImpl#addAttribute(String,String,int,boolean,String) added as an attribute}.</li>
     * </ul>
     *
     * {@section Element type}
     * This method expects a {@code type} argument, which can be a {@link CodeList} subclass,
     * one of the interfaces member of the given metadata {@code standard}, or a simple JSE
     * type (boolean, number of {@link String}). Do <strong>not</strong> specify collection
     * types, since the type of collection elements can not be inferred easily. To specify
     * a multi-occurrence, use the array type instead (e.g. {@code CoordinateSystemAxis[].class}).
     *
     * {@section Substitution map}
     * The {@linkplain #substitutions() substitution map} applies only to children (if any),
     * not to the type given directly to this method.
     *
     * @param standard      The metadata standard of the element or attribute to be added.
     * @param type          The type of the element or attribute to be added (see javadoc).
     * @param elementName   The name of the element or attribute node to be added.
     * @param parentName    The name of the parent node to where to add the child.
     * @param mandatory     {@code true} if the element should be mandatory, or {@code false}
     *                      if optional.
     */
    public void addTree(final MetadataStandard standard, Class<?> type,
            final String elementName, final String parentName, final boolean mandatory)
    {
        ensureNonNull("standard",    standard);
        ensureNonNull("type",        type);
        ensureNonNull("elementName", elementName);
        ensureNonNull("parentName",  parentName);
        ensureModifiable();
        excludes.clear();
        if (substitutions != null) {
            for (final Map.Entry<Class<?>,Class<?>> entry : substitutions.entrySet()) {
                if (entry.getValue() == null) {
                    excludes.add(entry.getKey());
                }
            }
        }
        /*
         * If the given type is an arrray, handle as a multi-occurrence (i.e. we will add
         * a "Elements" parent node, and declare in that parent a single "Element" child
         * which can be repeated many time).
         */
        int max = 1;
        String identifier = null;
        if (type.isArray()) {
            type = type.getComponentType();
            max = Integer.MAX_VALUE;
            try {
                identifier = standard.getInterface(type).getSimpleName();
            } catch (ClassCastException e) {
                // Not an implementation of the expected standard.
                // It may be an "ordinary" object from the JDK.
                identifier = elementName;
            }
        }
        existings.clear();
        addTreeRecursively(standard, type, identifier, elementName, parentName, mandatory ? 1 : 0, max, null);
        incompletes = Collections.emptySet(); // Not a public API for now.
    }

    /**
     * Implementation of {@link #addTree}. This method invokes itself recursively.
     *
     * @param standard
     *          The metadata standard of the element or attribute to be added. This standard will
     *          be constant for the whole tree added by this method call (including children).
     * @param type
     *          The type of the element or attribute to be added. May be a {@link CodeList}, an
     *          interface of the given {@code standard}, or a simple JSE object like a boolean,
     *          a number or a {@link String}. This type shall <strong>not</strong> be an array
     *          or a collection. To add a multi-occurrence, specify the <em>element</em> type
     *          with a {@code maxOccurrence} parameter greater than 1.
     * @param identifier
     *          On first invocation, the simple Java name of the standard interface implemented
     *          by the given {@code type} (this is usually the UML identifier without the OGC/ISO
     *          two-letters prefix). On recursive invocations, the UML identifier of the Java method
     *          for the element to be added. If non-null, this argument is usually the same than
     *          {@code elementName} but in singular form. If {@code null}, a singular name will be
     *          derived from the {@code elementName} string if needed.
     * @param elementName
     *          The name of the element or attribute node to be added. This is the name of the
     *          parent node, except that the first letter may be changed to upper-case. If the
     *          node accepts multi-occurrence ({@code maxOccurrence > 1}), a compound child node
     *          will be added with the {@code identifier} name and {@code CHILD_POLICY_REPEAT}.
     * @param parentName
     *          The name of the parent node where to add the child. This is the name given in
     *          calls to {@link IIOMetadataFormatImpl} methods for specifying where to add the node.
     * @param minOccurrence
     *          Minimal occurrence of the element or attribute in the parent node. If 0, the
     *          element is considered optional. If different than zero, it is considered
     *          mandatory.
     * @param maxOccurrence
     *          Maximal occurrence of the element or attribute in the parent node. If greater
     *          than 1, the node will be added with {@link #CHILD_POLICY_REPEAT}.
     * @param restriction
     *          The restriction on the valid values, or {@code null} if none. This is used for
     *          determining the minimal and maximal values of attributes, and the child policy.
     * @return
     *      The {@code elementName}, or a modified version of it if that method
     *      modified the case, or {@code null} if it has not been added.
     */
    private String addTreeRecursively(
            final MetadataStandard standard,
                  Class<?>         type,
                  String           identifier,    // May replace first letter by upper-case.
                  String           elementName,   // Replaced by component name on multi-occurrence
                  String           parentName,    // Replaced by element name on multi-occurrence
            final int              minOccurrence,
            final int              maxOccurrence,
            final ValueRestriction restriction)
    {
        if (maxOccurrence == 0) {
            return null;
        }
         /*
         * CodeList    ⇒    Attribute VALUE_ENUMERATION
         *
         * The enums are the code list elements. There is no default value.
         */
        if (CodeList.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            final Class<CodeList<?>> codeType = (Class<CodeList<?>>) type;
            metadata.addEnumeration(parentName, elementName, (minOccurrence != 0), getCodeList(codeType));
            return elementName;
        }
        /*
         * JSE type    ⇒    Attribute VALUE_ARBITRARY | VALUE_LIST | VALUE_ENUMERATION
         *
         * If the element is not an other object from the same metadata standard, handles it as
         * an attribute. Everything which can not be handled by one of the DATATYPE_* constants
         * is handled as a String.
         */
        if (!standard.isMetadata(type)) {
            String containerName = elementName;
            if (maxOccurrence != 1) {
                /*
                 * Collection  ⇒  Attribute VALUE_LIST
                 *
                 * In most case, we are adding a list of String or double values. But in a
                 * few cases we add a list of double[] arrays (e.g. "offsetVectors"), in
                 * which cases we need to insert a compound element in the tree.
                 */
                final Class<?> componentType = type.getComponentType();
                if (componentType != null) {
                    // The container for the repeated elements (CHILD_POLICY_REPEAT)
                    containerName = elementName = toElementName(elementName);
                    final String componentName = toComponentName(elementName, identifier, true);
                    metadata.addListWrapper(standard, parentName, elementName, componentName,
                            componentType, minOccurrence, maxOccurrence);

                    // The attribute of kind VALUE_LIST.
                    parentName  = componentName;
                    elementName = ARRAY_ATTRIBUTE_NAME;
                    type        = componentType;
                }
                metadata.addAttribute(parentName, elementName, typeOf(type), minOccurrence, maxOccurrence, null);
            } else {
                /*
                 * Boolean  ⇒  Attribute VALUE_ENUMERATION
                 * Number   ⇒  Attribute VALUE_RANGE[_?_INCLUSIVE]
                 * Object   ⇒  Attribute VALUE_ARBITRARY
                 */
                metadata.addAttribute(parentName, elementName, typeOf(type), minOccurrence,
                        maxOccurrence, (restriction != null) ? restriction.range : null);
            }
            return containerName;
        }
        /*
         * Collection of Metadata    ⇒    Element CHILD_POLICY_REPEAT
         *
         * The 'elementName' is inferred from the method name and is typically in plural
         * form (at least in GeoAPI interfaces).  We add a node for 'elementName', which
         * can contain many occurrences of the actual metadata structure. The new node is
         * set as the parent of the actual metadata structure. The name of that metadata
         * structure is set to the UML identifier, which is typically the same name than
         * 'elementName' except that it is in singular form.
         */
        elementName = toElementName(elementName);
        final String containerName = elementName;
        if (maxOccurrence != 1) {
            final Class<?> existingType = existings.get(elementName);
            if (existingType != null) {
                if (!existingType.equals(type)) {
                    throw new IllegalArgumentException(elementName); // TODO: better error message
                }
                metadata.addExistingElement(elementName, parentName);
                return null;
            }
            metadata.addElement(standard, null, elementName, parentName, CHILD_POLICY_REPEAT, minOccurrence, maxOccurrence);
            existings.put(elementName, type);
            parentName  = elementName;
            identifier  = toElementName(identifier);
            elementName = toComponentName(elementName, identifier, false);
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
        final Map<String,String> methods, identifiers;
        final Map<String,ValueRestriction> restrictions;
        final Map<String,Class<?>> propertyTypes, elementTypes;
        methods       = standard.asNameMap       (type, KeyNamePolicy.  METHOD_NAME,    NAME_POLICY);
        identifiers   = standard.asNameMap       (type, KeyNamePolicy.  UML_IDENTIFIER, NAME_POLICY);
        propertyTypes = standard.asTypeMap       (type, TypeValuePolicy.PROPERTY_TYPE,  NAME_POLICY);
        elementTypes  = standard.asTypeMap       (type, TypeValuePolicy.ELEMENT_TYPE,   NAME_POLICY);
        restrictions  = standard.asRestrictionMap(type, ValueExistencePolicy.NON_NULL,  NAME_POLICY);
        final boolean isComplete = (incompletes != null) && !incompletes.contains(type);
        for (final Map.Entry<String,Class<?>> entry : elementTypes.entrySet()) {
            final Class<?> candidate = entry.getValue();
            if (isComplete && excludes.contains(candidate)) {
                // If the caller does not plan to complete manually the tree after this method,
                // then the excluded types should not be considered when determining the child
                // policy. Note that a null 'incomplete' map means that every types are incomplete.
                continue;
            }
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
                        if (c == obligation) {
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
        final Class<?> existingType = existings.get(elementName);
        if (existingType != null) {
            if (!existingType.equals(type)) {
                throw new IllegalArgumentException(elementName); // TODO: better error message
            }
            metadata.addExistingElement(elementName, parentName);
            return null;
        }
        metadata.addElement(standard, type, elementName, parentName, childPolicy, 0, 1);
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
                if (childType == null) {
                    /*
                     * We have been unable to find the element type.
                     * Silently ignore.
                     */
                    continue;
                }
                max = Integer.MAX_VALUE;
            }
            /*
             * If the caller specified a substitution map, then we perform two checks:
             *
             * 1) If we have a collection (max > 1), then check if the caller wants to
             *    replace the collection (identified by an array type) by a singleton.
             *
             * 2) Then check if we want to replace the element type by an other element
             *    type. It could be a new array type.
             */
            if (substitutions != null) {
                Class<?> replacement = null;
                if (max > 1) { // Collection case.
                    replacement = substitutions.get(Classes.changeArrayDimension(childType, 1));
                    if (replacement != null) {
                        childType = replacement;    // Typically, the replacement type is the same.
                        childName = identifiers.get(childName); // Replace plural by singular form.
                        max = 1;
                    }
                }
                replacement = substitutions.get(childType);
                if (replacement != null) {
                    childType = replacement;
                    replacement = childType.getComponentType();
                    if (replacement != null) {
                        max = Integer.MAX_VALUE;
                        childType = replacement;
                    }
                }
            }
            /*
             * We now have all the properties for the child that we want to add. Invoke this method
             * recursively for proceding to the addition, with guard against infinite recursivity.
             */
            if (excludes.add(childType)) {
                childName = addTreeRecursively(standard, childType, identifiers.get(childName),
                        childName, elementName, min, max, vr);
                if (!excludes.remove(childType)) {
                    throw new AssertionError(childType);
                }
                if (childName != null) {
                    metadata.mapName(elementName, methods.get(entry.getKey()), childName);
                }
            }
        }
        return containerName;
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
        type = Numbers.primitiveToWrapper(type);
        if (Number.class.isAssignableFrom(type)) {
            if (Numbers.isInteger(type)) {
                return DATATYPE_INTEGER;
            }
            if (Float.class.isAssignableFrom(type)) {
                return DATATYPE_FLOAT;
            }
            return DATATYPE_DOUBLE;
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return DATATYPE_BOOLEAN;
        }
        return DATATYPE_STRING;
    }

    /**
     * Returns the list of UML identifiers for the given code list type.
     * If a code has no UML identifier, then the programmatic name is used as a fallback.
     *
     * @param  codeType The type of code list.
     * @return The list of UML identifiers or programmatic names for the given
     *         code list, or an empty array if none.
     *
     * @since 3.03
     */
    private static String[] identifiers(final Class<? extends CodeList<?>> codeType) {
        final CodeList<?>[] codes = Types.getCodeValues(codeType);
        final String[] ids = new String[codes.length];
        for (int i=0; i<codes.length; i++) {
            ids[i] = Types.getCodeName(codes[i]);
        }
        return ids;
    }

    /**
     * Returns the code list identifiers, with some changes for code inherited from
     * legacy specifications.
     */
    private static String[] getCodeList(final Class<? extends CodeList<?>> codeType) {
        String[] identifiers = identifiers(codeType);
        if (codeType == AxisDirection.class) {
            for (int i=0; i<identifiers.length; i++) {
                // Replace "CS_AxisOrientationEnum.CS_AO_Other" by something more readable.
                if (identifiers[i].endsWith("Other")) {
                    identifiers[i] = "other";
                }
            }
        }
        return identifiers;
    }

    /**
     * Returns the name of an entry in a collection.
     *
     * @param  elementName The Java-Beans name of the collection. This is usually plural.
     * @param  identifier  The UML identifier of the same element than above.
     *                     This is usually singular. It may be {@code null}
     * @param  attribute   {@code true} if the {@code elementName} is actually for an attribute.
     * @return The name of an entry in the collection.
     */
    private static String toComponentName(final String elementName, final String identifier,
            final boolean attribute)
    {
        if (identifier != null && !identifier.equalsIgnoreCase(elementName)) {
            return identifier;
        }
        if (attribute && elementName.endsWith("s")) {
            /*
             * Try to make singular assuming an English speeling (we are already making the same
             * assumption when adding the "Entry" suffix below). We do that only for attributes,
             * not for elements, because elements may be complex structures in which the plural
             * form is intentional.
             *
             * Examples:
             *  - "DescriptiveKeywords" is an element with "Keywords" (and others) attributes.
             *    We don't want to make it singular, because it can contains many keywords.
             *  - "offsetVectors" is an attribute of type List<double[]>, which is converted
             *    by this class as an "offserVectors" element with "offsetVector" childs.
             */
            return elementName.substring(0, elementName.length() - 1);
        }
        // This is used only as a fallback.
        return (identifier != null ? identifier : elementName) + "Entry";
    }

    /**
     * Adds the tree structure for an ISO-19115 metadata object.
     * <b>Warning:</b> this tree is big and is supported only for a few plugins like
     * {@link org.geotoolkit.image.io.plugin.NetcdfImageReader}.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#ISO_FORMAT_NAME
     *
     * @since 3.20
     */
    protected void addTreeForISO19115(String addToElement) {
        ensureModifiable();
        if (addToElement == null) {
            addToElement = metadata.getRootName();
        }
        final Map<Class<?>,Class<?>> substitutions = substitutions();
        // TODO: need to rename the nodes below.
        substitutions.put(ProcessStepReport.class, null); // "Reports" name clash with quality.Element
        substitutions.put(Source.class, null); // "Sources" name clash with ResponsibleParty.
        addTree(MetadataStandard.ISO_19115, Metadata.class, "Metadata", addToElement, false);
    }

    /**
     * Adds the tree structure for <cite>stream</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Stream metadata</cite>" column of the
     * <code><a href="SpatialMetadataFormat.html#default-formats">SpatialMetadataFormat</a></code>
     * javadoc.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#getStreamInstance(String)
     */
    public void addTreeForStream(String addToElement) {
        ensureModifiable();
        if (addToElement == null) {
            addToElement = metadata.getRootName();
        }
        final Map<Class<?>,Class<?>> substitutions = substitutions();
        /*
         * Metadata excluded because they are redundant with standard API.
         */
        substitutions.put(Format.class,                    null);  // Redundant with ImageReaderWriterSpi.
        substitutions.put(Locale.class,                    null);  // Specified in ImageReader.getLocale().
        substitutions.put(CharacterSet.class,              null);  // Fixed to Unicode in java.lang.String.
        substitutions.put(BrowseGraphic.class,             null);  // Redundant with Image I/O Thumbnails.
        substitutions.put(SpatialRepresentationType.class, null);  // Fixed to "grid" for Image I/O.
        /*
         * Metadata excluded because we are not interested in (at this time). Their
         * inclusion introduce large sub-trees that would need to be simplified.  We
         * may revisit some of those exclusion in a future version, when we get more
         * experience about what are needed.
         */
        substitutions.put(Usage.class,                  null);  // MD_DataIdentification.resourceSpecificUsage
        substitutions.put(ResponsibleParty.class,       null);  // MD_DataIdentification.pointOfContact
        substitutions.put(Constraints.class,            null);  // MD_DataIdentification.resourceConstraints
        substitutions.put(MaintenanceInformation.class, null);  // MD_DataIdentification.resourceMaintenance
        substitutions.put(AggregateInformation.class,   null);  // MD_DataIdentification.aggregationInfo
        substitutions.put(Plan.class,                   null);  // MI_AcquisitionInformation.acquisitionPlan
        substitutions.put(Objective.class,              null);  // MI_AcquisitionInformation.objective
        substitutions.put(Operation.class,              null);  // MI_AcquisitionInformation.operation
        substitutions.put(Requirement.class,            null);  // MI_AcquisitionInformation.acquisitionRequirement
        substitutions.put(Scope.class,                  null);  // DQ_DataQuality.scope
        substitutions.put(Lineage.class,                null);  // DQ_DataQuality.lineage
        substitutions.put(Result.class,                 null);  // DQ_DataQuality.report.result
        /*
         * Metadata excluded because not yet implemented.
         */
        substitutions.put(TemporalExtent.class, null);
        /*
         * Metadata simplification, where elements are replaced by attributes. The simplification
         * is especially important for Citation because they appear in many different places with
         * the same name ("citation"),  while Image I/O does not allow many element nodes to have
         * the same name (this is not strictly forbidden, but the getter methods return information
         * only about the first occurrence of the given name. Note however that having the same name
         * under different element node is not an issue for attributes). In addition, the Citation
         * sub-tree is very large and we don't want to allow the tree to growth that big.
         */
        substitutions.put(Citation.class,   String.class);
        substitutions.put(Citation[].class, String.class);
        substitutions.put(Identifier.class, String.class);
        /*
         * Metadata excluded because they introduce circularity or because
         * they appear more than once (we shall not declare two nodes with
         * the same name in Image I/O). Some will be added by hand later.
         */
        substitutions.put(Instrument.class, null);  // MI_AcquisitionInformation.instrument
        /*
         * Collections replaced by singletons, because only one
         * instance is enough for the purpose of stream metadata.
         */
        substitutions.put(Extent[].class,           Extent.class);            // MD_DataIdentification.extent
        substitutions.put(GeographicExtent[].class, GeographicExtent.class);  // MD_DataIdentification.extent.geographicElement
        substitutions.put(VerticalExtent[].class,   VerticalExtent.class);    // MD_DataIdentification.extent.verticalElement
        substitutions.put(Resolution[].class,       Resolution.class);        // MD_DataIdentification.spatialResolution
        substitutions.put(Platform[].class,         Platform.class);          // MI_AcquisitionInformation.platform
        substitutions.put(Element[].class,          Element.class);           // DQ_DataQuality.report
        substitutions.put(Date[].class,             Date.class);              // DQ_DataQuality.report.dateTime
        /*
         * Since this set of metadata is about gridded data,
         * replace the generic interfaces by specialized ones.
         */
        substitutions.put(Identification.class,        DataIdentification.class);
        substitutions.put(SpatialRepresentation.class, GridSpatialRepresentation.class);
        substitutions.put(GeographicExtent.class,      GeographicBoundingBox.class);
        /*
         * Build the tree.
         */
        final MetadataStandard standard = MetadataStandard.ISO_19115;
        addTree(standard, DataIdentification.class,     "DiscoveryMetadata",   addToElement, false);
        addTree(standard, AcquisitionInformation.class, "AcquisitionMetadata", addToElement, false);
        addTree(standard, DataQuality.class,            "QualityMetadata",     addToElement, false);
        metadata.removeAttribute("EquivalentScale", "doubleValue");
        /*
         * Add by hand a node in the place where it would have been added if we didn't
         * excluded it. We do this addition because Instruments appear in two places,
         * while we want only the occurrence that appear under the "Platform" node.
         */
        substitutions.put(Platform.class, null);
        substitutions.remove(Identifier.class); // Allow full expansion.
        addTree(standard, Instrument[].class, "Instruments", "Platform", false);
        metadata.mapName("Instruments", "getCitations", "citation");
    }

    /**
     * Adds the tree structure for <cite>image</cite> metadata. The default implementation
     * adds the tree structure documented in the "<cite>Image metadata</cite>" column of the
     * <a href="SpatialMetadataFormat.html#default-formats">class javadoc</a>.
     * <p>
     * The <cite>Coordinate Reference System</cite> branch is not included by this method.
     * For including CRS information, the {@link #addTreeForCRS(String)} method shall be
     * invoked explicitly.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     *
     * @see SpatialMetadataFormat#getImageInstance(String)
     */
    public void addTreeForImage(String addToElement) {
        ensureModifiable();
        if (addToElement == null) {
            addToElement = metadata.getRootName();
        }
        final Map<Class<?>,Class<?>> substitutions = substitutions();
        substitutions.put(Citation.class,       String.class);   // MD_ImageDescription.xxxCode
        substitutions.put(RecordType.class,     null);           // MD_CoverageDescription.attributeDescription
        substitutions.put(RangeDimension.class, Band.class);     // MD_CoverageDescription.dimension
        /*
         * Adds the "ImageDescription" node derived from ISO 19115.
         * The 'fillSampleValues' attribute is a Geotk extension.
         */
        MetadataStandard standard = MetadataStandard.ISO_19115;
        addTree(standard, ImageDescription.class, "ImageDescription", addToElement, false);
        metadata.addAttribute("Dimension", "validSampleValues", DATATYPE_STRING, 0, 1, null);
        metadata.addAttribute("Dimension", "fillSampleValues",  DATATYPE_DOUBLE, 0, Integer.MAX_VALUE, null);
        metadata.addObjectValue("Dimension", SampleDimension.class); // Replace Band.class.
        /*
         * Adds the "SpatialRepresentation" node derived from ISO 19115.
         * We omit the information about spatial-temporal axis properties (the Dimension object)
         * because it is redundant with the information provided in the CRS and offset vectors.
         */
        substitutions.put(Dimension.class,           null);  // GridSpatialRepresentation.axisDimensionProperties
        substitutions.put(Point.class,     double[].class);  // MD_Georectified.centerPoint
        substitutions.put(GCP.class,                 null);  // MD_Georectified.checkPoint
        substitutions.put(Boolean.TYPE,              null);  // MD_Georectified.checkPointAvailability
        substitutions.put(InternationalString.class, null);  // MD_Georectified.various descriptions...
        addTree(standard, Georectified.class, "SpatialRepresentation", addToElement, false);
        metadata.removeAttribute("SpatialRepresentation", "cornerPoints");
        /*
         * Adds the "RectifiedGridDomain" node derived from ISO 19123.
         */
        substitutions.put(String.class,          null); // CV_Grid.axisNames
        substitutions.put(GridCell.class,        null); // CV_Grid.cell
        substitutions.put(GridPoint.class,       null); // CV_Grid.intersection
        substitutions.put(GridEnvelope.class,    null); // CV_Grid.extent (will be added later)
        substitutions.put(GridCoordinates.class, int[].class);    // CV_GridEnvelope.low/high
        substitutions.put(DirectPosition.class,  double[].class); // CV_RectifiedGrid.origin
        standard = MetadataStandard.ISO_19123;
        incompletes = null; // Will consider every RectifiedGridDomain nodes as incomplete.
        addTree(standard, RectifiedGrid.class, "RectifiedGridDomain", addToElement, false);
        /*
         * Following is part of ISO 19123 and "GML in JPEG 2000" specifications,
         * but under different names. We use the "GML in JPEG 2000" names.
         */
        addTree(standard, GridEnvelope.class, "Limits", "RectifiedGridDomain", false);
        metadata.removeAttribute("Limits",              "dimension"); // Geotk extension not in ISO 19123.
        metadata.removeAttribute("RectifiedGridDomain", "dimension"); // Redundant with the one in SpatialRepresentation.
        /*
         * There is no public API for this functionality at this time...
         */
        metadata.mapName("RectifiedGridDomain", "getExtent", "Limits");
    }

    /**
     * Adds the tree structure for a <cite>Coordinate Reference System</cite> object.
     *
     * @param addToElement The name of the element where to add the tree,
     *        or {@code null} for adding the tree at the root.
     */
    protected void addTreeForCRS(String addToElement) {
        ensureModifiable();
        if (addToElement == null) {
            addToElement = metadata.getRootName();
        }
        final Map<Class<?>,Class<?>> substitutions = substitutions();
        /*
         * Metadata excluded in order to keep the CRS node relatively simple.
         */
        substitutions.put(ReferenceIdentifier.class, null);  // IO_IdentifiedObject.identifiers
        substitutions.put(GenericName.class,         null);  // IO_IdentifiedObject.alias
        substitutions.put(String.class,              null);  // IO_IdentifiedObject.toWKT
        substitutions.put(Extent.class,              null);  // RS_ReferenceSystem.domainOfValidity
        substitutions.put(InternationalString.class, null);  // SC_CRS.scope
        substitutions.put(Date.class,                null);  // CD_Datum.realizationEpoch
        substitutions.put(Boolean.TYPE,              null);  // CD_Ellipsoid.isIvfDefinitive
        /*
         * Assume that the CRS will be geodetic CRS.
         * After the tree has been added, we will generalize the declared types.
         */
        substitutions.put(Datum.class, GeodeticDatum.class);
        MetadataStandard standard = MetadataStandard.ISO_19111;
        incompletes = new HashSet<>(4);
        incompletes.add(CoordinateReferenceSystem.class);
        incompletes.add(CoordinateSystem.class);
        incompletes.add(GeodeticDatum.class);
        addTree(standard, SingleCRS.class, "CoordinateReferenceSystem", addToElement, false);
        metadata.addObjectValue("CoordinateReferenceSystem", CoordinateReferenceSystem.class);
        metadata.addObjectValue("Datum", Datum.class);
        /*
         * We need to add the axes explicitly, because the method signature is
         * CoordinateSystem.getAxis(int) which is not recognized by our reflection API.
         */
        addTree(standard, CoordinateSystemAxis[].class, "Axes", "CoordinateSystem", true);
        /*
         * Add conversion parameters. Note that the operation method will be replaced by a String
         * later. We can not replace it by a String now since Strings are excluded because of the
         * toWKT() method.
         */
        substitutions.put(MathTransform.class,             null);
        substitutions.put(OperationMethod.class,           null);
        substitutions.put(PositionalAccuracy.class,        null);
        substitutions.put(CoordinateReferenceSystem.class, null);
        substitutions.put(ParameterValueGroup.class,       null);
        incompletes = null; // Will consider every Conversion nodes as incomplete.
        addTree(standard, Conversion.class, "Conversion", "CoordinateReferenceSystem", false);
        metadata.addAttribute("Conversion", "method", DATATYPE_STRING, 1, 1, null);
        /*
         * Adds the parameter manually, because they are not in the ISO 19111 package
         * (so the MetadataStandard.ISO_19111 doesn't known them) and because we want
         * a simple (name, value) pair, instead than the parameter descriptor.
         */
        metadata.addElement(null, ParameterValueGroup.class, "Parameters",     "Conversion", CHILD_POLICY_REPEAT, 0, Integer.MAX_VALUE);
        metadata.addElement(null, ParameterValue.class,      "ParameterValue", "Parameters", CHILD_POLICY_EMPTY,  0, 0);
        metadata.addAttribute("ParameterValue", "name",  DATATYPE_STRING, 1, 1, null);
        metadata.addAttribute("ParameterValue", "value", DATATYPE_DOUBLE, 1, 1, null);
    }

    /**
     * Returns the metadata format instance which has been build.
     * This builder can not be used anymore after this method has been invoked.
     *
     * @return The metadata format instance.
     */
    @Override
    public SpatialMetadataFormat build() {
        done = true;
        return metadata;
    }
}
