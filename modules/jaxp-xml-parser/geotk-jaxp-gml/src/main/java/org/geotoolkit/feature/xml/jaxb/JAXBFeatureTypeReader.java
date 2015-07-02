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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;
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
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.collection.Cache;

import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.xml.AbstractConfigurable;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.op.AliasOperation;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.feature.type.DefaultFeatureType;
import org.geotoolkit.feature.type.DefaultOperationDescriptor;
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
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.OperationDescriptor;
import org.geotoolkit.feature.type.OperationType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.geotoolkit.xsd.xml.v2001.Annotated;
import org.geotoolkit.xsd.xml.v2001.Any;
import org.geotoolkit.xsd.xml.v2001.Appinfo;
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


    private boolean skipStandardObjectProperties = false;
    private final Map<String, Schema> knownSchemas = new HashMap<>();
    private final Map<String,String> locationMap = new HashMap<>();

    private final Map<QName,org.geotoolkit.feature.type.ComplexType> typeCache = new HashMap<>();

    //Substitution group hierarchy
    // example : AbstractGeometry -> [AbstractGeometricAggregate,AbstractGeometricPrimitive,GeometricComplex,AbstractImplicitGeometry]
    private final Map<QName,Set<QName>> substitutionGroups = new HashMap<>();

    private final List<Entry<ComplexType,ModifiableType>> unfinished = new ArrayList<>();
    private final List<ModifiableType> uncompleted = new ArrayList<>();

    /**
     * Target namespace of the primary XSD.
     */
    private String targetNamespace;

    public JAXBFeatureTypeReader() {
        this(null);
    }

    public boolean isSkipStandardObjectProperties() {
        return skipStandardObjectProperties;
    }

    public void setSkipStandardObjectProperties(boolean skip) {
        this.skipStandardObjectProperties = skip;
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
     * Target namespace of the primary XSD.
     * This value is available only after reading.
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final String xml) throws JAXBException {
        return read((Object)xml);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final InputStream in) throws JAXBException {
        return read((Object)in);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final URL url) throws JAXBException {
        return read((Object)url);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Reader reader) throws JAXBException {
        return read((Object)reader);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Node element) throws JAXBException {
        return read((Object)element);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final String xml, final String name) throws JAXBException {
        return read((Object)xml,name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final InputStream in, final String name) throws JAXBException {
        return read((Object)in,name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Reader reader, final String name) throws JAXBException {
        return read((Object)reader,name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Node node, final String name) throws JAXBException {
        return read((Object)node,name);
    }

    public FeatureType read(final Object candidate, final String name) throws JAXBException {
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
            knownSchemas.put("unknow location", schema);
            return getFeatureTypeFromSchema(schema, name);
        } catch (IOException ex) {
            throw new JAXBException(ex.getMessage(),ex);
        }
    }

    public List<FeatureType> read(final Object candidate) throws JAXBException {
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
            return getAllFeatureTypeFromSchema(schema, baseLocation);
        } catch (IOException ex) {
            throw new JAXBException(ex.getMessage(),ex);
        }
    }

    public List<FeatureType> getAllFeatureTypeFromSchema(final Schema schema, final String baseLocation) throws MismatchedFeatureException {
        final List<FeatureType> result = new ArrayList<>();
        // first we look for imported xsd
        // NOTE : we must list and fill the knownshemas map before analyzing
        // some xsd have cyclic references : core -> sub1 + sub2 , sub1 -> core
        final List<Entry<Schema,String>> refs = new ArrayList<>();
        refs.add(new AbstractMap.SimpleEntry<>(schema, baseLocation));
        listAllSchemas(schema, baseLocation, refs);

        for(Entry<Schema,String> entry : refs){
            listFeatureTypes(entry.getKey(), result);
        }

        return result;
    }

    private void listAllSchemas(final Schema schema, final String baseLocation, List<Entry<Schema,String>> refs) throws MismatchedFeatureException{
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

    private void listFeatureTypes(Schema schema, List<FeatureType> result) throws MismatchedFeatureException{
        // then we look for feature type and groups
        for (OpenAttrs opAtts : schema.getSimpleTypeOrComplexTypeOrGroup()) {

            if(opAtts instanceof TopLevelElement){
                final TopLevelElement element = (TopLevelElement) opAtts;
                final QName typeName = element.getType();
                if (typeName != null) {
                    final ComplexType type = findComplexType(typeName);

                    if (isFeatureType(type)) {
                        final org.geotoolkit.feature.type.ComplexType ct = getType(typeName.getNamespaceURI(), type, null);
                        final FeatureType ft = (FeatureType) ct;
                        result.add(ft);

                        //if the type name is not the same as the element name, make a copy of the type renaming it
                        if(!ft.getName().tip().toString().equals(element.getName())){
                            final GenericName name = NamesExt.create(NamesExt.getNamespace(ft.getName()), element.getName());
                            final ModifiableFeaturetype renamed = new ModifiableFeaturetype(name,new ArrayList(), null, type.isAbstract(), null, ft, null);
                            uncompleted.add(renamed);
                            result.add(renamed);
                        }

                    } else if (type == null && findSimpleType(typeName) == null) {
                        LOGGER.log(Level.WARNING, "Unable to find a the declaration of type {0} in schemas.", typeName.getLocalPart());
                        continue;
                    }

                } else {
                    LOGGER.log(Level.WARNING, "null typeName for element : {0}", element.getName());
                }
            }
        }

        //finish all substitution types
        for(Entry<ComplexType,ModifiableType> ct : unfinished){
            completeType(ct.getKey(), ct.getValue());
        }
        unfinished.clear();

        //finish all uncomplete types (inherited properties)
        for(ModifiableType t : uncompleted){
            checkInherit(t);
        }
        uncompleted.clear();

    }

    private void checkInherit(ModifiableType t){
        if(t.isLock()) return;

        final AttributeType parent = t.getSuper();
        if(parent!=null && parent instanceof ModifiableType){
            checkInherit((ModifiableType)parent);

            final org.geotoolkit.feature.type.ComplexType ct = (org.geotoolkit.feature.type.ComplexType) parent;
            for(PropertyDescriptor pd : ct.getDescriptors()){
                if(t.getDescriptor(pd.getName())==null){
                    t.getDescriptors().add(pd);
                }
            }
        }

        t.rebuildPropertyMap();
        t.lock();
    }

    private Set<QName> getSubstitutions(QName name){
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

    public FeatureType getFeatureTypeFromSchema(final Schema schema, final String name) throws MismatchedFeatureException {
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

    public org.geotoolkit.feature.type.ComplexType getComplexType(GenericName name) throws MismatchedFeatureException{
        return getType(new QName(NamesExt.getNamespace(name), name.tip().toString()));
    }

    public PropertyDescriptor getElementType(GenericName name) throws MismatchedFeatureException{
        final Element parentElement = findGlobalElement(new QName(NamesExt.getNamespace(name), name.tip().toString()));
        return elementToAttribute(parentElement, NamesExt.getNamespace(name)).get(0);
    }

    private org.geotoolkit.feature.type.ComplexType getType(QName qname) throws MismatchedFeatureException{
        final org.geotoolkit.feature.type.ComplexType ct = typeCache.get(qname);
        if(ct!=null) return ct;

        final ComplexType type = findComplexType(qname);

        if(type==null){
            throw new MismatchedFeatureException("Unable to find complex type for name : "+ qname);
        }else{
            return getType(qname.getNamespaceURI(), type, null);
        }
    }

    private org.geotoolkit.feature.type.ComplexType getType(String namespace, ComplexType type, String elementName) throws MismatchedFeatureException{
        return getType(namespace, type, elementName, false);
    }

    private org.geotoolkit.feature.type.ComplexType getType(String namespace, ComplexType type, String elementName, boolean delay) throws MismatchedFeatureException{
        String typeName = type.getName();
        if(typeName==null || typeName.isEmpty()) typeName = elementName;
        final QName qname = new QName(namespace, typeName);
        final org.geotoolkit.feature.type.ComplexType ct = typeCache.get(qname);
        if(ct!=null && ((ModifiableType)ct).isLock()) return ct;

        final ModifiableType finalType;

        if(ct!=null){
            finalType = (ModifiableType) ct;
        }else{
            final boolean isFeatureType = isFeatureType(type);

            final FeatureTypeBuilder builder = new FeatureTypeBuilder(new ModifiableFeatureTypeFactory());
            builder.setAbstract(type.isAbstract());
            String properName = qname.getLocalPart();

            //we remove the 'Type' extension for feature types.
            if (isFeatureType && properName.endsWith("Type")) {
                properName = properName.substring(0, properName.lastIndexOf("Type"));
            }
            final GenericName ftypeName = NamesExt.create(namespace, properName);
            builder.setName(ftypeName);
            finalType = (ModifiableType) ((isFeatureType) ?
                    new ModifiableFeaturetype(ftypeName,new ArrayList(), null, type.isAbstract(), null, ct, null)
                    : builder.buildType());
            typeCache.put(qname, finalType);

            if(isFeatureType){
                finalType.changeParent(BasicFeatureTypes.FEATURE);
            }
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

    private void completeType(ComplexType type, ModifiableType finalType) throws MismatchedFeatureException{

        if(finalType.isLock()) return;

        final String namespace = NamesExt.getNamespace(finalType.getName());

        //read attributes
        final List<Annotated> atts = type.getAttributeOrAttributeGroup();
        if(atts!=null){
            for(Annotated att : atts){
                if(att instanceof Attribute){
                    addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (Attribute) att));
                }else if(att instanceof AttributeGroupRef){
                    addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                }
            }
        }

        //read sequence properties
        addOrReplace(finalType.getDescriptors(), getGroupAttributes(namespace, type.getSequence()));

        boolean uncomplete = false;

        //read complex content if defined
        final ComplexContent content = type.getComplexContent();
        ExtensionType ext = null;
        if (content != null) {
            ext = content.getExtension();
            if (ext != null) {
                final QName base = ext.getBase();
                if(base!=null && !base.getLocalPart().equalsIgnoreCase("anytype")){
                    final org.geotoolkit.feature.type.ComplexType parent = getType(base);
                    if(parent!=null){
                        if(!((ModifiableType)parent).isLock()){
                            uncomplete = true;
                        }

                        addOrReplace(finalType.getDescriptors(), parent.getDescriptors());
                        if(!Utils.GML_FEATURE_TYPES.contains(parent.getName())){
                            finalType.changeParent(parent);
                        }
                    }
                }

                //read attributes
                final List<Annotated> attexts = ext.getAttributeOrAttributeGroup();
                if(attexts!=null){
                    for(Annotated att : attexts){
                        if(att instanceof Attribute){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (Attribute) att));
                        }else if(att instanceof AttributeGroupRef){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                        }
                    }
                }

                //sequence attributes
                addOrReplace(finalType.getDescriptors(), getGroupAttributes(namespace, ext.getSequence()));
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

                addOrReplace(finalType.getDescriptors(), getGroupAttributes(namespace, restriction.getSequence()));
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

                if(st instanceof org.geotoolkit.feature.type.ComplexType){
                    addOrReplace(finalType.getDescriptors(), ((org.geotoolkit.feature.type.ComplexType)st).getDescriptors());
                }else{
                    final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                    finalType.getDescriptors().add(adb.create(st, NamesExt.create(namespace, Utils.VALUE_PROPERTY_NAME), 0, 1, false, null));
                }

                //read attributes
                final List<Annotated> attexts = sext.getAttributeOrAttributeGroup();
                if(attexts!=null){
                    for(Annotated att : attexts){
                        if(att instanceof Attribute){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (Attribute) att));
                        }else if(att instanceof AttributeGroupRef){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                        }
                    }
                }
            }

            if(restriction!=null){

                final QName base = restriction.getBase();
                if(base !=null){
                    final ComplexType sct = findComplexType(base);
                    if(sct!=null){
                        final org.geotoolkit.feature.type.ComplexType tct = getType(namespace, sct, null);
                        addOrReplace(finalType.getDescriptors(), tct.getDescriptors());
                    }else{
                        final PropertyType restType = resolveSimpleType(base);
                        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                        addOrReplace(finalType.getDescriptors(), adb.create(restType, NamesExt.create(namespace, Utils.VALUE_PROPERTY_NAME), 0, 1, false, null));
                    }
                }


                //read attributes
                final List<Annotated> attexts = restriction.getAttributeOrAttributeGroup();
                if(attexts!=null){
                    for(Annotated att : attexts){
                        if(att instanceof Attribute){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (Attribute) att));
                        }else if(att instanceof AttributeGroupRef){
                            addOrReplace(finalType.getDescriptors(), getAnnotatedAttributes(namespace, (AttributeGroupRef) att));
                        }
                    }
                }
            }

        }

        //read choice if set
        final ExplicitGroup choice = type.getChoice();
        if(choice != null){
            final Integer minOccurs = choice.getMinOccurs();
            final String maxOccurs = choice.getMaxOccurs();
            final List<PropertyDescriptor> choices = getGroupAttributes(namespace, choice);
            for(PropertyDescriptor pd : choices){
                //change the min/max occurences
                int maxOcc = 1;
                if("unbounded".equalsIgnoreCase(maxOccurs)) {
                    maxOcc = Integer.MAX_VALUE;
                } else if(maxOccurs!=null){
                    maxOcc = Integer.parseInt(maxOccurs);
                }
                //NOTE : a choice with max occurence ? yet we must consider the limitation
                //of each element
                /*
                final PropertyDescriptor rpd;
                if(pd instanceof OperationDescriptor){
                    final OperationDescriptor od = (OperationDescriptor) pd;
                    rpd =  new DefaultOperationDescriptor(od.getType(), od.getName(), 0, maxOcc, od.isNillable());
                }else{
                    final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                    adb.copy(pd);
                    adb.setMinOccurs(0);
                    adb.setMaxOccurs(maxOcc);
                    rpd = adb.buildDescriptor();
                }
                */

                addOrReplace(finalType.getDescriptors(), pd);
            }
        }


        removeAttributes(finalType, Utils.GML_ABSTRACT_FEATURE_PROPERTIES);

        //remove standard object properties if requested
        if(skipStandardObjectProperties){
            removeAttributes(finalType, Utils.GML_STANDARD_OBJECT_PROPERTIES);
        }

        finalType.rebuildPropertyMap();

        if(!uncomplete){
            //finalType.lock();
        }else{
            uncompleted.add(finalType);
        }
    }



    private static void removeAttributes(ModifiableType type, Set<GenericName> propNames){
        final List<PropertyDescriptor> descs = type.getDescriptors();
        for(int i=descs.size()-1;i>=0;i--){
            if(propNames.contains(descs.get(i).getName())){
                descs.remove(i);
            }
        }
    }

    private List<PropertyDescriptor> getGroupAttributes(String namespace, Group group) throws MismatchedFeatureException {
        if(group==null) return Collections.EMPTY_LIST;

        final List<PropertyDescriptor> atts = new ArrayList<>();

        final List<Object> particles = group.getParticle();
        for(Object particle : particles){
            if(particle instanceof JAXBElement){
                particle = ((JAXBElement)particle).getValue();
            }

            if(particle instanceof Element){
                final Element ele = (Element) particle;
                final List<PropertyDescriptor> att = elementToAttribute(ele, namespace);
                if(att!=null)atts.addAll(att);

            }else if(particle instanceof Any){
                final Any ele = (Any) particle;
                final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                atb.setName(namespace, Utils.ANY_PROPERTY_NAME);
                atb.setBinding(Object.class);
                adb.setName(namespace, Utils.ANY_PROPERTY_NAME);
                adb.setType(atb.buildType());
                copyMinMax(ele, adb);
                atts.add(adb.buildDescriptor());
                
            }else if(particle instanceof GroupRef){
                final GroupRef ref = (GroupRef) particle;
                final QName groupRef = ref.getRef();
                final NamedGroup ng = findGlobalGroup(groupRef);
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

    private AttributeDescriptor getAnnotatedAttributes(String namespace, final Attribute att) throws MismatchedFeatureException{
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        adb.reset();
        atb.reset();

        if(att.getRef()!=null){
            namespace = att.getRef().getNamespaceURI();
            //copy properties from parent
            final Attribute attRef = findGlobalAttribute(att.getRef());
            final AttributeDescriptor atDesc = getAnnotatedAttributes(namespace, attRef);
            adb.copy(atDesc);
            adb.setName(namespace, atDesc.getName().tip().toString());
            atb.copy(atDesc.getType());
        } else {
            namespace = null;
        }

        final String id = att.getId();
        final String name = att.getName();
        final String def = att.getDefault();
        final PropertyType type = resolveAttributeValueName(att);
        final String use = att.getUse();

        if(id!=null || name!=null){
            //find name
            final GenericName attName = NamesExt.create(namespace, "@"+ ((name==null) ? id : name));
            adb.setName(attName);
            atb.setName(attName);
        }

        //find min/max occurences
        adb.setMinOccurs((use==null || "optional".equals(use)) ? 0 : 1);
        adb.setMaxOccurs(1);
        adb.setNillable(false);

        atb.setBinding(type.getBinding());
        adb.setType(atb.buildType());

        if(def!=null && !def.isEmpty()){
            final Object defVal = ObjectConverters.convert(def, type.getBinding());
            adb.setDefaultValue(defVal);
        }

        return adb.buildDescriptor();
    }

    private List<PropertyDescriptor> getAnnotatedAttributes(final String namespace, final AttributeGroup group) throws MismatchedFeatureException{
        final List<PropertyDescriptor> descs = new ArrayList<>();
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
            final NamedAttributeGroup refGroup = findAttributeGroup(ref);
            addOrReplace(descs, getAnnotatedAttributes(namespace, refGroup));
        }
        return descs;
    }

    private List<PropertyDescriptor> elementToAttribute(final Element attributeElement, final String namespace) throws MismatchedFeatureException {
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
    private List<PropertyDescriptor> elementToAttribute(final Element attributeElement, final String namespace, boolean isSubstitute) throws MismatchedFeatureException {
        final List<PropertyDescriptor> results = new ArrayList<>();

        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();

        if (attributeElement.getRef() != null) {
            final Element parentElement = findGlobalElement(attributeElement.getRef());
            if (parentElement == null) {
                throw new MismatchedFeatureException("unable to find referenced element : "+ attributeElement.getRef());
            }
            final List<PropertyDescriptor> parentAtt = elementToAttribute(parentElement, namespace);
            for(int i=1,n=parentAtt.size();i<n;i++){
                //substitution groups
                results.add(parentAtt.get(i));
            }
            final AttributeDescriptor parentDesc = (AttributeDescriptor) parentAtt.get(0);
            adb.copy(parentDesc);
            adb.setType(parentDesc.getType());
            atb.copy(parentDesc.getType());
        }

        copyMinMaxNill(attributeElement, adb);

        final String elementName = attributeElement.getName();
        if(elementName!=null){
            final GenericName attName = NamesExt.create(namespace, elementName);
            adb.setName(attName);
            if(atb.getName()==null){
                atb.setName(attName);
            }
        }

        //try to extract complex type
        if(attributeElement.getComplexType()!=null){
            final org.geotoolkit.feature.type.ComplexType type = getType(namespace,
                    attributeElement.getComplexType(),attributeElement.getName(),isSubstitute);
            adb.setType(type);
        }

        final AttributeDescriptor baseDesc;
        if(adb.getType() instanceof org.geotoolkit.feature.type.ComplexType){
            baseDesc = adb.buildDescriptor();
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

            atb.setBinding(Object.class);
            if (elementType != null) {
                if(Utils.isPrimitiveType(elementType)){
                    final Class c = Utils.getTypeFromQName(elementType);
                    if (c == null) {
                        throw new MismatchedFeatureException("The attribute : " + attributeElement + " does no have a declared type.");
                    }
                    atb.setBinding(c);
                    atb.setName(elementType.getNamespaceURI(), elementType.getLocalPart());
                }else{
                    final PropertyType pt = resolveSimpleType(elementType);
                    adb.setType(pt);
                }
            }

            if(adb.getType()==null){
                adb.setType(atb.buildType());
            }

            baseDesc = adb.buildDescriptor();
        }

        results.add(baseDesc);

        //check for substitutions
        if(elementName!=null && !isSubstitute){
            final Collection<QName> substitutions = getSubstitutions(new QName(namespace, elementName));
            if(substitutions!=null && !substitutions.isEmpty()){
                for(QName sub : substitutions){
                    final Element subEle = findGlobalElement(sub);
                    final List<PropertyDescriptor> subs = elementToAttribute(subEle,sub.getNamespaceURI(),true);
                    //create an alias operator for each of them
                    for(PropertyDescriptor ad : subs){
                        if(ad instanceof OperationDescriptor){
                            throw new UnsupportedOperationException("Substitution is an operation, not supported.");
                        }else{
                            final OperationType optype = new AliasOperation(ad.getName(), baseDesc.getName(), ad);
                            final OperationDescriptor desc = new DefaultOperationDescriptor(optype,
                                    ad.getName(), baseDesc.getMinOccurs(), baseDesc.getMaxOccurs(), baseDesc.isNillable());
                            results.add(desc);
                        }
                    }
                }
            }
        }

        return results;

    }

    private static void copyMinMaxNill(Element attributeElement, AttributeDescriptorBuilder adb){
        //override properties which are defined
        final Integer minAtt = attributeElement.getMinOccurs();
        if(minAtt!=null){
            adb.setMinOccurs(minAtt);
        }

        final String maxxAtt = attributeElement.getMaxOccurs();
        if("unbounded".equalsIgnoreCase(maxxAtt)) {
            adb.setMaxOccurs(Integer.MAX_VALUE);
        } else if(maxxAtt!=null){
            adb.setMaxOccurs(Integer.parseInt(maxxAtt));
        }

        adb.setNillable(attributeElement.isNillable());
    }

    private static void copyMinMax(Any attributeElement, AttributeDescriptorBuilder adb){
        //override properties which are defined
        final BigInteger minAtt = attributeElement.getMinOccurs();
        if(minAtt!=null){
            adb.setMinOccurs(minAtt.intValue());
        }

        final String maxxAtt = attributeElement.getMaxOccurs();
        if("unbounded".equalsIgnoreCase(maxxAtt)) {
            adb.setMaxOccurs(Integer.MAX_VALUE);
        } else if(maxxAtt!=null){
            adb.setMaxOccurs(Integer.parseInt(maxxAtt));
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
        for (Entry<String,Schema> entry : knownSchemas.entrySet()) {
            final Schema schema = entry.getValue();
            if(!schema.getTargetNamespace().equalsIgnoreCase(typeName.getNamespaceURI())) continue;
            final ComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    private NamedGroup findGlobalGroup(final QName typeName) {
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

    private Attribute findGlobalAttribute(final QName typeName) {
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

    private Element findGlobalElement(final QName typeName){
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

    private NamedAttributeGroup findAttributeGroup(final QName typeName){
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

    private PropertyType resolveAttributeValueName(Attribute att) throws MismatchedFeatureException{
        //test direct type
        final QName type = att.getType();
        if(type!=null){
            return resolveSimpleType(type);
        }

        //test reference
        final QName ref = att.getRef();
        if(ref!=null){
            final Attribute parentAtt = findGlobalAttribute(ref);
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

    private PropertyType resolveSimpleType(QName name) throws MismatchedFeatureException{

        //check if primitive type
        if(Utils.existPrimitiveType(name.getLocalPart())){
            final Class valueType = Utils.getTypeFromQName(name);
            final AttributeTypeBuilder atb = new AttributeTypeBuilder();
            atb.setName(NamesExt.create(name));
            atb.setBinding(valueType);
            return atb.buildType();
        }

        //check if a simple type exist
        final SimpleType simpleType = findSimpleType(name);
        if(simpleType!=null){
            return resolveSimpleType(simpleType);
        }else{
            //could be a complex type ... for a simple content, that's not an error. xsd/xml makes no sense at all sometimes
            final org.geotoolkit.feature.type.ComplexType sct = getType(name);
            if(sct==null){
                throw new MismatchedFeatureException("Could not find type : "+name);
            }
            return sct;
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
                final SimpleType refType = findSimpleType(name);
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
                final SimpleType refType = findSimpleType(subTypeName);
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

    private static void addOrReplace(List<PropertyDescriptor> descs, PropertyDescriptor pd){
        addOrReplace(descs, Collections.singleton(pd));
    }

    private static void addOrReplace(List<PropertyDescriptor> descs, Collection<? extends PropertyDescriptor> toAdd){
        loop:
        for(PropertyDescriptor pd : toAdd){
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

    private static boolean isDeprecated(TopLevelElement candidate){
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
