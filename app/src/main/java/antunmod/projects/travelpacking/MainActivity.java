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
    private String applicationLocation = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create required folders for storage
        createRequiredFolder(applicationLocation);
        createRequiredFolder(applicationLocation + File.separator + ".compressed");
        createRequiredFolder(applicationLocation + File.separator + ".fullSize");

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


    }

    public static void createRequiredFolder(String path) {
        File directory = new File(path);
        if(!directory.exists())
            directory.mkdir();
    }
}
