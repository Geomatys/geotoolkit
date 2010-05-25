package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DataDefault extends AbstractObjectDefault implements Data {

    private String displayName;
    private String value;
    private List<Object> dataExtensions;

    public DataDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.displayName = displayName;
        this.value = value;
        this.dataExtensions = dataExtensions;
    }

    @Override
    public String getDisplayName() {return this.displayName;}

    @Override
    public String getValue() {return this.value;}

    @Override
    public List<Object> getDataExtensions() {return this.dataExtensions;}

}
