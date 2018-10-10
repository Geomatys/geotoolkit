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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.data.query.QueryFeatureSet;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.kml.xml.KMLMarshallerPool;
import org.geotoolkit.kml.xml.v220.AbstractContainerType;
import org.geotoolkit.kml.xml.v220.AbstractFeatureType;
import org.geotoolkit.kml.xml.v220.AbstractGeometryType;
import org.geotoolkit.kml.xml.v220.BoundaryType;
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
import org.geotoolkit.storage.DataStores;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
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
public class KMLStore extends DataStore implements FeatureSet, ResourceOnFileSystem {

    private static final GeometryFactory GF = GO2Utilities.JTS_FACTORY;
    private static final CoordinateReferenceSystem CRS = CommonCRS.WGS84.normalizedGeographic(); //CRS:84 , longitude/latitude
    static final String ABSTRACT_FEATURE_NAME = "AbstractFeatureType";
    static final String PLACEMARK_NAME = "Placemark";

    private static FeatureType ABSTRACT_FEATURE_TYPE;
    private static FeatureType PLACEMARK_TYPE;

    private final URI path;
    private final ParameterValueGroup params;

    public KMLStore(URI path) {
        this(toParameters(path));
    }

    public KMLStore(ParameterValueGroup params){
        this.params = params;
        this.path = Parameters.castOrWrap(params).getValue(KMLProvider.LOCATION_PARAM);
    }

    private static ParameterValueGroup toParameters(final URI f) {
        final Parameters params = Parameters.castOrWrap(KMLProvider.provider().getOpenParameters().createValue());
        params.getOrCreate(KMLProvider.LOCATION_PARAM).setValue(f);
        return params;
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(KMLProvider.NAME);
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return params;
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final FeatureType type = getType();
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification ident = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        citation.setTitle(new SimpleInternationalString(type.getName().toString()));
        citation.setIdentifiers(Arrays.asList(new NamedIdentifier(type.getName())));
        ident.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(ident));
        return metadata;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return getPlacemarkType();
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return null;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final KmlType kml = read();

        final List<PlacemarkType> placemarks = new ArrayList<>();
        extractPlacemarks(kml.getAbstractFeatureGroup(), placemarks);

        final List<Feature> features = new ArrayList<>();
        for (PlacemarkType pt : placemarks) {
            features.add(convert(pt));
        }

        return parallel ? features.parallelStream() : features.stream();
    }

    @Override
    public FeatureSet subset(Query query) throws UnsupportedQueryException, DataStoreException {
        if (query instanceof org.geotoolkit.data.query.Query) {
            return QueryFeatureSet.apply(this, (org.geotoolkit.data.query.Query)query);
        }
        return FeatureSet.super.subset(query);
    }

    private KmlType read() throws DataStoreException {
        final KmlType kml;
        final MarshallerPool pool = KMLMarshallerPool.getINSTANCE();
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

    private static synchronized FeatureType getAbstractFeatureType() {
        if (ABSTRACT_FEATURE_TYPE == null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(ABSTRACT_FEATURE_NAME);
            ftb.addAttribute(String.class).setName("name");
            ftb.addAttribute(Boolean.class).setName("visibility");
            ftb.addAttribute(Boolean.class).setName("open");
            ftb.addAttribute(String.class).setName("address");
            ftb.addAttribute(String.class).setName("phoneNumber");
            ftb.addAttribute(String.class).setName("description");
            ftb.addAttribute(String.class).setName("styleUrl");
            // TODO : add complex data as "kml:Region".

            ABSTRACT_FEATURE_TYPE = ftb.build();
        }

        return ABSTRACT_FEATURE_TYPE;
    }

    private static synchronized FeatureType getPlacemarkType() {
        if (PLACEMARK_TYPE == null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(PLACEMARK_NAME);
            ftb.setSuperTypes(getAbstractFeatureType());

            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
            PLACEMARK_TYPE = ftb.build();
        }

        return PLACEMARK_TYPE;
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

    private Feature convert(PlacemarkType placemark) throws DataStoreException {
        final Feature feature = getPlacemarkType().newInstance();
        feature.setPropertyValue("name", placemark.getName());
        feature.setPropertyValue("visibility", Boolean.TRUE.equals(placemark.isVisibility()));
        feature.setPropertyValue("open", Boolean.TRUE.equals(placemark.isOpen()));
        feature.setPropertyValue("address", placemark.getAddress());
        feature.setPropertyValue("phoneNumber", placemark.getPhoneNumber());
        feature.setPropertyValue("description", placemark.getDescription());
        feature.setPropertyValue("styleUrl", placemark.getStyleUrl());


        //convert geometry to JTS
        final Geometry geom = convert(placemark.getAbstractGeometryGroup());
        if(geom!=null) {
            feature.setPropertyValue("geometry", geom);
        }

        return feature;
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
                        throw new DataStoreException("Invalid coordinate size " +parts.length);
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
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{Paths.get(path)};
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public void close() throws DataStoreException {
    }

}
