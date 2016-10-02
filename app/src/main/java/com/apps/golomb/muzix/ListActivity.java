package com.apps.golomb.muzix;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import com.apps.golomb.muzix.Example.StringDataExtractor;
import com.apps.golomb.muzix.Example.StringViewHolder;
import com.apps.golomb.muzix.Example.StringViewHolderGenerator;
import com.apps.golomb.muzix.ExtendedRecycleView.DataExtractor;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleAdapter;
import com.apps.golomb.muzix.ExtendedRecycleView.ExtendedRecycleView;
import com.apps.golomb.muzix.ExtendedRecycleView.IViewHolderGenerator;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        List<String> list = new ArrayList<>();
        for(int i=0;i<10;i++)
            list.add(""+i);
        // data source
        DataExtractor<String,StringViewHolder> dataExtractor = new StringDataExtractor(list);

        IViewHolderGenerator<StringViewHolder> viewHolderGenerator = new StringViewHolderGenerator(this);

        ExtendedRecycleAdapter adapter = new ExtendedRecycleAdapter<>(dataExtractor, viewHolderGenerator);

        ExtendedRecycleView mRecyclerView = (ExtendedRecycleView) findViewById(R.id.recycler_view);
        mRecyclerView.initializeDefault(this,LinearLayoutManager.VERTICAL,adapter);

        //


    }
}
