package daily.zjrb.com.mediaproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zjrb.daily.mediaselector.ui.MediaSelectActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MediaSelectActivity.class);
                intent.putExtra("max_num", 3);
//                Bundle bundle = new Bundle();
//                bundle.putInt("max_num", 3);
//                intent.putExtra(bundle);
                startActivity(intent);

            }
        });
    }
}
