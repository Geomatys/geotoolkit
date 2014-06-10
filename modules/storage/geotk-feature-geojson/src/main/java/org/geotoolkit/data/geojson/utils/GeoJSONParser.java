/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.geojson.binding.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.geotoolkit.data.geojson.utils.GeoJSONMembres.*;
import static org.geotoolkit.data.geojson.utils.GeoJSONTypes.*;
import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;

/**
 * Efficient GeoJSONParsing using jackson
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONParser {

    public static final JsonFactory FACTORY = new JsonFactory();
    public final static Logger LOGGER = Logging.getLogger(GeoJSONParser.class);

    private Boolean lazy;

    public GeoJSONParser() {
        this.lazy = Boolean.FALSE;
    }

    /**
     *
     * @param lazyParsing If lazyParsing is {@code true} and root object of parsed file
     *                    is a FeatureCollection, returned GeoJSONFeatureCollection will only have
     *                    start and end feature array location. Otherwise, all Feature will be parsed
     *                    and add to GeoJSONFeatureCollection. lazyParsing as {@code true} is recommended for
     *                    large json files.
     *
     */
    public GeoJSONParser(Boolean lazyParsing) {
        this.lazy = lazyParsing;
    }

    /**
     * Parse a json file and return a GeoJSONObject.
     * If parser was construct with lazyParsing as {@code true} and root object
     * is a FeatureCollection, returned GeoJSONFeatureCollection will only have
     * start and end feature array location.
     * Otherwise, all Feature will be parsed and add to GeoJSONFeatureCollection.
     *
     * @param jsonFile file to parse
     * @return GeoJSONObject
     * @throws IOException
     */
    public GeoJSONObject parse(File jsonFile) throws IOException {
        try (JsonParser p = FACTORY.createParser(jsonFile)) {
            JsonToken startToken = p.nextToken();
            assert (startToken == JsonToken.START_OBJECT) : "Input File is not a JSON file " + jsonFile.getAbsolutePath();
            return parseGeoJSONObject(p);
        }
    }

    /**
     * Parse a json InputStream and return a GeoJSONObject.
     * In InputStream case, lazy loading of FeatureCollection is
     * disabled.
     *
     * @param inputStream stream to parse
     * @return GeoJSONObject
     * @throws IOException
     */
    public GeoJSONObject parse(InputStream inputStream) throws IOException {
        this.lazy = false;
        try (JsonParser p = FACTORY.createParser(inputStream)) {
            JsonToken startToken = p.nextToken();
            assert (startToken == JsonToken.START_OBJECT) : "Input stream is not a valid JSON ";
            return parseGeoJSONObject(p);
        }
    }

    /**
     * Parse a GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * JsonParser location MUST be on a START_OBJECT token.
     * @param p parser jackson parser with current token on a START_OBJECT.
     * @return GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * @throws IOException
     */
    public GeoJSONObject parseGeoJSONObject(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_OBJECT);

        GeoJSONObject object = new GeoJSONObject();
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = p.getCurrentName();

            switch (fieldname) {
                case TYPE:
                    p.nextToken();
                    String value = p.getValueAsString();
                    object = getOrCreateFromType(object, value);
                    break;

                case BBOX:
                    //array
                    p.nextToken(); // "["
                    object.setBbox(parseBBoxArray(p));
                    break;

                case CRS:
                    //object
                    p.nextToken(); // "{"
                    object.setCrs(parseCRS(p));
                    break;

                case FEATURES:
                    object = getOrCreateFromType(object, FEATURE_COLLECTION);

                    //array of GeoJSONFeature
                    if (Boolean.TRUE.equals(lazy)) {
                        lazyParseFeatureCollection((GeoJSONFeatureCollection) object, p);
                    } else {
                        parseFeatureCollection((GeoJSONFeatureCollection) object, p);
                    }
                    break;

                case PROPERTIES:
                    object = getOrCreateFromType(object, FEATURE);
                    //object
                    parseProperties((GeoJSONFeature) object, p);
                    break;

                case GEOMETRY:
                    object = getOrCreateFromType(object, FEATURE);
                    //object
                    parseFeatureGeometry((GeoJSONFeature) object, p);
                    break;

                case COORDINATES:
                    //array
                    if (object instanceof GeoJSONGeometry) {
                        parseGeometry((GeoJSONGeometry) object, p);
                    } else {
                        LOGGER.log(Level.WARNING, "Error need type before coordinates");
                    }
                    break;

                case GEOMETRIES:
                    if (object instanceof GeoJSONGeometryCollection) {
                        object = getOrCreateFromType(object, GEOMETRY_COLLECTION);
                        //array of GeoJSONGeometry
                        parseGeometryCollection((GeoJSONGeometryCollection) object, p);
                    } else {
                        LOGGER.log(Level.WARNING, "Error need type before coordinates");
                    }
                    break;
            }
        }
        return object;
    }

    /**
     * Parse properties map and add to given Feature
     * @param feature Feature to attach properties
     * @param p parser
     * @throws IOException
     */
    private void parseProperties(GeoJSONFeature feature, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        feature.getProperties().putAll(parseMap(p));
    }

    /**
     * Parse a map of String, Object.
     * JsonParser location MUST be on a START_OBJECT token.
     * @param p parser jackson parser with current token on a START_OBJECT.
     * @return Map of String - Object
     * @throws IOException
     */
    public static Map<String, Object> parseMap(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_OBJECT);
        Map<String, Object> map = new HashMap<>();
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String key = p.getCurrentName();
            JsonToken next = p.nextToken();

            map.put(key, getValue(next, p));
        }
        return map;
    }

    /**
     * Parse a List of Objects.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p parser jackson parser with current token on a START_ARRAY.
     * @return List of Objects
     * @throws IOException
     */
    public static List<Object> parseArray(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        List<Object> list = new ArrayList<>();
        while (p.nextToken() != JsonToken.END_ARRAY) {
            list.add(getValue(p.getCurrentToken(), p));
        }
        return list;
    }

    /**
     * Parse a List of Objects.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p parser jackson parser with current token on a START_ARRAY.
     * @return List of Objects
     * @throws IOException
     */
    public static Object parseArray2(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        List<Object> list = new ArrayList<Object>();
        while (p.nextToken() != JsonToken.END_ARRAY) {
            list.add(getValue(p.getCurrentToken(), p));
        }

        if (list.isEmpty()) {
            return new Object[0];
        }

        Class binding = list.get(0).getClass();
        Object newArray = Array.newInstance(binding, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(newArray, i, list.get(i));
        }

        return newArray;
    }

    /**
     * Convert the current token to appropriate object.
     * Supported (String, Integer, Float, Boolean, Null, Array, Map)
     *
     * @param token current token
     * @param p parser
     * @return current token value String or Integer or Float or Boolean or null or an array or a map.
     * @throws IOException
     */
    public static Object getValue(JsonToken token, JsonParser p) throws IOException {
        if (token == JsonToken.VALUE_STRING) {
            return p.getValueAsString();
        } else if (token == JsonToken.VALUE_NUMBER_INT) {
            return p.getValueAsInt();
        } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            return p.getValueAsDouble();
        } else if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                return token == JsonToken.VALUE_TRUE;
        } else if (token == JsonToken.VALUE_NULL) {
            return null;
        } else if (token == JsonToken.START_ARRAY) {
            return parseArray2(p);
        } else if (token == JsonToken.START_OBJECT) {
            return parseMap(p);
        } else {
            throw new UnsupportedOperationException("Unsupported JSON token : "+token+ ", value : "+p.getText());
        }
    }

    /**
     * Parse a coordinates array(s) and add to given GeoJSONGeometry.
     * @param geom GeoJSONGeometry
     * @param p parser
     * @throws IOException
     */
    private void parseGeometry(GeoJSONGeometry geom, JsonParser p) throws IOException {
        p.nextToken(); // "["
        if (geom instanceof GeoJSONPoint) {
            ((GeoJSONPoint) geom).setCoordinates(parsePoint(p));
        } else if (geom instanceof GeoJSONLineString) {
            ((GeoJSONLineString) geom).setCoordinates(parseLineString(p));
        } else if (geom instanceof GeoJSONPolygon) {
            ((GeoJSONPolygon) geom).setCoordinates(parseMultiLineString(p));
        } else if (geom instanceof GeoJSONMultiPoint) {
            ((GeoJSONMultiPoint) geom).setCoordinates(parseLineString(p));
        } else if (geom instanceof GeoJSONMultiLineString) {
            ((GeoJSONMultiLineString) geom).setCoordinates(parseMultiLineString(p));
        } else if (geom instanceof GeoJSONMultiPolygon) {
            ((GeoJSONMultiPolygon) geom).setCoordinates(parseMultiPolygon(p));
        }
    }

    /**
     * Full parse of GeoJSONFeatures for a GeoJSONFeatureCollection
     * @param coll GeoJSONFeatureCollection
     * @param p parser
     * @throws IOException
     */
    private void parseFeatureCollection(GeoJSONFeatureCollection coll, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {

            GeoJSONObject obj = parseGeoJSONObject(p);
            if (obj instanceof GeoJSONFeature) {
                coll.getFeatures().add((GeoJSONFeature)obj);
            } else {
                LOGGER.log(Level.WARNING, "ERROR feature collection");
            }
        }
    }

    /**
     * Lazy parse of GeoJSONFeatures for a GeoJSONFeatureCollection.
     * Only find an set START_ARRAY and END_ARRAY TokenLocation of the features array to
     * the GeoJSONFeatureCollection object.
     *
     * @param coll GeoJSONFeatureCollection
     * @param p parser
     * @throws IOException
     */
    private void lazyParseFeatureCollection(GeoJSONFeatureCollection coll, JsonParser p) throws IOException {

        p.nextToken();
        coll.setStartIdx(p.getCurrentLocation());

        int startArray = 1;
        int endArray = 0;

        //loop to the right "]"
        while (startArray != endArray) {
            JsonToken token = p.nextToken();
            if (token ==  JsonToken.START_ARRAY) startArray ++;
            if (token ==  JsonToken.END_ARRAY) endArray ++;
        }

        coll.setEndIdx(p.getCurrentLocation());
    }

    /**
     * Parse GeoJSONGeometry for GeoJSONFeature.
     * @param feature GeoJSONFeature
     * @param p parser
     * @throws IOException
     */
    private void parseFeatureGeometry(GeoJSONFeature feature, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        GeoJSONObject obj = parseGeoJSONObject(p);
        assert(obj != null) : "Unparsable GeoJSONGeometry.";
        assert(obj instanceof GeoJSONGeometry) : "Unexpected GeoJSONObject : " + obj.getType()+" expected : GeoJSONGeometry";
        feature.setGeometry((GeoJSONGeometry) obj);
    }

    /**
     * Parse GeoJSONGeometry for GeoJSONGeometryCollection.
     * @param geom GeoJSONGeometryCollection
     * @param p parser
     * @throws IOException
     */
    private void parseGeometryCollection(GeoJSONGeometryCollection geom, JsonParser p) throws IOException {
        p.nextToken(); // "["
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            GeoJSONObject obj = parseGeoJSONObject(p);
            assert(obj != null) : "Unparsable GeoJSONGeometry.";
            assert(obj instanceof GeoJSONGeometry) : "Unexpected GeoJSONObject : " + obj.getType()+" expected : GeoJSONGeometry";
            geom.getGeometries().add((GeoJSONGeometry)obj);
        }
    }

    /**
     * Create GeoJSONObject using type.
     * If previous object is not null, forward bbox and crs parameter to the new GeoJSONObject.
     *
     * @param object previous object.
     * @param type see {@link org.geotoolkit.data.geojson.utils.GeoJSONTypes}
     * @return GeoJSONObject
     */
    private GeoJSONObject getOrCreateFromType(GeoJSONObject object, String type) {

        GeoJSONObject result = object;
        if (type != null) {
            switch (type) {
                case FEATURE_COLLECTION:
                    if (result instanceof GeoJSONFeatureCollection) {
                        return result;
                    } else {
                        result = new GeoJSONFeatureCollection();
                    }
                    break;
                case FEATURE:
                    if (result instanceof GeoJSONFeature) {
                        return result;
                    } else {
                        result = new GeoJSONFeature();
                    }
                    break;
                case POINT:
                    if (result instanceof GeoJSONPoint) {
                        return result;
                    } else {
                        result = new GeoJSONPoint();
                    }
                    break;
                case LINESTRING:
                    if (result instanceof GeoJSONLineString) {
                        return result;
                    } else {
                        result = new GeoJSONLineString();
                    }
                    break;
                case POLYGON:
                    if (result instanceof GeoJSONPolygon) {
                        return result;
                    } else {
                        result = new GeoJSONPolygon();
                    }
                    break;
                case MULTI_POINT:
                    if (result instanceof GeoJSONMultiPoint) {
                        return result;
                    } else {
                        result = new GeoJSONMultiPoint();
                    }
                    break;
                case MULTI_LINESTRING:
                    if (result instanceof GeoJSONMultiLineString) {
                        return result;
                    } else {
                        result = new GeoJSONMultiLineString();
                    }
                    break;
                case MULTI_POLYGON:
                    if (result instanceof GeoJSONMultiPolygon) {
                        return result;
                    } else {
                        result = new GeoJSONMultiPolygon();
                    }
                    break;
                case GEOMETRY_COLLECTION:
                    if (result instanceof GeoJSONGeometryCollection) {
                        return result;
                    } else {
                        result = new GeoJSONGeometryCollection();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type " + type);
            }

            if (object != null) {
                result.setBbox(object.getBbox());
                result.setCrs(object.getCrs());
            }
        }
        return result;
    }

    /**
     * Parse the bbox array.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return  an array of double with a length of 4 or 6.
     * @throws IOException
     */
    private double[] parseBBoxArray(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        double[] bbox = new double[4];

        int idx = 0;
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (idx == 4) {
                bbox = Arrays.copyOf(bbox, 6);
            }
            bbox[idx++] = p.getDoubleValue();
        }

        return bbox;
    }

    /**
     * Parse a CRS Object.
     * JsonParser location MUST be on a START_OBJECT token.
     * @param p JsonParser location MUST be on a START_OBJECT token.
     * @return GeoJSONCRS
     * @throws IOException
     */
    private GeoJSONCRS parseCRS(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_OBJECT);
        GeoJSONCRS crs = new GeoJSONCRS();

        while (p.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = p.getCurrentName();
            if (TYPE.equals(fieldName)) {
                crs.setType(p.nextTextValue());
            } else if (PROPERTIES.equals(fieldName)) {
                //object
                p.nextToken();
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    crs.getProperties().put(p.getCurrentName(), p.getValueAsString());
                }
            }
        }

        return crs;
    }

    /**
     * Parse a Coordinate.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [X,Y,(Z)]
     * @throws IOException
     */
    private double[] parsePoint(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        double[] pt = new double[2];

        int idx = 0;
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (idx == 2) {
                pt = Arrays.copyOf(pt, 3);
            }
            pt[idx++] = p.getDoubleValue();
        }
        return pt;
    }

    /**
     * Parse a LineString/MultiPoint.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [[X0,Y0,(Z0)], [X1,Y1,(Z1)]]
     * @throws IOException
     */
    private double[][] parseLineString(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[]> line = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            line.add(parsePoint(p));
        }
        return line.toArray(new double[line.size()][]);
    }

    /**
     * Parse a List of LineString or Polygon.
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like
     * [
     *  [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     *  [[X0,Y0,(Z0)], [X1,Y1,(Z1)]], ...
     * ]
     * @throws IOException
     */
    private double[][][] parseMultiLineString(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[][]> lines = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            lines.add(parseLineString(p));
        }
        return lines.toArray(new double[lines.size()][][]);
    }

    /**
     * Parse a List of Polygons (list of list of LineString).
     * JsonParser location MUST be on a START_ARRAY token.
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like
     * [[
     *      [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     *      [[X0,Y0,(Z0)], [X1,Y1,(Z1)]]
     *  ],[
     *      [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     *      [[X0,Y0,(Z0)], [X1,Y1,(Z1)]]
     *  ], ... ]
     * @throws IOException
     */
    private double[][][][] parseMultiPolygon(JsonParser p) throws IOException {
        assert(p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[][][]> polygons = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            polygons.add(parseMultiLineString(p));
        }
        return polygons.toArray(new double[polygons.size()][][][]);
    }

}
