package com.example.project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinkAdapter linkAdapter;
    private List<LinkItem> linkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        recyclerView = findViewById(R.id.recyclerViewInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        linkList = new ArrayList<>();
        // Add your links here
        linkList.add(new LinkItem("https://example.com/sextortion-danger1"));
        linkList.add(new LinkItem("https://example.com/sextortion-danger2"));
        linkList.add(new LinkItem("https://example.com/sextortion-danger3"));

        linkAdapter = new LinkAdapter(linkList);
        recyclerView.setAdapter(linkAdapter);
    }
}
