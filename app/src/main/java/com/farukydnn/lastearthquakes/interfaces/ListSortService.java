package com.farukydnn.lastearthquakes.interfaces;


public interface ListSortService {

    int BY_SEVERITY = 0;
    int BY_DATE = 1;

    int SORT_ASCENDING = 0;
    int SORT_DESCENDING = 1;

    void sortList(int _type, int _method);
    void exchange(int position);
}
