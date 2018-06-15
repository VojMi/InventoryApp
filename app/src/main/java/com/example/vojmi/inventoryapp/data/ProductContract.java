package com.example.vojmi.inventoryapp.data;

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
     * Class defining the constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {
        /**
         * Name of database table for the products.
         */
        public final static String TABLE_NAME = "products";
        /**
         * Unique ID number for the product.
         * Type: INTEGER
         */
        public final static String _ID = "ID";
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

