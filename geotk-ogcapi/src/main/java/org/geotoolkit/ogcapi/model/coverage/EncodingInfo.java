package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        EncodingInfo.JSON_PROPERTY_DATA_TYPE
})
@XmlRootElement(name = "EncodingInfo")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "EncodingInfo")
public class EncodingInfo extends DataTransferObject {

    public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
    @XmlElement(name = "dataType")
    private String dataType;

    public EncodingInfo(String dataType) {
        this.dataType = dataType;
    }

    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dataType")
    public String getDataType() {
        return dataType;
    }

    @JsonProperty(JSON_PROPERTY_DATA_TYPE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "dataType")
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @JsonIgnore
    private static final Map<Class<?>, String> TYPE_TO_LINK_MAP = new HashMap<>();

    static {
        TYPE_TO_LINK_MAP.put(Byte.class, "https://www.opengis.net/def/data-type/ogc/0/unsigned-byte");
        TYPE_TO_LINK_MAP.put(Short.class, "https://www.opengis.net/def/data-type/ogc/0/unsigned-short");
        TYPE_TO_LINK_MAP.put(Integer.class, "https://www.opengis.net/def/data-type/ogc/0/unsigned-int");
        TYPE_TO_LINK_MAP.put(Long.class, "https://www.opengis.net/def/data-type/ogc/0/unsigned-long");
        TYPE_TO_LINK_MAP.put(Float.class, "https://www.opengis.net/def/data-type/ogc/0/float32");
        TYPE_TO_LINK_MAP.put(Double.class, "https://www.opengis.net/def/data-type/ogc/0/double");
        // Add other types here
    }

    public static String getOpenGisLink(Class<?> type) {
        String link = TYPE_TO_LINK_MAP.get(type);
        if (link == null) {
            return "empty";
        } else {
            return link;
        }
    }
}
