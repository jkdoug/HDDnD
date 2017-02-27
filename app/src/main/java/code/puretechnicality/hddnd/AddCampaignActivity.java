package code.puretechnicality.hddnd;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddCampaignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campaign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClickSave(View view) {
        // Add a new student record
        ContentValues values = new ContentValues();
        values.put(CampaignContentProvider.NAME,
                ((EditText)findViewById(R.id.editTitle)).getText().toString());
        values.put(CampaignContentProvider.DESCRIPTION,
                ((EditText)findViewById(R.id.editDescription)).getText().toString());

        Uri uri = getContentResolver().insert(CampaignContentProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
    }

}
