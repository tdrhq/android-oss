package com.kickstarter.libs.utils

import android.content.Context
import android.util.Pair
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.R
import com.kickstarter.libs.KSString
import com.kickstarter.libs.models.OptimizelyExperiment
import com.kickstarter.libs.utils.RewardUtils.deadlineCountdownDetail
import com.kickstarter.libs.utils.RewardUtils.deadlineCountdownUnit
import com.kickstarter.libs.utils.RewardUtils.deadlineCountdownValue
import com.kickstarter.libs.utils.RewardUtils.hasBackers
import com.kickstarter.libs.utils.RewardUtils.isAvailable
import com.kickstarter.libs.utils.RewardUtils.isExpired
import com.kickstarter.libs.utils.RewardUtils.isItemized
import com.kickstarter.libs.utils.RewardUtils.isLimitReached
import com.kickstarter.libs.utils.RewardUtils.isLimited
import com.kickstarter.libs.utils.RewardUtils.isNoReward
import com.kickstarter.libs.utils.RewardUtils.isReward
import com.kickstarter.libs.utils.RewardUtils.isShippable
import com.kickstarter.libs.utils.RewardUtils.isTimeLimited
import com.kickstarter.libs.utils.RewardUtils.rewardAmountByVariant
import com.kickstarter.libs.utils.RewardUtils.shippingSummary
import com.kickstarter.libs.utils.RewardUtils.timeInSecondsUntilDeadline
import com.kickstarter.mock.factories.LocationFactory
import com.kickstarter.mock.factories.ProjectFactory
import com.kickstarter.mock.factories.RewardFactory
import junit.framework.TestCase
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import org.junit.Before
import org.junit.Test
import java.util.*

class RewardUtilsTest : KSRobolectricTestCase() {

    private lateinit var context : Context
    private lateinit var ksString : KSString
    private lateinit var date : Date
    private lateinit var currentDate : MutableDateTime

    @Before
    fun setUpTests() {
        context = context()
        ksString = ksString()
        date = DateTime.now().toDate()
        currentDate = MutableDateTime(date)
    }

    @Test
    fun testIsAvailable() {
        TestCase.assertTrue(isAvailable(ProjectFactory.project(), RewardFactory.reward()))
        TestCase.assertFalse(isAvailable(ProjectFactory.project(), RewardFactory.ended()))
        TestCase.assertFalse(isAvailable(ProjectFactory.project(), RewardFactory.limitReached()))
        TestCase.assertFalse(isAvailable(ProjectFactory.successfulProject(), RewardFactory.reward()))
        TestCase.assertFalse(isAvailable(ProjectFactory.successfulProject(), RewardFactory.ended()))
        TestCase.assertFalse(isAvailable(ProjectFactory.successfulProject(), RewardFactory.limitReached()))
    }

