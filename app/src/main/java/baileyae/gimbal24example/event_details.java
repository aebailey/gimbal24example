package baileyae.gimbal24example;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


public class event_details extends ActionBarActivity {

    private GimbalEventListAdapter adapter;
    private ListView listView;
    private Context main_context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        main_context = this;
        adapter = new GimbalEventListAdapter(this);
        adapter.setEvents(GimbalDAO.getEvents(getApplicationContext()));
        listView = (ListView) findViewById(R.id.listview_full);
        createListView(listView);
    }

    public void createListView(final ListView clistView){

        clistView.setAdapter(adapter);
        //clistView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
        //        Intent intent = new Intent(main_context,event_details.class);
        //
        //       intent.putExtra("item_clicked", position);
        //        startActivity(intent);
        //    }
        //});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
