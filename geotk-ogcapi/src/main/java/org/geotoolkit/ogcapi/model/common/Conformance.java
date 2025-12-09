package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.ogcapi.model.DataTransferObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin BIALOTA
 * @author Guilhem LEGAL
 */
@XmlRootElement(name = "ConformsTo")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conformance extends DataTransferObject implements CommonResponse {

    @XmlElement(
            name = "link",
            namespace = "http://www.w3.org/2005/Atom"
    )
    @JsonProperty("conformsTo")
    private List<Link> conformsTo;

    public Conformance() {
        conformsTo = new ArrayList<>();
    }

    public Conformance(List<Link> conformsTo) {
        this.conformsTo = conformsTo;
    }

    /**
     * @return the array list of link
     */
    public List<Link> getConformsTo() {
        return conformsTo;
    }

    /**
     * @param conformsTo the array list to set
     */
    public void setConformsTo(List<Link> conformsTo) {
        this.conformsTo = conformsTo;
    }
}
