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
package org.geotoolkit.ogcapi.model.dggs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.ogcapi.model.DataTransferObject;

@XmlRootElement(name = "Crs")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Crs")
@JsonDeserialize(using = Crs.CrsDeserializer.class)
@JsonSerialize(using = Crs.CrsSerializer.class)
public final class Crs extends DataTransferObject {

    private static final Logger log = Logger.getLogger(Crs.class.getName());

    public static class CrsSerializer extends StdSerializer<Crs> {

        public CrsSerializer(Class<Crs> t) {
            super(t);
        }

        public CrsSerializer() {
            this(null);
        }

        @Override
        public void serialize(Crs value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            if (value.plain != null && !value.plain.isBlank()) {
                jgen.writeString(value.plain);
            } else {
                final Map map = new HashMap();
                if (value.uri != null) map.put("uri", value.uri);
                if (value.wkt != null) map.put("wkt", value.wkt);
                if (value.referenceSystem != null) map.put("referenceSystem", value.referenceSystem);
                jgen.writeObject(map);
            }
        }
    }

    public static class CrsDeserializer extends StdDeserializer<Crs> {

        public CrsDeserializer() {
            this(Crs.class);
        }

        public CrsDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Crs deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final JsonNode tree = jp.readValueAsTree();
            try {
                Crs crs = tree.traverse(jp.getCodec()).readValueAs(Crs.class);
                return crs;
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'CrsOneOf'", e);
            }

            // deserialize String
            String plain = tree.traverse(jp.getCodec()).readValueAs(String.class);
            return new Crs().plain(plain);
        }

        /**
         * Handle deserialization of the 'null' value.
         */
        @Override
        public Crs getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "Crs cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    @jakarta.annotation.Nullable
    private String plain;

    public static final String JSON_PROPERTY_URI = "uri";
    @XmlElement(name = "uri")
    @jakarta.annotation.Nullable
    private URI uri;

    public static final String JSON_PROPERTY_WKT = "wkt";
    @XmlElement(name = "wkt")
    @jakarta.annotation.Nullable
    private Object wkt;

    public static final String JSON_PROPERTY_REFERENCE_SYSTEM = "referenceSystem";
    @XmlElement(name = "referenceSystem")
    @jakarta.annotation.Nullable
    private Object referenceSystem;

    public Crs plain(@jakarta.annotation.Nonnull String plain) {
        this.plain = plain;
        return this;
    }

    public Crs uri(@jakarta.annotation.Nonnull URI uri) {
        this.uri = uri;
        return this;
    }

    /**
     * Reference to one coordinate reference system (CRS)
     *
     * @return uri
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uri")
    public URI getUri() {
        return uri;
    }

    @JsonProperty(JSON_PROPERTY_URI)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uri")
    public void setUri(@jakarta.annotation.Nonnull URI uri) {
        this.uri = uri;
    }

    public Crs wkt(@jakarta.annotation.Nonnull Object wkt) {
        this.wkt = wkt;
        return this;
    }

    /**
     * Get wkt
     *
     * @return wkt
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_WKT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "wkt")
    public Object getWkt() {
        return wkt;
    }

    @JsonProperty(JSON_PROPERTY_WKT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "wkt")
    public void setWkt(@jakarta.annotation.Nonnull Object wkt) {
        this.wkt = wkt;
    }

    public Crs referenceSystem(@jakarta.annotation.Nonnull Object referenceSystem) {
        this.referenceSystem = referenceSystem;
        return this;
    }

    /**
     * A reference system data structure as defined in the MD_ReferenceSystem of the ISO 19115
     *
     * @return referenceSystem
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public Object getReferenceSystem() {
        return referenceSystem;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public void setReferenceSystem(@jakarta.annotation.Nonnull Object referenceSystem) {
        this.referenceSystem = referenceSystem;
    }

    /**
     * Return true if this crs_oneOf_oneOf object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Crs other = (Crs) o;
        return Objects.equals(this.plain, other.plain)
            && Objects.equals(this.uri, other.uri)
            && Objects.equals(this.wkt, other.wkt)
            && Objects.equals(this.referenceSystem, other.referenceSystem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plain, uri, wkt, referenceSystem);
    }
}
