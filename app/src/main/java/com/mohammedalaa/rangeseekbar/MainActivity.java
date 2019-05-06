package com.mohammedalaa.rangeseekbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mohammedalaa.seekbar.RangeSeekBarView;

public class MainActivity extends AppCompatActivity {

    RangeSeekBarView rangeSeekBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rangeSeekBarView=findViewById(R.id.range_seekbar);
        rangeSeekBarView.setValue(40);
        rangeSeekBarView.setAnimated(true,3000L);
    }

    public void getValue(View view) {
        Toast.makeText(this, rangeSeekBarView.getValue() + "", Toast.LENGTH_SHORT).show();

    }
}
