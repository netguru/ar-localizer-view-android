package com.netguru.android.arlocalizeralternative.common

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OptionalTest {

    @Test
    fun `Should transform object to optional Some when toOptional called`() {
        val testObject = "Test"
        val optionalTestObject = testObject.toOptional()

        assertTrue(optionalTestObject is Optional.Some)
        assertEquals(testObject, optionalTestObject.toNullable())
    }

    @Test
    fun `Should transform null to optional None when toOptional called`() {
        val testObject: String? = null
        val optionalTestObject = testObject.toOptional()

        assertTrue(optionalTestObject == Optional.None)
        assertEquals(testObject, optionalTestObject.toNullable())
    }

    @Test
    fun `Should return underlying object when toNullable called`() {
        val underlyingString = "Test"
        val nonNullOptionalWithUnderlyingString: Optional<String> = Optional.Some(
            underlyingString
        )
        assertEquals(underlyingString, nonNullOptionalWithUnderlyingString.toNullable())

        val nullStringOptional: Optional<String> = Optional.None
        assertEquals(null, nullStringOptional.toNullable())
    }

    @Test
    fun `Map should transform underlying object if it is not null else return None`() {
        val f: (Int) -> Int = { it + 1 }
        val absentVal: Optional<Int> = Optional.None
        val presentVal: Optional<Int> = Optional.Some(1)

        assertEquals(absentVal.map(f), Optional.None)
        assertEquals(presentVal.map(f), Optional.Some(2))
    }

    @Test
    fun `Map transformation can return null`() {
        val f: (Int) -> Int? = { null }
        val absentVal: Optional<Int> = Optional.None
        val presentVal: Optional<Int> = Optional.Some(1)

        assertEquals(absentVal.map(f), Optional.None)
        assertEquals(presentVal.map(f), Optional.None)
    }

    @Test
    fun `FlatMap should transform underlying object if it is not null else return None`() {
        val f: (Int) -> Optional<Int> = { (it + 1).toOptional() }
        val absentVal: Optional<Int> = Optional.None
        val presentVal: Optional<Int> = Optional.Some(1)

        assertEquals(absentVal.flatMap(f), Optional.None)
        assertEquals(presentVal.flatMap(f), Optional.Some(2))
    }

    @Test
    fun `FlatMap transformation can return None Optional`() {
        val f: (Int) -> Optional<Int> = { Optional.None }
        val absentVal: Optional<Int> = Optional.None
        val presentVal: Optional<Int> = Optional.Some(1)

        assertEquals(absentVal.flatMap(f), Optional.None)
        assertEquals(presentVal.flatMap(f), Optional.None)
    }

    @Test
    fun `Filter optional on observable should filter none and pass value into stream`() {
        val firstValue = "test"
        val secondValue = "test2"
        val test = Observable.fromArray(
            "test".toOptional(),
            Optional.None,
            "test2".toOptional()
        )
            .filterOptionalNone()
            .test()

        test.assertValues(firstValue, secondValue)
    }

    @Test
    fun `Filter optional on single should filter none and pass value into stream`() {
        val firstValue = "test"
        val testOptionalSome = Single.just(firstValue.toOptional())
            .filterOptionalNone()
            .test()

        testOptionalSome.assertValues(firstValue)

        val testOptionalNone = Single.just(Optional.None as Optional<String>)
            .filterOptionalNone()
            .test()

        testOptionalNone.assertNoValues()
    }

    @Test
    fun `Filter optional on maybe should filter none and pass value into stream`() {
        val firstValue = "test"
        val testOptionalSome = Maybe.just(firstValue.toOptional())
            .filterOptionalNone()
            .test()

        testOptionalSome.assertValues(firstValue)

        val testOptionalNone = Maybe.just(Optional.None as Optional<String>)
            .filterOptionalNone()
            .test()

        testOptionalNone.assertNoValues()
    }
}
