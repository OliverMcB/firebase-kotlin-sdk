package dev.gitlive.firebase.database

import dev.gitlive.firebase.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.*
import kotlin.test.*

expect val emulatorHost: String
expect val context: Any
expect fun runTest(test: suspend () -> Unit)

class FirebaseDatabaseTest {

    @Serializable
    data class FirebaseDatabaseChildTest(val prop1: String? = null, val time: Double = 0.0)

    @Serializable
    data class DatabaseTest(val title: String, val likes: Int = 0)

    @BeforeTest
    fun initializeFirebase() {
        Firebase
            .takeIf { Firebase.apps(context).isEmpty() }
            ?.apply {
                initialize(
                    context,
                    FirebaseOptions(
                        applicationId = "1:846484016111:ios:dd1f6688bad7af768c841a",
                        apiKey = "AIzaSyCK87dcMFhzCz_kJVs2cT2AVlqOTLuyWV0",
                        databaseUrl = "http://fir-kotlin-sdk.firebaseio.com",
                        storageBucket = "fir-kotlin-sdk.appspot.com",
                        projectId = "fir-kotlin-sdk",
                        gcmSenderId = "846484016111"
                    )
                )
                Firebase.database.useEmulator(emulatorHost, 9000)
            }
    }

    @Test
    fun testSetValue() = runTest {
        val testValue = "test"
        val testReference = Firebase.database.reference("testPath")

        testReference.setValue(testValue)

        val testReferenceValue = testReference
            .valueEvents
            .first()
            .value<String>()

        assertEquals(testValue, testReferenceValue)
    }

    @Test
    fun testChildCount() = runTest {
        setupRealtimeData()
        val dataSnapshot = Firebase.database
            .reference("FirebaseRealtimeDatabaseTest")
            .valueEvents
            .first()

        val firebaseDatabaseChildCount = dataSnapshot.children.count()
        assertEquals(3, firebaseDatabaseChildCount)
    }

    @Test
    fun testBasicIncrementTransaction() = runTest {
        val data = DatabaseTest("PostOne", 2)
        val userRef = Firebase.database.reference("users/user_1/post_id_1")
        setupDatabase(userRef, data, DatabaseTest.serializer())

        // Check database before transaction
        val userDocBefore = userRef.valueEvents.first().value(DatabaseTest.serializer())
        assertEquals(data.title, userDocBefore.title)
        assertEquals(data.likes, userDocBefore.likes)

        // Run transaction
        val transactionSnapshot = userRef.runTransaction(DatabaseTest.serializer()) { DatabaseTest(data.title, it.likes + 1) }
        val userDocAfter = transactionSnapshot.value(DatabaseTest.serializer())

        // Check the database after transaction
        assertEquals(data.title, userDocAfter.title)
        assertEquals(data.likes + 1, userDocAfter.likes)

        // cleanUp Firebase
        cleanUp()
    }

    @Test
    fun testBasicDecrementTransaction() = runTest {
        val data = DatabaseTest("PostTwo", 2)
        val userRef = Firebase.database.reference("users/user_1/post_id_2")
        setupDatabase(userRef, data, DatabaseTest.serializer())

        // Check database before transaction
        val userDocBefore = userRef.valueEvents.first().value(DatabaseTest.serializer())
        assertEquals(data.title, userDocBefore.title)
        assertEquals(data.likes, userDocBefore.likes)

        // Run transaction
        val transactionSnapshot = userRef.runTransaction(DatabaseTest.serializer()) { DatabaseTest(data.title, it.likes - 1) }
        val userDocAfter = transactionSnapshot.value(DatabaseTest.serializer())

        // Check the database after transaction
        assertEquals(data.title, userDocAfter.title)
        assertEquals(data.likes - 1, userDocAfter.likes)

        // cleanUp Firebase
        cleanUp()
    }

    private suspend fun setupRealtimeData() {
        val firebaseDatabaseTestReference = Firebase.database
            .reference("FirebaseRealtimeDatabaseTest")

        val firebaseDatabaseChildTest1 = FirebaseDatabaseChildTest("aaa")
        val firebaseDatabaseChildTest2 = FirebaseDatabaseChildTest("bbb")
        val firebaseDatabaseChildTest3 = FirebaseDatabaseChildTest("ccc")

        firebaseDatabaseTestReference.child("1").setValue(firebaseDatabaseChildTest1)
        firebaseDatabaseTestReference.child("2").setValue(firebaseDatabaseChildTest2)
        firebaseDatabaseTestReference.child("3").setValue(firebaseDatabaseChildTest3)
    }

    private fun cleanUp() {
        Firebase
            .takeIf { Firebase.apps(context).isNotEmpty() }
            ?.apply { app.delete() }
    }

    private suspend fun <T> setupDatabase(ref: DatabaseReference, data: T, strategy: SerializationStrategy<T>) {
        try {
            ref.setValue(strategy, data)
        } catch (err: DatabaseException) {
            println(err)
        }
    }

}