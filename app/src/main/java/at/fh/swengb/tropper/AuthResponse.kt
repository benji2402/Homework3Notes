package at.fh.swengb.tropper
import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
class AuthResponse(val token: String) {
}