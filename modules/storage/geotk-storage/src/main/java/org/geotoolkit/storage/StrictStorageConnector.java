package org.geotoolkit.storage;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;
import javax.imageio.stream.ImageInputStream;
import javax.sql.DataSource;
import org.apache.sis.storage.ConcurrentReadException;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.UnsupportedStorageException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.collection.BackingStoreException;

/**
 * Extension of a storage connector providing strong encapsulation of "views". This allows to:
 * <ul>
 *     <li>Adopt a <em>fail-fast</em> behavior in case storage view is corrupted by a user</li>
 *     <li>
 *         Provide easier usage:
 *         <ul>
 *             <li>Initial mark/final rewind is performed internally, user do not need to care about it.</li>
 *             <li>Provide strongly typed operators, to guide user on how to use this object.</li>
 *         </ul>
 *     </li>
 * </ul>
 * The purpose of this class is to be merged in StorageConnector once its principle has been validated.
 *
 * <em>Guarantees</em>:
 * <ul>
 *     <li>This object is <em>not</em> concurrent, and ensure a <em>fail-fast</em> behavior in such cases.</li>
 *     <li>
 *         useAs* methods will enforce following behavior:
 *         <ul>
 *             <li>If possible, rewind properly consumed storage view to its initial state</li>
 *             <li>If above statement is not possible, an error will be immediately propagated, and the connector will be marked as closed.</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * Typical usage:
 * <ol>
 *     <li>Check storage compatibility through `useAs*` methods</li>
 *     <li>If the storage is compatible, commit our choice by locking a storage view, closing the connector in the process.</li>
 * </ol>
 *
 * Example:
 * <pre>
 * final Path file = Paths.get("path/to/file");
 * try (var c = new StrictStorageConnector(new StorageConnector(file)) {
 *
 *   // Use connector automatically reset buffering to check support
 *   Boolean isSupported = c.useAsBuffer((buffer) -%gt; buffer.get() == SEARCHED_KEY);
 *
 *   // Once support is validated, acquire real storage connection. At this point,
 *   // storage life cycle becomes the responsability of the caller, allowing it
 *   // to survive beyond the connector scope.
 *   if (supported) {
 *     try ( InputStream stream = c.commit( InputStream.class ) ) {
 *         // read all needed data from acquired stream
 *     }
 *   }
 * }
 * </pre>
 *
 * TODO: remove once fail-fast connector has been approved on SIS (see branch refactor/strict_storage_connector).
 */
public class StrictStorageConnector implements AutoCloseable {

    private final StorageConnector storage;

    private Object committedStorage;
    private volatile int concurrentFlag;

    public StrictStorageConnector(StorageConnector storage) {
        this.storage = storage;
    }

    public void closeAllExcept(Object view) throws DataStoreException {
        // Closing multiple times is OK. However, if the view is not null, we will let control raise an error.
        if (concurrentFlag < 0 && view == null) return;
        try {
            doUnderControl(() -> {
                concurrentFlag = -1;
                storage.closeAllExcept(view);
                committedStorage = view;
                return null;
            });
        } catch (IOException e) {
            throw new DataStoreException(e);
        }
    }

    /**
     * Provides an in-memory byte buffer containing first bytes of the source storage.
     * To know how many bytes are available, refer to the buffer {@link ByteBuffer#remaining() remaining byte count}.
     * User <em>do not</em> need to rewind buffer after use. It is the storage connector responsability.
     *
     * @param operator User operation to perform against provided buffer.
     * @param <T> Type of result produced by user operator.
     * @return The value computed by input operator.
     * @throws UnsupportedStorageException If queried storage type cannot be accessed in current context.
     * @throws IOException If given operator throws IOException on execution.
     * @throws DataStoreException If an error occurs while fetching queried storage.
     */
    public <T> T useAsBuffer(StorageOperatingFunction<ByteBuffer, T> operator) throws DataStoreException, IOException {
        return doUnderControl(() -> {
            final ByteBuffer buffer = getOrFail(ByteBuffer.class);
            try ( Closeable rewindOnceDone = buffer::rewind ) {
                return operator.apply(buffer);
            }
        });
    }

    /**
     * Specialization of {@link #useAs(Class, StorageOperatingFunction)} for {@link ImageInputStream ImageIO API}.
     */
    public <T> T useAsImageInputStream(StorageOperatingFunction<ImageInputStream, T> operator) throws IOException, DataStoreException {
        return doUnderControl(() -> {
            ImageInputStream stream = getOrFail(ImageInputStream.class);
            final long positionCtrl = stream.getStreamPosition();
            stream.mark();
            T result;
            try ( Closeable rewindOnceDone = stream::reset ) {
                result = operator.apply(stream);
            }
            final long rewindPosition = stream.getStreamPosition();
            if (rewindPosition != positionCtrl) {
                concurrentFlag = -1; // mark this connector as closed/not valid anymore
                throw new StorageControlException(String.format(
                        "Operator has messed with stream marks. Rewind should have positioned at %d, but ended at %d",
                        positionCtrl, rewindPosition
                ), ImageInputStream.class);
            }
            return result;
        });
    }

