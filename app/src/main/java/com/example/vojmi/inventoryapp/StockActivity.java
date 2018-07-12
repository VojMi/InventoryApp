package com.example.vojmi.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vojmi.inventoryapp.data.ProductContract.ProductEntry;

public class StockActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the data loader.
     **/
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        // Find the ListView to be filled with the product data.
        ListView productListView = findViewById(R.id.list);

        // Find and set empty view on the ListView which only shows when the list has no items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Prepare an Adapter to create a list item for each row of the product, all via Cursor.
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Set the listener.
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent.
                Intent intent = new Intent(StockActivity.this, com.example.vojmi.inventoryapp.EditorActivity.class);

                // Declare the content URI that represents the particular product.
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

    }


    /**
     * Helper method to delete all items from database. .
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from product database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "New product" menu option.
            case R.id.new_product:
                Intent intent = new Intent(StockActivity.this, com.example.vojmi.inventoryapp.EditorActivity.class);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete all entries" menu option.
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider method on a background thread.
        // It is generally recommended to use background thread to make slower operations.
        return new CursorLoader(this,   // Parent activity context.
                ProductEntry.CONTENT_URI,   // Provider content URI to query.
                projection,             // Columns to include in the resulting Cursor.
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update Cursor Adapter with this new cursor containing updated product data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This callback is called when any data will be deleted.
        mCursorAdapter.swapCursor(null);
    }

    public void saleProduct(long productId, int quantity) {

        // Decrement of product quantity.
        if (quantity >= 1) {
            quantity--;
            // Construct new uri and content values.
            Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, R.string.sale_ok, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sale_failed, Toast.LENGTH_SHORT).show();
            }

        } else {
            //  Out of stock
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_SHORT).show();
        }
    }

    public void addProduct(long productId, int quantity) {
        // Increment of product quantity.
        quantity++;
        // Construct new uri and content values.
        Uri updateUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        int rowsUpdated = getContentResolver().update(
                updateUri,
                values,
                null,
                null);
        if (rowsUpdated == 1) {
            Toast.makeText(this, R.string.added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.add_failed, Toast.LENGTH_SHORT).show();
        }


    }

}





