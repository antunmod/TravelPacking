package antunmod.projects.travelpacking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WardrobeActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp" // and other formats you need
    };
    final String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    private FloatingActionButton addNewItem;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    SharedPreferences prefs;
    static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        prefs= getSharedPreferences("aName", Context.MODE_PRIVATE);

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridAdapter);

        addNewItem = (FloatingActionButton) findViewById(R.id.addNewItem);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                //dispatchTakePictureIntent();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            getIntent().getBundleExtra("aa");
            galleryAddPic();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(), "There was an error creating image file",
                        Toast.LENGTH_LONG);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        /*// Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //save the value
        prefs.edit()
                .putString("currentPhotoPath", mCurrentPhotoPath).apply();*/
        PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .edit().putString("currentPhotoPath", mCurrentPhotoPath).apply();

        return image;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            galleryAddPic();
        }
    }*/

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        /*// get the data
        prefs.getString("currentPhotoPath", FOLDER_LOCATION+"aaa.jpg");*/
        mCurrentPhotoPath = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
                .getString("currentPhotoPath", FOLDER_LOCATION + File.separator +"aaa.jpg");

        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void Savefile(String name, String path) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/MyAppFolder/MyApp/");
        File file = new File(Environment.getExternalStorageDirectory() + "/MyAppFolder/MyApp/" + name + ".png");

        if (!direct.exists()) {
            direct.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileChannel src = new FileInputStream(path).getChannel();
                FileChannel dst = new FileOutputStream(file).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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

// Wut mate?
                /*//camera stuff
                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//folder stuff
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
                imagesFolder.mkdirs();

                File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
                Uri uriSavedImage = Uri.fromFile(image);

                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/



        // Don't even know what all of this does
        /*addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 1);
            }

            protected void (int requestCode, int resultCode, Intent imageReturnedIntent) {
                WardrobeActivity.super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

                switch(requestCode) {
                    case 1:
                        if(resultCode == RESULT_OK) {
                            Uri selectedImage = imageReturnedIntent.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            //file path of captured image
                            String filePath = cursor.getString(columnIndex);
                            //file path of captured image
                            File f = new File(filePath);
                            String filename= f.getName();

                            Toast.makeText(getApplicationContext(), "Your Path:"+filePath, Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Your Filename:"+filename, Toast.LENGTH_LONG).show();
                            cursor.close();

                            //Convert file path into bitmap image using below line.
                            // yourSelectedImage = BitmapFactory.decodeFile(filePath);
                            Toast.makeText(getApplicationContext(), "Your image", Toast.LENGTH_LONG).show();

                            //put bitmapimage in your imageview
                            //yourimgView.setImageBitmap(yourSelectedImage);

                            Savefile(filename,filePath);
                        }
                }
            }

        });*/

// get data from drawable folder
    /*private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < 3*imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i%imgs.length(), -1));
            //bitmap = getResizedBitmap(bitmap, 200, 200);
            imageItems.add(new ImageItem(bitmap, "Testing text size: -----------------------------Image#" + i));
        }
        return imageItems;
    }*/
