package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class CountryNameCodeDefault implements CountryNameCode {

    private final String content;
    private final String scheme;
    private final GrPostal grPostal;

    /**
     *
     * @param sheme
     * @param grPostal
     * @param content
     */
    public CountryNameCodeDefault(String sheme, GrPostal grPostal, String content){
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
