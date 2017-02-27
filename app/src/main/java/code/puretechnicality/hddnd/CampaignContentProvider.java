package code.puretechnicality.hddnd;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class CampaignContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "code.puretechnicality.hddnd.CampaignProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/campaigns";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String ID = "_id";
    static final String NAME = "name";
    static final String DESCRIPTION = "description";

    private static Map<String, String> CAMPAIGNS_PROJECTION_MAP;

    static final int CAMPAIGNS = 1;
    static final int CAMPAIGN_ID = 2;

    static final UriMatcher uriMatcher;

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "hddnd";
    static final String CAMPAIGNS_TABLE_NAME = "campaigns";
    static final int DATABASE_VERSION = 1;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "campaigns", CAMPAIGNS);
        uriMatcher.addURI(PROVIDER_NAME, "campaigns/#", CAMPAIGN_ID);

        CAMPAIGNS_PROJECTION_MAP = new HashMap<>();
        CAMPAIGNS_PROJECTION_MAP.put(ID, ID);
        CAMPAIGNS_PROJECTION_MAP.put(NAME, NAME);
        CAMPAIGNS_PROJECTION_MAP.put(DESCRIPTION, DESCRIPTION);
    }

    /**
     * Helper class that actually creates and manages the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CAMPAIGNS_TABLE_NAME +
                       " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                       " name TEXT NOT NULL, " +
                       " description TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CAMPAIGNS_TABLE_NAME);
            onCreate(db);
        }
    }

    public CampaignContentProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Create or open a writable copy of the database
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Add a new record to the table
        long rowID = db.insert(CAMPAIGNS_TABLE_NAME, "", values);

        // Was the record added successfully?
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(CAMPAIGNS_TABLE_NAME);

            switch (uriMatcher.match(uri)) {
                case CAMPAIGNS:
                    qb.setProjectionMap(CAMPAIGNS_PROJECTION_MAP);
                    break;

                case CAMPAIGN_ID:
                    qb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
                    break;

                default:
            }

            // If no sort order is specified, sort on name, by default
            if (sortOrder == null || sortOrder.isEmpty()){
                sortOrder = NAME;
            }

            // Execute the query
            Cursor c = qb.query(db,	projection,	selection, selectionArgs, null, null, sortOrder);

            // Notify the caller when the data changes
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)){
            case CAMPAIGNS:
                count = db.delete(CAMPAIGNS_TABLE_NAME, selection, selectionArgs);
                break;

            case CAMPAIGN_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(CAMPAIGNS_TABLE_NAME, ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case CAMPAIGNS:
                count = db.update(CAMPAIGNS_TABLE_NAME, values, selection, selectionArgs);
                break;

            case CAMPAIGN_ID:
                count = db.update(CAMPAIGNS_TABLE_NAME, values,
                        ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            // Get all campaign records
            case CAMPAIGNS:
                return "vnd.android.cursor.dir/vnd.hddnd.campaigns";

            // Get a particular campaign
            case CAMPAIGN_ID:
                return "vnd.android.cursor.item/vnd.hddnd.campaigns";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
