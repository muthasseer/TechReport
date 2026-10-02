package com.techreport.app

import android.content.*
import android.database.sqlite.*

class AppDb(ctx:Context):SQLiteOpenHelper(ctx,"techreport.db",null,1){
 override fun onCreate(d:SQLiteDatabase){
  d.execSQL("CREATE TABLE sites(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,job TEXT,customer TEXT,person TEXT,phone TEXT,address TEXT)")
  d.execSQL("CREATE TABLE services(id INTEGER PRIMARY KEY AUTOINCREMENT,siteId INTEGER,visitType TEXT,serviceNo TEXT,system TEXT,recommendation TEXT,date TEXT)")
  d.execSQL("CREATE TABLE assets(id INTEGER PRIMARY KEY AUTOINCREMENT,siteId INTEGER,system TEXT,type TEXT,brand TEXT,model TEXT,serial TEXT,ip TEXT,location TEXT,qty INTEGER,installDate TEXT,remarks TEXT,secret TEXT)")
  d.execSQL("CREATE TABLE technicians(id INTEGER PRIMARY KEY AUTOINCREMENT,company TEXT,name TEXT,email TEXT,phone TEXT,epf TEXT,password TEXT)")
 }
 override fun onUpgrade(d:SQLiteDatabase,o:Int,n:Int){ }
 fun addSite(s:Site):Long { val v=ContentValues().apply{put("name",s.name);put("job",s.job);put("customer",s.customer);put("person",s.person);put("phone",s.phone);put("address",s.address)};return writableDatabase.insert("sites",null,v)}
 fun sites():List<Site>{ val out=mutableListOf<Site>();readableDatabase.rawQuery("SELECT * FROM sites ORDER BY id DESC",null).use{while(it.moveToNext())out+=Site(it.getLong(0),it.getString(1),it.getString(2),it.getString(3),it.getString(4),it.getString(5),it.getString(6))};return out }
 fun addService(r:ServiceReport):Long {val v=ContentValues().apply{put("siteId",r.siteId);put("visitType",r.visitType);put("serviceNo",r.serviceNo);put("system",r.system);put("recommendation",r.recommendation);put("date",r.date)};return writableDatabase.insert("services",null,v)}
 fun addAsset(a:Asset):Long {val v=ContentValues().apply{put("siteId",a.siteId);put("system",a.system);put("type",a.type);put("brand",a.brand);put("model",a.model);put("serial",a.serial);put("ip",a.ip);put("location",a.location);put("qty",a.qty);put("installDate",a.installDate);put("remarks",a.remarks);put("secret",a.secret)};return writableDatabase.insert("assets",null,v)}
 fun assets(siteId:Long):List<Asset>{val o=mutableListOf<Asset>();readableDatabase.rawQuery("SELECT * FROM assets WHERE siteId=?",arrayOf(siteId.toString())).use{while(it.moveToNext())o+=Asset(it.getLong(0),it.getLong(1),it.getString(2),it.getString(3),it.getString(4),it.getString(5),it.getString(6),it.getString(7),it.getString(8),it.getInt(9),it.getString(10),it.getString(11),it.getString(12))};return o}
 fun saveTechnician(company:String,name:String,email:String,phone:String,epf:String,password:String){val v=ContentValues().apply{put("company",company);put("name",name);put("email",email);put("phone",phone);put("epf",epf);put("password",password)};writableDatabase.insert("technicians",null,v)}
}
