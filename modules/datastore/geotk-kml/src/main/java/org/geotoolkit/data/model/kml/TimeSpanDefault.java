package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class TimeSpanDefault extends AbstractTimePrimitiveDefault implements TimeSpan {

    private final String begin;
    private final String end;
    private final List<SimpleType> timeSpanSimpleExtensions;
    private final List<AbstractObject> timeSpanObjectExtensions;

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
    public TimeSpanDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String begin, String end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions){
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
    public String getBegin() {return this.begin;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getEnd() {return this.end;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getTimeSpanSimpleExtensions() {return this.timeSpanSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getTimeSpanObjectExtensions() {return this.timeSpanObjectExtensions;}

}
