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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
@JsonPropertyOrder({
        Billing.JSON_PROPERTY_CURRENCY,
        Billing.JSON_PROPERTY_DEFAULT_PLAN,
        Billing.JSON_PROPERTY_PLANS
})
@XmlRootElement(name = "Billing")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Billing")
public class Billing extends DataTransferObject {

    public static final String JSON_PROPERTY_CURRENCY = "currency";
    @XmlElement(name = "currency")
    @jakarta.annotation.Nullable
    private String currency = "EUR";

    public static final String JSON_PROPERTY_DEFAULT_PLAN = "default_plan";
    @XmlElement(name = "default_plan")
    @jakarta.annotation.Nullable
    private String defaultPlan;

    public static final String JSON_PROPERTY_PLANS = "plans";
    @XmlElement(name = "plans")
    @jakarta.annotation.Nullable
    private List<BillingPlan> plans = null;

    public Billing(String currency, String defaultPlan, List<BillingPlan> plans) {
        this.currency = currency;
        this.defaultPlan = defaultPlan;
        this.plans = plans;
    }

    /**
     * Get currency
     *
     * @return currency
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_CURRENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty(JSON_PROPERTY_CURRENCY)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "currency")
    public void setCurrency(@jakarta.annotation.Nullable String currency) {
        this.currency = currency;
    }

    public Billing defaultPlan(@jakarta.annotation.Nullable String defaultPlan) {
        this.defaultPlan = defaultPlan;
        return this;
    }

    /**
     * Get defaultPlan
     *
     * @return defaultPlan
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DEFAULT_PLAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default_plan")
    public String getDefaultPlan() {
        return defaultPlan;
    }

    @JsonProperty(JSON_PROPERTY_DEFAULT_PLAN)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "default_plan")
    public void setDefaultPlan(@jakarta.annotation.Nullable String defaultPlan) {
        this.defaultPlan = defaultPlan;
    }

    public Billing plans(@jakarta.annotation.Nullable List<BillingPlan> plans) {
        this.plans = plans;
        return this;
    }

    public Billing addPlansItem(BillingPlan plansItem) {
        if (this.plans == null) {
            this.plans = new ArrayList<>();
        }
        this.plans.add(plansItem);
        return this;
    }

    /**
     * Get plans
     *
     * @return plans
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_PLANS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "plans")
    public List<BillingPlan> getPlans() {
        return plans;
    }

    @JsonProperty(JSON_PROPERTY_PLANS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "plans")
    public void setPlans(@jakarta.annotation.Nullable List<BillingPlan> plans) {
        this.plans = plans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Billing billing = (Billing) o;
        return Objects.equals(this.currency, billing.currency) &&
                Objects.equals(this.defaultPlan, billing.defaultPlan) &&
                Objects.equals(this.plans, billing.plans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, defaultPlan, plans);
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
