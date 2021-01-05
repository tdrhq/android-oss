package com.kickstarter.viewmodels

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Pair
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.R
import com.kickstarter.libs.Environment
import com.kickstarter.libs.MockCurrentUser
import com.kickstarter.libs.MockSharedPreferences
import com.kickstarter.libs.RefTag
import com.kickstarter.libs.models.Country
import com.kickstarter.libs.utils.DateTimeUtils
import com.kickstarter.libs.utils.RefTagUtils
import com.kickstarter.mock.MockCurrentConfig
import com.kickstarter.mock.factories.*
import com.kickstarter.mock.services.MockApiClient
import com.kickstarter.mock.services.MockApolloClient
import com.kickstarter.models.*
import com.kickstarter.services.apiresponses.ShippingRulesEnvelope
import com.kickstarter.services.mutations.CreateBackingData
import com.kickstarter.services.mutations.UpdateBackingData
import com.kickstarter.ui.ArgumentsKey
import com.kickstarter.ui.data.*
import com.stripe.android.StripeIntentResult
import junit.framework.TestCase
import org.joda.time.DateTime
import org.junit.Test
import rx.Observable
import rx.observers.TestSubscriber
import java.math.RoundingMode
import java.net.CookieManager
import java.util.*

class PledgeFragmentViewModelTest : KSRobolectricTestCase() {

    private lateinit var vm: PledgeFragmentViewModel.ViewModel
    private val deadline = DateTime.parse("2020-10-23T18:13:09Z")

    private val addedCard = TestSubscriber<Pair<StoredCard, Project>>()
    private val additionalPledgeAmount = TestSubscriber<String>()
    private val additionalPledgeAmountIsGone = TestSubscriber<Boolean>()
    private val baseUrlForTerms = TestSubscriber<String>()
    private val cardsAndProject = TestSubscriber<Pair<List<StoredCard>, Project>>()
    private val continueButtonIsEnabled = TestSubscriber<Boolean>()
    private val continueButtonIsGone = TestSubscriber<Boolean>()
    private val conversionText = TestSubscriber<String>()
    private val conversionTextViewIsGone = TestSubscriber<Boolean>()
    private val decreasePledgeButtonIsEnabled = TestSubscriber<Boolean>()
    private val estimatedDelivery = TestSubscriber<String>()
    private val estimatedDeliveryInfoIsGone = TestSubscriber<Boolean>()
    private val increasePledgeButtonIsEnabled = TestSubscriber<Boolean>()
    private val paymentContainerIsGone = TestSubscriber<Boolean>()
    private val pledgeAmount = TestSubscriber<String>()
    private val pledgeButtonCTA = TestSubscriber<Int>()
    private val pledgeButtonIsEnabled = TestSubscriber<Boolean>()
    private val pledgeButtonIsGone = TestSubscriber<Boolean>()
    private val pledgeHint = TestSubscriber<String>()
    private val pledgeMaximum = TestSubscriber<String>()
    private val pledgeMaximumIsGone = TestSubscriber<Boolean>()
    private val pledgeMinimum = TestSubscriber<String>()
    private val pledgeProgressIsGone = TestSubscriber<Boolean>()
    private val pledgeSectionIsGone = TestSubscriber<Boolean>()
    private val pledgeSummaryAmount = TestSubscriber<CharSequence>()
    private val pledgeSummaryIsGone = TestSubscriber<Boolean>()
    private val pledgeTextColor = TestSubscriber<Int>()
    private val projectCurrencySymbol = TestSubscriber<String>()
    private val rewardTitle = TestSubscriber<String>()
    private val selectedShippingRule = TestSubscriber<ShippingRule>()
    private val shippingAmount = TestSubscriber<CharSequence>()
    private val shippingRuleAndProject = TestSubscriber<Pair<List<ShippingRule>, Project>>()
    private val shippingRulesSectionIsGone = TestSubscriber<Boolean>()
    private val shippingSummaryAmount = TestSubscriber<CharSequence>()
    private val shippingSummaryIsGone = TestSubscriber<Boolean>()
    private val shippingSummaryLocation = TestSubscriber<String>()
    private val showNewCardFragment = TestSubscriber<Project>()
    private val showPledgeError = TestSubscriber<Void>()
    private val showPledgeSuccess = TestSubscriber<Pair<CheckoutData, PledgeData>>()
    private val showSelectedCard = TestSubscriber<Pair<Int, CardState>>()
    private val showSCAFlow = TestSubscriber<String>()
    private val showUpdatePaymentError = TestSubscriber<Void>()
    private val showUpdatePaymentSuccess = TestSubscriber<Void>()
    private val showUpdatePledgeError = TestSubscriber<Void>()
    private val showUpdatePledgeSuccess = TestSubscriber<Void>()
    private val startChromeTab = TestSubscriber<String>()
    private val startLoginToutActivity = TestSubscriber<Void>()
    private val totalAmount = TestSubscriber<CharSequence>()
    private val totalAndDeadline = TestSubscriber<Pair<String, String>>()
    private val totalAndDeadlineIsVisible = TestSubscriber<Void>()
    private val totalDividerIsGone = TestSubscriber<Boolean>()
    private val headerSectionIsGone = TestSubscriber<Boolean>()
    private val bonusAmount = TestSubscriber<String>()
    private val decreaseBonusButtonIsEnabled = TestSubscriber<Boolean>()
    private val isNoReward = TestSubscriber<Boolean>()
    private val projectTitle = TestSubscriber<String>()
    private val rewardAndAddOns = TestSubscriber<List<Reward>>()
    private val headerSelectedItems = TestSubscriber<List<Pair<Project, Reward>>>()
    private val shippingRuleStaticIsGone = TestSubscriber<Boolean>()
    private val bonusSectionIsGone = TestSubscriber<Boolean>()
    private val bonusSummaryIsGone = TestSubscriber<Boolean>()

