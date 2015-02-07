package com.contactmanager.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.contactmanager.controller.TextFileWriter;
import com.contactmanager.model.Contact;
import com.example.contacts.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * ContactViewer activity is used to view a saved contact. The user can modify, delete, call or email a contact from this activity.
 */
@SuppressLint("NewApi") public class ContactViewer extends Activity{
	
	ArrayList<Contact> contactsList;
	Contact selectedContact;
	int selectedItemPosition;
	TextView nameView;
	ArrayList<String[]> fieldAndValue;
	Context con;
	ListView list;
	ContactViewerAdapter adapter;
	TextFileWriter fileWriter;
	
	/**
	 * The onCreate method initializes the view and populates appropriate data to be viewed.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		con=this;
		setContentView(R.layout.viewer_contact);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);
		contactsList=(ArrayList<Contact>) getIntent().getSerializableExtra("contacts_list");
		fileWriter=new TextFileWriter(Environment.getExternalStorageDirectory()+"/contacts.txt", contactsList);
		selectedItemPosition=getIntent().getIntExtra("index", -1);
		selectedContact=contactsList.get(selectedItemPosition);
		displayData();

		 list = (ListView)findViewById(R.id.contactViewerList);
		 adapter = new ContactViewerAdapter(con,R.layout.viewer_contact, fieldAndValue);
		    list.setAdapter(adapter);
		addListClickListener(list);
	}
	
	/**
	 * Populates the screen with the selected contact's details.
	 */
	void displayData(){
		nameView=(TextView) findViewById(R.id.nameview);
		if(contactsList.get(selectedItemPosition).getFirstName().equals("\u0000")&&contactsList.get(selectedItemPosition).getLastName().equals("\u0000")){
			nameView.setTextColor(Color.parseColor("#FFFFFF"));
			nameView.setText("<No name>");
		}
		else{
			nameView.setText(contactsList.get(selectedItemPosition).getFirstName()+" "+contactsList.get(selectedItemPosition).getLastName());
		}
		fieldAndValue=new ArrayList<String[]>();
		String[] phone=new String[3];
		phone[0]="Phone";
		phone[1]=contactsList.get(selectedItemPosition).getPhoneNo();
		phone[2]="phone";
		String[] email=new String[3];
		email[0]="Email";
		email[1]=contactsList.get(selectedItemPosition).getEmail();
		email[2]="email";
		fieldAndValue.add(phone);
		fieldAndValue.add(email);
		
	}
	
	/**
	 * The phone number and email are shown in a ListView in the contact viewer screen. 
	 * This method adds a listener to the list view so that appropriate action(dialpad or email) is executed when a list item is clicked.
	 * @param list the list to which listener has to be added
	 */
	void addListClickListener(final ListView list){
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long index) {
				// TODO Auto-generated method stub
				String[] selectedItem= (String[]) list.getItemAtPosition(position);
				if(position==0){	//open dialer
					Intent i = new Intent(Intent.ACTION_DIAL);

					i.setData(Uri.parse("tel:"+selectedItem[1]));
					try{
						startActivity(i);
					}
					catch(android.content.ActivityNotFoundException ex){
						Toast.makeText(ContactViewer.this, "Your device does not have calling facility.", Toast.LENGTH_SHORT).show();
					}
				}
				else if(position==1){	//email
					Intent i = new Intent(Intent.ACTION_SEND);
					String[] TO={selectedItem[1]};
					i.setType("plain/text");//message/rfc822
					
					i.putExtra(Intent.EXTRA_EMAIL  , TO);
					i.putExtra(Intent.EXTRA_SUBJECT, "");
					i.putExtra(Intent.EXTRA_TEXT   , "");
					try {
					    startActivity(Intent.createChooser(i, "Send mail"));
					} catch (android.content.ActivityNotFoundException ex) {
					    Toast.makeText(ContactViewer.this, "You do not have any email clients installed.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}
	
	/**
	 * populates the action bar
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewer_contact_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	/**
	 * handling action bar button clicks
	 * The buttons in this activity's action bar are app icon back button, edit and delete buttons.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
        
		case android.R.id.home:	//Home Up button
        	//action
        	NavUtils.navigateUpFromSameTask(this);
        	return true;
        
        case R.id.action_edit: //edit button
        	//Open editor
        	Intent editorIntent = new Intent(ContactViewer.this, ContactEditor.class);
        	editorIntent.putExtra("mode","edit");
        	editorIntent.putExtra("contacts_list",contactsList);
        	editorIntent.putExtra("index", selectedItemPosition);
        	finish();
        	ContactViewer.this.startActivity(editorIntent);
        	return true;
        
        case R.id.action_delete: //delete button
        	//Delete item, update file and close contact view.
        	Log.d("removed rec",contactsList.get(getIndex(selectedContact)).getFirstName());
        	contactsList.remove(getIndex(selectedContact));
        	sortRecords(contactsList);
        	Log.d("sorted", "sorted"+contactsList.size());
        	try {
				fileWriter.reWriteFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	finish();
        	Toast.makeText(getApplicationContext(), "Contact Deleted", Toast.LENGTH_LONG).show();
        	return true;
       
        default:
        	return super.onOptionsItemSelected(item);
        }
	}
	
	/**
	 * sorts the arrayList of contacts
	 * @param recordsList the arrayList to sort
	 */
	void sortRecords(ArrayList<Contact> recordsList){
		Collections.sort(recordsList,new Comparator<Contact>() {
            public int compare(Contact contact1, Contact contact2) {
            	if(contact1.getFirstName().equals("\u0000")&&contact1.getLastName().equals("\u0000")){
            		return contactsList.size()+1;
            	}
            	else{
            		return contact1.getFirstName().toUpperCase().compareTo(contact2.getFirstName().toUpperCase());
            	}
            }
        });
	}
	
	/**
	 * gets the index of a specified contact in the arrayList
	 * @param c the contact whose index i to be found
	 * @return index if contact is found, -1 otherwise
	 */
	int getIndex(Contact c){
		for(int i=0;i<contactsList.size();i++){
			if(c.isEqualTo(contactsList.get(i))){
				Log.d("found index",i+" ");
				return i;
			}
		}
		return -1;
	}
}
