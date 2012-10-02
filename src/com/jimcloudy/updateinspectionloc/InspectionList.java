package com.jimcloudy.updateinspectionloc;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class InspectionList extends Activity{
	ListView listInspectionList;
	InspectionData inspectionData;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	InspectionApplication app;
	int selected;
	
	
	static final String[] FROM = { InspectionData.C_ID, InspectionData.C_NAME, InspectionData.C_NAME2 };
	static final int[] TO = {R.id.textPolicy, R.id.textInsured, R.id.textInsured2};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspection_list);
        listInspectionList = (ListView) findViewById(R.id.listInspectionList);
        //listInspectionList.setOnItemClickListener(this);
        inspectionData = new InspectionData(this);
        app = new InspectionApplication();
        if(!app.isGetInspectionRunning()){
            startService(new Intent(this,GetInspections.class));
            app.setGetInspectionRunning(true);
        }
        selected = 0;
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		this.setupList();		
	}
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if(app.isGetInspectionRunning()){
    		stopService(new Intent(this,GetInspections.class));
    		app.setGetInspectionRunning(false);
    	}
    	
    	this.inspectionData.close();
    }
    
    private void setupList(){
    	cursor = inspectionData.getInspections();
    	startManagingCursor(cursor);
    	
    	adapter = new SimpleCursorAdapter(this,R.layout.row, cursor, FROM, TO);
    	
    	listInspectionList.setAdapter(adapter);
    	
    	if(selected < adapter.getCount()){
    		listInspectionList.setSelection(selected);
    	}
    	else{
    		listInspectionList.setSelection(selected-1);
    	}
    	
    	listInspectionList.setOnItemClickListener(new OnItemClickListener() {
    		   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		    Cursor row = (Cursor) parent.getItemAtPosition(position);
    		    String policy = row.getString(row.getColumnIndex("_id"));
    		    String name = row.getString(row.getColumnIndex("name"));
    		    String name2 = row.getString(row.getColumnIndex("name2"));
    		    String address = row.getString(row.getColumnIndex("address"));
    		    String address2= row.getString(row.getColumnIndex("address2"));
    		    String cityst = row.getString(row.getColumnIndex("cityst"));
    		    String zip = row.getString(row.getColumnIndex("zip"));
    		    String phone = row.getString(row.getColumnIndex("phone"));    	
    		    Intent i = new Intent(getApplicationContext(),Inspection.class);
    		    i.putExtra("policy", policy);
    		    i.putExtra("name", name);
    		    i.putExtra("name2", name2);    		    
    		    i.putExtra("address", address);
    		    i.putExtra("address2", address2);
    		    i.putExtra("cityst", cityst);
    		    i.putExtra("zip",zip);
    		    i.putExtra("phone", phone);
    		    startActivity(i);
    		    selected = position;
    		   }
    		  });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
    	return true;
    }

}
