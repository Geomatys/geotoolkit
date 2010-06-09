package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class LargeMailUserNameDefault implements LargeMailUserName{

    private final String content;
    private final String type;
    private final String code;

    /**
     * 
     * @param type
     * @param code
     * @param content
     */
    public LargeMailUserNameDefault(String type, String code, String content){
        this.content = content;
        this.type = type;
        this.code = code;
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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCode() {return this.code;}

}
