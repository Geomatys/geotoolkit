package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSchema implements Schema {

    private final List<SimpleField> simpleFields;
    //private final List<SchemaExtension> schemaExtensions;
    private final String name;
    private final String id;

    /**
     *
     * @param simpleFields
     * @param name
     * @param id
     */
    public DefaultSchema(List<SimpleField> simpleFields,
            String name, String id){
        this.simpleFields = (simpleFields == null) ? EMPTY_LIST : simpleFields;
        this.name = name;
        this.id = id;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleField> getSimpleFields() {return this.simpleFields;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {return this.name;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getId() {return this.id;}

}
