package com.iv.piter

import com.github.mvysny.kaributools.navigateTo
import com.github.mvysny.vaadinsimplesecurity.SimpleNavigationAccessControl
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import com.iv.piter.home.HomeRoute
import com.iv.piter.security.LoginRoute
import com.iv.piter.security.loginService
import eu.vaadinonkotlin.vaadin.Session

/**
 * Checks security and redirects to the LoginView if need be.
 */
class AppServiceInitListener : VaadinServiceInitListener {
    private val accessControl = SimpleNavigationAccessControl.usingService { Session.loginService }
    init {
       // accessControl.setLoginView(LoginRoute::class.java)
          accessControl.setLoginView(HomeRoute::class.java)
//        navigateTo<HomeRoute>()
    }

    override fun serviceInit(e: ServiceInitEvent) {
        e.source.addUIInitListener { uiInitEvent -> uiInitEvent.ui.addBeforeEnterListener(accessControl) }
    }
}
