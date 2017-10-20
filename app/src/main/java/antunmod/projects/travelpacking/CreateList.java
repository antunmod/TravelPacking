package antunmod.projects.travelpacking;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static antunmod.projects.travelpacking.Utility.filenameExists;
import static antunmod.projects.travelpacking.Utility.modifyName;
import static antunmod.projects.travelpacking.Utility.nameIsValid;

public class CreateList extends AppCompatActivity {

    String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    String COMPRESSED_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".compressed";
    String LISTS_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + "lists";
    ListView listView;
    String[] itemTypes;
    Button btnOrderList;
    CustomListItem[] customListItem;
    EditText editListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        listView = (ListView) findViewById(R.id.itemsListView);
        itemTypes = getData();
        customListItem = new CustomListItem[itemTypes.length];

        ItemsListAdapter itemsListAdapter = new ItemsListAdapter();
        listView.setAdapter(itemsListAdapter);

        editListName = (EditText) findViewById(R.id.editListName);


        btnOrderList = (Button) findViewById(R.id.btnOrderList);
        btnOrderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName = editListName.getText().toString();
                listName = modifyName(listName);
                listName += ".txt";
                if(nameIsValid(listName))
                    if(!filenameExists(LISTS_FOLDER_LOCATION, listName))
                        createNewList(listName);
                    else
                        Toast.makeText(getApplicationContext(), "A list with the given name already exists!",
                                Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "List name mustn't be empty!", Toast.LENGTH_LONG).show();
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

    private void createNewList(String listName) {
        String saveString = "";

        int noOfSelectedItems = getNoOfSelectedItems();
        int noOfAddedItems = 0;
        for (int i = 0; i < customListItem.length; ++i) {
            if(customListItem[i].getCheckBox()) {
                saveString += itemTypes[i];
                if (noOfAddedItems != noOfSelectedItems - 1) {
                    saveString += "|";
                }
                ++noOfAddedItems;
            }
        }

        if(noOfAddedItems == 0) {
            Toast.makeText(getApplicationContext(), "Select some items to be added to the list!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File newList = new File(LISTS_FOLDER_LOCATION, listName);
            FileWriter writer = new FileWriter((newList));
            writer.append(saveString);
            writer.flush();
            writer.close();
            Toast.makeText(getApplicationContext(), "list " + listName + " saved!", Toast.LENGTH_LONG).show();
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
