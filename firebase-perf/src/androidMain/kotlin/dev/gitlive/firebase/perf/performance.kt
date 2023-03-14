package dev.gitlive.firebase.perf

import com.google.firebase.FirebaseException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.perf.metrics.HttpMetric
import dev.gitlive.firebase.perf.metrics.Trace
import java.net.URL

actual val Firebase.performance get() =
    FirebasePerformance(com.google.firebase.perf.FirebasePerformance.getInstance())

actual fun Firebase.performance(app: FirebaseApp) =
    FirebasePerformance(app.android.get(com.google.firebase.perf.FirebasePerformance::class.java))

actual class FirebasePerformance(private val android: com.google.firebase.perf.FirebasePerformance){

    actual fun newTrace(traceName: String): Trace = Trace(android.newTrace(traceName))

    actual fun isPerformanceCollectionEnabled() = android.isPerformanceCollectionEnabled

    actual fun setPerformanceCollectionEnabled(enable: Boolean) {
        android.isPerformanceCollectionEnabled = enable
    }

    actual fun newHttpMetric(url: Any, httpMethod: String): HttpMetric = HttpMetric(android.newHttpMetric(url as URL, httpMethod))

    actual fun newHttpMetric(url: String, httpMethod: String): HttpMetric = HttpMetric(android.newHttpMetric(url, httpMethod))

    actual fun getAttributes(): MutableMap<String, String> = android.attributes

    actual fun getAttribute(attribute: String) = android.getAttribute(attribute)

    actual fun putAttribute(attribute: String, value: String) = android.putAttribute(attribute, value)

    actual fun removeAttribute(attribute: String) = android.removeAttribute(attribute)
}

actual open class FirebasePerformanceException(message: String) : FirebaseException(message)
