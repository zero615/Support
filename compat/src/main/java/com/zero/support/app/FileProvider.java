package com.zero.support.app;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.zero.support.compat.AppGlobal;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

public class FileProvider extends ContentProvider {
    private static final String[] COLUMNS = {
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};


    private static final HashMap<String, PathStrategy> sCache = new HashMap<String, PathStrategy>();


    private PathStrategy mStrategy;

    public static void registerPathStrategy(final String authority, final PathStrategy strategy) {
        synchronized (sCache) {
            sCache.put(authority, strategy);
        }
    }

    public static Uri getUriForFile(Context context, String authority,
                                    File file) {
        final PathStrategy strategy = getPathStrategy(context, authority);
        if (strategy == null) {
            return null;
        }
        return strategy.getUriForFile(file);
    }

    /**
     * Return {@link PathStrategy} for given authority, either by parsing or
     * returning from cache.
     */
    private static PathStrategy getPathStrategy(Context context, String authority) {
        PathStrategy strategy;
        synchronized (sCache) {
            strategy = sCache.get(authority);
            if (strategy == null) {
                strategy = XmlPathStrategy.newInstance(context, authority);
                if (strategy != null) {
                    sCache.put(authority, strategy);
                } else {
//                    throw new IllegalArgumentException(
//                            "Failed to parse " + XmlPathStrategy.META_DATA_FILE_PROVIDER_PATHS + " meta-data");
                }
            }
        }
        return strategy;
    }

