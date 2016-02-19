package com.yoavgibri.cash;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  showDialogNewExpense();

//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialogNewExpense(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_expense);
        dialog.setTitle("New Expense");

        EditText amount = (EditText) dialog.findViewById(R.id.editTextAmount);
        final EditText what = (EditText) dialog.findViewById(R.id.editTextWhat);
        final EditText where = (EditText) dialog.findViewById(R.id.editTextWhere);
        Button ok = (Button) dialog.findViewById(R.id.buttonDialogOk);
        Button dismiss = (Button) dialog.findViewById(R.id.buttonDialogCancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = what.getText().toString();
                String place = where.getText().toString();
                long time = Calendar.getInstance().getTimeInMillis();

//                Expense newExpense = new Expense(name, place, null, time);
//                dbHelper.insertExpense(newExpense);
                Toast.makeText(MainActivity.this, "Ha", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
}
