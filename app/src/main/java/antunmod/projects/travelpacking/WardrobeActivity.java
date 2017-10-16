package antunmod.projects.travelpacking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class WardrobeActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg" // and other formats you need
    };
    final String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    private FloatingActionButton addNewItem;
    static final int REQUEST_CODE = 1;
    static final int EXTENSION_LENGTH = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        addNewItem = (FloatingActionButton) findViewById(R.id.addNewItem);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }

        });

        getIntent().setAction("Created");
    }

    @Override
    protected void onResume () {
        String action = getIntent().getAction();

        //Restart activity to update items list
        if(action == null || !action.equals("Created")) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        else
            getIntent().setAction(null);

        super.onResume();
    }


    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        File dir = new File(FOLDER_LOCATION + File.separator + ".compressed");

        File[] folderList = dir.listFiles();
        if(folderList.length == 0)
            return imageItems;
        for(File folder : folderList) {
            File[] images = folder.listFiles(IMAGE_FILTER);
            if (images.length == 0)
                return imageItems;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            for(File image : images) {
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
                String imageName = image.getName();
                imageItems.add(new ImageItem(bitmap, imageName.substring(0, imageName.length() - EXTENSION_LENGTH)));
            }
        }
        return imageItems;
    }

    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

}