    /**
     * Copied from ContentResolver.java
     */
    private static int modeToMode(String mode) {
        int modeBits;
        if ("r".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_ONLY;
        } else if ("w".equals(mode) || "wt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else if ("wa".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_WRITE_ONLY
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_APPEND;
        } else if ("rw".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE;
        } else if ("rwt".equals(mode)) {
            modeBits = ParcelFileDescriptor.MODE_READ_WRITE
                    | ParcelFileDescriptor.MODE_CREATE
                    | ParcelFileDescriptor.MODE_TRUNCATE;
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return modeBits;
    }

    private static String[] copyOf(String[] original, int newLength) {
        final String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static Object[] copyOf(Object[] original, int newLength) {
        final Object[] result = new Object[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    @Override
    public boolean onCreate() {
        Application app = (Application) getContext().getApplicationContext();
        AppGlobal.initialize(app);
        authority = app.getPackageName() + ":zero.support.file.provider";
        return true;
    }

    private static String authority;

    public static String getAuthority() {
        return authority;
    }

    public static Uri getUriForFile(File file) {
        return getUriForFile(AppGlobal.getApplication(), authority, file);
    }

    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);

        // Sanity check our security
        if (info.exported) {
            throw new SecurityException("Provider must not be exported");
        }
        if (!info.grantUriPermissions) {
            throw new SecurityException("Provider must grant uri permissions");
        }
        mStrategy = getPathStrategy(context, info.authority);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        // ContentProvider has already checked granted permissions
        final File file = mStrategy.getFileForUri(uri);

        if (projection == null) {
            projection = COLUMNS;
        }

        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int i = 0;
        for (String col : projection) {
            if (OpenableColumns.DISPLAY_NAME.equals(col)) {
                cols[i] = OpenableColumns.DISPLAY_NAME;
                values[i++] = file.getName();
            } else if (OpenableColumns.SIZE.equals(col)) {
                cols[i] = OpenableColumns.SIZE;
                values[i++] = file.length();
            }
        }

        cols = copyOf(cols, i);
        values = copyOf(values, i);

        final MatrixCursor cursor = new MatrixCursor(cols, 1);
        cursor.addRow(values);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        // ContentProvider has already checked granted permissions
        final File file = mStrategy.getFileForUri(uri);

        final int lastDot = file.getName().lastIndexOf('.');
        if (lastDot >= 0) {
            final String extension = file.getName().substring(lastDot + 1);
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (mime != null) {
                return mime;
            }
        }

        return "application/octet-stream";
    }

    /**
     * By default, this method throws an {@link UnsupportedOperationException}. You must
     * subclass FileProvider if you want to provide different functionality.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    /**
     * By default, this method throws an {@link UnsupportedOperationException}. You must
     * subclass FileProvider if you want to provide different functionality.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    @Override
    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {
        // ContentProvider has already checked granted permissions
        final File file = mStrategy.getFileForUri(uri);
        return file.delete() ? 1 : 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        // ContentProvider has already checked granted permissions
        final File file = mStrategy.getFileForUri(uri);
        final int fileMode = modeToMode(mode);
        return ParcelFileDescriptor.open(file, fileMode);
    }

    /**
     * Strategy for mapping between {@link File} and {@link Uri}.
     * <p>
     * Strategies must be symmetric so that mapping a {@link File} to a
     * {@link Uri} and then back to a {@link File} points at the original
     * target.
     * <p>
     * Strategies must remain consistent across app launches, and not rely on
     * dynamic state. This ensures that any generated {@link Uri} can still be
     * resolved if your process is killed and later restarted.
     *
     * @see SimplePathStrategy
     */
    interface PathStrategy {
        /**
         * Return a {@link Uri} that represents the given {@link File}.
         */
        Uri getUriForFile(File file);

        /**
         * Return a {@link File} that represents the given {@link Uri}.
         */
        File getFileForUri(Uri uri);
    }

    /**
     * Strategy that provides access to files living under a narrow whitelist of
     * filesystem roots. It will throw {@link SecurityException} if callers try
     * accessing files outside the configured roots.
     * <p>
     * For example, if configured with
     * {@code addRoot("myfiles", mContext.getFilesDir())}, then
     * {@code mContext.getFileStreamPath("foo.txt")} would map to
     * {@code content://myauthority/myfiles/foo.txt}.
     */
    public static class SimplePathStrategy implements PathStrategy {
        private final String mAuthority;
        private final HashMap<String, File> mRoots = new HashMap<String, File>();

        public SimplePathStrategy(String authority) {
            mAuthority = authority;
        }

        /**
         * Add a mapping from a name to a filesystem root. The provider only offers
         * access to files that live under configured roots.
         */
        public void addRoot(String name, File root) {
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Name must not be empty");
            }

            try {
                // Resolve to canonical path to keep path checking fast
                root = root.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Failed to resolve canonical path for " + root, e);
            }

            mRoots.put(name, root);
        }

        @Override
        public Uri getUriForFile(File file) {
            String path;
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }

            // Find the most-specific root path
            Map.Entry<String, File> mostSpecific = null;
            for (Map.Entry<String, File> root : mRoots.entrySet()) {
                final String rootPath = root.getValue().getPath();
                if (path.startsWith(rootPath) && (mostSpecific == null
                        || rootPath.length() > mostSpecific.getValue().getPath().length())) {
                    mostSpecific = root;
                }
            }

            if (mostSpecific == null) {
                throw new IllegalArgumentException(
                        "Failed to find configured root that contains " + path);
            }

            // Start at first char of path under root
            final String rootPath = mostSpecific.getValue().getPath();
            if (rootPath.endsWith("/")) {
                path = path.substring(rootPath.length());
            } else {
                path = path.substring(rootPath.length() + 1);
            }

            // Encode the tag and path separately
            path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
            return new Uri.Builder().scheme("content")
                    .authority(mAuthority).encodedPath(path).build();
        }

        @Override
        public File getFileForUri(Uri uri) {
            String path = uri.getEncodedPath();

            final int splitIndex = path.indexOf('/', 1);
            final String tag = Uri.decode(path.substring(1, splitIndex));
            path = Uri.decode(path.substring(splitIndex + 1));

            final File root = mRoots.get(tag);
            if (root == null) {
                throw new IllegalArgumentException("Unable to find configured root for " + uri);
            }

            File file = new File(root, path);
            try {
                file = file.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }

            if (!file.getPath().startsWith(root.getPath())) {
                throw new SecurityException("Resolved path jumped beyond configured root");
            }

            return file;
        }
    }

