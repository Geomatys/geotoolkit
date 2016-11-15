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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.apache.sis.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.CharacteristicTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.util.ObjectConverters;

import org.geotoolkit.util.NamesExt;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.xml.AbstractConfigurable;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.LocalSimpleType;
import org.geotoolkit.xsd.xml.v2001.OpenAttrs;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.SimpleType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.opengis.util.GenericName;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.Any;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.geotoolkit.xsd.xml.v2001.AttributeGroup;
import org.geotoolkit.xsd.xml.v2001.AttributeGroupRef;
import org.geotoolkit.xsd.xml.v2001.ComplexRestrictionType;
import org.geotoolkit.xsd.xml.v2001.Group;
import org.geotoolkit.xsd.xml.v2001.GroupRef;
import org.geotoolkit.xsd.xml.v2001.NamedAttributeGroup;
import org.geotoolkit.xsd.xml.v2001.NamedGroup;
import org.geotoolkit.xsd.xml.v2001.Restriction;
import org.geotoolkit.xsd.xml.v2001.SimpleContent;
import org.geotoolkit.xsd.xml.v2001.SimpleRestrictionType;
import org.geotoolkit.xsd.xml.v2001.Union;
import org.opengis.feature.MismatchedFeatureException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.xml.GMLConvention;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.w3c.dom.Node;

