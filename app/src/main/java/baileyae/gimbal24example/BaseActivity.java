package baileyae.gimbal24example;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import camera.CameraActivity;
import tesco.SettingsProd;
import tesco.TescoActivity;


public class BaseActivity extends ActionBarActivity {
    //Added for drawer layout
    private String[] mNavigationDrawerItemTitles;
    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    private DrawerItemCustomAdapter d_adapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        createDrawer(mDrawerList);
        mTitle = mDrawerTitle = getTitle();



    }

    public  void createDrawer(final ListView dlistView){
        //Adding for navigation drawer
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ObjectDrawerItem[] drawerItem = new ObjectDrawerItem[5];

        drawerItem[0] = new ObjectDrawerItem(R.drawable.ic_action_map, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new ObjectDrawerItem(R.drawable.ic_action_place, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new ObjectDrawerItem(R.drawable.ic_action_view_as_list,mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new ObjectDrawerItem(R.drawable.ic_action_camera,mNavigationDrawerItemTitles[3]);
        drawerItem[4] = new ObjectDrawerItem(R.drawable.ic_action_settings,mNavigationDrawerItemTitles[4]);
        DrawerItemCustomAdapter d_adapter = new DrawerItemCustomAdapter(this, R.layout.drawer_item_row, drawerItem);

        mDrawerList.setAdapter(d_adapter);
        dlistView.setOnItemClickListener (new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                //R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
                hideKeyboard();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }
    //On Navigation drawer item click
    private void selectItem(int position) {
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        Intent intent = new Intent();
        switch (position) {
            case 0:
                intent = new Intent(this,MapsActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this,event_details.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, TescoActivity.class);
                startActivity(intent);;
                break;
            case 3:
                //TODO: link to preference page
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);;
                break;
            case 4:
                //TODO: link to preference page
                intent = new Intent(this, SettingsProd.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        getSupportActionBar().setTitle(mNavigationDrawerItemTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Display the navigation drawer icon on action bar when there state has changed
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
