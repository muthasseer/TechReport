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

private val SYSTEMS = listOf("CCTV", "Access Control", "Burglar Alarm", "Fire Alarm", "Networking/Fiber")
private val VISIT_TYPES = listOf("Service", "Troubleshooting", "Installation", "Inspection")
private val SERVICE_NUMBERS = listOf("1st", "2nd", "3rd", "4th", "Other")
private val STATUS_VALUES = listOf("Good", "Fair", "Fault")

private val INSPECTION_ITEMS = mapOf(
    "CCTV" to listOf("Cameras", "NVR", "HDD", "Recording", "UPS/Battery", "PoE/Network", "Cabling"),
    "Access Control" to listOf("Controller", "Reader", "Magnetic Lock", "Push Button", "Emergency Release/Break Glass", "Power Supply", "UPS/Battery", "Door Contact/Door Sensor"),
    "Burglar Alarm" to listOf("Alarm Panel", "PIR Sensor", "Door Magnetic Contact Sensor", "Keypad/Control", "Siren", "Battery/Power Supply", "Shock Sensor"),
    "Fire Alarm" to listOf("Fire Alarm Panel", "Smoke Detector", "Heat Detector", "Manual Call Point", "Sounder/Bell/Strobe", "Battery/Power Supply"),
    "Networking/Fiber" to listOf("Network Switch", "Router/Firewall", "Access Point", "Fiber Link/SFP", "UTP/Fiber Cabling")
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TechReportApp()
            }
        }
    }
}

@Composable
fun TechReportApp() {
    var screen by remember { mutableStateOf("home") }

    when (screen) {
        "home" -> HomeScreen(
            onService = { screen = "service" },
            onInstallation = { screen = "installation" },
            onSites = { screen = "sites" },
            onProfile = { screen = "profile" }
        )
        "service" -> ServiceScreen { screen = "home" }
        "installation" -> InstallationScreen { screen = "home" }
        "sites" -> ExistingSitesScreen { screen = "home" }
        "profile" -> TechnicianProfileScreen { screen = "home" }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppShell(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }
    }
}

@Composable
fun HomeScreen(
    onService: () -> Unit,
    onInstallation: () -> Unit,
    onSites: () -> Unit,
    onProfile: () -> Unit
) {
    AppShell("TechReport") {
        Text("Technician Service Manager", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(20.dp))
        HomeButton("Service / Troubleshooting Report", onService)
        HomeButton("New Installation / Asset Report", onInstallation)
        HomeButton("Existing Sites", onSites)
        HomeButton("Technician Profile", onProfile)
    }
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(56.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ServiceScreen(onBack: () -> Unit) {
    var visitType by remember { mutableStateOf("Service") }
    var serviceNumber by remember { mutableStateOf("1st") }
    var system by remember { mutableStateOf("CCTV") }
    var siteName by remember { mutableStateOf("") }
    var jobNumber by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(today()) }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var recommendation by remember { mutableStateOf("") }

    val statuses = remember { mutableStateMapOf<String, String>() }
    val remarks = remember { mutableStateMapOf<String, String>() }

    var tech1 by remember { mutableStateOf("") }
    var tech1Epf by remember { mutableStateOf("") }
    var tech1Signature by remember { mutableStateOf("") }
    var tech2 by remember { mutableStateOf("") }
    var tech2Epf by remember { mutableStateOf("") }
    var tech2Signature by remember { mutableStateOf("") }
    var tech3 by remember { mutableStateOf("") }
    var tech3Epf by remember { mutableStateOf("") }
    var tech3Signature by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }
    var managerSignature by remember { mutableStateOf("") }
    var managerDate by remember { mutableStateOf(today()) }

    AppShell("Service Report", onBack) {
        Text("Visit Details", style = MaterialTheme.typography.titleLarge)
        DropdownField("Visit Type", visitType, VISIT_TYPES) { visitType = it }

        if (visitType == "Service") {
            DropdownField("Service Number", serviceNumber, SERVICE_NUMBERS) { serviceNumber = it }
        }

        Text("Site Details", style = MaterialTheme.typography.titleLarge)
        InputField("Site Name", siteName) { siteName = it }
        InputField("Job Number", jobNumber) { jobNumber = it }
        InputField("Date", date) { date = it }
        InputField("Company / Customer", company) { company = it }
        InputField("Contact Number", phone) { phone = it }
        InputField("Contact Person", contactPerson) { contactPerson = it }
        InputField("Address", address) { address = it }

        Text("System", style = MaterialTheme.typography.titleLarge)
        DropdownField("Selected System", system, SYSTEMS) { system = it }

        INSPECTION_ITEMS[system].orEmpty().forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(Modifier.padding(10.dp)) {
                    Text(item, style = MaterialTheme.typography.titleMedium)
                    DropdownField(
                        "Status",
                        statuses[item] ?: "Good",
                        STATUS_VALUES
                    ) {
                        statuses[item] = it
                    }
                    InputField("Remarks", remarks[item] ?: "") {
                        remarks[item] = it
                    }
                }
            }
        }

        Text("Recommendation", style = MaterialTheme.typography.titleLarge)
        InputField("Recommendation", recommendation) { recommendation = it }

        Text("Technician 1", style = MaterialTheme.typography.titleLarge)
        InputField("Name", tech1) { tech1 = it }
        InputField("EPF", tech1Epf) { tech1Epf = it }
        InputField("Signature", tech1Signature) { tech1Signature = it }

        Text("Technician 2", style = MaterialTheme.typography.titleLarge)
        InputField("Name", tech2) { tech2 = it }
        InputField("EPF", tech2Epf) { tech2Epf = it }
        InputField("Signature", tech2Signature) { tech2Signature = it }

        Text("Technician 3", style = MaterialTheme.typography.titleLarge)
        InputField("Name", tech3) { tech3 = it }
        InputField("EPF", tech3Epf) { tech3Epf = it }
        InputField("Signature", tech3Signature) { tech3Signature = it }

        Text("Manager Approval", style = MaterialTheme.typography.titleLarge)
        InputField("Manager / Boss Name", manager) { manager = it }
        InputField("Manager Signature", managerSignature) { managerSignature = it }
        InputField("Approval Date", managerDate) { managerDate = it }

        Button(
            onClick = {
                Pdf.service(
                    siteName, jobNumber, date, company, phone, contactPerson,
                    address, visitType, serviceNumber, system, recommendation
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Generate PDF")
        }
    }
}

