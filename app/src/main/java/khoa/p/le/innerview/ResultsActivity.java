package khoa.p.le.innerview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static khoa.p.le.innerview.MainActivity.REQUEST_VIDEO_CAPTURE;

public class ResultsActivity extends AppCompatActivity {
    TextView sentscoreView, filler1, filler2, filler3;
    ListView keywordsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        //initialize textviews
        sentscoreView = (TextView) findViewById(R.id.sentiment_textView);
        filler1=(TextView)findViewById(R.id.filler1_textView);
        filler2=(TextView)findViewById(R.id.filler2_textView);
        filler3=(TextView)findViewById(R.id.filler3_textView);
        keywordsListView=(ListView)findViewById(R.id.keywords_listview);

        Double sentscoredata = getIntent().getDoubleExtra("sentscore", 0.0);

        if(sentscoredata >= 0 && sentscoredata <= .50)
            sentscoreView.setText("Cheer Up. Relax.");
        else if(sentscoredata > .50 && sentscoredata <= .75)
            sentscoreView.setText("Perfect Level");
        else
            sentscoreView.setText("Good don't be too cheery yet!");

        int count1 = getIntent().getIntExtra("like", 0);
        int count2 = getIntent().getIntExtra("so", 0);
        int count3 = getIntent().getIntExtra("yeah", 0);

        filler1.setText("Like: " + count1);
        filler2.setText("So: " + count2 );
        filler3.setText("Yeah: " + count3);

        ArrayList<String> keywordsArr = getIntent().getStringArrayListExtra("keywords");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                keywordsArr
        );
        keywordsListView.setAdapter(arrayAdapter);

    }

}
