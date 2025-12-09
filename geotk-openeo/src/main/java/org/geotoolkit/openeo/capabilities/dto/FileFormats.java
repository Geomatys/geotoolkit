package org.geotoolkit.openeo.capabilities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        FileFormats.JSON_PROPERTY_INPUT,
        FileFormats.JSON_PROPERTY_OUTPUT
})
@XmlRootElement(name = "FileFormats")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "FileFormats")
public class FileFormats extends DataTransferObject {

    public static final String JSON_PROPERTY_INPUT = "input";
    @XmlTransient
    @XmlElement(name = "input")
    @jakarta.annotation.Nonnull
    private Map<String, FileFormat> input = new HashMap<>();

    public static final String JSON_PROPERTY_OUTPUT = "output";
    @XmlTransient
    @XmlElement(name = "output")
    @jakarta.annotation.Nonnull
    private Map<String, FileFormat> output = new HashMap<>();

    public FileFormats() {
    }

    public FileFormats(Map<String, FileFormat> input, Map<String, FileFormat> output) {
        this.input = input;
        this.output = output;
    }

    public FileFormats input(Map<String, FileFormat> input) {
        this.input = input;
        return this;
    }

    public FileFormats putInputItem(String key, FileFormat inputItem) {
        this.input.put(key, inputItem);
        return this;
    }

    public Map<String, FileFormat> getInput() {
        return input;
    }

    public void setInput(Map<String, FileFormat> input) {
        this.input = input;
    }

    public FileFormats output(Map<String, FileFormat> output) {
        this.output = output;
        return this;
    }

    public FileFormats putOutputItem(String key, FileFormat outputItem) {
        this.output.put(key, outputItem);
        return this;
    }

    public Map<String, FileFormat> getOutput() {
        return output;
    }

    public void setOutput(Map<String, FileFormat> output) {
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileFormats fileFormats = (FileFormats) o;
        return Objects.equals(this.input, fileFormats.input) &&
                Objects.equals(this.output, fileFormats.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output);
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
