package antunmod.projects.travelpacking;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateList extends AppCompatActivity {

    String COMPRESSED_FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking" +
            File.separator + ".compressed";
    ListView listView;
    String[] itemTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        listView = (ListView) findViewById(R.id.listView_items);
        itemTypes = getData();
        ItemsListAdapter itemsListAdapter = new ItemsListAdapter();
        listView.setAdapter(itemsListAdapter);
    }

    // Get the names of folders and return them in a String array
    private String[] getData() {
        List<String> returnStringList = new ArrayList<>();

        File[] directories = new File(COMPRESSED_FOLDER_LOCATION).listFiles();
        if(directories.length == 0)
            return null;
        for (File f : directories) {
            returnStringList.add(f.getName());
        }
        String[] returnString = new String[returnStringList.size()];
        returnStringList.toArray(returnString);

        return returnString;
    }

    class ItemsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemTypes.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.checked_list_item, null);

            TextView itemType = (TextView) view.findViewById(R.id.textView_itemType);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            itemType.setText(itemTypes[i]);
            return view;
        }
    }
}
