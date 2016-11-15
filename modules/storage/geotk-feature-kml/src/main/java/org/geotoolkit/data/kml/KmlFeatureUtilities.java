package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.data.kml.model.AbstractGeometry;
import org.geotoolkit.data.kml.model.Boundary;
import org.geotoolkit.data.kml.model.Data;
import org.geotoolkit.data.kml.model.DefaultExtendedData;
import org.geotoolkit.data.kml.model.DefaultMultiGeometry;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PosixDirectoryFilter;
import org.geotoolkit.nio.ZipUtilities;
import org.opengis.referencing.operation.TransformException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyType;
import org.geotoolkit.data.kml.xml.KmlConstants;

/**
 * Generate a {@link org.opengis.feature.Feature} {@link java.util.List} from kml/kmz folder or file
 * @author bgarcia
 * @since 11/04/13
 */
public class KmlFeatureUtilities {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.kml");

    /**
     * create {@link SimpleFeature} from kml or kmz file on a folder
     * @param directory folder which have kml files
     * @return a {@link SimpleFeature} {@link List}
     */
    public static List<Feature> getAllKMLGeometriesEntries(final Path directory) throws IOException {
        final List<Feature> results = new ArrayList<>();

        if (Files.isDirectory(directory)) {

            //first loop to unzip kmz files
            try(DirectoryStream<Path> filteredStream = Files.newDirectoryStream(directory, new PosixDirectoryFilter("*.kmz", true))) {
                for (Path path : filteredStream) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE, "getAllKMLGeometriesEntries unzipping kmz file : {0}", path.getFileName().toString());
                    }
                    ZipUtilities.unzip(path, null);
                }

            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Error on unzip kmz file", ex);
            }

            final KmlReader reader = new KmlReader();
            try {
                //first loop to unzip kmz files recursively
                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                        final String ext = IOUtilities.extension(file);

                        if ("kml".equalsIgnoreCase(ext)) {
                            if (LOGGER.isLoggable(Level.FINE)) {
                                LOGGER.log(Level.FINE, "getAllKMLGeometriesEntries  proceed to extract features for kml : {0}",
                                        file.getFileName().toString());
                            }

                            try {
                                // create kml reader for current file
                                reader.setInput(file);
                                reader.setUseNamespace(false);
                                final Kml kmlObject = reader.read();

                                // find features and add it on lisr
                                final List<Feature> simplefeatList = resolveFeaturesFromKml(kmlObject);
                                results.addAll(simplefeatList);
                            } catch (Exception ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }

                        return FileVisitResult.CONTINUE;
                    }
                });
            } finally {
                try {
                    reader.dispose();
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return results;
    }

    /**
     * get {@link SimpleFeature} {@link List} from a {@link Kml} object
     * @param kmlObject : object which can have feature to extract
     * @return {@link SimpleFeature} {@link List} include in kml file
     */
    public static List<Feature> resolveFeaturesFromKml(final Kml kmlObject) {
        final List<Feature> results = new ArrayList<>();
        if (kmlObject != null) {
            final Feature document = kmlObject.getAbstractFeature();
            final Iterator<?> propertiesFeat = ((Iterable<?>) document.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();

            //increment for each features
            int idgeom = 0;

            //loop on document properties
            while (propertiesFeat.hasNext()) {
                final Object object = propertiesFeat.next();
                if (object instanceof Feature) {
                    final Feature candidat = (Feature) object;

                    //find geometry on tree
                    final List<Map.Entry<Object, Map<String, String>>> geometries = new ArrayList<>();
                    fillGeometryListFromFeature(candidat, geometries);

                    //if geometry was found
                    if (!geometries.isEmpty()) {
                        //loop to create simpleFeature from geometry
                        for (final Map.Entry<Object, Map<String, String>> geometry : geometries) {
                            Feature simpleFeature = extractFeature(idgeom, geometry);

                            //test if geometry already exist
                            if(simpleFeature!=null){
                                if (!results.contains(simpleFeature)) {
                                    results.add(simpleFeature);
                                    idgeom++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * create a {@link SimpleFeature} from an {@link Map.Entry}
     * @param idgeom current id iterator
     * @param geometry : {@link Map.Entry} which contains a geometry
     * @return a {@link SimpleFeature}
     */
    private static Feature extractFeature(int idgeom, Map.Entry<Object, Map<String, String>> geometry) {
        final Object geom = geometry.getKey();
        Geometry finalGeom = null;
        //if it's a simple geometry
        if (geom instanceof Geometry) {
            finalGeom = (Geometry) geometry.getKey();
            try {
                //if it's lineString it can cut meridian. So test it
                if (finalGeom instanceof LineString) {
                    LineString lineString = (LineString) finalGeom;
                    MultiLineString multiLine = cutAtMeridian(lineString);
                    if(multiLine!=null){
                        finalGeom = multiLine;
                    }
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        //it's a geometry collection
        else if(geom instanceof DefaultMultiGeometry){
            final DefaultMultiGeometry kmlabstractGeometry = (DefaultMultiGeometry)geom;
            final List<Geometry> multiGeometry = new ArrayList<>(0);

            //loop on geometry to add id on a GeometryList
            for (AbstractGeometry abstractGeometry : kmlabstractGeometry.getGeometries()) {
                if(abstractGeometry instanceof Geometry){
                    final Geometry currentGeom = (Geometry)abstractGeometry;
                    multiGeometry.add(currentGeom);
                }
            }

            final GeometryFactory gf = new GeometryFactory();
            Geometry[] geometryArray = new Geometry[multiGeometry.size()];
            for (int i = 0; i < multiGeometry.size(); i++) {
                geometryArray[i] = multiGeometry.get(i);

            }
            finalGeom = new GeometryCollection(geometryArray, gf);
        }

        if(finalGeom!=null){
            return BuildSimpleFeature(idgeom, geometry.getValue(), finalGeom);
        }
        return null;
    }

    /**
     * Build simple feature
     * @param idgeom geometry id
     * @param values no geographic data
     * @param finalGeom geometry need to be insert in feature
     * @return a {@link SimpleFeature}
     */
    private static Feature BuildSimpleFeature(int idgeom, Map<String, String> values, Geometry finalGeom) {
        //Building simplefeature
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final String name = "Geometry";
        ftb.setName(name);
        ftb.addAttribute(Geometry.class).setName("geometry").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);

        //loop on values to find data names
        for (String valName : values.keySet()) {
            ftb.addAttribute(String.class).setName(valName);
        }


        final FeatureType sft = ftb.build();
        final Feature simpleFeature = sft.newInstance();
        simpleFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "feature" + idgeom);

        //add geometry
        simpleFeature.setPropertyValue("geometry", finalGeom);

        //add other data
        for (String valName : values.keySet()) {
            simpleFeature.setPropertyValue(valName, values.get(valName));
        }

        return simpleFeature;
    }

    /**
     * recursive method on feature to find geometries
     * @param feature {@link org.opengis.feature.Feature} traveled to find geometry
     * @param geometries {@link List} where we add {@link Geometry}
     */
    public static void fillGeometryListFromFeature(final Feature feature, final List<Map.Entry<Object, Map<String, String>>> geometries) {
        final Object geometry = feature.getPropertyValue("geometry");
        if (geometry != null) {
            // create map which have other data value
            final Map<String, String> values = new HashMap<>(0);

            // get feature name
            final String name = (String) feature.getPropertyValue("name");
            if(name!=null){
                values.put("name", name);
            }

            //get extendedData
            final DefaultExtendedData extendData = (DefaultExtendedData) feature.getPropertyValue("ExtendedData");
            if(extendData!=null){

                // loop on extendedSchemaData to find data
                final List<SchemaData> schemaDatas =  extendData.getSchemaData();
                for (SchemaData schemaData : schemaDatas) {

                    //get simples data to add it on values map
                    final List<SimpleData> simpleDataList = schemaData.getSimpleDatas();
                    for (SimpleData simpleData : simpleDataList) {
                        values.put(simpleData.getName(), simpleData.getContent());
                    }
                }
            }

            //add geometry on list
            geometries.add(new AbstractMap.SimpleEntry<Object, Map<String, String>>(geometry, values));

        // it's a folder, go recursivly on childs
        } else {
            final Iterator<?> iterator = ((Iterable<?>) feature.getPropertyValue(KmlConstants.TAG_FEATURES)).iterator();
            while (iterator.hasNext()) {
                final Object object = iterator.next();
                if (object instanceof Feature) {
                    final Feature candidat = (Feature) object;

                    //recursive call
                    fillGeometryListFromFeature(candidat, geometries);
                }
            }
        }
    }

    /**
     * Cut a lineString if the anti-meridien intersect the line and return a multiLineString
     * column type of geometry from lineString to MultiLineString
     *
     * @param geom {@link LineString} tested
     * @return a {@link MultiLineString} if {@link LineString} cut meridian else <code>null</code>
     * @throws org.opengis.referencing.operation.TransformException
     */
    public static MultiLineString cutAtMeridian(LineString geom) throws TransformException {
        final GeometryFactory gf = geom.getFactory();
        final Geometry clip = gf.createPolygon(
                gf.createLinearRing(
                        new Coordinate[]{
                                new Coordinate(-180, 90),
                                new Coordinate(180, 90),
                                new Coordinate(180, -90),
                                new Coordinate(-180, -90),
                                new Coordinate(-180, 90)}),
                new LinearRing[0]);
        final Geometry rightMeridian = gf.createPolygon(
                gf.createLinearRing(
                        new Coordinate[]{
                                new Coordinate(180, 90),
                                new Coordinate(360, 90),
                                new Coordinate(360, -90),
                                new Coordinate(180, -90),
                                new Coordinate(180, 90)}),
                new LinearRing[0]);
        final Geometry leftMeridian = gf.createPolygon(
                gf.createLinearRing(
                        new Coordinate[]{
                                new Coordinate(-180, 90),
                                new Coordinate(-360, 90),
                                new Coordinate(-360, -90),
                                new Coordinate(-180, -90),
                                new Coordinate(-180, 90)}),
                new LinearRing[0]);

        Geometry cutright = rightMeridian.intersection(geom);
        Geometry cutleft = leftMeridian.intersection(geom);
        Geometry clipped = clip.intersection(geom);

        final List<LineString> strs = new ArrayList<>();

        if (cutright instanceof LineString) {
            final AffineTransform2D trs = new AffineTransform2D(1, 0, 0, 1, -180, 0);
            cutright = JTS.transform(cutright, trs);
            strs.add((LineString) cutright);
        }
        if (cutleft instanceof LineString) {
            final AffineTransform2D trs = new AffineTransform2D(1, 0, 0, 1, +180, 0);
            cutleft = JTS.transform(cutleft, trs);
            strs.add((LineString) cutleft);
        }
        if (clipped instanceof LineString) {
            strs.add((LineString) clipped);
        }

        //if strs.size<0, geometry don't need to change his value
        if(strs.size()>0){
            MultiLineString ml = gf.createMultiLineString(strs.toArray(new LineString[strs.size()]));

            //second pass to cut points which already cross the meridien properly
            strs.clear();
            for (int i = 0; i < ml.getNumGeometries(); i++) {
                final LineString ls = (LineString) ml.getGeometryN(i);
                final Coordinate[] coords = ls.getCoordinates();

                int from = 0;
                int end = 1;
                while (end < coords.length) {
                    if (Math.abs(coords[end - 1].x - coords[end].x) > 160) {
                        final Coordinate[] cut = Arrays.copyOfRange(coords, from, end);
                        if (cut.length > 1) {
                            final LineString cls = gf.createLineString(cut);
                            strs.add(cls);
                            from = end;
                        }
                    }
                    end++;
                }

                if ((end - from) > 1) {
                    final Coordinate[] cut = Arrays.copyOfRange(coords, from, end);
                    final LineString cls = gf.createLineString(cut);
                    strs.add(cls);
                }

            }

            ml = gf.createMultiLineString(strs.toArray(new LineString[strs.size()]));
            return ml;
        }

        return null;
    }

    /**
     * create a {@link Geometry from a Feature.}
     *
     * @param noKmlFeature feature from an other type
     * @param defaultIdStyle style defined on document.
     * @return a valid kml {@link Feature}
     */
    public static Feature buildKMLFeature(Feature noKmlFeature, IdAttributes defaultIdStyle){
        //Transform geometry
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();
        final Feature placemark = kmlFactory.createPlacemark();
        final String geoColumn = AttributeConvention.GEOMETRY_PROPERTY.tip().toString();
        final AbstractGeometry ag = buildKMLGeometry((Geometry) noKmlFeature.getPropertyValue(geoColumn));
        placemark.setPropertyValue(KmlConstants.TAG_GEOMETRY, ag);

        try {
            placemark.setPropertyValue(KmlConstants.TAG_STYLE_URL, new URI("#" + defaultIdStyle.getId()));
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "unnable to define style URI", e);
        }

        //TODO : transform datas
        final List<Data> simpleDatas = new ArrayList<>(0);
        for (final PropertyType type : noKmlFeature.getType().getProperties(true)) {
            final Property property = noKmlFeature.getProperty(type.getName().toString());
            String localPartName = property.getName().tip().toString();
            final Object value = property.getValue();
            if (localPartName.equalsIgnoreCase(KmlConstants.TAG_NAME)) {
                placemark.setPropertyValue(KmlConstants.TAG_NAME, value);
            } else if (!(localPartName.equalsIgnoreCase(geoColumn) || localPartName.equalsIgnoreCase("fid"))) {
                if (value != null) {
                    Data simpleData = kmlFactory.createData();
                    simpleData.setName(localPartName);
                    simpleData.setValue(value.toString());
                    simpleDatas.add(simpleData);
                }
            }
        }
        if (!simpleDatas.isEmpty()) {
            ExtendedData extendedData = kmlFactory.createExtendedData();
            extendedData.setDatas(simpleDatas);
            placemark.setPropertyValue(KmlConstants.TAG_EXTENDED_DATA, extendedData);
        }
        return placemark;
    }

    private static AbstractGeometry buildKMLGeometry(Geometry geometry) {
        Class<?> geometryClass = geometry.getClass();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();
        if (geometryClass.equals(LineString.class)) {
            LineString ls = (LineString) geometry;
            org.geotoolkit.data.kml.model.LineString lineString = kmlFactory.createLineString(ls.getCoordinateSequence());
            return lineString;
        } else if (geometryClass.equals(Point.class)) {
            Point point = (Point) geometry;
            org.geotoolkit.data.kml.model.Point kmlPoint = kmlFactory.createPoint(point.getCoordinateSequence());
            return kmlPoint;
        } else if (geometryClass.equals(Polygon.class)) {
            final Polygon poly = (Polygon) geometry;

            // interiorRing
            final List<Boundary> innerBoundaries = new ArrayList<>(0);
            int innerRing = poly.getNumInteriorRing();
            if (innerRing>0) {
                for (int i = 0; i < innerRing; i++) {
                    final LineString inner = poly.getInteriorRingN(i);
                    final org.geotoolkit.data.kml.model.LinearRing innerLineRing = kmlFactory.createLinearRing(inner.getCoordinateSequence());
                    final Boundary innerBoundary = kmlFactory.createBoundary(innerLineRing, null, null);
                    innerBoundaries.add(innerBoundary);
                }
            }

            //exterior ring
            final org.geotoolkit.data.kml.model.LinearRing lr = kmlFactory.createLinearRing(poly.getExteriorRing().getCoordinateSequence());
            final Boundary boundary = kmlFactory.createBoundary(lr, null, null);
            final org.geotoolkit.data.kml.model.Polygon kmlPolygon = kmlFactory.createPolygon(boundary, innerBoundaries);
            return kmlPolygon;
        } else if (GeometryCollection.class.isAssignableFrom(geometryClass)) {
            final GeometryCollection geoCollec = (GeometryCollection)geometry;
            final MultiGeometry mg = kmlFactory.createMultiGeometry();
            final List<AbstractGeometry> geometries = new ArrayList<>(0);
            for (int i = 0; i < geoCollec.getNumGeometries(); i++) {
                Geometry currentGeometry = geoCollec.getGeometryN(i);
                AbstractGeometry ag = buildKMLGeometry(currentGeometry);
                geometries.add(ag);
            }
            mg.setGeometries(geometries);
            return mg;
        }
        return null;
    }
}
