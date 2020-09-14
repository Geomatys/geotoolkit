package org.geotoolkit.storage.multires;

import java.awt.Point;
import java.util.Optional;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;

public interface TileInError extends EmptyTile {
    /**
     *
     * @return The error thrown on tile loading, should never be null.
     */
    Exception getCause();

    /**
     *
     * @return Additional information about the error, if any.
     */
    Optional<CharSequence> reason();

    static TileInError create(Point position, final Exception cause) {
        return create(position, null, cause);
    }

    static TileInError create(Point position, final CharSequence reason, final Exception cause) {
        ensureNonNull("Tile coordinate", position);
        ensureNonNull("Error cause", cause);
        return new TileInError() {
            @Override
            public Exception getCause() {
                return cause;
            }

            @Override
            public Optional<CharSequence> reason() {
                return Optional.ofNullable(reason);
            }

            @Override
            public Point getPosition() {
                return position;
            }

            @Override
            public Optional<GenericName> getIdentifier() throws DataStoreException {
                return Optional.empty();
            }

            @Override
            public Metadata getMetadata() throws DataStoreException {
                throw new UnsupportedOperationException("Not supported yet"); // geomatyz on 14/09/2020
            }

            @Override
            public <T extends StoreEvent> void addListener(Class<T> aClass, StoreListener<? super T> storeListener) {
                throw new UnsupportedOperationException("Not supported yet"); // geomatyz on 14/09/2020
            }

            @Override
            public <T extends StoreEvent> void removeListener(Class<T> aClass, StoreListener<? super T> storeListener) {
                throw new UnsupportedOperationException("Not supported yet"); // geomatyz on 14/09/2020
            }
        };
    }
}
