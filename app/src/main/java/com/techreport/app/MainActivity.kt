package com.techreport.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SYSTEMS=listOf("CCTV","Access Control","Burglar Alarm","Fire Alarm","Networking/Fiber")
private val VISIT_TYPES=listOf("Service","Troubleshooting","Installation","Inspection")
private val SERVICE_NUMBERS=listOf("1st","2nd","3rd","4th","Other")
private val STATUS_VALUES=listOf("Good","Fair","Fault")
private val INSPECTION_ITEMS=mapOf(
 "CCTV" to listOf("Cameras","NVR","HDD","Recording","UPS/Battery","PoE/Network","Cabling"),
 "Access Control" to listOf("Controller","Reader","Magnetic Lock","Push Button","Emergency Release/Break Glass","Power Supply","UPS/Battery","Door Contact/Door Sensor"),
 "Burglar Alarm" to listOf("Alarm Panel","PIR Sensor","Door Magnetic Contact Sensor","Keypad/Control","Siren","Battery/Power Supply","Shock Sensor"),
 "Fire Alarm" to listOf("Fire Alarm Panel","Smoke Detector","Heat Detector","Manual Call Point","Sounder/Bell/Strobe","Battery/Power Supply"),
 "Networking/Fiber" to listOf("Network Switch","Router/Firewall","Access Point","Fiber Link/SFP","UTP/Fiber Cabling")
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MaterialTheme{TechReportApp()}}}
}
@Composable fun TechReportApp(){
 var screen by remember{mutableStateOf("home")}
 when(screen){
  "home"->HomeScreen({screen="service"},{screen="installation"},{screen="sites"},{screen="profile"})
  "service"->ServiceScreen{screen="home"}
  "installation"->InstallationScreen{screen="home"}
  "sites"->ExistingSitesScreen{screen="home"}
  "profile"->TechnicianProfileScreen{screen="home"}
 }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AppShell(title:String,onBack:(()->Unit)?=null,content:@Composable()->Unit){
 Scaffold(topBar={TopAppBar(title={Text(title)},navigationIcon={if(onBack!=null)TextButton(onClick=onBack){Text("Back")}})}){p->Column(Modifier.fillMaxSize().padding(p).padding(16.dp).verticalScroll(rememberScrollState())){content()}}}
@Composable fun HomeScreen(s:()->Unit,i:()->Unit,e:()->Unit,p:()->Unit){
 AppShell("TechReport"){Text("Technician Service Manager",style=MaterialTheme.typography.headlineSmall);Spacer(Modifier.height(20.dp));HomeButton("Service / Troubleshooting Report",s);HomeButton("New Installation / Asset Report",i);HomeButton("Existing Sites",e);HomeButton("Technician Profile",p)}
}
@Composable fun HomeButton(t:String,c:()->Unit)=Button(c,Modifier.fillMaxWidth().padding(vertical=6.dp).height(56.dp)){Text(t)}

@Composable fun ServiceScreen(back:()->Unit){
 var visit by remember{mutableStateOf("Service")};var no by remember{mutableStateOf("1st")};var system by remember{mutableStateOf("CCTV")}
 var site by remember{mutableStateOf("")};var job by remember{mutableStateOf("")};var date by remember{mutableStateOf(today())};var company by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var person by remember{mutableStateOf("")};var address by remember{mutableStateOf("")};var rec by remember{mutableStateOf("")}
 val status=remember{mutableStateMapOf<String,String>()};val remarks=remember{mutableStateMapOf<String,String>()}
 var t1 by remember{mutableStateOf("")};var e1 by remember{mutableStateOf("")};var s1 by remember{mutableStateOf("")};var t2 by remember{mutableStateOf("")};var e2 by remember{mutableStateOf("")};var s2 by remember{mutableStateOf("")};var t3 by remember{mutableStateOf("")};var e3 by remember{mutableStateOf("")};var s3 by remember{mutableStateOf("")};var manager by remember{mutableStateOf("")};var msig by remember{mutableStateOf("")};var mdate by remember{mutableStateOf(today())}
 AppShell("Service Report",back){
  Text("Visit Details",style=MaterialTheme.typography.titleLarge);DropdownField("Visit Type",visit,VISIT_TYPES){visit=it};if(visit=="Service")DropdownField("Service Number",no,SERVICE_NUMBERS){no=it}
  Text("Site Details",style=MaterialTheme.typography.titleLarge);InputField("Site Name",site){site=it};InputField("Job Number",job){job=it};InputField("Date",date){date=it};InputField("Company / Customer",company){company=it};InputField("Contact Number",phone){phone=it};InputField("Contact Person",person){person=it};InputField("Address",address){address=it}
  Text("System",style=MaterialTheme.typography.titleLarge);DropdownField("Selected System",system,SYSTEMS){system=it}
  INSPECTION_ITEMS[system].orEmpty().forEach{item->Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Column(Modifier.padding(10.dp)){Text(item,style=MaterialTheme.typography.titleMedium);DropdownField("Status",status[item]?:"Good",STATUS_VALUES){status[item]=it};InputField("Remarks",remarks[item]?:""){remarks[item]=it}}}}
  Text("Recommendation",style=MaterialTheme.typography.titleLarge);InputField("Recommendation",rec){rec=it}
  Text("Technician 1",style=MaterialTheme.typography.titleLarge);InputField("Name",t1){t1=it};InputField("EPF",e1){e1=it};InputField("Signature",s1){s1=it}
  Text("Technician 2",style=MaterialTheme.typography.titleLarge);InputField("Name",t2){t2=it};InputField("EPF",e2){e2=it};InputField("Signature",s2){s2=it}
  Text("Technician 3",style=MaterialTheme.typography.titleLarge);InputField("Name",t3){t3=it};InputField("EPF",e3){e3=it};InputField("Signature",s3){s3=it}
  Text("Manager Approval",style=MaterialTheme.typography.titleLarge);InputField("Manager / Boss Name",manager){manager=it};InputField("Manager Signature",msig){msig=it};InputField("Approval Date",mdate){mdate=it}
  Button({Pdf.service(site,job,date,company,phone,person,address,visit,no,system,rec)},Modifier.fillMaxWidth()){Text("Save & Generate PDF")}
 }
}

