package com.yoavgibri.cash;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yoavgibri.cash.DataBase.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivityFragment extends Fragment implements View.OnClickListener {

    private OnExpenseClickListener mListener;
    private TextView mTextViewTotalExpenses;
    private Button mButtonShowExpenses;
    private RecyclerView mRecyclerView;
    private ExpensesAdapter mAdapter;
    private DBHelper helper;


    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExpenseClickListener) {
            mListener = (OnExpenseClickListener) context;
        } else throw new RuntimeException(context.toString()
                + " must implement OnStoryClickListener");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        helper = new DBHelper(getContext());


        mTextViewTotalExpenses = (TextView) v.findViewById(R.id.textViewExpenses);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewExpenses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setRecyclerItemTouch();
        updateRecycler();
        updateTotalExpenses();
        return v;
    }

    private void setRecyclerItemTouch() {
        final ItemTouchHelper.SimpleCallback touchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                Expense expense = mAdapter.expenses.get(position);
                final long uId = expense.getId();
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Delete this expense?")
                        .setPositiveButton("Delete it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.remove(position);
                                helper.removeExpense(uId);
                                updateTotalExpenses();
                            }
                        })
                        .setNegativeButton("Don't do it man!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateRecycler();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                updateRecycler();
                            }
                        })
                        .show();


            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void updateRecycler() {
        mAdapter = new ExpensesAdapter(helper.getExpensesArray());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void updateTotalExpenses() {
        String expenses = helper.getTotalExpenses() + "";
        mTextViewTotalExpenses.setText(expenses);
    }

    public void undoClear() {
        mAdapter = new ExpensesAdapter(helper.getTempArray());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
    }

    public void loadDummyContent() {
        ArrayList<Expense> dummyArray = new ArrayList<>();
        dummyArray.add(new Expense("אוכל", "מיכאל", "קפה ומאפה", 1456335063645L, 20));
        dummyArray.add(new Expense("קניות", "פוליצר", "", 1456335063645L, 130));
        dummyArray.add(new Expense("דלק", "yellow", "", 1456335063645L, 340));
        dummyArray.add(new Expense("אוכל", "גוז' וגניאל", "ארוחת צהריים בקטנה", 1456335063645L, 800));
        dummyArray.add(new Expense("מסאז'", "ליאור", "", 1456335063645L, 150));
        dummyArray.add(new Expense("חדר כושר", "", "מנוי חודשי", 1456335063645L, 300));
        dummyArray.add(new Expense("מתנה ליואבי", "", "טלויזיה 50 אינץ'", 1456335063645L, 3000));
        dummyArray.add(new Expense("קניות", "סטופ מרקט", "קניות למרקיה", 1456335063645L, 250));
        dummyArray.add(new Expense("אוכל", "בארבי", "בירות", 1456335063645L, 60));
        mAdapter = new ExpensesAdapter(dummyArray);
        mRecyclerView.setAdapter(mAdapter);
    }


    public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesViewHolder> {

        ArrayList<Expense> expenses;

        public ExpensesAdapter(ArrayList<Expense> expenses) {
            this.expenses = expenses;
        }

        @Override
        public ExpensesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(R.layout.item_expenses, parent, false);
            return new ExpensesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ExpensesViewHolder holder, int position) {
            holder.bindExpense(expenses.get(position));
        }

        @Override
        public int getItemCount() {
            return expenses.size();
        }


        public void remove(long position) {
            expenses.remove((int) position);
            notifyItemRemoved((int) position);
        }
    }

    private class ExpensesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private Expense mExpense;
        private final TextView tvName;
        private final TextView tvAmount;
        private final TextView tvPlace;
        private final TextView tvComment;
        private final TextView tvDate;

        public ExpensesViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.textView_Item_Name);
            tvAmount = (TextView) itemView.findViewById(R.id.textView_Item_Amount);
            tvPlace = (TextView) itemView.findViewById(R.id.textView_Item_Place);
            tvComment = (TextView) itemView.findViewById(R.id.textView_Item_Comment);
            tvDate = (TextView) itemView.findViewById(R.id.textView_Item_Date);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        public void bindExpense(Expense expense) {
            mExpense = expense;
            tvName.setText(expense.getName());
            if (expense.getAmount() != 0) {
                tvAmount.setText(expense.getAmount() + "");
            } else tvAmount.clearComposingText();

            tvPlace.setText(expense.getPlace());
            tvComment.setText(expense.getComment());

            if (expense.getTime() != 0) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                tvDate.setText(format.format(new Date(expense.getTime())));
            } else tvDate.clearComposingText();
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.OnExpenseClick(mExpense.getId());
            return true;
        }

        public long getExpenseUid() {
            return mExpense.getId();
        }
    }


    public interface OnExpenseClickListener {
        void OnExpenseClick(long id);
    }

}
