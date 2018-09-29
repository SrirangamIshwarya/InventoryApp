package com.exampleone.s.inventoryapp;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.exampleone.s.inventoryapp.database.ProductContract.ProductEntry;

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri pCurrentProductUri;
    private EditText pProductNameEditText;
    private EditText pProductPriceEditText;
    private EditText pProductQuantityEditText;
    private Spinner pProductSupplieNameSpinner;
    private EditText pProductSupplierPhoneNumberEditText;
    private int pSupplieName = ProductEntry.SUPPLIER_UNKNOWN;
    private boolean pProductHasChanged = false;
    private View.OnTouchListener pTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            pProductHasChanged = true;
            Log.d("message", "onTouch");
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        Log.d("message", "onCreate");
        Intent intent = getIntent();
        pCurrentProductUri = intent.getData();
        if (pCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        pProductNameEditText = findViewById(R.id.product_name_edit_text);
        pProductPriceEditText = findViewById(R.id.product_price_edit_text);
        pProductQuantityEditText = findViewById(R.id.product_quantity_edit_text);
        pProductSupplieNameSpinner = findViewById(R.id.product_supplier_name_spinner);
        pProductSupplierPhoneNumberEditText = findViewById(R.id.product_supplier_phone_number_edit_text);
        pProductNameEditText.setOnTouchListener(pTouchListener);
        pProductPriceEditText.setOnTouchListener(pTouchListener);
        pProductQuantityEditText.setOnTouchListener(pTouchListener);
        pProductSupplieNameSpinner.setOnTouchListener(pTouchListener);
        pProductSupplierPhoneNumberEditText.setOnTouchListener(pTouchListener);
        setupSpinner();
    }
    private void setupSpinner() {
        ArrayAdapter productSupplieNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        productSupplieNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        pProductSupplieNameSpinner.setAdapter(productSupplieNameSpinnerAdapter);
        pProductSupplieNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_amazon))) {
                        pSupplieName = ProductEntry.SUPPLIER_AMAZON;
                    } else if (selection.equals(getString(R.string.supplier_paytm))) {
                        pSupplieName = ProductEntry.SUPPLIER_PAYTM;
                    } else if (selection.equals(getString(R.string.supplier_myntra))) {
                        pSupplieName = ProductEntry.SUPPLIER_MYNTRA;
                    } else {
                        pSupplieName = ProductEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pSupplieName = ProductEntry.SUPPLIER_UNKNOWN;
            }
        });
    }
    private void saveProduct() {
        String productNameString = pProductNameEditText.getText().toString().trim();
        String productPriceString = pProductPriceEditText.getText().toString().trim();
        String productQuantityString = pProductQuantityEditText.getText().toString().trim();
        String productSupplierPhoneNumberString = pProductSupplierPhoneNumberEditText.getText().toString().trim();
        if (pCurrentProductUri == null) {
            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.product_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (pSupplieName == ProductEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPriceString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, pSupplieName);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.product_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.price_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.quantity_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (pSupplieName == ProductEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, getString(R.string.supplier_name_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productSupplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.supplier_phone_requires), Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPriceString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, pSupplieName);
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, productSupplierPhoneNumberString);
            int rowsAffected = getContentResolver().update(pCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        Log.d("message", "open Editor Activity");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case android.R.id.home:
                if (!pProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(InsertActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(InsertActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (!pProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };
        return new CursorLoader(this,
                pCurrentProductUri,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentSupplierName = cursor.getInt(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);
            pProductNameEditText.setText(currentName);
            pProductPriceEditText.setText(Integer.toString(currentPrice));
            pProductQuantityEditText.setText(Integer.toString(currentQuantity));
            pProductSupplierPhoneNumberEditText.setText(Integer.toString(currentSupplierPhone));
            switch (currentSupplierName) {
                case ProductEntry.SUPPLIER_AMAZON:
                    pProductSupplieNameSpinner.setSelection(1);
                    break;
                case ProductEntry.SUPPLIER_PAYTM:
                    pProductSupplieNameSpinner.setSelection(2);
                    break;
                case ProductEntry.SUPPLIER_MYNTRA:
                    pProductSupplieNameSpinner.setSelection(3);
                    break;
                default:
                    pProductSupplieNameSpinner.setSelection(0);
                    break;
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        pProductNameEditText.setText("");
        pProductPriceEditText.setText("");
        pProductQuantityEditText.setText("");
        pProductSupplierPhoneNumberEditText.setText("");
        pProductSupplieNameSpinner.setSelection(0);
    }
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}