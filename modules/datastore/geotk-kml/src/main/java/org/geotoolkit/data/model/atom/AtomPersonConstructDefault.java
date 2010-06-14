package org.geotoolkit.data.model.atom;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class AtomPersonConstructDefault implements AtomPersonConstruct{

    final private List<String> names;
    final private List<String> uris;
    final private List<String> emails;

    /**
     *
     * @param names
     * @param uris
     * @param emails
     */
    public AtomPersonConstructDefault(List<String> names, List<String> uris, List<String> emails){
        this.names = names;
        this.uris = uris;
        this.emails = emails;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<String> getNames() {return this.names;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<String> getUris() {return this.uris;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<String> getEmails() {return this.emails;}

}
