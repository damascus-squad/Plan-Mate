import com.google.common.truth.Truth.assertThat
import org.damascus.add
import kotlin.test.Test

class MainTest {
    @Test
    fun `add returns 7 when 3 and 4 are arguments`() {
        // Given
        val a = 3
        val b = 4

        // When
        val result = add(a, b)

        // Then
        assertThat(result).isEqualTo(7)
    }

}