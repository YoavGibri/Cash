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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private SharedPreferences mSharedPreferences;
    private Dialog mDialog;
    private Set<String> mCategoriesSet;
    private List<String> mCategoriesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
//                  showDialogNewExpense();
            }
        });
    }

    private void startPreferencesIfNotExist() {
        mSharedPreferences = getSharedPreferences("Categories", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.contains(CATEGORIES)) {
            Set<String> set = new HashSet<>();
            set.add("מצרכים");
            set.add("אוכל ושתיה בחוץ");
            set.add("דלק");
            set.add("תחבורה ציבורית");
            editor.putStringSet(CATEGORIES, set);
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
                        .setTitle("Clear all expenses?")
                        .setPositiveButton("Clear it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.clearAllExpenses();
                                mMainFragment.updateRecycler();
                                mMainFragment.updateTotalExpenses();
                                Snackbar.make(mFab, "all expenses had been cleared", Snackbar.LENGTH_INDEFINITE)
//                                        .setAction("UNDO", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                mMainFragment.undoClear();
//                                            }
//                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("Don't do it man!", new DialogInterface.OnClickListener() {
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

        final EditText howMuch = (EditText) mDialog.findViewById(R.id.editTextAmount);
        final EditText what = (EditText) mDialog.findViewById(R.id.editTextWhat);
        final Spinner spinnerWhat = (Spinner) mDialog.findViewById(R.id.spinnerWhat);
        final EditText where = (EditText) mDialog.findViewById(R.id.editTextWhere);
        final EditText comment = (EditText) mDialog.findViewById(R.id.editTextComment);
        Button ok = (Button) mDialog.findViewById(R.id.buttonDialogOk);
        Button dismiss = (Button) mDialog.findViewById(R.id.buttonDialogCancel);
        setSpinnerAdapter(spinnerWhat);
        mDialog.setTitle("New Expense");
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (what.getText().toString().equals("") && howMuch.getText().toString().equals("")){
                Boolean isOther = what.getVisibility() == View.VISIBLE;

                if (isOther) {
                    if (what.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter the expense's kind...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (spinnerWhat.getPrompt().equals(getString(R.string.pick_a_category))) {
                    Toast.makeText(MainActivity.this, "Please choose a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (howMuch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter an amount...", Toast.LENGTH_SHORT).show();
                } else {
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

                    Expense newExpense = new Expense(name, place, comments, time, amount);
                    DBHelper mDbHelper = new DBHelper(MainActivity.this);

                    mDbHelper.insertExpense(newExpense);
                    mDialog.dismiss();

                    //refresh the Total Expenses TextView:
                    mMainFragment.updateTotalExpenses();
                    mMainFragment.updateRecycler();

                    Snackbar.make(mFab, "A new Expense has been added!", Snackbar.LENGTH_LONG)
                            .setAction("Undo", null).show();
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

    private void updateCategoriesSP(String name) {
        mSharedPreferences = getSharedPreferences("Categories", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        mCategoriesSet.add(name);
        editor.putStringSet(CATEGORIES, mCategoriesSet);
        editor.apply();
    }

    private void setSpinnerAdapter(final Spinner spinnerWhat) {
        mCategoriesSet = mSharedPreferences.getStringSet(CATEGORIES, null);
        if (mCategoriesSet != null) {
            mCategoriesList = new ArrayList(mCategoriesSet);
            mCategoriesList.add(getString(R.string.otherCategory));
            mCategoriesList.add(getString(R.string.pick_a_category));
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
            SpinnerAdapter adapter = new SpinnerAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, mCategoriesList);
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
    }

    public void showDialogEditExpense(final Expense expense) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_expense);

        final EditText howMuch = (EditText) dialog.findViewById(R.id.editTextAmount);
        final EditText what = (EditText) dialog.findViewById(R.id.editTextWhat);
        final Spinner spinnerWhat = (Spinner) dialog.findViewById(R.id.spinnerWhat);
        final EditText where = (EditText) dialog.findViewById(R.id.editTextWhere);
        final EditText comment = (EditText) dialog.findViewById(R.id.editTextComment);
        Button ok = (Button) dialog.findViewById(R.id.buttonDialogOk);
        Button dismiss = (Button) dialog.findViewById(R.id.buttonDialogCancel);
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

        dialog.setTitle("Edit Expense");
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
                        Toast.makeText(MainActivity.this, "Please enter the expense's kind...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else if (spinnerWhat.getPrompt().equals(getString(R.string.pick_a_category))) {
                    Toast.makeText(MainActivity.this, "Please choose a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (howMuch.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter an amount...", Toast.LENGTH_SHORT).show();
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
                    dialog.dismiss();

                    //refresh the Total Expenses TextView:
                    mMainFragment.updateTotalExpenses();
                    mMainFragment.updateRecycler();

                    Snackbar.make(mFab, "A new Expense has been added!", Snackbar.LENGTH_LONG)
                            .setAction("Undo", null).show();
                }
            }
        });
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void OnExpenseClick(long id) {
        showDialogEditExpense(helper.getExpense(id));
    }
}
