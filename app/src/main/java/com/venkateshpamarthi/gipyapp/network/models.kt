package com.venkateshpamarthi.gipyapp.network

import com.google.gson.annotations.SerializedName

data class GiphyObject(
    val bitly_gif_url: String,
    val bitly_url: String,
    val content_url: String,
    val embed_url: String,
    val id: String,
    val import_datetime: String,
    val is_sticker: Int,
    val rating: String,
    val slug: String,
    val source: String,
    val source_post_url: String,
    val source_tld: String,
    val trending_datetime: String,
    val type: String,
    val url: String,
    val username: String,
    val title: String,
    val _score: String,
    val user: User,
    val images: Images,
    val analytics: Analytics
)

data class User(
    val avatar_url: String,
    val banner_image: String,
    val banner_url: String,
    val display_name: String,
    val is_verified: Boolean,
    val profile_url: String,
    val username: String
)

data class Images(
    @SerializedName("480w_still")
    val w_still: ImageObject,
    val downsized: ImageObject,
    val downsized_large: ImageObject,
    val downsized_medium: ImageObject,
    val downsized_small: ImageObject,
    val downsized_still: ImageObject,
    val fixed_height: ImageObject,
    val fixed_height_downsampled: ImageObject,
    val fixed_height_small: ImageObject,
    val fixed_height_small_still: ImageObject,
    val fixed_height_still: ImageObject,
    val fixed_width: ImageObject,
    val fixed_width_downsampled: ImageObject,
    val fixed_width_small: ImageObject,
    val fixed_width_small_still: ImageObject,
    val fixed_width_still: ImageObject,
    val looping: ImageObject,
    val original: ImageObject,
    val original_mp4: ImageObject,
    val original_still: ImageObject,
    val preview: ImageObject,
    val preview_gif: ImageObject,
    val preview_webp: ImageObject
)



data class ImageObject(
    val height: String,
    val mp4: String?,
    val mp4_size: String?,
    val size: String,
    val url: String,
    val webp: String?,
    val webp_size: String?,
    val width: String
)

data class Analytics(
    val onclick: OnEventUrl,
    val onload: OnEventUrl,
    val onsent: OnEventUrl
)

data class OnEventUrl(
    val url: String
)


data class ResponseModel(
    val data: List<GiphyObject>,
    val meta: Meta,
    val pagination: Pagination
)

data class Pagination(
    val count: Int,
    val offset: Int,
    val total_count: Int
)

data class Meta(
    val msg: String,
    val response_id: String,
    val status: Int
)