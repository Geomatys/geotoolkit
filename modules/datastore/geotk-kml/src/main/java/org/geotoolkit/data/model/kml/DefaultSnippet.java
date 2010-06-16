package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSnippet implements Snippet{

    private final int maxLines;
    private final String content;

    /**
     *
     * @param maxLines
     * @param content
     */
    public DefaultSnippet(int maxLines, String content){
        this.maxLines = maxLines;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getMaxLines() {return this.maxLines;}


    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

}
