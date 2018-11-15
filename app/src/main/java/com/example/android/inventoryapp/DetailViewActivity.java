package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

public class DetailViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book
     */
    private Uri mCurrentBookUri;

    /**
     * TextView that displays the product's name
     */
    private TextView mProductNameText;

    /**
     * TextView that displays the product's price
     */
    private TextView mPriceText;

    /**
     * TextView that displays the product's quantity
     */
    private TextView mQuantityText;

    /**
     * TextView that displays the product's supplier name
     */
    private TextView mSupplierNameText;

    /**
     * TextView that displays the product's supplier phone number
     */
    private TextView mSupplierPhoneNumberText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        // Find all relevant views that will display the product details
        mProductNameText = findViewById(R.id.product_name_details);
        mPriceText = findViewById(R.id.product_price_details);
        mQuantityText = findViewById(R.id.product_quantity_details);
        mSupplierNameText = findViewById(R.id.supplier_name_details);
        mSupplierPhoneNumberText = findViewById(R.id.supplier_phone_number_details);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLER_PHONE_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            final int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColumnIndex);
            final double productPrice = cursor.getDouble(productPriceColumnIndex);
            final int productQuantity = cursor.getInt(productQuantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameText.setText(productName);
            mPriceText.setText(Double.toString(productPrice));
            mQuantityText.setText(Integer.toString(productQuantity));
            mSupplierNameText.setText(supplierName);
            mSupplierPhoneNumberText.setText(supplierPhoneNumber);

            // Finds the button that decreases the product quantity by one
            Button decreaseButton = (Button) findViewById(R.id.decrease_btn);
            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseProductQuantity(idColumnIndex, productQuantity);
                }
            });

            // Finds the button that increases the product quantity by one
            Button increaseButton = (Button) findViewById(R.id.increase_btn);
            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseProductQuantity(idColumnIndex, productQuantity);
                }
            });

            //Finds the button that deletes the product
            Button deleteButton = (Button) findViewById(R.id.delete_btn);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            //Finds the button that orders(allows user to contact the supplier via an intent to a phone app using the Supplier Phone Number) the product
            Button orderButton = (Button) findViewById(R.id.order_btn);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callSupplier(supplierPhoneNumber);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameText.setText("");
        mPriceText.setText("");
        mQuantityText.setText("");
        mSupplierNameText.setText("");
        mSupplierPhoneNumberText.setText("");
    }

    // This method decreases the product quantity by one
    public void decreaseProductQuantity(int productId, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            updateProductQuantity(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.quantity_finish_msg), Toast.LENGTH_SHORT).show();
        }
    }

    // This method increases the product quantity by one
    public void increaseProductQuantity(int productId, int productQuantity) {
        productQuantity = productQuantity + 1;
        if (productQuantity >= 0) {
            updateProductQuantity(productQuantity);
            Toast.makeText(this, getString(R.string.quantity_change_msg), Toast.LENGTH_SHORT).show();
        }
    }

    // This method allows user to contact the supplier via an intent to a phone app using the Supplier Phone Number
    public void callSupplier(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(intent);
    }

    private void updateProductQuantity(int productQuantity) {

        if (mCurrentBookUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.update_quantity_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_quantity_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_quantity_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_quantity_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
