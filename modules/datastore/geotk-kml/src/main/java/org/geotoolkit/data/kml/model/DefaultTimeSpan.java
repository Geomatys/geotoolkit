package org.geotoolkit.data.kml.model;

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTimeSpan extends DefaultAbstractTimePrimitive implements TimeSpan {

    private Calendar begin;
    private Calendar end;
    private List<SimpleType> timeSpanSimpleExtensions;
    private List<AbstractObject> timeSpanObjectExtensions;

    /**
     * 
     */
    public DefaultTimeSpan() {
        this.timeSpanSimpleExtensions = EMPTY_LIST;
        this.timeSpanObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param abstractTimePrimitiveSimpleExtensions
     * @param abstractTimePrimitiveObjectExtensions
     * @param begin
     * @param end
     * @param timeSpanSimpleExtensions
     * @param timeSpanObjectExtensions
     */
    public DefaultTimeSpan(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end,
            List<SimpleType> timeSpanSimpleExtensions,
            List<AbstractObject> timeSpanObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions);
        this.begin = begin;
        this.end = end;
        this.timeSpanSimpleExtensions = (timeSpanSimpleExtensions == null) ? EMPTY_LIST : timeSpanSimpleExtensions;
        this.timeSpanObjectExtensions = (timeSpanObjectExtensions == null) ? EMPTY_LIST : timeSpanObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getBegin() {
        return this.begin;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Calendar getEnd() {
        return this.end;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getTimeSpanSimpleExtensions() {
        return this.timeSpanSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getTimeSpanObjectExtensions() {
        return this.timeSpanObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setBegin(Calendar begin) {
        this.begin = begin;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setEnd(Calendar end) {
        this.end = end;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimeSpanSimpleExtensions(List<SimpleType> timeSpanSimpleExtensions) {
        this.timeSpanSimpleExtensions = timeSpanSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimeSpanObjectExtensions(List<AbstractObject> timeSpanObjectExtensions) {
        this.timeSpanObjectExtensions = timeSpanObjectExtensions;
    }
}
