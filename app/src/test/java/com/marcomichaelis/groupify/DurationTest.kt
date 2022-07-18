package com.marcomichaelis.groupify

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test


class DurationTest {

    @Test
    fun `should format duration correctly`() {
        val duration = 245 * 1000
        assertThat(formatDuration(duration)).isEqualTo("4:05")
    }

    @Test
    fun `should format progress correctly`() {
        val duration = 245 * 1000
        val progress = 30 * 1000
        assertThat(formatProgress(duration, progress)).isEqualTo("0:30/4:05")
    }


}