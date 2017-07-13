package abcsms.project1

import android.content.Context
import org.supercsv.cellprocessor.constraint.NotNull
import org.supercsv.cellprocessor.ift.CellProcessor
import org.supercsv.io.CsvBeanWriter
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvBeanWriter
import org.supercsv.prefs.CsvPreference
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


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

    fun saveCSVFileAndShare(list: ArrayList<History>) {

        val listToSave = ArrayList<HistoryMode>()
        val simpleDate: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

       // (0..10000).mapTo(list) { History(name = "User$it", number = "Number $it", created = Date().time) }
        list.mapTo(listToSave) { HistoryMode(it.name, it.number, simpleDate.format(it.created)) }
        val cellProcessor = arrayOf<CellProcessor>(NotNull(), NotNull(), NotNull())
        val baseDir = context.filesDir.absolutePath
        val fileName = "History.csv" //simpleDate.format(Date())
        val filePath = baseDir + File.separator + fileName
        val file = File(filePath)
        val csvPref = CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE
        val headers = arrayOf("Name", "Number", "Created")

        if (file.exists() && !file.isDirectory) {
            val csvWriter = CsvListWriter(FileWriter(file, true), csvPref)
            for (it in listToSave) {
                csvWriter.write(it, headers, cellProcessor)
            }
            csvWriter.close()
        } else {
            val csvWriter: ICsvBeanWriter = CsvBeanWriter(FileWriter(file), csvPref)
            csvWriter.writeHeader(*headers)
            for (it in listToSave) {
                csvWriter.write(it, headers, cellProcessor)
            }
            csvWriter.close()
        }

    }
}