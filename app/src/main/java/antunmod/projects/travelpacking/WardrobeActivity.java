package antunmod.projects.travelpacking;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
        "gif", "png", "bmp" // and other formats you need
    };
    final String FOLDER_LOCATION = Environment.getExternalStorageDirectory() + File.separator + "TravelPacking";
    private FloatingActionButton addNewItem;

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
                
            }
        });
    }

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

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        File dir = new File(FOLDER_LOCATION);
        File[] imageList = dir.listFiles(IMAGE_FILTER);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
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

