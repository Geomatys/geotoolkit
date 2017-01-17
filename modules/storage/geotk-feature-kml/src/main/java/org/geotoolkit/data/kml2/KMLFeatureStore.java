/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.data.kml2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequence;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.kml.xml.KMLMarshallerPool;
import org.geotoolkit.kml.xml.v220.AbstractContainerType;
import org.geotoolkit.kml.xml.v220.AbstractFeatureType;
import org.geotoolkit.kml.xml.v220.AbstractGeometryType;
import org.geotoolkit.kml.xml.v220.BoundaryType;
import org.geotoolkit.kml.xml.v220.DocumentType;
import org.geotoolkit.kml.xml.v220.GroundOverlayType;
import org.geotoolkit.kml.xml.v220.KmlType;
import org.geotoolkit.kml.xml.v220.LineStringType;
import org.geotoolkit.kml.xml.v220.LinearRingType;
import org.geotoolkit.kml.xml.v220.LocationType;
import org.geotoolkit.kml.xml.v220.ModelType;
import org.geotoolkit.kml.xml.v220.MultiGeometryType;
import org.geotoolkit.kml.xml.v220.PhotoOverlayType;
import org.geotoolkit.kml.xml.v220.PlacemarkType;
import org.geotoolkit.kml.xml.v220.PointType;
import org.geotoolkit.kml.xml.v220.PolygonType;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStores;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLFeatureStore extends AbstractFeatureStore {

    private static final GeometryFactory GF = GO2Utilities.JTS_FACTORY;
    private static final CoordinateReferenceSystem CRS = CommonCRS.WGS84.normalizedGeographic(); //CRS:84 , longitude/latitude
    private static final String DEFAULT_NAME = "Placemark";
    
    private final URI path;
    private Map<GenericName,FeatureType> types = null;
    
    private FeatureType placemarkType;
    
    public KMLFeatureStore(Path path) {
        this(toParameters(path, "no namespace"));
    }
    
    public KMLFeatureStore(ParameterValueGroup params){
        super(params);
        path = org.apache.sis.parameter.Parameters.castOrWrap(params).getValue(KMLFeatureStoreFactory.PATH);
    }
    
    private static ParameterValueGroup toParameters(final Path f,final String namespace){
        final ParameterValueGroup params = KMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(KMLFeatureStoreFactory.PATH, params).setValue(f.toUri());
        Parameters.getOrCreate(KMLFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        return params;
    }
    
    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(KMLFeatureStoreFactory.NAME);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false, false);
    }
    
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        scanTypes();
        return types.keySet();
    }

    @Override
    public FeatureType getFeatureType(GenericName gn) throws DataStoreException {
        typeCheck(gn);
        return types.get(gn);
    }

    private KmlType read() throws DataStoreException {
        final KmlType kml;
        final MarshallerPool pool = KMLMarshallerPool.getInstance();
        try {
            //fix old google namespace
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(path.toString());
            renameNamespaceRecursive(doc, doc.getDocumentElement(), "http://earth.google.com/kml/2.2", "http://www.opengis.net/kml/2.2");
            
            //unmarshall file
            final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
            Object cdt = unmarshaller.unmarshal(doc);
            if (cdt instanceof JAXBElement) cdt = ((JAXBElement)cdt).getValue();
            kml = (KmlType) cdt;
            pool.recycle(unmarshaller);
        } catch(FileNotFoundException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        } catch(JAXBException | ParserConfigurationException | SAXException | IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
        return kml;
    }
    
    private static void renameNamespaceRecursive(Document doc, Node node, String oldNamespace, String newNamespace) {

        if (node.getNodeType() == Node.ELEMENT_NODE && oldNamespace.equalsIgnoreCase(node.getNamespaceURI())) {
            doc.renameNode(node, newNamespace, node.getNodeName());
        }

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            renameNamespaceRecursive(doc, list.item(i), oldNamespace, newNamespace);
        }
    }
    
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        final GenericName typeName = query.getTypeName();
        typeCheck(typeName);
        
        //check we can read the file
        final KmlType kml = read();

        final List<PlacemarkType> placemarks = new ArrayList<>();
        extractPlacemarks(kml.getAbstractFeatureGroup(), placemarks);

        int i=0;
        final List<Feature> features = new ArrayList<>();
        for(PlacemarkType pt : placemarks){
            final Feature f = FeatureUtilities.defaultFeature(placemarkType, "kml."+i++);
            convert(pt, f);
            features.add(f);
        }
            
        final FeatureReader reader = GenericWrapFeatureIterator.wrapToReader(features.iterator(), placemarkType);
            
        return handleRemaining(reader, query);
    }

    private synchronized void scanTypes() throws DataStoreException {
        if(types!=null) return;
        //check it works
        final KmlType kml = read();
        
        final AbstractFeatureType base = kml.getAbstractFeatureGroup().getValue();
        String typeName = null;
        if(base instanceof DocumentType){
            typeName = ((DocumentType)base).getName();
        }
        if(typeName==null) typeName = DEFAULT_NAME;
        
        final FeatureTypeBuilder ftb =  new FeatureTypeBuilder();
        ftb.setName(typeName);
        ftb.add("name", String.class);
        ftb.add("visibility", Boolean.class);
        ftb.add("open", Boolean.class);
        //feature.getProperty("author").setValue(placemark.getAuthor());
        //feature.getProperty("link").setValue(placemark.getLink());
        ftb.add("address", String.class);
        //feature.getProperty("addressDetails").setValue(placemark.getName());
        ftb.add("phoneNumber", String.class);
        //feature.getProperty("snippet").setValue(placemark.getSnippet());
        //feature.getProperty("snippetDenominator").setValue(placemark.getSnippetDenominator());
        ftb.add("description", String.class);
        //feature.getProperty("abstractViewGroup").setValue(placemark.getAbstractViewGroup());
        //feature.getProperty("abstractTimePrimitiveGroup").setValue(placemark.getAbstractTimePrimitiveGroup());
        ftb.add("styleUrl", String.class);
        //feature.getProperty("abstractStyleSelectorGroup").setValue(placemark.getAbstractStyleSelectorGroup());
        //feature.getProperty("region").setValue(placemark.getRegion());
        //feature.getProperty("metadata").setValue(placemark.getMetadata());
        //feature.getProperty("extendedData").setValue(placemark.getExtendedData());
        
        ftb.add("the_geom", Geometry.class, CRS);
        placemarkType = ftb.buildFeatureType();
        
        types = new HashMap<>();
        types.put(placemarkType.getName(), placemarkType);
    }
    
    /**
     * Loop in KML object and collect Placemarks.
     * 
     * @param candidate
     * @param placemarks 
     */
    private static void extractPlacemarks(Object candidate, List<PlacemarkType> placemarks) {
        if (candidate instanceof JAXBElement) candidate = ((JAXBElement)candidate).getValue();
        
        if (candidate instanceof AbstractContainerType) {
            final AbstractContainerType ct = (AbstractContainerType) candidate;
            final List<JAXBElement<? extends AbstractFeatureType>> children = ct.getAbstractFeatureGroup();
            if (children!=null) {
                for(Object o : children){
                    extractPlacemarks(o,placemarks);
                }
            }
        } else if(candidate instanceof PlacemarkType) {
            placemarks.add((PlacemarkType) candidate);
        } else if(candidate instanceof PhotoOverlayType) {
            //todo
        } else if(candidate instanceof GroundOverlayType) {
            //todo
        }
    }
    
    private static void convert(PlacemarkType placemark, Feature feature) throws DataStoreException {
                
        feature.getProperty("name").setValue(placemark.getName());
        feature.getProperty("visibility").setValue(Boolean.TRUE.equals(placemark.isVisibility()));
        feature.getProperty("open").setValue(Boolean.TRUE.equals(placemark.isOpen()));
        //feature.getProperty("author").setValue(placemark.getAuthor());
        //feature.getProperty("link").setValue(placemark.getLink());
        feature.getProperty("address").setValue(placemark.getAddress());
        //feature.getProperty("addressDetails").setValue(placemark.getName());
        feature.getProperty("phoneNumber").setValue(placemark.getPhoneNumber());
        //feature.getProperty("snippet").setValue(placemark.getSnippet());
        //feature.getProperty("snippetDenominator").setValue(placemark.getSnippetDenominator());
        feature.getProperty("description").setValue(placemark.getDescription());
        //feature.getProperty("abstractViewGroup").setValue(placemark.getAbstractViewGroup());
        //feature.getProperty("abstractTimePrimitiveGroup").setValue(placemark.getAbstractTimePrimitiveGroup());
        feature.getProperty("styleUrl").setValue(placemark.getStyleUrl());
        //feature.getProperty("abstractStyleSelectorGroup").setValue(placemark.getAbstractStyleSelectorGroup());
        //feature.getProperty("region").setValue(placemark.getRegion());
        //feature.getProperty("metadata").setValue(placemark.getMetadata());
        //feature.getProperty("extendedData").setValue(placemark.getExtendedData());
        
        
        //convert geometry to JTS
        final Geometry geom = convert(placemark.getAbstractGeometryGroup());
        if(geom!=null) {
            feature.getProperty("the_geom").setValue(geom);
        }
    }
    
    private static Geometry convert(Object geomType) throws DataStoreException {
        if(geomType instanceof JAXBElement) geomType = ((JAXBElement)geomType).getValue();
        
        Geometry geom = null;
        if (geomType instanceof ModelType) {
            final ModelType modelType = (ModelType) geomType;
            final LocationType location = modelType.getLocation();
            geom = GF.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));
            
        } else if (geomType instanceof PointType) {
            final PointType pointType = (PointType) geomType;
            final List<String> coordinates = pointType.getCoordinates();
            geom = GF.createPoint(toCoordinates(coordinates, 1, false));
            
        } else if (geomType instanceof PolygonType) {
            final PolygonType polygonType = (PolygonType) geomType;
            final CoordinateSequence outter = toCoordinates(polygonType.getOuterBoundaryIs().getLinearRing().getCoordinates(), 3, true);
            final List<BoundaryType> inners = polygonType.getInnerBoundaryIs();
            final LinearRing[] holes = new LinearRing[inners.size()];
            for(int i=0;i<holes.length;i++){
                holes[i] = GF.createLinearRing(toCoordinates(inners.get(i).getLinearRing().getCoordinates(), 3, true));
            }
            geom = GF.createPolygon(GF.createLinearRing(outter), holes);
            
        } else if (geomType instanceof LinearRingType) {
            final LinearRingType linearRingType = (LinearRingType) geomType;
            geom = GF.createLineString(toCoordinates(linearRingType.getCoordinates(), 3, true));
            
        } else if (geomType instanceof MultiGeometryType) {
            final MultiGeometryType multigeometryType = (MultiGeometryType) geomType;
            final List<JAXBElement<? extends AbstractGeometryType>> children = multigeometryType.getAbstractGeometryGroup();
            final Geometry[] childs = new Geometry[children.size()];
            for (int i=0;i<childs.length;i++) {
                childs[i] = convert(children.get(i));
            }
            geom = GF.createGeometryCollection(childs);
            
        } else if (geomType instanceof LineStringType) {
            final LineStringType lineStringType = (LineStringType) geomType;
            geom = GF.createLineString(toCoordinates(lineStringType.getCoordinates(), 2, false));
            
        }
        
        if(geom!=null) {
            JTS.setCRS(geom, CRS);
        }
        
        return geom;
    }
    
    private static CoordinateSequence toCoordinates(List<String> coordinates, int minPoint, boolean close) throws DataStoreException{
        final List<Coordinate> coords = new ArrayList<>();
        try {
            for (String c : coordinates) {
                final String[] parts = c.split(",");
                switch (parts.length) {
                    case 2:
                        coords.add(new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1])));
                        break;
                    case 3:
                        coords.add(new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2])));
                        break;
                    default:
                        throw new DataStoreException("Unvalid coordinate size " +parts.length);
                }
            }
        } catch(NumberFormatException ex){
            throw new DataStoreException(ex.getMessage(), ex);
        }
        
        while (coordinates.size() < minPoint) {
            coordinates.add(coordinates.get(coordinates.size()-1));
        }
        
        if (close && !coordinates.get(0).equals(coordinates.get(coordinates.size()-1))) {
            coordinates.add(coordinates.get(0));
        }
        
        return new PackedCoordinateSequence.Double(coords.toArray(new Coordinate[coords.size()]));
    }
    
    @Override
    public void refreshMetaModel() {
    }
    
    // WRITE OPERATIONS NOT SUPPORTED //////////////////////////////////////////
    
    
    @Override
    public void createFeatureType(GenericName gn, FeatureType ft) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void updateFeatureType(GenericName gn, FeatureType ft) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void deleteFeatureType(GenericName gn) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }
    
    @Override
    public List<FeatureId> addFeatures(GenericName gn, Collection<? extends Feature> clctn, Hints hints) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void updateFeatures(GenericName gn, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> map) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void removeFeatures(GenericName gn, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }
    
    @Override
    public FeatureWriter getFeatureWriter(GenericName gn, Filter filter, Hints hints) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }
    
}
