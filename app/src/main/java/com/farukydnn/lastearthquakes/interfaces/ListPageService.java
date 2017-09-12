package com.farukydnn.lastearthquakes.interfaces;


import java.util.ArrayList;
import java.util.List;

import com.farukydnn.lastearthquakes.model.Earthquake;

public interface ListPageService {
    List<List<Earthquake.EarthquakeData>> listViewPages = new ArrayList<>();

    int PAGE_LAST_EARTHQUAKES = 0;
    int PAGE_IMPORTANT_EARTHQUAKES = 1;

    void createPageList(int pageCount);

    List<Earthquake.EarthquakeData> getPage(int pageNumber);

    void updatePage(int pageNumber, List<Earthquake.EarthquakeData> page);

    int getCurrentPageIndex();

    void setCurrentPageIndex(int pageIndex);
}
