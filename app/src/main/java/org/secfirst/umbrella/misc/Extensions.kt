package org.secfirst.umbrella.misc

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerFontProvider
import com.itextpdf.tool.xml.XMLWorkerHelper
import com.itextpdf.tool.xml.html.CssAppliersImpl
import com.itextpdf.tool.xml.html.Tags
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext
import com.jakewharton.processphoenix.ProcessPhoenix
import org.apache.commons.io.FileUtils
import org.jsoup.select.Elements
import org.secfirst.umbrella.BuildConfig
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.data.database.lesson.Module
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.feature.main.MainActivity
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.reflect.KClass


const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1

fun MainActivity.requestExternalStoragePermission() {

    ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_EXTERNAL_STORAGE)
}

fun doRestartApplication(context: Context) {
    val tourIntent = Intent(context, MainActivity::class.java)
    tourIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    ProcessPhoenix.triggerRebirth(context, tourIntent)
}

fun Context.isInternetConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return (activeNetwork != null && activeNetwork.isConnected)
}

fun <T : Any> parseYmlFile(file: File, c: KClass<T>): T {
    val mapper = ObjectMapper(YAMLFactory())
    mapper.registerModule(KotlinModule())
    return file.bufferedReader().use { mapper.readValue(it.readText(), c.java) }
}

fun setMaskMode(activity: Activity, masked: Boolean) {
    val packageName = BuildConfig.APPLICATION_ID
    val disableNames = ArrayList<String>()
    disableNames.add("$packageName.MainActivityNormal")
    disableNames.add("$packageName.MainActivityCalculator")
    val activeName = disableNames.removeAt(if (masked) 1 else 0)

    activity.packageManager.setComponentEnabledSetting(
            ComponentName(packageName, activeName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)

    for (i in disableNames.indices) {
        activity.packageManager.setComponentEnabledSetting(
                ComponentName(packageName, disableNames[i]),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
}


fun htmlToPdf(doc: org.jsoup.nodes.Document, file: FileOutputStream) {
    //pdf
    val document = Document()
    val writer = PdfWriter.getInstance(document, file)
    document.open()

    val fontProvider = XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS)
    fontProvider.register("/assets/fonts/Roboto-Regular.ttf")
    val cssAppliers = CssAppliersImpl(fontProvider)

    // HTML
    val htmlContext = HtmlPipelineContext(cssAppliers)
    htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory())

    XMLWorkerHelper.getInstance().parseXHtml(writer, document, ByteArrayInputStream(doc.toString().toByteArray(Charsets.UTF_8)), Charset.forName("UTF-8"), fontProvider)

    document.close()
    file.close()
}

fun createDocument(doc: org.jsoup.nodes.Document, filename: String, type: String, context: Context): File {
    val img: Elements = doc.getElementsByTag("img")
    doc.body().attr("style", "font-family: Roboto")
    lateinit var src: String
    lateinit var base64img: String

    img.forEach {
        src = it.absUrl("src").replace("file://", "")

        if (type == context.getString(R.string.html_name)) {
            base64img = encodeToBase64(File(src))
            src = "data:image/png;base64, $base64img"
        }
        it.attr("src", src)
    }

    val fileToShare = File(context.cacheDir, filename + ".${type.toLowerCase()}")

    when (type) {
        context.getString(R.string.html_name) -> {
            val writer = BufferedWriter(FileWriter(fileToShare))
            writer.write(doc.toString())
            writer.flush()
            writer.close()
        }
        context.getString(R.string.pdf_name) -> {
            htmlToPdf(doc, FileOutputStream(fileToShare))
        }
    }
    return fileToShare
}

fun appContext(): Context = UmbrellaApplication.instance.applicationContext

fun encodeToBase64(file: File) = Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT)
        ?: ""

enum class TypeHelper(val value: String) {
    MODULE(Module::class.java.name),
    SUBJECT(Subject::class.java.name),
    DIFFICULTY(Difficulty::class.java.name)
}

fun Context.setLocale(lang: String) {
    val res = resources
    val dm = res.displayMetrics
    val conf = res.configuration
    conf.locale = Locale(lang)
    res.updateConfiguration(conf, dm)
}

fun deviceLanguage(): String {
    val defaultLanguage = Resources.getSystem().configuration.locale.language

    if (defaultLanguage == "gb")
        return "en"
    else if (defaultLanguage.contains("zh", true))
        return "zh-Hant"
    return defaultLanguage
}

fun Context.createNotification(title: String, body: String, channelId: String, id: Int) {
    val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.umbrella190)
            .build()
    val notificationManagerCompat = NotificationManagerCompat.from(this)
    notificationManagerCompat.notify(id, notification)
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        val startIndexOfLink = this.text.toString().indexOf(link.first)
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}