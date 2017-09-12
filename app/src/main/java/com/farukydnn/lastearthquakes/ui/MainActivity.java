package com.farukydnn.lastearthquakes.ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import com.farukydnn.lastearthquakes.interfaces.ListPageService;
import com.farukydnn.lastearthquakes.interfaces.ListSortService;
import com.farukydnn.lastearthquakes.model.Earthquake;
import com.farukydnn.lastearthquakes.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, ListPageService {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<ImageButton> footerButtons;

    private int currentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate: Empty Bundle");

            createPageList(footerButtons.size());
            footerButtons.get(0).callOnClick();

        } else {
            Log.d(TAG, "onCreate: Full Bundle");

            setCurrentPageIndex(savedInstanceState.getInt("CURRENT_PAGE"));

            int selectedButtonInd = footerButtons.get(getCurrentPageIndex()).getId();
            selectFooterButton(selectedButtonInd);
        }
    }

    private void init() {
        footerButtons = new ArrayList<>();
        ImageButton button_lastEarthquakes, button_ImportantEarthquakes;

        button_lastEarthquakes = (ImageButton) findViewById(R.id.button_lastEarthquakes);
        initFooterButton(button_lastEarthquakes);

        button_ImportantEarthquakes = (ImageButton) findViewById(R.id.button_ImportantEarthquakes);
        initFooterButton(button_ImportantEarthquakes);
    }

    private void initFooterButton(ImageButton button) {
        button.setOnClickListener(this);
        footerButtons.add(button);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Action Button Clicked");
        switch (item.getItemId()) {
            case R.id.action_sort:
                showPopup();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showPopup() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.action_sort));
        popupMenu.getMenuInflater().inflate(R.menu.popup_sort_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        EarthquakeListFragment earthquakeListFragment =
                (EarthquakeListFragment) getSupportFragmentManager()
                        .findFragmentByTag("EARTHQUAKE_LIST_FRAGMENT");

        EarthquakeListFragment.ListActions listActions = null;

        if (earthquakeListFragment != null)
            listActions = earthquakeListFragment.new ListActions();

        switch (item.getItemId()) {
            case R.id.sort_large_to_small:
                Log.d(TAG, "Sorting large to small");

                if (listActions != null)
                    listActions.sortList(ListSortService.BY_SEVERITY, ListSortService.SORT_DESCENDING);

                return true;

            case R.id.sort_small_to_large:
                Log.d(TAG, "Sorting small to large");

                if (listActions != null)
                    listActions.sortList(ListSortService.BY_SEVERITY, ListSortService.SORT_ASCENDING);

                return true;

            case R.id.sort_newer_to_older:
                Log.d(TAG, "Sorting newer to older");

                if (listActions != null)
                    listActions.sortList(ListSortService.BY_DATE, ListSortService.SORT_DESCENDING);

                return true;

            case R.id.sort_older_to_newer:
                Log.d(TAG, "Sorting older to newer");

                if (listActions != null)
                    listActions.sortList(ListSortService.BY_DATE, ListSortService.SORT_ASCENDING);

                return true;

            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (!view.isSelected()) {
            Log.d(TAG, "Footer Button Clicked");

            selectFooterButton(viewId);

            switch (viewId) {
                case R.id.button_lastEarthquakes:
                    setCurrentPageIndex(PAGE_LAST_EARTHQUAKES);
                    showEarthquakeListFragment();
                    break;

                case R.id.button_ImportantEarthquakes:
                    setCurrentPageIndex(PAGE_IMPORTANT_EARTHQUAKES);
                    showEarthquakeListFragment();
                    break;
            }
        }
    }

    private void showEarthquakeListFragment() {
        EarthquakeListFragment earthquakeListFragment =
                (EarthquakeListFragment) getSupportFragmentManager()
                        .findFragmentByTag("EARTHQUAKE_LIST_FRAGMENT");

        if (earthquakeListFragment == null) {
            Log.d(TAG, "showEarthquakeListFragment: Creating new fragment");

            earthquakeListFragment = new EarthquakeListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, earthquakeListFragment, "EARTHQUAKE_LIST_FRAGMENT")
                    .commit();
        } else {
            Log.d(TAG, "showEarthquakeListFragment: Updating existing fragment");

            earthquakeListFragment.loadEarthquakes();
        }
    }

    private void selectFooterButton(int viewId) {
        for (ImageButton button : footerButtons) {
            if (button.getId() == viewId)
                button.setSelected(true);
            else
                button.setSelected(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putInt("CURRENT_PAGE", getCurrentPageIndex());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        if (getCurrentPageIndex() != PAGE_LAST_EARTHQUAKES)
            footerButtons.get(PAGE_LAST_EARTHQUAKES).callOnClick();
        else
            super.onBackPressed();
    }

    @Override
    public void createPageList(int pageCount) {
        Log.d(TAG, "createNewList");

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            listViewPages.add(new ArrayList<Earthquake.EarthquakeData>());
        }
    }

    @Override
    public List<Earthquake.EarthquakeData> getPage(int pageNumber) {
        return listViewPages.get(pageNumber);
    }

    @Override
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    @Override
    public void setCurrentPageIndex(int pageIndex) {
        currentPageIndex = pageIndex;
    }

    @Override
    public void updatePage(int pageNumber, List<Earthquake.EarthquakeData> page) {
        Log.d(TAG, "Save current page to cache");

        listViewPages.set(pageNumber, page);
    }
}
