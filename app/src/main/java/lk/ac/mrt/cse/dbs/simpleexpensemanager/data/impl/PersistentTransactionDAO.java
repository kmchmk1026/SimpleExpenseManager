package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by චානක මධුරංග on 2015-12-06.
 */
public class PersistentTransactionDAO implements TransactionDAO {

    SQLiteDatabase database;
    java.io.File filename = Constants.context.getFilesDir();
    //private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public PersistentTransactionDAO() {
        database = SQLiteDatabase.openOrCreateDatabase(filename.getAbsolutePath() + "/130281M.sqlite", null);
        database.execSQL("CREATE TABLE IF NOT EXISTS transactions(accountNo VARCHAR(8),expenseType VARCHAR(10), amount NUMERIC(12,2), date Date);");
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor cursor = database.rawQuery("Select * from transactions ORDER BY date LIMIT "+ limit, null);
        List<Transaction> transactions = new ArrayList<Transaction>();
        while (cursor.moveToNext())
        {
            ExpenseType expense = null;
            if(cursor.getString(1).equals("INCOME")){
                expense = ExpenseType.INCOME;
            }
            else{
                expense = ExpenseType.EXPENSE;
            }
            transactions.add( new Transaction(new Date(cursor.getString(3)),cursor.getString(0),expense, cursor.getDouble(2)  ));
        }
        return transactions;
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = database.rawQuery("SELECT * FROM transactions;",null);
        while (cursor.moveToNext()) {
            ExpenseType expenseType = null;
            if(cursor.getString(1).equals("INCOME")){
                expenseType = ExpenseType.INCOME;
            }
            else{
                expenseType = ExpenseType.EXPENSE;
            }
            Transaction transaction = new Transaction(new Date(cursor.getString(3)),cursor.getString(0),expenseType,cursor.getDouble(2));
            transactions.add(transaction);
        }
        return transactions;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String expense = null;
        if (expenseType.equals(ExpenseType.INCOME)) {
            expense = "INCOME";
        }
        else{
            expense = "EXPENSE";
        }

        database.execSQL("INSERT INTO transactions VALUES('"+accountNo+"','"+expense+"','"+amount+"','"+date.toString()+"');");}
}
