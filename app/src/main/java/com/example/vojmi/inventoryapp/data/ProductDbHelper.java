package com.example.vojmi.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for Inventory App which manages database creation and version management.
 */
public class ProductDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "stock.db";

    /**
     * Database version. Every time the database structure is changed, the number must increase.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Construction of a new instance of ProductDbHelper method.
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called for the creation of the database file.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creation of string containing the SQL statement to create the products table.
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE "
                + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " DOUBLE NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " LONG NOT NULL);";

        // Execution of the SQL statement.
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    /**
     * This method is called when the database is getting upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}