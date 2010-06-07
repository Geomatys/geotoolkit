package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class ExtendedDataDefault implements ExtendedData {

    private final List<Data> datas;
    private final List<SchemaData> schemaDatas;
    private final List<Object> anyOtherElements;

    /**
     *
     * @param datas
     * @param schemaDatas
     * @param anyOtherElements
     */
    public ExtendedDataDefault(List<Data> datas, List<SchemaData> schemaDatas, List<Object> anyOtherElements){
        this.datas = (datas == null) ? EMPTY_LIST : datas;
        this.schemaDatas = (schemaDatas == null) ? EMPTY_LIST : schemaDatas;
        this.anyOtherElements = (anyOtherElements == null) ? EMPTY_LIST : anyOtherElements;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Data> getDatas() {return this.datas;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SchemaData> getSchemaData() {return this.schemaDatas;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getAnyOtherElements() {return this.anyOtherElements;}

}