    private static class XmlPathStrategy extends SimplePathStrategy {
        private static final String META_DATA_FILE_PROVIDER_PATHS = "zero.support.FILE_PROVIDER_PATHS";
        private static final String TAG_ROOT_PATH = "root-path";
        private static final String TAG_FILES_PATH = "files-path";
        private static final String TAG_CACHE_PATH = "cache-path";
        private static final String TAG_EXTERNAL = "external-path";
        private static final String TAG_EXTERNAL_FILES = "external-files-path";
        private static final String TAG_EXTERNAL_CACHE = "external-cache-path";
        private static final String TAG_EXTERNAL_MEDIA = "external-media-path";
        private static final String ATTR_NAME = "name";
        private static final String ATTR_PATH = "path";
        private static final File DEVICE_ROOT = new File("/");


        private XmlPathStrategy(String authority) {
            super(authority);
        }

        public static File[] getExternalFilesDirs(Context context, String type) {
            if (Build.VERSION.SDK_INT >= 19) {
                return context.getExternalFilesDirs(type);
            } else {
                return new File[]{context.getExternalFilesDir(type)};
            }
        }

        public static File[] getExternalCacheDirs(Context context) {
            if (Build.VERSION.SDK_INT >= 19) {
                return context.getExternalCacheDirs();
            } else {
                return new File[]{context.getExternalCacheDir()};
            }
        }

        private static PathStrategy parsePathStrategy(Context context, String authority)
                throws IOException, XmlPullParserException {
            final SimplePathStrategy strat = new SimplePathStrategy(authority);

            final ProviderInfo info = context.getPackageManager()
                    .resolveContentProvider(authority, PackageManager.GET_META_DATA);
            final XmlResourceParser in = info.loadXmlMetaData(
                    context.getPackageManager(), META_DATA_FILE_PROVIDER_PATHS);
            if (in == null) {
                throw new IllegalArgumentException(
                        "Missing " + META_DATA_FILE_PROVIDER_PATHS + " meta-data");
            }

            int type;
            while ((type = in.next()) != END_DOCUMENT) {
                if (type == START_TAG) {
                    final String tag = in.getName();

                    final String name = in.getAttributeValue(null, ATTR_NAME);
                    String path = in.getAttributeValue(null, ATTR_PATH);

                    File target = null;
                    if (TAG_ROOT_PATH.equals(tag)) {
                        target = DEVICE_ROOT;
                    } else if (TAG_FILES_PATH.equals(tag)) {
                        target = context.getFilesDir();
                    } else if (TAG_CACHE_PATH.equals(tag)) {
                        target = context.getCacheDir();
                    } else if (TAG_EXTERNAL.equals(tag)) {
                        target = Environment.getExternalStorageDirectory();
                    } else if (TAG_EXTERNAL_FILES.equals(tag)) {
                        File[] externalFilesDirs = getExternalFilesDirs(context, null);
                        if (externalFilesDirs.length > 0) {
                            target = externalFilesDirs[0];
                        }
                    } else if (TAG_EXTERNAL_CACHE.equals(tag)) {
                        File[] externalCacheDirs = getExternalCacheDirs(context);
                        if (externalCacheDirs.length > 0) {
                            target = externalCacheDirs[0];
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                            && TAG_EXTERNAL_MEDIA.equals(tag)) {
                        File[] externalMediaDirs = context.getExternalMediaDirs();
                        if (externalMediaDirs.length > 0) {
                            target = externalMediaDirs[0];
                        }
                    }

                    if (target != null) {
                        strat.addRoot(name, buildPath(target, path));
                    }
                }
            }

            return strat;
        }

        private static File buildPath(File base, String... segments) {
            File cur = base;
            for (String segment : segments) {
                if (segment != null) {
                    cur = new File(cur, segment);
                }
            }
            return cur;
        }

        public static PathStrategy newInstance(Context context, String authority) {

            try {
                return parsePathStrategy(context, authority);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Failed to parse " + META_DATA_FILE_PROVIDER_PATHS + " meta-data", e);
            } catch (Throwable e) {
                //ignore
            }
            return null;
        }
    }

}
