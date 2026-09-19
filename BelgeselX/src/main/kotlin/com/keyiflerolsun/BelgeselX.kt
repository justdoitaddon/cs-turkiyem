// ! Bu araç @keyiflerolsun tarafından | @KekikAkademi için yazılmıştır.

package com.keyiflerolsun

import java.util.Locale
import android.util.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class BelgeselX : MainAPI() {
    override var mainUrl              = "https://belgeselx.com"
    override var name                 = "BelgeselX"
    override val hasMainPage          = true
    override var lang                 = "tr"
    override val hasQuickSearch       = false
    override val supportedTypes       = setOf(TvType.Documentary)
	
    override val mainPage = mainPageOf(
        "${mainUrl}/konu/turk-tarihi-belgeselleri&page=" to "Türk Tarihi",
        "${mainUrl}/konu/tarih-belgeselleri&page="		 to "Tarih",
        "${mainUrl}/konu/seyehat-belgeselleri&page="	 to "Seyahat",
        "${mainUrl}/konu/seri-belgeseller&page="		 to "Seri",
        "${mainUrl}/konu/savas-belgeselleri&page="		 to "Savaş",
        "${mainUrl}/konu/sanat-belgeselleri&page="		 to "Sanat",
        "${mainUrl}/konu/psikoloji-belgeselleri&page="	 to "Psikoloji",
        "${mainUrl}/konu/polisiye-belgeselleri&page="	 to "Polisiye",
        "${mainUrl}/konu/otomobil-belgeselleri&page="	 to "Otomobil",
        "${mainUrl}/konu/nazi-belgeselleri&page="		 to "Nazi",
        "${mainUrl}/konu/muhendislik-belgeselleri&page=" to "Mühendislik",
        "${mainUrl}/konu/kultur-din-belgeselleri&page="	 to "Kültür Din",
        "${mainUrl}/konu/kozmik-belgeseller&page="		 to "Kozmik",
        "${mainUrl}/konu/hayvan-belgeselleri&page="		 to "Hayvan",
        "${mainUrl}/konu/eski-tarih-belgeselleri&page="	 to "Eski Tarih",
        "${mainUrl}/konu/egitim-belgeselleri&page="		 to "Eğitim",
        "${mainUrl}/konu/dunya-belgeselleri&page="		 to "Dünya",
        "${mainUrl}/konu/doga-belgeselleri&page="		 to "Doğa",
        "${mainUrl}/konu/bilim-belgeselleri&page="		 to "Bilim"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = app.get("${request.data}${page}", cacheTime = 60).document
        val home     = document.select("div.px-grid > a.px-card").mapNotNull { it.toSearchResult() }

        return newHomePageResponse(request.name, home)
    }

    private fun String.toTitleCase(): String {
        val locale = Locale("tr", "TR")
        return this.split(" ").joinToString(" ") { word ->
            word.lowercase(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title     = this.selectFirst("div.px-card-info > div.px-card-title")?.text()?.trim()?.toTitleCase() ?: return null
        val href      = fixUrlNull(this.attr("href")) ?: return null
        val posterUrl = fixUrlNull(this.selectFirst("div.px-card-poster > img.px-card-img")?.attr("src"))

        return newTvSeriesSearchResponse(title, href, TvType.Documentary) { this.posterUrl = posterUrl }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val cx = "016376594590146270301:iwmy65ijgrm" // ! Might change in the future

        val tokenResponse = app.get("https://cse.google.com/cse.js?cx=${cx}", referer = "https://belgeselx.com/")
        val cseLibVersion = Regex("""cselibVersion": "(.*)"""").find(tokenResponse.text)?.groupValues?.get(1)
        val cseToken      = Regex("""cse_token": "(.*)"""").find(tokenResponse.text)?.groupValues?.get(1)

        val response = app.get("https://cse.google.com/cse/element/v1?rsz=filtered_cse&num=100&hl=tr&source=gcsc&cselibv=${cseLibVersion}&cx=${cx}&q=${query}&safe=off&cse_tok=${cseToken}&sort=&exp=cc%2Capo&oq=${query}&callback=google.search.cse.api9969&rurl=https%3A%2F%2Fbelgeselx.com%2F", referer = "https://belgeselx.com/")
        Log.d("BLX", "response » $response")
        val titles     = Regex(""""titleNoFormatting": "(.*)"""").findAll(response.text).map { it.groupValues[1] }.toList()
        val urls       = Regex(""""ogImage": "(.*)"""").findAll(response.text).map { it.groupValues[1] }.toList()
        val posterUrls = Regex(""""ogImage": "(.*)"""").findAll(response.text).map { it.groupValues[1] }.toList()

        val searchResponses = mutableListOf<TvSeriesSearchResponse>()

        for (i in titles.indices) {
            val title     = titles[i].split("İzle")[0].trim().toTitleCase()
            val url       = urls.getOrNull(i) ?: continue
            val posterUrl = posterUrls.getOrNull(i) ?: continue

        if (url.contains("diziresimleri")) {
            // URL'den dosya adını al ve .jpg uzantısını kaldır
            val fileName = url.substringAfterLast("/").replace(Regex("\\.(jpe?g|png|webp)$"), "")
            // Yeni URL'yi oluştur
            val modifiedUrl = "https://belgeselx.com/belgeseldizi/$fileName"
            searchResponses.add(newTvSeriesSearchResponse(title, modifiedUrl, TvType.Documentary) {
                this.posterUrl = posterUrl
            })
        } else {
            continue
        }
        }
        return searchResponses
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun load(url: String): LoadResponse? {
        val document = app.get(url).document

        val title       = document.selectFirst(".px-hero-title")?.text()?.trim()?.toTitleCase() ?: return null
        val poster      = fixUrlNull(document.selectFirst(".px-dizi-poster img")?.attr("src") ?: document.selectFirst(".px-dizi-card-poster img")?.attr("src")) ?: return null
        val description = document.selectFirst(".px-hero-desc")?.text()?.trim()
        val tags        = document.select("div.gen-socail-share a[href*='belgeselkanali']").map { it.attr("href").split("/").last().replace("-", " ").toTitleCase() }

        var counter  = 0
        val episodes = Regex("""diziGetir\('(\d+)','(\d+)','(\d+)','(\d+)','([^']+)','[^']*','[^']*','(\d+)','(\d+)','[^']*','([^']+)'""").findAll(document.html()).mapNotNull { match ->
            val id        = match.groupValues[1]
            val ic1       = match.groupValues[2]
            val ic2       = match.groupValues[3]
            val ic3       = match.groupValues[4]
            val epName    = match.groupValues[5]
            val epSeason  = match.groupValues[6].toIntOrNull() ?: 1
            val epEpisode = match.groupValues[7].toIntOrNull() ?: ++counter

            val dataString = "$id,$ic1,$ic2,$ic3,$url"

            newEpisode(dataString) {
                this.name    = epName
                this.season  = epSeason
                this.episode = epEpisode
            }
        }.toList()

        return newTvSeriesLoadResponse(title, url, TvType.Documentary, episodes) {
            this.posterUrl = poster
            this.plot      = description
            this.tags      = tags
        }
    }

    override suspend fun loadLinks(data: String, isCasting: Boolean, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit): Boolean {
        val parts = data.split(",")
        if (parts.size < 5) return false
        val id = parts[0]
        val icArray = listOf(parts[1], parts[2], parts[3]).filter { it != "0" }
        val referer = parts[4]

        val srcMap = mapOf("0" to "new5", "2" to "new1", "5" to "new4", "3" to "new2", "4" to "new3")

        icArray.forEachIndexed { index, ic ->
            val f = srcMap[ic] ?: "default"
            val sira = index + 1
            val iframeUrl = "https://belgeselx.com/video/data/$f.php?id=$id&sira=$sira"
            Log.d("BLX", "iframeUrl oluşturuldu » $iframeUrl")

            val alternatifResp = app.get(iframeUrl, referer = referer)

            Regex("""file:\s*"([^"]+)",\s*label:\s*"([^"]+)"""").findAll(alternatifResp.text).forEach {
                var thisName = this.name
                val videoUrl = it.groupValues[1]
                var quality = it.groupValues[2]

                if (quality.contains("FULL", ignoreCase = true)) {
                    quality = "1080p"
                    thisName = "Google"
                }
                
                callback.invoke(
                    newExtractorLink(
                        source = thisName,
                        name = thisName,
                        url = videoUrl,
                        referer = referer,
                        quality = getQualityFromName(quality.replace("p", "").toIntOrNull()?.toString() ?: quality)
                    )
                )
            }
        }

        return true
    }
}
