package code.puretechnicality.hddnd;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mCampaignList;
    private FloatingActionButton mFab;

    private static final String[] PROJECTION = new String[] { "_id", "name", "description" };
    private static final int LOADER_ID = 1;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mCampaignList = (ListView)findViewById(R.id.listCampaigns);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCampaignActivity.class);
                startActivity(intent);
            }
        });

        String[] dataColumns = new String[] { CampaignContentProvider.NAME,
                CampaignContentProvider.DESCRIPTION };
        int[] viewIDs = new int[] { R.id.textCampaignTitle, R.id.textCampaignDescription };
        mAdapter = new SimpleCursorAdapter(this, R.layout.campaign_list_item, null,
                dataColumns, viewIDs, 0);

        // Associate the (now empty) adapter with the ListView.
        mCampaignList.setAdapter(mAdapter);

        // Initialize the data manager and link to this activity's callbacks
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new loader for all campaign records
        return new CursorLoader(MainActivity.this, CampaignContentProvider.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // The asynchronous load is finished and the cursor gets associated with the adapter
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The data is gone and the cursor is removed from the adapter
        switch (loader.getId()) {
            case LOADER_ID:
                mAdapter.swapCursor(null);
                break;
        }
    }
}
