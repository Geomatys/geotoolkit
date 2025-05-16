/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.geojson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.ogcapi.model.AbstractOpenApiSchema;
import org.geotoolkit.ogcapi.model.JSON;

@XmlRootElement(name = "GeoJSONFeatureGeometry")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "GeoJSONFeatureGeometry")
@JsonDeserialize(using = GeoJSONFeatureGeometry.GeoJSONFeatureGeometryDeserializer.class)
@JsonSerialize(using = GeoJSONFeatureGeometry.GeoJSONFeatureGeometrySerializer.class)
public class GeoJSONFeatureGeometry extends AbstractOpenApiSchema {

    private static final Logger log = Logger.getLogger(GeoJSONFeatureGeometry.class.getName());

    public static class GeoJSONFeatureGeometrySerializer extends StdSerializer<GeoJSONFeatureGeometry> {

        public GeoJSONFeatureGeometrySerializer(Class<GeoJSONFeatureGeometry> t) {
            super(t);
        }

        public GeoJSONFeatureGeometrySerializer() {
            this(null);
        }

        @Override
        public void serialize(GeoJSONFeatureGeometry value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class GeoJSONFeatureGeometryDeserializer extends StdDeserializer<GeoJSONFeatureGeometry> {

        public GeoJSONFeatureGeometryDeserializer() {
            this(GeoJSONFeatureGeometry.class);
        }

        public GeoJSONFeatureGeometryDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public GeoJSONFeatureGeometry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            // deserialize GeoJSONLineString
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONLineString.class.equals(Integer.class) || GeoJSONLineString.class.equals(Long.class) || GeoJSONLineString.class.equals(Float.class) || GeoJSONLineString.class.equals(Double.class) || GeoJSONLineString.class.equals(Boolean.class) || GeoJSONLineString.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONLineString.class.equals(Integer.class) || GeoJSONLineString.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONLineString.class.equals(Float.class) || GeoJSONLineString.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONLineString.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONLineString.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONLineString.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONLineString'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONLineString'", e);
            }

            // deserialize GeoJSONMultiLineString
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONMultiLineString.class.equals(Integer.class) || GeoJSONMultiLineString.class.equals(Long.class) || GeoJSONMultiLineString.class.equals(Float.class) || GeoJSONMultiLineString.class.equals(Double.class) || GeoJSONMultiLineString.class.equals(Boolean.class) || GeoJSONMultiLineString.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONMultiLineString.class.equals(Integer.class) || GeoJSONMultiLineString.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONMultiLineString.class.equals(Float.class) || GeoJSONMultiLineString.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONMultiLineString.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONMultiLineString.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONMultiLineString.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONMultiLineString'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONMultiLineString'", e);
            }

            // deserialize GeoJSONMultiPoint
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONMultiPoint.class.equals(Integer.class) || GeoJSONMultiPoint.class.equals(Long.class) || GeoJSONMultiPoint.class.equals(Float.class) || GeoJSONMultiPoint.class.equals(Double.class) || GeoJSONMultiPoint.class.equals(Boolean.class) || GeoJSONMultiPoint.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONMultiPoint.class.equals(Integer.class) || GeoJSONMultiPoint.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONMultiPoint.class.equals(Float.class) || GeoJSONMultiPoint.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONMultiPoint.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONMultiPoint.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONMultiPoint.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONMultiPoint'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONMultiPoint'", e);
            }

