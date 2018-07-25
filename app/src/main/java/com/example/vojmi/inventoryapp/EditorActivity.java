package com.example.vojmi.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vojmi.inventoryapp.data.ProductContract;
import com.example.vojmi.inventoryapp.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    /**
     * Content URI for the product.
     */
    private Uri mCurrentProduct;

    /**
     * Name of the product.
     */
    private EditText mNameEditText;

    /**
     * Price of the product.
     */
    private EditText mPriceEditText;

    /**
     * Quantity of the product.
     */
    private EditText mQuantityEditText;

    /**
     * Supplier of the product.
     */
    private EditText mSupplierEditText;

    /**
     * Supplier's phone number.
     */
    private EditText mPhoneEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProduct = intent.getData();

        // Statement to determine whether we are creating new product or not.
        if (mCurrentProduct == null) {
            // Change the title according to current user action.
            setTitle(getString(R.string.title_new_product));

        } else {
            // Change the title according to current user action.
            setTitle(getString(R.string.title_edit_product));
            // Product data loader initialization.
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
        // Get values from all TextViews to variables.
        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_product_price);
        mQuantityEditText = findViewById(R.id.edit_product_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier);
        mPhoneEditText = findViewById(R.id.edit_phone);

        // Setup onClickListener to call supplier when button is clicked.
        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText phoneEditText = findViewById(R.id.edit_phone);
                String phone = phoneEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_phone, Toast.LENGTH_SHORT).show();
                }

            }
        });
        // Setup onClickListener to increase quantity when button is clicked.
        findViewById(R.id.sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQuantity) || currentQuantity == null) {
                    currentQuantity = String.valueOf(0);
                }

                int quantity = Integer.parseInt(currentQuantity);
                // Avoid negative quantity.
                if (quantity == 0) {
                    Toast.makeText(getApplicationContext(), R.string.invalid_quantity, Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    mQuantityEditText.setText(String.valueOf(quantity));
                }

            }
        });

        // Setup onClickListener to decrease quantity when button is clicked.
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQuantity) || currentQuantity == null) {
                    currentQuantity = String.valueOf(0);
                }
                int quantity = Integer.parseInt(currentQuantity);
                quantity++;
                mQuantityEditText.setText(String.valueOf(quantity));

            }
        });
    }


    /**
     * Get the values from EditTexts and put them into database.
     */
    private boolean saveProduct() {
        // Declare and set default values. 
        String name = "";
        double price;
        int quantity;
        String supplier = "";
        long phone;

        try {
            name = mNameEditText.getText().toString().trim();
            price = Double.parseDouble(mPriceEditText.getText().toString().trim());
            quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            supplier = mSupplierEditText.getText().toString().trim();
            phone = Long.parseLong(mPhoneEditText.getText().toString().trim());

        } catch (NumberFormatException e) {
            return false;
        }
        // Sanity check of empty fields and invalid values.
        try {

            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_product_name));
            }

            if (price <= 0) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_price));
            }

            if (quantity < 0) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_quantity));
            }

            if (TextUtils.isEmpty(supplier)) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_supplier));
            }

            if (phone <= 100000000) {
                throw new IllegalArgumentException(this.getResources().getString(R.string.invalid_phone_number));
            }
        } catch (IllegalArgumentException e) {
            // If any of fields is empty, do nothing.
            return false;
        }
        // Get writable database.
        ProductDbHelper dbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Values preparation before insertion.
        ContentValues values = new ContentValues();
        values.put("[" + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + "]", name);
        values.put("[" + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + "]", price);
        values.put("[" + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + "]", quantity);
        values.put("[" + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + "]", supplier);
        values.put("[" + ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "]", phone);


        Long rowId;
        int rowsUpdated;
        String toastText;

        if (mCurrentProduct == null) {

            // Insert Product into database.
            mCurrentProduct = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            rowId = ContentUris.parseId(mCurrentProduct);

            // Display toast after inserting record to database.
            if (rowId == -1) {
                toastText = getResources().getString(R.string.saving_error);
            } else {
                toastText = getResources().getString(R.string.saved_info);
            }


        } else {
            rowsUpdated = getContentResolver().update(
                    mCurrentProduct,
                    values,
                    null,
                    null
            );

            if (rowsUpdated != 1) {
                toastText = getResources().getString(R.string.saving_error);
            } else {
                toastText = getResources().getString(R.string.saved_info);
            }

        }

        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

        db.close();

        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProduct == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app  menu.
        switch (item.getItemId()) {
            // Respond to a click on the menu option.
            case R.id.action_save:
                // Save product to database.
                if (saveProduct()) {
                    // Exit activity if true is not returned.
                    finish();
                } else {
                    Toast.makeText(this, R.string.invalid_data_on_save, Toast.LENGTH_SHORT).show();
                }
                return true;
            // Respond to a click on the menu option.
            case R.id.action_delete:
                // Pop up confirmation dialog.
                showDeleteConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a columnsIncluded that contains
        // all columns from the table.
        String[] columnsIncluded = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };

        // This loader will execute the ContentProvider method on a background thread.
        // It is generally recommended to use background thread to make slower operations.
        return new CursorLoader(this,   // Activity context.
                mCurrentProduct,         // Query the content URI for the product.
                columnsIncluded,             // Columns to include in the resulting Cursor.
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Iff the cursor is null or there is less than one row in the cursor, do not continue.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // ProMove to the first row of the cursor and read data from it.
        if (cursor.moveToFirst()) {
            // Get the column indices of product.
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            // Extract out the value from the Cursor for the given column index.
            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            Long phone = cursor.getLong(phoneColumnIndex);

            // Update the views on the screen with the values from the database.
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(String.valueOf(phone));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is not valid, clear out all the data.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }

    /**
     * Show a dialog asking the user whether he/she really wants to not save data.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the dialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Let the user choose the option.
     */
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Delete product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Cancel editing of product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the dialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProduct != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProduct
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProduct, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Exit the activity.
        finish();

    }
}
