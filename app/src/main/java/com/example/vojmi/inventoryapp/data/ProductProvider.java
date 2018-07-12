package com.example.vojmi.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.vojmi.inventoryapp.R;
import com.example.vojmi.inventoryapp.data.ProductContract.ProductEntry;


public class ProductProvider extends ContentProvider {
    /**
     * Tag for the log messages.
     */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    /*
     * The code for URI matcher, products table.
     * */
    private static final int PRODUCTS = 1;
    /*
     * The code for URI matcher, single product.
     * */
    private static final int PRODUCT_ID = 2;
    /*
     * The UriMatcher object declaration. This allows match between content URI and corresponding code.
     * The code comes via the constructor input. This code is returned for the root URI.
     * In this case, NO_MATCH is common to use as an input.
     * */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // URI paterns which are recognised by the provider. This URI will provide access to multiple rows from the table.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        // URI paterns which are recognised by the provider. This URI will provide access to single row from the table.
        // The # symbol is used as a substitution for the integer in the pattern. In case the path will not end by integer, there will be no match.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable access to the database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        // Initialisation of the UriMatcher.
        int match = sUriMatcher.match(uri);
        // Switch statement performing the matching of codes.
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // The question mark is a substitution for the argument. This is the protection against SQL injection.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // The query action on the particular row according to cursor position.
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.invalid_uri) + uri);
        }
        // The notification is set for the case the data are updated.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Every content provider must return the content type for its supported URIs.
        // The signature of the method takes a URI and returns a String.
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                return null;

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS: // Meets our insert statement, as it operates without item id
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.invalid_uri) + uri);

        }
    }

    /**
     * The product info input to the database with the particular values. The specific URI is returned.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Sanity checks:
        // Check whether the name is null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        // If the price is input, check that it's  value is greater than or equal to 0.
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires the correct price");
        }
        // If the quantity is input, check that it's  value is greater than or equal to 0.
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires the correct quantity");
        }
        // Check that the supplier name is valid.
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String suplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(suplier)) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }
        // Check whether the phone number is valid (9 digits are expected).
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(phone) || Integer.valueOf(phone) < 100000000) {
                throw new IllegalArgumentException("Product requires a supplier's phone");
            }
        }
        // Get writable access to the database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with specified values.
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion was not successful. Log an exception and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for URI: " + uri);
            return null;
        }
        // Notify all listeners that the data has changed.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the appended ID.
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                /**
                 * Delete rows from the database according to the particular values specified
                 * in the selection arguments.
                 * The return value represents the number of rows returned.
                 */
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI.
                // The question mark is a substitution for the argument. This is the protection against SQL injection.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // In case any rows were deleted then listeners that the data at the given URI has changed. 
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Notify all listeners that the data has changed.
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        // Switch statement performing the matching of codes.
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // The question mark is a substitution for the argument. This is the protection against SQL injection.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * The product info update in the database with the particular values specified
     * in the selection and selection arguments.
     * The return value represents the number of rows returned.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Sanity checks:
        // Check whether the name is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        // If the price is input, check that it's  value is greater than or equal to 0.
        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires the correct price");
        }
        // If the quantity is input, check that it's  value is greater than or equal to 0.
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires the correct quantity");
        }
        // Check that the supplier name is valid.
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String suplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(suplier)) {
                throw new IllegalArgumentException("Product requires a supplier");
            }
        }
        // Check whether the phone number is valid (9 digits are expected).
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String phone = values.getAsString(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(phone) || Integer.valueOf(phone) < 100000000) {
                throw new IllegalArgumentException("Product requires a supplier's phone");
            }
        }
        // If there are no values to update, then don't update anything.
        if (values.size() == 0) {
            return 0;
        }
        // Get writable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Update the database and get the number of updated rows.
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // Notify all listeners that the data has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Get writable database.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowCount = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        //  Notify all listeners about the data change on given URI.
        if (rowCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows that were affected.
        return rowCount;
    }


}



