package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultLargeMailUserIdentifier implements LargeMailUserIdentifier {

    private final String type;
    private final String indicator;
    private final GrPostal grPostal;
    private final String content;

    /**
     *
     * @param type
     * @param indicator
     * @param grPostal
     * @param content
     */
    public DefaultLargeMailUserIdentifier(String type,
            String indicator, GrPostal grPostal, String content){
        this.type = type;
        this.indicator = indicator;
        this.grPostal = grPostal;
        this.content = content;
    }

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
    public String getContent() {return this.content;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
