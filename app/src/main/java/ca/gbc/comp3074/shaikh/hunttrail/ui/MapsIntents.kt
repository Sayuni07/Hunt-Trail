package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.content.Context
import android.content.Intent
import android.net.Uri

// Opens Google Maps (app or browser) using an implicit intent.
// Used by both the Details screen and the Map screen (FR8).
object MapsIntents {

    // Route from the user's current location to the address
    fun openDirections(context: Context, address: String) {
        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(address)
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    // Just show a place on the map
    fun openLocation(context: Context, query: String) {
        val uri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query)
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}