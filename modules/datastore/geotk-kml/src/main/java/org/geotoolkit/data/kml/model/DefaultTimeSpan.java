package org.geotoolkit.data.kml.model;

import java.util.Calendar;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultTimeSpan extends DefaultAbstractTimePrimitive implements TimeSpan {

    private Calendar begin;
    private Calendar end;

    /**
     * 
     */
    public DefaultTimeSpan() {
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
    public DefaultTimeSpan(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions,
            List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            Calendar begin, Calendar end,
            List<SimpleType> timeSpanSimpleExtensions,
            List<AbstractObject> timeSpanObjectExtensions) {
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions,
                abstractTimePrimitiveObjectExtensions);
        this.begin = begin;
        this.end = end;
        if (timeSpanSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.TIME_SPAN).addAll(timeSpanSimpleExtensions);
        }
        if (timeSpanObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.TIME_SPAN).addAll(timeSpanObjectExtensions);
        }
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
}
