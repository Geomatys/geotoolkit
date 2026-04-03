package org.geotoolkit.stac.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a STAC Asset.
 * <p>
 * An Asset is a child object found inside an {@link Item}. It is a link to the actual physical data file
 * (e.g., a NetCDF file, a GeoTIFF image, or XML metadata).
 * <p>
 * Key fields include the href (the actual download URL), the type (media type like application/netcdf),
 * and roles (e.g., "data", "metadata", or "thumbnail" to describe what the file is used for).
 */
@JsonPropertyOrder({
        Asset.JSON_PROPERTY_HREF,
        Asset.JSON_PROPERTY_TITLE,
        Asset.JSON_PROPERTY_DESCRIPTION,
        Asset.JSON_PROPERTY_TYPE,
        Asset.JSON_PROPERTY_ROLES,
        Asset.JSON_PROPERTY_ALTERNATE
})
public class Asset {

    public static final String JSON_PROPERTY_HREF = "href";
    public static final String JSON_PROPERTY_TITLE = "title";
    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    public static final String JSON_PROPERTY_TYPE = "type";
    public static final String JSON_PROPERTY_ROLES = "roles";
    public static final String JSON_PROPERTY_ALTERNATE = "alternate";

    @JsonProperty(JSON_PROPERTY_HREF)
    private String href;

    @JsonProperty(JSON_PROPERTY_TITLE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    @JsonProperty(JSON_PROPERTY_ROLES)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> roles;

    /**
     * Alternate access links for this asset (STAC alternate-assets extension).
     * Keys are provider names (e.g. "s3", "https") and values are Assets describing
     * the alternate access method, each with at minimum an {@code href}.
     */
    @JsonProperty(JSON_PROPERTY_ALTERNATE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Asset> alternate = new HashMap<>();

    private Map<String, Object> additionalProperties = new HashMap<>();

    public Asset() {}

    public Asset(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, Asset> getAlternate() {
        return alternate;
    }

    public void setAlternate(Map<String, Asset> alternate) {
        this.alternate = alternate != null ? alternate : new HashMap<>();
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(href, asset.href) && Objects.equals(title, asset.title)
                && Objects.equals(description, asset.description) && Objects.equals(type, asset.type)
                && Objects.equals(roles, asset.roles) && Objects.equals(alternate, asset.alternate)
                && Objects.equals(additionalProperties, asset.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, title, description, type, roles, alternate, additionalProperties);
    }
}
