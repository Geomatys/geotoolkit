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
import org.geotoolkit.data.kml.model.DefaultIdAttributes;
import org.geotoolkit.data.kml.model.DefaultMultiGeometry;
import org.geotoolkit.data.kml.model.ExtendedData;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LineStyle;
import org.geotoolkit.data.kml.model.MultiGeometry;
import org.geotoolkit.data.kml.model.PolyStyle;
import org.geotoolkit.data.kml.model.SchemaData;
import org.geotoolkit.data.kml.model.SimpleData;
import org.geotoolkit.data.kml.model.Style;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.FileUtilities;
import org.opengis.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.TransformException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generate a {@link org.opengis.feature.simple.SimpleFeature} {@link java.util.List} from kml/kmz folder or file
 * @author bgarcia
 * @since 11/04/13
 */
public class KmlFeatureUtilities {

    private static final Logger LOGGER = Logger.getLogger(KmlFeatureUtilities.class.getName());


    /**
     * create {@link SimpleFeature} from kml or kmz file on a folder
     * @param directory folder which have kml files
     * @return a {@link SimpleFeature} {@link List}
     */
    public static List<SimpleFeature> getAllKMLGeometriesEntries(final File directory) {
        final List<SimpleFeature> results = new ArrayList<SimpleFeature>();
        final KmlReader reader = new KmlReader();

        //first loop to unzip kmz files
        for (File f : directory.listFiles()) {
            final String fileName = f.getName();
            if (fileName.endsWith(".kmz")) {
                try {
                    if(LOGGER.isLoggable(Level.FINE)){
                        LOGGER.log(Level.FINE, "getAllKMLGeometriesEntries unzipping kmz file : {0}", fileName);
                    }
                    FileUtilities.unzip(f, null);

                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Error on unzip kmz file", ex);
                }
            }
        }

        //loop on kml files
        for (final File f : directory.listFiles()) {
            final String fileName = f.getName();
            if (fileName.endsWith(".kml")) {
                if(LOGGER.isLoggable(Level.FINE)){
                    LOGGER.log(Level.FINE, "getAllKMLGeometriesEntries  proceed to extract features for kml : {0}", fileName);
                }

                try {
                    // create kml reader for current file
                    reader.setInput(f);
                    reader.setUseNamespace(false);
                    final Kml kmlObject = reader.read();

                    // find features and add it on lisr
                    final List<SimpleFeature> simplefeatList = resolveFeaturesFromKml(kmlObject);
                    results.addAll(simplefeatList);
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            } else if (f.isDirectory()) {
                //recursive call to find other kml/kmz files
                results.addAll(getAllKMLGeometriesEntries(f));
            }
        }
        try {
            reader.dispose();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
        return results;
    }

    /**
     * get {@link SimpleFeature} {@link List} from a {@link Kml} object
     * @param kmlObject : object which can have feature to extract
     * @return {@link SimpleFeature} {@link List} include in kml file
     */
    public static List<SimpleFeature> resolveFeaturesFromKml(final Kml kmlObject) {
        final List<SimpleFeature> results = new ArrayList<SimpleFeature>();
        if (kmlObject != null) {
            final org.opengis.feature.Feature document = kmlObject.getAbstractFeature();
            final Iterator propertiesFeat = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();

            //increment for each features
            int idgeom = 0;

            //loop on document properties
            while (propertiesFeat.hasNext()) {
                final Object object = propertiesFeat.next();
                if (object instanceof org.opengis.feature.Feature) {
                    final org.opengis.feature.Feature candidat = (org.opengis.feature.Feature) object;

                    //find geometry on tree
                    final List<Map.Entry<Object, Map<String, String>>> geometries = new ArrayList<Map.Entry<Object, Map<String, String>>>();
                    fillGeometryListFromFeature(candidat, geometries);

                    //if geometry was found
                    if (!geometries.isEmpty()) {
                        //loop to create simpleFeature from geometry
                        for (final Map.Entry<Object, Map<String, String>> geometry : geometries) {
                            SimpleFeature simpleFeature = extractFeature(idgeom, geometry);

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
    private static SimpleFeature extractFeature(int idgeom, Map.Entry<Object, Map<String, String>> geometry) {
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
            final List<Geometry> multiGeometry = new ArrayList<Geometry>(0);

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
    private static SimpleFeature BuildSimpleFeature(int idgeom, Map<String, String> values, Geometry finalGeom) {
        //Building simplefeature
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final String name = "Geometry";
        ftb.setName(name);
        ftb.add("geometry", Geometry.class, DefaultGeographicCRS.WGS84);

        //loop on values to find data names
        for (String valName : values.keySet()) {
            ftb.add(valName, String.class);
        }


        final SimpleFeatureType sft = ftb.buildSimpleFeatureType();
        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(sft);

        //Clear the feature builder before creating a new feature.
        sfb.reset();

        //add geometry
        sfb.set("geometry", finalGeom);

        //add other data
        for (String valName : values.keySet()) {
            sfb.set(valName, values.get(valName));
        }

        //create simple feature
        final SimpleFeature simpleFeature = sfb.buildFeature("feature" + idgeom);
        simpleFeature.validate();
        return simpleFeature;
    }

    /**
     * recursive method on feature to find geometries
     * @param feature {@link org.opengis.feature.Feature} traveled to find geometry
     * @param geometries {@link List} where we add {@link Geometry}
     */
    public static void fillGeometryListFromFeature(final org.opengis.feature.Feature feature, final List<Map.Entry<Object, Map<String, String>>> geometries) {

        //If geometry is not null
        if (feature.getProperty("geometry") != null) {

            // get geometry
            final Object geometry = feature.getProperty("geometry").getValue();

            // create map which have other data value
            final Map<String, String> values = new HashMap<String, String>(0);

            // get feature name
            final String name = (String) feature.getProperty("name").getValue();
            if(name!=null){
                values.put("name", name);
            }

            //get extendedData
            final DefaultExtendedData extendData = (DefaultExtendedData)feature.getProperty("ExtendedData").getValue();
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
        } else if (feature.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()) != null) {
            final Iterator iterator = feature.getProperties(KmlModelConstants.ATT_FOLDER_FEATURES.getName()).iterator();
            while (iterator.hasNext()) {
                final Object object = iterator.next();
                if (object instanceof org.opengis.feature.Feature) {
                    final org.opengis.feature.Feature candidat = (org.opengis.feature.Feature) object;

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

        final List<LineString> strs = new ArrayList<LineString>();

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
        final FeatureFactory FF = FactoryFinder.getFeatureFactory(
                new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();
        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        final AbstractGeometry ag = buildKMLGeometry((Geometry) noKmlFeature.getDefaultGeometryProperty().getValue());
        placemarkProperties.add(FF.createAttribute(ag, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));
        final String geoColumn = noKmlFeature.getDefaultGeometryProperty().getName().getLocalPart();

        URI styleURI = null;
        try {
            styleURI = new URI("#"+defaultIdStyle.getId());
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "unnable to define style URI", e);
        }

        if(styleURI!=null){
            placemarkProperties.add((FF.createAttribute(styleURI, KmlModelConstants.ATT_STYLE_URL, null)));
        }

        //TODO : transform datas
        final Collection<Property> properties = noKmlFeature.getProperties();
        final List<Data> simpleDatas = new ArrayList<Data>(0);

        if(properties.size()>0){
            ExtendedData extendedData = kmlFactory.createExtendedData();

            for (Property property : properties) {
                String localPartName = property.getName().getLocalPart();
                if(localPartName.equalsIgnoreCase("NAME")){
                    placemarkProperties.add(FF.createAttribute(property.getValue(), KmlModelConstants.ATT_NAME, null));
                }else if(!(localPartName.equalsIgnoreCase(geoColumn) || localPartName.equalsIgnoreCase("fid"))){
                    if(property.getValue()!=null){
                        Data simpleData = kmlFactory.createData();
                        simpleData.setName(localPartName);
                        simpleData.setValue(property.getValue().toString());
                        simpleDatas.add(simpleData);
                    }
                }
            }
            extendedData.setDatas(simpleDatas);
            placemarkProperties.add(FF.createAttribute(extendedData, KmlModelConstants.ATT_EXTENDED_DATA, null));
        }

        return placemark;
    }

    /**
     *
     * @param geometry
     * @return
     */
    private static AbstractGeometry buildKMLGeometry(Geometry geometry) {
        Class geometryClass = geometry.getClass();
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        if(geometryClass.equals(LineString.class)){
            LineString ls = (LineString)geometry;
            org.geotoolkit.data.kml.model.LineString lineString = kmlFactory.createLineString(ls.getCoordinateSequence());
            return lineString;

        }else if(geometryClass.equals(Point.class)){
            Point point = (Point)geometry;
            org.geotoolkit.data.kml.model.Point kmlPoint = kmlFactory.createPoint(point.getCoordinateSequence());
            return kmlPoint;
            
        }else if(geometryClass.equals(Polygon.class)){
            final Polygon poly = (Polygon)geometry;

            // interiorRing
            final List<Boundary> innerBoundaries = new ArrayList<Boundary>(0);
            int innerRing = poly.getNumInteriorRing();
            if(innerRing>0){
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
            
        }else if(GeometryCollection.class.isAssignableFrom(geometryClass)){
            final GeometryCollection geoCollec = (GeometryCollection)geometry;
            final MultiGeometry mg = kmlFactory.createMultiGeometry();
            final List<AbstractGeometry> geometries = new ArrayList<AbstractGeometry>(0);
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
