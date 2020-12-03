package com.kickstarter.services

import android.net.Uri
import com.kickstarter.KSRobolectricTestCase
import com.kickstarter.libs.utils.extensions.*
import org.junit.Test

class UriExtTest: KSRobolectricTestCase() {
    private val checkoutUri = Uri.parse("https://www.ksr.com/projects/creator/project/pledge")
    private val discoverCategoriesUri = Uri.parse("https://www.ksr.com/discover/categories/art")
    private val discoverScopeUri = Uri.parse("https://www.kickstarter.com/discover/ending-soon")
    private val discoverPlacesUri = Uri.parse("https://www.ksr.com/discover/places/newest")
    private val newGuestCheckoutUri = Uri.parse("https://www.ksr.com/checkouts/1/guest/new")
    private val projectUri = Uri.parse("https://www.ksr.com/projects/creator/project")
    private val projectPreviewUri = Uri.parse("https://www.ksr.com/projects/creator/project?token=token")
    private val projectSurveyUri = Uri.parse("https://www.ksr.com/projects/creator/project/surveys/survey-param")
    private val updatesUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts")
    private val updateUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id")
    private val userSurveyUri = Uri.parse("https://www.ksr.com/users/user-param/surveys/survey-id")
    private val webEndpoint = "https://www.ksr.com"

    @Test
    fun testKSUri_isCheckoutUri() {
        assertTrue(checkoutUri.isCheckoutUri(webEndpoint))
    }

    @Test
    fun testKSUri_isDiscoverCategoriesPath() {
        assertTrue(discoverCategoriesUri.isDiscoverCategoriesPath())
        assertFalse(discoverPlacesUri.isDiscoverCategoriesPath())
    }

    @Test
    fun testKSUri_isDiscoverPlacesPath() {
        assertTrue(discoverPlacesUri.isDiscoverPlacesPath())
        assertFalse(discoverCategoriesUri.isDiscoverPlacesPath())
    }

    @Test
    fun testKSUri_isDiscoverScopePath() {
        assertTrue(discoverScopeUri.isDiscoverScopePath( "ending-soon"))
    }

    @Test
    fun testKSUri_isKickstarterUri() {
        val ksrUri = Uri.parse("https://www.ksr.com/discover")
        val uri = Uri.parse("https://www.hello-world.org/goodbye")
        assertTrue(ksrUri.isKickstarterUri(webEndpoint))
        assertFalse(uri.isKickstarterUri(webEndpoint))
    }

    @Test
    fun testKSUri_isWebViewUri() {
        val ksrUri = Uri.parse("https://www.ksr.com/project")
        val uri = Uri.parse("https://www.hello-world.org/goodbye")
        val ksrGraphUri = Uri.parse("https://www.ksr.com/graph")
        val graphUri = Uri.parse("https://www.hello-world.org/graph")
        val favIconUri = Uri.parse("https://www.ksr.com/favicon.ico")
        assertTrue(ksrUri.isWebViewUri(webEndpoint))
        assertFalse(uri.isWebViewUri(webEndpoint))
        assertTrue(ksrGraphUri.isWebViewUri(webEndpoint))
        assertFalse(graphUri.isWebViewUri(webEndpoint))
        assertFalse(favIconUri.isWebViewUri(webEndpoint))
    }

    @Test
    fun testKSUri_isKSFavIcon() {
        val ksrUri = Uri.parse("https://www.ksr.com/favicon.ico")
        val uri = Uri.parse("https://www.hello-world.org/goodbye")
        assertTrue(ksrUri.isKSFavIcon(webEndpoint))
        assertFalse(uri.isKSFavIcon(webEndpoint))
    }

    @Test
    fun testKSUri_isModalUri() {
        val modalUri = Uri.parse("https://www.ksr.com/project?modal=true")
        assertTrue(modalUri.isModalUri(webEndpoint))
        assertFalse(projectUri.isModalUri(webEndpoint))
    }

    @Test
    fun testKSUri_isNewGuestCheckoutUri() {
        assertTrue(newGuestCheckoutUri.isNewGuestCheckoutUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectSurveyUri() {
        assertTrue(projectSurveyUri.isProjectSurveyUri(webEndpoint))
        assertFalse(userSurveyUri.isProjectSurveyUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectUpdateCommentsUri() {
        val updateCommentsUri = Uri.parse("https://www.ksr.com/projects/creator/project/posts/id/comments")
        assertTrue(updateCommentsUri.isProjectUpdateCommentsUri(webEndpoint))
        assertFalse(updatesUri.isProjectUpdateCommentsUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectUpdateUri() {
        assertTrue(updateUri.isProjectUpdateUri(webEndpoint))
        assertFalse(updatesUri.isProjectUpdateUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectUpdatesUri() {
        assertTrue(updatesUri.isProjectUpdatesUri(webEndpoint))
        assertFalse(updateUri.isProjectUpdatesUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectUri() {
        assertTrue(projectUri.isProjectUri(webEndpoint))
        assertTrue(projectPreviewUri.isProjectUri(webEndpoint))
        assertFalse(updateUri.isProjectUri(webEndpoint))
    }

    @Test
    fun testKSUri_isProjectPreviewUri() {
        assertTrue(projectPreviewUri.isProjectPreviewUri(webEndpoint))
        assertFalse(projectUri.isProjectPreviewUri(webEndpoint))
    }

    @Test
    fun testKSuri_isUserSurveyUri() {
        assertTrue(userSurveyUri.isUserSurveyUri(webEndpoint))
        assertFalse(projectSurveyUri.isUserSurveyUri(webEndpoint))
    }
}