            // deserialize GeoJSONMultiPolygon
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONMultiPolygon.class.equals(Integer.class) || GeoJSONMultiPolygon.class.equals(Long.class) || GeoJSONMultiPolygon.class.equals(Float.class) || GeoJSONMultiPolygon.class.equals(Double.class) || GeoJSONMultiPolygon.class.equals(Boolean.class) || GeoJSONMultiPolygon.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONMultiPolygon.class.equals(Integer.class) || GeoJSONMultiPolygon.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONMultiPolygon.class.equals(Float.class) || GeoJSONMultiPolygon.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONMultiPolygon.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONMultiPolygon.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONMultiPolygon.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONMultiPolygon'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONMultiPolygon'", e);
            }

            // deserialize GeoJSONPoint
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONPoint.class.equals(Integer.class) || GeoJSONPoint.class.equals(Long.class) || GeoJSONPoint.class.equals(Float.class) || GeoJSONPoint.class.equals(Double.class) || GeoJSONPoint.class.equals(Boolean.class) || GeoJSONPoint.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONPoint.class.equals(Integer.class) || GeoJSONPoint.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONPoint.class.equals(Float.class) || GeoJSONPoint.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONPoint.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONPoint.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONPoint.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONPoint'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONPoint'", e);
            }

            // deserialize GeoJSONPolygon
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (GeoJSONPolygon.class.equals(Integer.class) || GeoJSONPolygon.class.equals(Long.class) || GeoJSONPolygon.class.equals(Float.class) || GeoJSONPolygon.class.equals(Double.class) || GeoJSONPolygon.class.equals(Boolean.class) || GeoJSONPolygon.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((GeoJSONPolygon.class.equals(Integer.class) || GeoJSONPolygon.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((GeoJSONPolygon.class.equals(Float.class) || GeoJSONPolygon.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (GeoJSONPolygon.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (GeoJSONPolygon.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(GeoJSONPolygon.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'GeoJSONPolygon'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'GeoJSONPolygon'", e);
            }

            if (match == 1) {
                GeoJSONFeatureGeometry ret = new GeoJSONFeatureGeometry();
                ret.setActualInstance(deserialized);
                return ret;
            }
            throw new IOException(String.format("Failed deserialization for GeoJSONFeatureGeometry: %d classes match result, expected 1", match));
        }

        /**
         * Handle deserialization of the 'null' value.
         */
        @Override
        public GeoJSONFeatureGeometry getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "GeoJSONFeatureGeometry cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public GeoJSONFeatureGeometry() {
        super("oneOf", Boolean.FALSE);
    }

    public GeoJSONFeatureGeometry(GeoJSONLineString o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public GeoJSONFeatureGeometry(GeoJSONMultiLineString o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public GeoJSONFeatureGeometry(GeoJSONMultiPoint o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public GeoJSONFeatureGeometry(GeoJSONMultiPolygon o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public GeoJSONFeatureGeometry(GeoJSONPoint o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    public GeoJSONFeatureGeometry(GeoJSONPolygon o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("GeoJSONLineString", GeoJSONLineString.class);
        schemas.put("GeoJSONMultiLineString", GeoJSONMultiLineString.class);
        schemas.put("GeoJSONMultiPoint", GeoJSONMultiPoint.class);
        schemas.put("GeoJSONMultiPolygon", GeoJSONMultiPolygon.class);
        schemas.put("GeoJSONPoint", GeoJSONPoint.class);
        schemas.put("GeoJSONPolygon", GeoJSONPolygon.class);
        JSON.registerDescendants(GeoJSONFeatureGeometry.class, Collections.unmodifiableMap(schemas));
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return GeoJSONFeatureGeometry.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check the instance parameter is valid against the oneOf
     * child schemas: GeoJSONLineString, GeoJSONMultiLineString, GeoJSONMultiPoint, GeoJSONMultiPolygon, GeoJSONPoint,
     * GeoJSONPolygon
     *
     * It could be an instance of the 'oneOf' schemas. The oneOf child schemas may themselves be a composed schema
     * (allOf, anyOf, oneOf).
     */
    @Override
    public void setActualInstance(Object instance) {
        if (JSON.isInstanceOf(GeoJSONLineString.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(GeoJSONMultiLineString.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(GeoJSONMultiPoint.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(GeoJSONMultiPolygon.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(GeoJSONPoint.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        if (JSON.isInstanceOf(GeoJSONPolygon.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be GeoJSONLineString, GeoJSONMultiLineString, GeoJSONMultiPoint, GeoJSONMultiPolygon, GeoJSONPoint, GeoJSONPolygon");
    }

    /**
     * Get the actual instance, which can be the following: GeoJSONLineString, GeoJSONMultiLineString,
     * GeoJSONMultiPoint, GeoJSONMultiPolygon, GeoJSONPoint, GeoJSONPolygon
     *
     * @return The actual instance (GeoJSONLineString, GeoJSONMultiLineString, GeoJSONMultiPoint, GeoJSONMultiPolygon,
     * GeoJSONPoint, GeoJSONPolygon)
     */
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONLineString`. If the actual instance is not `GeoJSONLineString`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `GeoJSONLineString`
     * @throws ClassCastException if the instance is not `GeoJSONLineString`
     */
    public GeoJSONLineString getGeoJSONLineString() throws ClassCastException {
        return (GeoJSONLineString) super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONMultiLineString`. If the actual instance is not `GeoJSONMultiLineString`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `GeoJSONMultiLineString`
     * @throws ClassCastException if the instance is not `GeoJSONMultiLineString`
     */
    public GeoJSONMultiLineString getGeoJSONMultiLineString() throws ClassCastException {
        return (GeoJSONMultiLineString) super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONMultiPoint`. If the actual instance is not `GeoJSONMultiPoint`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `GeoJSONMultiPoint`
     * @throws ClassCastException if the instance is not `GeoJSONMultiPoint`
     */
    public GeoJSONMultiPoint getGeoJSONMultiPoint() throws ClassCastException {
        return (GeoJSONMultiPoint) super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONMultiPolygon`. If the actual instance is not `GeoJSONMultiPolygon`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `GeoJSONMultiPolygon`
     * @throws ClassCastException if the instance is not `GeoJSONMultiPolygon`
     */
    public GeoJSONMultiPolygon getGeoJSONMultiPolygon() throws ClassCastException {
        return (GeoJSONMultiPolygon) super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONPoint`. If the actual instance is not `GeoJSONPoint`, the ClassCastException
     * will be thrown.
     *
     * @return The actual instance of `GeoJSONPoint`
     * @throws ClassCastException if the instance is not `GeoJSONPoint`
     */
    public GeoJSONPoint getGeoJSONPoint() throws ClassCastException {
        return (GeoJSONPoint) super.getActualInstance();
    }

    /**
     * Get the actual instance of `GeoJSONPolygon`. If the actual instance is not `GeoJSONPolygon`, the
     * ClassCastException will be thrown.
     *
     * @return The actual instance of `GeoJSONPolygon`
     * @throws ClassCastException if the instance is not `GeoJSONPolygon`
     */
    public GeoJSONPolygon getGeoJSONPolygon() throws ClassCastException {
        return (GeoJSONPolygon) super.getActualInstance();
    }

}
