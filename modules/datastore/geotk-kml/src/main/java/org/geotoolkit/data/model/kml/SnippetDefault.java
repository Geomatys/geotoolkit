package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class SnippetDefault implements Snippet{

    private int maxLines;
    private String content;

    public SnippetDefault(int maxLines, String content){
        this.maxLines = maxLines;
        this.content = content;
    }

    @Override
    public int getMaxLines() {return this.maxLines;}

    @Override
    public String getContent() {return this.content;}

}
