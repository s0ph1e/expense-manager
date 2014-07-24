package ua.pp.appdev.expense.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Expense implements Serializable{
    public long id;
    public Calendar createDate;
    public Calendar expenseDate;
    public Category category;
    public BigDecimal sum;
    public Currency currency;
    public String note;

    public Expense(){
        id = 0;
        createDate = GregorianCalendar.getInstance();
        expenseDate = GregorianCalendar.getInstance();
        category = null;
        sum = new BigDecimal(0);
        currency = null;
        note = "";
    }
}
