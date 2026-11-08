package ca.gbc.comp3074.shaikh.hunttrail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

// Single-activity app: this activity only hosts the toolbar, bottom bar
// and the NavHostFragment. Every screen is a Fragment.
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    // Screens that show the bottom bar (no back arrow on these)
    private val topLevelScreens = setOf(
        R.id.huntListFragment,
        R.id.mapFragment,
        R.id.teamContactsFragment,
        R.id.aboutFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        setSupportActionBar(toolbar)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        // Toolbar title comes from android:label in nav_graph.xml
        val appBarConfig = AppBarConfiguration(topLevelScreens + R.id.splashFragment)
        setupActionBarWithNavController(navController, appBarConfig)
        bottomNav.setupWithNavController(navController)

        // Hide toolbar on Splash; hide bottom bar on Splash, Details and Add/Edit
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.visibility =
                if (destination.id == R.id.splashFragment) View.GONE else View.VISIBLE
            bottomNav.visibility =
                if (destination.id in topLevelScreens) View.VISIBLE else View.GONE
        }
    }

    // Makes the toolbar back arrow work
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp() || super.onSupportNavigateUp()
}