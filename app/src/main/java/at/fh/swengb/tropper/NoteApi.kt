package at.fh.swengb.tropper

import android.telecom.Call
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

object NoteApi {
    val token = ""
    const val baseUrl = "https://notes.bloder.xyz"
    val retrofit: Retrofit
    val retrofitService: NoteApiService
    init {val moshi = Moshi.Builder().build()
        retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(
            baseUrl).build()
        retrofitService = retrofit.create(NoteApiService::class.java)}

}
interface NoteApiService {
    @GET("/notes")
    fun notes(@Header("access-token") accessToken: String, @Header("last-sync") lastSync: Long): retrofit2.Call<NotesResponse>
    @POST("/login")
    fun login(@Body body: AuthRequest): retrofit2.Call<AuthResponse>
    @POST("/notes")
    fun addOrUpdateNote(@Header("access-token") accessToken: String, @Body body: Note): retrofit2.Call<Note>
}