@Composable
fun InstallationScreen(onBack: () -> Unit) {
    var system by remember { mutableStateOf("CCTV") }
    var siteName by remember { mutableStateOf("") }
    var jobNumber by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(today()) }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var contactPerson by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var deviceType by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var installationDate by remember { mutableStateOf(today()) }
    var remarks by remember { mutableStateOf("") }

    val assets = remember { mutableStateListOf<String>() }

    AppShell("Installation / Asset Register", onBack) {
        Text("Site Details", style = MaterialTheme.typography.titleLarge)
        InputField("Site Name", siteName) { siteName = it }
        InputField("Job Number", jobNumber) { jobNumber = it }
        InputField("Date", date) { date = it }
        InputField("Company / Customer", company) { company = it }
        InputField("Contact Number", phone) { phone = it }
        InputField("Contact Person", contactPerson) { contactPerson = it }
        InputField("Address", address) { address = it }

        Text("System", style = MaterialTheme.typography.titleLarge)
        DropdownField("Selected System", system, SYSTEMS) { system = it }

        Text("Add Device", style = MaterialTheme.typography.titleLarge)
        InputField("Device Type", deviceType) { deviceType = it }
        InputField("Brand", brand) { brand = it }
        InputField("Model", model) { model = it }
        InputField("Serial Number", serial) { serial = it }
        InputField("IP Address", ip) { ip = it }
        InputField("Location", location) { location = it }
        InputField("Quantity", quantity) { quantity = it }
        InputField("Installation Date", installationDate) { installationDate = it }
        InputField("Remarks", remarks) { remarks = it }

        Button(
            onClick = {
                assets.add(
                    "System: $system | Device: $deviceType | Brand: $brand | Model: $model | Serial: $serial | IP: $ip | Location: $location | Qty: $quantity | Date: $installationDate | Remarks: $remarks"
                )
                deviceType = ""
                brand = ""
                model = ""
                serial = ""
                ip = ""
                location = ""
                quantity = "1"
                remarks = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Device")
        }

        Text("Added Devices: ${assets.size}")

        assets.forEachIndexed { index, asset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(asset, Modifier.weight(1f))
                    TextButton(onClick = { assets.removeAt(index) }) {
                        Text("Remove")
                    }
                }
            }
        }

        Button(
            onClick = {
                Pdf.installation(
                    siteName, jobNumber, date, company, phone,
                    contactPerson, address, system, assets.toList()
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Generate PDF")
        }
    }
}

@Composable
fun ExistingSitesScreen(onBack: () -> Unit) {
    val sites = remember { mutableStateListOf<String>() }
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AppShell("Existing Sites", onBack) {
        Text("Add Site", style = MaterialTheme.typography.titleLarge)
        InputField("Site Name", name) { name = it }
        InputField("Company / Customer", company) { company = it }
        InputField("Contact Number", phone) { phone = it }
        InputField("Address", address) { address = it }

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    sites.add("$name | $company | $phone | $address")
                    name = ""
                    company = ""
                    phone = ""
                    address = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Site")
        }

        sites.forEachIndexed { index, site ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(site, Modifier.weight(1f))
                    TextButton(onClick = { sites.removeAt(index) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicianProfileScreen(onBack: () -> Unit) {
    var company by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var epf by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AppShell("Technician Profile", onBack) {
        InputField("Company Name", company) { company = it }
        InputField("Technician Name", name) { name = it }
        InputField("Email", email) { email = it }
        InputField("Phone", phone) { phone = it }
        InputField("EPF", epf) { epf = it }
        InputField("Password", password) { password = it }

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var open by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $value")
        }

        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        open = false
                    }
                )
            }
        }
    }
}

fun today(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
