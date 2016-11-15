/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.Features;
import org.apache.sis.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.CharacteristicTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xml.AbstractConfigurable;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.Any;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.geotoolkit.xsd.xml.v2001.AttributeGroup;
import org.geotoolkit.xsd.xml.v2001.AttributeGroupRef;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.Group;
import org.geotoolkit.xsd.xml.v2001.GroupRef;
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
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
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

    private final XSDSchemaContext xsdContext;

    private boolean skipStandardObjectProperties = true;

    private final Map<GenericName,FeatureType> featureTypeCache = new HashMap<>();

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
        final Schema schema = xsdContext.read(candidate,name);
        return getFeatureTypeFromSchema(schema, name);
    }

    public List<FeatureType> read(final Object candidate) throws JAXBException {
        final Entry<Schema, String> entry = xsdContext.read(candidate);
        return getAllFeatureTypeFromSchema(entry.getKey(), entry.getValue());
    }

    private List<FeatureType> getAllFeatureTypeFromSchema(final Schema schema, final String baseLocation) throws MismatchedFeatureException {
        final List<FeatureType> result = new ArrayList<>();
        // first we look for imported xsd
        // NOTE : we must list and fill the knownshemas map before analyzing
        // some xsd have cyclic references : core -> sub1 + sub2 , sub1 -> core
        final List<Entry<Schema,String>> refs = new ArrayList<>();
        refs.add(new AbstractMap.SimpleEntry<>(schema, baseLocation));
        xsdContext.listAllSchemas(schema, baseLocation, refs);

        for(Entry<Schema,String> entry : refs){
            listFeatureTypes(entry.getKey(), result);
        }

        return result;
    }

    private void listFeatureTypes(Schema schema, List<FeatureType> result) throws MismatchedFeatureException {

        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if(opAtts instanceof TopLevelElement){
                final TopLevelElement element = (TopLevelElement) opAtts;
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = xsdContext.findComplexType(typeName);

                    if (xsdContext.isFeatureType(type)) {
                        final BuildStack stack = new BuildStack();
                        final FeatureType ft = (FeatureType) getType(typeName.getNamespaceURI(), type, stack);
                        result.add(ft);

                        //if the type name is not the same as the element name, make a subtype
                        if(!ft.getName().tip().toString().equals(element.getName())){
                            final GenericName name = NamesExt.create(NamesExt.getNamespace(ft.getName()), element.getName());

                            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                            ftb.setName(name);
                            ftb.setSuperTypes(ft);
                            final FeatureType renamed = ftb.build();
                            result.add(renamed);
                        }

                    } else if (type == null && xsdContext.findSimpleType(typeName) == null) {
                        LOGGER.log(Level.WARNING, "Unable to find a the declaration of type {0} in schemas.", typeName.getLocalPart());
                        continue;
                    }

                } else {
                    LOGGER.log(Level.WARNING, "null typeName for element : {0}", element.getName());
                }
            }
        }

    }

    private FeatureType getFeatureTypeFromSchema(Schema schema, String name){
        final TopLevelElement element = schema.getElementByName(name);
        final QName typeName = element.getType();
        final ComplexType type = xsdContext.findComplexType(typeName);
        final BuildStack stack = new BuildStack();
        return (FeatureType) getType(typeName.getNamespaceURI(), type, stack);
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
        if(type==null){
            throw new MismatchedFeatureException("Unable to find complex type for name : "+ qname);
        }

        final GenericName name = extractFinalName(qname.getNamespaceURI(),type);
        if (featureTypeCache.containsKey(name)) {
            return featureTypeCache.get(name);
        }
        if (stack.contains(name)) {
            //recursive build
            return name;
        }

        stack.add(name);
        return getType(qname.getNamespaceURI(), type, stack);
    }

    private Object getType(String namespaceURI, ComplexType type, BuildStack stack) {

        final GenericName name = extractFinalName(namespaceURI,type);

        if (featureTypeCache.containsKey(name)) {
            return featureTypeCache.get(name);
        }

        System.out.println("GET TYPE "+name);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);

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
                if (base!=null && !base.getLocalPart().equalsIgnoreCase("anytype")) {
                    final Object parent = getType(base, stack);
                    if (parent instanceof FeatureType) {
                        ftb.setSuperTypes((FeatureType)parent);
                    } else if(parent instanceof GenericName) {
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
        }

        //read simple content type if defined
        final SimpleContent simpleContent = type.getSimpleContent();
        if(simpleContent!=null){
            final ExtensionType sext = simpleContent.getExtension();

            if(sext!=null){
                //simple type base, it must be : this is the content of the tag <tag>XXX<tag>
                //it is not named, so we call it value
                final QName base = sext.getBase();
                final PropertyType st = resolveType(base, stack);

                if(st instanceof FeatureAssociationRole){
                    addProperty(ftb,st);
                }else{
                    addProperty(ftb,st);
                }

                //read attributes
                for (PropertyType property : getAnnotatedAttributes(namespaceURI, sext.getAttributeOrAttributeGroup(), stack)) {
                    addProperty(ftb, property);
                }
            }

            final SimpleRestrictionType restriction = simpleContent.getRestriction();
            if(restriction!=null){

                final QName base = restriction.getBase();
                if(base !=null){
                    final ComplexType sct = xsdContext.findComplexType(base);
                    if(sct!=null){
                        final Object tct = getType(namespaceURI, sct, stack);
                        ftb.setSuperTypes((FeatureType) tct);
                    }else{
//                        final PropertyType restType = resolveType(base, stack);
//                        addOrReplace(finalType.builder, atb.create(restType, NamesExt.create(namespaceURI, Utils.VALUE_PROPERTY_NAME), 0, 1, false, null));
                    }
                }

                //read attributes
                for (PropertyType property : getAnnotatedAttributes(namespaceURI, restriction.getAttributeOrAttributeGroup(), stack)) {
                    addProperty(ftb, property);
                }
            }

        }

        //read choice if set
        final ExplicitGroup choice = type.getChoice();
        if (choice != null) {
            //TODO
        }

        final FeatureType featureType = ftb.build();
        featureTypeCache.put(name, featureType);
        return featureType;
    }

    private List<PropertyType> getGroupAttributes(String namespaceURI, Group group, BuildStack stack) throws MismatchedFeatureException {
        if(group==null) return Collections.EMPTY_LIST;

        final List<PropertyType> atts = new ArrayList<>();

        final List<Object> particles = group.getParticle();
        for(Object particle : particles){
            if(particle instanceof JAXBElement){
                particle = ((JAXBElement)particle).getValue();
            }

            if(particle instanceof Element){
                final Element ele = (Element) particle;
                final PropertyType att = elementToAttribute(namespaceURI, ele, stack);
                atts.add(att);

            }else if(particle instanceof Any){
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
                atts.addAll(getGroupAttributes(namespaceURI, ng, stack));

            } else if (particle instanceof ExplicitGroup) {
                final ExplicitGroup eg = (ExplicitGroup) particle;
                atts.addAll(getGroupAttributes(namespaceURI, eg, stack));
            } else {
                throw new MismatchedFeatureException("Unexpected TYPE : "+particle);
            }
        }

        return atts;
    }

    private PropertyType elementToAttribute(String namespaceURI, Element element, BuildStack stack) {

        GenericName name = null;

        final QName refName = element.getRef();
        PropertyType refType = null;
        if (refName != null) {
            final Element parentElement = xsdContext.findGlobalElement(refName);
            if (parentElement == null) {
                throw new MismatchedFeatureException("unable to find referenced element : "+ refName);
            }
            refType = elementToAttribute(namespaceURI, parentElement, stack);
            name = refType.getName();
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
        if(minMax[0]==null) minMax[0] = 1;
        if(minMax[1]==null) minMax[1] = 1;


        final QName typeName = element.getType();
        if (typeName != null) {
            final PropertyType parentType = resolveType(typeName, stack);
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

        if (element.isAbstract()) {
            //create an abstract feature type with nothing in it
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(name);
            ftb.setAbstract(true);
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
            CharacteristicTypeBuilder cb = atb.getCharacteristic(GMLConvention.NILLABLE_PROPERTY.toString());
            if (cb == null) cb = atb.addCharacteristic(GMLConvention.NILLABLE_CHARACTERISTIC);
            cb.setDefaultValue(nillable);
            return atb.build();
        } else if (type instanceof FeatureAssociationRole) {
            final Map properties = Collections.singletonMap("name", name);
            try {
                return new DefaultAssociationRole(properties,
                    ((FeatureAssociationRole)type).getValueType(), minOcc, maxOcc);
            } catch (IllegalStateException ex) {
                return new DefaultAssociationRole(properties,
                        Features.getValueTypeName(type), minOcc, maxOcc);
            }
        } else {
            throw new UnsupportedOperationException("Unexpected type "+type);
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

        if (id!=null || name!=null) {
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
        if(ref!=null){
            final Attribute parentAtt = xsdContext.findGlobalAttribute(ref);
            if(parentAtt==null){
                throw new MismatchedFeatureException("The attribute : " + ref + " has not been found.");
            }
            return resolveAttributeValueName(parentAtt, stack);
        }

        //test local simple type
        final LocalSimpleType simpleType = att.getSimpleType();
        if(simpleType!=null){
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
        if(Utils.existPrimitiveType(name.getLocalPart())){
            final Class valueType = Utils.getTypeFromQName(name);
            final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
            atb.setName(NamesExt.create(name));
            atb.setValueClass(valueType);
            return atb.build();
        }

        //check if a simple type exist
        final SimpleType simpleType = xsdContext.findSimpleType(name);
        if(simpleType!=null){
            return toProperty(simpleType, stack);
        }else{
            //could be a complex type ... for a simple content, that's not an error. xsd/xml makes no sense at all sometimes
            final Object sct = getType(name, stack);
            final Map properties = Collections.singletonMap("name", NamesExt.create(name));
            if (sct==null) {
                throw new MismatchedFeatureException("Could not find type : "+name);
            } else if(sct instanceof GenericName) {
                return new DefaultAssociationRole(properties, (GenericName)sct, 1, 1);
            } else if(sct instanceof FeatureType) {
                return new DefaultAssociationRole(properties, (FeatureType)sct, 1, 1);
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
                    if(facet instanceof JAXBElement) {
                        String name = ((JAXBElement)facet).getName().getLocalPart();
                        facet = ((JAXBElement)facet).getValue();

                        if(facet instanceof NumFacet){
                            final NumFacet nf = (NumFacet) facet;
                            final int length = Integer.valueOf(nf.getValue());
                            if("maxLength".equalsIgnoreCase(name)){
                                atb.setMaximalLength(length);
                            }
                        }
                    } else if(facet instanceof Pattern) {
                        //TODO
                    }
                }

                return atb.build();
            }
        }

        // TODO union can be a collection of anything
        // collection ? array ? Object.class ? most exact type ?
        final Union union = simpleType.getUnion();
        if(union !=null){
            if(union.getMemberTypes()!=null && !union.getMemberTypes().isEmpty()){
                final QName name = union.getMemberTypes().get(0);
                final SimpleType refType = xsdContext.findSimpleType(name);
                if(refType==null){
                    throw new MismatchedFeatureException("Could not find type : "+name);
                }
                return toProperty(refType, stack);
            }else if(union.getSimpleType()!=null && !union.getSimpleType().isEmpty()){
                final LocalSimpleType st = union.getSimpleType().get(0);
                return toProperty(st, stack);
            }
        }

        //TODO list type
        final org.geotoolkit.xsd.xml.v2001.List list = simpleType.getList();
        if(list!=null){
            final QName subTypeName = list.getItemType();
            if(subTypeName!=null){
                final SimpleType refType = xsdContext.findSimpleType(subTypeName);
                if(refType!=null){
                    return toProperty(refType, stack);
                }
                return resolveType(subTypeName, stack);
            }
            final LocalSimpleType subtype = list.getSimpleType();
            if(subtype!=null){
                return toProperty(simpleType, stack);
            }
        }

        if(Utils.existPrimitiveType(simpleType.getName())){
            return resolveType(new QName(null, simpleType.getName()), stack);
        }else{
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
            final AttributeTypeBuilder atb = ftb.addAttribute((AttributeType)property);
            if (FeatureExt.getCharacteristicValue(property, FLAG_ID, false)){
                //special case, consider it as a property
                atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
                atb.characteristics().clear();
            }
        } else if (property instanceof FeatureAssociationRole) {
            final FeatureAssociationRole far = (FeatureAssociationRole) property;
            if (Features.getValueTypeName(far).tip().toString().endsWith("PropertyType")) {
                //this is an encapsulated property, we unroll it
                final FeatureType valueType = far.getValueType();
                final Collection<? extends PropertyType> subProps = valueType.getProperties(true);
                if (subProps.size()>=1) {
                    //we peek the first association, there should be only one
                    //but attributes are possible
                    for (PropertyType pt : subProps) {
                        if (pt instanceof FeatureAssociationRole) {
                            final FeatureAssociationRole subFar = (FeatureAssociationRole)pt;
                            ftb.addAssociation(subFar)
                                .setName(((FeatureAssociationRole) property).getName())
                                .setMinimumOccurs(((FeatureAssociationRole) property).getMinimumOccurs())
                                .setMaximumOccurs(((FeatureAssociationRole) property).getMaximumOccurs());
                            break;
                        }
                    }
                } else {
                    //ignore this property
                    //throw new MismatchedFeatureException("Unvalid property type, was expecting a single association property but was :\n"+valueType);
                }
            } else {
                ftb.addProperty(property);
            }
        } else {
            ftb.addProperty(property);
        }
    }

    private GenericName extractFinalName(String namespaceURI, ComplexType type) {
        String localName = type.getName();
        if(localName==null) localName = type.getId();

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

        final Set<QName> toResolve = new HashSet<>();

    }

}
