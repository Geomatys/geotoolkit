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

import com.vividsolutions.jts.geom.Geometry;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.geotoolkit.feature.type.BasicFeatureTypes;
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
import org.geotoolkit.feature.type.ModifiableFeatureTypeFactory;
import org.geotoolkit.feature.type.ModifiableFeaturetype;
import org.geotoolkit.feature.type.ModifiableType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.xsd.xml.v2001.Group;
import org.geotoolkit.xsd.xml.v2001.GroupRef;
import org.geotoolkit.xsd.xml.v2001.NamedGroup;
import org.geotoolkit.xsd.xml.v2001.Restriction;
import org.geotoolkit.xsd.xml.v2001.Union;
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

    private static final List<String> EXCLUDED_SCHEMA = new ArrayList<>();
    static {
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.2.1/base/gml.xsd");
        EXCLUDED_SCHEMA.add("http://schemas.opengis.net/gml/3.2.1/base/feature.xsd");
    }

    /**
     * default relocations
     * used by all reader instances
     */
    private static final Map<String,String> RELOCATIONS = new HashMap<>();
    static {

        //GML 3.2.1
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/gml.xsd","/xsd/gml/3.2.1/gml.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/dynamicFeature.xsd","/xsd/gml/3.2.1/dynamicFeature.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/feature.xsd","/xsd/gml/3.2.1/feature.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/geometryAggregates.xsd","/xsd/gml/3.2.1/geometryAggregates.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/geometryPrimitives.xsd","/xsd/gml/3.2.1/geometryPrimitives.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/geometryBasic2d.xsd","/xsd/gml/3.2.1/geometryBasic2d.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/geometryBasic0d1d.xsd","/xsd/gml/3.2.1/geometryBasic0d1d.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/measures.xsd","/xsd/gml/3.2.1/measures.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/units.xsd","/xsd/gml/3.2.1/units.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/dictionary.xsd","/xsd/gml/3.2.1/dictionary.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/gmlBase.xsd","/xsd/gml/3.2.1/gmlBase.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/basicTypes.xsd","/xsd/gml/3.2.1/basicTypes.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/temporal.xsd","/xsd/gml/3.2.1/temporal.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/direction.xsd","/xsd/gml/3.2.1/direction.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/topology.xsd","/xsd/gml/3.2.1/topology.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/geometryComplexes.xsd","/xsd/gml/3.2.1/geometryComplexes.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/coverage.xsd","/xsd/gml/3.2.1/coverage.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/valueObjects.xsd","/xsd/gml/3.2.1/valueObjects.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/grids.xsd","/xsd/gml/3.2.1/grids.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/coordinateReferenceSystems.xsd","/xsd/gml/3.2.1/coordinateReferenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/coordinateSystems.xsd","/xsd/gml/3.2.1/coordinateSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/referenceSystems.xsd","/xsd/gml/3.2.1/referenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/datums.xsd","/xsd/gml/3.2.1/datums.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/coordinateOperations.xsd","/xsd/gml/3.2.1/coordinateOperations.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/observation.xsd","/xsd/gml/3.2.1/observation.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/temporalReferenceSystems.xsd","/xsd/gml/3.2.1/temporalReferenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/temporalTopology.xsd","/xsd/gml/3.2.1/temporalTopology.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.2.1/deprecatedTypes.xsd","/xsd/gml/3.2.1/deprecatedTypes.xsd");

        //ISO 19139 Metadata
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/gmd.xsd","/xsd/iso/19139/20070417/gmd/gmd.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/metadataApplication.xsd","/xsd/iso/19139/20070417/gmd/metadataApplication.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/metadataEntity.xsd","/xsd/iso/19139/20070417/gmd/metadataEntity.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/spatialRepresentation.xsd","/xsd/iso/19139/20070417/gmd/spatialRepresentation.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/citation.xsd","/xsd/iso/19139/20070417/gmd/citation.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/referenceSystem.xsd","/xsd/iso/19139/20070417/gmd/referenceSystem.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/extent.xsd","/xsd/iso/19139/20070417/gmd/extent.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/metadataExtension.xsd","/xsd/iso/19139/20070417/gmd/metadataExtension.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/content.xsd","/xsd/iso/19139/20070417/gmd/content.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/applicationSchema.xsd","/xsd/iso/19139/20070417/gmd/applicationSchema.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/portrayalCatalogue.xsd","/xsd/iso/19139/20070417/gmd/portrayalCatalogue.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/dataQuality.xsd","/xsd/iso/19139/20070417/gmd/dataQuality.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/identification.xsd","/xsd/iso/19139/20070417/gmd/identification.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/constraints.xsd","/xsd/iso/19139/20070417/gmd/constraints.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/distribution.xsd","/xsd/iso/19139/20070417/gmd/distribution.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/maintenance.xsd","/xsd/iso/19139/20070417/gmd/maintenance.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gmd/freeText.xsd","/xsd/iso/19139/20070417/gmd/freeText.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gco/gco.xsd","/xsd/iso/19139/20070417/gco/gco.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gco/basicTypes.xsd","/xsd/iso/19139/20070417/gco/basicTypes.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gco/gcoBase.xsd","/xsd/iso/19139/20070417/gco/gcoBase.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gss/gss.xsd","/xsd/iso/19139/20070417/gss/gss.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gss/geometry.xsd","/xsd/iso/19139/20070417/gss/geometry.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gts/gts.xsd","/xsd/iso/19139/20070417/gts/gts.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gts/temporalObjects.xsd","/xsd/iso/19139/20070417/gts/temporalObjects.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gsr/gsr.xsd","/xsd/iso/19139/20070417/gsr/gsr.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/iso/19139/20070417/gsr/spatialReferencing.xsd","/xsd/iso/19139/20070417/gsr/spatialReferencing.xsd");

        //XML
        RELOCATIONS.put("http://www.w3.org/1999/xlink.xsd","/xsd/1999/xlink.xsd");
        RELOCATIONS.put("http://www.w3.org/2001/xml.xsd","/xsd/2001/xml.xsd");
    }


    private final Map<String, Schema> knownSchemas = new HashMap<>();
    private final Map<QName, Element> knownElements = new HashMap<>();
    private final Map<QName, NamedGroup> knownGroups = new HashMap<>();
    private final Map<String,String> locationMap = new HashMap<>();

    private final Map<QName,org.geotoolkit.feature.type.ComplexType> typeCache = new HashMap<>();

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
        //default relocations
        this.locationMap.putAll(RELOCATIONS);
        
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
        final List<FeatureType> result = new ArrayList<>();

        // first we look for imported xsd
        // NOTE : we must list and fill the knownshemas map before analyzing
        // some xsd have cyclic references : core -> sub1 + sub2 , sub1 -> core
        final List<Entry<Schema,String>> refs = new ArrayList<>();
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
                        refs.add(new AbstractMap.SimpleEntry<>(importedSchema, newBaseLocation));
                    } else {
                        LOGGER.log(Level.WARNING, "Unable to retrieve imported schema:{0}", schemalocation);
                    }
                }
            }
        }

        //analyze each schema
        for(Entry<Schema,String> entry : refs){
            result.addAll(getAllFeatureTypeFromSchema(entry.getKey(), entry.getValue()));
        }


        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if(opAtts instanceof TopLevelElement){
                final TopLevelElement element = (TopLevelElement) opAtts;
                knownElements.put(new QName(schema.getTargetNamespace(), element.getName()), element);
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = findComplexType(typeName);

                    if (isFeatureType(type)) {
                        final org.geotoolkit.feature.type.ComplexType ct = getType(typeName.getNamespaceURI(), type, null);
                        final FeatureType ft = (FeatureType) ct;
                        result.add(ft);

                    } else if (type == null && findSimpleType(typeName) == null) {
                        LOGGER.log(Level.WARNING, "Unable to find a the declaration of type {0} in schemas.", typeName.getLocalPart());
                        continue;
                    }
                } else {
                    LOGGER.log(Level.WARNING, "null typeName for element : {0}", element.getName());
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
                final ComplexType type = findComplexType(typeName);
                final org.geotoolkit.feature.type.ComplexType ct = getType(typeName.getNamespaceURI(), type, null);
                return (FeatureType)ct;
            } else {
                LOGGER.log(Level.WARNING, "the element:{0} has no type", name);
            }
        }
        return null;
    }

    /**
     * Check if the given complex type inherit from FeatureType.
     * 
     * @param search
     * @return true if this type is a feature type.
     */
    private boolean isFeatureType(ComplexType search){
        //loop on parent types until we find a Feature type
        while(search!=null){
            if(search.extendFeature()) return true;
            if(search.getComplexContent()==null || search.getComplexContent().getExtension()==null) break;
            final QName base = search.getComplexContent().getExtension().getBase();
            search = findComplexType(base);
        }
        return false;
    }

    private org.geotoolkit.feature.type.ComplexType getType(QName qname) throws SchemaException{
        final org.geotoolkit.feature.type.ComplexType ct = typeCache.get(qname);
        if(ct!=null) return ct;

        final ComplexType type = findComplexType(qname);

        if(type==null){
            LOGGER.log(Level.WARNING, "Unable to find complex type for name : {0}", qname);
            return null;
        }else{
            return getType(qname.getNamespaceURI(), type, null);
        }
    }

    private org.geotoolkit.feature.type.ComplexType getType(String namespace, ComplexType type, String elementName) throws SchemaException{
        String typeName = type.getName();
        if(typeName==null || typeName.isEmpty()) typeName = elementName;
        final QName qname = new QName(namespace, typeName);
        final org.geotoolkit.feature.type.ComplexType ct = typeCache.get(qname);
        if(ct!=null) return ct;

        final boolean isFeatureType = isFeatureType(type);

        final FeatureTypeBuilder builder = new FeatureTypeBuilder(new ModifiableFeatureTypeFactory());
        String properName = qname.getLocalPart();
        
        //we remove the 'Type' extension for feature types.
        if (isFeatureType && properName.endsWith("Type")) {
            properName = properName.substring(0, properName.lastIndexOf("Type"));
        }
        final Name ftypeName = new DefaultName(namespace, properName);
        builder.setName(ftypeName);
        final ModifiableType finalType = (ModifiableType) ((isFeatureType) ?
                new ModifiableFeaturetype(ftypeName,new ArrayList(), null, false, null, ct, null)
                : builder.buildType());
        typeCache.put(qname, finalType);

        if(isFeatureType){
            finalType.changeParent(BasicFeatureTypes.FEATURE);
        }

        finalType.getDescriptors().addAll(getGroupAttributes(namespace, type.getSequence()));

        final ComplexContent content = type.getComplexContent();
        if (content != null) {
            final ExtensionType ext = content.getExtension();
            if (ext != null) {
                final QName base = ext.getBase();
                if(base!=null){
                    final org.geotoolkit.feature.type.ComplexType parent = getType(base);
                    if(parent!=null){
                        finalType.changeParent(parent);
                        if(!"AbstractFeatureType".equals(parent.getName().getLocalPart())){
                            //we don't declare the base feature attribute types.
                            finalType.getDescriptors().addAll(parent.getDescriptors());
                        }
                    }
                }

                finalType.getDescriptors().addAll(getGroupAttributes(namespace, ext.getSequence()));
            }
        }
        finalType.rebuildPropertyMap();

        return finalType;
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
                final Element ele = (Element) particle;
                

                final AttributeDescriptor att = elementToAttribute(ele, namespace);
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

        //get properties
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
        final Name attName = new DefaultName(namespace, elementName);

        //try to extract complex type
        if(currentElement.getComplexType()!=null){
            final org.geotoolkit.feature.type.ComplexType type = getType(namespace,
                    currentElement.getComplexType(),currentElement.getName());
            return adb.create(type, attName, min, max, nillable, null);
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
            LOGGER.log(Level.WARNING, "unable to find the type of element : {0}", currentElement.getName());
            return null;
        }
        
        knownElements.put(new QName(namespace, elementName), currentElement);
        CoordinateReferenceSystem crs = null;

        if(Utils.isGeometricType(elementType)){
            final Class c = Utils.getTypeFromQName(elementType);
            if (c == null) {
                throw new SchemaException("The attribute : " + currentElement + " does no have a declared type.");
            } else if (Geometry.class.isAssignableFrom(c) || org.opengis.geometry.Geometry.class.isAssignableFrom(c)) {
                return adb.create(attName, c, crs, min, max, nillable, null);
            } else {
                return adb.create(attName, c, min, max, nillable, null);
            }
        }else{
            
            //search simple types
            final SimpleType simpleType = findSimpleType(elementType);
            if(simpleType != null){
                elementType = resolveSimpleTypeValueName(simpleType);
            }else{
                //search in complex types
                final org.geotoolkit.feature.type.ComplexType cType = getType(elementType);
                if (cType != null) {
                    return adb.create(cType, attName, null, min, max, nillable, null);
                }
            }

            //search in knowned types
            final Class c = Utils.getTypeFromQName(elementType);
            if (c == null) {
                throw new SchemaException("The attribute : " + currentElement + " does no have a declared type.");
            } else if (Geometry.class.isAssignableFrom(c) || org.opengis.geometry.Geometry.class.isAssignableFrom(c)) {
                return adb.create(attName, c, crs, min, max, nillable, null);
            } else {
                return adb.create(attName, c, min, max, nillable, null);
            }
        }
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

    private ComplexType findComplexType(QName typeName) {
        for (Schema schema : knownSchemas.values()) {
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            final ComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    private SimpleType findSimpleType(final QName typeName) {

        // look in the schemas
        for (Schema schema : knownSchemas.values()) {
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            final SimpleType type = schema.getSimpleTypeByName(typeName.getLocalPart());
            if (type != null) {
                return type;
            }
        }
        // look in primitive types
        if (Utils.existPrimitiveType(typeName.getLocalPart())) {
            return new LocalSimpleType(typeName.getLocalPart());
        }
        return null;
    }

    private QName resolveSimpleTypeValueName(SimpleType simpleType){
        final Restriction restriction = simpleType.getRestriction();
        if(restriction!=null){
            QName base = restriction.getBase();
            if(base!=null){
                return base;
            }
            final LocalSimpleType localSimpleType = restriction.getSimpleType();
            if(localSimpleType!=null){
                return resolveSimpleTypeValueName(localSimpleType);
            }

            return null;
        }

        // TODO union can be a collection of anything
        // collection ? array ? Object.class ? most exact type ?
        final Union union = simpleType.getUnion();
        if(union !=null){
            final QName name = union.getMemberTypes().get(0);
            final SimpleType refType = findSimpleType(name);
            return resolveSimpleTypeValueName(refType);
        }

        //TODO list type
        final org.geotoolkit.xsd.xml.v2001.List list = simpleType.getList();
        if(list!=null){
            final QName subTypeName = list.getItemType();
            if(subTypeName!=null){
                final SimpleType refType = findSimpleType(subTypeName);
                if(refType!=null){
                    return resolveSimpleTypeValueName(refType);
                }
                return subTypeName;
            }
            final LocalSimpleType subtype = list.getSimpleType();
            if(subtype!=null){
                return resolveSimpleTypeValueName(simpleType);
            }
        }

        if(Utils.existPrimitiveType(simpleType.getName())){
            return new QName(null, simpleType.getName());
        }else{
            return null;
        }
    }

}
