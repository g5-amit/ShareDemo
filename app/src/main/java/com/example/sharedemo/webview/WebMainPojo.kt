package com.example.sharedemo.webview

data class WebMainPojo(
    val id: Int = 1,
    val name: String = "Amit Gupta",
    val org: String = "ODSP",
    val innerPojo: WebInnerPojo = WebInnerPojo()
)

data class WebInnerPojo(
    val empID: Int = 1,
    val location: String = "HYD"
)
