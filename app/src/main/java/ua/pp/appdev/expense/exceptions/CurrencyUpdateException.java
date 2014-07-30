package ua.pp.appdev.expense.exceptions;

/**
 * Created by:
 * Ilya Antipenko <ilya@antipenko.pp.ua>
 * Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class CurrencyUpdateException extends RuntimeException {

    public CurrencyUpdateException() {

    }

    public CurrencyUpdateException(String msg) {
        super(msg);
    }

    public CurrencyUpdateException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public CurrencyUpdateException(Throwable throwable) {
        super(throwable);
    }
}
