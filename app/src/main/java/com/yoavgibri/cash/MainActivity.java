package com.yoavgibri.cash;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yoavgibri.cash.DataBase.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnExpenseClickListener {


    public static final String CATEGORIES = "categories";
    private FloatingActionButton mFab;
    private DBHelper helper = new DBHelper(this);
    private MainActivityFragment mMainFragment;
    private Dialog mDialog;
    private List<String> mCategoriesList;
    private Spinner mSpinnerWhat;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMainFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        startPreferencesIfNotExist();
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogNewExpense();
                mSpinnerWhat.performClick();
            }
        });
    }

    private void startPreferencesIfNotExist() {
        SharedPreferences sharedPreferences = getSharedPreferences(CATEGORIES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!sharedPreferences.contains("1")) {
            editor.putString("1", getString(R.string.def_cat_groceries));
            editor.putString("2", getString(R.string.def_cat_eating_out));
            editor.putString("3", getString(R.string.def_cat_gas));
            editor.putString("4", getString(R.string.def_cat_public_transportation));
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_settings:
//                return true;
            case R.id.action_clear_all_expenses:
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.clear_all_expenses))
                        .setPositiveButton(getString(R.string.clear_it), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.clearAllExpenses();
                                mMainFragment.updateRecycler();
                                mMainFragment.updateTotalExpenses();
                                Snackbar.make(mFab, R.string.all_expenses_cleared, Snackbar.LENGTH_INDEFINITE)
//                                        .setAction("UNDO", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                mMainFragment.undoClear();
//                                            }
//                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton(getString(R.string.dont_do_it_man), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                return true;
//            case R.id.action_dummy_content:
//                mMainFragment.loadDummyContent();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogNewExpense() {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_add_expense);

//        TextView title = (TextView) mDialog.findViewById(R.id.textViewDialogTitle);
//        title.setText(R.string.dialog_title_new_expense);
        mSpinnerWhat = (Spinner) mDialog.findViewById(R.id.spinnerWhat);
        final EditText howMuch = (EditText) mDialog.findViewById(R.id.editTextAmount);
        final EditText what = (EditText) mDialog.findViewById(R.id.editTextWhat);
        final EditText where = (EditText) mDialog.findViewById(R.id.editTextWhere);
        final EditText comment = (EditText) mDialog.findViewById(R.id.editTextComment);
        final Button ok = (Button) mDialog.findViewById(R.id.buttonDialogOk);
        Button dismiss = (Button) mDialog.findViewById(R.id.buttonDialogCancel);
        setSpinnerAdapter(mSpinnerWhat);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (what.getText().toString().equals("") && howMuch.getText().toString().equals("")){
                Boolean isOther = what.getVisibility() == View.VISIBLE;

                if (isOther) {
                    if (what.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, R.string.Toast_enter_expense_kind, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (mSpinnerWhat.getPrompt().equals(getString(R.string.pick_a_category))) {
                    Toast.makeText(MainActivity.this, R.string.Toast_choose_category, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (howMuch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.Toast_enter_amount, Toast.LENGTH_SHORT).show();
                } else {
                    String name = null;
                    if (isOther) {
                        name = what.getText().toString();
                        updateCategoriesSP(name);
                    } else {
                        name = mSpinnerWhat.getPrompt().toString();
                    }
                    String place = where.getText().toString();
                    String comments = comment.getText().toString();
                    int amount = Integer.valueOf(howMuch.getText().toString());
                    long time = Calendar.getInstance().getTimeInMillis();

                    Expense newExpense = new Expense(name, place, comments, time, amount);
                    DBHelper mDbHelper = new DBHelper(MainActivity.this);

                    mDbHelper.insertExpense(newExpense);
                    mDialog.dismiss();

                    //refresh the Total Expenses TextView:
                    mMainFragment.updateTotalExpenses();
                    mMainFragment.updateRecycler();

                    Snackbar.make(mFab, R.string.snackBar_new_expense_added, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, null).show();
                }
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    ok.performClick();
                }
                return false;
            }
        });
        mDialog.show();
    }

    private void updateCategoriesSP(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(CATEGORIES, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        mCategoriesList.add(0, name);
        mCategoriesList.remove(getString(R.string.otherCategory));
        mCategoriesList.remove(getString(R.string.pick_a_category));
        mCategoriesList.add(name);

        int i = 1;
        for (String cat : mCategoriesList) {
            editor.putString(String.valueOf(i), cat);
            i++;
        }
        editor.apply();
    }

    private void setSpinnerAdapter(final Spinner spinnerWhat) {
        getCategoriesList();

        SpinnerAdapter adapter = new SpinnerAdapter(MainActivity.this, R.layout.item_categories, mCategoriesList);
        spinnerWhat.setAdapter(adapter);
        spinnerWhat.setSelection(adapter.getCount());
        spinnerWhat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(getString(R.string.otherCategory))) {
                    EditText what = (EditText) mDialog.findViewById(R.id.editTextWhat);
                    what.setVisibility(View.VISIBLE);
                    spinnerWhat.setVisibility(View.GONE);
                }
                spinnerWhat.setPrompt(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCategoriesList() {
        mCategoriesList = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences(CATEGORIES, 0);
        int i = 1;
        String category;
        do {
            category = sharedPreferences.getString(String.valueOf(i), "");
            if (!category.equals("")){
                mCategoriesList.add(category);
            }
            i++;
        } while (!category.equals(""));
        mCategoriesList.add(getString(R.string.otherCategory));
        mCategoriesList.add(getString(R.string.pick_a_category));
    }


    public void showDialogEditExpense(final Expense expense) {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_add_expense);
//        TextView title = (TextView) dialog.findViewById(R.id.textViewDialogTitle);
//        title.setText(R.string.dialog_title_edit_expense);

        final EditText howMuch = (EditText) mDialog.findViewById(R.id.editTextAmount);
        final EditText what = (EditText) mDialog.findViewById(R.id.editTextWhat);
        final Spinner spinnerWhat = (Spinner) mDialog.findViewById(R.id.spinnerWhat);
        final EditText where = (EditText) mDialog.findViewById(R.id.editTextWhere);
        final EditText comment = (EditText) mDialog.findViewById(R.id.editTextComment);
        Button ok = (Button) mDialog.findViewById(R.id.buttonDialogOk);
        Button dismiss = (Button) mDialog.findViewById(R.id.buttonDialogCancel);
        setSpinnerAdapter(spinnerWhat);

        for (int i = 0; i < mCategoriesList.size(); i++) {
            if (mCategoriesList.get(i).equals(expense.getName())) {
                spinnerWhat.setSelection(i);
                break;
            }
        }
        howMuch.setText(String.valueOf(expense.getAmount()));
//        what.setText(expense.getName());
        where.setText(expense.getPlace());
        comment.setText(expense.getComment());

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (howMuch.getText().toString().equals("")){
//                    Toast.makeText(MainActivity.this, "Please enter a name and an amount...", Toast.LENGTH_SHORT).show();
//                }else {
////                    String name = what.getText().toString();
//                    String place = where.getText().toString();
//                    String comments = comment.getText().toString();
//                    int amount = Integer.valueOf(howMuch.getText().toString());
////                    long time = Calendar.getInstance().getTimeInMillis();
//                    long time = expense.getTime();
//
//                    Expense editExpense = new Expense(expense.getId(),"name", place, comments, time, amount);
//                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                Boolean isOther = what.getVisibility() == View.VISIBLE;

                if (isOther) {
                    if (what.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, getString(R.string.Toast_enter_expense_kind), Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (spinnerWhat.getPrompt().equals(getString(R.string.pick_a_category))) {
                    Toast.makeText(MainActivity.this, getString(R.string.Toast_choose_category), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (howMuch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, getString(R.string.Toast_enter_amount), Toast.LENGTH_SHORT).show();
                } else {
                    long uid = expense.getId();
                    String name = null;
                    if (isOther) {
                        name = what.getText().toString();
                        updateCategoriesSP(name);
                    } else {
                        name = spinnerWhat.getPrompt().toString();
                    }
                    String place = where.getText().toString();
                    String comments = comment.getText().toString();
                    int amount = Integer.valueOf(howMuch.getText().toString());
                    long time = Calendar.getInstance().getTimeInMillis();
                    Expense editExpense = new Expense(uid, name, place, comments, time, amount);
                    DBHelper mDbHelper = new DBHelper(MainActivity.this);

                    mDbHelper.updateExpense(editExpense);
                    mDialog.dismiss();

                    //refresh the Total Expenses TextView:
                    mMainFragment.updateTotalExpenses();
                    mMainFragment.updateRecycler();

                    Snackbar.make(mFab, R.string.snackBar_expense_updated, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, null).show();
                }
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();

    }

    @Override
    public void OnExpenseClick(long id) {
        showDialogEditExpense(helper.getExpense(id));
    }

    @Override
    protected void onPause() {
        if (mDialog!=null){
            mDialog.dismiss();
        }
        super.onPause();
    }
}
