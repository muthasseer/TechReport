package com.example.techreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SYSTEMS = listOf(
    "CCTV",
    "Access Control",
    "Burglar Alarm",
    "Fire Alarm",
    "Networking/Fiber"
)

private val VISIT_TYPES = listOf(
    "Service",
    "Troubleshooting",
    "Installation",
    "Inspection"
)

private val SERVICE_NUMBERS = listOf(
    "1st",
    "2nd",
    "3rd",
    "4th",
    "Other"
)

private val STATUS_VALUES = listOf(
    "Good",
    "Fair",
    "Fault"
)

private val INSPECTION_ITEMS = mapOf(
    "CCTV" to listOf(
        "Cameras",
        "NVR",
        "HDD",
        "Recording",
        "UPS/Battery",
        "PoE/Network",
        "Cabling"
    ),
    "Access Control" to listOf(
        "Controller",
        "Reader",
        "Magnetic Lock",
        "Push Button",
        "Emergency Release/Break Glass",
        "Power Supply",
        "UPS/Battery",
        "Door Contact/Door Sensor"
    ),
    "Burglar Alarm" to listOf(
        "Alarm Panel",
        "PIR Sensor",
        "Door Magnetic Contact Sensor",
        "Keypad/Control",
        "Siren",
        "Battery/Power Supply",
        "Shock Sensor"
    ),
    "Fire Alarm" to listOf(
        "Fire Alarm Panel",
        "Smoke Detector",
        "Heat Detector",
        "Manual Call Point",
        "Sounder/Bell/Strobe",
        "Battery/Power Supply"
    ),
    "Networking/Fiber" to listOf(
        "Network Switch",
        "Router/Firewall",
        "Access Point",
        "Fiber Link/SFP",
        "UTP/Fiber Cabling"
    )
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

    var screen by remember {
        mutableStateOf("home")
    }

    when (screen) {

        "home" -> HomeScreen(
            onService = { screen = "service" },
            onInstallation = { screen = "installation" },
            onSites = { screen = "sites" },
            onProfile = { screen = "profile" }
        )

        "service" -> ServiceScreen(
            onBack = { screen = "home" }
        )

        "installation" -> InstallationScreen(
            onBack = { screen = "home" }
        )

        "sites" -> ExistingSitesScreen(
            onBack = { screen = "home" }
        )

        "profile" -> TechnicianProfileScreen(
            onBack = { screen = "home" }
        )
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
                title = {
                    Text(title)
                },
                navigationIcon = {
                    if (onBack != null) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
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

    AppShell(title = "TechReport") {

        Text(
            text = "Technician Service Manager",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        HomeButton(
            text = "Service / Troubleshooting Report",
            onClick = onService
        )

        HomeButton(
            text = "New Installation / Asset Report",
            onClick = onInstallation
        )

        HomeButton(
            text = "Existing Sites",
            onClick = onSites
        )

        HomeButton(
            text = "Technician Profile",
            onClick = onProfile
        )
    }
}

@Composable
fun HomeButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(58.dp)
    ) {
        Text(text)
    }
}

@Composable
fun ServiceScreen(
    onBack: () -> Unit
) {

    var visitType by remember {
        mutableStateOf(VISIT_TYPES.first())
    }

    var serviceNumber by remember {
        mutableStateOf(SERVICE_NUMBERS.first())
    }

    var selectedSystem by remember {
        mutableStateOf(SYSTEMS.first())
    }

    var siteName by remember {
        mutableStateOf("")
    }

    var jobNumber by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf(today())
    }

    var company by remember {
        mutableStateOf("")
    }

    var contactNumber by remember {
        mutableStateOf("")
    }

    var contactPerson by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var recommendation by remember {
        mutableStateOf("")
    }

    val statusMap = remember {
        mutableStateMapOf<String, String>()
    }

    val remarksMap = remember {
        mutableStateMapOf<String, String>()
    }

    var technician1 by remember { mutableStateOf("") }
    var technician1Epf by remember { mutableStateOf("") }
    var technician1Signature by remember { mutableStateOf("") }

    var technician2 by remember { mutableStateOf("") }
    var technician2Epf by remember { mutableStateOf("") }
    var technician2Signature by remember { mutableStateOf("") }

    var technician3 by remember { mutableStateOf("") }
    var technician3Epf by remember { mutableStateOf("") }
    var technician3Signature by remember { mutableStateOf("") }

    var managerName by remember { mutableStateOf("") }
    var managerSignature by remember { mutableStateOf("") }
    var managerDate by remember { mutableStateOf(today()) }

    AppShell(
        title = "Service Report",
        onBack = onBack
    ) {

        Text(
            text = "Visit Details",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        DropdownField(
            label = "Visit Type",
            value = visitType,
            options = VISIT_TYPES,
            onSelected = { visitType = it }
        )

        if (visitType == "Service") {

            DropdownField(
                label = "Service Number",
                value = serviceNumber,
                options = SERVICE_NUMBERS,
                onSelected = { serviceNumber = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Site Details",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Site Name", siteName) {
            siteName = it
        }

        InputField("Job Number", jobNumber) {
            jobNumber = it
        }

        InputField("Date", date) {
            date = it
        }

        InputField("Company / Customer", company) {
            company = it
        }

        InputField("Contact Number", contactNumber) {
            contactNumber = it
        }

        InputField("Contact Person", contactPerson) {
            contactPerson = it
        }

        InputField("Address", address) {
            address = it
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "System",
            style = MaterialTheme.typography.titleLarge
        )

        DropdownField(
            label = "Select System",
            value = selectedSystem,
            options = SYSTEMS,
            onSelected = {
                selectedSystem = it
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$selectedSystem Inspection",
            style = MaterialTheme.typography.titleLarge
        )

        val items = INSPECTION_ITEMS[selectedSystem].orEmpty()

        items.forEach { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = item,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownField(
                        label = "Status",
                        value = statusMap[item] ?: "Good",
                        options = STATUS_VALUES,
                        onSelected = {
                            statusMap[item] = it
                        }
                    )

                    InputField(
                        label = "Remarks",
                        value = remarksMap[item] ?: ""
                    ) {
                        remarksMap[item] = it
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Recommendation",
            style = MaterialTheme.typography.titleLarge
        )

        InputField(
            label = "Recommendation",
            value = recommendation
        ) {
            recommendation = it
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Technician 1",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Name", technician1) {
            technician1 = it
        }

        InputField("EPF", technician1Epf) {
            technician1Epf = it
        }

        InputField("Signature", technician1Signature) {
            technician1Signature = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Technician 2",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Name", technician2) {
            technician2 = it
        }

        InputField("EPF", technician2Epf) {
            technician2Epf = it
        }

        InputField("Signature", technician2Signature) {
            technician2Signature = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Technician 3",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Name", technician3) {
            technician3 = it
        }

        InputField("EPF", technician3Epf) {
            technician3Epf = it
        }

        InputField("Signature", technician3Signature) {
            technician3Signature = it
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Manager Approval",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Manager / Boss Name", managerName) {
            managerName = it
        }

        InputField("Manager Signature", managerSignature) {
            managerSignature = it
        }

        InputField("Approval Date", managerDate) {
            managerDate = it
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                Pdf.service(
                    siteName = siteName,
                    jobNumber = jobNumber,
                    date = date,
                    company = company,
                    contactNumber = contactNumber,
                    contactPerson = contactPerson,
                    address = address,
                    visitType = visitType,
                    serviceNumber = serviceNumber,
                    system = selectedSystem,
                    recommendation = recommendation
                )

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Generate PDF")
        }
    }
}

@Composable
fun InstallationScreen(
    onBack: () -> Unit
) {

    var selectedSystem by remember {
        mutableStateOf(SYSTEMS.first())
    }

    var siteName by remember {
        mutableStateOf("")
    }

    var jobNumber by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf(today())
    }

    var company by remember {
        mutableStateOf("")
    }

    var contactNumber by remember {
        mutableStateOf("")
    }

    var contactPerson by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var deviceType by remember {
        mutableStateOf("")
    }

    var brand by remember {
        mutableStateOf("")
    }

    var model by remember {
        mutableStateOf("")
    }

    var serial by remember {
        mutableStateOf("")
    }

    var ip by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var quantity by remember {
        mutableStateOf("1")
    }

    var installationDate by remember {
        mutableStateOf(today())
    }

    var remarks by remember {
        mutableStateOf("")
    }

    val assets = remember {
        mutableStateListOf<String>()
    }

    AppShell(
        title = "Installation / Asset Register",
        onBack = onBack
    ) {

        Text(
            text = "Site Details",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Site Name", siteName) {
            siteName = it
        }

        InputField("Job Number", jobNumber) {
            jobNumber = it
        }

        InputField("Date", date) {
            date = it
        }

        InputField("Company / Customer", company) {
            company = it
        }

        InputField("Contact Number", contactNumber) {
            contactNumber = it
        }

        InputField("Contact Person", contactPerson) {
            contactPerson = it
        }

        InputField("Address", address) {
            address = it
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "System",
            style = MaterialTheme.typography.titleLarge
        )

        DropdownField(
            label = "Select System",
            value = selectedSystem,
            options = SYSTEMS,
            onSelected = {
                selectedSystem = it
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Add Device",
            style = MaterialTheme.typography.titleLarge
        )

        InputField("Device Type", deviceType) {
            deviceType = it
        }

        InputField("Brand", brand) {
            brand = it
        }

        InputField("Model", model) {
            model = it
        }

        InputField("Serial Number", serial) {
            serial = it
        }

        InputField("IP Address", ip) {
            ip = it
        }

        InputField("Location", location) {
            location = it
        }

        InputField("Quantity", quantity) {
            quantity = it
        }

        InputField("Installation Date", installationDate) {
            installationDate = it
        }

        InputField("Remarks", remarks) {
            remarks = it
        }

        Button(
            onClick = {

                val device = buildString {

                    append("System: ")
                    append(selectedSystem)

                    append(" | Device: ")
                    append(deviceType)

                    append(" | Brand: ")
                    append(brand)

                    append(" | Model: ")
                    append(model)

                    append(" | Serial: ")
                    append(serial)

                    append(" | IP: ")
                    append(ip)

                    append(" | Location: ")
                    append(location)

                    append(" | Qty: ")
                    append(quantity)

                    append(" | Installation Date: ")
                    append(installationDate)

                    append(" | Remarks: ")
                    append(remarks)
                }

                assets.add(device)

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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Added Devices: ${assets.size}",
            style = MaterialTheme.typography.titleMedium
        )

        assets.forEachIndexed { index, asset ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        text = "${index + 1}. $asset"
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            assets.removeAt(index)
                        }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                Pdf.installation(
                    siteName = siteName,
                    jobNumber = jobNumber,
                    date = date,
                    company = company,
                    contactNumber = contactNumber,
                    contactPerson = contactPerson,
                    address = address,
                    system = selectedSystem,
                    assets = assets.toList()
                )

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save & Generate PDF")
        }
    }
}

@Composable
fun ExistingSitesScreen(
    onBack: () -> Unit
) {

    val sites = remember {
        mutableStateListOf<String>()
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    var siteName by remember {
        mutableStateOf("")
    }

    var company by remember {
        mutableStateOf("")
    }

    var contactNumber by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    AppShell(
        title = "Existing Sites",
        onBack = onBack
    ) {

        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Existing Site")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (sites.isEmpty()) {

            Text(
                text = "No sites saved yet."
            )

        } else {

            sites.forEachIndexed { index, site ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            text = site,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = {
                                sites.removeAt(index)
                            }
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        if (showDialog) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "New Site",
                        style = MaterialTheme.typography.titleLarge
                    )

                    InputField("Site Name", siteName) {
                        siteName = it
                    }

                    InputField("Company / Customer", company) {
                        company = it
                    }

                    InputField("Contact Number", contactNumber) {
                        contactNumber = it
                    }

                    InputField("Address", address) {
                        address = it
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = {
                                showDialog = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {

                                if (siteName.isNotBlank()) {

                                    sites.add(
                                        "$siteName | $company | $contactNumber | $address"
                                    )

                                    siteName = ""
                                    company = ""
                                    contactNumber = ""
                                    address = ""

                                    showDialog = false
                                }

                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicianProfileScreen(
    onBack: () -> Unit
) {

    var companyName by remember {
        mutableStateOf("")
    }

    var technicianName by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var epf by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var saved by remember {
        mutableStateOf(false)
    }

    AppShell(
        title = "Technician Profile",
        onBack = onBack
    ) {

        InputField("Company Name", companyName) {
            companyName = it
        }

        InputField("Technician Name", technicianName) {
            technicianName = it
        }

        InputField("Email", email) {
            email = it
        }

        InputField("Phone", phone) {
            phone = it
        }

        InputField("EPF", epf) {
            epf = it
        }

        InputField("Password", password) {
            password = it
        }

        Button(
            onClick = {
                saved = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }

        if (saved) {

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Profile saved.",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        singleLine = false
    )
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $value")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {

                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun today(): String {

    return SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date())
}
