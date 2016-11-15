/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2016, Geomatys
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.apache.sis.util.collection.Cache;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.xsd.xml.v2001.Appinfo;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.geotoolkit.xsd.xml.v2001.ComplexType;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.Import;
import org.geotoolkit.xsd.xml.v2001.Include;
import org.geotoolkit.xsd.xml.v2001.LocalSimpleType;
import org.geotoolkit.xsd.xml.v2001.NamedAttributeGroup;
import org.geotoolkit.xsd.xml.v2001.NamedGroup;
import org.geotoolkit.xsd.xml.v2001.OpenAttrs;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.SimpleType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.geotoolkit.xsd.xml.v2001.XSDMarshallerPool;
import org.opengis.feature.MismatchedFeatureException;
import org.w3c.dom.Node;

/**
 * Store location and relations in sxd schemas.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class XSDSchemaContext {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.feature.xml.jaxp");
    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();
    private static final Cache<String,Schema> SCHEMA_CACHE = new Cache<>(60,60,true);

    /**
     * default relocations
     * used by all reader instances
     */
    private static final Map<String,String> RELOCATIONS = new HashMap<>();
    static {

        //GML 3.1.1
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/gml.xsd","/xsd/gml/3.1.1/gml.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/dynamicFeature.xsd","/xsd/gml/3.1.1/dynamicFeature.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/feature.xsd","/xsd/gml/3.1.1/feature.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/geometryBasic2d.xsd","/xsd/gml/3.1.1/geometryBasic2d.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/geometryBasic0d1d.xsd","/xsd/gml/3.1.1/geometryBasic0d1d.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/measures.xsd","/xsd/gml/3.1.1/measures.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/units.xsd","/xsd/gml/3.1.1/units.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/dictionary.xsd","/xsd/gml/3.1.1/dictionary.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/gmlBase.xsd","/xsd/gml/3.1.1/gmlBase.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/basicTypes.xsd","/xsd/gml/3.1.1/basicTypes.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/temporal.xsd","/xsd/gml/3.1.1/temporal.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/direction.xsd","/xsd/gml/3.1.1/direction.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/topology.xsd","/xsd/gml/3.1.1/topology.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/geometryComplexes.xsd","/xsd/gml/3.1.1/geometryComplexes.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/geometryAggregates.xsd","/xsd/gml/3.1.1/geometryAggregates.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/geometryPrimitives.xsd","/xsd/gml/3.1.1/geometryPrimitives.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/coverage.xsd","/xsd/gml/3.1.1/coverage.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/valueObjects.xsd","/xsd/gml/3.1.1/valueObjects.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/grids.xsd","/xsd/gml/3.1.1/grids.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/coordinateReferenceSystems.xsd","/xsd/gml/3.1.1/coordinateReferenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/coordinateSystems.xsd","/xsd/gml/3.1.1/coordinateSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/referenceSystems.xsd","/xsd/gml/3.1.1/referenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/datums.xsd","/xsd/gml/3.1.1/datums.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/coordinateOperations.xsd","/xsd/gml/3.1.1/coordinateOperations.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/dataQuality.xsd","/xsd/gml/3.1.1/dataQuality.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/observation.xsd","/xsd/gml/3.1.1/observation.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/defaultStyle.xsd","/xsd/gml/3.1.1/defaultStyle.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/smil/smil20.xsd","/xsd/gml/3.1.1/smil20.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/smil/smil20-language.xsd","/xsd/gml/3.1.1/smil20-language.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/temporalReferenceSystems.xsd","/xsd/gml/3.1.1/temporalReferenceSystems.xsd");
        RELOCATIONS.put("http://schemas.opengis.net/gml/3.1.1/base/temporalTopology.xsd","/xsd/gml/3.1.1/temporalTopology.xsd");

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


    final Map<String,Schema> knownSchemas = new HashMap<>();
    final Map<String,String> locationMap = new HashMap<>();

    //Substitution group hierarchy
    // example : AbstractGeometry -> [AbstractGeometricAggregate,AbstractGeometricPrimitive,GeometricComplex,AbstractImplicitGeometry]
    final Map<QName,Set<QName>> substitutionGroups = new HashMap<>();

    /**
     * Target namespace of the primary XSD.
     */
    String targetNamespace;

    public XSDSchemaContext(Map<String,String> locationMap) {
        //default relocations
        this.locationMap.putAll(RELOCATIONS);

        if(locationMap!=null){
            this.locationMap.putAll(locationMap);
        }
    }

    /**
     *
     * @param candidate
     * @param name
     * @return
     * @throws JAXBException
     */
    public Schema read(final Object candidate, final String name) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema;
            String baseLocation = null;
            if(candidate instanceof Node) schema = (Schema) unmarshaller.unmarshal((Node)candidate);
            else if(candidate instanceof Reader) schema = (Schema) unmarshaller.unmarshal((Reader)candidate);
            else if(candidate instanceof InputStream) schema = (Schema) unmarshaller.unmarshal((InputStream)candidate);
            else if(candidate instanceof String) schema = (Schema) unmarshaller.unmarshal(new StringReader((String)candidate));
            else if(candidate instanceof URL){
                schema = (Schema) unmarshaller.unmarshal(((URL)candidate).openStream());
                // we build the base url to retrieve imported xsd
                final String location = ((URL)candidate).toString();
                knownSchemas.put(location, schema);
                if (location.lastIndexOf('/') != -1) {
                    baseLocation = location.substring(0, location.lastIndexOf('/') + 1);
                } else {
                    baseLocation = location;
                }
            }
            else throw new JAXBException("Unsupported input type : "+candidate);
            POOL.recycle(unmarshaller);
            knownSchemas.put("unknow location", schema);
            return schema;
        } catch (IOException ex) {
            throw new JAXBException(ex.getMessage(),ex);
        }
    }

    /**
     * 
     * @param candidate
     * @return Map<Schema,Location>
     * @throws JAXBException
     */
    public Map.Entry<Schema,String> read(final Object candidate) throws JAXBException {
        try {
            final Unmarshaller unmarshaller = POOL.acquireUnmarshaller();
            final Schema schema;
            String baseLocation = null;

            if(candidate instanceof Node) schema = (Schema) unmarshaller.unmarshal((Node)candidate);
            else if(candidate instanceof Reader) schema = (Schema) unmarshaller.unmarshal((Reader)candidate);
            else if(candidate instanceof InputStream) schema = (Schema) unmarshaller.unmarshal((InputStream)candidate);
            else if(candidate instanceof String) schema = (Schema) unmarshaller.unmarshal(new StringReader((String)candidate));
            else if(candidate instanceof URL){
                schema = (Schema) unmarshaller.unmarshal(((URL)candidate).openStream());
                // we build the base url to retrieve imported xsd;
                final String location = ((URL)candidate).toString();
                knownSchemas.put(location, schema);
                if (location.lastIndexOf('/') != -1) {
                    baseLocation = location.substring(0, location.lastIndexOf('/') + 1);
                } else {
                    baseLocation = location;
                }
            }
            else throw new JAXBException("Unsupported input type : "+candidate);
            POOL.recycle(unmarshaller);
            targetNamespace = schema.getTargetNamespace();
            knownSchemas.put("unknow location", schema);
            return new AbstractMap.SimpleImmutableEntry<>(schema, baseLocation);
        } catch (IOException ex) {
            throw new JAXBException(ex.getMessage(),ex);
        }
    }

    public void listAllSchemas(final Schema schema, final String baseLocation, List<Map.Entry<Schema,String>> refs) throws MismatchedFeatureException{
        fillAllSubstitution(schema);

        for (OpenAttrs attr: schema.getIncludeOrImportOrRedefine()) {
            if (attr instanceof Import || attr instanceof Include) {
                final String schemalocation = Utils.getIncludedLocation(baseLocation, attr);
                if (schemalocation != null && !knownSchemas.containsKey(schemalocation)) {
                    //check for a relocation
                    final String relocation = locationMap.get(schemalocation);
                    final String finalLocation = (relocation==null) ? schemalocation : relocation;
                    Schema importedSchema = null;
                    try{
                        importedSchema = SCHEMA_CACHE.getOrCreate(finalLocation, new Callable() {
                            public Schema call()  {
                                return Utils.getDistantSchema(finalLocation);
                            }
                        });
                    }catch(Exception ex){
                        throw new MismatchedFeatureException(ex.getMessage(),ex);
                    }

                    if (importedSchema != null) {
                        knownSchemas.put(schemalocation, importedSchema);
                        final String newBaseLocation = getNewBaseLocation(schemalocation, baseLocation);
                        refs.add(new AbstractMap.SimpleEntry<>(importedSchema, newBaseLocation));

                        //recursive search of all imports and include
                        listAllSchemas(importedSchema, newBaseLocation, refs);

                    } else {
                        LOGGER.log(Level.WARNING, "Unable to retrieve imported schema:{0}", schemalocation);
                    }
                }
            }
        }
    }

    public ComplexType findComplexType(QName typeName) {
        for (Map.Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            final ComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    /**
     * list all elements and complexe types and put them in the allCache map.
     * Do not parse them yet.
     *
     * @param schema
     */
    private void fillAllSubstitution(Schema schema){
        for(OpenAttrs att : schema.getSimpleTypeOrComplexTypeOrGroup()){
            if(att instanceof TopLevelElement){
                final TopLevelElement ele = (TopLevelElement) att;
                final QName parent = ele.getSubstitutionGroup();
                if(parent!=null){
                    Set<QName> subList = substitutionGroups.get(parent);
                    if(subList==null){
                        subList = new HashSet<>();
                        substitutionGroups.put(parent, subList);
                    }
                    final QName name = new QName(schema.getTargetNamespace(), ele.getName());
                    if(subList.contains(name)){
                        //name already here, check if one of them is deprecated
                        subList.add(name);
                    }else{
                        subList.add(name);
                    }
                }
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

    public NamedGroup findGlobalGroup(final QName typeName) {
        // look in the schemas
        for (Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            for(OpenAttrs att : schema.getSimpleTypeOrComplexTypeOrGroup()){
                if(att instanceof NamedGroup){
                    final NamedGroup candidate = (NamedGroup) att;
                    if(candidate.getName().equals(typeName.getLocalPart())){
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public Attribute findGlobalAttribute(final QName typeName) {
        // look in the schemas
        for (Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            for(OpenAttrs att : schema.getSimpleTypeOrComplexTypeOrGroup()){
                if(att instanceof Attribute){
                    final Attribute candidate = (Attribute) att;
                    if(candidate.getName().equals(typeName.getLocalPart())){
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public Element findGlobalElement(final QName typeName){
        Element element = null;
        // look in the schemas
        for (Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            loop:
            for(OpenAttrs att : schema.getSimpleTypeOrComplexTypeOrGroup()){
                if(att instanceof Element){
                    final TopLevelElement candidate = (TopLevelElement) att;
                    if(candidate.getName().equals(typeName.getLocalPart())){
                        //check if it's a deprecated type, we will return it only in last case
                        if(isDeprecated(candidate)){
                            element = candidate;
                            continue loop;
                        }

                        //found it
                        return candidate;
                    }
                }
            }
        }
        return element;
    }

    public NamedAttributeGroup findAttributeGroup(final QName typeName){
        // look in the schemas
        for (Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            for(OpenAttrs att : schema.getSimpleTypeOrComplexTypeOrGroup()){
                if(att instanceof NamedAttributeGroup){
                    final NamedAttributeGroup candidate = (NamedAttributeGroup) att;
                    if(candidate.getName().equals(typeName.getLocalPart())){
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    public SimpleType findSimpleType(final QName typeName) {

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

    public Set<QName> getSubstitutions(QName name){
        final Set<QName> subs = new HashSet<>();
        final Set<QName> lst = substitutionGroups.get(name);
        if(lst!=null){
            for(QName sub : lst){
                subs.add(sub);
                subs.addAll(getSubstitutions(sub));
            }
        }
        return subs;
    }

    /**
     * Check if the given complex type inherit from FeatureType.
     *
     * @param search
     * @return true if this type is a feature type.
     */
    public boolean isFeatureType(ComplexType search){
        //loop on parent types until we find a Feature type
        while(search!=null){
            if(search.extendFeature()) return true;
            if(search.getComplexContent()==null || search.getComplexContent().getExtension()==null) break;
            final QName base = search.getComplexContent().getExtension().getBase();
            search = findComplexType(base);
        }
        return false;
    }

    public static boolean isDeprecated(TopLevelElement candidate){
        //check if it's a deprecated type, we will return it only in last case
        if(candidate.getAnnotation()!=null){
            for(Object obj : candidate.getAnnotation().getAppinfoOrDocumentation()){
                if(obj instanceof Appinfo){
                    for(Object cdt : ((Appinfo)obj).getContent()){
                        if(cdt instanceof String && "deprecated".equalsIgnoreCase((String)cdt)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
