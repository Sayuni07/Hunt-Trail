package ca.gbc.comp3074.shaikh.hunttrail.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// Opens Google Maps (app or browser) using an implicit intent.
// Used by both the Details screen and the Map screen (FR8).
object MapsIntents {

    // Route from the user's current location to the address
    fun openDirections(context: Context, address: String) {
        open(context, "https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(address))
    }

    // Just show a place on the map
    fun openLocation(context: Context, query: String) {
        open(context, "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
    }

    private fun open(context: Context, url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No maps or browser app found", Toast.LENGTH_SHORT).show()
        }
    }
}