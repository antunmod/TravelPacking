package antunmod.projects.travelpacking;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static antunmod.projects.travelpacking.Utility.removeExtension;

public class ListsActivity extends AppCompatActivity {

    ListView listView;
    SimpleAdapter simpleAdapter;
    FloatingActionButton floatingActionButton;
    String LISTS_FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator +
            "TravelPacking" + File.separator + "lists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        listView = (ListView) findViewById(R.id.listView);
        simpleAdapter = new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_2,
                new String[] {"listName", "listString"}, new int[] {android.R.id.text1, android.R.id.text2});
        listView.setAdapter(simpleAdapter);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createListIntent = new Intent(getApplicationContext(), CreateList.class);
                startActivity(createListIntent);
            }
        });


    }

    public List<Map<String, String>> getData () {

        File directory = new File(LISTS_FOLDER_LOCATION);
        File[] listFiles = directory.listFiles();
        String text = "";
        List<Map<String, String>> lists = new ArrayList<>();
        for( File list : listFiles) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(list));
                String line;

                while ((line = br.readLine()) != null)
                    text += line;
                br.close();
            } catch (IOException e) {}
            Map<String, String> map = new HashMap<>(2);
            map.put("listName", removeExtension(list.getName()));
            map.put("listString", text);
            lists.add(map);
            text = "";
        }


        return lists;
    }
}
