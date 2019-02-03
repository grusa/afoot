package ru.ai.afoot;

import android.app.ListActivity;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class TrainingList extends ListActivity {
    private ArrayAdapter<String> mAdapter;
    public static ArrayList<String> trainingList;
    public static ArrayList<Integer> trainingListID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        mAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                TrainingAdapter.trainingList);
        setListAdapter(mAdapter);
        Toast.makeText(getApplicationContext(),"To delete item make long click on it",Toast.LENGTH_LONG).show();
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"onItemClick: "+position,Toast.LENGTH_LONG).show();
            }
        };
        AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TrainingAdapter trainingAdapter = new TrainingAdapter();
                SQLiteDatabase db = trainingAdapter.open(getApplicationContext());
                try {
                    db.execSQL("DELETE FROM TRAININGS where _ID ="+TrainingAdapter.trainingListID.get(position));
                } catch (SQLException e) {
                    Toast.makeText(getApplicationContext(),"Delete error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
                db.close();
                Toast.makeText(getApplicationContext(),"Item deleted",Toast.LENGTH_LONG).show();
                return false;
            }
        };
        getListView().setOnItemClickListener(onItemClickListener);
        getListView().setOnItemLongClickListener(onItemLongClickListener);
    }
  /*  @Override
    public void onListItemClick(ListView listView,View v,int position,long id ) {
        TrainingAdapter trainingAdapter = new TrainingAdapter();
        SQLiteDatabase db = trainingAdapter.open(this);
        try {
        db.execSQL("DELETE FROM TRAININGS where _ID ="+TrainingAdapter.trainingListID.get(position));
        } catch (SQLException e) {
            Toast.makeText(this,"Delete error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        db.close();
        onContentChanged();
    }
*/
    //TODO Long TAB - delete row
    //TODO short TAB - go to detail activity
}
