package antunmod.projects.travelpacking;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateList extends AppCompatActivity {

    String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    String COMPRESSED_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".compressed";
    String LISTS_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + "lists";
    ListView listView;
    String[] itemTypes;
    Button btnOrderList;
    CustomListItem[] customListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        listView = (ListView) findViewById(R.id.itemsListView);
        itemTypes = getData();
        customListItem = new CustomListItem[itemTypes.length];

        ItemsListAdapter itemsListAdapter = new ItemsListAdapter();
        listView.setAdapter(itemsListAdapter);

        btnOrderList = (Button) findViewById(R.id.btnOrderList);
        btnOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createNewList();
            }
        });

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

    private void createNewList() {
        String saveString = "";
        ItemsListAdapter itemsListAdapter = (ItemsListAdapter) listView.getAdapter();

        int noOfSelectedItems = getNoOfSelectedItems();
        int noOfAddedItems = 0;
        for (int i = 0; i < customListItem.length; ++i) {
            if(customListItem[i].getCheckBox()) {
                saveString += itemTypes[i];
                if (noOfAddedItems != noOfSelectedItems - 1) {
                    ++noOfAddedItems;
                    saveString += "|";
                }
            }
        }

        try {
            File newList = new File(LISTS_FOLDER_LOCATION, "aab.txt");
            FileWriter writer = new FileWriter((newList));
            writer.append(saveString);
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNoOfSelectedItems() {
        int noOfSelectedItems = 0;
        for (int i = 0; i < customListItem.length; ++i) {
            if(customListItem[i].getCheckBox())
                ++noOfSelectedItems;
        }
        return noOfSelectedItems;
    }

    class ItemsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemTypes.length;
        }

        @Override
        public Object getItem(int i) {
            return customListItem[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.checked_list_item, null);

            TextView itemType = (TextView) view.findViewById(R.id.textView_itemType);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            final int position = i;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customListItem[position].setCheckBox(checkBox.isChecked());
                }
            });
            customListItem[i] = new CustomListItem(itemTypes[i], false);

            itemType.setText(itemTypes[i]);
            return view;
        }
    }
}
