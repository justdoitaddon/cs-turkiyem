package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.app

class YouTubeProvider : MainAPI() {
    override var mainUrl = "https://www.youtube.com"
    override var name = "YouTube"
    override val supportedTypes = setOf(TvType.Movie)
    override var lang = "tr"
    override val hasMainPage = false

    override suspend fun search(query: String): List<SearchResponse> {
        val searchUrl = "$mainUrl/results?search_query=$query"
        val response = app.get(searchUrl).text
        val ytInitialData = Regex("""ytInitialData\s*=\s*(\{.*?\});""").find(response)?.groupValues?.get(1)
            ?: return emptyList()

        val searchResults = mutableListOf<SearchResponse>()
        val chunks = ytInitialData.split("\"videoRenderer\":")
        for (i in 1 until chunks.size) {
            val chunk = chunks[i]

            val videoIdMatch = """"videoId":"([^"]+)"""".toRegex().find(chunk)
            val titleMatch = """"title":\{"runs":\[\{"text":"(.*?[^\\])"\}""".toRegex().find(chunk) 
                ?: """"title":\{"runs":\[\{"text":"(.*?)"\}""".toRegex().find(chunk)
            val thumbMatch = """"thumbnails":\[\{"url":"([^"]+)"""".toRegex().find(chunk)

            if (videoIdMatch != null && titleMatch != null) {
                val videoId = videoIdMatch.groupValues[1]
                var title = titleMatch.groupValues[1]
                try {
                    title = parseJson<String>("\"$title\"")
                } catch (e: Exception) {
                    // Ignore parsing error and use raw
                }
                val posterUrl = thumbMatch?.groupValues?.get(1) ?: ""
                val url = "$mainUrl/watch?v=$videoId"

                searchResults.add(
                    newMovieSearchResponse(title, url, TvType.Movie) {
                        this.posterUrl = posterUrl
                    }
                )
            }
        }
        return searchResults
    }

    override suspend fun load(url: String): LoadResponse? {
        val response = app.get(url).text
        
        val titleMatch = Regex("""<title>(.*?)</title>""").find(response)
        var title = titleMatch?.groupValues?.get(1)?.replace(" - YouTube", "") ?: "Bilinmeyen Başlık"
        title = title.replace("&amp;", "&").replace("&#39;", "'").replace("&quot;", "\"")
        
        val descMatch = Regex(""""shortDescription":"(.*?[^\\])"""").find(response) 
            ?: Regex(""""shortDescription":"(.*?)"""").find(response)
        var description = descMatch?.groupValues?.get(1) ?: ""
        try {
            description = parseJson<String>("\"$description\"")
        } catch (e: Exception) {}
        
        val thumbMatch = Regex("""<meta property="og:image" content="(.*?)">""").find(response)
        val posterUrl = thumbMatch?.groupValues?.get(1)

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = posterUrl
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        loadExtractor(data, subtitleCallback, callback)
        return true
    }
}