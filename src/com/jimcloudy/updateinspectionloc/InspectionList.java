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
    	
    	listInspectionList.setOnItemClickListener(new OnItemClickListener() {
    		   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		    Cursor row = (Cursor) parent.getItemAtPosition(position);
    		    String policy = row.getString(row.getColumnIndex("_id"));
    		    String name = row.getString(row.getColumnIndex("name"));
    		    String name2 = row.getString(row.getColumnIndex("name2"));
    		    String address = row.getString(row.getColumnIndex("address"));
    		    String address2= row.getString(row.getColumnIndex("address2"));
    		    Intent i = new Intent(getApplicationContext(),Inspection.class);
    		    i.putExtra("policy", policy);
    		    i.putExtra("name", name);
    		    i.putExtra("name2", name2);    		    
    		    i.putExtra("address", address);
    		    i.putExtra("address2", address2);
    		    startActivity(i);
    		   }
    		  });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { 
    	startActivity(new Intent(this,Inspection.class));
    	return true;
    }

}
