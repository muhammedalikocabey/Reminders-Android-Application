package com.muhammedalikocabey.reminders;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Context context = this;
    private Toolbar mainSearchToolbar;

    private RecyclerView listsRecyclerView;
    private MainListsAdapter mainListsAdapter;
    private TextView listsArrowTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /////
        ReminderDatabase reminderDaoDB = ReminderDatabase.getDatabase(context);
        ReminderDao reminderDao = reminderDaoDB.reminderDao();

        ///

        ReminderListDatabase reminderListDB = ReminderListDatabase.getDatabase(context);
        ReminderListDao reminderListDao = reminderListDB.reminderListDao();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();


        List<Reminder> listOfReminders = reminderDao.getAllReminders();

        Log.e("MAin", "--------------");

        for(Reminder r: listOfReminders) {
            Log.e("reminder", r.toString() + "\n");
            Log.e("---", "\n");
        }




        List<ReminderList> listOfReminderList = reminderListDao.getAllReminderList();

        for(ReminderList rl: listOfReminderList) {
            Log.e("reminderList", rl.toString() + "\n");
            Log.e("----", "\n");
        }



        Log.e("--", "-------------");








        listsRecyclerView = findViewById(R.id.lists_RecyclearView);
        listsRecyclerView.setHasFixedSize(true);
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mainListsAdapter = new MainListsAdapter(context, reminderListDao.getAllReminderList());

        listsRecyclerView.setAdapter(mainListsAdapter);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame_layout_down_menu, new Fragment_main_down_menu());
        fragmentTransaction.add(R.id.frame_layout_main_categories, new Fragment_main_categories());

        fragmentTransaction.commit();



        listsArrowTextView = findViewById(R.id.list_arrow_TextView);

        listsArrowTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                if(listsArrowTextView.getBackground().getConstantState() ==
                        getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_right_24_grey).getConstantState()) {
                    listsArrowTextView.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24_grey);

                    listsRecyclerView.setVisibility(View.GONE);
                }

                else if(listsArrowTextView.getBackground().getConstantState() ==
                        getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24_grey).getConstantState()) {

                    listsArrowTextView.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_right_24_grey);

                    listsRecyclerView.setVisibility(View.VISIBLE);
                }

            }

        });



        mainSearchToolbar = findViewById(R.id.main_search_toolbar);

        setSupportActionBar(mainSearchToolbar);

        getSupportActionBar().setTitle("");

        mainSearchToolbar.setVisibility(View.GONE);


        /* MAIN SEARCH TOOLBAR - IN THE NEW VERSION
        setSupportActionBar(mainSearchToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainSearchToolbar.setTitle("");
        mainSearchToolbar.setSubtitle("");
        */

        // DARK MODE - IN THE NEW VERSION
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }



    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    // MAIN SEARCH TOOLBAR - IN THE NEW VERSION
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search_menu, menu);

        MenuItem item = menu.findItem(R.id.main_menu_action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Log.e("OnQueryTextSubmit", query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Log.e("onQueryTextChange", newText);
        return true;
    }




}