    @Test
    fun testDeadlineCountdownDetailWithDaysLeft() {
        currentDate.addDays(31)
        val rewardWith30DaysRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownDetail(rewardWith30DaysRemaining, context, ksString), "days to go")
    }

    @Test
    fun testDeadlineCountdownDetailWithHoursLeft() {
        currentDate.addHours(3)
        val rewardWith30DaysRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownDetail(rewardWith30DaysRemaining, context, ksString), "hours to go")
    }

    @Test
    fun testDeadlineCountdownDetailWithSecondsLeft() {
        currentDate.addSeconds(3)
        val rewardWith30DaysRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownDetail(rewardWith30DaysRemaining, context, ksString), "secs to go")
    }

    @Test
    fun testDeadlineCountdownUnitWithDaysLeft() {
        currentDate.addDays(31)
        val rewardWithDaysRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownUnit(rewardWithDaysRemaining, context), "days")
    }

    @Test
    fun testDeadlineCountdownUnitWithHoursLeft() {
        currentDate.addHours(3)
        val rewardWithHoursRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownUnit(rewardWithHoursRemaining, context), "hours")
    }

    @Test
    fun testDeadlineCountdownUnitWithMinutesLeft() {
        currentDate.addMinutes(3)
        val rewardWithMinutesRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownUnit(rewardWithMinutesRemaining, context), "mins")
    }

    @Test
    fun testDeadlineCountdownUnitWithSecondsLeft() {
        currentDate.addSeconds(30)
        val rewardWithSecondsRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownUnit(rewardWithSecondsRemaining, context), "secs")
    }

    @Test
    fun testDeadlineCountdownValueWithMinutesLeft() {
        currentDate.addSeconds(300)
        val rewardWithMinutesRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownValue(rewardWithMinutesRemaining), 5)
    }

    @Test
    fun testDeadlineCountdownValueWithHoursLeft() {
        currentDate.addSeconds(3600)
        val rewardWithHoursRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownValue(rewardWithHoursRemaining), 60)
    }

    @Test
    fun testDeadlineCountdownValueWithDaysLeft() {
        currentDate.addSeconds(86400)
        val rewardWithDaysRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownValue(rewardWithDaysRemaining), 24)
    }

    @Test
    fun testDeadlineCountdownValueWithSecondsLeft() {
        currentDate.addSeconds(30)
        val rewardWithSecondsRemaining = RewardFactory.reward().toBuilder()
                .endsAt(currentDate.toDateTime())
                .build()
        TestCase.assertEquals(deadlineCountdownValue(rewardWithSecondsRemaining), 30)
    }

    @Test
    fun testHasBackers() {
        TestCase.assertTrue(hasBackers(RewardFactory.backers()))
        TestCase.assertFalse(hasBackers(RewardFactory.noBackers()))
    }

    @Test
    fun testIsLimited() {
        val rewardWithRemaining = RewardFactory.reward().toBuilder()
                .remaining(5)
                .limit(10)
                .build()
        TestCase.assertTrue(isLimited(rewardWithRemaining))
        val rewardWithNoneRemaining = RewardFactory.reward().toBuilder()
                .remaining(0)
                .limit(10)
                .build()
        TestCase.assertFalse(isLimited(rewardWithNoneRemaining))
        val rewardWithNoLimitAndRemainingSet = RewardFactory.reward().toBuilder()
                .remaining(null)
                .limit(null)
                .build()
        TestCase.assertFalse(isLimited(rewardWithNoLimitAndRemainingSet))
    }

    @Test
    fun testIsItemized() {
        TestCase.assertFalse(isItemized(RewardFactory.reward()))
        TestCase.assertTrue(isItemized(RewardFactory.itemized()))
    }

    @Test
    fun testIsLimitReachedWhenLimitSetAndRemainingIsZero() {
        val reward = RewardFactory.reward().toBuilder()
                .limit(100)
                .remaining(0)
                .build()
        TestCase.assertTrue(isLimitReached(reward))
    }

    @Test
    fun testIsLimitNotReachedWhenLimitSetButRemainingIsNull() {
        val reward = RewardFactory.reward().toBuilder()
                .limit(100)
                .build()
        TestCase.assertFalse(isLimitReached(reward))
    }

    @Test
    fun testIsLimitReachedWhenRemainingIsGreaterThanZero() {
        val reward = RewardFactory.reward().toBuilder()
                .limit(100)
                .remaining(50)
                .build()
        TestCase.assertFalse(isLimitReached(reward))
    }

    @Test
    fun testIsReward() {
        TestCase.assertTrue(isReward(RewardFactory.reward()))
        TestCase.assertFalse(isReward(RewardFactory.noReward()))
    }

    @Test
    fun testIsNoReward() {
        TestCase.assertTrue(isNoReward(RewardFactory.noReward()))
        TestCase.assertFalse(isNoReward(RewardFactory.reward()))
    }

    @Test
    fun testIsShippable() {
        val rewardWithNullShipping = RewardFactory.reward()
                .toBuilder()
                .shippingType(null)
                .build()
        TestCase.assertFalse(isShippable(rewardWithNullShipping))
        val rewardWithNoShipping = RewardFactory.reward()
        TestCase.assertFalse(isShippable(rewardWithNoShipping))
        val rewardWithMultipleLocationShipping = RewardFactory.multipleLocationShipping()
        TestCase.assertTrue(isShippable(rewardWithMultipleLocationShipping))
        val rewardWithSingleLocationShipping = RewardFactory.singleLocationShipping(LocationFactory.nigeria().displayableName())
        TestCase.assertTrue(isShippable(rewardWithSingleLocationShipping))
        val rewardWithWorldWideShipping = RewardFactory.multipleLocationShipping()
        TestCase.assertTrue(isShippable(rewardWithWorldWideShipping))
    }

    @Test
    fun isTimeLimited() {
        TestCase.assertFalse(isTimeLimited(RewardFactory.reward()))
        TestCase.assertTrue(isTimeLimited(RewardFactory.endingSoon()))
    }

    @Test
    fun testIsExpired() {
        TestCase.assertFalse(isExpired(RewardFactory.reward()))
        val rewardEnded2DaysAgo = RewardFactory.reward()
                .toBuilder()
                .endsAt(DateTime.now().minusDays(2))
                .build()
        TestCase.assertTrue(isExpired(rewardEnded2DaysAgo))
        val rewardEndingIn2Days = RewardFactory.reward()
                .toBuilder()
                .endsAt(DateTime.now().plusDays(2))
                .build()
        TestCase.assertFalse(isExpired(rewardEndingIn2Days))
    }

    @Test
    fun testShippingSummary() {
        val rewardWithNullShipping = RewardFactory.reward()
                .toBuilder()
                .shippingType(null)
                .build()
        TestCase.assertNull(shippingSummary(rewardWithNullShipping))
        val rewardWithNoShipping = RewardFactory.reward()
        TestCase.assertNull(shippingSummary(rewardWithNoShipping))
        val rewardWithMultipleLocationShipping = RewardFactory.multipleLocationShipping()
        TestCase.assertEquals(Pair.create<Int, Any?>(R.string.Limited_shipping, null), shippingSummary(rewardWithMultipleLocationShipping))
        val rewardWithSingleLocationShipping = RewardFactory.singleLocationShipping(LocationFactory.nigeria().displayableName())
        TestCase.assertEquals(Pair.create(R.string.location_name_only, "Nigeria"), shippingSummary(rewardWithSingleLocationShipping))
        val rewardWithWorldWideShipping = RewardFactory.rewardWithShipping()
        TestCase.assertEquals(Pair.create<Int, Any?>(R.string.Ships_worldwide, null), shippingSummary(rewardWithWorldWideShipping))
    }

    @Test
    fun minimumRewardAmountByVariant() {
        val noReward = RewardFactory.noReward()
        TestCase.assertEquals(1.0, rewardAmountByVariant(OptimizelyExperiment.Variant.CONTROL, noReward, 1))
        TestCase.assertEquals(10.0, rewardAmountByVariant(OptimizelyExperiment.Variant.VARIANT_2, noReward, 1))
        TestCase.assertEquals(20.0, rewardAmountByVariant(OptimizelyExperiment.Variant.VARIANT_3, noReward, 1))
        TestCase.assertEquals(50.0, rewardAmountByVariant(OptimizelyExperiment.Variant.VARIANT_4, noReward, 1))
        TestCase.assertEquals(10.0, rewardAmountByVariant(OptimizelyExperiment.Variant.CONTROL, noReward, 10))
        TestCase.assertEquals(100.0, rewardAmountByVariant(OptimizelyExperiment.Variant.VARIANT_4, noReward, 100))
    }

    @Test
    fun testTimeInSecondsUntilDeadline() {
        val date = DateTime.now().toDate()
        val currentDate = MutableDateTime(date)
        currentDate.addSeconds(120)
        val reward = RewardFactory.reward().toBuilder().endsAt(currentDate.toDateTime()).build()
        val timeInSecondsUntilDeadline = timeInSecondsUntilDeadline(reward)
        TestCase.assertEquals(timeInSecondsUntilDeadline, 120)
    }
}