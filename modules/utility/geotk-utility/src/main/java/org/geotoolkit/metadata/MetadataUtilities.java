package org.geotoolkit.metadata;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultOnlineResource;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.util.Static;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.metadata.content.CoverageDescription;
import org.opengis.metadata.identification.Identification;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MetadataUtilities extends Static {

    private MetadataUtilities() {}

    public static String getIdentifier(Metadata metadata) {
        final Collection<? extends Identification> identifications = metadata.getIdentificationInfo();
        for (Identification identification : identifications) {
            final Citation citation = identification.getCitation();
            if (citation != null) {
                for (Identifier identifier : citation.getIdentifiers()) {
                    return identifier.getCode();
                }
            }
        }
        return null;
    }

    /**
     * Update input metadata by adding given URI to its online resources. If the
     * URI is already present in the metadata, calling this method will have no
     * effect (except consuming CPU time).
     *
     * @param target Metadata to update.
     * @param resource The resource to add as linkage into the metadata.
     * @return True if we successfully added given URI as {@link OnlineResource#getLinkage() }.
     * False if we have not because it is already present.
     * @throws UnsupportedOperationException If given metadata is read-only.
     */
    public static boolean addOnlineResource(final Metadata target, URI resource) {
        DefaultDataIdentification info = null;
        boolean alreadySet = false;
        final Iterator<? extends Identification> it = target.getIdentificationInfo().iterator();
        while (it.hasNext()) {
            Identification tmp = it.next();
            if (tmp.getCitation() != null) {
                alreadySet = tmp.getCitation().getOnlineResources().stream()
                        .map(OnlineResource::getLinkage)
                        .anyMatch(resource::equals);
                if (alreadySet) // Wanted resource is already set.
                    break;
            }
            if (tmp instanceof DefaultDataIdentification) {
                info = (DefaultDataIdentification) tmp;
                break;
            }
        }

        if (!alreadySet) {
            if (info == null) {
                info = new DefaultDataIdentification();
                ((Collection) target.getIdentificationInfo()).add(info);
            }
            final DefaultCitation cit = new DefaultCitation();
            cit.getOnlineResources().add(new DefaultOnlineResource(resource));
            info.setCitation(cit);
        }

        return alreadySet;
    }

    public static Stream<CoverageDescription> extractCoverageDescription(final Metadata source) {
        Stream<CoverageDescription> cds = (Stream) source.getContentInfo().stream()
                .filter(info -> info instanceof CoverageDescription);

        return cds;
    }
}
