package dev.gitlive.firebase.perf

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.perf.metrics.HttpMetric
import dev.gitlive.firebase.perf.metrics.Trace

/** Returns the [FirebasePerformance] instance of the default [FirebaseApp]. */
expect val Firebase.performance: FirebasePerformance

/** Returns the [FirebasePerformance] instance of a given [FirebaseApp]. */
expect fun Firebase.performance(app: FirebaseApp): FirebasePerformance

expect class FirebasePerformance {

    fun newTrace(traceName: String): Trace

    fun isPerformanceCollectionEnabled(): Boolean

    fun setPerformanceCollectionEnabled(enable: Boolean)

    fun newHttpMetric(url: Any, httpMethod: String): HttpMetric

    fun newHttpMetric(url: String, httpMethod: String): HttpMetric

    fun getAttributes(): MutableMap<String, String>

    fun getAttribute(attribute: String): String?

    fun putAttribute(attribute: String, value: String)

    fun removeAttribute(attribute: String)
}

expect open class FirebasePerformanceException : FirebaseException