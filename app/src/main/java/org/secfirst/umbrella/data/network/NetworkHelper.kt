package org.secfirst.umbrella.data.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.di.ApiKeyInfo
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

/**
 * Just define possible header for requests.
 */
class ApiHeader @Inject constructor(internal val publicApiHeader: PublicApiHeader) {

    class PublicApiHeader @Inject constructor(@ApiKeyInfo
                                              @Expose
                                              @SerializedName
                                              ("api_key") val apiKey: String)
}

/**
 * Responsible to tracking all API calls.
 */
interface ApiHelper {

    @GET(NetworkEndPoint.FEED_LIST)
    fun getBlogApiCall(): Deferred<BlogResponse>

    @GET(NetworkEndPoint.FEED_LIST)
    @Wrapped
    fun getFeedList(@Query("country") countryCode: String,
                    @Query("sources") sources: String,
                    @Query("since") since: String): Deferred<ResponseBody>

    @GET
    fun getRss(@Url url: String): Deferred<ResponseBody>
}

interface MatrixApiHelper {

    @POST(NetworkEndPoint.MATRIX_REGISTER_USER)
    fun registerUserAsync(@Header("Content-Type") content_type: String, @Body req: String): Deferred<RegisterUserResponse>

    @POST(NetworkEndPoint.MATRIX_USER_LOGIN)
    fun loginAsync(@Header("Content-Type") content_type: String, @Body req: String): Deferred<RegisterUserResponse>
}

