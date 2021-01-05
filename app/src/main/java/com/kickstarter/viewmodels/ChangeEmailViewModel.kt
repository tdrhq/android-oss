package com.kickstarter.viewmodels

import SendEmailVerificationMutation
import UpdateUserEmailMutation
import UserPrivacyQuery
import androidx.annotation.NonNull
import com.kickstarter.R
import com.kickstarter.libs.ActivityViewModel
import com.kickstarter.libs.Environment
import com.kickstarter.libs.rx.transformers.Transformers.*
import com.kickstarter.libs.utils.StringUtils
import com.kickstarter.libs.utils.extensions.isEmail
import com.kickstarter.libs.utils.extensions.isValidPassword
import com.kickstarter.services.ApolloClientType
import com.kickstarter.ui.activities.ChangeEmailActivity
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject

interface ChangeEmailViewModel {

    interface Inputs {
        /** Call when the new email field changes.  */
        fun email(email: String)

        /** Call when the new email field focus changes.  */
        fun emailFocus(hasFocus: Boolean)

        /** Call when the current password field changes.  */
        fun password(password: String)

        /** Call when the send verification button is clicked. */
        fun sendVerificationEmail()

        /** Call when save button has been clicked.  */
        fun updateEmailClicked()
    }

    interface Outputs {
        /** Emits the logged in user's email address.  */
        fun currentEmail(): Observable<String>

        /** Emits a boolean that determines if the email address error should be shown.  */
        fun emailErrorIsVisible(): Observable<Boolean>

        /** Emits a string to display when email update fails.  */
        fun error(): Observable<String>

        /** Emits a boolean to display if the user's email is verified. */
        fun sendVerificationIsHidden(): Observable<Boolean>

        /** Emits a boolean that determines if update email call to server is executing.  */
        fun progressBarIsVisible(): Observable<Boolean>

        /** Emits a boolean that determines if the email and password are valid.  */
        fun saveButtonIsEnabled(): Observable<Boolean>

        /** Emits when the user's email is changed successfully. */
        fun success(): Observable<Void>

        /** Emits the text for the verification button depending on whether the user is a backer or creator. */
        fun verificationEmailButtonText(): Observable<Int>

        /** Emits the warning text string depending on is an email is undeliverable or un-verified for creators. */
        fun warningText(): Observable<Int>

        /** Emits the text color for the warning text depending on if the email is undeliverable or unverified. */
        fun warningTextColor(): Observable<Int>
    }

    class ViewModel(@NonNull val environment: Environment) : ActivityViewModel<ChangeEmailActivity>(environment), Inputs, Outputs {

        val inputs: Inputs = this
        val outputs: Outputs = this

        private val email = PublishSubject.create<String>()
        private val emailFocus = PublishSubject.create<Boolean>()
        private val password = PublishSubject.create<String>()
        private val sendVerificationEmailClick = PublishSubject.create<Void>()
        private val updateEmailClicked = PublishSubject.create<Void>()

        private val currentEmail = BehaviorSubject.create<String>()
        private val emailErrorIsVisible = BehaviorSubject.create<Boolean>()
        private val sendVerificationIsHidden = BehaviorSubject.create<Boolean>()
        private val saveButtonIsEnabled = BehaviorSubject.create<Boolean>()
        private val showProgressBar = BehaviorSubject.create<Boolean>()
        private val success = BehaviorSubject.create<Void>()
        private val warningText = BehaviorSubject.create<Int>()
        private val warningTextColor = BehaviorSubject.create<Int>()
        private val verificationEmailButtonText = BehaviorSubject.create<Int>()

        private val error = BehaviorSubject.create<String>()

        private val apolloClient: ApolloClientType = environment.apolloClient()

