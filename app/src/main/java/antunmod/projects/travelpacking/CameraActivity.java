package antunmod.projects.travelpacking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    EditText editItemName;
    EditText editItemType;
    final static String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg" // and other formats you need
    };
    Spinner spinner;
    Button btnTakePhoto;
    ArrayAdapter<String> dataAdapter;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        editItemName = (EditText)findViewById(R.id.editItemName);
        editItemType = (EditText)findViewById(R.id.editItemType);
        spinner = (Spinner) findViewById(R.id.spinner);
        addItemsToSpinner();
        btnTakePhoto = (Button)findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If there is a new folder name
                if(!editItemType.getText().toString().isEmpty()) {
                    String newItemType = editItemType.getText().toString();
                    for (String item : spinnerList) {
                        if(item.equals(newItemType.toUpperCase()))
                            Toast.makeText(CameraActivity.this, "The Item type with the given name already exists!", Toast.LENGTH_LONG).show();
                    }
                    createDirectory(newItemType);
                }
                else {
                    String newItemName = editItemName.getText().toString();
                    String fileLocation = FOLDER_LOCATION + File.separator + spinner.getSelectedItem().toString();
                    if(filenameExists(fileLocation, newItemName))
                        Toast.makeText(getApplicationContext(), "The item with the given name already exists!", Toast.LENGTH_LONG).show();
                }

                Adapter adapter = spinner.getAdapter();

            }
        });
    }

    private void addItemsToSpinner() {
        spinnerList = new ArrayList<>();
        File dir = new File(FOLDER_LOCATION);
        File[] directoryList = dir.listFiles();
        if(directoryList==null)
            return;
        for(File directory:directoryList) {
            if(directory.isDirectory())
                spinnerList.add(directory.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void createDirectory (String newFolderName) {
        File directory = new File(FOLDER_LOCATION + File.separator +  newFolderName.toUpperCase());
        if(!directory.exists())
            if(directory.mkdir())
                Toast.makeText(getApplicationContext(), "New item type created!", Toast.LENGTH_LONG).show();
        //addItemsToSpinner();
    }

    private boolean filenameExists (String fileLocation, String filename) {
        File dir = new File(fileLocation);
        File[] imageList = dir.listFiles(IMAGE_FILTER);
        for(File f : imageList) {
            if(f.getName().substring(0,f.getName().length()-4).equals(filename))
                return true;
        }
        return false;
    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        File dir = new File(FOLDER_LOCATION);
        File[] imageList = dir.listFiles(IMAGE_FILTER);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        if(imageList==null)
            return imageItems;
        for (File f : imageList) {
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
            //bitmap = getResizedBitmap(bitmap, 200, 200);
            imageItems.add(new ImageItem(bitmap, "Test"));
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