/**
 * Reader class to convert an XSD to OGC Feature Type.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class JAXBFeatureTypeReaderOld extends AbstractConfigurable {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.feature.xml.jaxp");

    private static final String FLAG_ID = "isId";

    private final Map<QName,XSDFeatureType> typeCache = new HashMap<>();
    private final List<Entry<ComplexType,XSDFeatureType>> unfinished = new ArrayList<>();
    private final List<XSDFeatureType> uncompleted = new ArrayList<>();
    private final XSDSchemaContext xsdContext;

    private boolean skipStandardObjectProperties = false;

    public JAXBFeatureTypeReaderOld() {
        this(null);
    }

    /**
     *
     * @param locationMap xsd imports or resources are often online, this map allows to replace
     *      resources locations by new locations. It can be used to relocated toward a local file to
     *      use offline for example.
     */
    public JAXBFeatureTypeReaderOld(Map<String,String> locationMap) {
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

    /**
     * {@inheritDoc }
     */
    public List<FeatureType> read(final String xml) throws JAXBException {
        return read((Object)xml);
    }

    /**
     * {@inheritDoc }
     */
    public List<FeatureType> read(final InputStream in) throws JAXBException {
        return read((Object)in);
    }

    /**
     * {@inheritDoc }
     */
    public List<FeatureType> read(final URL url) throws JAXBException {
        return read((Object)url);
    }

    /**
     * {@inheritDoc }
     */
    public List<FeatureType> read(final Reader reader) throws JAXBException {
        return read((Object)reader);
    }

    /**
     * {@inheritDoc }
     */
    public List<FeatureType> read(final Node element) throws JAXBException {
        return read((Object)element);
    }

    /**
     * {@inheritDoc }
     */
    public FeatureType read(final String xml, final String name) throws JAXBException {
        return read((Object)xml,name);
    }

    /**
     * {@inheritDoc }
     */
    public FeatureType read(final InputStream in, final String name) throws JAXBException {
        return read((Object)in,name);
    }

    /**
     * {@inheritDoc }
     */
    public FeatureType read(final Reader reader, final String name) throws JAXBException {
        return read((Object)reader,name);
    }

    /**
     * {@inheritDoc }
     */
    public FeatureType read(final Node node, final String name) throws JAXBException {
        return read((Object)node,name);
    }

    public FeatureType read(final Object candidate, final String name) throws JAXBException {
        final Schema schema = xsdContext.read(candidate,name);
        return getFeatureTypeFromSchema(schema, name);
    }

    public List<FeatureType> read(final Object candidate) throws JAXBException {
        final Entry<Schema, String> entry = xsdContext.read(candidate);
        return getAllFeatureTypeFromSchema(entry.getKey(), entry.getValue());
    }

    public List<FeatureType> getAllFeatureTypeFromSchema(final Schema schema, final String baseLocation) throws MismatchedFeatureException {
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

        //resolve all types
        for (int i=0,n=result.size();i<n;i++) {
            result.set(i, ((XSDFeatureType)result.get(i)).builder.build());
        }

        return result;
    }

    private void listFeatureTypes(Schema schema, List<FeatureType> result) throws MismatchedFeatureException{

        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if(opAtts instanceof TopLevelElement){
                final TopLevelElement element = (TopLevelElement) opAtts;
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = xsdContext.findComplexType(typeName);

                    if (xsdContext.isFeatureType(type)) {
                        final XSDFeatureType ft = getType(typeName.getNamespaceURI(), type, null);
                        result.add(ft);

                        //if the type name is not the same as the element name, make a subtype
                        if(!ft.getName().tip().toString().equals(element.getName())){
                            final GenericName name = NamesExt.create(NamesExt.getNamespace(ft.getName()), element.getName());

                            final XSDFeatureType renamed = new XSDFeatureType();
                            renamed.builder.setName(name);
                            renamed.builder.setSuperTypes(ft);
                            uncompleted.add(renamed);
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

        //finish all substitution types
        for(Entry<ComplexType,XSDFeatureType> ct : unfinished){
            completeType(ct.getKey(), ct.getValue());
        }
        unfinished.clear();

        uncompleted.clear();

    }

    public XSDFeatureType getFeatureTypeFromSchema(final Schema schema, final String name) throws MismatchedFeatureException {
        final TopLevelElement element = schema.getElementByName(name);
        if (element != null) {
            final QName typeName = element.getType();
            if (typeName != null) {
                final ComplexType type = xsdContext.findComplexType(typeName);
                final XSDFeatureType ct = getType(typeName.getNamespaceURI(), type, null);
                return ct;
            } else {
                LOGGER.log(Level.WARNING, "the element:{0} has no type", name);
            }
        }
        return null;
    }

    public XSDFeatureType getComplexType(GenericName name) throws MismatchedFeatureException{
        return getType(new QName(NamesExt.getNamespace(name), name.tip().toString()));
    }

//    public PropertyDescriptor getElementType(GenericName name) throws MismatchedFeatureException{
//        final Element parentElement = findGlobalElement(new QName(NamesExt.getNamespace(name), name.tip().toString()));
//        return elementToAttribute(parentElement, NamesExt.getNamespace(name)).get(0);
//    }

    private XSDFeatureType getType(QName qname) throws MismatchedFeatureException{
        final XSDFeatureType ct = typeCache.get(qname);
        if(ct!=null) return ct;

        final ComplexType type = xsdContext.findComplexType(qname);

        if(type==null){
            throw new MismatchedFeatureException("Unable to find complex type for name : "+ qname);
        }else{
            return getType(qname.getNamespaceURI(), type, null);
        }
    }

    private XSDFeatureType getType(String namespace, ComplexType type, String elementName) throws MismatchedFeatureException{
        return getType(namespace, type, elementName, false);
    }

    private XSDFeatureType getType(String namespace, ComplexType type, String elementName, boolean delay) throws MismatchedFeatureException{
        String typeName = type.getName();
        if(typeName==null || typeName.isEmpty()) typeName = elementName;
        final QName qname = new QName(namespace, typeName);
        final XSDFeatureType ct = typeCache.get(qname);
        if(ct!=null && ct.isLock()) return ct;

        final XSDFeatureType finalType;

        if(ct!=null){
            finalType = ct;
        }else{
            final boolean isFeatureType = xsdContext.isFeatureType(type);

            String properName = qname.getLocalPart();

            //we remove the 'Type' extension for feature types.
            if (isFeatureType && properName.endsWith("Type")) {
                properName = properName.substring(0, properName.lastIndexOf("Type"));
            }
            final GenericName ftypeName = NamesExt.create(namespace, properName);
            finalType = new XSDFeatureType();
            finalType.builder.setName(ftypeName);
            finalType.builder.setAbstract(type.isAbstract());
            typeCache.put(qname, finalType);
        }

        if(delay){
            //this is a subsitution, we don't resolve it now otherwise it will cause
            //a loop and some inherited properties will be missing
            unfinished.add(new AbstractMap.SimpleImmutableEntry<>(type, finalType));
            return finalType;
        }

        completeType(type, finalType);

        return finalType;
    }

    private void completeType(ComplexType type, XSDFeatureType finalType) throws MismatchedFeatureException{

        if(finalType.isLock()) return;

        final String namespace = NamesExt.getNamespace(finalType.getName());

        //read attributes
        final List<Annotated> atts = type.getAttributeOrAttributeGroup();
        if(atts!=null){
            for(Annotated att : atts){
                if(att instanceof Attribute){
                    addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (Attribute) att));
                }else if(att instanceof AttributeGroupRef){
                    addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                }
            }
        }

        //read sequence properties
        addOrReplace(finalType.builder, getGroupAttributes(namespace, type.getSequence()));

        boolean uncomplete = false;

        //read complex content if defined
        final ComplexContent content = type.getComplexContent();
        ExtensionType ext = null;
        if (content != null) {
            ext = content.getExtension();
            if (ext != null) {
                final QName base = ext.getBase();
                if(base!=null && !base.getLocalPart().equalsIgnoreCase("anytype")){
                    final XSDFeatureType parent = getType(base);
                    if(parent!=null){
                        if(!parent.isLock()){
                            uncomplete = true;
                        }

                        
                        //if(!Utils.GML_FEATURE_TYPES.contains(parent.getName())){
                            finalType.builder.setSuperTypes(parent);
                            //erase parent properties which are not identifier
                            //TODO do we exclude gml properties ?
                        //}
                    }
                }

                //read attributes
                final List<Annotated> attexts = ext.getAttributeOrAttributeGroup();
                if(attexts!=null){
                    for(Annotated att : attexts){
                        if(att instanceof Attribute){
                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (Attribute) att));
                        }else if(att instanceof AttributeGroupRef){
                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                        }
                    }
                }

                //sequence attributes
                addOrReplace(finalType.builder, getGroupAttributes(namespace, ext.getSequence()));
            }

            // restrictions
            ComplexRestrictionType restriction = content.getRestriction();
            if(restriction!=null){
//                final QName base = restriction.getBase();
//                if(base!=null && !base.getLocalPart().equalsIgnoreCase("anytype")){
//                    final org.geotoolkit.feature.type.ComplexType parent = getType(base);
//                    if(parent!=null){
//                        addOrReplace(finalType.getDescriptors(), parent.getDescriptors());
//                        if(!Utils.GML_FEATURE_TYPES.contains(parent.getName())){
//                            finalType.changeParent(parent);
//                        }
//                    }
//                }

                addOrReplace(finalType.builder, getGroupAttributes(namespace, restriction.getSequence()));
            }
        }

        //read simple content type if defined
        final SimpleContent simpleContent = type.getSimpleContent();
        if(simpleContent!=null){
            final ExtensionType sext = simpleContent.getExtension();
            final SimpleRestrictionType restriction = simpleContent.getRestriction();

            if(sext!=null){
                //simple type base, it must be : this is the content of the tag <tag>XXX<tag>
                //it is not named, so we call it value
                final QName base = sext.getBase();
                final PropertyType st = resolveSimpleType(base);

                if(st instanceof FeatureAssociationRole){
                    addOrReplace(finalType.builder, st);
                }else{
                    finalType.builder.addAttribute((AttributeType) st);
                }

                //read attributes
                final List<Annotated> attexts = sext.getAttributeOrAttributeGroup();
                if(attexts!=null){
                    for(Annotated att : attexts){
                        if(att instanceof Attribute){
                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (Attribute) att));
                        }else if(att instanceof AttributeGroupRef){
                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                        }
                    }
                }
            }

