package dev.gitlive.firebase.perf

import cocoapods.FirebasePerformance.FIRPerformance
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.perf.metrics.HttpMetric
import dev.gitlive.firebase.perf.metrics.Trace

actual val Firebase.performance get() =
    FirebasePerformance(FIRPerformance.sharedInstance())

actual fun Firebase.performance(app: FirebaseApp) =
    FirebasePerformance(FIRPerformance.sharedInstance())

actual class FirebasePerformance(private val ios: FIRPerformance) {

    actual fun newTrace(traceName: String): Trace = Trace(ios.traceWithName(traceName))

    actual fun isPerformanceCollectionEnabled(): Boolean {
        return ios.isDataCollectionEnabled()
    }

    actual fun setPerformanceCollectionEnabled(enable: Boolean) {
        ios.setDataCollectionEnabled(enable)
    }

    actual fun newHttpMetric(url: Any, httpMethod: String) : HttpMetric {
        return HttpMetric()
    }

    actual fun newHttpMetric(url: String, httpMethod: String): HttpMetric {
        return HttpMetric()
    }
    actual fun getAttributes(): MutableMap<String, String> = mutableMapOf()

    actual fun getAttribute(attribute: String): String? = null

    actual fun putAttribute(attribute: String, value: String) {
    }

    actual fun removeAttribute(attribute: String) {
    }
}

actual open class FirebasePerformanceException(message: String) : FirebaseException(message)