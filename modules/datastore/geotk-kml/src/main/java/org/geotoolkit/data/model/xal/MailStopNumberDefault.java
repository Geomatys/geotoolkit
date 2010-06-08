package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class MailStopNumberDefault implements MailStopNumber {

    private final String content;
    private final String nameNumberSeparator;
    private final GrPostal grPostal;

    /**
     *
     * @param content
     * @param nameNumberSeparator
     * @param grPostal
     */
    public MailStopNumberDefault(String nameNumberSeparator, GrPostal grPostal, String content){
        this.content = content;
        this.nameNumberSeparator = nameNumberSeparator;
        this.grPostal = grPostal;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getNameNumberSeparator() {return this.nameNumberSeparator;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}


}
