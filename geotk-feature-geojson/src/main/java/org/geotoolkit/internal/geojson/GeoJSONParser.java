/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.geojson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.internal.storage.io.IOUtilities;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.geojson.binding.GeoJSONCRS;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeatureCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiLineString;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONMultiPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPoint;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry.GeoJSONPolygon;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import static org.geotoolkit.storage.geojson.GeoJSONConstants.*;

/**
 * Efficient GeoJSONParsing using jackson {@link JsonParser}
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public final class GeoJSONParser {

    public static final JsonFactory JSON_FACTORY = new JsonFactory();
    public static final JsonFactory CBOR_FACTORY;
    public static final Logger LOGGER = Logging.getLogger("org.apache.sis.storage.geojson.utils");

    static {
        JsonFactory factory = null;
        try {
            factory = new CBORFactory();
        } catch (Throwable ex) {
            //this is caused by old ucar cdm and amazon libraries using version 2.6.7 of cbor
            //version 2.6.3 is not compatible with latest 2.10.3 of jackson
            LOGGER.info("Could not initialize CBOR factor, check for mixed version of jackson cbor jar files.\n" + ex.getMessage());
        }
        CBOR_FACTORY = factory;
    }

    private GeoJSONParser() {}

    public static JsonFactory getFactory(Path path) {
        final String extension = IOUtilities.extension(path).toLowerCase();
        if ("cbor".equals(extension)) {
            return CBOR_FACTORY;
        } else {
            return JSON_FACTORY;
        }
    }

    /**
     * Parse a json file and return a GeoJSONObject. If parser was construct
     * with lazyParsing as {@code true} and root object is a FeatureCollection,
     * returned GeoJSONFeatureCollection will only have start and end feature
     * array location. Otherwise, all Feature will be parsed and add to
     * GeoJSONFeatureCollection.
     *
     * @param jsonFile file to parse
     * @return GeoJSONObject
     * @throws IOException
     */
    public static GeoJSONObject parse(Path jsonFile) throws IOException {
        return parse(jsonFile, Boolean.FALSE);
    }

    /**
     * Parse a json file and return a GeoJSONObject. If parser was construct
     * with lazyParsing as {@code true} and root object is a FeatureCollection,
     * returned GeoJSONFeatureCollection will only have start and end feature
     * array location. Otherwise, all Feature will be parsed and add to
     * GeoJSONFeatureCollection.
     *
     * @param jsonFile file to parse
     * @param lazy lazy mode flag
     * @return GeoJSONObject
     * @throws IOException
     */
    public static GeoJSONObject parse(Path jsonFile, boolean lazy) throws IOException {

        try (InputStream reader = Files.newInputStream(jsonFile);
                JsonParser p = getFactory(jsonFile).createParser(reader)) {

            JsonToken startToken = p.nextToken();
            assert (startToken == JsonToken.START_OBJECT) : "Input File is not a JSON file " + jsonFile.toAbsolutePath().toString();
            return parseGeoJSONObject(p, lazy, jsonFile);
        }
    }

    /**
     * Parse a json InputStream and return a GeoJSONObject. In InputStream case,
     * lazy loading of FeatureCollection is disabled.
     *
     * @param inputStream stream to parse
     * @return GeoJSONObject
     * @throws IOException
     */
    public static GeoJSONObject parse(InputStream inputStream) throws IOException {
        try (JsonParser p = JSON_FACTORY.createParser(inputStream)) {
            JsonToken startToken = p.nextToken();
            assert (startToken == JsonToken.START_OBJECT) : "Input stream is not a valid JSON ";
            return parseGeoJSONObject(p);
        }
    }

    /**
     * Parse a GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * JsonParser location MUST be on a START_OBJECT token.
     *
     * @param p parser jackson parser with current token on a START_OBJECT.
     * @return GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * @throws IOException
     */
    public static GeoJSONObject parseGeoJSONObject(JsonParser p) throws IOException {
        return parseGeoJSONObject(p, Boolean.FALSE, null);
    }

    /**
     * Parse a GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * JsonParser location MUST be on a START_OBJECT token.
     *
     * @param p parser jackson parser with current token on a START_OBJECT.
     * @param lazy lazy mode flag
     * @return GeoJSONObject (FeatureCollection, Feature or a Geometry)
     * @throws IOException
     */
    private static GeoJSONObject parseGeoJSONObject(JsonParser p, Boolean lazy, Path source) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_OBJECT);

        GeoJSONObject object = new GeoJSONObject();
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = p.getCurrentName();

            if (fieldname == null) {
                throw new IOException("Parsing error, expect object field name value but got null");
            }

            switch (fieldname) {
                case ID:
                    p.nextToken();
                    String id = p.getValueAsString();
                    if (object instanceof GeoJSONFeature) {
                        ((GeoJSONFeature) object).setId(id);
                    }
                    break;
                case TYPE:
                    p.nextToken();
                    String value = p.getValueAsString();
                    object = getOrCreateFromType(object, value, lazy);
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
                    object = getOrCreateFromType(object, FEATURE_COLLECTION, lazy);

                    //array of GeoJSONFeature
                    if (Boolean.TRUE.equals(lazy)) {
                        lazyParseFeatureCollection((GeoJSONFeatureCollection) object, p, source);
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
                default:
                    p.skipChildren();
            }
        }
        return object;
    }

    /**
     * Parse properties map and add to given Feature
     *
     * @param feature Feature to attach properties
     * @param p parser
     * @throws IOException
     */
    private static void parseProperties(GeoJSONFeature feature, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        feature.getProperties().putAll(parseMap(p));
    }

    /**
     * Parse a map of String, Object. JsonParser location MUST be on a
     * START_OBJECT token.
     *
     * @param p parser jackson parser with current token on a START_OBJECT.
     * @return Map of String - Object
     * @throws IOException
     */
    private static Map<String, Object> parseMap(JsonParser p) throws IOException {
        Map<String, Object> map = new HashMap<>();
        final JsonToken currentToken = p.getCurrentToken();
        if (currentToken == JsonToken.VALUE_NULL) {
            return map;
        }

        if (currentToken != JsonToken.START_OBJECT) {
            LOGGER.log(Level.WARNING, "Expect START_OBJECT token but got " + currentToken + " for " + p.getCurrentName());
            return map;
        }

        assert (currentToken == JsonToken.START_OBJECT);
        while (p.nextToken() != JsonToken.END_OBJECT) {
            String key = p.getCurrentName();
            JsonToken next = p.nextToken();

            map.put(key, getValue(next, p));
        }
        return map;
    }

    /**
     * Parse a List of Objects. JsonParser location MUST be on a START_ARRAY
     * token.
     *
     * @param p parser jackson parser with current token on a START_ARRAY.
     * @return array object typed after first element class
     * @throws IOException
     */
    private static Object parseArray2(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
        List<Object> list = new ArrayList<>();
        Set<Class> valueTypes = new HashSet<>();
        boolean nullFound = false;
        JsonToken token;
        while ((token = p.nextToken()) != JsonToken.END_ARRAY) {
            final Object value = getValue(token, p);
            if (value == null) nullFound = true;
            else valueTypes.add(value.getClass());
            list.add(value);
        }

        if (list.isEmpty()) {
            return new Object[0];
        } else if (valueTypes.isEmpty()) {
            assert nullFound;
            return new Object[list.size()];
        }

        final Class valueType = valueTypes.stream()
                .reduce(GeoJSONParser::findCommonType)
                .orElseThrow(() -> new JsonParseException(p, "Cannot properly resolve array type"));
        if (!nullFound && Numbers.isFloat(valueType)) {
            return list.stream().mapToDouble(value -> ((Number)value).doubleValue()).toArray();
        } else if (!nullFound && Integer.class.isAssignableFrom(valueType)) {
            return list.stream().mapToInt(value -> ((Number)value).intValue()).toArray();
        } else if (!nullFound && Long.class.isAssignableFrom(valueType)) {
            return list.stream().mapToLong(value -> ((Number)value).longValue()).toArray();
        } else if (Object.class.equals(valueType)) {
            // Short-circuit: Optimized array transformation
            return list.toArray();
        } else {
            Object newArray = Array.newInstance(valueType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(newArray, i, list.get(i));
            }
            return newArray;
        }
    }

    private static Class findCommonType(Class c1, Class c2) {
        if (c1.isAssignableFrom(c2)) return c1;
        else if (c2.isAssignableFrom(c1)) return c2;
        if (Number.class.isAssignableFrom(c1) && Number.class.isAssignableFrom(c2)) {
            if (Numbers.isFloat(c1) || Numbers.isFloat(c2)) return Double.class;

            c1 = Numbers.primitiveToWrapper(c1);
            c2 = Numbers.primitiveToWrapper(c2);
            if (Long.class.isAssignableFrom(c1) || Long.class.isAssignableFrom(c2)) return Long.class;

            assert Integer.class.equals(c1)|| Integer.class.equals(c2);
            return Integer.class;
        }

        // We could try to find a common parent, however it would be an arbitrary and possibly bad choice.
        return Object.class;
    }

    /**
     * Convert the current token to appropriate object. Supported (String,
     * Integer, Float, Boolean, Null, Array, Map)
     *
     * @param token current token
     * @param p parser
     * @return current token value String or Integer or Float or Boolean or null
     * or an array or a map.
     * @throws IOException
     */
    static Object getValue(JsonToken token, JsonParser p) throws IOException {
        if (token == JsonToken.VALUE_STRING) {
            return p.getValueAsString();
        } else if (token == JsonToken.VALUE_NUMBER_INT) {
            final long value = p.getValueAsLong();
            if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                return (int) value;
            }
            return value;
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
            throw new UnsupportedOperationException("Unsupported JSON token : " + token + ", value : " + p.getText());
        }
    }

    /**
     * Parse a coordinates array(s) and add to given GeoJSONGeometry.
     *
     * @param geom GeoJSONGeometry
     * @param p parser
     * @throws IOException
     */
    private static void parseGeometry(GeoJSONGeometry geom, JsonParser p) throws IOException {
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
     *
     * @param coll GeoJSONFeatureCollection
     * @param p parser
     * @throws IOException
     */
    private static void parseFeatureCollection(GeoJSONFeatureCollection coll, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {

            GeoJSONObject obj = parseGeoJSONObject(p, false, null);
            if (obj instanceof GeoJSONFeature) {
                coll.getFeatures().add((GeoJSONFeature) obj);
            } else {
                LOGGER.log(Level.WARNING, "ERROR feature collection");
            }
        }
    }

    /**
     * Lazy parse of GeoJSONFeatures for a GeoJSONFeatureCollection. Only find
     * an set START_ARRAY and END_ARRAY TokenLocation of the features array to
     * the GeoJSONFeatureCollection object.
     *
     * @param coll GeoJSONFeatureCollection
     * @param p parser
     * @param source
     * @throws IOException
     */
    private static void lazyParseFeatureCollection(GeoJSONFeatureCollection coll, JsonParser p, Path source) throws IOException {

        p.nextToken();
        coll.setSourceInput(source);
        coll.setStartPosition(p.getCurrentLocation());

        int startArray = 1;
        int endArray = 0;

        //loop to the right "]"
        while (startArray != endArray) {
            JsonToken token = p.nextToken();
            if (token == JsonToken.START_ARRAY) {
                startArray++;
            }
            if (token == JsonToken.END_ARRAY) {
                endArray++;
            }
        }

        coll.setEndPosition(p.getCurrentLocation());
    }

    /**
     * Parse GeoJSONGeometry for GeoJSONFeature.
     *
     * @param feature GeoJSONFeature
     * @param p parser
     * @throws IOException
     */
    private static void parseFeatureGeometry(GeoJSONFeature feature, JsonParser p) throws IOException {
        p.nextToken(); // "{"
        GeoJSONObject obj = parseGeoJSONObject(p);
        assert (obj != null) : "Un-parsable GeoJSONGeometry.";
        assert (obj instanceof GeoJSONGeometry) : "Unexpected GeoJSONObject : " + obj.getType() + " expected : GeoJSONGeometry";
        feature.setGeometry((GeoJSONGeometry) obj);
    }

    /**
     * Parse GeoJSONGeometry for GeoJSONGeometryCollection.
     *
     * @param geom GeoJSONGeometryCollection
     * @param p parser
     * @throws IOException
     */
    private static void parseGeometryCollection(GeoJSONGeometryCollection geom, JsonParser p) throws IOException {
        p.nextToken(); // "["
        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            GeoJSONObject obj = parseGeoJSONObject(p);
            assert (obj != null) : "Un-parsable GeoJSONGeometry.";
            assert (obj instanceof GeoJSONGeometry) : "Unexpected GeoJSONObject : " + obj.getType() + " expected : GeoJSONGeometry";
            geom.getGeometries().add((GeoJSONGeometry) obj);
        }
    }

    /**
     * Create GeoJSONObject using type. If previous object is not null, forward
     * bbox and crs parameter to the new GeoJSONObject.
     *
     * @param object previous object.
     * @param type see {@link org.apache.sis.storage.geojson.GeoJSONConstants}
     * @return GeoJSONObject
     */
    private static GeoJSONObject getOrCreateFromType(GeoJSONObject object, String type) {
        return getOrCreateFromType(object, type, Boolean.FALSE);

    }

    /**
     * Create GeoJSONObject using type. If previous object is not null, forward
     * bbox and crs parameter to the new GeoJSONObject.
     *
     * @param object previous object.
     * @param type see {@link org.apache.sis.storage.geojson.GeoJSONConstants}
     * @param lazy lazy mode flag
     * @return GeoJSONObject
     */
    private static GeoJSONObject getOrCreateFromType(GeoJSONObject object, String type, Boolean lazy) {

        GeoJSONObject result = object;
        if (type != null) {
            switch (type) {
                case FEATURE_COLLECTION:
                    if (result instanceof GeoJSONFeatureCollection) {
                        return result;
                    } else {
                        result = new GeoJSONFeatureCollection(lazy);
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
     * Parse the bbox array. JsonParser location MUST be on a START_ARRAY token.
     *
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double with a length of 4 or 6.
     * @throws IOException
     */
    private static double[] parseBBoxArray(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
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
     * Parse a CRS Object. JsonParser location MUST be on a START_OBJECT token.
     *
     * @param p JsonParser location MUST be on a START_OBJECT token.
     * @return GeoJSONCRS
     * @throws IOException
     */
    private static GeoJSONCRS parseCRS(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_OBJECT);
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
     * Parse a Coordinate. JsonParser location MUST be on a START_ARRAY token.
     *
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [X,Y,(Z)]
     * @throws IOException
     */
    private static double[] parsePoint(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
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
     * Parse a LineString/MultiPoint. JsonParser location MUST be on a
     * START_ARRAY token.
     *
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [[X0,Y0,(Z0)], [X1,Y1,(Z1)]]
     * @throws IOException
     */
    private static double[][] parseLineString(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[]> line = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            line.add(parsePoint(p));
        }
        return line.toArray(new double[line.size()][]);
    }

    /**
     * Parse a List of LineString or Polygon. JsonParser location MUST be on a
     * START_ARRAY token.
     *
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [ [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     * [[X0,Y0,(Z0)], [X1,Y1,(Z1)]], ... ]
     * @throws IOException
     */
    private static double[][][] parseMultiLineString(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[][]> lines = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            lines.add(parseLineString(p));
        }
        return lines.toArray(new double[lines.size()][][]);
    }

    /**
     * Parse a List of Polygons (list of list of LineString). JsonParser
     * location MUST be on a START_ARRAY token.
     *
     * @param p JsonParser location MUST be on a START_ARRAY token.
     * @return an array of double like [[ [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     * [[X0,Y0,(Z0)], [X1,Y1,(Z1)]] ],[ [[X0,Y0,(Z0)], [X1,Y1,(Z1)]],
     * [[X0,Y0,(Z0)], [X1,Y1,(Z1)]] ], ... ]
     * @throws IOException
     */
    private static double[][][][] parseMultiPolygon(JsonParser p) throws IOException {
        assert (p.getCurrentToken() == JsonToken.START_ARRAY);
        List<double[][][]> polygons = new ArrayList<>();

        // messages is array, loop until token equal to "]"
        while (p.nextToken() != JsonToken.END_ARRAY) {
            polygons.add(parseMultiLineString(p));
        }
        return polygons.toArray(new double[polygons.size()][][][]);
    }

}
