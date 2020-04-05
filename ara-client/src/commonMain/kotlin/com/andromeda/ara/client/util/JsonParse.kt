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

package com.andromeda.ara.client.util

import com.andromeda.ara.client.models.NewsData
import com.andromeda.ara.client.models.OutputModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse

class JsonParse {
    fun outputModel(text:String): ArrayList<OutputModel> {
        return Json.parse(text)
    }
    fun newsData(text:String): ArrayList<NewsData> {
        return Json.parse(text)
    }
}