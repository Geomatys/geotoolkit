/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.feature.xml.jaxb;

import com.vividsolutions.jts.geom.Geometry;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.xml.AbstractConfigurable;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.Import;
import org.geotoolkit.xsd.xml.v2001.Include;
import org.geotoolkit.xsd.xml.v2001.LocalSimpleType;
import org.geotoolkit.xsd.xml.v2001.OpenAttrs;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.SimpleType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.geotoolkit.xsd.xml.v2001.XSDMarshallerPool;

import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.xsd.xml.v2001.Group;
import org.geotoolkit.xsd.xml.v2001.GroupRef;
import org.geotoolkit.xsd.xml.v2001.NamedGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Node;

/**
 * Reader class to convert an XSD to OGC Feature Type.
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JAXBFeatureTypeReader extends AbstractConfigurable implements XmlFeatureTypeReader {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();

    private final Map<String, Schema> knownSchemas = new HashMap<>();
    private final Map<QName, Element> knownElements = new HashMap<>();
    private final Map<QName, NamedGroup> knownGroups = new HashMap<>();
    private final Map<String,String> locationMap = new HashMap<>();

    private static final List<String> EXCLUDED_SCHEMA = new ArrayList<>();
    static {
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.2.1/base/gml.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");
    }

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
        if(locationMap!=null){
            this.locationMap.putAll(locationMap);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final String xml) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema   = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            final String location = null;
            return getAllFeatureTypeFromSchema(schema, location);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final InputStream in) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema   = (Schema) unmarshaller.unmarshal(in);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            final String location = null;
            return getAllFeatureTypeFromSchema(schema, location);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final URL url) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema   = (Schema) unmarshaller.unmarshal(url.openStream());
            POOL.recycle(unmarshaller);

            // we build the base url to retrieve imported xsd;
            final String location = url.toString();
            knownSchemas.put(location, schema);
            if (!EXCLUDED_SCHEMA.contains(location)) {
                final String baseLocation;
                if (location.lastIndexOf('/') != -1) {
                    baseLocation = location.substring(0, location.lastIndexOf('/') + 1);
                } else {
                    baseLocation = location;
                }
                return getAllFeatureTypeFromSchema(schema, baseLocation);
            } else {
                return new ArrayList<FeatureType>();
            }
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } catch (IOException ex) {
            throw new JAXBException(ex);
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Reader reader) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema  = (Schema) unmarshaller.unmarshal(reader);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            final String location = null;
            return getAllFeatureTypeFromSchema(schema, location);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Node element) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema  = (Schema) unmarshaller.unmarshal(element);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            final String location = null;
            return getAllFeatureTypeFromSchema(schema, location);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final String xml, final String name) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final InputStream in, final String name) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(in);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Reader reader, final String name) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(reader);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Node node, final String name) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(node);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        }
    }

    public List<FeatureType> getAllFeatureTypeFromSchema(final Schema schema, final String baseLocation) throws SchemaException {
        final List<FeatureType> result = new ArrayList<FeatureType>();

        // first we look for imported xsd
        for (OpenAttrs attr: schema.getIncludeOrImportOrRedefine()) {
            if (attr instanceof Import || attr instanceof Include) {
                final String schemalocation = Utils.getIncludedLocation(baseLocation, attr);
                if (schemalocation != null && !knownSchemas.containsKey(schemalocation) && !EXCLUDED_SCHEMA.contains(schemalocation)) {
                    //check for a relocation
                    final String relocation = locationMap.get(schemalocation);
                    final Schema importedSchema;
                    if(relocation!=null){
                        importedSchema = Utils.getDistantSchema(relocation);
                    }else{
                        importedSchema = Utils.getDistantSchema(schemalocation);
                    }
                    
                    if (importedSchema != null) {
                        knownSchemas.put(schemalocation, importedSchema);
                        final String newBaseLocation = getNewBaseLocation(schemalocation, baseLocation);
                        result.addAll(getAllFeatureTypeFromSchema(importedSchema, newBaseLocation));
                    } else {
                        LOGGER.log(Level.WARNING, "Unable to retrieve imported schema:{0}", schemalocation);
                    }
                }
            }
        }

        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if(opAtts instanceof TopLevelElement){
                final TopLevelElement element = (TopLevelElement) opAtts;
                knownElements.put(new QName(schema.getTargetNamespace(), element.getName()), element);
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = findComplexType(typeName.getLocalPart());

                    //loop on parent types until we find a Feature type
                    boolean isFeature = false;
                    ComplexType search = type;
                    while(search!=null){
                        isFeature = search.extendFeature();
                        if(isFeature) break;
                        if(search.getComplexContent()==null || search.getComplexContent().getExtension()==null) break;
                        final QName base = search.getComplexContent().getExtension().getBase();
                        search = findComplexType(base.getLocalPart());
                    }

                    if (isFeature) {
                        result.add(getFeatureTypeFromSchema(element.getName(), type, typeName.getNamespaceURI()));

                    } else if (type == null && findSimpleType(typeName.getLocalPart()) == null) {
                        LOGGER.log(Level.WARNING, "Unable to find a the declaration of type {0} in schemas.", typeName.getLocalPart());
                        continue;
                    }
                } else {
                    LOGGER.log(Level.WARNING, "null typeName for element:{0}", element.getName());
                }
            }else if(opAtts instanceof NamedGroup){
                final NamedGroup group = (NamedGroup) opAtts;
                final String name = group.getName();
                final QName qname = new QName(schema.getTargetNamespace(), name);
                knownGroups.put(qname, group);
            }
            
        }
        return result;
    }


    public FeatureType getFeatureTypeFromSchema(final Schema schema, final String name) throws SchemaException {
        final TopLevelElement element = schema.getElementByName(name);
        if (element != null) {
            final QName typeName = element.getType();
            if (typeName != null) {
                final ComplexType type = findComplexType(typeName.getLocalPart());
                return getFeatureTypeFromSchema(name, type, typeName.getNamespaceURI());
            } else {
                LOGGER.log(Level.WARNING, "the element:{0} has no type", name);
            }
        }
        return null;
    }

    private FeatureType getFeatureTypeFromSchema(final String name, final ComplexType type, final String namespace) throws SchemaException {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        if (type != null) {
            builder.setName(new DefaultName(namespace, name));
            final ComplexContent content = type.getComplexContent();
            if (content != null) {
                final ExtensionType ext = content.getExtension();
                if (ext != null) {
                    // TODO handle base
                    builder.getProperties().addAll(getGroupAttributes(namespace, ext.getSequence()));
                }
            }
            return builder.buildFeatureType();
        }
        LOGGER.log(Level.WARNING, "no declared type for:{0}", name);
        return null;
    }

    private org.geotoolkit.feature.type.ComplexType getComplexTypeFromSchema(final String namespace, final String name) throws SchemaException {

        //search for a schema with given namespace
        for(Schema schema : knownSchemas.values()){
            if(!schema.getTargetNamespace().equalsIgnoreCase(namespace)) continue;
            final ComplexType complexType = schema.getComplexTypeByName(name);
            if(complexType==null) continue;

            final FeatureTypeBuilder builder = new FeatureTypeBuilder();
            final String properName;
            if (name.endsWith("Type")) {
                properName = name.substring(0, name.lastIndexOf("Type"));
            } else {
                properName = name;
            }
            builder.setName(new DefaultName(namespace, properName));
            builder.getProperties().addAll(getGroupAttributes(namespace, complexType.getSequence()));

            return builder.buildType();
        }

        LOGGER.log(Level.WARNING, "Unable to find complex type for:{0}", name);
        return null;
    }

    private List<AttributeDescriptor> getGroupAttributes(String namespace, Group group) throws SchemaException {
        if(group==null) return Collections.EMPTY_LIST;

        final List<AttributeDescriptor> atts = new ArrayList<>();

        final List<Object> particles = group.getParticle();
        for(Object particle : particles){
            if(particle instanceof JAXBElement){
                particle = ((JAXBElement)particle).getValue();
            }

            if(particle instanceof Element){
                final AttributeDescriptor att = elementToAttribute((Element)particle, namespace);
                if(att!=null)atts.add(att);
                
            }else if(particle instanceof GroupRef){
                final GroupRef ref = (GroupRef) particle;
                final QName groupRef = ref.getRef();
                final NamedGroup ng = knownGroups.get(groupRef);
                atts.addAll(getGroupAttributes(namespace, ng));

            }else if(particle instanceof ExplicitGroup){
                final ExplicitGroup eg = (ExplicitGroup) particle;
                atts.addAll(getGroupAttributes(namespace, eg));
            }
        }

        return atts;
    }

    private AttributeDescriptor elementToAttribute(final Element attributeElement, final String namespace) throws SchemaException {

        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        final Element currentElement;
        if (attributeElement.getRef() != null ) {
            currentElement = knownElements.get(attributeElement.getRef());
            if (currentElement == null) {
                LOGGER.log(Level.WARNING, "unable to find referenced element:{0}", attributeElement.getRef());
                return null;
            }
        } else {
            currentElement = attributeElement;
        }
        QName elementType = currentElement.getType();
        // Try to extract base from a SimpleType
        if (elementType == null && currentElement.getSimpleType() != null) {
            final LocalSimpleType simpleType = currentElement.getSimpleType();
            if (simpleType.getRestriction() != null) {
                elementType = simpleType.getRestriction().getBase();
            }
        }
        if (elementType == null) {
            LOGGER.log(Level.WARNING, "unable to find the type of element:{0}", currentElement.getName());
            return null;
        }
        final String typeName    = elementType.getLocalPart();
        final String elementName = currentElement.getName();
        final Integer minAtt     = currentElement.getMinOccurs();
        final String maxxAtt     = currentElement.getMaxOccurs();
        final boolean nillable   = currentElement.isNillable();
        final int min = (minAtt == null) ? 1 : minAtt;
        final int max;
        if (maxxAtt == null) {
            max = 1;
        } else if (maxxAtt.equalsIgnoreCase("unbounded")) {
            max = Integer.MAX_VALUE;
        } else {
            max = Integer.parseInt(maxxAtt);
        }
        knownElements.put(new QName(namespace, elementName), currentElement);
        CoordinateReferenceSystem crs = null;
        if ((typeName.endsWith("PropertyType") || typeName.endsWith("Type")) && !Utils.isGeometricType(elementType)) {

            final String cname;
            if (typeName.endsWith("PropertyType")) {
                cname = typeName.substring(0, typeName.lastIndexOf("PropertyType")) + "Type";
            } else {
                cname = typeName;
            }
            final org.geotoolkit.feature.type.ComplexType cType = getComplexTypeFromSchema(elementType.getNamespaceURI(), cname);
            if (cType != null) {
                final AttributeDescriptor desc = adb.create(cType, new DefaultName(namespace, elementName), null, min, max, nillable, null);
                return desc;
            }

        } else {
            final Class c = Utils.getTypeFromQName(elementType);
            if (c == null) {
                throw new SchemaException("The attribute:" + elementName + " does no have a declared type.");
            }
            if (Geometry.class.isAssignableFrom(c) || org.opengis.geometry.Geometry.class.isAssignableFrom(c)) {
                final AttributeDescriptor desc = adb.create(new DefaultName(namespace, elementName), c, crs, min, max, nillable, null);
                return desc;
            } else {
                final AttributeDescriptor desc = adb.create(new DefaultName(namespace, elementName), c, min, max, nillable, null);
                return desc;
            }
        }

        return null;
    }

    private String getNewBaseLocation(final String schemalocation, final String oldBaseLocation) {
        final String newBaseLocation;
        if (schemalocation.lastIndexOf('/') != -1) {
            newBaseLocation = schemalocation.substring(0, schemalocation.lastIndexOf('/') + 1);
        } else {
            newBaseLocation = oldBaseLocation;
        }
        return newBaseLocation;
    }

    private ComplexType findComplexType(final String typeName) {
        for (Schema schema : knownSchemas.values()) {
            final ComplexType type = schema.getComplexTypeByName(typeName);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    private SimpleType findSimpleType(final String typeName) {

        // look in the schemas
        for (Schema schema : knownSchemas.values()) {
            final SimpleType type = schema.getSimpleTypeByName(typeName);
            if (type != null) {
                return type;
            }
        }
        // look in primitive types
        if (Utils.existPrimitiveType(typeName)) {
            return new LocalSimpleType(typeName);
        }
        return null;
    }
}