    private fun setUpEnvironment(environment: Environment,
                                 reward: Reward = RewardFactory.rewardWithShipping(),
                                 project: Project = ProjectFactory.project(),
                                 pledgeReason: PledgeReason = PledgeReason.PLEDGE,
                                 addOns: java.util.List<Reward>? = null) {
        this.vm = PledgeFragmentViewModel.ViewModel(environment)

        this.vm.outputs.addedCard().subscribe(this.addedCard)
        this.vm.outputs.additionalPledgeAmount().subscribe(this.additionalPledgeAmount)
        this.vm.outputs.additionalPledgeAmountIsGone().subscribe(this.additionalPledgeAmountIsGone)
        this.vm.outputs.baseUrlForTerms().subscribe(this.baseUrlForTerms)
        this.vm.outputs.cardsAndProject().subscribe(this.cardsAndProject)
        this.vm.outputs.continueButtonIsEnabled().subscribe(this.continueButtonIsEnabled)
        this.vm.outputs.continueButtonIsGone().subscribe(this.continueButtonIsGone)
        this.vm.outputs.conversionText().subscribe(this.conversionText)
        this.vm.outputs.conversionTextViewIsGone().subscribe(this.conversionTextViewIsGone)
        this.vm.outputs.decreasePledgeButtonIsEnabled().subscribe(this.decreasePledgeButtonIsEnabled)
        this.vm.outputs.estimatedDelivery().subscribe(this.estimatedDelivery)
        this.vm.outputs.estimatedDeliveryInfoIsGone().subscribe(this.estimatedDeliveryInfoIsGone)
        this.vm.outputs.increasePledgeButtonIsEnabled().subscribe(this.increasePledgeButtonIsEnabled)
        this.vm.outputs.paymentContainerIsGone().subscribe(this.paymentContainerIsGone)
        this.vm.outputs.pledgeAmount().subscribe(this.pledgeAmount)
        this.vm.outputs.pledgeButtonCTA().subscribe(this.pledgeButtonCTA)
        this.vm.outputs.pledgeButtonIsEnabled().subscribe(this.pledgeButtonIsEnabled)
        this.vm.outputs.pledgeButtonIsGone().subscribe(this.pledgeButtonIsGone)
        this.vm.outputs.pledgeHint().subscribe(this.pledgeHint)
        this.vm.outputs.pledgeMaximum().subscribe(this.pledgeMaximum)
        this.vm.outputs.pledgeMaximumIsGone().subscribe(this.pledgeMaximumIsGone)
        this.vm.outputs.pledgeMinimum().subscribe(this.pledgeMinimum)
        this.vm.outputs.pledgeProgressIsGone().subscribe(this.pledgeProgressIsGone)
        this.vm.outputs.pledgeSectionIsGone().subscribe(this.pledgeSectionIsGone)
        this.vm.outputs.pledgeSummaryAmount().map { normalizeCurrency(it) }.subscribe(this.pledgeSummaryAmount)
        this.vm.outputs.pledgeSummaryIsGone().subscribe(this.pledgeSummaryIsGone)
        this.vm.outputs.pledgeTextColor().subscribe(this.pledgeTextColor)
        this.vm.outputs.projectCurrencySymbol().map { it.first.toString().trim() }.subscribe(this.projectCurrencySymbol)
        this.vm.outputs.rewardTitle().subscribe(this.rewardTitle)
        this.vm.outputs.selectedShippingRule().subscribe(this.selectedShippingRule)
        this.vm.outputs.shippingAmount().map { normalizeCurrency(it) }.subscribe(this.shippingAmount)
        this.vm.outputs.shippingRulesAndProject().subscribe(this.shippingRuleAndProject)
        this.vm.outputs.shippingRulesSectionIsGone().subscribe(this.shippingRulesSectionIsGone)
        this.vm.outputs.shippingSummaryAmount().map { normalizeCurrency(it) }.subscribe(this.shippingSummaryAmount)
        this.vm.outputs.shippingSummaryIsGone().subscribe(this.shippingSummaryIsGone)
        this.vm.outputs.shippingSummaryLocation().subscribe(this.shippingSummaryLocation)
        this.vm.outputs.showNewCardFragment().subscribe(this.showNewCardFragment)
        this.vm.outputs.showPledgeError().subscribe(this.showPledgeError)
        this.vm.outputs.showPledgeSuccess().subscribe(this.showPledgeSuccess)
        this.vm.outputs.showSelectedCard().subscribe(this.showSelectedCard)
        this.vm.outputs.showSCAFlow().subscribe(this.showSCAFlow)
        this.vm.outputs.showUpdatePaymentError().subscribe(this.showUpdatePaymentError)
        this.vm.outputs.showUpdatePaymentSuccess().subscribe(this.showUpdatePaymentSuccess)
        this.vm.outputs.showUpdatePledgeError().subscribe(this.showUpdatePledgeError)
        this.vm.outputs.showUpdatePledgeSuccess().subscribe(this.showUpdatePledgeSuccess)
        this.vm.outputs.startChromeTab().subscribe(this.startChromeTab)
        this.vm.outputs.startLoginToutActivity().subscribe(this.startLoginToutActivity)
        this.vm.outputs.totalAmount().map { normalizeCurrency(it) }.subscribe(this.totalAmount)
        this.vm.outputs.totalAndDeadline().subscribe(this.totalAndDeadline)
        this.vm.outputs.totalAndDeadlineIsVisible().subscribe(this.totalAndDeadlineIsVisible)
        this.vm.outputs.totalDividerIsGone().subscribe(this.totalDividerIsGone)
        this.vm.outputs.headerSectionIsGone().subscribe(this.headerSectionIsGone)
        this.vm.outputs.bonusAmount().subscribe(this.bonusAmount)
        this.vm.outputs.decreaseBonusButtonIsEnabled().subscribe(this.decreaseBonusButtonIsEnabled)
        this.vm.outputs.isNoReward().subscribe(this.isNoReward)
        this.vm.outputs.projectTitle().subscribe(this.projectTitle)
        this.vm.outputs.rewardAndAddOns().subscribe(this.rewardAndAddOns)
        this.vm.outputs.headerSelectedItems().subscribe(this.headerSelectedItems)
        this.vm.outputs.shippingRuleStaticIsGone().subscribe(this.shippingRuleStaticIsGone)
        this.vm.outputs.isBonusSupportSectionGone().subscribe(this.bonusSectionIsGone)
        this.vm.outputs.bonusSummaryIsGone().subscribe(this.bonusSummaryIsGone)

        val projectData = project.backing()?.let {
            return@let ProjectData.builder()
                    .project(project)
                    .backing(it)
                    .build()
        } ?: ProjectDataFactory.project(project.toBuilder()
                .deadline(this.deadline)
                .build())

        val bundle = Bundle()
        bundle.putParcelable(ArgumentsKey.PLEDGE_PLEDGE_DATA, PledgeData.with(
                PledgeFlowContext.forPledgeReason(pledgeReason),
                projectData,
                reward,
                addOns,
                ShippingRuleFactory.usShippingRule()))

        bundle.putSerializable(ArgumentsKey.PLEDGE_PLEDGE_REASON, pledgeReason)
        this.vm.arguments(bundle)
    }

    @Test
    fun testBaseUrlForTerms() {
        setUpEnvironment(environment().toBuilder()
                .webEndpoint("www.test.dev")
                .build())

        this.baseUrlForTerms.assertValue("www.test.dev")
    }

