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
package org.geotoolkit.ogcapi.dto.connectedsystems.swecommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A discriminated choice among several named SWE Common data components.
 *
 * @see <a href="https://docs.ogc.org/is/24-014/24-014.html">OGC SWE Common Data Model Encoding Standard</a>
 */
public class DataChoice extends AbstractDataComponent {

    public static final String JSON_PROPERTY_CHOICE_VALUE = "choiceValue";
    private Category choiceValue;

    public static final String JSON_PROPERTY_ITEMS = "items";
    private List<AbstractDataComponent> items;

    public DataChoice() {
        setType("DataChoice");
    }

    /**
     * Component whose value selects the active choice item. (optional)
     */
    @JsonProperty(JSON_PROPERTY_CHOICE_VALUE)
    public Category getChoiceValue() {
        return choiceValue;
    }

    @JsonProperty(JSON_PROPERTY_CHOICE_VALUE)
    public void setChoiceValue(Category choiceValue) {
        this.choiceValue = choiceValue;
    }

    /**
     * Named alternative components. (required)
     */
    @JsonProperty(JSON_PROPERTY_ITEMS)
    public List<AbstractDataComponent> getItems() {
        return items;
    }

    @JsonProperty(JSON_PROPERTY_ITEMS)
    public void setItems(List<AbstractDataComponent> items) {
        this.items = items;
    }

}
