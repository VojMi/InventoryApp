package com.example.vojmi.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.vojmi.inventoryapp.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml.
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views to be modified in the list item layout
        TextView infoTextView = view.findViewById(R.id.info);
        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        // Update the TextViews with the attributes for the current product.
        infoTextView.setText("Product: " + "\nPrice: " + "\nQuantity:");
        TextView valuesTextView = view.findViewById(R.id.values);
        valuesTextView.setText(productName + "\n" + productPrice + " $\n" + productQuantity + " pcs");
        // These will be used in saleProduct method. They're final, as we are calling.
        // MainActivity class directly.
        final long id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        final int quantity = Integer.parseInt(productQuantity);
        // Handle saleProduct button click.
        view.findViewById(R.id.sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StockActivity stockActivity = (StockActivity) context;
                stockActivity.saleProduct(id, quantity);
            }
        });
        // Handle add button click.
        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StockActivity stockActivity = (StockActivity) context;
                stockActivity.addProduct(id, quantity);
            }
        });

    }
}