@Composable fun InstallationScreen(back:()->Unit){
 var system by remember{mutableStateOf("CCTV")};var site by remember{mutableStateOf("")};var job by remember{mutableStateOf("")};var date by remember{mutableStateOf(today())};var company by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var person by remember{mutableStateOf("")};var address by remember{mutableStateOf("")};var type by remember{mutableStateOf("")};var brand by remember{mutableStateOf("")};var model by remember{mutableStateOf("")};var serial by remember{mutableStateOf("")};var ip by remember{mutableStateOf("")};var loc by remember{mutableStateOf("")};var qty by remember{mutableStateOf("1")};var idate by remember{mutableStateOf(today())};var rem by remember{mutableStateOf("")}
 val assets=remember{mutableStateListOf<String>()}
 AppShell("Installation / Asset Register",back){
  Text("Site Details",style=MaterialTheme.typography.titleLarge);InputField("Site Name",site){site=it};InputField("Job Number",job){job=it};InputField("Date",date){date=it};InputField("Company / Customer",company){company=it};InputField("Contact Number",phone){phone=it};InputField("Contact Person",person){person=it};InputField("Address",address){address=it}
  Text("System",style=MaterialTheme.typography.titleLarge);DropdownField("Selected System",system,SYSTEMS){system=it};Text("Add Device",style=MaterialTheme.typography.titleLarge)
  InputField("Device Type",type){type=it};InputField("Brand",brand){brand=it};InputField("Model",model){model=it};InputField("Serial Number",serial){serial=it};InputField("IP Address",ip){ip=it};InputField("Location",loc){loc=it};InputField("Quantity",qty){qty=it};InputField("Installation Date",idate){idate=it};InputField("Remarks",rem){rem=it}
  Button({assets.add("System: $system | Device: $type | Brand: $brand | Model: $model | Serial: $serial | IP: $ip | Location: $loc | Qty: $qty | Date: $idate | Remarks: $rem");type="";brand="";model="";serial="";ip="";loc="";qty="1";rem=""},Modifier.fillMaxWidth()){Text("Add Device")}
  Text("Added Devices: ${assets.size}");assets.forEachIndexed{i,a->Card(Modifier.fillMaxWidth().padding(vertical=3.dp)){Row(Modifier.fillMaxWidth().padding(8.dp)){Text("${i+1}. $a",Modifier.weight(1f));TextButton({assets.removeAt(i)}){Text("Remove")}}}}
  Button({Pdf.installation(site,job,date,company,phone,person,address,system,assets.toList())},Modifier.fillMaxWidth()){Text("Save & Generate PDF")}
 }
}

@Composable fun ExistingSitesScreen(back:()->Unit){
 val sites=remember{mutableStateListOf<String>()};var name by remember{mutableStateOf("")};var company by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var address by remember{mutableStateOf("")}
 AppShell("Existing Sites",back){Text("Add Site",style=MaterialTheme.typography.titleLarge);InputField("Site Name",name){name=it};InputField("Company / Customer",company){company=it};InputField("Contact Number",phone){phone=it};InputField("Address",address){address=it};Button({if(name.isNotBlank()){sites.add("$name | $company | $phone | $address");name="";company="";phone="";address=""}},Modifier.fillMaxWidth()){Text("Save Site")};sites.forEachIndexed{i,s->Card(Modifier.fillMaxWidth().padding(vertical=4.dp)){Row(Modifier.fillMaxWidth().padding(8.dp)){Text(s,Modifier.weight(1f));TextButton({sites.removeAt(i)}){Text("Delete")}}}}}
@Composable
fun TechnicianProfileScreen(back:()->Unit){
 var company by remember{mutableStateOf("")}
 var name by remember{mutableStateOf("")}
 var email by remember{mutableStateOf("")}
 var phone by remember{mutableStateOf("")}
 var epf by remember{mutableStateOf("")}
 var pass by remember{mutableStateOf("")}

 AppShell("Technician Profile",back){
  InputField("Company Name",company){company=it}
  InputField("Technician Name",name){name=it}
  InputField("Email",email){email=it}
  InputField("Phone",phone){phone=it}
  InputField("EPF",epf){epf=it}
  InputField("Password",pass){pass=it}
  Button({},Modifier.fillMaxWidth()){Text("Save Profile")}
 }
}
@Composable fun InputField(label:String,value:String,onChange:(String)->Unit)=OutlinedTextField(value,onChange,label={Text(label)},modifier=Modifier.fillMaxWidth().padding(vertical=4.dp))
@Composable fun DropdownField(label:String,value:String,options:List<String>,onSelected:(String)->Unit){var open by remember{mutableStateOf(false)};Box(Modifier.fillMaxWidth().padding(vertical=4.dp)){OutlinedButton({open=true},Modifier.fillMaxWidth()){Text("$label: $value")};DropdownMenu(open,{open=false}){options.forEach{o->DropdownMenuItem(text={Text(o)},onClick={onSelected(o);open=false})}}}}
fun today()=SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date())
