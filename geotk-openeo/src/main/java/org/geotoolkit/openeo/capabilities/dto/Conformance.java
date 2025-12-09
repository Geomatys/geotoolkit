package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Conformance.JSON_PROPERTY_CONFORMS_TO
})
@XmlRootElement(name = "Conformance")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Conformance")
public class Conformance extends DataTransferObject {

    public static final String JSON_PROPERTY_CONFORMS_TO = "conformsTo";
    @XmlElementWrapper(name = "conformsTo")
    @XmlElement(name = "conformsToItem")
    @JacksonXmlElementWrapper(localName = "conformsTo", useWrapping = false)
    @JacksonXmlProperty(localName = "conformsToItem")
    @jakarta.annotation.Nonnull
    private List<String> conformsTo = new ArrayList<>();

    public Conformance() {
    }

    public Conformance(List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }

    public Conformance conformsTo(List<String> conformsTo) {
        this.conformsTo = conformsTo;
        return this;
    }

    public Conformance addConformsTo(String conformsToItem) {
        this.conformsTo.add(conformsToItem);
        return this;
    }

    /**
     * Get conformsTo
     *
     * @return conformsTo
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONFORMS_TO)
    public List<String> getConformsTo() {
        return conformsTo;
    }

    @JsonProperty(JSON_PROPERTY_CONFORMS_TO)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = JSON_PROPERTY_CONFORMS_TO)
    public void setConformsTo(@jakarta.annotation.Nonnull List<String> conformsTo) {
        this.conformsTo = conformsTo;
    }
}
