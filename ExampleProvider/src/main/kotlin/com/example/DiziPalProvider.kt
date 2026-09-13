package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class DiziPalProvider : MainAPI() {
    override var mainUrl = "https://dizipal2131.com"
    override var name = "DiziPal"
    override val hasMainPage = true
    override var lang = "tr"
    override val hasDownloadSupport = true
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val document = app.get(mainUrl).document
        val home = ArrayList<HomePageList>()
        
        // This is a basic template for DiziPal.
        val elements = document.select(".item") // Placeholder selector for DiziPal items
        if (elements.isNotEmpty()) {
            val items = elements.mapNotNull { it.toSearchResult() }
            home.add(HomePageList("Güncel İçerikler", items))
        }

        return newHomePageResponse(home, false)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst(".title, h3")?.text() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val posterUrl = this.selectFirst("img")?.attr("src")

        return newMovieSearchResponse(title, href, TvType.TvSeries) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=$query" // Typical WordPress/DiziPal search structure
        val document = app.get(url).document
        
        return document.select(".item, .result-item").mapNotNull {
            it.toSearchResult()
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: "Bilinmeyen İçerik"
        val poster = document.selectFirst(".poster img")?.attr("src")
        val plot = document.selectFirst(".summary, p.plot")?.text()

        return newMovieLoadResponse(title, url, TvType.TvSeries, url) {
            this.posterUrl = poster
            this.plot = plot
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val document = app.get(data).document
        
        // Find all iframes that might contain the video player
        val iframes = document.select("iframe")
        for (iframe in iframes) {
            val src = iframe.attr("data-src").takeIf { it.isNotEmpty() } ?: iframe.attr("src")
            if (src.isNotEmpty() && src.startsWith("http")) {
                loadExtractor(src, data, subtitleCallback, callback)
            }
        }
        
        return true
    }
}
