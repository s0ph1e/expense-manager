package ua.pp.appdev.expense;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Expense implements Serializable{
    public long id;
    public Calendar createDate;
    public Calendar expenseDate;
    public long categoryId;
    public BigDecimal sum;
    public long currencyId;
    public String description;

    public Expense(){
        id = 0;
        createDate = GregorianCalendar.getInstance();
        expenseDate = GregorianCalendar.getInstance();
        categoryId = 0;
        sum = new BigDecimal(0);
        currencyId = 0;
        description = "";
    }
}
