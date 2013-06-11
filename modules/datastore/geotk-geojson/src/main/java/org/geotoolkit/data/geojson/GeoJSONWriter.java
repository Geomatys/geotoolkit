package org.geotoolkit.data.geojson;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import net.sf.json.JSONObject;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.opengis.feature.Feature;
import org.opengis.geometry.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Contains method to write GeoJSON geometries from {@link com.vividsolutions.jts.geom.Geometry} or {@link org.opengis.geometry.Geometry}.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 08/04/13
 */
public final class GeoJSONWriter {

    public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private GeoJSONWriter() {}

    public static Map<String, Object> geometryToGeoJSON(final Object source) throws NonconvertibleObjectException {
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();

        Object tmpSrc = toWellKnownGeometry(source);

        final String geomName = tmpSrc.getClass().getSimpleName();
        try {
            GeometryTypes.valueOf(geomName);
        } catch (IllegalArgumentException e) {
            throw new NonconvertibleObjectException("unsupported geometry type ["+ geomName +"] have been given for GeoJSON conversion.");
        }
        jsonMap.put("type", geomName);

        final Geometry jtsGeometry = (Geometry) tmpSrc;

        if(jtsGeometry instanceof Point) {
            jsonMap.put("coordinates", new double[]{((Point)jtsGeometry).getX(), ((Point)jtsGeometry).getY()});

        } else if(jtsGeometry instanceof LineString) {
            final Coordinate[] points = ((LineString)jtsGeometry).getCoordinates();
            ArrayList<double[]> linePts = new ArrayList<double[]>(points.length);
            for(Coordinate c : points) {
                linePts.add(new double[]{c.x, c.y});
            }
            jsonMap.put("coordinates", linePts);

        } else if(jtsGeometry instanceof Polygon) {
            final Polygon polygon = (Polygon)jtsGeometry;
            final ArrayList<ArrayList> rings = new ArrayList<ArrayList>(polygon.getNumInteriorRing()+1);

            // hull
            final Coordinate[] exterior = polygon.getExteriorRing().getCoordinates();
            ArrayList<double[]> extRing = new ArrayList<double[]>(exterior.length);
            for(Coordinate c : exterior) {
                extRing.add(new double[]{c.x, c.y});
            }
            rings.add(extRing);

            // holes
            for(int i = 0 ; i < polygon.getNumInteriorRing() ; i++) {
                final Coordinate[] interior = polygon.getInteriorRingN(i).getCoordinates();
                ArrayList<double[]> intRing = new ArrayList<double[]>(interior.length);
                for(Coordinate c : interior) {
                    intRing.add(new double[]{c.x, c.y});
                }
                rings.add(intRing);
            }

            jsonMap.put("coordinates", rings);

        } else if (jtsGeometry instanceof MultiPoint) {
            final MultiPoint multiPt = (MultiPoint) jtsGeometry;
            final ArrayList<double[]> coordinates = new ArrayList<double[]>(multiPt.getNumGeometries());
            for (int i = 0 ; i < multiPt.getNumGeometries() ; i++) {
                final Coordinate c = multiPt.getGeometryN(i).getCoordinate();
                coordinates.add(new double[]{c.x, c.y});
            }
            jsonMap.put("coordinates", coordinates);

        } else if (jtsGeometry instanceof MultiLineString) {
            MultiLineString multiLine = (MultiLineString) jtsGeometry;
            final ArrayList<ArrayList> lines = new ArrayList<ArrayList>(multiLine.getNumGeometries());
            for(int i = 0 ; i < multiLine.getNumGeometries() ; i++) {
                final Coordinate[] points = multiLine.getGeometryN(i).getCoordinates();
                ArrayList<double[]> linePts = new ArrayList<double[]>(points.length);
                for(Coordinate c : points) {
                    linePts.add(new double[]{c.x, c.y});
                }
                lines.add(linePts);
            }
            jsonMap.put("coordinates", lines);

        } else if (jtsGeometry instanceof MultiPolygon) {
            MultiPolygon region = (MultiPolygon) jtsGeometry;
            final ArrayList<ArrayList> polygons = new ArrayList<ArrayList>(region.getNumGeometries());
            for (int i = 0; i < region.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) region.getGeometryN(i);
                final ArrayList<ArrayList> rings = new ArrayList<ArrayList>(polygon.getNumInteriorRing() + 1);

                // hull
                final Coordinate[] exterior = polygon.getExteriorRing().getCoordinates();
                ArrayList<double[]> extRing = new ArrayList<double[]>(exterior.length);
                for (Coordinate c : exterior) {
                    extRing.add(new double[]{c.x, c.y});
                }
                rings.add(extRing);

                // holes
                for (int holeCount = 0; holeCount < polygon.getNumInteriorRing(); holeCount++) {
                    final Coordinate[] interior = polygon.getInteriorRingN(holeCount).getCoordinates();
                    ArrayList<double[]> intRing = new ArrayList<double[]>(interior.length);
                    for (Coordinate c : interior) {
                        intRing.add(new double[]{c.x, c.y});
                    }
                    rings.add(intRing);
                }
                polygons.add(rings);
            }
            jsonMap.put("coordinates", polygons);

        } else if(jtsGeometry instanceof GeometryCollection) {
            ArrayList< Map<String, Object> > geometries = new ArrayList<Map<String, Object>>(jtsGeometry.getNumGeometries());
            for(int geomCount = 0 ; geomCount < jtsGeometry.getNumGeometries() ; geomCount++) {
                geometries.add(geometryToGeoJSON(jtsGeometry.getGeometryN(geomCount)));
            }
            jsonMap.put("geometries", geometries);

        } else {
            throw new NonconvertibleObjectException("Given geometry is of unknown type : "+ jtsGeometry.getClass().getName());
        }

