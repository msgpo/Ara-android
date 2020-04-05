/*
 * Copyright (c) 2020. Fulton Browne
 *  This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.andromeda.ara.client.feeds

import com.andromeda.ara.client.models.NewsData
import com.andromeda.ara.client.util.JsonParse
import com.andromeda.ara.client.util.ReadURL
import com.andromeda.ara.client.util.ServerUrl

class News {
    suspend fun general(locale: String): ArrayList<NewsData> {
        val get = ReadURL().get("${ServerUrl.url}/${linkMapGeneral(locale)}")
        return JsonParse().newsData(get)
    }
    private fun linkMapGeneral(locale: String): String? {
        val map = mapOf("us" to "news/us", "uk" to "news/uk")
        return map.getOrElse(locale, { return "news/us"})
    }
}