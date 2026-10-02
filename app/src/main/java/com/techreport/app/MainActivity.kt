package com.techreport.app

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object Pdf {

    fun service(
        siteName: String,
        jobNumber: String,
        date: String,
        company: String,
        contactNumber: String,
        contactPerson: String,
        address: String,
        visitType: String,
        serviceNumber: String,
        system: String,
        recommendation: String
    ) {

        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(
            595,
            842,
            1
        ).create()

        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 20f

        canvas.drawText(
            "TECHREPORT - SERVICE REPORT",
            40f,
            50f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f

        var y = 85f

        fun line(label: String, value: String) {
            canvas.drawText(
                "$label: $value",
                40f,
                y,
                paint
            )
            y += 22f
        }

        line("Site Name", siteName)
        line("Job Number", jobNumber)
        line("Date", date)
        line("Company / Customer", company)
        line("Contact Number", contactNumber)
        line("Contact Person", contactPerson)
        line("Address", address)
        line("Visit Type", visitType)

        if (visitType == "Service") {
            line("Service Number", serviceNumber)
        }

        line("System", system)

        y += 15f

        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(
            "Recommendation",
            40f,
            y,
            paint
        )

        y += 22f

        paint.typeface = Typeface.DEFAULT

        val recommendationLines =
            recommendation.chunked(75)

        recommendationLines.forEach {
            canvas.drawText(
                it,
                40f,
                y,
                paint
            )
            y += 18f
        }

        y += 25f

        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "Technician / Manager Approval",
            40f,
            y,
            paint
        )

        y += 30f

        paint.typeface = Typeface.DEFAULT

        canvas.drawText(
            "Technician Signature: __________________________",
            40f,
            y,
            paint
        )

        y += 30f

        canvas.drawText(
            "Manager Signature: _____________________________",
            40f,
            y,
            paint
        )

        document.finishPage(page)

        savePdf(
            document,
            "TechReport_Service_${safeFileName(siteName)}.pdf"
        )
    }

    fun installation(
        siteName: String,
        jobNumber: String,
        date: String,
        company: String,
        contactNumber: String,
        contactPerson: String,
        address: String,
        system: String,
        assets: List<String>
    ) {

        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(
            595,
            842,
            1
        ).create()

        val page = document.startPage(pageInfo)

        val canvas = page.canvas
        val paint = Paint()

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 20f

        canvas.drawText(
            "TECHREPORT - INSTALLATION / ASSET REPORT",
            30f,
            50f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f

        var y = 85f

        fun line(label: String, value: String) {

            if (y > 790f) {
                return
            }

            canvas.drawText(
                "$label: $value",
                35f,
                y,
                paint
            )

            y += 20f
        }

        line("Site Name", siteName)
        line("Job Number", jobNumber)
        line("Date", date)
        line("Company / Customer", company)
        line("Contact Number", contactNumber)
        line("Contact Person", contactPerson)
        line("Address", address)
        line("System", system)

        y += 15f

        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "Installed Equipment",
            35f,
            y,
            paint
        )

        y += 25f

        paint.typeface = Typeface.DEFAULT

        assets.forEachIndexed { index, asset ->

            if (y > 790f) {
                return@forEachIndexed
            }

            val lines = asset.chunked(75)

            canvas.drawText(
                "${index + 1}. ${lines.firstOrNull() ?: ""}",
                35f,
                y,
                paint
            )

            y += 18f

            lines.drop(1).forEach { extraLine ->

                if (y <= 790f) {

                    canvas.drawText(
                        "   $extraLine",
                        35f,
                        y,
                        paint
                    )

                    y += 18f
                }
            }

            y += 5f
        }

        document.finishPage(page)

        savePdf(
            document,
            "TechReport_Installation_${safeFileName(siteName)}.pdf"
        )
    }

    private fun savePdf(
        document: PdfDocument,
        fileName: String
    ) {

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val resolver =
                    AppContextHolder.context.contentResolver

                val values = ContentValues().apply {

                    put(
                        MediaStore.Downloads.DISPLAY_NAME,
                        fileName
                    )

                    put(
                        MediaStore.Downloads.MIME_TYPE,
                        "application/pdf"
                    )

                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                }

                val uri = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )

                if (uri != null) {

                    resolver.openOutputStream(uri)?.use { output ->

                        document.writeTo(output)
                    }
                }

            } else {

                val directory =
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )

                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val file =
                    File(directory, fileName)

                FileOutputStream(file).use { output ->

                    document.writeTo(output)
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

        } finally {

            document.close()
        }
    }

    private fun safeFileName(
        name: String
    ): String {

        return name
            .ifBlank { "Site" }
            .replace(
                Regex("[^A-Za-z0-9._-]"),
                "_"
            )
    }
}