        return jsonMap;
    }


    /**
     * Try to convert the given geometry object to comprehensive object for json writer.
     * @param source
     * @return
     */
    public static Object toWellKnownGeometry(final Object source) {
        Object target = source;
        // JTS geometry case.
        if (source instanceof Coordinate) {
            target = GEOMETRY_FACTORY.createPoint((Coordinate) source);
        } else if (source instanceof Envelope) {
            target = GEOMETRY_FACTORY.toGeometry((Envelope)source);
        } else if (source instanceof Triangle) {
            final LinearRing ext = GEOMETRY_FACTORY.createLinearRing(new Coordinate[]{((Triangle) source).p0, ((Triangle) source).p1, ((Triangle) source).p2});
            target = GEOMETRY_FACTORY.createPolygon(ext, null);
        }

        // OpenGIS case.
        if(source instanceof org.opengis.geometry.Envelope) {
            org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) source;
            Envelope jtsEnv = new Envelope(env.getMinimum(0), env.getMaximum(0), env.getMinimum(1), env.getMaximum(1));
            target = GEOMETRY_FACTORY.toGeometry(jtsEnv);
        }

        return target;
    }

    /**
     * Convert a Feature into GeoJSON object
     * @param toConvert the feature to get a {@link java.util.LinkedHashMap} ready to be GeoJSONed.
     * @return A map containing all needed information for JSON creation.
     */
    private static Map<String,Object> featureToGeoJSON(Feature toConvert) throws NonconvertibleObjectException {
        final Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("type", "feature");

        final Map<String, Object> properties = FeatureUtilities.toMap(toConvert);

        if(toConvert.getDefaultGeometryProperty() != null) {
            final Map<String, Object> geometry = geometryToGeoJSON(toConvert.getDefaultGeometryProperty().getValue());
            jsonMap.put("geometry", geometry);
            properties.remove(toConvert.getDefaultGeometryProperty().getName().getLocalPart());
        }

        if(properties.size() > 0) {
            jsonMap.put("properties", properties);
        }

        return jsonMap;
    }

    /**
     * Convert a given object into GeoJSON object.
     * @param source An object to convert. For now, only {@link org.opengis.feature.Feature}, {@link FeatureCollection} and {@link org.opengis.geometry.Geometry}
     *               objects are managed.
     * @return A string representation of the generated GeoJSON.
     * @throws org.geotoolkit.util.converter.NonconvertibleObjectException If the input object is of unknown type.
     */
    public static String toGeoJSON(Object source) throws NonconvertibleObjectException {
        final Map<String, Object> jsonMap;
        if(source instanceof FeatureCollection) {
            jsonMap = new LinkedHashMap<String, Object>();
            FeatureCollection fColl = (FeatureCollection) source;
            jsonMap.put("type", "FeatureCollection");
            jsonMap.put("totalResults", fColl.size());

            ArrayList<Map<String, Object> > features = new ArrayList<Map<String, Object>>(fColl.size());
            for (Iterator<Feature> it = fColl.iterator(); it.hasNext(); ) {
                features.add(featureToGeoJSON(it.next()));
            }
            jsonMap.put("features", features);

        } else if(source instanceof Feature) {
            jsonMap = featureToGeoJSON((Feature) source);
        } else if (source instanceof Geometry) {
            jsonMap = geometryToGeoJSON(source);
        } else {
            throw new NonconvertibleObjectException("No JSON conversion found for object type : "+source.getClass());
        }

        return JSONObject.fromObject(jsonMap).toString(2);
    }

}