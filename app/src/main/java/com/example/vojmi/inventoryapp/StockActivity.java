package com.example.vojmi.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vojmi.inventoryapp.data.ProductContract.ProductEntry;
import com.example.vojmi.inventoryapp.data.ProductDbHelper;

/**
 * Displays list of items in stock, stored in the database.
 */
public class StockActivity extends AppCompatActivity {
    /**
     * Database helper providing access to the database.
     */
    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper.
        // The context is current activity.
        mDbHelper = new ProductDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * The helper method displays the database content via TextView in our layout.
     */
    private void displayDatabaseInfo() {
        // Getting and access to the database.
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Specification of columns to be accessed.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Proceeding the query on products table.
        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_product);

        try {
            // Create a header of database in the Text View.

            displayView.setText(ProductEntry._ID + " | " +
                    ProductEntry.COLUMN_PRODUCT_NAME + " | " +
                    ProductEntry.COLUMN_PRODUCT_PRICE + " | " +
                    ProductEntry.COLUMN_PRODUCT_QUANTITY + " | " +
                    ProductEntry.COLUMN_SUPPLIER_NAME + " | " +
                    ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");


            // Getting the index of each column.
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Move through all the given rows by the cursor.
            while (cursor.moveToNext()) {
                // Getting the value according to given column index.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                int currentSupplier = cursor.getInt(supplierColumnIndex);
                int currentPhone = cursor.getInt(phoneColumnIndex);
                // Display the values from each column of the current row via TextView.
                displayView.append(("\n" + currentID + " | " +
                        currentName + " | " +
                        currentPrice + "$ | " +
                        currentQuantity + " | " +
                        currentSupplier + " | " +
                        currentPhone));
            }
        } finally {
            // Cursor should be closed when not used (probably for resource saving purposes).
            cursor.close();
        }
    }

    /**
     * Method for inserting testing data into database - for testing purposes only.
     */
    private void insertProduct() {
        // Getting access to the database.
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Specification of column names are the appropriate keys,
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Headphones");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 48.8);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 25);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Kossos");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "777112567");

        // Insert specified data.
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu and displays the icon on the app bar.
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Perform action when the item is selected.
        switch (item.getItemId()) {
            // Call method inserting the test data into database.
            case R.id.action_insert_test_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
