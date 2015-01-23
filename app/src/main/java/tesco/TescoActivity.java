package tesco;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import baileyae.gimbal24example.BaseActivity;
import baileyae.gimbal24example.R;


public class TescoActivity extends BaseActivity {
    private ImageLoaderConfiguration config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_prod_search);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_prod_search, null, false);
        mDrawerLayout.addView(contentView, 0);

        // Create default options which will be used for every
//  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisk(true)

                .build();

        config = new ImageLoaderConfiguration.Builder(getApplicationContext())

                .defaultDisplayImageOptions(defaultOptions)

                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO: Create menu for prod screen
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }






    public void searchProduct(View view){
        hideKeyboard();
        Intent intent = new Intent(this, OfferListActivity.class);
        EditText editText = (EditText) findViewById(R.id.search_text);
        String search = editText.getText().toString();
        intent.putExtra("SEARCH", search);
        startActivity(intent);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
