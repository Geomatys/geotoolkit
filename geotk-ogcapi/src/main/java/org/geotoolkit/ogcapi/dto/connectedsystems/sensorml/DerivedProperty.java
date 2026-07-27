/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.dto.connectedsystems.sensorml;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.geotoolkit.ogcapi.dto.DataTransferObject;
import org.geotoolkit.ogcapi.dto.connectedsystems.common.Link;
import org.geotoolkit.ogcapi.dto.connectedsystems.swecommon.AbstractDataComponent;

/**
 * A property derived from a base property by application of a qualifier and/or a statistical
 * function, backing the Part 1 {@code Property} API resource.
 *
 * <p>Unlike the rest of this hierarchy, this class extends {@link DataTransferObject} directly
 * rather than {@link DescribedObject}: the standard defines it as an {@code allOf
 * AbstractSweIdentifiable} (the shared {@code id}/{@code label}/{@code description} base of SWE
 * Common components), not {@code allOf DescribedObject}.</p>
 *
 * @see <a href="https://docs.ogc.org/is/23-000/23-000.html">OGC SensorML 3.0 JSON Encoding</a>
 * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
 */
public class DerivedProperty extends DataTransferObject {

    public static final String JSON_PROPERTY_ID = "id";
    private String id;

    public static final String JSON_PROPERTY_LABEL = "label";
    private String label;

    public static final String JSON_PROPERTY_DESCRIPTION = "description";
    private String description;

    public static final String JSON_PROPERTY_UNIQUE_ID = "uniqueId";
    private String uniqueId;

    public static final String JSON_PROPERTY_BASE_PROPERTY = "baseProperty";
    private String baseProperty;

    public static final String JSON_PROPERTY_OBJECT_TYPE = "objectType";
    private String objectType;

    public static final String JSON_PROPERTY_STATISTIC = "statistic";
    private String statistic;

    public static final String JSON_PROPERTY_QUALIFIERS = "qualifiers";
    private List<AbstractDataComponent> qualifiers;

    public static final String JSON_PROPERTY_LINKS = "links";
    private List<Link> links;

    /**
     * Local identifier. (optional)
     */
    @JsonProperty(JSON_PROPERTY_ID)
    public String getId() {
        return id;
    }

    @JsonProperty(JSON_PROPERTY_ID)
    public void setId(String id) {
        this.id = id;
    }

    public DerivedProperty id(String id) {
        setId(id);
        return this;
    }

    /**
     * Human readable label. (required)
     */
    @JsonProperty(JSON_PROPERTY_LABEL)
    public String getLabel() {
        return label;
    }

    @JsonProperty(JSON_PROPERTY_LABEL)
    public void setLabel(String label) {
        this.label = label;
    }

    public DerivedProperty label(String label) {
        setLabel(label);
        return this;
    }

    /**
     * Description of the property. (optional)
     */
    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @JsonProperty(JSON_PROPERTY_DESCRIPTION)
    public void setDescription(String description) {
        this.description = description;
    }

    public DerivedProperty description(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Globally unique identifier (URI) of this property. Optional in the base SensorML model but
     * required at the OGC API - Connected Systems Part 1 layer. (required at API layer)
     */
    @JsonProperty(JSON_PROPERTY_UNIQUE_ID)
    public String getUniqueId() {
        return uniqueId;
    }

    @JsonProperty(JSON_PROPERTY_UNIQUE_ID)
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public DerivedProperty uniqueId(String uniqueId) {
        setUniqueId(uniqueId);
        return this;
    }

    /**
     * URI of the base property this one is derived from. (required)
     */
    @JsonProperty(JSON_PROPERTY_BASE_PROPERTY)
    public String getBaseProperty() {
        return baseProperty;
    }

    @JsonProperty(JSON_PROPERTY_BASE_PROPERTY)
    public void setBaseProperty(String baseProperty) {
        this.baseProperty = baseProperty;
    }

    public DerivedProperty baseProperty(String baseProperty) {
        setBaseProperty(baseProperty);
        return this;
    }

    /**
     * URI of the type of object/feature this property applies to. (optional)
     */
    @JsonProperty(JSON_PROPERTY_OBJECT_TYPE)
    public String getObjectType() {
        return objectType;
    }

    @JsonProperty(JSON_PROPERTY_OBJECT_TYPE)
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public DerivedProperty objectType(String objectType) {
        setObjectType(objectType);
        return this;
    }

    /**
     * URI of the statistical function applied to derive this property (e.g. mean, max). (optional)
     */
    @JsonProperty(JSON_PROPERTY_STATISTIC)
    public String getStatistic() {
        return statistic;
    }

    @JsonProperty(JSON_PROPERTY_STATISTIC)
    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public DerivedProperty statistic(String statistic) {
        setStatistic(statistic);
        return this;
    }

    /**
     * Qualifiers further restricting the applicability of this property (e.g. a time interval, a
     * spatial extent). (optional)
     */
    @JsonProperty(JSON_PROPERTY_QUALIFIERS)
    public List<AbstractDataComponent> getQualifiers() {
        return qualifiers;
    }

    @JsonProperty(JSON_PROPERTY_QUALIFIERS)
    public void setQualifiers(List<AbstractDataComponent> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public DerivedProperty qualifiers(List<AbstractDataComponent> qualifiers) {
        setQualifiers(qualifiers);
        return this;
    }

    /**
     * Web links related to this resource. This field is not part of the base SensorML 3.0 model:
     * it is added by the OGC API - Connected Systems Part 1 encoding of the Property resource.
     * (optional)
     *
     * @see <a href="https://docs.ogc.org/is/23-001/23-001.html">OGC API - Connected Systems - Part 1</a>
     */
    @JsonProperty(JSON_PROPERTY_LINKS)
    public List<Link> getLinks() {
        return links;
    }

    @JsonProperty(JSON_PROPERTY_LINKS)
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public DerivedProperty links(List<Link> links) {
        setLinks(links);
        return this;
    }

}
