package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class TimeStampDefault extends AbstractTimePrimitiveDefault implements TimeStamp {

    private final String when;
    private final List<SimpleType> timeStampSimpleExtensions;
    private final List<AbstractObject> timeStampObjectExtensions;

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
    public TimeStampDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            List<SimpleType> abstractTimePrimitiveSimpleExtensions, List<AbstractObject> abstractTimePrimitiveObjectExtensions,
            String when, List<SimpleType> timeStampSimpleExtensions, List<AbstractObject> timeStampObjectExtensions){
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
    public String getWhen() {return this.when;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getTimeStampSimpleExtensions() {return this.timeStampSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getTimeStampObjectExtensions() {return this.timeStampObjectExtensions;}

}
