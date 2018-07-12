package com.example.vojmi.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
/**
 * API Contract for the Inventory App.
 */
public final class ProductContract {
    /**
     * To prevent someone from accidentally instantiating the contract class.
     * It is necessary give it an empty constructor.
     */
    private ProductContract() {
    }

    /**
     * Definition of the "Content authority" which is basically the entire content provider.
     * The name is represented by string, the structure is unique for the app across all devices.
     */
    public static final String CONTENT_AUTHORITY = "com.example.vojmi.inventoryapp";

    /**
     * The base of all URIs created via CONTENT_AUTHORITY for the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Definition of the possible path. In this case the folder 'products' is allowed.
     */
    public static final String PATH_PRODUCTS = "products";

    /**
     * Class defining the constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * The content URI allows access to the products data through provider.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the CONTENT_URI for a list of products.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the CONTENT_URI for the particular product. .
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Name of database table for the products.
         */
        public final static String TABLE_NAME = "products";
        /**
         * Unique ID number for the product.
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;
        /**
         * Name of the product.
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "Product";

        /**
         * Price of the product.
         * Type: DOUBLE
         */
        public final static String COLUMN_PRODUCT_PRICE = "Price";

        /**
         * Quantity of the product.
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";

        /**
         * Supplier name.
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "Supplier";

        /**
         * Supplier phone number.
         * Type: LONG
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "Phone";
    }
}

