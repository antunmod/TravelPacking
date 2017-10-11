package antunmod.projects.travelpacking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListsActivity extends AppCompatActivity {

    ListView listView;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        listView = (ListView) findViewById(R.id.listView);
        simpleAdapter = new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_2,
                new String[] {"listName", "listString"}, new int[] {android.R.id.text1, android.R.id.text2});
        listView.setAdapter(simpleAdapter);
    }

    public List<Map<String, String>> getData () {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>(2);
        map.put("listName", "listName");
        map.put("listString", "shirts, trousers, ...");
        list.add(map);

        return list;
    }
}
