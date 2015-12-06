package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by චානක මධුරංග on 2015-12-06.
 */
public class PersistentAccountDAO implements AccountDAO{
        SQLiteDatabase database;
        java.io.File filename = Constants.context.getFilesDir();

    public PersistentAccountDAO(){
            database = SQLiteDatabase.openOrCreateDatabase(filename.getAbsolutePath() + "/130281M.sqlite", null);
            database.execSQL("CREATE TABLE IF NOT EXISTS account(accountNo VARCHAR(8),bankName VARCHAR(15),accountHolderName VARCHAR(100), balance NUMERIC(12,2));");
        }


    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNos = new ArrayList<String>();
        Cursor cursor = database.rawQuery("SELECT accountNo from account;", null);

        while (cursor.moveToNext()) {
            accountNos.add(cursor.getString(0));
        }
        return accountNos;

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance = 0;
        Cursor cursor =  database.rawQuery("SELECT balance FROM account WHERE accountNo = '"+ accountNo+"';", null);
        cursor.moveToNext();
        double current_balance = cursor.getDouble(0);
        switch (expenseType) {
            case EXPENSE:
                balance = current_balance - amount;
            case INCOME:
                balance = current_balance + amount;
        }
        database.execSQL("UPDATE account SET balance = '"+ balance + "'WHERE accountNo = '" + accountNo + "';");
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        database.execSQL("DELETE FROM account WHERE accountNo = '" + accountNo +"';");
    }

    @Override
    public void addAccount(Account account) {
        database.execSQL("INSERT INTO account VALUES('" + account.getAccountNo() + "','" + account.getBankName() + "','" + account.getAccountHolderName() + "','" + account.getBalance() + "');");
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = database.rawQuery("SELECT * FROM account WHERE accountNo = '" + accountNo + "';",null);
        cursor.moveToNext();
        Account account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
        return account;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<Account>();

        Cursor cursor = database.rawQuery("SELECT * FROM account;",null);

        while (cursor.moveToNext()) {
            Account account = new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
            accounts.add(account);
        }
        return accounts;
    }
}
