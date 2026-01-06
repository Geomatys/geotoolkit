package org.geotoolkit.openeo.capabilities.dto;

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

import java.net.URI;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        BillingPlan.JSON_PROPERTY_NAME,
        BillingPlan.JSON_PROPERTY_DESCRIPTION,
        BillingPlan.JSON_PROPERTY_PAID,
        BillingPlan.JSON_PROPERTY_URL
})
@XmlRootElement(name = "BillingPlan")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "BillingPlan")
public class BillingPlan extends DataTransferObject {
    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nullable
    private String name;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    @XmlElement(name = "description")
    @jakarta.annotation.Nullable
    private String description;

    public static final String JSON_PROPERTY_PAID = "paid";
    @XmlElement(name = "paid")
    @jakarta.annotation.Nullable
    private Boolean paid;

    public static final String JSON_PROPERTY_URL = "url";
    @XmlElement(name = "url")
    @jakarta.annotation.Nullable
    private URI url;

    public BillingPlan name(@jakarta.annotation.Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nullable String name) {
        this.name = name;
    }

    public BillingPlan description(@jakarta.annotation.Nullable String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     */
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

    public BillingPlan paid(@jakarta.annotation.Nullable Boolean paid) {
        this.paid = paid;
        return this;
    }

    /**
     * Get paid
     *
     * @return paid
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PAID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "paid")
    public Boolean isPaid() {
        return paid;
    }

    @JsonProperty(JSON_PROPERTY_PAID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "paid")
    public void setPaid(@jakarta.annotation.Nullable Boolean paid) {
        this.paid = paid;
    }

    public BillingPlan url(@jakarta.annotation.Nullable URI url) {
        this.url = url;
        return this;
    }

    /**
     * Get url
     *
     * @return url
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "url")
    public URI getUrl() {
        return url;
    }

    @JsonProperty(JSON_PROPERTY_URL)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "url")
    public void setUrl(@jakarta.annotation.Nullable URI url) {
        this.url = url;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BillingPlan billingPlan = (BillingPlan) o;
        return Objects.equals(this.name, billingPlan.name) &&
                Objects.equals(this.description, billingPlan.description) &&
                Objects.equals(this.paid, billingPlan.paid) &&
                Objects.equals(this.url, billingPlan.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, paid, url);
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
