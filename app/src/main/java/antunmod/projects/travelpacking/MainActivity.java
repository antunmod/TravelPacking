package antunmod.projects.travelpacking;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView packTextView;
    private TextView wardrobeTextView;
    private TextView listsTextView;
    private String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    private String COMPRESSED_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".compressed";
    private String FULL_SIZE_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".fullSize";
    private String LISTS_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + "lists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create required folders for storage
        createRequiredFolder(FOLDER_LOCATION);
        createRequiredFolder(COMPRESSED_FOLDER_LOCATION);
        createRequiredFolder(FULL_SIZE_FOLDER_LOCATION);
        createRequiredFolder(LISTS_FOLDER_LOCATION);

        File dir = new File (COMPRESSED_FOLDER_LOCATION);

        //Wait until the folder creates
        while(!dir.exists());

        packTextView = (TextView) findViewById(R.id.packTextView);
        packTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchIntent = new Intent (view.getContext(), ScrollingActivity.class);
                startActivity(switchIntent);
            }
        });

        wardrobeTextView = (TextView) findViewById(R.id.wardrobeTextView);
        wardrobeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchIntent = new Intent (view.getContext(), WardrobeActivity.class);
                startActivity(switchIntent);
            }
        });

        listsTextView = (TextView) findViewById(R.id.listsTextView);
        listsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchIntent = new Intent (view.getContext(), ListsActivity.class);
                startActivity(switchIntent);
            }
        });



    }

    public static void createRequiredFolder(String path) {
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdir();
    }
}
