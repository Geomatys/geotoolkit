package org.geotoolkit.data.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultThoroughfareNumberRange implements ThoroughfareNumberRange {

    private final List<GenericTypedGrPostal> addressLines;
    private final ThoroughfareNumberFrom thoroughfareNumberFrom;
    private final ThoroughfareNumberTo thoroughfareNumberTo;
    private final OddEvenEnum rangeType;
    private final String indicator;
    private final String separator;
    private final String type;
    private final AfterBeforeEnum indicatorOccurrence;
    private final AfterBeforeTypeNameEnum numberRangeOccurrence;

    /**
     *
     * @param addressLines
     * @param thoroughfareNumberFrom
     * @param thoroughfareNumberTo
     * @param rangeType
     * @param indicator
     * @param separator
     * @param type
     * @param indicatorOccurrence
     * @param numberRangeOccurrence
     */
    public DefaultThoroughfareNumberRange(List<GenericTypedGrPostal> addressLines,
            ThoroughfareNumberFrom thoroughfareNumberFrom,
            ThoroughfareNumberTo thoroughfareNumberTo,
            OddEvenEnum rangeType, String indicator, String separator, String type,
            AfterBeforeEnum indicatorOccurrence, AfterBeforeTypeNameEnum numberRangeOccurrence){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.thoroughfareNumberFrom = thoroughfareNumberFrom;
        this.thoroughfareNumberTo = thoroughfareNumberTo;
        this.rangeType = rangeType;
        this.indicator = indicator;
        this.separator = separator;
        this.type = type;
        this.indicatorOccurrence = indicatorOccurrence;
        this.numberRangeOccurrence = numberRangeOccurrence;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberFrom getThoroughfareNumberFrom() {return this.thoroughfareNumberFrom;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ThoroughfareNumberTo getThoroughfareNumberTo() {return this.thoroughfareNumberTo;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public OddEvenEnum getRangeType() {return this.rangeType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getSeparator() {return this.separator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeEnum getIndicatorOccurence() {return this.indicatorOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AfterBeforeTypeNameEnum getNumberRangeOccurence() {return this.numberRangeOccurrence;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

}
