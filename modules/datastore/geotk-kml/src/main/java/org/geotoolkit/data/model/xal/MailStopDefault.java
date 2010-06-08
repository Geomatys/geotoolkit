package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class MailStopDefault implements MailStop {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> mailStopNames;
    private final MailStopNumber mailStopNumber;
    private final String type;

    /**
     *
     * @param addressLines
     * @param mailStopNames
     * @param mailStopNumber
     * @param type
     */
    public MailStopDefault(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> mailStopNames,
            MailStopNumber mailStopNumber, String type){
        this.addressLines = addressLines;
        this.mailStopNames = mailStopNames;
        this.mailStopNumber = mailStopNumber;
        this.type = type;
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
    public List<GenericTypedGrPostal> getMailStopNames() {return this.mailStopNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStopNumber getMailStopNumber() {return this.mailStopNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

}
