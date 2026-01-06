package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        IrregularAxis.JSON_PROPERTY_UOM_LABEL,
        IrregularAxis.JSON_PROPERTY_COORDINATE
})
@XmlRootElement(name = "IrregularAxis")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "IrregularAxis")
public class IrregularAxis extends Axis{

    public static final String JSON_PROPERTY_UOM_LABEL = "uomLabel";
    @XmlElement(name = "uomLabel")
    private String uomLabel;

    public static final String JSON_PROPERTY_COORDINATE = "coordinate";
    @XmlElement(name = "coordinate")
    private List<Object> coordinate;

    public IrregularAxis(String axisLabel, Object lowerBound, Object upperBound, String uomLabel, List<Object> coordinate) {
        super("IrregularAxis", axisLabel, lowerBound, upperBound);
        this.coordinate = coordinate;
        this.uomLabel = uomLabel;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinate")
    public List<Object> getCoordinate() {
        return coordinate;
    }

    @JsonProperty(JSON_PROPERTY_COORDINATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "coordinate")
    public void setCoordinate(List<Object> coordinate) {
        this.coordinate = coordinate;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uomLabel")
    public String getUomLabel() {
        return uomLabel;
    }

    @JsonProperty(JSON_PROPERTY_UOM_LABEL)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "uomLabel")
    public void setUomLabel(String uomLabel) {
        this.uomLabel = uomLabel;
    }
}