//            if(restriction!=null){
//
//                final QName base = restriction.getBase();
//                if(base !=null){
//                    final ComplexType sct = xsdContext.findComplexType(base);
//                    if(sct!=null){
//                        final XSDFeatureType tct = getType(namespace, sct, null);
//                        addOrReplace(finalType.builder, tct.getProperties(true));
//                    }else{
//                        final PropertyType restType = resolveSimpleType(base);
//                        addOrReplace(finalType.builder, atb.create(restType, NamesExt.create(namespace, Utils.VALUE_PROPERTY_NAME), 0, 1, false, null));
//                    }
//                }
//
//
//                //read attributes
//                final List<Annotated> attexts = restriction.getAttributeOrAttributeGroup();
//                if(attexts!=null){
//                    for(Annotated att : attexts){
//                        if(att instanceof Attribute){
//                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (Attribute) att));
//                        }else if(att instanceof AttributeGroupRef){
//                            addOrReplace(finalType.builder, getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
//                        }
//                    }
//                }
//            }

        }

        //read choice if set
        final ExplicitGroup choice = type.getChoice();
        if(choice != null){
            final Integer minOccurs = choice.getMinOccurs();
            final String maxOccurs = choice.getMaxOccurs();
            final List<PropertyType> choices = getGroupAttributes(namespace, choice);
            for(PropertyType pd : choices){
                //change the min/max occurences
                int maxOcc = 1;
                if("unbounded".equalsIgnoreCase(maxOccurs)) {
                    maxOcc = Integer.MAX_VALUE;
                } else if(maxOccurs!=null){
                    maxOcc = Integer.parseInt(maxOccurs);
                }
                //NOTE : a choice with max occurence ? yet we must consider the limitation
                //of each element
                
                final PropertyType rpd;
                if(pd instanceof Operation){
//                    final OperationDescriptor od = (OperationDescriptor) pd;
//                    rpd =  new DefaultOperationDescriptor(od.getType(), od.getName(), 0, maxOcc, od.isNillable());
                    throw new MismatchedFeatureException("Operation in choices not supported yet.");
                }else if(pd instanceof FeatureAssociationRole){
                    final FeatureAssociationRole ref = (FeatureAssociationRole) pd;
                    final Map properties = Collections.singletonMap("name", ref.getName());
                    final FeatureAssociationRole asso = new DefaultAssociationRole(properties, ref.getValueType(), 0, maxOcc);
                    rpd = asso;
                }else{
                    final SingleAttributeTypeBuilder adb = new SingleAttributeTypeBuilder();
                    adb.copy((AttributeType) pd);
                    adb.setMinimumOccurs(0);
                    adb.setMaximumOccurs(maxOcc);
                    rpd = adb.build();
                }
                
                addOrReplace(finalType.builder, rpd);
            }
        }


