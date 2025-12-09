package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Argument.JSON_PROPERTY_TYPE,
        Argument.JSON_PROPERTY_DESCRIPTION,
        Argument.JSON_PROPERTY_REQUIRED,
        Argument.JSON_PROPERTY_DEFAULT,
        Argument.JSON_PROPERTY_MINIMUM,
        Argument.JSON_PROPERTY_MAXIMUM,
        Argument.JSON_PROPERTY_ENUM,
        Argument.JSON_PROPERTY_EXAMPLE
})
@XmlRootElement(name = "Argument")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Argument")
public class Argument extends DataTransferObject {

    public enum TypeEnum {
        STRING("string"),
        NUMBER("number"),
        INTEGER("integer"),
        BOOLEAN("boolean"),
        ARRAY("array"),
        OBJECT("object");

        private final String value;

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
    private TypeEnum type;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_REQUIRED = "required";
    @XmlElement(name = "required")
    @jakarta.annotation.Nullable
    private Boolean required = false;

    public static final String JSON_PROPERTY_DEFAULT = "default";
    @XmlElement(name = "default")
    @jakarta.annotation.Nullable
    private Object _default = null;

    public static final String JSON_PROPERTY_MINIMUM = "minimum";
    @XmlElement(name = "minimum")
    @jakarta.annotation.Nullable
    private BigDecimal minimum;

    public static final String JSON_PROPERTY_MAXIMUM = "maximum";
    @XmlElement(name = "maximum")
    @jakarta.annotation.Nullable
    private BigDecimal maximum;

    public static final String JSON_PROPERTY_ENUM = "enum";
    @XmlElement(name = "enum")
    @jakarta.annotation.Nullable
    private List<Object> _enum = null;

    public static final String JSON_PROPERTY_EXAMPLE = "example";
    @XmlElement(name = "example")
    @jakarta.annotation.Nullable
    private Object example = null;

    public Argument type(TypeEnum type) {
        this.type = type;
        return this;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS) // Assuming type is always present
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

    public Argument description(String description) {
        this.description = description;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "description")
    public void setDescription(@jakarta.annotation.Nullable String description) {
        this.description = description;
    }

    public Argument required(Boolean required) {
        this.required = required;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "required")
    public Boolean isRequired() {
        return required;
    }

    @JsonProperty(JSON_PROPERTY_REQUIRED)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "required")
    public void setRequired(@jakarta.annotation.Nullable Boolean required) {
        this.required = required;
    }

    public Argument _default(Object _default) {
        this._default = _default;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default")
    public Object getDefault() {
        return _default;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default")
    public void setDefault(@jakarta.annotation.Nullable Object _default) {
        this._default = _default;
    }

    public Argument minimum(BigDecimal minimum) {
        this.minimum = minimum;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public BigDecimal getMinimum() {
        return minimum;
    }

    @JsonProperty(JSON_PROPERTY_MINIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "minimum")
    public void setMinimum(@jakarta.annotation.Nullable BigDecimal minimum) {
        this.minimum = minimum;
    }

    public Argument maximum(BigDecimal maximum) {
        this.maximum = maximum;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public BigDecimal getMaximum() {
        return maximum;
    }

    @JsonProperty(JSON_PROPERTY_MAXIMUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "maximum")
    public void setMaximum(@jakarta.annotation.Nullable BigDecimal maximum) {
        this.maximum = maximum;
    }

    public Argument _enum(List<Object> _enum) {
        this._enum = _enum;
        return this;
    }

    public Argument addEnumItem(Object _enumItem) {
        if (this._enum == null) {
            this._enum = new ArrayList<>();
        }
        this._enum.add(_enumItem);
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "enum")
    public List<Object> getEnum() {
        return _enum;
    }

    @JsonProperty(JSON_PROPERTY_ENUM)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "enum")
    public void setEnum(@jakarta.annotation.Nullable List<Object> _enum) {
        this._enum = _enum;
    }

    public Argument example(Object example) {
        this.example = example;
        return this;
    }

    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_EXAMPLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "example")
    public Object getExample() {
        return example;
    }

    @JsonProperty(JSON_PROPERTY_EXAMPLE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "example")
    public void setExample(@jakarta.annotation.Nullable Object example) {
        this.example = example;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Argument argument = (Argument) o;
        return Objects.equals(this.type, argument.type) &&
                Objects.equals(this.description, argument.description) &&
                Objects.equals(this.required, argument.required) &&
                Objects.equals(this._default, argument._default) &&
                Objects.equals(this.minimum, argument.minimum) &&
                Objects.equals(this.maximum, argument.maximum) &&
                Objects.equals(this._enum, argument._enum) &&
                Objects.equals(this.example, argument.example);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, description, required, _default, minimum, maximum, _enum, example);
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
