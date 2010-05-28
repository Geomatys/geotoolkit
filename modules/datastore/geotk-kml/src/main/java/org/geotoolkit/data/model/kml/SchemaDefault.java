package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class SchemaDefault implements Schema {

    private List<SimpleField> simpleFields;
    //private List<SchemaExtension> schemaExtensions;
    private String name;
    private String id;

    /**
     *
     * @param simpleFields
     * @param name
     * @param id
     */
    public SchemaDefault(List<SimpleField> simpleFields,
            String name, String id){
        this.simpleFields = simpleFields;
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
