package com.iv.piter

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.router.RouterLayout
//import dev.inmo.tgbotapi.webapps.webApp
//import com.vaadin.securitydemo.welcome.WelcomeRoute

/**
 * The main layout. It uses the app-layout component which makes the app look like an Android Material app.
 */
class MainLayout : KComposite(), RouterLayout {

    private val root = ui {
      appLayout {
          className = "main-layout"
      }
  }

}
