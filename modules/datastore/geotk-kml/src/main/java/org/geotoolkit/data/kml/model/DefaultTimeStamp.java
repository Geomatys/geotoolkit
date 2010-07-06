package org.geotoolkit.data.kml.model;

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTimeStamp extends DefaultAbstractTimePrimitive implements TimeStamp {

    private final Extensions exts = new Extensions();
    private Calendar when;

    /**
     * 
     */
    public DefaultTimeStamp() {
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
    public DefaultTimeStamp(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar when,
            List<SimpleType> timeStampSimpleExtensions,
            List<AbstractObject> timeStampObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions,
                abstractTimePrimitiveObjectExtensions);
        this.when = when;
        if (timeStampSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_STAMP).addAll(timeStampSimpleExtensions);
        }
        if (timeStampObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_STAMP).addAll(timeStampObjectExtensions);
        }
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
    public void setWhen(Calendar when) {
        this.when = when;
    }
}
