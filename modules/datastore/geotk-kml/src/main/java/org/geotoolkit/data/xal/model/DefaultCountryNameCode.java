package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCountryNameCode implements CountryNameCode {

    private final String content;
    private final String scheme;
    private final GrPostal grPostal;

    /**
     *
     * @param sheme
     * @param grPostal
     * @param content
     */
    public DefaultCountryNameCode(String sheme, GrPostal grPostal, String content){
        this.scheme = sheme;
        this.grPostal = grPostal;
        this.content = content;
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
    public String getScheme() {return this.scheme;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
