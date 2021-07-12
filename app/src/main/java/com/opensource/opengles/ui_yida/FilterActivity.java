package com.opensource.opengles.ui_yida;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.opensource.opengles.R;
import com.opensource.opengles.databinding.ActivityFilterBinding;
import com.opensource.opengles.ui_yida.bean.FilterBean;

import java.util.ArrayList;
import java.util.List;

import x.com.view.Recyclerview.SpacesItemDecoration;

public class FilterActivity extends AppCompatActivity {
    ActivityFilterBinding binding;
    private List<FilterBean> list = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.filterListView.addItemDecoration(new SpacesItemDecoration(20));
        binding.filterListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        addFilter(R.mipmap.detail_filter_n, "无");
        addFilter(R.mipmap.detail_filter_n, "1");
        addFilter(R.mipmap.detail_filter_n, "2");
        addFilter(R.mipmap.detail_filter_n, "3");
        addFilter(R.mipmap.detail_filter_n, "4");
        addFilter(R.mipmap.detail_filter_n, "5");
        addFilter(R.mipmap.detail_filter_n, "6");
        addFilter(R.mipmap.detail_filter_n, "7");
        addFilter(R.mipmap.detail_filter_n, "8");
        addFilter(R.mipmap.detail_filter_n, "9");
        FilterSelectAdapter adapter = new FilterSelectAdapter(list);
        adapter.setOnItemClickListener(new FilterSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        binding.filterListView.setAdapter(adapter);
    }

    private void addFilter(int img, String name) {
        FilterBean filterBean1 = new FilterBean();
        filterBean1.setName(name);
        filterBean1.setImgResource(img);
        list.add(filterBean1);
    }
}
