package com.techreport.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val SYSTEMS = listOf(
    "CCTV",
    "Access Control",
    "Burglar Alarm",
    "Fire Alarm",
    "Networking / Fiber"
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
        "Access Control Machine/Controller",
        "Access Control Reader",
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

    "Networking / Fiber" to listOf(
        "Network Switch",
        "Router/Firewall",
        "Access Point",
        "Fiber Link/SFP",
        "UTP/Fiber Cabling"
    )
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

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDb(this)

        setContent {

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF6A1B9A),
                    secondary = Color(0xFF8E24AA)
                )
            ) {

                TechReportApp()
            }
        }
    }

    @Composable
    private fun TechReportApp() {

        var page by remember {
            mutableStateOf("home")
        }

        when (page) {

            "home" -> {
                HomeScreen {
                    page = it
                }
            }

            "service" -> {
                ServiceScreen {
                    page = it
                }
            }

            "install" -> {
                InstallationScreen {
                    page = it
                }
            }

            "sites" -> {
                ExistingSitesScreen {
                    page = it
                }
            }

            "profile" -> {
                TechnicianProfileScreen {
                    page = it
                }
            }

            else -> {
                HomeScreen {
                    page = it
                }
            }
        }
    }

    @Composable
    private fun AppShell(
        title: String,
        onBack: () -> Unit,
        content: @Composable ColumnScope.() -> Unit
    ) {

        Scaffold(

            topBar = {

                TopAppBar(

                    title = {
                        Text(title)
                    },

                    navigationIcon = {

                        TextButton(
                            onClick = onBack
                        ) {
                            Text("‹")
                        }
                    }
                )
            }

        ) { padding ->

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),

                content = content
            )
        }
    }

    @Composable
    private fun HomeScreen(
        navigate: (String) -> Unit
    ) {

        Scaffold(

            topBar = {
                TopAppBar(
                    title = {
                        Text("TechReport")
                    }
                )
            }

        ) { padding ->

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(18.dp),

                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Field Technician Service Manager",
                    style = MaterialTheme.typography.titleMedium
                )

                HomeButton(
                    "1. Service / Troubleshooting Report"
                ) {
                    navigate("service")
                }

                HomeButton(
                    "2. New Installation / Asset Report"
                ) {
                    navigate("install")
                }

                HomeButton(
                    "3. Existing Sites"
                ) {
                    navigate("sites")
                }

                HomeButton(
                    "4. Technician Profile"
                ) {
                    navigate("profile")
                }
            }
        }
    }

    @Composable
    private fun HomeButton(
        text: String,
        onClick: () -> Unit
    ) {

        Button(

            onClick = onClick,

            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(text)
        }
    }

    @Composable
    private fun ServiceScreen(
        navigate: (String) -> Unit
    ) {

        val sites = db.sites()

        var selectedSite by remember {
            mutableStateOf(sites.firstOrNull())
        }

        var visitType by remember {
            mutableStateOf("Service")
        }

        var serviceNumber by remember {
            mutableStateOf("1st")
        }

        var selectedSystem by remember {
            mutableStateOf(SYSTEMS.first())
        }

        var recommendation by remember {
            mutableStateOf("")
        }

        val statusMap = remember(selectedSystem) {

            mutableStateMapOf<String, String>().apply {

                INSPECTION_ITEMS[selectedSystem]
                    .orEmpty()
                    .forEach { item ->

                        put(
                            item,
                            "Good"
                        )
                    }
            }
        }

        val remarksMap = remember(selectedSystem) {

            mutableStateMapOf<String, String>().apply {

                INSPECTION_ITEMS[selectedSystem]
                    .orEmpty()
                    .forEach { item ->

                        put(
                            item,
                            ""
                        )
                    }
            }
        }

        AppShell(

            title = "Service / Troubleshooting",

            onBack = {
                navigate("home")
            }

        ) {

            Text(
                text = "Site",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            if (sites.isEmpty()) {

                Text(
                    "No sites yet. Please create a site first."
                )

            } else {

                sites.forEach { site ->

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        RadioButton(

                            selected =
                                selectedSite?.id == site.id,

                            onClick = {
                                selectedSite = site
                            }
                        )

                        Text(
                            text = site.name,

                            modifier = Modifier
                                .padding(top = 12.dp)
                        )
                    }
                }
            }

            DropdownField(
                label = "Visit Type",
                value = visitType,
                options = VISIT_TYPES
            ) {

                visitType = it
            }

            if (visitType == "Service") {

                DropdownField(
                    label = "Service Number",
                    value = serviceNumber,
                    options = SERVICE_NUMBERS
                ) {

                    serviceNumber = it
                }
            }

            DropdownField(
                label = "System",
                value = selectedSystem,
                options = SYSTEMS
            ) {

                selectedSystem = it
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Inspection",
                style = MaterialTheme.typography.titleMedium
            )

            INSPECTION_ITEMS[selectedSystem]
                .orEmpty()
                .forEach { item ->

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {

                            Text(item)

                            Row {

                                STATUS_VALUES.forEach { status ->

                                    RadioButton(

                                        selected =
                                            statusMap[item] == status,

                                        onClick = {
                                            statusMap[item] = status
                                        }
                                    )

                                    Text(
                                        text = status,

                                        modifier = Modifier
                                            .padding(
                                                top = 12.dp,
                                                end = 4.dp
                                            )
                                    )
                                }
                            }

                            InputField(
                                value =
                                    remarksMap[item]
                                        .orEmpty(),

                                label = "Remarks"
                            ) {

                                remarksMap[item] = it
                            }
                        }
                    }
                }

            InputField(
                value = recommendation,
                label = "Recommendation"
            ) {

                recommendation = it
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(

                onClick = {

                    val site = selectedSite

                    if (site == null) {

                        Toast.makeText(
                            this@MainActivity,
                            "Please create/select a site first",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    val report = ServiceReport(

                        siteId = site.id,

                        visitType = visitType,

                        serviceNo =
                            if (visitType == "Service") {
                                serviceNumber
                            } else {
                                ""
                            },

                        system = selectedSystem,

                        recommendation =
                            recommendation,

                        date = today()
                    )

                    val reportId =
                        db.addService(report)

                    val savedReport =
                        report.copy(
                            id = reportId
                        )

                    val inspections =
                        INSPECTION_ITEMS[selectedSystem]
                            .orEmpty()
                            .map { item ->

                                Inspection(

                                    item = item,

                                    status =
                                        statusMap[item]
                                            .orEmpty(),

                                    remarks =
                                        remarksMap[item]
                                            .orEmpty()
                                )
                            }

                    Pdf.service(

                        ctx = this@MainActivity,

                        site = site,

                        r = savedReport,

                        items = inspections
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Service report saved and PDF created",
                        Toast.LENGTH_LONG
                    ).show()

                    navigate("home")
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Save Report & Generate PDF"
                )
            }
        }
    }

    @Composable
    private fun InstallationScreen(
        navigate: (String) -> Unit
    ) {

        val sites = db.sites()

        var selectedSite by remember {
            mutableStateOf(sites.firstOrNull())
        }

        var selectedSystem by remember {
            mutableStateOf(SYSTEMS.first())
        }

        var deviceType by remember {

            mutableStateOf(
                INSPECTION_ITEMS[
                    SYSTEMS.first()
                ]
                    .orEmpty()
                    .first()
            )
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

        var secret by remember {
            mutableStateOf("")
        }

        var assets by remember {
            mutableStateOf(
                listOf<Asset>()
            )
        }

        AppShell(

            title = "Installation / Asset Register",

            onBack = {
                navigate("home")
            }

        ) {

            Text(
                text = "Site",
                style = MaterialTheme.typography.titleMedium
            )

            if (sites.isEmpty()) {

                Text(
                    "No sites yet. Please create a site first."
                )

            } else {

                sites.forEach { site ->

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        RadioButton(

                            selected =
                                selectedSite?.id == site.id,

                            onClick = {
                                selectedSite = site
                            }
                        )

                        Text(
                            text = site.name,

                            modifier = Modifier
                                .padding(top = 12.dp)
                        )
                    }
                }
            }

            DropdownField(
                label = "System",
                value = selectedSystem,
                options = SYSTEMS
            ) {

                selectedSystem = it

                deviceType =
                    INSPECTION_ITEMS[it]
                        .orEmpty()
                        .first()
            }

            DropdownField(
                label = "Device Type",
                value = deviceType,
                options =
                    INSPECTION_ITEMS[
                        selectedSystem
                    ].orEmpty()
            ) {

                deviceType = it
            }

            InputField(
                brand,
                "Brand"
            ) {
                brand = it
            }

            InputField(
                model,
                "Model"
            ) {
                model = it
            }

            InputField(
                serial,
                "Serial Number"
            ) {
                serial = it
            }

            InputField(
                ip,
                "IP Address (if applicable)"
            ) {
                ip = it
            }

            InputField(
                location,
                "Location"
            ) {
                location = it
            }

            InputField(
                quantity,
                "Quantity"
            ) {
                quantity = it
            }

            InputField(
                installationDate,
                "Installation Date"
            ) {
                installationDate = it
            }

            InputField(
                remarks,
                "Remarks"
            ) {
                remarks = it
            }

            InputField(
                secret,
                "Device / NVR / Camera Password"
            ) {
                secret = it
            }

            Button(

                onClick = {

                    val site = selectedSite

                    if (site == null) {

                        Toast.makeText(
                            this@MainActivity,
                            "Please create/select a site first",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    val newAsset = Asset(

                        siteId = site.id,

                        system = selectedSystem,

                        type = deviceType,

                        brand = brand,

                        model = model,

                        serial = serial,

                        ip = ip,

                        location = location,

                        qty =
                            quantity.toIntOrNull()
                                ?: 1,

                        installDate =
                            installationDate,

                        remarks =
                            remarks,

                        secret =
                            secret
                    )

                    assets =
                        assets + newAsset

                    brand = ""
                    model = ""
                    serial = ""
                    ip = ""
                    location = ""
                    quantity = "1"
                    remarks = ""
                    secret = ""
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Add Device"
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                "Devices added: ${assets.size}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(

                onClick = {

                    val site = selectedSite

                    if (
                        site == null ||
                        assets.isEmpty()
                    ) {

                        Toast.makeText(
                            this@MainActivity,
                            "Select a site and add at least one device",
                            Toast.LENGTH_LONG
                        ).show()

                        return@Button
                    }

                    assets.forEach { asset ->

                        db.addAsset(asset)
                    }

                    Pdf.installation(

                        ctx = this@MainActivity,

                        site = site,

                        system = selectedSystem,

                        assets = assets
                    )

                    Toast.makeText(
                        this@MainActivity,
                        "Asset register saved and PDF created",
                        Toast.LENGTH_LONG
                    ).show()

                    navigate("home")
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Save Asset Register & Generate PDF"
                )
            }
        }
    }

    @Composable
    private fun ExistingSitesScreen(
        navigate: (String) -> Unit
    ) {

        var showDialog by remember {
            mutableStateOf(false)
        }

        var refresh by remember {
            mutableStateOf(0)
        }

        var name by remember {
            mutableStateOf("")
        }

        var job by remember {
            mutableStateOf("")
        }

        var customer by remember {
            mutableStateOf("")
        }

        var person by remember {
            mutableStateOf("")
        }

        var phone by remember {
            mutableStateOf("")
        }

        var address by remember {
            mutableStateOf("")
        }

        val sites =
            remember(refresh) {
                db.sites()
            }

        AppShell(

            title = "Existing Sites",

            onBack = {
                navigate("home")
            }

        ) {

            Button(
                onClick = {
                    showDialog = true
                }
            ) {

                Text(
                    "+ Add Site"
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyColumn(

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(sites) { site ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(12.dp)
                        ) {

                            Text(
                                site.name,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )

                            Text(
                                "Job: ${site.job}"
                            )

                            Text(
                                "Customer: ${site.customer}"
                            )

                            Text(
                                "Contact: ${site.person} / ${site.phone}"
                            )

                            Text(
                                site.address
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {

            AlertDialog(

                onDismissRequest = {
                    showDialog = false
                },

                title = {
                    Text(
                        "New Site"
                    )
                },

                text = {

                    Column {

                        InputField(
                            name,
                            "Site Name"
                        ) {
                            name = it
                        }

                        InputField(
                            job,
                            "Job Number"
                        ) {
                            job = it
                        }

                        InputField(
                            customer,
                            "Company / Customer"
                        ) {
                            customer = it
                        }

                        InputField(
                            person,
                            "Contact Person"
                        ) {
                            person = it
                        }

                        InputField(
                            phone,
                            "Contact Number"
                        ) {
                            phone = it
                        }

                        InputField(
                            address,
                            "Address"
                        ) {
                            address = it
                        }
                    }
                },

                confirmButton = {

                    Button(

                        onClick = {

                            db.addSite(

                                Site(

                                    name = name,

                                    job = job,

                                    customer = customer,

                                    person = person,

                                    phone = phone,

                                    address = address
                                )
                            )

                            name = ""
                            job = ""
                            customer = ""
                            person = ""
                            phone = ""
                            address = ""

                            showDialog = false

                            refresh++
                        }
                    ) {

                        Text(
                            "Save"
                        )
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {
                            showDialog = false
                        }

                    ) {

                        Text(
                            "Cancel"
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun TechnicianProfileScreen(
        navigate: (String) -> Unit
    ) {

        var company by remember {
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

        AppShell(

            title = "Technician Profile",

            onBack = {
                navigate("home")
            }

        ) {

            InputField(
                company,
                "Company Name"
            ) {
                company = it
            }

            InputField(
                technicianName,
                "Technician Name"
            ) {
                technicianName = it
            }

            InputField(
                email,
                "Email"
            ) {
                email = it
            }

            InputField(
                phone,
                "Phone Number"
            ) {
                phone = it
            }

            InputField(
                epf,
                "EPF Number"
            ) {
                epf = it
            }

            InputField(
                password,
                "Password"
            ) {
                password = it
            }

            Button(

                onClick = {

                    db.saveTechnician(

                        company,

                        technicianName,

                        email,

                        phone,

                        epf,

                        password
                    )

                    Toast.makeText(

                        this@MainActivity,

                        "Technician profile saved",

                        Toast.LENGTH_SHORT

                    ).show()
                }

            ) {

                Text(
                    "Save Profile"
                )
            }
        }
    }

    @Composable
    private fun InputField(
        value: String,
        label: String,
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
                .padding(vertical = 4.dp),

            singleLine = true
        )
    }

    @Composable
    private fun DropdownField(
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
                .padding(vertical = 4.dp)
        ) {

            Text(
                label,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium
            )

            OutlinedButton(

                onClick = {
                    expanded = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    value
                )
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

    private fun today(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }
}
