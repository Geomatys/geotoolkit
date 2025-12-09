package org.geotoolkit.ogcapi.model.coverage;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Quentin BIALOTA
 */
@JsonPropertyOrder({
        // Since IndexAxis only inherits properties, the order is managed by the parent's @JsonPropertyOrder.
        // We include this annotation for consistency in case future properties are added.
})
@XmlRootElement(name = "IndexAxis")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "IndexAxis")
public class IndexAxis extends Axis {

    public IndexAxis() {
        super("IndexAxis", null, null, null);
    }

    public IndexAxis(String axisLabel, Object lowerBound, Object upperBound) {
        super("IndexAxis", axisLabel, lowerBound, upperBound);
    }
}
