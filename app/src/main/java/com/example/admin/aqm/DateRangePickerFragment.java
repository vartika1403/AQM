package com.example.admin.aqm;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TabWidget;

public class DateRangePickerFragment extends DialogFragment implements View.OnClickListener {
    private static final String LOG_TAG = DateRangePickerFragment.class.getSimpleName();
    private OnDateRangeSelectedListener onDateRangeSelectedListener;

    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    boolean is24HourMode;
    boolean isDoneClicked;

    public DateRangePickerFragment() {
        // Required empty public constructor
    }

    public static DateRangePickerFragment newInstance(OnDateRangeSelectedListener callback,
                                                      boolean is24HourMode) {
        DateRangePickerFragment fragment = new DateRangePickerFragment();
        fragment.initialize(callback, is24HourMode);
        return fragment;
    }

    public void initialize(OnDateRangeSelectedListener callback,
                           boolean is24HourMode) {
        onDateRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_range_picker, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);
        Button butSetDateRange = (Button) view.findViewById(R.id.but_set_time_range);
        startDatePicker = (DatePicker) view.findViewById(R.id.start_date_picker);
        endDatePicker = (DatePicker) view.findViewById(R.id.end_date_picker);
        butSetDateRange.setOnClickListener(this);
        tabHost.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec startDatePage = tabHost.newTabSpec("start");
        startDatePage.setContent(R.id.start_date_group);
        startDatePage.setIndicator("START DATE");

        TabHost.TabSpec endDatePage = tabHost.newTabSpec("end");
        endDatePage.setContent(R.id.end_date_group);
        endDatePage.setIndicator("END DATE");
        tabHost.addTab(startDatePage);
        tabHost.addTab(endDatePage);

        TabWidget widget = tabHost.getTabWidget();

        for (int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);
            v.setBackgroundResource(R.drawable.tab_bg_selector);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View view) {
        isDoneClicked = true;
        dismiss();
        onDateRangeSelectedListener.onDateRangeSelected(startDatePicker.getDayOfMonth(),
                startDatePicker.getMonth(), startDatePicker.getYear(),
                endDatePicker.getDayOfMonth(), endDatePicker.getMonth(), endDatePicker.getYear(), isDoneClicked);
    }

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay,
                                 int endMonth, int endYear, boolean isDoneClicked);
    }
}
