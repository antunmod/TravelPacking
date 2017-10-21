/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package antunmod.projects.travelpacking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * This application creates a listview where the ordering of the data set
 * can be modified in response to user touch events.
 *
 * An item in the listview is selected via a long press event and is then
 * moved around by tracking and following the movement of the user's finger.
 * When the item is released, it animates to its new position within the listview.
 */
public class ReorderActivity extends Activity {

    private String LISTS_FOLDER_LOCATION = Environment.getExternalStorageDirectory() +
            File.separator + "TravelPacking" + File.separator + "lists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reorder);


        Intent reorderIntent = getIntent();
        String listName = reorderIntent.getStringExtra("listName");

        ArrayList<String> listItems = getData(listName);



        StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.text_view, listItems);
        DynamicListView listView = (DynamicListView) findViewById(R.id.listview);

        listView.setList(listItems);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private ArrayList<String> getData(String listName) {

        File directory = new File(LISTS_FOLDER_LOCATION);
        File[] listFiles = directory.listFiles();
        String text = "";
        ArrayList<String> listItems = new ArrayList<>();
        for (File list : listFiles) {
            if(!list.getName().equals(listName))
                continue;
            try {
                BufferedReader br = new BufferedReader(new FileReader(list));
                String line;

                while ((line = br.readLine()) != null)
                    text += line;
                br.close();
            } catch (IOException e) {
            }
            String[] listParts = text.split("\\|");
            for(String item : listParts)
                listItems.add(item);

        }
        return listItems;
    }
}