        init {

            val userPrivacy = this.apolloClient.userPrivacy()
                    .compose(neverError())

            userPrivacy
                    .compose(bindToLifecycle())
                    .subscribe {
                        this.currentEmail.onNext(it.me()?.email())
                        this.sendVerificationIsHidden.onNext(it.me()?.isEmailVerified)
                    }

            userPrivacy
                    .map { getWarningText(it) }
                    .subscribe { this.warningText.onNext(it) }

            userPrivacy
                    .map { getWarningTextColor(it) }
                    .subscribe { this.warningTextColor.onNext(it) }

            userPrivacy
                    .map { getVerificationText(it) }
                    .subscribe { this.verificationEmailButtonText.onNext(it) }

            this.emailFocus
                    .compose(combineLatestPair<Boolean, String>(this.email))
                    .map { !it.first && it.second.isNotEmpty() && !it.second.isEmail()}
                    .distinctUntilChanged()
                    .compose(bindToLifecycle())
                    .subscribe { this.emailErrorIsVisible.onNext(it) }

            val changeEmail = Observable.combineLatest(this.email, this.password)
            { email, password -> ChangeEmail(email, password) }

            changeEmail
                    .map { ce -> ce.isValid() }
                    .distinctUntilChanged()
                    .compose(bindToLifecycle())
                    .subscribe { this.saveButtonIsEnabled.onNext(it) }

            val updateEmailNotification = changeEmail
                    .compose(takeWhen<ChangeEmail, Void>(this.updateEmailClicked))
                    .switchMap { updateEmail(it).materialize() }
                    .compose(bindToLifecycle())
                    .share()

            updateEmailNotification
                    .compose(errors())
                    .subscribe { this.error.onNext(it.localizedMessage) }

            updateEmailNotification
                    .compose(values())
                    .subscribe {
                        this.currentEmail.onNext(it.updateUserAccount()?.user()?.email())
                        this.success.onNext(null)
                        this.koala.trackChangedEmail()
                    }

            val sendEmailNotification = this.sendVerificationEmailClick
                    .compose(bindToLifecycle())
                    .switchMap { sendEmailVerification().materialize() }
                    .share()

            sendEmailNotification
                    .compose(errors())
                    .subscribe { this.error.onNext(it.localizedMessage) }

            sendEmailNotification
                    .compose(values())
                    .subscribe {
                        this.success.onNext(null)
                        this.koala.trackResentVerificationEmail()
                    }

            this.koala.trackViewedChangedEmail()
        }

        override fun email(email: String) {
            this.email.onNext(email)
        }

        override fun emailFocus(hasFocus: Boolean) {
            this.emailFocus.onNext(hasFocus)
        }

        override fun password(password: String) {
            this.password.onNext(password)
        }

        override fun updateEmailClicked() {
            this.updateEmailClicked.onNext(null)
        }

        override fun sendVerificationEmail() {
            this.sendVerificationEmailClick.onNext(null)
        }

        override fun currentEmail(): Observable<String> = this.currentEmail

        override fun emailErrorIsVisible(): Observable<Boolean> = this.emailErrorIsVisible

        override fun error(): Observable<String> = this.error

        override fun sendVerificationIsHidden(): Observable<Boolean> = this.sendVerificationIsHidden

        override fun progressBarIsVisible(): Observable<Boolean> = this.showProgressBar

        override fun saveButtonIsEnabled(): Observable<Boolean> = this.saveButtonIsEnabled

        override fun success(): Observable<Void> = this.success

        override fun warningText(): Observable<Int> = this.warningText

        override fun warningTextColor(): Observable<Int> = this.warningTextColor

        override fun verificationEmailButtonText(): Observable<Int> = this.verificationEmailButtonText

        private fun getWarningTextColor(userPrivacyData: UserPrivacyQuery.Data?): Int? {
            val deliverable = userPrivacyData?.me()?.isDeliverable ?: false

            return if (!deliverable) {
                R.color.ksr_red_400
            } else {
                R.color.ksr_dark_grey_400
            }
        }

        private fun getWarningText(userPrivacyData: UserPrivacyQuery.Data?): Int? {
            val deliverable = userPrivacyData?.me()?.isDeliverable ?: false
            val isEmailVerified = userPrivacyData?.me()?.isEmailVerified ?: false

            return if (!deliverable) {
                R.string.We_ve_been_unable_to_send_email
            } else if (!isEmailVerified) {
                R.string.Email_unverified
            } else {
                null
            }
        }

        private fun getVerificationText(userPrivacy: UserPrivacyQuery.Data?): Int? {
            val creator = userPrivacy?.me()?.isCreator ?: false

            return if (!creator) {
                R.string.Send_verfication_email
            } else {
                R.string.Resend_verification_email
            }
        }

        private fun sendEmailVerification(): Observable<SendEmailVerificationMutation.Data> {
            return this.apolloClient.sendVerificationEmail()
                    .doOnSubscribe { this.showProgressBar.onNext(true) }
                    .doAfterTerminate { this.showProgressBar.onNext(false) }
        }

        private fun updateEmail(changeEmail: ChangeEmail): Observable<UpdateUserEmailMutation.Data> {
            return this.apolloClient.updateUserEmail(changeEmail.email, changeEmail.password)
                    .doOnSubscribe { this.showProgressBar.onNext(true) }
                    .doAfterTerminate { this.showProgressBar.onNext(false) }
        }

        data class ChangeEmail(val email: String, val password: String) {
            fun isValid(): Boolean {
                return this.email.isEmail() && this.password.isValidPassword()
            }
        }
    }
}
