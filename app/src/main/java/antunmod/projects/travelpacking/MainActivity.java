package antunmod.projects.travelpacking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView packTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packTextView = (TextView) findViewById(R.id.packTextView);
        packTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchIntent = new Intent (view.getContext(), ScrollingActivity.class);
                startActivity(switchIntent);
            }
        });
    }
}
