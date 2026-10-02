package com.techreport.app

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File

object Pdf {

    fun service(
        ctx: Context,
        site: Site,
        r: ServiceReport,
        items: List<Inspection>
    ): File {

        val doc = PdfDocument()

        val page = doc.startPage(
            PdfDocument.PageInfo.Builder(595, 842, 1).create()
        )

        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(106, 27, 154)
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
        }

        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 11f
        }

        canvas.drawText(
            "TechReport - Service Report",
            32f,
            45f,
            titlePaint
        )

        var y = 72f

        val header = listOf(
            "Site: ${site.name}",
            "Job No: ${site.job}",
            "Customer: ${site.customer}",
            "Visit: ${r.visitType} ${
                if (r.serviceNo.isNotBlank()) "- ${r.serviceNo}" else ""
            }",
            "System: ${r.system}",
            "Date: ${r.date}"
        )

        for (line in header) {
            canvas.drawText(line, 32f, y, textPaint)
            y += 18f
        }

        y += 8f

        canvas.drawText("Inspection", 32f, y, textPaint)
        y += 20f

        for (item in items) {
            if (y > 790f) break

            canvas.drawText(
                "${item.item}: ${item.status}  ${item.remarks}",
                38f,
                y,
                textPaint
            )

            y += 17f
        }

        if (y < 810f) {
            y += 12f

            canvas.drawText(
                "Recommendation: ${r.recommendation}",
                32f,
                y,
                textPaint
            )
        }

        doc.finishPage(page)

        return save(
            ctx,
            doc,
            "service_${r.id}.pdf"
        )
    }

    fun installation(
        ctx: Context,
        site: Site,
        system: String,
        assets: List<Asset>
    ): File {

        val doc = PdfDocument()

        val page = doc.startPage(
            PdfDocument.PageInfo.Builder(595, 842, 1).create()
        )

        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(106, 27, 154)
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
        }

        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 10f
        }

        canvas.drawText(
            "TechReport - Installation / Asset Register",
            25f,
            45f,
            titlePaint
        )

        var y = 72f

        canvas.drawText(
            "Site: ${site.name}   Job: ${site.job}",
            25f,
            y,
            textPaint
        )

        y += 18f

        canvas.drawText(
            "Customer: ${site.customer}   System: $system",
            25f,
            y,
            textPaint
        )

        y += 25f

        for (asset in assets) {

            if (y > 780f) break

            canvas.drawText(
                "${asset.type} | ${asset.brand} ${asset.model}",
                25f,
                y,
                textPaint
            )

            y += 16f

            canvas.drawText(
                "S/N: ${asset.serial} | IP: ${asset.ip}",
                32f,
                y,
                textPaint
            )

            y += 16f

            canvas.drawText(
                "Location: ${asset.location}  Qty: ${asset.qty}",
                32f,
                y,
                textPaint
            )

            y += 16f

            canvas.drawText(
                "Date: ${asset.installDate}  ${asset.remarks}",
                32f,
                y,
                textPaint
            )

            y += 20f
        }

        doc.finishPage(page)

        return save(
            ctx,
            doc,
            "installation_${site.id}.pdf"
        )
    }

    private fun save(
        ctx: Context,
        document: PdfDocument,
        name: String
    ): File {

        val file = File(
            ctx.getExternalFilesDir(null),
            name
        )

        file.outputStream().use {
            document.writeTo(it)
        }

        document.close()

        return file
    }
}
