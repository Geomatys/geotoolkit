/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017-2023, Geomatys
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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.storage.base.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.iso.Names;
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
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
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
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class KMLStore extends DataStore implements Aggregate, ResourceOnFileSystem {

    private static final GeometryFactory GF = GO2Utilities.JTS_FACTORY;
    private static final CoordinateReferenceSystem CRS = CommonCRS.WGS84.normalizedGeographic(); //CRS:84 , longitude/latitude

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
            ftb.setName("AbstractFeature");
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
            ftb.setName("Placemark");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
            PLACEMARK_TYPE = ftb.build();
        }

        { // NetworkLink type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("NetworkLink");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            NETWORKLINK_TYPE = ftb.build();
        }

        { // PhotoOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("PhotoOverlay");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            PHOTOOVERLAY_TYPE = ftb.build();
        }

        { // ScreenOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("ScreenOverlay");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            SCREENOVERLAY_TYPE = ftb.build();
        }

        { // GroundOverlay type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("GroundOverlay");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            ftb.addAttribute(Polygon.class).setName("geometry").setCRS(CRS).addRole(AttributeRole.DEFAULT_GEOMETRY);
            ftb.addAttribute(GridCoverage.class).setName("icon");
            GROUNDOVERLAY_TYPE = ftb.build();
        }

        { // Tour type
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("Tour");
            ftb.setSuperTypes(ABSTRACT_FEATURE_TYPE);
            TOUR_TYPE = ftb.build();
        }
    }

    private final URI path;
    private final GenericName name;
    private final ParameterValueGroup params;
    private final List<FeatureSet> components;

    public KMLStore(URI path) {
        this(toParameters(path));
    }

    public KMLStore(ParameterValueGroup params){
        this.params = params;
        this.path = Parameters.castOrWrap(params).getValue(KMLProvider.LOCATION_PARAM);
        this.components = List.of(
                new KmlFeatureSet(PLACEMARK_TYPE, PlacemarkType.class),
                new KmlFeatureSet(GROUNDOVERLAY_TYPE, GroundOverlayType.class));
        this.name = Names.createLocalName(null, null, IOUtilities.filename(path));
    }

    private static ParameterValueGroup toParameters(final URI f) {
        final Parameters params = Parameters.castOrWrap(KMLProvider.provider().getOpenParameters().createValue());
        params.getOrCreate(KMLProvider.LOCATION_PARAM).setValue(f);
        return params;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.of(name);
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
        return new DefaultMetadata();
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return components;
    }

    /**
     * Extract and transform to features all kml features of requested type.
     */
    private Stream<Feature> features(boolean parallel, Class<? extends AbstractFeatureType> clazz) throws DataStoreException {
        final Entry<Object, FileSystem> entry = openKmlResource();
        final Object kmlPath = entry.getKey();
        final FileSystem kmzFs = entry.getValue();

        final KmlType kml;
        try {
            kml = readKml(kmlPath);
        } catch (DataStoreException ex) {
            if (kmzFs != null) {
                try {
                    kmzFs.close();
                } catch (IOException e) {
                    ex.addSuppressed(e);
                }
            }
            throw ex;
        }

        final List<AbstractFeatureType> kmlFeatures = new ArrayList<>();
        extractKmlFeatures(kml.getAbstractFeatureGroup(), kmlFeatures);

        final List<Feature> features = new ArrayList<>();
        for (AbstractFeatureType candidate : kmlFeatures) {
            if (clazz.isInstance(candidate)) {
                if (candidate instanceof PlacemarkType cdt) {
                    features.add(convert(cdt, kmzFs));
                } else if(candidate instanceof PhotoOverlayType cdt) {
                    features.add(convert(cdt, kmzFs));
                } else if(candidate instanceof GroundOverlayType cdt) {
                    features.add(convert(cdt, kmzFs));
                } else if(candidate instanceof ScreenOverlayType cdt) {
                    features.add(convert(cdt, kmzFs));
                } else if(candidate instanceof TourType cdt) {
                    features.add(convert(cdt, kmzFs));
                } else if(candidate instanceof NetworkLinkType cdt) {
                    features.add(convert(cdt, kmzFs));
                }
            }
        }

        Stream<Feature> stream = parallel ? features.parallelStream() : features.stream();
        return stream.onClose(new Runnable() {
            @Override
            public void run() {
                if (kmzFs != null) {
                    try {
                        kmzFs.close();
                    } catch (IOException ex) {
                        Logger.getLogger(KMLStore.class.getName()).log(Level.WARNING, "Failed to close KMZ zip filesystem", ex);
                    }
                }
            }
        });
    }

    /**
     * Get the original KML object.
     */
    public Entry<Object,FileSystem> openKmlResource() throws DataStoreException {
        if (path.toString().toLowerCase().endsWith(".kmz")) {

            try {
                final FileSystem fs = FileSystems.newFileSystem(Paths.get(path));
                final Path root = fs.getPath("/");
                Path kmlPath;
                try (Stream<Path> stream = Files.list(root)) {
                    /*
                    https://developers.google.com/kml/documentation/kmzarchives
                    Put the default KML file (doc.kml, or whatever name you want to give it) at the top level within this folder.
                    Include only one .kml file. (When Google Earth opens a KMZ file, it scans the file, looking for the first .kml
                    file in this list. It ignores all subsequent .kml files, if any, in the archive. If the archive contains
                    multiple .kml files, you cannot be sure which one will be found first, so you need to include only one.)
                    */
                    kmlPath = stream.filter((Path t) -> t.getFileName().toString().toLowerCase().endsWith(".kml")).findFirst().orElse(null);
                } catch (IOException ex) {
                    fs.close();
                    throw new DataStoreException(ex.getMessage(), ex);
                }

                if (kmlPath == null) {
                    fs.close();
                    throw new DataStoreException("No KML file found in root directory of KMZ : " + path.toString());
                }

                return new AbstractMap.SimpleImmutableEntry<>(kmlPath, fs);
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        } else {
            return new AbstractMap.SimpleImmutableEntry<>(path,null);
        }
    }

    /**
     * Get the original KML object.
     */
    public KmlType readKml(Object input) throws DataStoreException {
        final KmlType kml;
        final MarshallerPool pool = KMLMarshallerPoolV230.getINSTANCE();
        try {
            final StorageConnector cnx = new StorageConnector(input);
            try (final InputStream stream = cnx.commit(InputStream.class, "kml")) {
                //fix old google namespace
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document doc = builder.parse(stream);
                renameNamespaceRecursive(doc, doc.getDocumentElement(), "http://earth.google.com/kml/2.2", "http://www.opengis.net/kml/2.2");

                //unmarshall file
                final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
                Object cdt = unmarshaller.unmarshal(doc);
                if (cdt instanceof JAXBElement) cdt = ((JAXBElement)cdt).getValue();
                kml = (KmlType) cdt;
                pool.recycle(unmarshaller);
            }
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (JAXBException | ParserConfigurationException | SAXException | IOException ex) {
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

    private Feature convert(PlacemarkType candidate, FileSystem kmzFs) throws DataStoreException {
        final Feature feature = PLACEMARK_TYPE.newInstance();
        fillFeature(candidate, feature);
        //convert geometry to JTS
        final Geometry geom = convert(candidate.getAbstractGeometryGroup());
        if (geom != null) {
            feature.setPropertyValue("geometry", geom);
        }
        return feature;
    }

    private Feature convert(PhotoOverlayType candidate, FileSystem kmzFs) throws DataStoreException {
        final Feature feature = PHOTOOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(GroundOverlayType candidate, FileSystem kmzFs) throws DataStoreException {
        final Feature feature = GROUNDOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        final LinkType icon = candidate.getIcon();
        final JAXBElement<? extends AbstractExtentType> abs = candidate.getAbstractExtentGroup();

        if (icon != null && abs != null) {
            final String str = icon.getHref();
            try {
                final StorageConnector cnx;
                URI iconUri = new URI(str);
                if (iconUri.isAbsolute()) {
                    cnx = new StorageConnector(iconUri);
                } else {
                    if (kmzFs != null) {
                        Path root = kmzFs.getPath("/");
                        cnx = new StorageConnector(root.resolve(iconUri.toString()));
                    } else {
                        iconUri = IOUtilities.resolve(path, iconUri);
                        cnx = new StorageConnector(iconUri);
                    }
                }

                try (InputStream in = cnx.getStorageAs(InputStream.class)) {
                    final BufferedImage image = ImageIO.read(in);
                    final int width = image.getWidth();
                    final int height = image.getHeight();

                    final GeneralEnvelope env = new GeneralEnvelope(CRS);
                    final AbstractExtentType extent = abs.getValue();
                    double rotation = 0.0;
                    if (extent instanceof LatLonBoxType llb) {
                        final Double east = llb.getEast();
                        final Double west = llb.getWest();
                        final Double north = llb.getNorth();
                        final Double south = llb.getSouth();
                        env.setRange(0, west == null ? -180.0 : west, east == null ? 180.0 : east);
                        env.setRange(1, south == null ? -90.0 : south, north == null ? 90 : north);
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

                    final GridGeometry grid = new GridGeometry(new GridExtent(width, height), env, GridOrientation.REFLECTION_Y);
                    final GridCoverageBuilder gcb = new GridCoverageBuilder();
                    gcb.setValues(image);
                    gcb.setDomain(grid);
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
                                Math.toRadians(-rotation),//counterclockwise, image is in reverse, y down
                                extent1.getSize(0) / 2.0,
                                extent1.getSize(1) / 2.0));
                        gridToCRS = MathTransforms.concatenate(rotateTrs, gridToCRS);
                        gridGeometry = new GridGeometry(extent1, PixelInCell.CELL_CENTER, gridToCRS, env.getCoordinateReferenceSystem());
                        gcb.setDomain(gridGeometry);
                        coverage = gcb.build();
                    }

                    MathTransform gridToCRS = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER);
                    Geometry polygon = new GeometryFactory().createPolygon(new Coordinate[]{
                        new Coordinate(0, 0),
                        new Coordinate(width, 0),
                        new Coordinate(width, height),
                        new Coordinate(0, height),
                        new Coordinate(0, 0)
                    });
                    polygon = org.apache.sis.geometry.wrapper.jts.JTS.transform(polygon, gridToCRS);
                    polygon.setUserData(CRS);

                    feature.setPropertyValue("icon", coverage);
                    feature.setPropertyValue("geometry", polygon);
                }
            } catch (TransformException  ex) {
                ex.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return feature;
    }

    private Feature convert(ScreenOverlayType candidate, FileSystem kmzFs) throws DataStoreException {
        final Feature feature = SCREENOVERLAY_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(TourType candidate, FileSystem kmzFs) throws DataStoreException {
        final Feature feature = TOUR_TYPE.newInstance();
        fillFeature(candidate, feature);
        //todo
        return feature;
    }

    private Feature convert(NetworkLinkType candidate, FileSystem kmzFs) throws DataStoreException {
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

    private final class KmlFeatureSet extends AbstractFeatureSet {

        private final Class<? extends AbstractFeatureType> entityType;
        private final FeatureType featureType;

        private KmlFeatureSet(FeatureType featureType, Class<? extends AbstractFeatureType> entityType) {
            super(null, false);
            this.featureType = featureType;
            this.entityType = entityType;
        }

        @Override
        public FeatureType getType() throws DataStoreException {
            return featureType;
        }

        @Override
        public Optional<Envelope> getEnvelope() throws DataStoreException {
            return Optional.ofNullable(FeatureStoreUtilities.getEnvelope(this, true));
        }

        @Override
        public Stream<Feature> features(boolean bln) throws DataStoreException {
            return KMLStore.this.features(bln, entityType);
        }
    }
}
