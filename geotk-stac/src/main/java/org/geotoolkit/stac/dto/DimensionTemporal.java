package org.geotoolkit.stac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/EO-Data-Discovery">OpenEO Doc</a>
 * Based on : <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md#temporal-extent-object">STAC Spec Github</a>
 */
@JsonPropertyOrder({
        DimensionTemporal.JSON_PROPERTY_EXTENT,
        DimensionTemporal.JSON_PROPERTY_VALUES,
        DimensionTemporal.JSON_PROPERTY_STEP
})
@XmlRootElement(name = "DimensionTemporal")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DimensionTemporal")
public class DimensionTemporal extends Dimension {

    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_VALUES = "values";
    public static final String JSON_PROPERTY_EXTENT = "extent";
    public static final String JSON_PROPERTY_STEP = "step";

    public DimensionTemporal() {
        super(TypeEnum.TEMPORAL);
    }

    public DimensionTemporal(List<Object> values, List<String> extent) {
        super(TypeEnum.TEMPORAL);
        this.values = values;
        this.extent = extent;
        this.step = null;
    }

    public DimensionTemporal(String step, List<String> extent) {
        super(TypeEnum.TEMPORAL);
        this.values = null;
        this.extent = extent;
        this.step = step;
    }

    @XmlElement(name = "values")
    private List<Object> values = null;

    @XmlElement(name = "extent")
    private List<String> extent = new ArrayList<>();

    @XmlElement(name = "step")
    private String step = null;

    public DimensionTemporal values(List<Object> values) {
        this.values = values;
        return this;
    }

    public DimensionTemporal addValuesItem(Object valuesItem) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        this.values.add(valuesItem);
        return this;
    }

    public DimensionTemporal extent(List<String> extent) {
        this.extent = extent;
        return this;
    }

    public DimensionTemporal addExtentItem(String extentItem) {
        this.extent.add(extentItem);
        return this;
    }

    public DimensionTemporal step(String step) {
        this.step = step;
        return this;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    public List<Object> getValues() {
        return values;
    }

    @JsonProperty(JSON_PROPERTY_VALUES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "values")
    public void setValues(List<Object> values) {
        this.values = values;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public List<String> getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public void setExtent(List<String> extent) {
        this.extent = extent;
    }

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public String getStep() { // Corrected method name from 'get()'
        return step;
    }

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DimensionTemporal dimensionTemporal = (DimensionTemporal) o;
        return Objects.equals(this.values, dimensionTemporal.values) &&
                Objects.equals(this.extent, dimensionTemporal.extent) &&
                Objects.equals(this.step, dimensionTemporal.step) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, extent, step, super.hashCode());
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