//        removeAttributes(finalType.builder, Utils.GML_ABSTRACT_FEATURE_PROPERTIES);
//
//        //remove standard object properties if requested
//        if(skipStandardObjectProperties){
//            removeAttributes(finalType.builder, Utils.GML_STANDARD_OBJECT_PROPERTIES);
//        }

        if(!uncomplete){
            //finalType.lock();
        }else{
            uncompleted.add(finalType);
        }
    }



//    private static void removeAttributes(FeatureTypeBuilder type, Set<GenericName> propNames){
//        final List<PropertyDescriptor> descs = type.getDescriptors();
//        for(int i=descs.size()-1;i>=0;i--){
//            if(propNames.contains(descs.get(i).getName())){
//                descs.remove(i);
//            }
//        }
//    }

    private List<PropertyType> getGroupAttributes(String namespace, Group group) throws MismatchedFeatureException {
        if(group==null) return Collections.EMPTY_LIST;

        final List<PropertyType> atts = new ArrayList<>();

        final List<Object> particles = group.getParticle();
        for(Object particle : particles){
            if(particle instanceof JAXBElement){
                particle = ((JAXBElement)particle).getValue();
            }

            if(particle instanceof Element){
                final Element ele = (Element) particle;
                final List<PropertyType> att = elementToAttribute(ele, namespace);
                if(att!=null)atts.addAll(att);

            }else if(particle instanceof Any){
                final Any ele = (Any) particle;
                final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                atb.setName(namespace, Utils.ANY_PROPERTY_NAME);
                atb.setValueClass(Object.class);
                copyMinMax(ele, atb);
                atts.add(atb.build());

            }else if(particle instanceof GroupRef){
                final GroupRef ref = (GroupRef) particle;
                final QName groupRef = ref.getRef();
                final NamedGroup ng = xsdContext.findGlobalGroup(groupRef);
                atts.addAll(getGroupAttributes(namespace, ng));

            }else if(particle instanceof ExplicitGroup){
                final ExplicitGroup eg = (ExplicitGroup) particle;
                atts.addAll(getGroupAttributes(namespace, eg));
            }else{
                throw new MismatchedFeatureException("Unexpected TYPE : "+particle);
            }
        }

        return atts;
    }

    private AttributeType getAnnotatedAttributes(String namespace, final Attribute att) throws MismatchedFeatureException{
        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
        atb.reset();

        if(att.getRef()!=null){
            namespace = att.getRef().getNamespaceURI();
            //copy properties from parent
            final Attribute attRef = xsdContext.findGlobalAttribute(att.getRef());
            final AttributeType atDesc = getAnnotatedAttributes(namespace, attRef);
            atb.copy(atDesc);
            atb.setName(namespace, atDesc.getName().tip().toString());
        } else {
            namespace = null;
        }

        final String id = att.getId();
        final String name = att.getName();
        final String def = att.getDefault();
        final AttributeType type = (AttributeType) resolveAttributeValueName(att);
        final String use = att.getUse();

        if(id!=null || name!=null){
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
        atb.addCharacteristic(GMLConvention.NILLABLE_PROPERTY, Boolean.class, 0, 1, Boolean.FALSE);
        atb.setValueClass(type.getValueClass());

        if(def!=null && !def.isEmpty()){
            final Object defVal = ObjectConverters.convert(def, type.getValueClass());
            atb.setDefaultValue(defVal);
        }

        return atb.build();
    }

    private List<PropertyType> getAnnotatedAttributes(final String namespace, final AttributeGroup group) throws MismatchedFeatureException{
        final List<PropertyType> descs = new ArrayList<>();
        final List<Annotated> atts = group.getAttributeOrAttributeGroup();
        if(atts!=null){
            for(Annotated att : atts){
                if(att instanceof Attribute){
                    addOrReplace(descs, getAnnotatedAttributes(namespace, (Attribute) att));
                }else if(att instanceof AttributeGroupRef){
                    addOrReplace(descs, getAnnotatedAttributes(namespace, (AttributeGroupRef)att));
                }
            }
        }
        final QName ref = group.getRef();
        if(ref!=null){
            final NamedAttributeGroup refGroup = xsdContext.findAttributeGroup(ref);
            addOrReplace(descs, getAnnotatedAttributes(namespace, refGroup));
        }
        return descs;
    }

    private List<PropertyType> elementToAttribute(final Element attributeElement, final String namespace) throws MismatchedFeatureException {
        return elementToAttribute(attributeElement, namespace, false);
    }

    /**
     * Convert an Element to a AttributeDescriptor
     * Returns a list of AttributeDescriptors, other descriptors are substitution groups.
     *
     *
     * @param attributeElement
     * @param namespace
     * @return
     * @throws SchemaException
     */
    private List<PropertyType> elementToAttribute(final Element attributeElement, final String namespace, boolean isSubstitute) throws MismatchedFeatureException {
        final List<PropertyType> results = new ArrayList<>();

        //search for the parent description
        GenericName name = null;
        PropertyType parentDesc = null;
        if (attributeElement.getRef() != null) {
            final Element parentElement = xsdContext.findGlobalElement(attributeElement.getRef());
            if (parentElement == null) {
                throw new MismatchedFeatureException("unable to find referenced element : "+ attributeElement.getRef());
            }
            final List<PropertyType> parentAtt = elementToAttribute(parentElement, namespace);
            for(int i=1,n=parentAtt.size();i<n;i++){
                //substitution groups
                results.add(parentAtt.get(i));
            }

            parentDesc = parentAtt.get(0);
            name = parentDesc.getName();
        }

        final String elementName = attributeElement.getName();
        if(elementName!=null){
            name = NamesExt.create(namespace, elementName);
        }

        final Integer[] minMax = getMinMax(attributeElement);
        if(minMax[0]==null) minMax[0] = 1;
        if(minMax[1]==null) minMax[1] = 1;


        //try to extract complex type
        final PropertyType baseDesc;
        if(attributeElement.getComplexType()!=null){
            final FeatureType type = getType(namespace, attributeElement.getComplexType(),attributeElement.getName(),isSubstitute);

            final Map properties = Collections.singletonMap("name", name);
            final FeatureAssociationRole asso = new DefaultAssociationRole(properties, type, minMax[0], minMax[1]);
            baseDesc = asso;
        }else{
            //Simple type
            QName elementType = attributeElement.getType();
            // Try to extract base from a SimpleType
            if (elementType == null && attributeElement.getSimpleType() != null) {
                final LocalSimpleType simpleType = attributeElement.getSimpleType();
                if (simpleType.getRestriction() != null) {
                    elementType = simpleType.getRestriction().getBase();
                }
            }

            if (elementType != null) {
                if(Utils.isPrimitiveType(elementType)){
                    final Class c = Utils.getTypeFromQName(elementType);
                    if (c == null) {
                        throw new MismatchedFeatureException("The attribute : " + attributeElement + " does no have a declared type.");
                    }
                    final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                    atb.setMinimumOccurs(minMax[0]);
                    atb.setMaximumOccurs(minMax[1]);
                    atb.setValueClass(c);
                    atb.setName(name);
                    baseDesc = atb.build();
                }else{
                    final PropertyType pt = resolveSimpleType(elementType);
                    if(pt instanceof AttributeType){
                        final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                        atb.setMinimumOccurs(minMax[0]);
                        atb.setMaximumOccurs(minMax[1]);
                        atb.setName(name);
                        atb.setDefaultValue(((AttributeType)pt).getDefaultValue());
                        atb.setValueClass(((AttributeType)pt).getValueClass());
                        baseDesc = atb.build();
                    }else{
                        final FeatureAssociationRole ref = (FeatureAssociationRole) pt;
                        final Map properties = Collections.singletonMap("name", name);
                        final FeatureAssociationRole asso = new DefaultAssociationRole(properties, ref.getValueType(), minMax[0], minMax[1]);
                        baseDesc = asso;
                    }
                }
            }else if(parentDesc instanceof FeatureAssociationRole){
                final Map properties = Collections.singletonMap("name", name);
                final FeatureAssociationRole asso = new DefaultAssociationRole(properties,
                        ((FeatureAssociationRole)parentDesc).getValueType(), minMax[0], minMax[1]);
                baseDesc = asso;
            }else if(parentDesc instanceof AttributeType){
                final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                atb.copy((AttributeType) parentDesc);
                atb.setName(name);
                atb.setMinimumOccurs(minMax[0]);
                atb.setMaximumOccurs(minMax[1]);
                baseDesc = atb.build();
            }else{
                final SingleAttributeTypeBuilder atb = new SingleAttributeTypeBuilder();
                atb.setName(name);
                atb.setMinimumOccurs(minMax[0]);
                atb.setMaximumOccurs(minMax[1]);
                atb.setValueClass(Object.class);
                baseDesc = atb.build();
            }
        }

        results.add(baseDesc);

        //check for substitutions
//        if(elementName!=null && !isSubstitute){
//            final Collection<QName> substitutions = xsdContext.getSubstitutions(new QName(namespace, elementName));
//            if(substitutions!=null && !substitutions.isEmpty()){
//                for(QName sub : substitutions){
//                    final Element subEle = xsdContext.findGlobalElement(sub);
//                    final List<PropertyType> subs = elementToAttribute(subEle,sub.getNamespaceURI(),true);
//                    //create an alias operator for each of them
//                    for(PropertyType ad : subs){
//                        if(ad instanceof Operation){
//                            throw new UnsupportedOperationException("Substitution is an operation, not supported.");
//                        }else{
//                            final OperationType optype = new AliasOperation(ad.getName(), baseDesc.getName(), ad);
//                            final OperationDescriptor desc = new DefaultOperationDescriptor(optype,
//                                    ad.getName(), baseDesc.getMinOccurs(), baseDesc.getMaxOccurs(), baseDesc.isNillable());
//                            results.add(desc);
//                        }
//                    }
//                }
//            }
//        }

        return results;
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

    private static void copyMinMax(Any attributeElement, SingleAttributeTypeBuilder adb) {
        //override properties which are defined
        final BigInteger minAtt = attributeElement.getMinOccurs();
        if(minAtt!=null){
            adb.setMinimumOccurs(minAtt.intValue());
        }

        final String maxxAtt = attributeElement.getMaxOccurs();
        if("unbounded".equalsIgnoreCase(maxxAtt)) {
            adb.setMaximumOccurs(Integer.MAX_VALUE);
        } else if(maxxAtt!=null){
            adb.setMaximumOccurs(Integer.parseInt(maxxAtt));
        }
    }

    private PropertyType resolveAttributeValueName(Attribute att) throws MismatchedFeatureException{
        //test direct type
        final QName type = att.getType();
        if(type!=null){
            return resolveSimpleType(type);
        }

        //test reference
        final QName ref = att.getRef();
        if(ref!=null){
            final Attribute parentAtt = xsdContext.findGlobalAttribute(ref);
            if(parentAtt==null){
                throw new MismatchedFeatureException("The attribute : " + ref + " has not been found.");
            }
            return resolveAttributeValueName(parentAtt);
        }

        //test local simple type
        final LocalSimpleType simpleType = att.getSimpleType();
        if(simpleType!=null){
            return resolveSimpleType(simpleType);
        }

        return null;
    }

    private PropertyType resolveSimpleType(QName name) throws MismatchedFeatureException{

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
            return resolveSimpleType(simpleType);
        }else{
            //could be a complex type ... for a simple content, that's not an error. xsd/xml makes no sense at all sometimes
            final XSDFeatureType sct = getType(name);
            if(sct==null){
                throw new MismatchedFeatureException("Could not find type : "+name);
            }
            final Map properties = Collections.singletonMap("name", NamesExt.create(name));
            return new DefaultAssociationRole(properties, sct, 1, 1);
        }
    }

    private PropertyType resolveSimpleType(SimpleType simpleType) throws MismatchedFeatureException{
        final Restriction restriction = simpleType.getRestriction();
        if(restriction!=null){
            QName base = restriction.getBase();
            if(base!=null){
                return resolveSimpleType(base);
            }
            final LocalSimpleType localSimpleType = restriction.getSimpleType();
            if(localSimpleType!=null){
                return resolveSimpleType(localSimpleType);
            }

            return null;
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
                return resolveSimpleType(refType);
            }else if(union.getSimpleType()!=null && !union.getSimpleType().isEmpty()){
                final LocalSimpleType st = union.getSimpleType().get(0);
                return resolveSimpleType(st);
            }
        }

        //TODO list type
        final org.geotoolkit.xsd.xml.v2001.List list = simpleType.getList();
        if(list!=null){
            final QName subTypeName = list.getItemType();
            if(subTypeName!=null){
                final SimpleType refType = xsdContext.findSimpleType(subTypeName);
                if(refType!=null){
                    return resolveSimpleType(refType);
                }
                return resolveSimpleType(subTypeName);
            }
            final LocalSimpleType subtype = list.getSimpleType();
            if(subtype!=null){
                return resolveSimpleType(simpleType);
            }
        }

        if(Utils.existPrimitiveType(simpleType.getName())){
            return resolveSimpleType(new QName(null, simpleType.getName()));
        }else{
            return null;
        }
    }

    private static void addOrReplace(FeatureTypeBuilder descs, PropertyType pd){
        addOrReplace(descs, Collections.singleton(pd));
    }

    private static void addOrReplace(FeatureTypeBuilder descs, Collection<? extends PropertyType> toAdd){
        for(PropertyType pd : toAdd){
            List<PropertyTypeBuilder> properties = descs.properties();
            loop:
            for(int i=0;i<properties.size();i++){
                if(properties.get(i).getName().equals(pd.getName())){
                    //remove existing property
                    properties.remove(i);
                    break loop;
                }
            }
            //add new property
            if(pd instanceof AttributeType){
                final AttributeTypeBuilder<?> atb = descs.addAttribute((AttributeType) pd);

                //copy id information
                if(FeatureExt.getCharacteristicValue(pd, FLAG_ID, Boolean.FALSE)){
                    atb.addRole(AttributeRole.IDENTIFIER_COMPONENT);
                    for(CharacteristicTypeBuilder<?> ctb : atb.characteristics()){
                        if(ctb.getName().tip().toString().equals(FLAG_ID)){
                            atb.characteristics().remove(ctb);
                            break;
                        }
                    }
                }

            }else if(pd instanceof FeatureAssociationRole){
                descs.addAssociation((FeatureAssociationRole) pd);
            }else if(pd instanceof Operation){
                throw new RuntimeException("Operation type not supported yet.");
            }
            
        }
    }

    private static void addOrReplace(List<PropertyType> descs, PropertyType pd){
        addOrReplace(descs, Collections.singleton(pd));
    }

    private static void addOrReplace(List<PropertyType> descs, Collection<? extends PropertyType> toAdd){
        loop:
        for(PropertyType pd : toAdd){
            for(int i=0;i<descs.size();i++){
                if(descs.get(i).getName().equals(pd.getName())){
                    //replace existing property
                    descs.set(i, pd);
                    continue loop;
                }
            }
            //add new property
            descs.add(pd);
        }
    }
}
