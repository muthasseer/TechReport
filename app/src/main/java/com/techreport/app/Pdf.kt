package com.techreport.app

import android.content.*
import android.graphics.*
import android.graphics.pdf.PdfDocument
import java.io.File

object Pdf {
 fun service(ctx:Context,site:Site,r:ServiceReport,items:List<Inspection>):File{
  val doc=PdfDocument(); val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create()); val c=page.canvas; val p=Paint().apply{color=Color.rgb(106,27,154);textSize=22f;typeface=Typeface.DEFAULT_BOLD}; c.drawText("TechReport - Service Report",32f,45f,p);p.color=Color.DKGRAY;p.textSize=11f
  var y=72f; listOf("Site: ${site.name}","Job No: ${site.job}","Customer: ${site.customer}","Visit: ${r.visitType} ${if(r.serviceNo.isNotBlank()) "- ${r.serviceNo}" else ""}","System: ${r.system}","Date: ${r.date}").forEach{c.drawText(it,32f,y,p);y+=18}
  y+=8;c.drawText("Inspection",32f,y,p);y+=20;items.forEach{c.drawText("${it.item}: ${it.status}  ${it.remarks}",38f,y,p);y+=17;if(y>790){ break }}
  y+=12;c.drawText("Recommendation: ${r.recommendation}",32f,y,p);doc.finishPage(page);return save(ctx,doc,"service_${r.id}.pdf")
 }
 fun installation(ctx:Context,site:Site,system:String,assets:List<Asset>):File{
  val doc=PdfDocument();val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create());val c=page.canvas;p=Paint().apply{color=Color.rgb(106,27,154);textSize=22f;typeface=Typeface.DEFAULT_BOLD};c.drawText("TechReport - Installation / Asset Register",25f,45f,p);p.color=Color.DKGRAY;p.textSize=11f;var y=72f;c.drawText("Site: ${site.name}   Job: ${site.job}",25f,y,p);y+=18;c.drawText("Customer: ${site.customer}   System: $system",25f,y,p);y+=25;assets.forEach{c.drawText("${it.type} | ${it.brand} ${it.model} | S/N: ${it.serial} | IP: ${it.ip}",25f,y,p);y+=17;c.drawText("Location: ${it.location}  Qty: ${it.qty}  Date: ${it.installDate}  ${it.remarks}",32f,y,p);y+=18;if(y>800) break};doc.finishPage(page);return save(ctx,doc,"installation_${site.id}.pdf")}
 private fun save(ctx:Context,d:PdfDocument,n:String):File{val f=File(ctx.getExternalFilesDir(null),n);f.outputStream().use{d.writeTo(it)};d.close();return f}
 private lateinit var p:Paint
}
