package com.techreport.app

data class Site(val id: Long=0, val name:String, val job:String, val customer:String, val person:String, val phone:String, val address:String)
data class Inspection(val item:String, var status:String="Good", var remarks:String="")
data class ServiceReport(val id:Long=0, val siteId:Long, val visitType:String, val serviceNo:String, val system:String, val recommendation:String, val date:String)
data class Asset(val id:Long=0, val siteId:Long, val system:String, val type:String, val brand:String, val model:String, val serial:String, val ip:String, val location:String, val qty:Int, val installDate:String, val remarks:String, val secret:String="")
