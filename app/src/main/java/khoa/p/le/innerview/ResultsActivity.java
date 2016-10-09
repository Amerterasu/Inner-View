package khoa.p.le.innerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ResultsActivity extends AppCompatActivity {
    TextView sentscoreView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sentscoreView = (TextView) findViewById(R.id.sentiment_textView);
        Double data = getIntent().getDoubleExtra("sentscore", 0.0);
        sentscoreView.setText(Double.toString(data));
    }

}
