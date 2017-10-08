package antunmod.projects.travelpacking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
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
    boolean itemTypeExists = false;
    int CAPTURE_IMAGE_REQUEST_CODE = 1;

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
                        if(item.equals(newItemType.toUpperCase())) {
                            Toast.makeText(CameraActivity.this, "The Item type with the given name already exists!", Toast.LENGTH_LONG).show();
                            itemTypeExists = true;
                            break;
                        }
                    }
                    if(!itemTypeExists)
                        createDirectory(newItemType);
                }

                else {
                    String imageName = editItemName.getText().toString();
                    String folderName = spinner.getSelectedItem().toString();
                    String fileLocation = FOLDER_LOCATION  + File.separator + ".fullSize" + File.separator + folderName;
                    if(filenameExists(fileLocation, imageName))
                        Toast.makeText(getApplicationContext(), "The item with the given name already exists!", Toast.LENGTH_LONG).show();
                    else {
                        capturePhoto(imageName + ".jpg", fileLocation);
                        //compressAndSaveImage(FOLDER_LOCATION + File.separator + ".compressed" + File.separator + folderName, imageName + ".jpg");
                    }
                }


            }
        });
    }

    private void addItemsToSpinner() {
        spinnerList = new ArrayList<>();
        File dir = new File(FOLDER_LOCATION + File.separator + ".fullSize");
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
        File directory = new File(FOLDER_LOCATION + File.separator +  ".fullSize" + File.separator + newFolderName.toUpperCase());
        if(!directory.exists())
            if(directory.mkdir()) {
                //Toast.makeText(getApplicationContext(), "New item type created!", Toast.LENGTH_LONG).show();
                String imageName = editItemName.getText().toString() + ".jpg";
                String imageLocation = FOLDER_LOCATION + File.separator + ".compressed" + File.separator + newFolderName;
                capturePhoto(imageName, directory.getAbsolutePath());
                //compressAndSaveImage(imageLocation, imageName);
                //addItemsToSpinner();
            }
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

    private void capturePhoto(String imageName, String filepath) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File image = new File(filepath, imageName);
        Uri uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra("imageName", imageName);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
        return;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent imageReturnedIntent) {
        if(requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                if(editItemType.getText().toString().isEmpty())
                    compressAndSaveImage(FOLDER_LOCATION + File.separator + ".fullSize" + File.separator + spinner.getSelectedItem().toString(), editItemName.getText().toString());
                else
                    compressAndSaveImage(FOLDER_LOCATION + File.separator + ".fullSize" + File.separator + editItemType.getText().toString(), editItemName.getText().toString());

                Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_LONG).show();

            }
            else Toast.makeText(getApplicationContext(), "Image not saved", Toast.LENGTH_LONG).show();

        }
        //finish();
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

    public void compressAndSaveImage(String imageLocation, String imageName) {
        //get to correct folder
        File compressedImageDirectory;
        String path;
        if(editItemType.getText().toString().isEmpty()) {
            //create new folder
            String folderName = spinner.getSelectedItem().toString();
            path = FOLDER_LOCATION + File.separator + ".compressed" + File.separator + folderName;
            File directory = new File(path);
            if(!directory.exists())
                directory.mkdir();
            compressedImageDirectory = new File(FOLDER_LOCATION + File.separator + ".compressed"
                    + File.separator + spinner.getSelectedItem().toString());
        }
        else {
            //create new folder
            String folderName = editItemType.getText().toString().toUpperCase();
            path = FOLDER_LOCATION + File.separator + ".compressed" + File.separator + folderName;
            MainActivity.createRequiredFolder(path);
            compressedImageDirectory  = new File(path);
        }

        String folderName = compressedImageDirectory.getName();
        String imageFile = imageLocation + File.separator + imageName + ".jpg";
        //String fullSizedFile = FOLDER_LOCATION + File.separator + ".compressed" + File.separator + folderName;
        OutputStream outStream;
        File file = new File (compressedImageDirectory, imageName + ".jpg");
        //File fullSizeImage = new File(imageLocation + File.separator + imageName);
        //File compressedImage = new File(FOLDER_LOCATION + File.separator + ".compressed" + File.separator + folderName + File.separator + imageName);

        Bitmap bm = ShrinkBitmap(imageFile, 300, 300);

        // Put the following code in a separate method, find a way not to send values a million times in different
        // methods
        int o = 0;
        try {
            ExifInterface ei = new ExifInterface(imageLocation + File.separator + imageName);
            o = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();
            outStream.close();
            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        ExifInterface ei2 = null;
        try {
            ei2 = new ExifInterface(path + File.separator + imageName);
            ei2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(o));
            ei2.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //saveOrientationAttributes(imageLocation, imageName + ".jpg", path);
    }

    Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            /*if (heightRatio > widthRatio)
            {*/
                bmpFactoryOptions.inSampleSize = heightRatio;
            /*} else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }*/
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    private void saveOrientationAttributes(String imageLocation, String imageName, String compressedImagePath)  {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imageLocation + File.separator + imageName);
            int o = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            ExifInterface ei2 = new ExifInterface(compressedImagePath + File.separator + imageName);
            ei2.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(o));
            ei2.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