    @Test
    fun testCards_whenLoggedIn_userHasCards() {
        val card = StoredCardFactory.discoverCard()
        val mockCurrentUser = MockCurrentUser(UserFactory.user())
        val project = ProjectFactory.project()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(mockCurrentUser)
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(Collections.singletonList(card))
                    }
                }).build()

        setUpEnvironment(environment, project = project)

        this.cardsAndProject.assertValue(Pair(Collections.singletonList(card), project))
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        val visa = StoredCardFactory.visa()
        this.vm.inputs.cardSaved(visa)
        this.vm.inputs.addedCardPosition(0)

        this.cardsAndProject.assertValue(Pair(Collections.singletonList(card), project))
        this.addedCard.assertValue(Pair(visa, project))
        this.showSelectedCard.assertValues(Pair(0, CardState.SELECTED), Pair(0, CardState.SELECTED))
    }

    @Test
    fun testCards_whenLoggedIn_userHasCards_firstCardIsNotAllowed() {
        val allowedCard = StoredCardFactory.visa()
        val storedCards = listOf(StoredCardFactory.discoverCard(), allowedCard, StoredCardFactory.visa())
        val mockCurrentUser = MockCurrentUser(UserFactory.user())
        val project = ProjectFactory.mxProject()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(mockCurrentUser)
                .apolloClient(apolloClientWithStoredCards(storedCards))
                .build()

        setUpEnvironment(environment, project = project)

        this.cardsAndProject.assertValue(Pair(storedCards, project))
        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        val visa = StoredCardFactory.visa()
        this.vm.inputs.cardSaved(visa)
        this.vm.inputs.addedCardPosition(0)

        this.cardsAndProject.assertValue(Pair(storedCards, project))
        this.addedCard.assertValue(Pair(visa, project))
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))
    }

    @Test
    fun testCards_whenLoggedIn_userHasCards_noAllowedCards() {
        val storedCards = listOf(StoredCardFactory.discoverCard())
        val mockCurrentUser = MockCurrentUser(UserFactory.user())
        val project = ProjectFactory.mxProject()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(mockCurrentUser)
                .apolloClient(apolloClientWithStoredCards(storedCards))
                .build()

        setUpEnvironment(environment, project = project)

        this.cardsAndProject.assertValue(Pair(storedCards, project))
        this.showSelectedCard.assertNoValues()

        val visa = StoredCardFactory.visa()
        this.vm.inputs.cardSaved(visa)
        this.vm.inputs.addedCardPosition(0)

        this.cardsAndProject.assertValue(Pair(storedCards, project))
        this.addedCard.assertValue(Pair(visa, project))
        this.showSelectedCard.assertValues(Pair(0, CardState.SELECTED))
    }

    @Test
    fun testCards_whenLoggedIn_userHasNoCards() {
        val mockCurrentUser = MockCurrentUser(UserFactory.user())
        val project = ProjectFactory.project()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(mockCurrentUser)
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(Collections.emptyList())
                    }
                }).build()

        setUpEnvironment(environment, project = project)

        this.cardsAndProject.assertValue(Pair(Collections.emptyList(), project))
        this.showSelectedCard.assertNoValues()

        val visa = StoredCardFactory.visa()
        this.vm.inputs.cardSaved(visa)
        this.vm.inputs.addedCardPosition(0)

        this.cardsAndProject.assertValue(Pair(Collections.emptyList(), project))
        this.addedCard.assertValue(Pair(visa, project))
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
    }

    @Test
    fun testCards_whenLoggedIn_userBackedProject() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val storedCards = testData.storedCards
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .apolloClient(apolloClientWithStoredCards(storedCards))
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()

        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.cardsAndProject.assertValue(Pair(storedCards, backedProject))
        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        val visa = StoredCardFactory.visa()
        this.vm.inputs.cardSaved(visa)
        this.vm.inputs.addedCardPosition(0)

        this.cardsAndProject.assertValue(Pair(storedCards, backedProject))
        this.addedCard.assertValue(Pair(visa, backedProject))
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))
    }

    @Test
    fun testCards_whenLoggedOut() {
        setUpEnvironment(environment())

        this.cardsAndProject.assertNoValues()
        this.showSelectedCard.assertNoValues()
    }

    @Test
    fun testPaymentLoggingInUser_whenPhysicalReward() {
        val mockCurrentUser = MockCurrentUser()
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(mockCurrentUser)
                .build()
        setUpEnvironment(environment)

        this.cardsAndProject.assertNoValues()
        this.continueButtonIsGone.assertValue(false)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonIsGone.assertValue(true)

        mockCurrentUser.refresh(UserFactory.user())

        this.cardsAndProject.assertValueCount(1)
        this.continueButtonIsGone.assertValues(false, true)
        this.paymentContainerIsGone.assertValues(true, false)
        this.pledgeButtonIsGone.assertValues(true, false)
    }

    @Test
    fun testPaymentLoggingInUser_whenDigitalReward() {
        val mockCurrentUser = MockCurrentUser()
        setUpEnvironment(environment().toBuilder().currentUser(mockCurrentUser).build(), RewardFactory.reward())

        this.cardsAndProject.assertNoValues()
        this.continueButtonIsGone.assertValue(false)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonIsGone.assertValue(true)

        mockCurrentUser.refresh(UserFactory.user())

        this.cardsAndProject.assertValueCount(1)
        this.continueButtonIsGone.assertValues(false, true)
        this.paymentContainerIsGone.assertValues(true, false)
        this.pledgeButtonIsGone.assertValues(true, false)
    }

    @Test
    fun testPledgeAmount_whenUpdatingPledge() {
        val reward = RewardFactory.rewardWithShipping()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(40.0)
                .shippingAmount(10f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.pledgeAmount.assertValue("30")
    }

    @Test
    fun testPledgeScreenConfiguration_whenPledgingShippableRewardAndNotLoggedIn() {
        setUpEnvironment(environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules()))

        this.continueButtonIsEnabled.assertValue(true)
        this.continueButtonIsGone.assertValue(false)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Pledge)
        this.pledgeButtonIsEnabled.assertNoValues()
        this.pledgeButtonIsGone.assertValue(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(false)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)
        this.koalaTest.assertValue("Pledge Screen Viewed")
        this.lakeTest.assertValue("Checkout Payment Page Viewed")
        this.experimentsTest.assertValue("Checkout Payment Page Viewed")
    }

    @Test
    fun testPledgeScreenConfiguration_whenPledgingDigitalRewardAndNotLoggedIn() {
        setUpEnvironment(environment(), reward = RewardFactory.noReward())

        this.continueButtonIsEnabled.assertValue(true)
        this.continueButtonIsGone.assertValue(false)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Pledge)
        this.pledgeButtonIsEnabled.assertNoValues()
        this.pledgeButtonIsGone.assertValue(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertValue("Pledge Screen Viewed")
        this.lakeTest.assertValue("Checkout Payment Page Viewed")
        this.experimentsTest.assertValue("Checkout Payment Page Viewed")
    }

    @Test
    fun testPledgeScreenConfiguration_whenPledgingShippableRewardAndLoggedIn() {
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        setUpEnvironment(environment)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonCTA.assertValue(R.string.Pledge)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(false)
        this.shippingRulesSectionIsGone.assertValue(false)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertValue("Pledge Screen Viewed")
        this.lakeTest.assertValue("Checkout Payment Page Viewed")
        this.experimentsTest.assertValue("Checkout Payment Page Viewed")
    }

    @Test
    fun testPledgeScreenConfiguration_whenPledgingDigitalRewardAndLoggedIn() {
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), RewardFactory.noReward())

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonCTA.assertValue(R.string.Pledge)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(true)
        this.headerSectionIsGone.assertValue(true)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertValue("Pledge Screen Viewed")
        this.lakeTest.assertValue("Checkout Payment Page Viewed")
        this.experimentsTest.assertValue("Checkout Payment Page Viewed")
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingPledgeOfShippableReward() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()

        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsEnabled.assertValue(false)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(true)
        this.pledgeSummaryIsGone.assertValue(false)
        this.bonusSectionIsGone.assertValue(false)
        this.shippingRulesSectionIsGone.assertValues(false)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingPledgeOfDigitalReward() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward

        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), noReward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsEnabled.assertValue(false)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(false)
        this.headerSectionIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingPaymentOfShippableReward() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()

        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertNoValues()
        this.pledgeSummaryIsGone.assertValue(false)
        this.shippingRulesSectionIsGone.assertValues(true, true)
        this.totalDividerIsGone.assertValue(true)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingPaymentOfDigitalReward() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward

        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertNoValues()
        this.pledgeSummaryIsGone.assertValue(true)
        this.headerSectionIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.selectedShippingRule.assertNoValues()
        this.shippingSummaryIsGone.assertValues(true)
        this.totalDividerIsGone.assertValue(true)
        this.pledgeButtonIsEnabled.assertValue(true)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenFixingPaymentOfShippableReward() {
        val shippableReward = RewardFactory.rewardWithShipping()
        val unitedStates = LocationFactory.unitedStates()
        val shippingRule = ShippingRuleFactory.usShippingRule().toBuilder().location(unitedStates).build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(50.0)
                .location(unitedStates)
                .locationId(unitedStates.id())
                .reward(shippableReward)
                .rewardId(shippableReward.id())
                .shippingAmount(shippingRule.cost().toFloat())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val shippingRulesEnvelope = ShippingRulesEnvelopeFactory.shippingRules()
                .toBuilder()
                .shippingRules(listOf(ShippingRuleFactory.germanyShippingRule(), shippingRule))
                .build()

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()

        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeSectionIsGone.assertNoValues()
        this.bonusSectionIsGone.assertNoValues()
        this.pledgeSummaryIsGone.assertValue(false)
        this.shippingRulesSectionIsGone.assertValues(true, true)
        this.shippingSummaryIsGone.assertValue(false)
        this.totalDividerIsGone.assertValue(true)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenFixingPaymentOfDigitalReward() {
        val noReward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .reward(noReward)
                .rewardId(noReward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), noReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(false)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeSectionIsGone.assertNoValues()
        this.bonusSectionIsGone.assertNoValues()
        this.pledgeSummaryIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.selectedShippingRule.assertNoValues()
        this.shippingSummaryIsGone.assertValues(true)
        this.bonusSummaryIsGone.assertValues(true, true)
        this.totalDividerIsGone.assertValue(true)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingRewardToShippableReward() {
        val shippableReward = RewardFactory.rewardWithShipping()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        setUpEnvironment(environment, shippableReward, ProjectFactory.backedProject(), PledgeReason.UPDATE_REWARD)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(true)
        this.bonusSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(false)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeScreenConfiguration_whenUpdatingRewardToDigitalReward() {
        val noReward = RewardFactory.noReward()

        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), noReward, ProjectFactory.backedProject(), PledgeReason.UPDATE_REWARD)

        this.continueButtonIsEnabled.assertNoValues()
        this.continueButtonIsGone.assertValue(true)
        this.paymentContainerIsGone.assertValue(true)
        this.pledgeButtonCTA.assertValue(R.string.Confirm)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeButtonIsGone.assertValue(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeProgressIsGone.assertNoValues()
        this.pledgeSectionIsGone.assertValue(false)
        this.pledgeSummaryIsGone.assertValue(true)
        this.headerSectionIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.shippingSummaryIsGone.assertNoValues()
        this.totalDividerIsGone.assertValue(false)

        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
        this.experimentsTest.assertNoValues()
    }

    @Test
    fun testPledgeSummaryAmount_whenUpatingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward

        val environment = environment()
        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 1.0))
    }

    @Test
    fun testPledgeSummaryAmount_whenFixingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward

        val environment = environment()
        setUpEnvironment(environment, noReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 1.0))
    }

    @Test
    fun testTotalAmount_whenUpdatingPledge() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()
        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 50.0))
    }

    @Test
    fun testTotalAmount_whenUpdatingPayment() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()
        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 50.0))
    }

    @Test
    fun testTotalAmount_whenFixingPaymentMethod() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()
        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 50.0))
    }

    @Test
    fun testTotalAmount_whenUpdatingReward() {
        val reward = RewardFactory.rewardWithShipping()
        val backedProject = ProjectFactory.backedProject()

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_REWARD)

        this.totalAmount.assertValue(expectedCurrency(environment, backedProject, 50.0))
    }

    @Test
    fun testEstimatedDelivery_whenPhysicalReward() {
        setUpEnvironment(environment())
        this.estimatedDelivery.assertValue(DateTimeUtils.estimatedDeliveryOn(RewardFactory.ESTIMATED_DELIVERY))
        this.estimatedDeliveryInfoIsGone.assertValue(false)
    }

    @Test
    fun testEstimatedDelivery_whenDigitalReward() {
        setUpEnvironment(environment(), reward = RewardFactory.reward())

        this.estimatedDelivery.assertValue(DateTimeUtils.estimatedDeliveryOn(RewardFactory.ESTIMATED_DELIVERY))
        this.estimatedDeliveryInfoIsGone.assertValue(false)
    }

    @Test
    fun testEstimatedDelivery_whenNoReward() {
        setUpEnvironment(environment(), RewardFactory.noReward())

        this.estimatedDelivery.assertNoValues()
        this.estimatedDeliveryInfoIsGone.assertValue(true)
    }



    @Test
    fun testShowMaxPledge_USProject_USDPref() {
        val environment = environmentForLoggedInUser(UserFactory.user())
        val project = ProjectFactory.project()
        val rw = RewardFactory.reward()
        setUpEnvironment(environment,rw, project)

        this.vm.inputs.bonusInput("999999")
        
        this.pledgeMaximumIsGone.assertValues(true, false)
        this.pledgeMaximum.assertValues("$9,980") // 10.000 - 20 : MAXUSD - REWARD.minimum
    }

    @Test
    fun testUpdatingPledgeAmount_WithShippingChange_USProject_USDPref() {
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        val project = ProjectFactory.project()
        setUpEnvironment(environment, project = project)

        val defaultRule = ShippingRuleFactory.usShippingRule()
        this.selectedShippingRule.assertValues(defaultRule)

        assertInitialPledgeState_WithShipping()
        assertInitialPledgeCurrencyStates_WithShipping_USProject(environment, project)

        val selectedRule = ShippingRuleFactory.germanyShippingRule()
        this.vm.inputs.shippingRuleSelected(selectedRule)

        this.additionalPledgeAmountIsGone.assertValues(true)
        this.additionalPledgeAmount.assertValues(expectedCurrency(environment, project, 0.0))
        this.continueButtonIsEnabled.assertNoValues()
        this.conversionText.assertNoValues()
        this.conversionTextViewIsGone.assertValues(true)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeHint.assertValue("20")
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeMinimum.assertValue(expectedCurrency(environment, project, 20.0))
        this.pledgeTextColor.assertValues(R.color.ksr_green_500)
        this.projectCurrencySymbol.assertValue("$")
        this.selectedShippingRule.assertValues(defaultRule, selectedRule)
        this.shippingAmount.assertValues(expectedCurrency(environment, project, 30.0),
                expectedCurrency(environment, project, 40.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0),
                expectedCurrency(environment, project, 60.0))
        this.totalAndDeadlineIsVisible.assertValueCount(2)
    }

    @Test
    fun testUpdatingPledgeAmount_WithStepper_MXProject_USDPref() {
        val project = ProjectFactory.mxProject().toBuilder().currentCurrency("USD").build()
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .apolloClient(apolloClientWithStoredCards(listOf(StoredCardFactory.visa())))
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        setUpEnvironment(environment, project = project)

        assertInitialPledgeState_WithShipping()
        assertInitialPledgeCurrencyStates_WithShipping_MXProject(environment, project)

        this.continueButtonIsEnabled.assertNoValues()
        this.conversionText.assertValues(expectedConvertedCurrency(environment, project, 50.0))
        this.conversionTextViewIsGone.assertValues(false)

        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeMinimum.assertValue(expectedCurrency(environment, project, 20.0))
        this.pledgeTextColor.assertValue(R.color.ksr_green_500)
        this.projectCurrencySymbol.assertValue("MX$")
        this.shippingAmount.assertValue(expectedCurrency(environment, project, 30.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0))
        this.totalAndDeadline.assertValues(Pair(expectedCurrency(environment, project, 50.0), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(1)

        this.continueButtonIsEnabled.assertNoValues()
        this.conversionText.assertValues(expectedConvertedCurrency(environment, project, 50.0))
        this.conversionTextViewIsGone.assertValues(false)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeHint.assertValue("20")
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeMinimum.assertValue(expectedCurrency(environment, project, 20.0))
        this.pledgeTextColor.assertValue(R.color.ksr_green_500)
        this.projectCurrencySymbol.assertValue("MX$")
        this.shippingAmount.assertValue(expectedCurrency(environment, project, 30.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0))
        this.totalAndDeadline.assertValues(Pair(expectedCurrency(environment, project, 50.0), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(1)
    }



    @Test
    fun testUpdatingPledgeAmount_WithShippingChange_MXProject_USDPref() {
        val project = ProjectFactory.mxProject().toBuilder().currentCurrency("USD").build()
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .apolloClient(apolloClientWithStoredCards(listOf(StoredCardFactory.visa())))
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        setUpEnvironment(environment, project = project)

        assertInitialPledgeState_WithShipping()
        assertInitialPledgeCurrencyStates_WithShipping_MXProject(environment, project)
        val initialRule = ShippingRuleFactory.usShippingRule()
        this.selectedShippingRule.assertValues(initialRule)

        val selectedRule = ShippingRuleFactory.germanyShippingRule()
        this.vm.inputs.shippingRuleSelected(selectedRule)

        this.additionalPledgeAmount.assertValue(expectedCurrency(environment, project, 0.0))
        this.additionalPledgeAmountIsGone.assertValues(true)
        this.continueButtonIsEnabled.assertNoValues()
        this.conversionText.assertValues(expectedConvertedCurrency(environment, project, 50.0),
                expectedConvertedCurrency(environment, project, 60.0))
        this.conversionTextViewIsGone.assertValues(false)

        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeHint.assertValue("20")
        this.pledgeMaximumIsGone.assertValue(true)
        this.pledgeMinimum.assertValue(expectedCurrency(environment, project, 20.0))
        this.pledgeTextColor.assertValue(R.color.ksr_green_500)
        this.projectCurrencySymbol.assertValue("MX$")
        this.selectedShippingRule.assertValues(initialRule, selectedRule)
        this.shippingAmount.assertValues(expectedCurrency(environment, project, 30.0),
                expectedCurrency(environment, project, 40.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0),
                expectedCurrency(environment, project, 60.00))
        this.totalAndDeadline.assertValues(Pair(expectedCurrency(environment, project, 50.0), DateTimeUtils.longDate(this.deadline)),
                Pair(expectedCurrency(environment, project, 60.00), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(2)
    }

    @Test
    fun testPledgeStepping_maxReward_USProject() {
        val environment = environment()
        val project = ProjectFactory.project()
        setUpEnvironment(environment, RewardFactory.maxReward(Country.US), project)

        this.additionalPledgeAmountIsGone.assertValuesAndClear(true)
        this.additionalPledgeAmount.assertValuesAndClear(expectedCurrency(environment, project, 0.0))
        this.decreaseBonusButtonIsEnabled.assertValuesAndClear(false)
        this.increasePledgeButtonIsEnabled.assertValuesAndClear(false)
    }

    @Test
    fun testPledgeStepping_maxReward_MXProject() {
        val environment = environment()
        val mxProject = ProjectFactory.mxProject()
        setUpEnvironment(environment, RewardFactory.maxReward(Country.MX), mxProject)

        this.additionalPledgeAmountIsGone.assertValue(true)
        this.additionalPledgeAmount.assertValue(expectedCurrency(environment, mxProject, 0.0))
        this.decreaseBonusButtonIsEnabled.assertValue(false)
        this.increasePledgeButtonIsEnabled.assertValue(false)
    }

    @Test
    fun testRefTagIsSent() {
        val project = ProjectFactory.project()
        val sharedPreferences: SharedPreferences = MockSharedPreferences()
        val cookieManager = CookieManager()

        val environment = environment()
                .toBuilder()
                .cookieManager(cookieManager)
                .sharedPreferences(sharedPreferences)
                .apolloClient(object : MockApolloClient() {
                    override fun createBacking(createBackingData: CreateBackingData): Observable<Checkout> {
                        //Assert that stored cookie is passed in
                        TestCase.assertEquals(createBackingData.refTag, RefTag.discovery())
                        return super.createBacking(createBackingData)
                    }
                })
                .build()

        //Store discovery ref tag for project
        RefTagUtils.storeCookie(RefTag.discovery(), project, cookieManager, sharedPreferences)

        setUpEnvironment(environment, RewardFactory.noReward(), project)

        this.vm.inputs.cardSelected(StoredCardFactory.visa(), 0)
        this.pledgeButtonIsEnabled.assertValue(true)
        this.vm.inputs.pledgeButtonClicked()

        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testRewardTitle_forRewardWithTitle() {
        val reward = RewardFactory.reward()
                .toBuilder()
                .title("Coolest reward")
                .build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(20.0)
                .shippingAmount(0f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject, PledgeReason.PLEDGE)

        this.rewardTitle.assertValue("Coolest reward")
    }

    @Test
    fun testRewardTitle_forRewardWithNullTitle() {
        val reward = RewardFactory.reward()
                .toBuilder()
                .title(null)
                .build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(20.0)
                .shippingAmount(0f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .name("Restart Your Computer")
                .build()

        setUpEnvironment(environment(), reward, backedProject, PledgeReason.PLEDGE)

        this.rewardTitle.assertValue("Restart Your Computer")
    }

    @Test
    fun testRewardTitle_forNoReward() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(20.0)
                .shippingAmount(0f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .name("Restart Your Computer")
                .build()

        setUpEnvironment(environment(), reward, backedProject, PledgeReason.PLEDGE)

        this.rewardTitle.assertValue("Restart Your Computer")
    }

    @Test
    fun testShippingSummaryAmount_whenFixingPaymentMethod() {
        val reward = RewardFactory.rewardWithShipping()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .shippingAmount(10f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environment()
        setUpEnvironment(environment, reward, backedProject, PledgeReason.FIX_PLEDGE)

        this.shippingSummaryAmount.assertValue(expectedCurrency(environment, backedProject, 10.0))
    }

    @Test
    fun testShippingSummaryAmount_whenUpdatingPaymentMethod() {
        val reward = RewardFactory.rewardWithShipping()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .shippingAmount(10f)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environment()
        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.shippingSummaryAmount.assertValue(expectedCurrency(environment, backedProject, 10.0))
    }

    @Test
    fun testShippingSummaryLocation_whenUpdatingPaymentMethod() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()
        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.shippingSummaryLocation.assertValue("Brooklyn, NY")
    }

    @Test
    fun testShippingSummaryLocation_whenFixingPaymentMethod() {
        val testData = setUpBackedShippableRewardTestData()
        val backedProject = testData.project
        val shippableReward = testData.reward
        val shippingRulesEnvelope = testData.shippingRulesEnvelope as ShippingRulesEnvelope

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()
        setUpEnvironment(environment, shippableReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.shippingSummaryLocation.assertValue("Brooklyn, NY")
    }

    @Test
    fun testShippingRulesAndProject_whenPhysicalReward() {
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
        val project = ProjectFactory.project()
        setUpEnvironment(environment, project = project)

        val shippingRules = ShippingRulesEnvelopeFactory.shippingRules().shippingRules()
        this.shippingRuleAndProject.assertValues(Pair.create(shippingRules, project))
    }

    @Test
    fun testShippingRulesAndProject_whenNoReward() {
        setUpEnvironment(environment(), RewardFactory.noReward())

        this.shippingRuleAndProject.assertNoValues()
    }

    @Test
    fun testShippingRulesAndProject_whenDigitalReward() {
        setUpEnvironment(environment(), RewardFactory.reward())

        this.shippingRuleAndProject.assertNoValues()
    }

    @Test
    fun testShippingRulesAndProject_error() {
        val environment = environment().toBuilder()
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.error(Throwable("error"))
                    }
                })
                .build()
        val project = ProjectFactory.project()
        setUpEnvironment(environment, project = project)

        this.shippingRuleAndProject.assertNoValues()
        this.totalAmount.assertNoValues()
    }

    @Test
    fun testShowNewCardFragment() {
        val project = ProjectFactory.project()
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), RewardFactory.noReward(), project)

        this.vm.inputs.newCardButtonClicked()
        this.showNewCardFragment.assertValue(project)
        this.koalaTest.assertValues("Pledge Screen Viewed", "Add New Card Button Clicked")
        this.lakeTest.assertValue("Checkout Payment Page Viewed")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowNewCardFragment_whenFixingPaymentMethod() {
        val backedProject = ProjectFactory.backedProject()
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), RewardFactory.noReward(), backedProject, PledgeReason.FIX_PLEDGE)

        this.vm.inputs.newCardButtonClicked()
        this.showNewCardFragment.assertValue(backedProject)
        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
    }

    @Test
    fun testShowNewCardFragment_whenUpdatingPaymentMethod() {
        val backedProject = ProjectFactory.backedProject()
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), RewardFactory.noReward(), backedProject, PledgeReason.UPDATE_PAYMENT)

        this.vm.inputs.newCardButtonClicked()
        this.showNewCardFragment.assertValue(backedProject)
        this.koalaTest.assertNoValues()
        this.lakeTest.assertNoValues()
    }

    @Test
    fun testShowUpdatePaymentError_whenUpdatingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(storedCards)
                    }

                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                }).build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)

        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showUpdatePaymentError.assertValueCount(1)
        this.koalaTest.assertValues("Update Payment Method Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentError_whenFixingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(storedCards)
                    }

                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                }).build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)

        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showUpdatePaymentError.assertValueCount(1)
        this.koalaTest.assertNoValues()
        this.lakeTest.assertValues("Pledge Submit Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentSuccess_whenUpdatingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(apolloClientWithStoredCards(storedCards))
                .build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)

        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showUpdatePaymentSuccess.assertValueCount(1)
        this.koalaTest.assertValues("Update Payment Method Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentSuccess_whenFixingPaymentMethod() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(apolloClientWithStoredCards(storedCards))
                .build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.FIX_PLEDGE)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)

        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showUpdatePaymentSuccess.assertValueCount(1)
        this.koalaTest.assertNoValues()
        this.lakeTest.assertValues("Pledge Submit Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentSuccess_whenRequiresAction_isSuccessful_successOutcome() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(storedCards)
                    }

                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                }).build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertNoValues()
        this.showUpdatePaymentSuccess.assertNoValues()

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.SUCCEEDED)

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertNoValues()
        this.showUpdatePaymentSuccess.assertValueCount(1)
        this.koalaTest.assertValues("Update Payment Method Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentSuccess_whenRequiresAction_isSuccessful_unsuccessfulOutcome() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(storedCards)
                    }

                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                }).build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertNoValues()
        this.showUpdatePaymentSuccess.assertNoValues()

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.FAILED)

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertValueCount(1)
        this.showUpdatePaymentSuccess.assertNoValues()
        this.koalaTest.assertValues("Update Payment Method Button Clicked")
    }

    @Test
    fun testShowUpdatePaymentSuccess_whenRequiresAction_isUnsuccessful() {
        val testData = setUpBackedNoRewardTestData()
        val backedProject = testData.project
        val noReward = testData.reward
        val storedCards = testData.storedCards

        val environment = environment()
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apolloClient(object : MockApolloClient() {
                    override fun getStoredCards(): Observable<List<StoredCard>> {
                        return Observable.just(storedCards)
                    }

                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                }).build()

        setUpEnvironment(environment, noReward, backedProject, PledgeReason.UPDATE_PAYMENT)

        this.showSelectedCard.assertValue(Pair(1, CardState.SELECTED))

        this.vm.inputs.cardSelected(storedCards[0], 0)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertNoValues()
        this.showUpdatePaymentSuccess.assertNoValues()

        this.vm.inputs.stripeSetupResultUnsuccessful(Exception("eek"))

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSelectedCard.assertValues(Pair(1, CardState.SELECTED), Pair(0, CardState.SELECTED))
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePaymentError.assertValueCount(1)
        this.showUpdatePaymentSuccess.assertNoValues()
        this.koalaTest.assertValues("Update Payment Method Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeError_whenUpdatingPledgeWithShipping() {
        val reward = RewardFactory.rewardWithShipping()
        val unitedStates = LocationFactory.unitedStates()
        val backingShippingRule = ShippingRuleFactory.usShippingRule().toBuilder().location(unitedStates).build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(40.0)
                .location(unitedStates)
                .locationId(unitedStates.id())
                .reward(reward)
                .rewardId(reward.id())
                .shippingAmount(backingShippingRule.cost().toFloat())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val config = ConfigFactory.configForUSUser()
        val currentConfig = MockCurrentConfig()
        currentConfig.config(config)

        val germanyShippingRule = ShippingRuleFactory.germanyShippingRule()
        val shippingRulesEnvelope = ShippingRulesEnvelopeFactory.shippingRules()
                .toBuilder()
                .shippingRules(listOf(germanyShippingRule, backingShippingRule))
                .build()

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                })
                .build()

        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.shippingRuleSelected(germanyShippingRule)
        this.vm.inputs.pledgeButtonClicked()

        this.showUpdatePledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeError_whenUpdatingPledgeWithNoShipping() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environment().toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                })
                .build()
        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.pledgeInput("31")
        this.vm.inputs.increasePledgeButtonClicked()
        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(false, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showUpdatePledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeError_whenUpdatingRewardWithShipping() {
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                })
                .build()
        setUpEnvironment(environment, project = ProjectFactory.backedProject(), pledgeReason = PledgeReason.UPDATE_REWARD)

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showUpdatePledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeError_whenUpdatingRewardWithNoShipping() {
        val environment = environment()
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.error(Exception("womp"))
                    }
                })
                .build()
        setUpEnvironment(environment, RewardFactory.noReward(), ProjectFactory.backedProject(), PledgeReason.UPDATE_REWARD)

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showUpdatePledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenUpdatingPledgeWithShipping() {
        val reward = RewardFactory.rewardWithShipping()
        val unitedStates = LocationFactory.unitedStates()
        val backingShippingRule = ShippingRuleFactory.usShippingRule().toBuilder().location(unitedStates).build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(40.0)
                .location(unitedStates)
                .locationId(unitedStates.id())
                .reward(reward)
                .rewardId(reward.id())
                .shippingAmount(backingShippingRule.cost().toFloat())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val config = ConfigFactory.configForUSUser()
        val currentConfig = MockCurrentConfig()
        currentConfig.config(config)

        val germanyShippingRule = ShippingRuleFactory.germanyShippingRule()
        val shippingRulesEnvelope = ShippingRulesEnvelopeFactory.shippingRules()
                .toBuilder()
                .shippingRules(listOf(germanyShippingRule, backingShippingRule))
                .build()

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()

        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.shippingRuleSelected(germanyShippingRule)
        this.vm.inputs.pledgeButtonClicked()

        this.pledgeProgressIsGone.assertValues(false)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenUpdatingPledgeWithNoShipping() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.pledgeInput("32.0")
        this.vm.inputs.increasePledgeButtonClicked()
        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(false, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenUpdatingRewardWithShipping() {
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
        setUpEnvironment(environment, project = ProjectFactory.backedProject(), pledgeReason = PledgeReason.UPDATE_REWARD)

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showUpdatePledgeSuccess.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenUpdatingRewardWithNoShipping() {
        setUpEnvironment(environment(), RewardFactory.noReward(), ProjectFactory.backedProject(), PledgeReason.UPDATE_REWARD)

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showUpdatePledgeSuccess.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    /* FIX TODO in https://kickstarter.atlassian.net/browse/NT-1462
    @Test
    fun testShowUpdatePledgeSuccess_whenRequiresAction_isSuccessful_successOutcome() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.bonusInput("31")
        this.vm.inputs.increaseBonusButtonClicked()
        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(false)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertNoValues()
        this.showUpdatePledgeSuccess.assertNoValues()

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.SUCCEEDED)

        this.pledgeButtonIsEnabled.assertValues(false, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertNoValues()
        this.showUpdatePledgeSuccess.assertValueCount(1)
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenRequiresAction_isSuccessful_unsuccessfulOutcome() {
        val testData = setUpBackedShippableRewardTestData()

        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, testData.reward, testData.project, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.bonusInput("31")
        this.vm.inputs.increaseBonusButtonClicked()

        this.pledgeProgressIsGone.assertNoValues()
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertNoValues()
        this.showUpdatePledgeSuccess.assertNoValues()
        this.pledgeButtonIsEnabled.assertValues(false)

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.FAILED)

        this.pledgeButtonIsEnabled.assertValues(false, true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertValueCount(1)
        this.showUpdatePledgeSuccess.assertNoValues()
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }

    @Test
    fun testShowUpdatePledgeSuccess_whenRequiresAction_isUnsuccessful() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun updateBacking(updateBackingData: UpdateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.bonusInput("31")
        this.vm.inputs.increaseBonusButtonClicked()
        this.pledgeButtonIsEnabled.assertValues(false)
        this.pledgeProgressIsGone.assertNoValues()
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertNoValues()
        this.showUpdatePledgeSuccess.assertNoValues()

        this.vm.inputs.stripeSetupResultUnsuccessful(Exception("woops"))

        this.pledgeButtonIsEnabled.assertValues(false, true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSCAFlow.assertValueCount(1)
        this.showUpdatePledgeError.assertValueCount(1)
        this.showUpdatePledgeSuccess.assertNoValues()
        this.koalaTest.assertValues("Update Pledge Button Clicked")
    }*/

    @Test
    fun testStartChromeTab() {
        setUpEnvironment(environment().toBuilder()
                .webEndpoint("www.test.dev")
                .build())

        this.vm.inputs.linkClicked("www.test.dev/trust")
        this.startChromeTab.assertValuesAndClear("www.test.dev/trust")

        this.vm.inputs.linkClicked("www.test.dev/cookies")
        this.startChromeTab.assertValuesAndClear("www.test.dev/cookies")

        this.vm.inputs.linkClicked("www.test.dev/privacy")
        this.startChromeTab.assertValuesAndClear("www.test.dev/privacy")

        this.vm.inputs.linkClicked("www.test.dev/terms")
        this.startChromeTab.assertValuesAndClear("www.test.dev/terms")
    }

    @Test
    fun testStartLoginToutActivity() {
        setUpEnvironment(environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules()))
        this.continueButtonIsEnabled.assertValue(true)

        this.vm.inputs.pledgeInput("1")
        this.vm.inputs.increasePledgeButtonClicked()
        this.continueButtonIsEnabled.assertValues(true)

        this.vm.inputs.pledgeInput("20")
        this.vm.inputs.increasePledgeButtonClicked()
        this.continueButtonIsEnabled.assertValues(true)

        this.vm.inputs.continueButtonClicked()

        this.startLoginToutActivity.assertValueCount(1)
    }

    @Test
    fun testShowPledgeSuccess_whenNoReward() {
        val project = ProjectFactory.project()
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), RewardFactory.noReward(), project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        //Successfully pledging with a valid amount should show the thanks page
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertValueCount(1)
        this.showPledgeError.assertNoValues()
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowPledgeSuccess_whenDigitalReward() {
        val project = ProjectFactory.project()
        val reward = RewardFactory.reward()
        setUpEnvironment(environmentForLoggedInUser(UserFactory.user()), reward, project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        //Successfully pledging with a valid amount should show the thanks page
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertValueCount(1)
        this.showPledgeError.assertNoValues()
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowPledgeSuccess_whenPhysicalReward() {
        val project = ProjectFactory.project()
        val reward = RewardFactory.rewardWithShipping()
        val environment = environmentForShippingRules(ShippingRulesEnvelopeFactory.shippingRules())
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .build()
        setUpEnvironment(environment, reward, project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        //Successfully pledging with a valid amount should show the thanks page
        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertValueCount(1)
        this.showPledgeError.assertNoValues()
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowPledgeSuccess_error() {
        val project = ProjectFactory.project()
        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun createBacking(createBackingData: CreateBackingData): Observable<Checkout> {
                        return Observable.error(Throwable("error"))
                    }
                })
                .build()
        setUpEnvironment(environment, RewardFactory.noReward(), project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertValueCount(1)
        this.showSCAFlow.assertNoValues()
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowPledgeSuccess_whenRequiresAction_isSuccessful_successOutcome() {
        val project = ProjectFactory.project()
        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun createBacking(createBackingData: CreateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, RewardFactory.noReward(), project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertNoValues()
        this.showSCAFlow.assertValueCount(1)
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.SUCCEEDED)

        this.showPledgeSuccess.assertValueCount(1)
        this.showPledgeError.assertNoValues()
    }

    @Test
    fun testShowPledgeSuccess_whenRequiresAction_isSuccessful_unsuccessfulOutcome() {
        val project = ProjectFactory.project()
        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun createBacking(createBackingData: CreateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, RewardFactory.noReward(), project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.pledgeButtonIsEnabled.assertValues(true)

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValue(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertNoValues()
        this.showSCAFlow.assertValueCount(1)

        this.vm.inputs.stripeSetupResultSuccessful(StripeIntentResult.Outcome.FAILED)

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testShowPledgeSuccess_whenRequiresAction_isUnsuccessful() {
        val project = ProjectFactory.project()
        val environment = environmentForLoggedInUser(UserFactory.user())
                .toBuilder()
                .apolloClient(object : MockApolloClient() {
                    override fun createBacking(createBackingData: CreateBackingData): Observable<Checkout> {
                        return Observable.just(CheckoutFactory.requiresAction(true))
                    }
                })
                .build()
        setUpEnvironment(environment, RewardFactory.noReward(), project)

        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))

        this.vm.inputs.pledgeButtonClicked()

        this.pledgeButtonIsEnabled.assertValues(true, false)
        this.pledgeProgressIsGone.assertValues(false)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertNoValues()
        this.showSCAFlow.assertValueCount(1)


        this.vm.inputs.stripeSetupResultUnsuccessful(Exception("yikes"))

        this.pledgeButtonIsEnabled.assertValues(true, false, true)
        this.pledgeProgressIsGone.assertValues(false, true)
        this.showSelectedCard.assertValue(Pair(0, CardState.SELECTED))
        this.showPledgeSuccess.assertNoValues()
        this.showPledgeError.assertValueCount(1)
        this.koalaTest.assertValues("Pledge Screen Viewed", "Pledge Button Clicked")
        this.lakeTest.assertValues("Checkout Payment Page Viewed", "Pledge Submit Button Clicked")
        this.experimentsTest.assertValues("Checkout Payment Page Viewed")
    }

    @Test
    fun testUpdatePledgeButtonIsEnabled_UpdatingPledge_whenShippingChanged() {
        val reward = RewardFactory.rewardWithShipping()
        val unitedStates = LocationFactory.unitedStates()
        val backingShippingRule = ShippingRuleFactory.usShippingRule().toBuilder().location(unitedStates).build()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(50.0)
                .location(unitedStates)
                .locationId(unitedStates.id())
                .reward(reward)
                .rewardId(reward.id())
                .shippingAmount(backingShippingRule.cost().toFloat())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val config = ConfigFactory.configForUSUser()
        val currentConfig = MockCurrentConfig()
        currentConfig.config(config)

        val germanyShippingRule = ShippingRuleFactory.germanyShippingRule()
        val shippingRulesEnvelope = ShippingRulesEnvelopeFactory.shippingRules()
                .toBuilder()
                .shippingRules(listOf(germanyShippingRule, backingShippingRule))
                .build()

        val environment = environmentForShippingRules(shippingRulesEnvelope)
                .toBuilder()
                .currentUser(MockCurrentUser(UserFactory.user()))
                .apiClient(object : MockApiClient() {
                    override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                        return Observable.just(shippingRulesEnvelope)
                    }
                })
                .build()

        setUpEnvironment(environment, reward, backedProject, PledgeReason.UPDATE_PLEDGE)

        this.selectedShippingRule.assertValues(backingShippingRule)
        this.pledgeButtonIsEnabled.assertValues(false)

        this.vm.inputs.shippingRuleSelected(germanyShippingRule)
        this.selectedShippingRule.assertValues(backingShippingRule, germanyShippingRule)
        this.pledgeButtonIsEnabled.assertValues(false, true)

        this.vm.inputs.shippingRuleSelected(backingShippingRule)
        this.selectedShippingRule.assertValues(backingShippingRule,germanyShippingRule, backingShippingRule)
        this.pledgeButtonIsEnabled.assertValues(false, true, false)

        this.vm.inputs.bonusInput("500")
        this.vm.inputs.increaseBonusButtonClicked()
        this.pledgeButtonIsEnabled.assertValues(false, true, false, true)
    }

    @Test
    fun testExpandableHeaderIsVisible() {
        val reward = RewardFactory.reward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject)

        this.headerSectionIsGone.assertValue(false)
        this.isNoReward.assertNoValues()
    }

    @Test
    fun testNoRewardHeaderIsVisible() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject)

        this.headerSectionIsGone.assertValue(true)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.shippingRuleStaticIsGone.assertValue(true)
        this.isNoReward.assertValue(true)
        this.projectTitle.assertValue(backedProject.name())
    }

    @Test
    fun testExpandableHeaderIsNoVisible() {
        val reward = RewardFactory.noReward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .reward(reward)
                .rewardId(reward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject)

        this.headerSectionIsGone.assertValues(true)
    }


    @Test // TODO: Review
    fun testBonusAmountIncreases_whenPlusButtonIsClicked() {
        val reward = RewardFactory.reward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(50.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()

        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val environment = environmentForLoggedInUser(UserFactory.user())

        setUpEnvironment(environment, reward, backedProject, PledgeReason.PLEDGE)

        this.vm.inputs.increaseBonusButtonClicked()

        this.bonusAmount.assertValues("0", "1")
    }


    @Test
    fun testTotalAmountUpdates_whenBonusIsAdded() {
        val testData = setUpBackedShippableRewardTestData()

        setUpEnvironment(environment(), testData.reward, testData.project, PledgeReason.UPDATE_PLEDGE)

        this.vm.inputs.bonusInput("20")
        this.vm.inputs.increaseBonusButtonClicked()
        this.totalAmount.assertValues("$50","$70", "$71")
        this.bonusAmount.assertValues("0", "20", "21")
    }

    @Test
    fun testBonusMinimumIsZero_andMinusButtonIsDisabled() {
        val reward = RewardFactory.reward()
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(30.0)
                .reward(reward)
                .rewardId(reward.id())
                .build()

        val backedProject = ProjectFactory.project()
                .toBuilder()
                .backing(backing)
                .build()

        setUpEnvironment(environment(), reward, backedProject)

        this.bonusAmount.assertValue("0")
        this.decreaseBonusButtonIsEnabled.assertValue(false)
    }

    @Test
    fun testRewardPlusAddons_inHeader() {
        val reward = RewardFactory.rewardWithShipping().toBuilder()
                .hasAddons(true)
                .build()
        val project = ProjectFactory.project()
                .toBuilder()
                .rewards(listOf(reward))
                .build()

        val addOn = RewardFactory.itemizedAddOn().toBuilder().quantity(2).build()
        val listAddOns = listOf(addOn, addOn, addOn) as java.util.List<Reward>?

        setUpEnvironment(environment(), reward, project, addOns = listAddOns)

        this.shippingRuleStaticIsGone.assertValue(false)
        this.shippingRulesSectionIsGone.assertValue(true)
        this.rewardAndAddOns.assertValue(listOf(reward, addOn, addOn, addOn))
        this.headerSelectedItems.assertValue(listOf(
                Pair(project, reward),
                Pair(project, addOn),
                Pair(project, addOn),
                Pair(project, addOn)
        ))
    }

    @Test
    fun total_whenShippableAddOns() {
        val shipRule = ShippingRuleFactory.usShippingRule()
        val reward = RewardFactory.rewardWithShipping().toBuilder()
                .hasAddons(true)
                .build()
        val project = ProjectFactory.project()
                .toBuilder()
                .rewards(listOf(reward))
                .build()

        val addOn = RewardFactory.itemizedAddOn().toBuilder().quantity(2)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()))
                .build()
        val listAddOns = listOf(addOn, addOn, addOn) as java.util.List<Reward>

        val environment = environment()
        setUpEnvironment(environment, reward, project, addOns = listAddOns)

        this.vm.inputs.shippingRuleSelected(shipRule)

        this.totalAmount.assertValue("$290")
    }

    @Test
    fun total_whenShippableAddOns_differentShippingCost() {
        val shipRule = ShippingRuleFactory.usShippingRule()
        val reward = RewardFactory.rewardWithShipping().toBuilder()
                .hasAddons(true)
                .minimum(50.0)
                .build()
        val project = ProjectFactory.project()
                .toBuilder()
                .rewards(listOf(reward))
                .build()
        // - total rw = 50 + 30 = 80

        val addOn = RewardFactory.itemizedAddOn().toBuilder().quantity(2)
                .minimum(9.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(5.0)
                        .build()
                ))
                .build()
        // - total a1 = (9 + 5) * 2 = 28

        val addOn2 = RewardFactory.itemizedAddOn().toBuilder().quantity(4)
                .minimum(11.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(3.0)
                        .build()
                ))
                .build()
        // total a2 = (11 + 3) * 4 = 56

        val addOn3 = RewardFactory.itemizedAddOn().toBuilder().quantity(10)
                .minimum(15.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(10.0)
                        .build()
                ))
                .build()
        // total a3 = (15 + 10) * 10 = 250

        val listAddOns = listOf(addOn, addOn2, addOn3) as java.util.List<Reward>

        val environment = environment()
        setUpEnvironment(environment, reward, project, addOns = listAddOns)

        this.vm.inputs.shippingRuleSelected(shipRule)

        this.totalAmount.assertValues("$414")
    }

    @Test
    fun total_whenShippableAddOns_differentShippingCost_AndBonus() {
        val shipRule = ShippingRuleFactory.usShippingRule()
        val reward = RewardFactory.rewardWithShipping().toBuilder()
                .hasAddons(true)
                .minimum(50.0)
                .build()
        val project = ProjectFactory.project()
                .toBuilder()
                .rewards(listOf(reward))
                .build()
        // - total rw = 50 + 30 = 80

        val addOn = RewardFactory.itemizedAddOn().toBuilder().quantity(2)
                .minimum(9.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(5.0)
                        .build()
                ))
                .build()
        // - total a1 = (9 + 5) * 2 = 28

        val addOn2 = RewardFactory.itemizedAddOn().toBuilder().quantity(4)
                .minimum(11.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(3.0)
                        .build()
                ))
                .build()
        // total a2 = (11 + 3) * 4 = 56

        val addOn3 = RewardFactory.itemizedAddOn().toBuilder().quantity(10)
                .minimum(15.0)
                .shippingRules(listOf(ShippingRuleFactory.usShippingRule()
                        .toBuilder()
                        .cost(10.0)
                        .build()
                ))
                .build()
        // total a3 = (15 + 10) * 10 = 250

        val listAddOns = listOf(addOn, addOn2, addOn3) as java.util.List<Reward>

        val environment = environment()
        setUpEnvironment(environment, reward, project, addOns = listAddOns)

        this.vm.inputs.shippingRuleSelected(shipRule)
        this.vm.inputs.bonusInput("123")
        this.vm.inputs.increaseBonusButtonClicked()
        this.vm.inputs.increaseBonusButtonClicked()
        this.vm.inputs.increaseBonusButtonClicked()

        this.totalAmount.assertValues(
                "$414",
                "$537",
                "$538",
                "$539",
                "$540")
    }

    private fun assertInitialPledgeCurrencyStates_NoShipping_USProject(environment: Environment, project: Project) {
        this.additionalPledgeAmount.assertValue(expectedCurrency(environment, project, 0.0))
        this.conversionText.assertNoValues()
        this.conversionTextViewIsGone.assertValues(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.projectCurrencySymbol.assertValue("$")
    }

    private fun assertInitialPledgeCurrencyStates_WithShipping_MXProject(environment: Environment, project: Project) {
        this.additionalPledgeAmount.assertValue(expectedCurrency(environment, project, 0.0))
        this.conversionText.assertValue(expectedConvertedCurrency(environment, project, 50.0))
        this.conversionTextViewIsGone.assertValues(false)
        this.pledgeMaximumIsGone.assertValue(true)
        this.projectCurrencySymbol.assertValue("MX$")
        this.shippingAmount.assertValue(expectedCurrency(environment, project, 30.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0))
        this.totalAndDeadline.assertValue(Pair(expectedCurrency(environment, project, 50.0), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(1)
    }

    private fun assertInitialPledgeCurrencyStates_WithShipping_USProject(environment: Environment, project: Project) {
        this.additionalPledgeAmount.assertValue(expectedCurrency(environment, project, 0.0))
        this.conversionText.assertNoValues()
        this.conversionTextViewIsGone.assertValues(true)
        this.pledgeMaximumIsGone.assertValue(true)
        this.projectCurrencySymbol.assertValue("$")
        this.shippingAmount.assertValue(expectedCurrency(environment, project, 30.0))
        this.totalAmount.assertValues(expectedCurrency(environment, project, 50.0))
        this.totalAndDeadline.assertValue(Pair(expectedCurrency(environment, project, 50.0), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(1)
    }

    private fun assertInitialPledgeState_NoShipping(environment: Environment, project: Project) {
        this.additionalPledgeAmountIsGone.assertValue(true)
        this.continueButtonIsEnabled.assertNoValues()
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeHint.assertValue("20")
        this.pledgeTextColor.assertValue(R.color.ksr_green_500)
        this.shippingAmount.assertNoValues()
        this.totalAmount.assertValues(expectedCurrency(environment, project, 20.0))
        this.totalAndDeadline.assertValue(Pair(expectedCurrency(environment, project, 20.0), DateTimeUtils.longDate(this.deadline)))
        this.totalAndDeadlineIsVisible.assertValueCount(1)
    }

    private fun assertInitialPledgeState_WithShipping() {
        this.additionalPledgeAmountIsGone.assertValue(true)
        this.continueButtonIsEnabled.assertNoValues()
        this.pledgeButtonIsEnabled.assertValue(true)
        this.pledgeTextColor.assertValue(R.color.ksr_green_500)
    }

    private fun apolloClientWithStoredCards(storedCards: List<StoredCard>): MockApolloClient {
        return object : MockApolloClient() {
            override fun getStoredCards(): Observable<List<StoredCard>> {
                return Observable.just(storedCards)
            }
        }
    }

    private fun environmentForLoggedInUser(user: User): Environment {
        return environment()
                .toBuilder()
                .currentUser(MockCurrentUser(user))
                .build()
    }

    private fun environmentForShippingRules(envelope: ShippingRulesEnvelope): Environment {
        val apiClient = object : MockApiClient() {
            override fun fetchShippingRules(project: Project, reward: Reward): Observable<ShippingRulesEnvelope> {
                return Observable.just(envelope)
            }
        }

        val config = ConfigFactory.configForUSUser()
        val currentConfig = MockCurrentConfig()
        currentConfig.config(config)

        return environment().toBuilder()
                .apiClient(apiClient)
                .currentConfig(currentConfig)
                .build()
    }

    data class TestData(val reward: Reward,
                        val project: Project,
                        val backing: Backing?,
                        val shippingRulesEnvelope: ShippingRulesEnvelope?,
                        val storedCards: List<StoredCard>)

    private fun setUpBackedShippableRewardTestData(): TestData {
        val backingCard = StoredCardFactory.visa()
        val shippableReward = RewardFactory.rewardWithShipping()
        val unitedStates = LocationFactory.unitedStates()
        val shippingRule = ShippingRuleFactory.usShippingRule().toBuilder().location(unitedStates).build()
        val storedCards = listOf(StoredCardFactory.discoverCard(), backingCard, StoredCardFactory.visa())
        val backing = BackingFactory.backing()
                .toBuilder()
                .amount(50.0)
                .location(unitedStates)
                .locationId(unitedStates.id())
                .paymentSource(PaymentSourceFactory.visa()
                        .toBuilder()
                        .id(backingCard.id())
                        .build())
                .reward(shippableReward)
                .rewardId(shippableReward.id())
                .shippingAmount(shippingRule.cost().toFloat())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        val shippingRulesEnvelope = ShippingRulesEnvelopeFactory.shippingRules()
                .toBuilder()
                .shippingRules(listOf(ShippingRuleFactory.germanyShippingRule(), shippingRule))
                .build()

        return TestData(shippableReward, backedProject, backing, shippingRulesEnvelope, storedCards)
    }

    private fun setUpBackedNoRewardTestData(): TestData {
        val backingCard = StoredCardFactory.visa()
        val noReward = RewardFactory.noReward()
        val storedCards = listOf(StoredCardFactory.discoverCard(), backingCard, StoredCardFactory.visa())
        val backing = BackingFactory.backing()
                .toBuilder()
                .paymentSource(PaymentSourceFactory.visa()
                        .toBuilder()
                        .id(backingCard.id())
                        .build())
                .reward(noReward)
                .rewardId(noReward.id())
                .build()
        val backedProject = ProjectFactory.backedProject()
                .toBuilder()
                .backing(backing)
                .build()

        return TestData(noReward, backedProject, backing, null, storedCards)
    }

    private fun expectedConvertedCurrency(environment: Environment, project: Project, amount: Double): String =
            environment.ksCurrency().formatWithUserPreference(amount, project, RoundingMode.HALF_UP, 2)

    private fun expectedCurrency(environment: Environment, project: Project, amount: Double): String =
            environment.ksCurrency().format(amount, project, RoundingMode.HALF_UP)

    private fun normalizeCurrency(spannedCurrencyString: CharSequence) =
            spannedCurrencyString.toString().replace("\u00A0", " ")
}
