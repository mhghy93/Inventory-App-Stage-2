package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        Button sellButton = (Button) view.findViewById(R.id.sale_btn);
        Button editButton = (Button) view.findViewById(R.id.edit_btn);

        // Find the columns of book attributes that we're interested in
        int productIdIndex = cursor.getColumnIndex(BookEntry._ID);
        int productNameIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int productPriceIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final String productId = cursor.getString(productIdIndex);
        String productName = cursor.getString(productNameIndex);
        String productPrice = cursor.getString(productPriceIndex);
        final String productQuantity = cursor.getString(productQuantityIndex);


        // Update the TextViews with the attributes for the current book
        productNameTextView.setText(productName);
        productPriceTextView.setText(context.getString(R.string.indian_currency) + " " + productPrice);
        productQuantityTextView.setText(context.getText(R.string.product_quantity) + ": " + productQuantity);

        // Create onclick listener on sellButtom
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity activity = (MainActivity) context;
                activity.saleProduct(Integer.valueOf(productId), Integer.valueOf(productQuantity));
            }
        });

        // Create onclick listener on editButtom
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long.parseLong(productId));
                intent.setData(currentBookUri);
                context.startActivity(intent);
            }
        });
    }
}
