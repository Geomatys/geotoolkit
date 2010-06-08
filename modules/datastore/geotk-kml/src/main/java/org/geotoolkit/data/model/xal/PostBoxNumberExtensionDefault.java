package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostBoxNumberExtensionDefault implements PostBoxNumberExtension {

    private final String content;
    private final String numberExtensionSeparator;

    /**
     * 
     * @param numberExtensionSeparator
     * @param content
     */
    public PostBoxNumberExtensionDefault(String numberExtensionSeparator, String content){
        this.content = content;
        this.numberExtensionSeparator = numberExtensionSeparator;
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
    public String getNumberExtensionSeparator() {return this.numberExtensionSeparator;}

}
