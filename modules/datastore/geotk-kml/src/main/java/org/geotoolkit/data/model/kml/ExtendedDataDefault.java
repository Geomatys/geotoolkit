package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class ExtendedDataDefault implements ExtendedData {

    private List<Data> datas;
    private List<SchemaData> schemaDatas;
    private List<Object> anyOtherElements;

    public ExtendedDataDefault(List<Data> datas, List<SchemaData> schemaDatas, List<Object> anyOtherElements){
        this.datas = datas;
        this.schemaDatas = schemaDatas;
        this.anyOtherElements = anyOtherElements;
    }

    @Override
    public List<Data> getDatas() {return this.datas;}

    @Override
    public List<SchemaData> getSchemaData() {return this.schemaDatas;}

    @Override
    public List<Object> getAnyOtherElements() {return this.anyOtherElements;}

}
