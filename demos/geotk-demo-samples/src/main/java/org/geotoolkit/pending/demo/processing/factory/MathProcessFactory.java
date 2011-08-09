
package org.geotoolkit.pending.demo.processing.factory;

import java.util.Collections;

import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;

import org.geotoolkit.process.AbstractProcessingRegistry;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;

/**
 * The factory is registered in META-INF/
 */
public class MathProcessFactory extends AbstractProcessingRegistry{
    
    /** factory name **/
    public static final String NAME = "mymaths";
    public static final DefaultServiceIdentification IDENTIFICATION;

    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public MathProcessFactory() {
        super(AddDescriptor.INSTANCE);
    }
    
    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }
    
}
