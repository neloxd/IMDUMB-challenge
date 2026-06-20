package com.imdumb.app.domain.model

data class AppConfiguration(val welcomeMessage: String, val homeTitle: String, val recommendationsEnabled: Boolean) {
    companion object {
        val DEFAULT = AppConfiguration(
            welcomeMessage = "Descubre historias que vale la pena recomendar",
            homeTitle = "Categorías",
            recommendationsEnabled = true
        )
    }
}
