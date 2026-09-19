// ! Bu araç @keyiflerolsun tarafından | @KekikAkademi için yazılmıştır.

package com.keyiflerolsun

import android.util.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class CizgiMax : MainAPI() {
    override var mainUrl              = "https://cizgimax.online"
    override var name                 = "CizgiMax"
    override val hasMainPage          = true
    override var lang                 = "tr"
    override val hasQuickSearch       = true
    override val supportedTypes       = setOf(TvType.Cartoon)

    override val mainPage = mainPageOf(
        "?orderby=date&order=DESC"                                   to "Son Eklenenler",
        "?s_type&tur[0]=aile&orderby=date&order=DESC"                to "Aile",
        "?s_type&tur[0]=aksiyon-macera&orderby=date&order=DESC"      to "Aksyion",
        "?s_type&tur[0]=animasyon&orderby=date&order=DESC"           to "Animasyon",
        "?s_type&tur[0]=bilim-kurgu-fantazi&orderby=date&order=DESC" to "Bilim Kurgu",
        "?s_type&tur[0]=cocuklar&orderby=date&order=DESC"            to "Çocuklar",
        "?s_type&tur[0]=komedi&orderby=date&order=DESC"              to "Komedi",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("${mainUrl}/diziler/page/${page}${request.data}").document
        val home     = document.select("div.film-item").mapNotNull { it.toSearchResult() }

        return newHomePageResponse(request.name, home)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title     = this.selectFirst("a.film-name")?.text()?.trim() ?: return null
        val href      = fixUrlNull(this.selectFirst("a.poster")?.attr("href")) ?: return null
        val posterUrl = fixUrlNull(this.selectFirst("a.poster img")?.attr("src"))

        return newTvSeriesSearchResponse(title, href, TvType.Cartoon) { this.posterUrl = posterUrl }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("${mainUrl}/ara/?q=${query}").document
        return document.select("div.film-item").mapNotNull { it.toSearchResult() }
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document

        val title       = document.selectFirst("a.anime-title-link")?.text() ?: return null
        val poster      = fixUrlNull(document.selectFirst("meta[property=og:image]")?.attr("content")) ?: return null
        val description = document.selectFirst("meta[property=og:description]")?.attr("content")
        val tags        = document.select("a[href^=/ara/?genre=]").mapNotNull { it.text().trim() }


        val episodes = document.select("a.ep-num-btn").mapNotNull {
            val epHref     = fixUrlNull(it.attr("href")) ?: return@mapNotNull null
            val epEpisode  = it.selectFirst("span.ep-num-label")?.text()?.trim()?.toIntOrNull()
            
            // Try extracting season from the URL (/ninjago-1-sezon-0-bolum-izle/)
            val epSeasonMatch = Regex("""-(\d+)-sezon-""").find(epHref)
            val epSeason = epSeasonMatch?.groupValues?.get(1)?.toIntOrNull() ?: 1

            newEpisode(epHref) {
                this.name = "Bölüm $epEpisode"
                this.season = epSeason
                this.episode = epEpisode
            }
        }

        return newTvSeriesLoadResponse(title, url, TvType.Cartoon, episodes) {
            this.posterUrl = poster
            this.plot      = description
            this.tags      = tags
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val document = app.get(data).document
        
        val scriptContent = document.select("script").map { it.data() }.joinToString("\n")
        val serversB64 = Regex("""JSON\.parse\(atob\("([^"]+)"\)""").find(scriptContent)?.groupValues?.get(1)
        
        if (serversB64 != null) {
            try {
                val decoded = String(android.util.Base64.decode(serversB64, android.util.Base64.DEFAULT))
                val servers = AppUtils.parseJson<List<CizgiMaxServer>>(decoded)
                servers.forEach { server ->
                    val streamUrl = fixUrlNull(server.streamUrl) ?: return@forEach
                    val serverUrl = "$mainUrl$streamUrl"
                    
                    val iframeResp = app.get(serverUrl, referer = data).text
                    val iframeUrl = Regex(""""url":"([^"]+)"""").find(iframeResp)?.groupValues?.get(1)?.replace("\\/", "/")
                    
                    if (iframeUrl != null) {
                        loadExtractor(iframeUrl, data, subtitleCallback, callback)
                    }
                }
            } catch (e: Exception) {
                Log.e("CZGM", "Error parsing servers: ${e.message}")
            }
        }

        return true
    }

    data class CizgiMaxServer(
        @com.fasterxml.jackson.annotation.JsonProperty("type") val type: String?,
        @com.fasterxml.jackson.annotation.JsonProperty("streamUrl") val streamUrl: String?,
        @com.fasterxml.jackson.annotation.JsonProperty("label") val label: String?,
        @com.fasterxml.jackson.annotation.JsonProperty("lang") val lang: String?
    )
}
