/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-1013, Geomatys
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
package org.geotoolkit.data.geojson;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;
import net.sf.json.JSONObject;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureUtilities;
import org.opengis.feature.Feature;
import org.apache.sis.util.UnconvertibleObjectException;

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

    public static Map<String, Object> geometryToGeoJSON(final Object source) throws UnconvertibleObjectException {
        final Map<String, Object> jsonMap = new LinkedHashMap<>();

        final Object tmpSrc = toWellKnownGeometry(source);

        final String geomName = tmpSrc.getClass().getSimpleName();
        try {
            GeometryTypes.valueOf(geomName);
        } catch (IllegalArgumentException e) {
            throw new UnconvertibleObjectException("unsupported geometry type ["+ geomName +"] have been given for GeoJSON conversion.");
        }
        jsonMap.put("type", geomName);

        final Geometry jtsGeometry = (Geometry) tmpSrc;

        if(jtsGeometry instanceof Point) {
            jsonMap.put("coordinates", new double[]{((Point)jtsGeometry).getX(), ((Point)jtsGeometry).getY()});

        } else if(jtsGeometry instanceof LineString) {
            final CoordinateSequence points = ((LineString)jtsGeometry).getCoordinateSequence();
            final ArrayList<double[]> linePts = new ArrayList<>(points.size());
            for(int i = 0, n = points.size(); i < n; i++) {
                final Coordinate c = points.getCoordinate(i);
                linePts.add(new double[]{c.x, c.y});
            }
            jsonMap.put("coordinates", linePts);

        } else if(jtsGeometry instanceof Polygon) {
            final Polygon polygon = (Polygon)jtsGeometry;
            final ArrayList<ArrayList> rings = new ArrayList<>(polygon.getNumInteriorRing()+1);

            // hull
            final CoordinateSequence exterior = polygon.getExteriorRing().getCoordinateSequence();
            final ArrayList<double[]> extRing = new ArrayList<>(exterior.size());
            for(int i = 0, n = exterior.size(); i < n; i++) {
                final Coordinate c = exterior.getCoordinate(i);
                extRing.add(new double[]{c.x, c.y});
            }
            rings.add(extRing);

            // holes
            for(int i = 0, n = polygon.getNumInteriorRing()  ; i < n ; i++) {
                final CoordinateSequence interior = polygon.getInteriorRingN(i).getCoordinateSequence();
                final ArrayList<double[]> intRing = new ArrayList<>(interior.size());
                for(int k = 0, l = interior.size(); k < l; k++) {
                    final Coordinate c = interior.getCoordinate(k);
                    intRing.add(new double[]{c.x, c.y});
                }
                rings.add(intRing);
            }

            jsonMap.put("coordinates", rings);

        } else if (jtsGeometry instanceof MultiPoint) {
            final MultiPoint multiPt = (MultiPoint) jtsGeometry;
            final ArrayList<double[]> coordinates = new ArrayList<>(multiPt.getNumGeometries());
            for (int i = 0, n = multiPt.getNumGeometries() ; i < n ; i++) {
                final Coordinate c = multiPt.getGeometryN(i).getCoordinate();
                coordinates.add(new double[]{c.x, c.y});
            }
            jsonMap.put("coordinates", coordinates);

        } else if (jtsGeometry instanceof MultiLineString) {
            final MultiLineString multiLine = (MultiLineString) jtsGeometry;
            final ArrayList<ArrayList> lines = new ArrayList<>(multiLine.getNumGeometries());
            for(int i = 0, n = multiLine.getNumGeometries() ; i < n ; i++) {
                final Coordinate[] points = multiLine.getGeometryN(i).getCoordinates();
                final ArrayList<double[]> linePts = new ArrayList<>(points.length);
                for(Coordinate c : points) {
                    linePts.add(new double[]{c.x, c.y});
                }
                lines.add(linePts);
            }
            jsonMap.put("coordinates", lines);

        } else if (jtsGeometry instanceof MultiPolygon) {
            final MultiPolygon region = (MultiPolygon) jtsGeometry;
            final ArrayList<ArrayList> polygons = new ArrayList<>(region.getNumGeometries());
            for (int i = 0, n = region.getNumGeometries(); i < n; i++) {
                final Polygon polygon = (Polygon) region.getGeometryN(i);
                final ArrayList<ArrayList> rings = new ArrayList<>(polygon.getNumInteriorRing() + 1);

                // hull
                final CoordinateSequence exterior = polygon.getExteriorRing().getCoordinateSequence();
                final ArrayList<double[]> extRing = new ArrayList<>(exterior.size());
                for(int k = 0, l = exterior.size(); k < l; k++) {
                    final Coordinate c = exterior.getCoordinate(k);
                    extRing.add(new double[]{c.x, c.y});
                }
                rings.add(extRing);

                // holes
                for (int holeCount = 0, k = polygon.getNumInteriorRing(); holeCount < k; holeCount++) {
                    final CoordinateSequence interior = polygon.getInteriorRingN(holeCount).getCoordinateSequence();
                    final ArrayList<double[]> intRing = new ArrayList<>(interior.size());
                    for(int r = 0, l = interior.size(); r < l; r++) {
                    final Coordinate c = interior.getCoordinate(r);
                        intRing.add(new double[]{c.x, c.y});
                    }
                    rings.add(intRing);
                }
                polygons.add(rings);
            }
            jsonMap.put("coordinates", polygons);

        } else if(jtsGeometry instanceof GeometryCollection) {
            final ArrayList< Map<String, Object> > geometries = new ArrayList<>(jtsGeometry.getNumGeometries());
            for(int geomCount = 0 , n = jtsGeometry.getNumGeometries(); geomCount < n ; geomCount++) {
                geometries.add(geometryToGeoJSON(jtsGeometry.getGeometryN(geomCount)));
            }
            jsonMap.put("geometries", geometries);

        } else {
            throw new UnconvertibleObjectException("Given geometry is of unknown type : "+ jtsGeometry.getClass().getName());
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
            final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) source;
            final Envelope jtsEnv = new Envelope(env.getMinimum(0), env.getMaximum(0), env.getMinimum(1), env.getMaximum(1));
            target = GEOMETRY_FACTORY.toGeometry(jtsEnv);
        }

        return target;
    }

    /**
     * Convert a Feature into GeoJSON object
     * @param toConvert the feature to get a {@link java.util.LinkedHashMap} ready to be GeoJSONed.
     * @return A map containing all needed information for JSON creation.
     */
    private static Map<String,Object> featureToGeoJSON(Feature toConvert) throws UnconvertibleObjectException {
        final Map<String, Object> jsonMap = new LinkedHashMap<>();
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
    public static String toGeoJSON(Object source) throws UnconvertibleObjectException {
        final Map<String, Object> jsonMap;
        if(source instanceof FeatureCollection) {
            jsonMap = new LinkedHashMap<>();
            final FeatureCollection fColl = (FeatureCollection) source;
            jsonMap.put("type", "FeatureCollection");
            jsonMap.put("totalResults", fColl.size());

            final ArrayList<Map<String, Object> > features = new ArrayList<>(fColl.size());
            for (Iterator<Feature> it = fColl.iterator(); it.hasNext(); ) {
                features.add(featureToGeoJSON(it.next()));
            }
            jsonMap.put("features", features);

        } else if(source instanceof Feature) {
            jsonMap = featureToGeoJSON((Feature) source);
        } else if (source instanceof Geometry) {
            jsonMap = geometryToGeoJSON(source);
        } else {
            throw new UnconvertibleObjectException("No JSON conversion found for object type : "+source.getClass());
        }

        return JSONObject.fromObject(jsonMap).toString(2);
    }

}