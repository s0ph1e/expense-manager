package ua.pp.appdev.expense;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class Category {
    public int id;
    public String name;
    public int color;
    public boolean checked;

    public Category(int id, String name, int color, boolean checked) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.checked = checked;
    }
}
