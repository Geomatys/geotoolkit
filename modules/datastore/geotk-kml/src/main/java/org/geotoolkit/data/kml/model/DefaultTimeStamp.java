package org.geotoolkit.data.kml.model;

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTimeStamp extends DefaultAbstractTimePrimitive implements TimeStamp {

    private Calendar when;
    private List<SimpleType> timeStampSimpleExtensions;
    private List<AbstractObject> timeStampObjectExtensions;

    /**
     * 
     */
    public DefaultTimeStamp(){
        this.timeStampSimpleExtensions = EMPTY_LIST;
        this.timeStampObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param when
     * @param timeStampSimpleExtensions
     * @param timeStampObjectExtensions
     */
    public DefaultTimeStamp(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions);
        this.when = when;
        this.timeStampSimpleExtensions = (timeStampSimpleExtensions == null) ? EMPTY_LIST : timeStampSimpleExtensions;
        this.timeStampObjectExtensions = (timeStampObjectExtensions == null) ? EMPTY_LIST : timeStampObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getWhen() {
        return this.when;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getTimeStampSimpleExtensions() {
        return this.timeStampSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getTimeStampObjectExtensions() {
        return this.timeStampObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setWhen(Calendar when) {
        this.when = when;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimeStampSimpleExtensions(List<SimpleType> timeStampSimpleExtensions) {
        this.timeStampSimpleExtensions = timeStampSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimeStampObjectExtensions(List<AbstractObject> timeStampObjectExtensions) {
        this.timeStampObjectExtensions = timeStampObjectExtensions;
    }
}
