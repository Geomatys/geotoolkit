package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class TimeStampDefault extends AbstractTimePrimitiveDefault implements TimeStamp {

    private String when;
    private List<SimpleType> timeStampSimpleExtensions;
    private List<AbstractObject> timeStampObjectExtensions;

    public TimeStampDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions){
        super(objectSimpleExtensions, idAttributes,
                abstractTimePrimitiveSimpleExtensions, abstractTimePrimitiveObjectExtensions);
        this.when = when;
        this.timeStampSimpleExtensions = timeStampSimpleExtensions;
        this.timeStampObjectExtensions = timeStampObjectExtensions;
    }

    @Override
    public String getWhen() {return this.when;}

    @Override
    public List<SimpleType> getTimeStampSimpleExtensions() {return this.timeStampSimpleExtensions;}

    @Override
    public List<AbstractObject> getTimeStampObjectExtensions() {return this.timeStampObjectExtensions;}

}
