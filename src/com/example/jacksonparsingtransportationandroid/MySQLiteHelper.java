package com.example.jacksonparsingtransportationandroid;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

@SuppressLint("SimpleDateFormat")
public class MySQLiteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	public final String TAG_BOOKMARKS = "bookmarks";
	public final String TAG_CATEGORY = "category";
	public final String TAG_COMPANY = "company";
	public final String TAG_DISTRICT = "district";
	public final String TAG_LANGUAGE = "language";
	public final String TAG_NODE = "node";
	public final String TAG_ROUTE = "route";
	public final String TAG_ROUTE_NODE = "route_node";
	public final String TAG_SERVICE = "service";
	public final String TAG_TYPE = "type";
	public final String TAG_TRANSPORTATION = "transportation";
	public final String TAG_SCHEDULE = "schedule";
	public final String TAG_TRANSPORTATION_DETAILS = "transportation_details";

	// added for airlines transport
	public final String TAG_AIRLINES = "airlines";
	public final String TAG_AIRLINES_CLASSES = "airlines_classes";
	public final String TAG_FACILITIES = "facilities";
	public final String TAG_PRICING = "pricing";

	String DB_PATH = "null";

	private static String DB_NAME = "travelguide.db";

	public MySQLiteHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		DB_PATH = context.getDatabasePath(DB_NAME).getPath();

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {

		SQLiteDatabase checkDB = null;
		try {
			String myPath = DB_PATH;
			File file = new File(myPath);
			if (file.exists() && !file.isDirectory()) {

				checkDB = SQLiteDatabase.openDatabase(myPath, null,
						SQLiteDatabase.OPEN_READONLY);
			}
		} catch (SQLiteException e) {

		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks(id integer primary key autoincrement,transportation_id integer);");
		db.execSQL("CREATE TABLE category (id integer primary key autoincrement, name text);");
		db.execSQL("CREATE TABLE company(id integer primary key autoincrement ,name text,address text, phone text);");
		db.execSQL("CREATE TABLE district(id integer primary key autoincrement, name text);");
		db.execSQL("CREATE TABLE language(id integer primary key autoincrement, name text);");
		db.execSQL("CREATE TABLE node (id integer primary key autoincrement, name text, lat text, lon text, specialities text, district_id integer, foreign key(district_id) references district(id));");
		db.execSQL("CREATE TABLE route(id integer primary key autoincrement, name text,number text,full_route text);");
		db.execSQL("CREATE TABLE route_node(id integer primary key autoincrement ,route_id integer, node_id integer,route_count integer, foreign key(route_id) references route(id), foreign key(node_id) references node(id));");
		db.execSQL("CREATE TABLE service(id integer primary key autoincrement, name text);");
		db.execSQL("CREATE TABLE transportation(id integer primary key autoincrement,route_id integer,type_id integer,company_id integer,category_id integer,contact_name text,contact_phone text,depart_time text,arrival_time text,interval text,return_type integer,cost integer,remarks text,foreign key(route_id) references route_node(id), foreign key(type_id) references type(id), foreign key(company_id) references company(id),foreign key(category_id) references category(id));");
		db.execSQL("CREATE TABLE type(id integer primary key autoincrement,name text);");
		db.execSQL("CREATE TABLE schedule (id integer primary key autoincrement, transportation_id integer, schedule_type integer,sun integer,mon integer,tues integer, wednes integer, thurs integer, fri integer, satur integer );");
		db.execSQL("CREATE TABLE transportation_details(id integer primary key autoincrement,transportation_id integer,node_id integer,arrival_time text, depart_time text, details text,day text);");

		// added later for airlines transport
		db.execSQL("CREATE TABLE airlines (id integer primary key autoincrement, name text);");
		db.execSQL("CREATE TABLE airlines_classes (id integer primary key autoincrement, airline_id integer, foreign key(airline_id) references airlines(id));");
		db.execSQL("CREATE TABLE facilities (id integer primary key autoincrement, airlineclass_id integer, foreign key(airlineclass_id) references airlines_classes(id));");
		db.execSQL("CREATE TABLE pricing (id integer primary key autoincrement, airlineclass_id integer, sourceId integer, destId integer, rate integer, foreign key(airlineclass_id) references airlines_classes(id), foreign key(sourceId) references node(id), foreign key(destId) references node(id));");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		dropAllTables(db);
		onCreate(db);

	}

	private void dropAllTables(SQLiteDatabase db) {
		db.execSQL("delete from " + DB_NAME);
	}

	public boolean insertIntoTable(String tableName, ContentValues contentValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		@SuppressWarnings("unused")
		Long id = db.insert(tableName, null, contentValue);

		return true;
	}

	public Cursor getData(String query) {

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery(query, null);
		return res;
	}

	@SuppressLint("NewApi")
	public Cursor getData(String settingTables, String[] selectcolumns,
			String whereString) {
		SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();

		_QB.setTables(settingTables);
		_QB.appendWhere(whereString);
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor res = _QB.query(db, selectcolumns, null, null, null, null, null);
		@SuppressWarnings("unused")
		String sql = _QB
				.buildQuery(selectcolumns, null, null, null, null, null);

		return res;
	}

	public int numberOfRows(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, tableName);
		return numRows;
	}

	public boolean updateContact(String tableName, Integer id,
			ContentValues contentValues) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(tableName, contentValues, "id = ? ",
				new String[] { Integer.toString(id) });
		return true;
	}

	public Integer deleteContact(String tableName, Integer id) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(tableName, "id = ? ",
				new String[] { Integer.toString(id) });
	}

	public ArrayList<ArrayList<String>> getAllColumnsData(String query)
			throws IOException {

		ArrayList<ArrayList<String>> array_list = new ArrayList<ArrayList<String>>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor res = db.rawQuery(query, null);

		res.moveToFirst();
		int count = res.getColumnCount();
		while (res.isAfterLast() == false) {
			ArrayList<String> array_list1 = new ArrayList<String>();

			for (int i = 0; i < count; i++) {
				array_list1.add(res.getString(i));
			}
			array_list.add(array_list1);
			res.moveToNext();
		}

		return array_list;

	}

	public int getIdForName(String tablename, String name) {
		int id = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select id from " + tablename
				+ " where name='" + name + "'", null);
		res.moveToFirst();
		@SuppressWarnings("unused")
		int count = res.getColumnCount();

		while (res.isAfterLast() == false) {
			id = res.getInt(0);
			res.moveToNext();
		}

		return id;
	}

	public void createInsertQuery(String tableName, JSONArray jsonarray)
			throws ParseException {

		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm");
		for (int i = 0; i < jsonarray.length(); i++) {
			try {
				JSONObject jsonObject = jsonarray.getJSONObject(i);
				HashMap<String, String> hashMap = new HashMap<String, String>();
				Iterator<?> keys = jsonObject.keys();
				ContentValues contentValue = new ContentValues();

				while (keys.hasNext()) {
					String key = (String) keys.next();
					String value = jsonObject.getString(key).trim();
					hashMap.put(key, value);
					contentValue.put(key, value);

				}

				if (tableName == TAG_TRANSPORTATION) {
					String arrival = contentValue.getAsString("arrival_time");
					String depart = contentValue.getAsString("depart_time");
					Date departdate = simpledateformat.parse(depart);
					Date arrivaldate = simpledateformat.parse(arrival);
					long difference = arrivaldate.getTime()
							- departdate.getTime();

					@SuppressWarnings("unused")
					int interval = (int) difference / 1000;

				}

				String valofis_delted = contentValue.getAsString("is_deleted");

				if (valofis_delted.equals("0")) {

					contentValue.remove("added_date");
					contentValue.remove("is_deleted");
					insertIntoTable(tableName, contentValue);

				}

				arrayList.add(hashMap);

			} catch (JSONException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean checkIfDataExists(String query) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery(query, null);
		res.moveToFirst();
		int count = res.getCount();

		if (count > 0)
			return true;
		else
			return false;

	}
}