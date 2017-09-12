package com.farukydnn.lastearthquakes.ui;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.farukydnn.lastearthquakes.interfaces.EarthquakeService;
import com.farukydnn.lastearthquakes.interfaces.ListPageService;
import com.farukydnn.lastearthquakes.interfaces.ListSortService;
import com.farukydnn.lastearthquakes.R;
import com.farukydnn.lastearthquakes.model.Earthquake;
import com.farukydnn.lastearthquakes.adapters.RetrofitAdapter;
import com.farukydnn.lastearthquakes.adapters.EarthquakeAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EarthquakeListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = EarthquakeListFragment.class.getSimpleName();

    private View view;
    private List<Earthquake.EarthquakeData> earthquakes;

    private FadedListView listView;
    private ProgressBar progressBar;
    private LinearLayout failedDialog;
    private TextView errorText;
    private Button buttonTryAgain;

    private Call<Earthquake> earthquakeCall;
    private ListPageService listPageService;
    private boolean isRequestCancelled;

    private int currentPageIndex;

    public EarthquakeListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);

        init();

        return view;
    }

    private void init() {
        listView = view.findViewById(R.id.custom_list);
        listView.setOnItemClickListener(this);
        listView.setFadeColor(Color.WHITE);

        progressBar = view.findViewById(R.id.progressbar_loading);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            progressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        failedDialog = view.findViewById(R.id.list_download_failed_dialog);
        errorText = view.findViewById(R.id.list_error_message);
        buttonTryAgain = view.findViewById(R.id.button_try_again_download);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listPageService = (ListPageService) getActivity();

        if (savedInstanceState != null && savedInstanceState.containsKey("EARTHQUAKE_DATA")) {
            Log.d(TAG, "onActivityCreated: Full Bundle");

            earthquakes = savedInstanceState.getParcelableArrayList("EARTHQUAKE_DATA");
            fillList();

        } else {
            Log.d(TAG, "onActivityCreated: Empty Bundle");

            loadEarthquakes();
        }
    }

    public void loadEarthquakes() {
        resetList();

        if (listPageService.getPage(currentPageIndex).isEmpty()) {

            if (isNetworkConnected()) {
                Log.d(TAG, "Page downloading from web server");

                isRequestCancelled = earthquakeCall != null && earthquakeCall.isCanceled();

                progressBar.setVisibility(View.VISIBLE);

                EarthquakeService earthquakeService = RetrofitAdapter.getEarthquakeService();

                switch (currentPageIndex) {
                    case ListPageService.PAGE_LAST_EARTHQUAKES:
                        Log.d(TAG, "Downloading last earthquakes");
                        earthquakeCall = earthquakeService.getLastEarthquakes();
                        break;

                    case ListPageService.PAGE_IMPORTANT_EARTHQUAKES:
                        Log.d(TAG, "Downloading important earthquakes");
                        earthquakeCall = earthquakeService.getImportantEarthquakes();
                        break;
                }

                earthquakeCall.enqueue(new Callback<Earthquake>() {
                    @Override
                    public void onResponse(@NonNull Call<Earthquake> call, @NonNull Response<Earthquake> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Response is successful! Page successfully downloaded");

                            earthquakes = response.body().getEarthquakeList();

                            formatList();

                            listPageService.updatePage(currentPageIndex, earthquakes);

                        } else {
                            Log.d(TAG, "Request failed because of unexpected response");

                            showError(R.string.list_data_download_failed);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Earthquake> call, @NonNull Throwable t) {
                        if (isRequestCancelled)
                            Log.d(TAG, "Request cancelled");

                        else {
                            Log.d(TAG, "Request failed. Check connection");

                            showError(R.string.no_connection);
                        }
                    }
                });

            } else {
                Log.d(TAG, "Device isn't connected to network!");

                showError(R.string.no_connection);
            }

        } else {
            Log.d(TAG, "Page loaded from cache!");

            earthquakes = listPageService.getPage(currentPageIndex);
            fillList();
        }
    }

    private void resetList() {
        earthquakes = null;

        failedDialog.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        listView.setAdapter(null);

        currentPageIndex = listPageService.getCurrentPageIndex();

        if (earthquakeCall != null)
            earthquakeCall.cancel();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showError(int errorMessage) {
        Log.d(TAG, "showError");

        progressBar.setVisibility(View.GONE);

        showTryAgainDialog(errorMessage);
    }

    private void showTryAgainDialog(int errorMessage) {
        failedDialog.setVisibility(View.VISIBLE);

        errorText.setText(errorMessage);

        buttonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAgainDownload();
            }
        });
    }

    private void tryAgainDownload() {
        failedDialog.setVisibility(View.GONE);

        loadEarthquakes();
    }

    private void formatList() {

        Log.d(TAG, "Formatting incoming date and time");

        for (Earthquake.EarthquakeData earthquakeData : earthquakes) {
            String dateTime = earthquakeData.getDateTime();
            String parseFormat = "dd.MM.yyyy HH:mm:ss";
            String getDate = "??.??.????", getTime = "??.??";

            if (currentPageIndex == ListPageService.PAGE_IMPORTANT_EARTHQUAKES)
                parseFormat = "dd.MM.yyyy HH:mm";

            try {
                Date date = new SimpleDateFormat(parseFormat, Locale.getDefault())
                        .parse(dateTime);

                getDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(date);

                getTime = new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(date);


            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "Date parsing operation failed! Couldn't get the earthquake's date info.");

            } finally {
                earthquakeData.setDate(getDate);
                earthquakeData.setTime(getTime);
                earthquakeData.setRealDate(getDate, getTime);
            }
        }

        Log.d(TAG, "Date formatting is successful!");

        fillList();
    }

    private void fillList() {
        Log.d(TAG, "fillList");

        failedDialog.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        listView.setAdapter(new EarthquakeAdapter(getActivity(), earthquakes));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick");

        String lat = earthquakes.get(i).getLat();
        String lon = earthquakes.get(i).getLng();
        String label = earthquakes.get(i).getLocation();

        Uri location = Uri.parse("geo:0,0?q=" + lat + "," + lon + "(" + label + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, location);

        if (isIntentSafe(intent)) {
            Log.d(TAG, "onItemClick: Starting map intent");
            startActivity(intent);
        } else {
            Log.d(TAG, "onItemClick: Map application not found!");

            Uri webpage = Uri.parse("http://www.google.com/maps/place/" + lat + "," + lon);
            intent = new Intent(Intent.ACTION_VIEW, webpage);

            if (isIntentSafe(intent)) {
                Log.d(TAG, "onItemClick: Starting browser intent");
                startActivity(intent);
            } else {
                Log.d(TAG, "onItemClick: Browser application not found!");
                Toast.makeText(getActivity(), R.string.no_intent_application, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isIntentSafe(Intent intent) {
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        return activities.size() > 0;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        if (earthquakes != null)
            outState.putParcelableArrayList("EARTHQUAKE_DATA", (ArrayList<? extends Parcelable>) earthquakes);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        if (earthquakeCall != null)
            earthquakeCall.cancel();

        super.onDestroy();
    }


    public class ListActions implements ListSortService {
        @Override
        public void sortList(int _type, int _method) {
            if (earthquakes != null) {
                boolean isAscending = _method == ListSortService.SORT_ASCENDING;
                boolean isSwapping = false;

                /* Insertion Sort */
                for (int i = 1; i < earthquakes.size(); i++) {
                    for (int j = i; j > 0; j--) {

                        if (isAscending) {
                            switch (_type) {
                                case ListSortService.BY_SEVERITY:
                                    isSwapping = earthquakes.get(j).getSeverity() < earthquakes.get(j - 1).getSeverity();
                                    break;

                                case ListSortService.BY_DATE:
                                    isSwapping = earthquakes.get(j).getRealDate().before(earthquakes.get(j - 1).getRealDate());
                                    break;
                            }
                        } else {
                            switch (_type) {
                                case ListSortService.BY_SEVERITY:
                                    isSwapping = earthquakes.get(j).getSeverity() > earthquakes.get(j - 1).getSeverity();
                                    break;

                                case ListSortService.BY_DATE:
                                    isSwapping = earthquakes.get(j).getRealDate().after(earthquakes.get(j - 1).getRealDate());
                                    break;
                            }
                        }

                        if (isSwapping)
                            exchange(j);
                        else
                            break;
                    }
                }

                listPageService.updatePage(currentPageIndex, earthquakes);
                fillList();
            }
        }

        @Override
        public void exchange(int position) {
            Earthquake.EarthquakeData temp = earthquakes.get(position - 1);
            earthquakes.set(position - 1, earthquakes.get(position));
            earthquakes.set(position, temp);
        }
    }


}
