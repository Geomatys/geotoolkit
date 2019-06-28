/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2019, Geomatys
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

package org.geotoolkit.feature.xml.jaxb;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.Features;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.CharacteristicTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.SingleAttributeTypeBuilder;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.jaxb.mapping.GeometryMapping;
import org.geotoolkit.feature.xml.jaxb.mapping.XSDMapping;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xml.AbstractConfigurable;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.Any;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.geotoolkit.xsd.xml.v2001.AttributeGroup;
import org.geotoolkit.xsd.xml.v2001.AttributeGroupRef;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ComplexRestrictionType;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.Group;
import org.geotoolkit.xsd.xml.v2001.GroupRef;
import org.geotoolkit.xsd.xml.v2001.LocalComplexType;
import org.geotoolkit.xsd.xml.v2001.LocalSimpleType;
import org.geotoolkit.xsd.xml.v2001.NamedGroup;
import org.geotoolkit.xsd.xml.v2001.NumFacet;
import org.geotoolkit.xsd.xml.v2001.OpenAttrs;
import org.geotoolkit.xsd.xml.v2001.Pattern;
import org.geotoolkit.xsd.xml.v2001.Restriction;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.SimpleContent;
import org.geotoolkit.xsd.xml.v2001.SimpleRestrictionType;
import org.geotoolkit.xsd.xml.v2001.SimpleType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.geotoolkit.xsd.xml.v2001.Union;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 * Reader class to convert an XSD to OGC Feature Type.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class JAXBFeatureTypeReader extends AbstractConfigurable {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.feature.xml.jaxp");
    private static final String FLAG_ID = "isId";
    private static final GenericName UNNAMED = NamesExt.create("unnamed");

    private final XSDSchemaContext xsdContext;

    private boolean skipStandardObjectProperties = false;

    private final Map<GenericName,IdentifiedType> typesCache = new HashMap<>();
    private final List<XSDMapping.Spi> mappings = new ArrayList<>();

    public JAXBFeatureTypeReader() {
        this(null);
    }

    /**
     *
     * @param locationMap xsd imports or resources are often online, this map allows to replace
     *      resources locations by new locations. It can be used to relocated toward a local file to
     *      use offline for example.
     */
    public JAXBFeatureTypeReader(Map<String,String> locationMap) {
        xsdContext = new XSDSchemaContext(locationMap);
        mappings.add(new GeometryMapping.Spi());
    }

    public void addMapping(XSDMapping.Spi spi) {
        mappings.add(spi);
        mappings.sort((XSDMapping.Spi o1, XSDMapping.Spi o2) -> Float.compare(o2.getPriority(), o1.getPriority()));
    }

    public boolean isSkipStandardObjectProperties() {
        return skipStandardObjectProperties;
    }

    public void setSkipStandardObjectProperties(boolean skip) {
        this.skipStandardObjectProperties = skip;
    }

    /**
     * Target namespace of the primary XSD.
     * This value is available only after reading.
     */
    public String getTargetNamespace() {
        return xsdContext.targetNamespace;
    }

    public FeatureType read(final Object candidate, final String name) throws JAXBException {
        final Schema schema = xsdContext.read(candidate).getKey();
        if (schema == null) {
            throw new IllegalArgumentException("No schema can be read from given source: "+candidate);
        }
        return getFeatureTypeFromSchema(schema, name);
    }

    public GenericNameIndex<FeatureType> read(final Object candidate) throws JAXBException {
        final Entry<Schema, String> entry = xsdContext.read(candidate);
        try {
            return getAllFeatureTypeFromSchema(entry.getKey(), entry.getValue());
        } catch (MismatchedFeatureException ex) {
            throw new JAXBException(ex.getMessage(), ex);
        } catch (IllegalNameException ex) {
            throw new JAXBException(ex.getMessage(), ex);
        }
    }

    private GenericNameIndex<FeatureType> getAllFeatureTypeFromSchema(final Schema schema, final String baseLocation) throws MismatchedFeatureException, IllegalNameException {
        final GenericNameIndex<FeatureType> result = new GenericNameIndex<>();
        // first we look for imported xsd
        // NOTE : we must list and fill the knownshemas map before analyzing
        // some xsd have cyclic references : core -> sub1 + sub2 , sub1 -> core
        final List<Entry<Schema,String>> refs = new ArrayList<>();
        refs.add(new AbstractMap.SimpleEntry<>(schema, baseLocation));
        xsdContext.listAllSchemas(schema, baseLocation, refs);

        for (Entry<Schema,String> entry : refs) {
            listFeatureTypes(entry.getKey(), result);
        }

        return result;
    }

    private void listFeatureTypes(Schema schema, GenericNameIndex<FeatureType> result) throws MismatchedFeatureException, IllegalNameException {

        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if (opAtts instanceof TopLevelElement) {
                final TopLevelElement element = (TopLevelElement) opAtts;
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = xsdContext.findComplexType(typeName);

                    if (type == null && xsdContext.findSimpleType(typeName) == null) {
                        LOGGER.log(Level.WARNING, "Unable to find a the declaration of type {0} in schemas.", typeName.getLocalPart());
                        continue;
                    }

                    //if (xsdContext.isFeatureType(type)) {
                        final BuildStack stack = new BuildStack();
                        final FeatureType ft = (FeatureType) getType(typeName.getNamespaceURI(), type, stack, true);
                        addIfMissing(result, ft);

                        //if the type name is not the same as the element name, make a subtype
                        if (!ft.getName().tip().toString().equals(element.getName())) {
                            final GenericName name = NamesExt.create(NamesExt.getNamespace(ft.getName()), element.getName());

                            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                            ftb.setName(name);
                            ftb.setSuperTypes(ft);
                            final FeatureType renamed = ftb.build();
                            addIfMissing(result, renamed);
                        }
                    //}

                } else {
                    LOGGER.log(Level.WARNING, "null typeName for element : {0}", element.getName());
                }
            }
        }
    }

    private void addIfMissing(GenericNameIndex<FeatureType> result, FeatureType type) throws IllegalNameException {
        try {
            result.add(type.getName(), type);
        } catch (IllegalNameException ex) {
            //already exist, check feature type is the same
            FeatureType ft = result.get(type.getName().toString());
            if (ft.equals(type)) {
                //ok
                return;
            } else {
                throw new IllegalNameException("A type with a different definition already exist for name " + type.getName());
            }
        }
    }

    private FeatureType getFeatureTypeFromSchema(Schema schema, String name) {
        final TopLevelElement element = schema.getElementByName(name);
        if (element == null) {
            throw new IllegalArgumentException("No type found for name "+name);
        }
        final QName typeName = element.getType();
        final ComplexType type = xsdContext.findComplexType(typeName);
        final BuildStack stack = new BuildStack();
        return (FeatureType) getType(typeName.getNamespaceURI(), type, stack, true);
    }

    /**
     *
     * @param qname
     * @param stack
     * @return FeatureType or GenericName
     * @throws MismatchedFeatureException
     */
    private Object getType(QName qname, BuildStack stack) throws MismatchedFeatureException {
        final ComplexType type = xsdContext.findComplexType(qname);

        if (type == null) {
            //search for an element with this name
            final Element element = xsdContext.findGlobalElement(qname);
            if (element != null) {
                return elementToAttribute(qname.getNamespaceURI(), element, stack);
            }
        }

        if (type == null) {
            throw new MismatchedFeatureException("Unable to find complex type for name : "+ qname);
        }

        final GenericName name = extractFinalName(qname.getNamespaceURI(),type);
        if (typesCache.containsKey(name)) {
            return typesCache.get(name);
        }
        if (stack.contains(name)) {
            //recursive build
            return name;
        }

        stack.add(name);
        return getType(qname.getNamespaceURI(), type, stack, true);
    }

    private Object getType(String namespaceURI, ComplexType type, BuildStack stack, boolean useCache) {

        final GenericName name = extractFinalName(namespaceURI,type);

        if (useCache && typesCache.containsKey(name)) {
            return typesCache.get(name);
        }

        for (XSDMapping.Spi mapping : mappings) {
            final XSDMapping map = mapping.create(name, this, type);
            if (map != null) {
                IdentifiedType mappedType = map.getType();

                if (mappedType instanceof AttributeType) {
                    AttributeType at = (AttributeType) mappedType;

                    AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(at);
                    atb.addCharacteristic(XSDMapping.class)
                            .setName(GMLConvention.MAPPING)
                            .setDefaultValue(map);
                    mappedType = atb.build();

//                    AttributeType[] att = (AttributeType[]) at.characteristics().values().toArray(new AttributeType[0]);
//                    final Map properties = new HashMap();
//                    properties.put(AbstractIdentifiedType.NAME_KEY, at.getName());
//                    properties.put(AbstractIdentifiedType.DEFINITION_KEY, at.getDefinition());
//                    properties.put(AbstractIdentifiedType.DEPRECATED_KEY, ((Deprecable) at).isDeprecated());
//                    properties.put(AbstractIdentifiedType.DESCRIPTION_KEY, at.getDescription());
//                    properties.put(AbstractIdentifiedType.DESIGNATION_KEY, at.getDesignation());
//                    mappedType = new MappedAttributeType(
//                            properties,
//                            at.getValueClass(),
//                            at.getMinimumOccurs(),
//                            at.getMaximumOccurs(),
//                            at.getDefaultValue(),
//                            map,
//                            att);
                }

                typesCache.put(name, mappedType);
                return mappedType;
            }
        }

        final boolean deprecated = GMLConvention.isDeprecated(type);

        //read simple content type if defined
        final SimpleContent simpleContent = type.getSimpleContent();
        if (simpleContent != null) {
            final ExtensionType sext = simpleContent.getExtension();

            if (sext != null) {
                //simple type base, it must be : this is the content of the tag <tag>XXX<tag>
                //it is not named, so we call it value
                final QName base = sext.getBase();
                final AttributeType st = (AttributeType) resolveType(base, stack);
                final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(st);
                atb.setName(name);
                atb.setDeprecated(deprecated);

                //read attributes
                for (PropertyType property : getAnnotatedAttributes(namespaceURI, sext.getAttributeOrAttributeGroup(), stack)) {
                    CharacteristicTypeBuilder cb = atb.getCharacteristic(property.getName().toString());
                    if (cb == null) {
                        atb.addCharacteristic((AttributeType) property);
                    } else {
                        //characteristic already exist
                    }
                }
                final AttributeType att = atb.build();
                typesCache.put(name, att);
                return att;
            }

            final SimpleRestrictionType restriction = simpleContent.getRestriction();
            if (restriction != null) {

                final QName base = restriction.getBase();
                if (base != null) {
                    final ComplexType sct = xsdContext.findComplexType(base);
                    if (sct != null) {
                        final AttributeType tct = (AttributeType) getType(namespaceURI, sct, stack, true);
                        final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(tct);
                        atb.setName(name);
                        atb.setDeprecated(deprecated);

                        //read attributes
                        for (PropertyType property : getAnnotatedAttributes(namespaceURI, restriction.getAttributeOrAttributeGroup(), stack)) {
                            CharacteristicTypeBuilder cb = atb.getCharacteristic(property.getName().toString());
                            if (cb == null) {
                                atb.addCharacteristic((AttributeType) property);
                            } else {
                                //characteristic already exist
                            }

                        }
                        final AttributeType att = atb.build();
                        typesCache.put(name, att);
                        return att;
                    } else {
//                        final PropertyType restType = resolveType(base, stack);
//                        addOrReplace(finalType.builder, atb.create(restType, NamesExt.create(namespaceURI, Utils.VALUE_PROPERTY_NAME), 0, 1, false, null));
                    }
                }
            }

            throw new MismatchedFeatureException("Undefined simple type : "+name);
        }


        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        ftb.setDeprecated(deprecated);

        //read attributes
        for (PropertyType property : getAnnotatedAttributes(namespaceURI, type.getAttributeOrAttributeGroup(), stack)) {
            addProperty(ftb, property);
        }

        //read sequence properties
        for (PropertyType property : getGroupAttributes(namespaceURI, type.getSequence(), stack)) {
            addProperty(ftb, property);
        }

        //read complex content if defined
        final ComplexContent content = type.getComplexContent();
        if (content != null) {
            final ExtensionType extension = content.getExtension();
            if (extension != null) {
                final QName base = extension.getBase();
                if (base != null && !base.getLocalPart().equalsIgnoreCase("anytype")) {
                    final Object parent = getType(base, stack);
                    if (parent instanceof FeatureType) {
                        ftb.setSuperTypes((FeatureType) parent);
                    } else if (parent instanceof GenericName) {
                        //parent type is currently being resolved
                        return name;
                    }
                }

                //read attributes
                for (PropertyType property : getAnnotatedAttributes(namespaceURI, extension.getAttributeOrAttributeGroup(), stack)) {
                    addProperty(ftb, property);
                }

                //read groups
                for (PropertyType property : getGroupAttributes(namespaceURI, extension.getSequence(), stack)) {
                    addProperty(ftb, property);
                }
            }

            /* BIG DIRTY HACK: Needed for GML 2.1.2 support.
             * For geometry definition, GML 2 propose an association to some
             * data-type defined by restiction over an abstract geometry type.
             * But, we do not want it to an association, we want it to be an
             * attribute, for god sake ! So, we cheat and if we find a structure
             * like that, we transform it into attribute (oh god that's awful).
             */
            final ComplexRestrictionType restriction = content.getRestriction();
            if (restriction != null) {
                final QName base = restriction.getBase();
                if (base != null) {
                    Object restrictionType = getType(base, stack);
                    if (restrictionType instanceof FeatureType) {
                        ftb.setSuperTypes((FeatureType) restrictionType);
                    }



//                    final ComplexType sct = xsdContext.findComplexType(base);
//                    if (sct != null) {
//                        final Object obj = getType(base.getNamespaceURI(), sct, stack);
//                        if (obj instanceof FeatureType
////                                && isGeometric((FeatureType)obj)
//                                ) {
//                            final ExplicitGroup sequence = sct.getSequence();
//                            if (sequence != null) {
//                                final List<Element> elements = sequence.getElements();
//                                if (elements != null && !elements.isEmpty()) {
//                                    Element e = sequence.getElements().get(0);
//                                    return ftb.addAttribute(Geometry.class)
//                                            .setName(e.getRef().getLocalPart())
//                                            .setMinimumOccurs(sequence.getMinOccurs())
//                                            .build();
//                                }
//                            }
//                        } else if (obj instanceof PropertyType) {
//                            final PropertyTypeBuilder ptb = new FeatureTypeBuilder().addProperty((PropertyType) obj);
//                            if (ptb instanceof PropertyTypeBuilder) {
//                                final AttributeTypeBuilder atb = (AttributeTypeBuilder) ptb;
//                                // check characteristics
//                                for (PropertyType property : getAnnotatedAttributes(namespaceURI, restriction.getAttributeOrAttributeGroup(), stack)) {
//                                    if (atb.getCharacteristic(property.getName().toString()) == null) {
//                                        atb.addCharacteristic((AttributeType) property);
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }

        //read choice if set
        final ExplicitGroup choice = type.getChoice();
        if (choice != null) {
            //this is the case of gml:location
        }


////        if (GMLConvention.isDecoratedProperty(featureType.getName().tip().toString())) {
////            //Used by geometry property types but also in some gml profils
////            final String decoratedName = NamesExt.toExpandedString(featureType.getName());
////
////            //this is an encapsulated property, we unroll it
////            final Collection<? extends PropertyType> subProps = featureType.getProperties(true);
////            //we peek the first association, there should be only one
////            //but attributes are possible
////            for (PropertyType pt : subProps) {
////                if (pt.getName().tip().toString().startsWith("@")) {
////                    //ignore xml attributes
////                    continue;
////                }
////
////                if (pt instanceof FeatureAssociationRole) {
////                    /* HACK : GML 3.1.1 : Only way I've found to manage
////                     * geometries as attributes. If we've found an association,
////                     * and if it's feature type is a geometric property
////                     * (derived from abstract geometric type), well, we
////                     * return a geometric property.
////                     */
////                    final FeatureAssociationRole subFar = (FeatureAssociationRole) pt;
////                    FeatureType valueType = subFar.getValueType();
////                    FeatureAssociationRole ar = ftb
////                            .addAssociation(subFar)
////                            .setDescription(GMLConvention.DECORATED_DESCRIPTION+NamesExt.toExpandedString(subFar.getName()))
////                            .build();
////
////                    typesCache.put(name, ar);
////                    return featureType;
////                } else if (pt instanceof AttributeType) {
////                    AttributeType at = (AttributeType) pt;
////
////                    ftb.clear();
////                    at = ftb.addAttribute(at).setDescription(GMLConvention.DECORATED_DESCRIPTION+" "+NamesExt.toExpandedString(at.getName())).build();
////                    typesCache.put(name, at);
////                    return at;
////                }
////            }
////
////            throw new UnsupportedOperationException("Decorated property without any property");
////
////        }

            //define the default geometry
            PropertyTypeBuilder candidateDefaultGeom = null;
            for (PropertyTypeBuilder ptb : ftb.properties()) {
                if (ptb instanceof AttributeTypeBuilder) {
                    Class valueClass = ((AttributeTypeBuilder) ptb).getValueClass();
                    if (Geometry.class.isAssignableFrom(valueClass)) {
                        XSDMapping mapping = GMLConvention.getMapping(ptb.build());
                        if (mapping instanceof GeometryMapping) {
                            if (((GeometryMapping) mapping).isDecorated()) {
                                //keep it as a candidate, we prefere undecorated properties
                                candidateDefaultGeom = ptb;
                            } else {
                                candidateDefaultGeom = null;
                                ((AttributeTypeBuilder) ptb).addRole(AttributeRole.DEFAULT_GEOMETRY);
                                break;
                            }
                        }
                    }
                }
            }
            if (candidateDefaultGeom != null) {
                ((AttributeTypeBuilder) candidateDefaultGeom).addRole(AttributeRole.DEFAULT_GEOMETRY);
            }
            
            FeatureType featureType = ftb.build();

            typesCache.put(name, featureType);
            return featureType;
    }

    private List<PropertyType> getGroupAttributes(String namespaceURI, Group group, BuildStack stack) throws MismatchedFeatureException {
        if (group == null) return Collections.EMPTY_LIST;

        final List<PropertyType> atts = new ArrayList<>();

        final List<Object> particles = group.getParticle();
        for (Object particle : particles) {
            if (particle instanceof JAXBElement) {
                particle = ((JAXBElement) particle).getValue();
            }

            if (particle instanceof Element) {
                final Element ele = (Element) particle;
                final PropertyType att = elementToAttribute(namespaceURI, ele, stack);
                atts.add(att);

            } else if (particle instanceof Any) {
                final Any ele = (Any) particle;
                final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                atb.setName(namespaceURI, Utils.ANY_PROPERTY_NAME);
                atb.setValueClass(Object.class);

                //override properties which are defined
                atb.setMinimumOccurs(ele.getMinOccurs() == null ? 0 : ele.getMinOccurs().intValue());

                final String maxxAtt = ele.getMaxOccurs();
                if("unbounded".equalsIgnoreCase(maxxAtt)) {
                    atb.setMaximumOccurs(Integer.MAX_VALUE);
                } else if(maxxAtt!=null){
                    atb.setMaximumOccurs(Integer.parseInt(maxxAtt));
                }

                atts.add(atb.build());

            } else if (particle instanceof GroupRef) {
                final GroupRef ref = (GroupRef) particle;
                final QName groupRef = ref.getRef();
                final NamedGroup ng = xsdContext.findGlobalGroup(groupRef);

                final List<PropertyType> groupAttributes = getGroupAttributes(namespaceURI, ng, stack);

                //change min/max occurences
                int minOcc = ref.getMinOccurs() == null ? 0 : ref.getMinOccurs().intValue();
                int maxOcc = 1;
                String maxxAtt = ref.getMaxOccurs();
                if ("unbounded".equalsIgnoreCase(maxxAtt)) {
                    maxOcc = Integer.MAX_VALUE;
                } else if (maxxAtt != null) {
                    maxOcc = Integer.parseInt(maxxAtt);
                }
                for (PropertyType pt : groupAttributes) {
                    pt = new FeatureTypeBuilder().addProperty(pt).setMinimumOccurs(minOcc).setMaximumOccurs(maxOcc).build();
                    atts.add(pt);
                }

            } else if (particle instanceof ExplicitGroup) {
                final ExplicitGroup eg = (ExplicitGroup) particle;
                atts.addAll(getGroupAttributes(namespaceURI, eg, stack));
            } else {
                throw new MismatchedFeatureException("Unexpected TYPE : "+particle);
            }
        }

        return atts;
    }

    private PropertyType elementToAttribute(final String namespaceURI, Element element, BuildStack stack) {

        GenericName name = null;

        final QName refName = element.getRef();
        PropertyType refType = null;
        if (refName != null) {
            final Element parentElement = xsdContext.findGlobalElement(refName);
            if (parentElement == null) {
                throw new MismatchedFeatureException("unable to find referenced element : "+ refName);
            }
            refType = elementToAttribute(namespaceURI, parentElement, stack);
            name = NamesExt.create(refName);
        }

        //extract name
        String localName = element.getName();
        if (localName == null) {
            localName = element.getId();
        }
        if (localName != null) {
            //override name
            name = NamesExt.create(namespaceURI, localName);
        }

        //extract min/max
        final Integer[] minMax = getMinMax(element);
        if (minMax[0] == null) minMax[0] = 1;
        if (minMax[1] == null) minMax[1] = 1;


        final QName typeName = element.getType();
        if (typeName != null) {

            PropertyType parentType = resolveType(typeName, stack);
            if (element instanceof TopLevelElement && parentType instanceof FeatureAssociationRole) {
                final Object sct = getType(typeName, stack);
                if (sct instanceof FeatureType) {
                    FeatureType type = new FeatureTypeBuilder()
                            .setSuperTypes((FeatureType) sct)
                            .setName(name)
                            .build();
                    parentType = new FeatureTypeBuilder().addAssociation(type).setName(name).build();
                }
            }
            return reDefine(parentType, name, minMax[0], minMax[1], element.isNillable());
        }

        if (refType != null) {
            return reDefine(refType, name, minMax[0], minMax[1], element.isNillable());
        }

        final LocalSimpleType simpleType = element.getSimpleType();
        if (simpleType != null) {
            final PropertyType restrictionType = toProperty(simpleType, stack);
            return reDefine(restrictionType, name, minMax[0], minMax[1], element.isNillable());
        }

        final LocalComplexType complexType = element.getComplexType();
        if (complexType != null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder((FeatureType) getType(namespaceURI, complexType, stack, false));
            ftb.setName(name);
            if(element.isNillable()) {
                ftb.addAttribute(GMLConvention.NILLABLE_CHARACTERISTIC);
            }
            return new DefaultAssociationRole(Collections.singletonMap("name", name), ftb.build(), minMax[0], minMax[1]);
        }

        if (element.isAbstract()) {
            //create an abstract feature type with nothing in it
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(name);
            ftb.setAbstract(true);
            if(element.isNillable()) {
                ftb.addAttribute(GMLConvention.NILLABLE_CHARACTERISTIC);
            }
            return new DefaultAssociationRole(Collections.singletonMap("name", name), ftb.build(), minMax[0], minMax[1]);
        } else {
            throw new UnsupportedOperationException("No type defined for "+element);
        }
    }

    /**
     * Change property name, cardinality and nillability.
     *
     * @param type
     * @param name
     * @param minOcc
     * @param maxOcc
     * @param nillable
     * @return
     * @throws MismatchedFeatureException
     */
    private PropertyType reDefine(PropertyType type, GenericName name, int minOcc, int maxOcc, boolean nillable) throws MismatchedFeatureException{
        if (type instanceof AttributeType) {
            final AttributeTypeBuilder atb = new FeatureTypeBuilder()
                    .addAttribute((AttributeType)type)
                    .setName(name)
                    .setMinimumOccurs(minOcc)
                    .setMaximumOccurs(maxOcc);
            if (nillable) {
                CharacteristicTypeBuilder cb = atb.getCharacteristic(GMLConvention.NILLABLE_PROPERTY.toString());
                if (cb == null) cb = atb.addCharacteristic(GMLConvention.NILLABLE_CHARACTERISTIC);
                cb.setDefaultValue(true);
            }
            return atb.build();
        } else if (type instanceof FeatureAssociationRole) {
            final Map properties = Collections.singletonMap("name", name);
            try {
                FeatureType valueType = ((FeatureAssociationRole)type).getValueType();
                if (nillable) {
                    final FeatureTypeBuilder ftb = new FeatureTypeBuilder(valueType);
                    ftb.addAttribute(GMLConvention.NILLABLE_CHARACTERISTIC);
                    valueType = ftb.build();
                }
                return new DefaultAssociationRole(properties, valueType, minOcc, maxOcc);
            } catch (IllegalStateException ex) {
                return new DefaultAssociationRole(properties,
                        Features.getValueTypeName(type), minOcc, maxOcc);
            }
        } else {
            throw new UnsupportedOperationException("Unexpected type "+type.getClass());
        }
    }

    private List<PropertyType> getAnnotatedAttributes(String namespaceURI, final List<Annotated> atts, BuildStack stack) throws MismatchedFeatureException{

        if (atts != null) {
            final List<PropertyType> props = new ArrayList<>();
            for (Annotated att : atts) {
                if (att instanceof Attribute) {
                    props.add(getAnnotatedAttributes(namespaceURI, (Attribute) att, stack));
                } else if(att instanceof AttributeGroupRef) {
                    props.addAll(getAnnotatedAttributes(namespaceURI, (AttributeGroupRef) att, stack));
                }
            }
            return props;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private AttributeType getAnnotatedAttributes(String namespace, final Attribute att, BuildStack stack) throws MismatchedFeatureException{
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();

        if (att.getRef() != null) {
            namespace = att.getRef().getNamespaceURI();
            //copy properties from parent
            final Attribute attRef = xsdContext.findGlobalAttribute(att.getRef());
            final AttributeType atDesc = getAnnotatedAttributes(namespace, attRef, stack);
            atb.copy(atDesc);
            atb.setName(namespace, atDesc.getName().tip().toString());
        } else {
            namespace = null;
        }

        final String id = att.getId();
        final String name = att.getName();
        final String def = att.getDefault();
        final AttributeType type = (AttributeType) resolveAttributeValueName(att, stack);
        final String use = att.getUse();

        if (id != null || name != null) {
            //find name
            final String tip = ((name==null) ? id : name);
            final GenericName attName = NamesExt.create(namespace, "@"+ tip);
            atb.setName(attName);

            //mark identifier fields
            if(type.getName().tip().toString().equals("ID")){
                atb.addCharacteristic(FLAG_ID, Boolean.class, 1, 1, Boolean.TRUE);
            }
        }
        //find min/max occurences
        atb.setMinimumOccurs((use==null || "optional".equals(use)) ? 0 : 1);
        atb.setMaximumOccurs(1);
        atb.setValueClass(type.getValueClass());

        if (def != null && !def.isEmpty()) {
            final Object defVal = ObjectConverters.convert(def, type.getValueClass());
            atb.setDefaultValue(defVal);
        }

        return atb.build();
    }

    private List<PropertyType> getAnnotatedAttributes(final String namespaceURI, final AttributeGroup group, BuildStack stack) throws MismatchedFeatureException{

//        final QName ref = group.getRef();
//        if (ref != null) {
//            final NamedAttributeGroup refGroup = xsdContext.findAttributeGroup(ref);
//            addOrReplace(descs, getAnnotatedAttributes(namespaceURI, refGroup));
//        }

        final List<PropertyType> descs = new ArrayList<>();
        final List<Annotated> atts = group.getAttributeOrAttributeGroup();
        if (atts != null) {
            for (Annotated att : atts) {
                if (att instanceof Attribute) {
                    descs.add(getAnnotatedAttributes(namespaceURI, (Attribute) att, stack));
                } else if (att instanceof AttributeGroupRef) {
                    descs.addAll(getAnnotatedAttributes(namespaceURI, (AttributeGroupRef) att, stack));
                }
            }
        }
        return descs;
    }

    private PropertyType resolveAttributeValueName(Attribute att, BuildStack stack) throws MismatchedFeatureException{
        //test direct type
        final QName type = att.getType();
        if (type != null) {
            return resolveType(type, stack);
        }

        //test reference
        final QName ref = att.getRef();
        if (ref != null) {
            final Attribute parentAtt = xsdContext.findGlobalAttribute(ref);
            if (parentAtt ==null) {
                throw new MismatchedFeatureException("The attribute : " + ref + " has not been found.");
            }
            return resolveAttributeValueName(parentAtt, stack);
        }

        //test local simple type
        final LocalSimpleType simpleType = att.getSimpleType();
        if (simpleType != null) {
            return toProperty(simpleType, stack);
        }

        return null;
    }

    /**
     *
     * @param attributeElement
     * @return [0] minimum values count, may be null
     *         [1] maximum values count, may be null
     */
    private static Integer[] getMinMax(Element attributeElement) {
        final Integer[] minmax = new Integer[2];

        //override properties which are defined
        minmax[0] = attributeElement.getMinOccurs();

        final String maxxAtt = attributeElement.getMaxOccurs();
        if("unbounded".equalsIgnoreCase(maxxAtt)) {
            minmax[1] = Integer.MAX_VALUE;
        } else if(maxxAtt!=null){
            minmax[1] = Integer.parseInt(maxxAtt);
        }

        return minmax;
    }

    private PropertyType resolveType(QName name, BuildStack stack) throws MismatchedFeatureException{

        //check if primitive type
        if (Utils.existPrimitiveType(name.getLocalPart())) {
            final Class valueType = Utils.getTypeFromQName(name);
            final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
            atb.setName(NamesExt.create(name));
            atb.setValueClass(valueType);

            final boolean isEncapsulated = GMLConvention.isDecoratedProperty(name.getLocalPart());
            if (isEncapsulated) {
                atb.setDescription(GMLConvention.DECORATED_DESCRIPTION+" "+name.toString());
            }
            return atb.build();
        }

        //check if a simple type exist
        final SimpleType simpleType = xsdContext.findSimpleType(name);
        if (simpleType != null) {
            return toProperty(simpleType, stack);
        } else {
            //could be a complex type ... for a simple content, that's not an error. xsd/xml makes no sense at all sometimes
            final Object sct = getType(name, stack);
            final Map properties = Collections.singletonMap("name", NamesExt.create(name));
            if (sct == null) {
                throw new MismatchedFeatureException("Could not find type : "+name);
            } else if(sct instanceof PropertyType) {
                return (PropertyType) sct;
            } else if(sct instanceof GenericName) {
                return new DefaultAssociationRole(properties, (GenericName)sct, 1, 1);
            } else if(sct instanceof FeatureType) {
                return new DefaultAssociationRole(properties, (FeatureType)sct, 1, 1);
            } else if(sct instanceof AttributeType) {
                return (AttributeType) sct;
            } else {
                throw new MismatchedFeatureException("Unexpected type "+sct);
            }

        }
    }

    private PropertyType toProperty(SimpleType simpleType, BuildStack stack) throws MismatchedFeatureException{

        final Restriction restriction = simpleType.getRestriction();
        if (restriction != null) {
            QName base = restriction.getBase();

            AttributeType baseType = null;
            if (base != null) {
                baseType = (AttributeType) resolveType(base, stack);
            }
            final LocalSimpleType localSimpleType = restriction.getSimpleType();
            if (localSimpleType != null) {
                baseType = (AttributeType) toProperty(localSimpleType, stack);
            }

            if (baseType != null) {
                final AttributeTypeBuilder atb = new FeatureTypeBuilder().addAttribute(baseType);

                for (Object facet : restriction.getFacets()) {
                    if (facet instanceof JAXBElement) {
                        String name = ((JAXBElement) facet).getName().getLocalPart();
                        facet = ((JAXBElement) facet).getValue();

                        if (facet instanceof NumFacet) {
                            final NumFacet nf = (NumFacet) facet;
                            final int length = Integer.valueOf(nf.getValue());
                            if ("maxLength".equalsIgnoreCase(name)) {
                                atb.setMaximalLength(length);
                            }
                        }
                    } else if (facet instanceof Pattern) {
                        //TODO
                    }
                }

                return atb.build();
            }
        }

        // TODO union can be a collection of anything
        // collection ? array ? Object.class ? most exact type ?
        final Union union = simpleType.getUnion();
        if (union != null) {
            if (union.getMemberTypes() != null && !union.getMemberTypes().isEmpty()) {
                final QName name = union.getMemberTypes().get(0);
                final SimpleType refType = xsdContext.findSimpleType(name);
                if (refType == null) {
                    throw new MismatchedFeatureException("Could not find type : "+name);
                }
                return toProperty(refType, stack);
            } else if (union.getSimpleType() != null && !union.getSimpleType().isEmpty()) {
                final LocalSimpleType st = union.getSimpleType().get(0);
                return toProperty(st, stack);
            }
        }

        //TODO list type
        final org.geotoolkit.xsd.xml.v2001.List list = simpleType.getList();
        if (list != null) {
            final QName subTypeName = list.getItemType();
            if (subTypeName != null) {
                final SimpleType refType = xsdContext.findSimpleType(subTypeName);
                if (refType != null) {
                    return toProperty(refType, stack);
                }
                return resolveType(subTypeName, stack);
            }
            final LocalSimpleType subtype = list.getSimpleType();
            if (subtype != null) {
                return toProperty(simpleType, stack);
            }
        }

        if (Utils.existPrimitiveType(simpleType.getName())) {
            return resolveType(new QName(null, simpleType.getName()), stack);
        } else {
            return null;
        }
    }

    /**
     * Add property to feature type builder.
     * This methods skips standard object properties, reset identifier parameters
     * and unroll propertytypes.
     *
     * @param ftb
     * @param property
     */
    private void addProperty(FeatureTypeBuilder ftb, PropertyType property) {

        if (skipStandardObjectProperties &&
               (Utils.GML_ABSTRACT_FEATURE_PROPERTIES.contains(property.getName()) ||
                Utils.GML_STANDARD_OBJECT_PROPERTIES.contains(property.getName()))
                ) {
            return;
        }

        if (property instanceof AttributeType) {
            final AttributeType att = (AttributeType) property;
////            final boolean decorated = GMLConvention.isDecoratedProperty(att.getName().tip().toString());

            final AttributeTypeBuilder atb = ftb.addAttribute(att);
            if (FeatureExt.getCharacteristicValue(property, FLAG_ID, false)) {
                //special case, consider it as a property
                atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
                atb.characteristics().clear();
            }

////            if (decorated) {
////                atb.setDescription(GMLConvention.DECORATED_DESCRIPTION);
////            }

        } else if (property instanceof FeatureAssociationRole) {
            final FeatureAssociationRole far = (FeatureAssociationRole) property;

            //PropertyType field are only decorations used to rename elements
            //this would cause a double association hard to manipulate
            //so we unroll them
            final GenericName valueTypeName = Features.getValueTypeName(far);
            final String tip = valueTypeName.tip().toString();
////            final boolean isEncapsulated = GMLConvention.isDecoratedProperty(tip);
////            if (isEncapsulated) {
////                //this is an encapsulated property, we unroll it
////                final FeatureType valueType = far.getValueType();
////                final Collection<? extends PropertyType> subProps = valueType.getProperties(true);
////                boolean found = false;
////                //we peek the first association, there should be only one
////                //but attributes are possible
////                for (PropertyType pt : subProps) {
////                    if (pt instanceof FeatureAssociationRole) {
////                        /* HACK : GML 3.1.1 : Only way I've found to manage
////                         * geometries as attributes. If we've found an association,
////                         * and if it's feature type is a geometric property
////                         * (derived from abstract geometric type), well, we
////                         * return a geometric property.
////                         */
////                        final FeatureAssociationRole subFar = (FeatureAssociationRole) pt;
////                        final PropertyTypeBuilder propBuilder;
//////                        if (isGeometric(subFar)) {
//////                            propBuilder = ftb.addAttribute(Geometry.class);
//////                        } else {
////                            propBuilder = ftb.addAssociation(subFar);
//////                        }
////
////                        propBuilder
////                                .setDescription(GMLConvention.DECORATED_DESCRIPTION)
////                                .setMinimumOccurs(far.getMinimumOccurs())
////                                .setMaximumOccurs(far.getMaximumOccurs())
////                                .setName(far.getName());
////                        found = true;
////                        break;
////                    }
////                }
////
////                if (!found) {
////                    //add property normally
////                    ftb.addProperty(property);
////                }
////
////            } else {
                ftb.addProperty(property);
////            }
        } else {
            ftb.addProperty(property);
        }
    }

//    private static boolean isGeometric(FeatureAssociationRole source) {
//        try {
//            return isGeometric(source.getValueType());
//        } catch (IllegalStateException e) {
//            return false; // Cannot know, we assume it's not a geometry.
//        }
//    }
//
//    private static boolean isGeometric(final FeatureType source) {
//        return withSuperTypes(source)
//                .map(FeatureType::getName)
//                .map(name -> name.tip().toString())
//                .anyMatch(name -> "AbstractGeometryType".equals(name) || "GeometryAssociationType".equals(name));
//    }

    private static Stream<FeatureType> withSuperTypes(final FeatureType ft) {
        return Stream.concat(
                Stream.of(ft),
                ft.getSuperTypes().stream().flatMap(JAXBFeatureTypeReader::withSuperTypes)
        );
    }

    private GenericName extractFinalName(String namespaceURI, ComplexType type) {
        String localName = type.getName();
        if(localName==null) localName = type.getId();
        if(localName==null && namespaceURI==null) return UNNAMED;
        if(localName==null && namespaceURI!=null) return NamesExt.create(namespaceURI, "unnamed");

        //we remove the 'Type' extension for feature types.
        GenericName name;
        final boolean isFeatureType = xsdContext.isFeatureType(type);
        if (isFeatureType && localName.endsWith("Type")) {
            name = NamesExt.create(namespaceURI, localName.substring(0,localName.length()-4));
        } else{
            name = NamesExt.create(namespaceURI, localName);
        }
        return name;
    }

    /**
     * Stores the names of types being created at this time.
     *
     */
    private static final class BuildStack extends HashSet<GenericName> {

    }

}
