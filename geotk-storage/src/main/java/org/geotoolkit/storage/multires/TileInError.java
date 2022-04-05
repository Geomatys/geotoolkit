package org.geotoolkit.storage.multires;

import java.util.Optional;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.TileStatus;
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

    static TileInError create(long[] position, final Exception cause) {
        return create(position, null, cause);
    }

    static TileInError create(long[] position, final CharSequence reason, final Exception cause) {
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
            public long[] getIndices() {
                return position;
            }

            @Override
            public TileStatus getStatus() {
                return TileStatus.IN_ERROR;
            }

            @Override
            public Resource getResource() throws DataStoreException {
                throw new DataStoreException(cause);
            }
        };
    }
}
