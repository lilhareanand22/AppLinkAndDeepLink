package android.ai.applinkanddeeplink

import android.app.Application
import android.util.Log

/**
 * Base Application class for the AppLink and DeepLink training project.
 *
 * In a multi-module architecture (:app, :deeplink, :applink):
 * - The :app module is the application module that merges manifests, resources, and dependencies.
 * - This Application class initializes global singletons, router configurations, and logs lifecycle events.
 */
class AppLinkAndDeepLinkApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "AppLinkAndDeepLinkApp initialized successfully.")
    }

    companion object {
        private const val TAG = "AppLinkAndDeepLinkApp"
    }
}
