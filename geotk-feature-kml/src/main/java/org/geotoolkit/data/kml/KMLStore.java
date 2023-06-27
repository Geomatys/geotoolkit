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
package org.geotoolkit.data.kml;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.SimpleInternationalString;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.kml.xml.KMLMarshallerPoolV230;
import org.geotoolkit.kml.xml.v230.AbstractContainerType;
import org.geotoolkit.kml.xml.v230.AbstractExtentType;
import org.geotoolkit.kml.xml.v230.AbstractFeatureType;
import org.geotoolkit.kml.xml.v230.AbstractGeometryType;
import org.geotoolkit.kml.xml.v230.BoundaryType;
import org.geotoolkit.kml.xml.v230.GroundOverlayType;
import org.geotoolkit.kml.xml.v230.KmlType;
import org.geotoolkit.kml.xml.v230.LatLonAltBoxType;
import org.geotoolkit.kml.xml.v230.LatLonBoxType;
import org.geotoolkit.kml.xml.v230.LatLonQuadType;
import org.geotoolkit.kml.xml.v230.LineStringType;
import org.geotoolkit.kml.xml.v230.LinearRingType;
import org.geotoolkit.kml.xml.v230.LinkType;
import org.geotoolkit.kml.xml.v230.LocationType;
import org.geotoolkit.kml.xml.v230.ModelType;
import org.geotoolkit.kml.xml.v230.MultiGeometryType;
import org.geotoolkit.kml.xml.v230.NetworkLinkType;
import org.geotoolkit.kml.xml.v230.PhotoOverlayType;
import org.geotoolkit.kml.xml.v230.PlacemarkType;
import org.geotoolkit.kml.xml.v230.PointType;
import org.geotoolkit.kml.xml.v230.PolygonType;
import org.geotoolkit.kml.xml.v230.ScreenOverlayType;
import org.geotoolkit.kml.xml.v230.TourType;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
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
    static final String NETWORKLINK_NAME = "NetworkLink";
    static final String PHOTOOVERLAY_NAME = "PhotoOverlay";
    static final String SCREENOVERLAY_NAME = "ScreenOverlay";
    static final String GROUNDOVERLAY_NAME = "GroundOverlay";
    static final String TOUR_NAME = "Tour";

    public static final FeatureType ABSTRACT_FEATURE_TYPE;
    public static final FeatureType PLACEMARK_TYPE;
    public static final FeatureType NETWORKLINK_TYPE;
    public static final FeatureType PHOTOOVERLAY_TYPE;
    public static final FeatureType SCREENOVERLAY_TYPE;
    public static final FeatureType GROUNDOVERLAY_TYPE;
    public static final FeatureType TOUR_TYPE;

    static {
        { //Abstract type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(ABSTRACT_FEATURE_NAME);
            ftb.addAttribute(String.class).setName("id");
            ftb.addAttribute(String.class).setName("name");
            ftb.addAttribute(String.class).setName("address");
            ftb.addAttribute(String.class).setName("addressDetails");
            ftb.addAttribute(String.class).setName("phoneNumber");
            ftb.addAttribute(String.class).setName("description");
            ftb.addAttribute(String.class).setName("styleUrl");
            ftb.addAttribute(String.class).setName("targetId");
            ftb.addAttribute(Boolean.class).setName("visibility");
            ftb.addAttribute(Boolean.class).setName("open");
            ftb.addAttribute(Boolean.class).setName("balloonVisibility");
            ABSTRACT_FEATURE_TYPE = ftb.build();
        }

        { // Placemark type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(PLACEMARK_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
            PLACEMARK_TYPE = ftb.build();
        }

        { // NetworkLink type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(NETWORKLINK_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            NETWORKLINK_TYPE = ftb.build();
        }

        { // PhotoOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(PHOTOOVERLAY_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            PHOTOOVERLAY_TYPE = ftb.build();
        }

        { // ScreenOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(SCREENOVERLAY_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            SCREENOVERLAY_TYPE = ftb.build();
        }

        { // GroundOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(GROUNDOVERLAY_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            ftb.addAttribute(Polygon.class).setName("geometry").setCRS(CRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
            ftb.addAttribute(GridCoverage.class).setName("icon");
            GROUNDOVERLAY_TYPE = ftb.build();
        }

        { // Tour type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(TOUR_NAME);
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            TOUR_TYPE = ftb.build();
        }
    }

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
    public Optional<GenericName> getIdentifier() {
        return Optional.empty();
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(KMLProvider.NAME);
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(params);
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
        return PLACEMARK_TYPE;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final KmlType kml = getKml();

        final List<AbstractFeatureType> kmlFeatures = new ArrayList<>();
        extractKmlFeatures(kml.getAbstractFeatureGroup(), kmlFeatures);

        final List<Feature> features = new ArrayList<>();
        for (AbstractFeatureType candidate : kmlFeatures) {
            if (candidate instanceof PlacemarkType cdt) {
                features.add(convert(cdt));
            } else if(candidate instanceof PhotoOverlayType cdt) {
                features.add(convert(cdt));
            } else if(candidate instanceof GroundOverlayType cdt) {
                features.add(convert(cdt));
            } else if(candidate instanceof ScreenOverlayType cdt) {
                features.add(convert(cdt));
            } else if(candidate instanceof TourType cdt) {
                features.add(convert(cdt));
            } else if(candidate instanceof NetworkLinkType cdt) {
                features.add(convert(cdt));
            }
        }

        return parallel ? features.parallelStream() : features.stream();
    }

    /**
     * Get the original KML object.
     */
    public KmlType getKml() throws DataStoreException {
        final KmlType kml;
        final MarshallerPool pool = KMLMarshallerPoolV230.getINSTANCE();
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

    /**
     * Loop in KML object and collect Placemarks.
     *
     * @param candidate
     * @param list
     */
    private static void extractKmlFeatures(Object candidate, List<AbstractFeatureType> list) {
        if (candidate instanceof JAXBElement) candidate = ((JAXBElement)candidate).getValue();

        if (candidate instanceof AbstractContainerType) {
            final AbstractContainerType ct = (AbstractContainerType) candidate;
            final List<JAXBElement<? extends AbstractFeatureType>> children = ct.getAbstractFeatureGroup();
            if (children != null) {
                for  (Object o : children){
                    extractKmlFeatures(o,list);
                }
            }
        } else if(candidate instanceof AbstractFeatureType cdt) {
            list.add(cdt);
        }
    }

    private Feature convert(PlacemarkType candidate) throws DataStoreException {
        final Feature feature = PLACEMARK_TYPE.newInstance();
        fillFeature(candidate, feature);
        //convert geometry to JTS
        final Geometry geom = convert(candidate.getAbstractGeometryGroup());
        if (geom != null) {
            feature.setPropertyValue("geometry", geom);
        }
        return feature;
    }

    private Feature convert(PhotoOverlayType candidate) throws DataStoreException {
        final Feature feature = PHOTOOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(GroundOverlayType candidate) throws DataStoreException {
        final Feature feature = GROUNDOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        final LinkType icon = candidate.getIcon();
        final JAXBElement<? extends AbstractExtentType> abs = candidate.getAbstractExtentGroup();

        if (icon != null && abs != null) {
            final String str = icon.getHref();
            try {
                final URI uri = IOUtilities.resolve(path, new URI(str));
                final StorageConnector cnx = new StorageConnector(uri);
                try (InputStream in = cnx.getStorageAs(InputStream.class)) {
                    final BufferedImage image = ImageIO.read(in);

                    final GeneralEnvelope env = new GeneralEnvelope(CRS);
                    final AbstractExtentType extent = abs.getValue();
                    double rotation = 0.0;
                    if (extent instanceof LatLonBoxType llb) {
                        final Double east = llb.getEast();
                        final Double west = llb.getWest();
                        final Double north = llb.getNorth();
                        final Double south = llb.getSouth();
                        env.setRange(0, west == null ? -180.0 : west, east == null ? 180.0 : east);
                        env.setRange(1, south, north);
                        rotation = llb.getRotation();

                    } else if (extent instanceof LatLonAltBoxType llb) {
                        final Double east = llb.getEast();
                        final Double west = llb.getWest();
                        final Double north = llb.getNorth();
                        final Double south = llb.getSouth();
                        env.setRange(0, west, east);
                        env.setRange(1, south, north);

                    } else if (extent instanceof LatLonQuadType llb) {
                        throw new DataStoreException("Unsupported");
                    }

                    final GridCoverageBuilder gcb = new GridCoverageBuilder();
                    gcb.setValues(image);
                    gcb.setDomain(env);
                    GridCoverage coverage = gcb.build();

                    if (rotation != 0) {
                        /*
                        Specifies a rotation of the overlay about its center, in degrees.
                        Values can be ±180. The default is 0 (north).
                        Rotations are specified in a counterclockwise direction.
                         */
                        GridGeometry gridGeometry = coverage.getGridGeometry();
                        MathTransform gridToCRS = gridGeometry.getGridToCRS(PixelInCell.CELL_CENTER);
                        final GridExtent extent1 = gridGeometry.getExtent();
                        final MathTransform rotateTrs = new AffineTransform2D(AffineTransform.getRotateInstance(
                                Math.toRadians(rotation),
                                extent1.getSize(0) / 2.0,
                                extent1.getSize(1) / 2.0));
                        gridToCRS = MathTransforms.concatenate(gridToCRS, rotateTrs);
                        gridGeometry = new GridGeometry(extent1, PixelInCell.CELL_CENTER, gridToCRS, env.getCoordinateReferenceSystem());
                        gcb.setDomain(gridGeometry);
                        coverage = gcb.build();
                    }

                    feature.setPropertyValue("icon", coverage);
                }
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return feature;
    }

    private Feature convert(ScreenOverlayType candidate) throws DataStoreException {
        final Feature feature = SCREENOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(TourType candidate) throws DataStoreException {
        final Feature feature = TOUR_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(NetworkLinkType candidate) throws DataStoreException {
        final Feature feature = NETWORKLINK_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private void fillFeature(AbstractFeatureType kmlFeature, Feature feature) throws DataStoreException {
        feature.setPropertyValue("id", kmlFeature.getId());
        feature.setPropertyValue("name", kmlFeature.getName());
        feature.setPropertyValue("address", kmlFeature.getAddress());
        feature.setPropertyValue("addressDetails", kmlFeature.getAddressDetails());
        feature.setPropertyValue("phoneNumber", kmlFeature.getPhoneNumber());
        feature.setPropertyValue("description", kmlFeature.getDescription());
        feature.setPropertyValue("styleUrl", kmlFeature.getStyleUrl());
        feature.setPropertyValue("targetId", kmlFeature.getTargetId());
        feature.setPropertyValue("visibility", Boolean.TRUE.equals(kmlFeature.isVisibility()));
        feature.setPropertyValue("open", Boolean.TRUE.equals(kmlFeature.isOpen()));
        feature.setPropertyValue("balloonVisibility", Boolean.TRUE.equals(kmlFeature.isBalloonVisibility()));
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
    public void close() throws DataStoreException {
    }
}
