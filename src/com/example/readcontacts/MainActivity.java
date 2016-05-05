package com.example.readcontacts;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	private ListView lvList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lvList = (ListView) findViewById(R.id.lv_list);
		ArrayList<HashMap<String,String>> readContact = readContact();
		lvList.setAdapter(new SimpleAdapter(this, readContact, R.layout.contact_list_item, new String[] {"name", "phone"}, new int[] {R.id.tv_name, R.id.tv_phone}));
		
	}
	private ArrayList<HashMap<String, String>> readContact() {
		
		
		//然后，根据mimetype来区分哪个是联系人，哪个是电话号码
		//首先从raw_contact中读取联系人的id（“contact_id”）
		Uri rawContactUri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		Cursor rawContactsCursor = getContentResolver().query(rawContactUri, new String[] {"contact_id"}, null, null, null);
		if(rawContactsCursor != null) {
			while(rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				//其次根据contact_id在data表中查询出相应的电话号码和联系人名称  实际上查询的是视图view_data
				Cursor dataCursor = getContentResolver().query(dataUri, new String[] {"data1", "mimetype"}, "contact_id=?", new String[] {contactId}, null);
				if(dataCursor !=null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						if("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						}else if("vnd.android.cursor.item/name".equals(mimetype)) {
							map.put("name", data1);
						}
					}
					list.add(map);
					dataCursor.close();
				}
			}
			rawContactsCursor.close();
		}
		return list;
	}
	
}
