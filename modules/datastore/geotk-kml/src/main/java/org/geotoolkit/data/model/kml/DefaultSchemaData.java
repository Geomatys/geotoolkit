package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSchemaData extends DefaultAbstractObject implements SchemaData {

    public final List<SimpleData> simpleDatas;
    public final List<Object> schemaDataExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param simpleDatas
     * @param schemaDataExtensions
     */
    public DefaultSchemaData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.simpleDatas = (simpleDatas == null) ? EMPTY_LIST : simpleDatas;
        this.schemaDataExtensions = (schemaDataExtensions == null) ? EMPTY_LIST : schemaDataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleData> getSimpleDatas() {return this.simpleDatas;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getSchemaDataExtensions() {return this.schemaDataExtensions;}

}
