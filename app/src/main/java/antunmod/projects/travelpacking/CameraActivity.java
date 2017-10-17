package antunmod.projects.travelpacking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import static antunmod.projects.travelpacking.Utility.filenameExists;
import static antunmod.projects.travelpacking.Utility.modifyName;
import static antunmod.projects.travelpacking.Utility.createFolder;
import static antunmod.projects.travelpacking.Utility.nameIsValid;


public class CameraActivity extends AppCompatActivity {

    EditText editItemName;
    EditText editItemType;
    final static String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    final static String COMPRESSED_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".compressed";
    final static String FULL_SIZE_FOLDER_LOCATION = FOLDER_LOCATION + File.separator + ".fullSize";

    Spinner spinner;
    Button btnTakePhoto;
    List<String> spinnerList;
    int CAPTURE_IMAGE_REQUEST_CODE = 1;
    protected static int EXTENSION_LENGTH = 4;


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
                String imageName = editItemName.getText().toString() + ".jpg";
                String itemType = editItemType.getText().toString().toUpperCase();
                String fullSizeImageFolderLocation;

                //If something is written in the ItemTypeEditText field, check whether to add new item
                if(!itemType.isEmpty()) {
                    imageName = modifyName(imageName);

                    if(!nameIsValid(imageName))
                        Toast.makeText(getApplicationContext(), "Image name mustn't be empty!", Toast.LENGTH_LONG).show();

                    else if (!itemTypeExists(itemType)) {
                        createFolder(COMPRESSED_FOLDER_LOCATION + File.separator + itemType);
                        createFolder(FULL_SIZE_FOLDER_LOCATION + File.separator + itemType);
                        fullSizeImageFolderLocation = FULL_SIZE_FOLDER_LOCATION + File.separator + itemType;
                        capturePhoto(imageName, fullSizeImageFolderLocation);
                    }

                }

                else {
                    if(spinner.getAdapter().getCount()==0) {
                        Toast.makeText(getApplicationContext(), "Enter item type to continue!", Toast.LENGTH_LONG).show();
                    }

                    else if (nameIsValid(imageName)) {
                        itemType = spinner.getSelectedItem().toString();
                        fullSizeImageFolderLocation = FULL_SIZE_FOLDER_LOCATION + File.separator + itemType;
                        if (filenameExists(fullSizeImageFolderLocation, imageName))
                            Toast.makeText(getApplicationContext(), "The item with the given name already exists!", Toast.LENGTH_LONG).show();
                        else {
                            capturePhoto(imageName, fullSizeImageFolderLocation);
                        }
                    }
                }


            }
        });
    }

    private void addItemsToSpinner() {
        spinnerList = new ArrayList<>();
        File dir = new File(COMPRESSED_FOLDER_LOCATION);
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

    /*protected static boolean nameIsValid(String name) {

        // If imageName is empty
        if (name.length()== EXTENSION_LENGTH) {
            return false;
        }

        return true;
    }*/

    public boolean itemTypeExists (String newItemType) {
        for (String item : spinnerList) {
            if(item.equals(newItemType)) {
                Toast.makeText(CameraActivity.this, "The Item type with the given name already exists!", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return false;
    }

    /*private boolean filenameExists (String fileLocation, String filename) {
        File dir = new File(fileLocation);
        File[] imageList = dir.listFiles(IMAGE_FILTER);
        for(File f : imageList) {
            if(f.getName().substring(0,f.getName().length()).equals(filename))
                return true;
        }
        return false;
    }*/

    private void capturePhoto(String imageName, String fullSizeImageFolderLocation) {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File image = new File(fullSizeImageFolderLocation, imageName);
        Uri uriSavedImage = Uri.fromFile(image);

        //imageIntent.putExtra("imageName", imageName);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_REQUEST_CODE);
        return;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent imageReturnedIntent) {
        String fullSizeImageFolderLocation = FULL_SIZE_FOLDER_LOCATION + File.separator;
        String imageName = editItemName.getText().toString() + ".jpg";
        String itemType = editItemType.getText().toString();
        if(requestCode == CAPTURE_IMAGE_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {

                if(itemType.isEmpty())
                    fullSizeImageFolderLocation += spinner.getSelectedItem().toString();
                else
                    fullSizeImageFolderLocation += itemType.toUpperCase();

                shrinkAndSaveImage(fullSizeImageFolderLocation, imageName);
                Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_LONG).show();
                restartActivity();
            }
            else Toast.makeText(getApplicationContext(), "Image not saved", Toast.LENGTH_LONG).show();

        }
    }

    /*// filter to identify images based on their extensions
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
    };*/

    public void shrinkAndSaveImage(String fullSizeImageFolderLocation, String imageName) {
        //get to correct folder
        File compressedImageDirectory;
        String compressedImageFolderLocation;
        String itemType = editItemType.getText().toString().toUpperCase();
        if(itemType.isEmpty()) {
            itemType = spinner.getSelectedItem().toString();
        }

        compressedImageFolderLocation = COMPRESSED_FOLDER_LOCATION + File.separator + itemType;
        compressedImageDirectory  = new File(compressedImageFolderLocation);

        String imageFile = fullSizeImageFolderLocation + File.separator + imageName;
        OutputStream outStream;
        File file = new File (compressedImageDirectory, imageName);

         /*Put the following code in a separate method, find a way not to send values a million times in different
         methods*/
        try {
            Bitmap bm = shrinkAndRotateBitmap(imageFile, 300, 300);

            // Save compressed image
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 10, outStream);

            outStream.flush();
            outStream.close();
            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    Bitmap shrinkAndRotateBitmap(String imageLocation, int width, int height) throws IOException {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap; /*= BitmapFactory.decodeFile(imageFile, bmpFactoryOptions);*/

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        ExifInterface ei = new ExifInterface(imageLocation);
        int o = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotate;
        switch (o) {
            case ExifInterface.ORIENTATION_ROTATE_90: rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180: rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270: rotate = 270;
                break;
            default: rotate = 0;
                break;
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imageLocation, bmpFactoryOptions);

        //rotate bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    private void restartActivity() {
        //restart activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
