package abcsms.project1

import android.content.Context
import org.jetbrains.anko.Android
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.supercsv.cellprocessor.CellProcessorAdaptor
import org.supercsv.cellprocessor.FmtDate
import org.supercsv.cellprocessor.constraint.NotNull
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvBeanWriter
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvBeanWriter
import org.supercsv.prefs.CsvPreference
import org.supercsv.util.CsvContext
import rx.Observable
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CSVUtils private constructor() {

    companion object {
        private var instance: CSVUtils? = null
        private lateinit var context: Context

        fun getInstance(context: Context): CSVUtils {
            if (instance == null) {
                instance = CSVUtils()
                this.context = context
            }
            return instance!!
        }
    }

    fun saveCSVFileAndShare(list: ArrayList<History>): Observable<Boolean> {
        val baseDir = context.filesDir.absolutePath
        val fileName = "History.csv" //simpleDate.format(Date())
        val filePath = baseDir + File.separator + fileName
        val file = File(filePath)
        val csvPref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE
        val headers = arrayOf("Name", "Number", "Created")

        val cellProcessor = arrayOf<CellProcessor>(NotNull(), NotNull(), object : CellProcessorAdaptor() {
            override fun <T : Any?> execute(value: Any?, context: CsvContext?): T {
                val longVal: Long? = (value as? Long)?.toLong()
                val s = DateTime(longVal, DateTimeZone.getDefault())
                return this.next.execute(s.toString("yyyy-MM-dd HH:mm:ss"), context)
            }
        })

        //Observable.from(list).
        //gen History
        (0..10000).mapTo(list) { History(name = "User$it", number = "Number $it", created = Date().time) }


        val exists = file.exists()
        val directory = file.isDirectory

        val b = !exists && !directory
        return Observable.just(1)
                .map { if (exists && !directory) CsvBeanWriter(FileWriter(file, true), csvPref) else CsvBeanWriter(FileWriter(file), csvPref) }
                .map {
                    it.use {
                        if (b)
                            it.writeHeader(*headers)
                        val writer = it
                        list.forEach { writer.write(it, headers, cellProcessor) }
                    }
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
               // .subscribe({ println("complete") }, { it?.printStackTrace() })
    }


}