package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultData extends DefaultAbstractObject implements Data {

    private final String displayName;
    private final String value;
    private final List<Object> dataExtensions;

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param displayName
     * @param value
     * @param dataExtensions
     */
    public DefaultData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions){
        super(objectSimpleExtensions, idAttributes);
        this.displayName = displayName;
        this.value = value;
        this.dataExtensions = (dataExtensions == null) ? EMPTY_LIST : dataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDisplayName() {return this.displayName;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValue() {return this.value;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getDataExtensions() {return this.dataExtensions;}

}
