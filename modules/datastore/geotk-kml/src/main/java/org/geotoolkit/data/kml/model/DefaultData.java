package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultData extends DefaultAbstractObject implements Data {

    private String displayName;
    private String value;
    private List<Object> dataExtensions;

    /**
     *
     */
    public DefaultData() {
        this.dataExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param displayName
     * @param value
     * @param dataExtensions
     */
    public DefaultData(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String displayName, String value, List<Object> dataExtensions) {
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
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getDataExtensions() {
        return this.dataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDataExtensions(List<Object> dataExtensions) {
        this.dataExtensions = dataExtensions;
    }

    @Override
    public Extensions extensions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
