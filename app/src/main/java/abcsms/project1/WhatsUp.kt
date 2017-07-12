import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.telephony.PhoneNumberUtils
import android.widget.Toast

private fun openWhatsApp() {
    val isWhatsAppInstalled = whatsAppInstalledOrNot("com.whatsapp")
    val smsNumber = getClearTextNumber(editText.getText().toString())
    if (isWhatsAppInstalled) {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net")//phone number without "+" prefix
        sendIntent.`package` = "com.whatsapp"
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    } else {
        val uri = Uri.parse("market://details?id=com.whatsapp")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        Toast.makeText(this, "WhatsApp not Installed",
                Toast.LENGTH_SHORT).show()
        startActivity(goToMarket)
    }
}

private fun getClearTextNumber(number: String): String {
    return number.replace("+", "").replace(" ", "")
}

private fun whatsAppInstalledOrNot(uri: String): Boolean {
    val pm = getPackageManager()
    var app_installed: Boolean
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        app_installed = true
    } catch (e: PackageManager.NameNotFoundException) {
        app_installed = false
    }

    return app_installed
}