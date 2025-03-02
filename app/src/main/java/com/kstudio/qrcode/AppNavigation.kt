package com.kstudio.qrcode

enum class Screen {
    HOME,
    History,
    Setting,
    Generate,
}

sealed class NavigationItem(val route: String) {
    data object Home: NavigationItem(Screen.HOME.name)
    data object History: NavigationItem(Screen.History.name)
    data object Setting: NavigationItem(Screen.Setting.name)
    data object Generate: NavigationItem(Screen.Generate.name)
}
