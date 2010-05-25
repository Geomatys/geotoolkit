package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class TimeSpanDefault extends AbstractTimePrimitiveDefault implements TimeSpan {

    private String begin;
    private String end;
    private List<SimpleType> timeSpanSimpleExtensions;
    private List<AbstractObject> timeSpanObjectExtensions;

    public TimeSpanDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String begin, String end, List<SimpleType> timeSpanSimpleExtensions, List<AbstractObject> timeSpanObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions);
        this.begin = begin;
        this.end = end;
        this.timeSpanSimpleExtensions = timeSpanSimpleExtensions;
        this.timeSpanObjectExtensions = timeSpanObjectExtensions;
    }

    @Override
    public String getBegin() {return this.begin;}

    @Override
    public String getEnd() {return this.end;}

    @Override
    public List<SimpleType> getTimeSpanSimpleExtensions() {return this.timeSpanSimpleExtensions;}

    @Override
    public List<AbstractObject> getTimeSpanObjectExtensions() {return this.timeSpanObjectExtensions;}

}
