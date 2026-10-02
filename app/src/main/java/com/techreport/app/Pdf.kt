package com.techreport.app

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object Pdf {
 fun service(siteName:String,jobNumber:String,date:String,company:String,contactNumber:String,contactPerson:String,address:String,visitType:String,serviceNumber:String,system:String,recommendation:String){
  val d=PdfDocument();val page=d.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());val c=page.canvas;val p=Paint();p.textSize=12f
  p.typeface=Typeface.DEFAULT_BOLD;p.textSize=20f;c.drawText("TECHREPORT SERVICE REPORT",40f,50f,p);p.typeface=Typeface.DEFAULT;p.textSize=12f;var y=85f
  fun line(a:String,b:String){c.drawText("$a: $b",40f,y,p);y+=21f}
  line("Site Name",siteName);line("Job Number",jobNumber);line("Date",date);line("Company / Customer",company);line("Contact Number",contactNumber);line("Contact Person",contactPerson);line("Address",address);line("Visit Type",visitType);if(visitType=="Service")line("Service Number",serviceNumber);line("System",system)
  y+=15;p.typeface=Typeface.DEFAULT_BOLD;c.drawText("Recommendation",40f,y,p);y+=22;p.typeface=Typeface.DEFAULT;(recommendation.ifBlank{"No recommendation entered."}).chunked(75).forEach{if(y<790){c.drawText(it,40f,y,p);y+=18}}
  y+=30;c.drawText("Technician Signature: __________________________",40f,y,p);y+=35;c.drawText("Manager Signature: _____________________________",40f,y,p);d.finishPage(page);save(d,"TechReport_Service_${safe(siteName)}.pdf")
 }
 fun installation(siteName:String,jobNumber:String,date:String,company:String,contactNumber:String,contactPerson:String,address:String,system:String,assets:List<String>){
  val d=PdfDocument();val page=d.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());val c=page.canvas;val p=Paint();p.textSize=12f
  p.typeface=Typeface.DEFAULT_BOLD;p.textSize=18f;c.drawText("TECHREPORT INSTALLATION / ASSET REPORT",30f,50f,p);p.typeface=Typeface.DEFAULT;p.textSize=12f;var y=85f
  fun line(a:String,b:String){if(y<790){c.drawText("$a: $b",35f,y,p);y+=20}}
  line("Site Name",siteName);line("Job Number",jobNumber);line("Date",date);line("Company / Customer",company);line("Contact Number",contactNumber);line("Contact Person",contactPerson);line("Address",address);line("System",system);y+=15;p.typeface=Typeface.DEFAULT_BOLD;c.drawText("Installed Equipment",35f,y,p);y+=25;p.typeface=Typeface.DEFAULT
  if(assets.isEmpty())c.drawText("No equipment added.",35f,y,p) else assets.forEachIndexed{i,a->if(y<790){a.chunked(75).forEachIndexed{j,t->if(y<790){c.drawText(if(j==0)"${i+1}. $t" else "   $t",35f,y,p);y+=18}};y+=5}}
  d.finishPage(page);save(d,"TechReport_Installation_${safe(siteName)}.pdf")
 }
 private fun save(d:PdfDocument,name:String){try{val dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);if(!dir.exists())dir.mkdirs();FileOutputStream(File(dir,name)).use{d.writeTo(it)}}catch(e:Exception){e.printStackTrace()}finally{d.close()}}
 private fun safe(v:String)=v.ifBlank{"Site"}.replace(Regex("[^A-Za-z0-9._-]"),"_")
}
