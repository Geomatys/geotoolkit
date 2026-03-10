package org.geotoolkit.stac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.ogcapi.model.common.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a STAC Item.
 * <p>
 * An Item is the core building block of a STAC. It is essentially a GeoJSON Feature that represents
 * a distinct, inseparable piece of data (like a single satellite swath or tile capture) at a specific time and location.
 * <p>
 * Key elements include its geometry, spatial bounding box, properties (metadata like cloud cover, datetime),
 * and importantly, assets (which contain the actual links to the data files).
 */
@JsonPropertyOrder({
        Item.JSON_PROPERTY_STAC_VERSION,
        Item.JSON_PROPERTY_STAC_EXTENSIONS,
        Item.JSON_PROPERTY_TYPE,
        Item.JSON_PROPERTY_ID,
        Item.JSON_PROPERTY_GEOMETRY,
        Item.JSON_PROPERTY_BBOX,
        Item.JSON_PROPERTY_PROPERTIES,
        Item.JSON_PROPERTY_LINKS,
        Item.JSON_PROPERTY_ASSETS,
        Item.JSON_PROPERTY_COLLECTION
})
public class Item {

    public static final String JSON_PROPERTY_STAC_VERSION = "stac_version";
    public static final String JSON_PROPERTY_STAC_EXTENSIONS = "stac_extensions";
    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_ID = "id";
    public static final String JSON_PROPERTY_GEOMETRY = "geometry";
    public static final String JSON_PROPERTY_BBOX = "bbox";
    public static final String JSON_PROPERTY_PROPERTIES = "properties";
    public static final String JSON_PROPERTY_LINKS = "links";
    public static final String JSON_PROPERTY_ASSETS = "assets";
    public static final String JSON_PROPERTY_COLLECTION = "collection";

    @JsonProperty(JSON_PROPERTY_STAC_VERSION)
    private String stacVersion;

    @JsonProperty(JSON_PROPERTY_STAC_EXTENSIONS)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> stacExtensions = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_TYPE)
    private final String type = "Feature";

    @JsonProperty(JSON_PROPERTY_ID)
    private String id;

    @JsonProperty(JSON_PROPERTY_GEOMETRY)
    private JsonNode geometry; // Retained as JsonNode to allow raw extraction

    @JsonProperty(JSON_PROPERTY_BBOX)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Double> bbox = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_PROPERTIES)
    private Map<String, Object> properties = new HashMap<>();

    @JsonProperty(JSON_PROPERTY_LINKS)
    private List<Link> links = new ArrayList<>();

    @JsonProperty(JSON_PROPERTY_ASSETS)
    private Map<String, Asset> assets = new HashMap<>();

    @JsonProperty(JSON_PROPERTY_COLLECTION)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String collection;

    public Item() {}

    public String getStacVersion() {
        return stacVersion;
    }

    public void setStacVersion(String stacVersion) {
        this.stacVersion = stacVersion;
    }

    public List<String> getStacExtensions() {
        return stacExtensions;
    }

    public void setStacExtensions(List<String> stacExtensions) {
        this.stacExtensions = stacExtensions;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonNode getGeometry() {
        return geometry;
    }

    public void setGeometry(JsonNode geometry) {
        this.geometry = geometry;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Map<String, Asset> getAssets() {
        return assets;
    }

    public void setAssets(Map<String, Asset> assets) {
        this.assets = assets;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(stacVersion, item.stacVersion) && Objects.equals(stacExtensions, item.stacExtensions) && Objects.equals(type, item.type) && Objects.equals(id, item.id) && Objects.equals(geometry, item.geometry) && Objects.equals(bbox, item.bbox) && Objects.equals(properties, item.properties) && Objects.equals(links, item.links) && Objects.equals(assets, item.assets) && Objects.equals(collection, item.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stacVersion, stacExtensions, type, id, geometry, bbox, properties, links, assets, collection);
    }
}
