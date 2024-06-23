package com.skilledlis.loraanalyticclient

sealed interface Data {

    class Valid(
        val message: String,
    ) : Data

    class Invalidate : Data

    class RSSI_SNR(
        val value: String
    ) : Data
}