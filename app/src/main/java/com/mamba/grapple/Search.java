package com.mamba.grapple;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;


public class Search extends ExpandableListActivity {

    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_search);

        ExpandableListView expandableList = getExpandableListView();

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        setGroupParents();
        setChildData();

        ExpandableAdapter adapter = new ExpandableAdapter(parentItems, childItems);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(this);

    }

    public void setGroupParents() {
        parentItems.add("Computer Sciences");
        parentItems.add("Mathematics");
        parentItems.add("Physics");
        parentItems.add("etc.");
    }

    public void setChildData() {

        // Computer Sciences
        ArrayList<String> child = new ArrayList<String>();
        child.add("CS302- Intro to Java");
        //child.add("");
        child.add("CS540- Intro to AI");
        child.add("CS577- Into to Algorithms");
        childItems.add(child);

        // Mathematics
        child = new ArrayList<String>();
        child.add("MATH221-Calc I");
        childItems.add(child);

        // Physics
        child = new ArrayList<String>();
        child.add("PHYS201- Physics I");
        childItems.add(child);

        // etc.
        child = new ArrayList<String>();
        child.add("test");
        childItems.add(child);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