    /**
     * Temporarily expose storage through queried interface/class to be used by a user defined operator.
     * Notes:
     * <ul>
     *     <li>
     *         This method handles mark before / rewind after usage. User responsability is to not leave additional
     *         marks unrewinded. Therefore, if you just need to sequentially read input, you don't have to mark/rewind
     *         the storage.
     *     </li>
     *     <li>
     *         Provided storage is checked after use, to ensure it has not been corrupted by input operator. If control
     *         fails, a {@link StorageControlException} is raised.
     *     </li>
     * </ul>
     *
     * @param storageType Storage access interface to provide to the operator.
     * @param operator The operator that will access storage to compute a result.
     * @param <S> Storage class
     * @param <T> Type of result computed by user operator.
     * @return The value computed by user operator.
     * @throws UnsupportedStorageException If queried storage type cannot be accessed in current context.
     * @throws StorageControlException If connector has detected storage corruption after operator usage.
     * @throws IOException If given operator throws IOException on execution.
     * @throws DataStoreException If an error occurs while fetching queried storage.
     */
    public <S, T> T useAs(Class<S> storageType, StorageOperatingFunction<? super S, ? extends T> operator) throws IOException, DataStoreException {
        if (ByteBuffer.class.isAssignableFrom(storageType)) return useAsBuffer((StorageOperatingFunction<ByteBuffer, T>) operator);
        else if (ImageInputStream.class.isAssignableFrom(storageType)) return useAsImageInputStream((StorageOperatingFunction<ImageInputStream, T>) operator);
        else if (URI.class.isAssignableFrom(storageType)) return ((StorageOperatingFunction<URI, T>) operator).apply(
                getURI().orElseThrow(() -> new UnsupportedStorageException("Cannot acquire an URI"))
        );
        else if (Path.class.isAssignableFrom(storageType)) return ((StorageOperatingFunction<Path, T>) operator).apply(
                getPath().orElseThrow(() -> new UnsupportedStorageException("Cannot acquire a path"))
        );
        else if (File.class.isAssignableFrom(storageType)) return ((StorageOperatingFunction<File, T>) operator).apply(
                getPath().map(p -> p.toFile()).orElseThrow(() -> new UnsupportedStorageException("Cannot acquire a file"))
        );
        else throw new UnsupportedStorageException("Queried storage type is not supported yet: "+storageType);
    }

    /**
     * Ensure only one storage operation is running at any time against this storage connector. It allows fail-fast
     * behavior if this connector is used in concurrent context.
     *
     * @param operator The operation to perform once we checked no other operation is running.
     *
     * @param <V> Type of result value produced by given operator.
     * @return The result produced by given operator.
     * @throws IOException If anything wrong happens while input operator consumes storage, or we can mark/rewind storage.
     * @throws DataStoreException Same reasons as for IOException + can happen if queried storage is of unsupported type.
     * @throws IllegalStateException If this connector is already closed.
     */
    protected <V> V doUnderControl(StorageCallable<V> operator) throws IOException, DataStoreException {
        if (concurrentFlag < 0) throw new IllegalStateException("...");
        if (concurrentFlag != 0) throw new ConcurrentReadException("...");
        concurrentFlag++;
        try {
            return operator.call();
        } finally {
            concurrentFlag--;
        }
    }

    public Optional<Path> getPath() { return getSilently(Path.class); }

    public Optional<URI> getURI() { return getSilently(URI.class); }

    public Optional<DataSource> getSQLDatasource() { return getSilently(DataSource.class); }

    public Optional<String> getPathAsString() { return getSilently(String.class); }

    /**
     * Retrieve storage in the queried form, closing all other opened view in the same time.
     * <em>Warning</em>: This method also closes this storage connector, making invalid any more calls on it.
     *
     * @param target Type of the view to get back / keep opened.
     * @param <T> Type of the wanted storage connection.
     * @return Underlying storage in the requested form. Never null.
     *
     * @throws IOException If anything goes wrong while initializing storage access.
     * @throws DataStoreException If this connector is used concurrently, or if any problem occurs while initializing view.
     * @throws IllegalStateException If this connector is already closed.
     */
    public <T> T commit(Class<T> target) throws IOException, DataStoreException {
        return doUnderControl(() -> {
            final T result = getOrFail(target);
            concurrentFlag = -1; //close flag
            storage.closeAllExcept(result);
            return result;
        });
    }

    private <T> T getOrFail(Class<T> target) throws DataStoreException {
        T view = storage.getStorageAs(target);
        if (view == null) throw new UnsupportedStorageException();
        return view;
    }

    private <T> Optional<T> getSilently(Class<T> target) {
        try {
            return Optional.ofNullable(storage.getStorageAs(target));
        } catch (UnconvertibleObjectException e) {
            // TODO: log fine
            return Optional.empty();
        } catch (DataStoreException e) {
            // According to current implementation, that should never happen.
            // Moreover, it is not really logic to propagate DataStoreException, as this operation should not involve
            // any "storage" logic (only in-memory path/uri conversion if needed).
            throw new BackingStoreException(e);
        }
    }

    @Override
    public void close() throws IOException, DataStoreException {
        storage.closeAllExcept(committedStorage);
    }

    private interface StorageCallable<V> extends Callable<V> {
        @Override
        V call() throws IOException, DataStoreException;
    }

    @FunctionalInterface
    public interface StorageOperatingFunction<I, O> {
        O apply(I storage) throws IOException, DataStoreException;
    }

    public class StorageControlException extends RuntimeException {
        public final Class<?> storageType;

        public StorageControlException(Class<?> storageType) {
            this(null, null, storageType);
        }

        public StorageControlException(String message, Class<?> storageType) {
            this(message, null, storageType);
        }

        public StorageControlException(Throwable cause, Class<?> storageType) {
            this(null, cause, storageType);
        }

        public StorageControlException(String message, Throwable cause, Class<?> storageType) {
            super(message, cause);
            this.storageType = storageType;
        }
    }
}
