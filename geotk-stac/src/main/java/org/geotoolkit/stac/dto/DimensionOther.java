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
 * Based on : <a href="https://github.com/radiantearth/stac-spec/blob/master/collection-spec/collection-spec.md">STAC Spec Github</a>
 */
@JsonPropertyOrder({
        DimensionOther.JSON_PROPERTY_EXTENT,
        DimensionOther.JSON_PROPERTY_VALUES,
        DimensionOther.JSON_PROPERTY_STEP,
        DimensionOther.JSON_PROPERTY_UNIT,
        DimensionOther.JSON_PROPERTY_REFERENCE_SYSTEM
})
@XmlRootElement(name = "DimensionOther")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "DimensionOther")
public class DimensionOther extends Dimension {

    // --- PROPERTY CONSTANTS ---
    public static final String JSON_PROPERTY_EXTENT = "extent";
    public static final String JSON_PROPERTY_VALUES = "values";
    public static final String JSON_PROPERTY_STEP = "step";
    public static final String JSON_PROPERTY_UNIT = "unit";
    public static final String JSON_PROPERTY_REFERENCE_SYSTEM = "reference_system";

    public DimensionOther() {
        super(TypeEnum.OTHER);
    }

    public DimensionOther(List<Object> extent, List<Object> values, String unit, String referenceSystem) {
        super(TypeEnum.OTHER);
        this.extent = extent;
        this.values = values;
        this.unit = unit;
        this.referenceSystem = referenceSystem;
    }

    public DimensionOther(List<Object> extent, String step, String unit, String referenceSystem) {
        super(TypeEnum.OTHER);
        this.extent = extent;
        this.step = step;
        this.unit = unit;
        this.referenceSystem = referenceSystem;
    }

    @XmlElement(name = "extent")
    private List<Object> extent = null;

    @XmlElement(name = "values")
    private List<Object> values = null;

    @XmlElement(name = "step")
    private String step = null;

    @XmlElement(name = "unit")
    private String unit;

    @XmlElement(name = "referenceSystem")
    private String referenceSystem;

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public List<Object> getExtent() {
        return extent;
    }

    @JsonProperty(JSON_PROPERTY_EXTENT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "extent")
    public void setExtent(List<Object> extent) {
        this.extent = extent;
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

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public String getStep() {
        return step;
    }

    @JsonProperty(JSON_PROPERTY_STEP)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "step")
    public void setStep(String step) {
        this.step = step;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "unit")
    public String getUnit() {
        return unit;
    }

    @JsonProperty(JSON_PROPERTY_UNIT)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "unit")
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public String getReferenceSystem() {
        return referenceSystem;
    }

    @JsonProperty(JSON_PROPERTY_REFERENCE_SYSTEM)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "referenceSystem")
    public void setReferenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
    }

    public DimensionOther step(String step) {
        this.step = step;
        return this;
    }

    public DimensionOther unit(String unit) {
        this.unit = unit;
        return this;
    }

    public DimensionOther referenceSystem(String referenceSystem) {
        this.referenceSystem = referenceSystem;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DimensionOther dimensionOther = (DimensionOther) o;
        return Objects.equals(this.extent, dimensionOther.extent) &&
                Objects.equals(this.values, dimensionOther.values) &&
                Objects.equals(this.step, dimensionOther.step) &&
                Objects.equals(this.unit, dimensionOther.unit) &&
                Objects.equals(this.referenceSystem, dimensionOther.referenceSystem) &&
                super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extent, values, step, unit, referenceSystem, super.hashCode());
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
