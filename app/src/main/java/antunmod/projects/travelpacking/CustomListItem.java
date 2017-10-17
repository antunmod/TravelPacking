package antunmod.projects.travelpacking;

import android.widget.CheckBox;

/**
 * Created by antun on 10/17/2017.
 */

public class CustomListItem {
    private String item;
    private Boolean checkBox;

    public CustomListItem(String item, Boolean checkBox) {
        this.item = item;
        this.checkBox = checkBox;
    }

    public Boolean getCheckBox() {

        return checkBox;
    }

    public void setCheckBox(Boolean checkBox) {
        this.checkBox = checkBox;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }


}
