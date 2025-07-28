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


import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;
import org.geotoolkit.ogcapi.model.DataTransferObject;
import org.geotoolkit.ogcapi.model.common.Link;


/**
 * FeatureCollection
 */
@JsonPropertyOrder({
    FeatureCollection.JSON_PROPERTY_TYPE,
    FeatureCollection.JSON_PROPERTY_FEATURES,
    FeatureCollection.JSON_PROPERTY_LINKS,
    FeatureCollection.JSON_PROPERTY_TIME_STAMP,
    FeatureCollection.JSON_PROPERTY_NUMBER_MATCHED,
    FeatureCollection.JSON_PROPERTY_NUMBER_RETURNED
})
@XmlRootElement(name = "FeatureCollection")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FeatureCollection")
public class FeatureCollection extends DataTransferObject {

    /**
     * Gets or Sets type
     */
    @XmlType(name = "TypeEnum")
    @XmlEnum(String.class)
    public enum TypeEnum {
        @XmlEnumValue("FeatureCollection")
        FEATURE_COLLECTION(String.valueOf("FeatureCollection"));

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TypeEnum fromValue(String value) {
            for (TypeEnum b : TypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public static final String JSON_PROPERTY_TYPE = "type";
    @XmlElement(name = "type")
    @jakarta.annotation.Nonnull
    private TypeEnum type;

    public static final String JSON_PROPERTY_FEATURES = "features";
    @XmlElement(name = "features")
    @jakarta.annotation.Nonnull
    private List<GeoJSONFeature> features = new ArrayList<>();

    public static final String JSON_PROPERTY_BBOX = "bbox";
    @XmlElement(name = "bbox")
    @jakarta.annotation.Nullable
    private List<BigDecimal> bbox = new ArrayList<>();

    public static final String JSON_PROPERTY_LINKS = "links";
    @XmlElement(name = "links")
    @jakarta.annotation.Nullable
    private List<Link> links = new ArrayList<>();

    public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
    @XmlElement(name = "timeStamp")
    @jakarta.annotation.Nullable
    private OffsetDateTime timeStamp;

    public static final String JSON_PROPERTY_NUMBER_MATCHED = "numberMatched";
    @XmlElement(name = "numberMatched")
    @jakarta.annotation.Nullable
    private Integer numberMatched;

    public static final String JSON_PROPERTY_NUMBER_RETURNED = "numberReturned";
    @XmlElement(name = "numberReturned")
    @jakarta.annotation.Nullable
    private Integer numberReturned;

    public FeatureCollection() {
    }

    public FeatureCollection type(@jakarta.annotation.Nonnull TypeEnum type) {
        this.type = type;
        return this;
    }

    /**
     * Get type
     *
     * @return type
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public TypeEnum getType() {
        return type;
    }

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "type")
    public void setType(@jakarta.annotation.Nonnull TypeEnum type) {
        this.type = type;
    }

    public FeatureCollection features(@jakarta.annotation.Nonnull List<GeoJSONFeature> features) {
        this.features = features;
        return this;
    }

    public FeatureCollection addFeaturesItem(GeoJSONFeature featuresItem) {
        if (this.features == null) {
            this.features = new ArrayList<>();
        }
        this.features.add(featuresItem);
        return this;
    }

    /**
     * Get features
     *
     * @return features
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "features")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<GeoJSONFeature> getFeatures() {
        return features;
    }

    @JsonProperty(JSON_PROPERTY_FEATURES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "features")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setFeatures(@jakarta.annotation.Nonnull List<GeoJSONFeature> features) {
        this.features = features;
    }

    public FeatureCollection bbox(@jakarta.annotation.Nullable List<BigDecimal> bbox) {
        this.bbox = bbox;
        return this;
    }

    public FeatureCollection addBboxItem(BigDecimal bboxItem) {
        if (this.bbox == null) {
            this.bbox = new ArrayList<>();
        }
        this.bbox.add(bboxItem);
        return this;
    }

    /**
     * Get bbox
     *
     * @return bbox
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<BigDecimal> getBbox() {
        return bbox;
    }

    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "bbox")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setBbox(@jakarta.annotation.Nullable List<BigDecimal> bbox) {
        this.bbox = bbox;
    }

    public FeatureCollection links(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
        return this;
    }

    public FeatureCollection addLinksItem(Link linksItem) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(linksItem);
        return this;
    }

    /**
     * Get links
     *
     * @return links
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "links")
    @JacksonXmlElementWrapper(useWrapping = false)
    public void setLinks(@jakarta.annotation.Nullable List<Link> links) {
        this.links = links;
    }

    public FeatureCollection timeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * This property indicates the time and date when the response was generated.
     *
     * @return timeStamp
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "timeStamp")
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty(JSON_PROPERTY_TIME_STAMP)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "timeStamp")
    public void setTimeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FeatureCollection numberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
        return this;
    }

    /**
     * The number of features of the feature type that match the selection parameters like &#x60;bbox&#x60;. minimum: 0
     *
     * @return numberMatched
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public Integer getNumberMatched() {
        return numberMatched;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_MATCHED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberMatched")
    public void setNumberMatched(@jakarta.annotation.Nullable Integer numberMatched) {
        this.numberMatched = numberMatched;
    }

    public FeatureCollection numberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
        return this;
    }

    /**
     * The number of features in the feature collection. A server may omit this information in a response, if the
     * information about the number of features is not known or difficult to compute. If the value is provided, the
     * value shall be identical to the number of items in the \&quot;features\&quot; array. minimum: 0
     *
     * @return numberReturned
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public Integer getNumberReturned() {
        return numberReturned;
    }

    @JsonProperty(JSON_PROPERTY_NUMBER_RETURNED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "numberReturned")
    public void setNumberReturned(@jakarta.annotation.Nullable Integer numberReturned) {
        this.numberReturned = numberReturned;
    }

    /**
     * Return true if this FeatureCollection object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureCollection featureCollection = (FeatureCollection) o;
        return Objects.equals(this.type, featureCollection.type)
                && Objects.equals(this.features, featureCollection.features)
                && Objects.equals(this.bbox, featureCollection.bbox)
                && Objects.equals(this.links, featureCollection.links)
                && Objects.equals(this.timeStamp, featureCollection.timeStamp)
                && Objects.equals(this.numberMatched, featureCollection.numberMatched)
                && Objects.equals(this.numberReturned, featureCollection.numberReturned);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, features, bbox, links, timeStamp, numberMatched, numberReturned);
    }

}
