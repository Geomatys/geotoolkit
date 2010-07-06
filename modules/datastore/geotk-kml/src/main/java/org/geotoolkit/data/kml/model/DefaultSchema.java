package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSchema implements Schema {

    private List<SimpleField> simpleFields;
    private String name;
    private String id;
    private List<Object> schemaExtensions;

    /**
     * 
     */
    public DefaultSchema() {
        this.simpleFields = EMPTY_LIST;
        this.schemaExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param simpleFields
     * @param name
     * @param id
     * @param schemaExtensions
     */
    public DefaultSchema(List<SimpleField> simpleFields,
            String name, String id, List<Object> schemaExtensions) {
        this.simpleFields = (simpleFields == null) ? EMPTY_LIST : simpleFields;
        this.name = name;
        this.id = id;
        this.schemaExtensions = (schemaExtensions == null) ? EMPTY_LIST : schemaExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleField> getSimpleFields() {
        return this.simpleFields;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getSchemaExtensions() {
        return this.schemaExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSimpleFields(List<SimpleField> simpleFields) {
        this.simpleFields = simpleFields;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemaExtensions(List<Object> schemaExtensions) {
        this.schemaExtensions = schemaExtensions;
    }
}
