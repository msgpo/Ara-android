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

package com.andromeda.ara.util

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andromeda.ara.models.TabModel
import com.andromeda.ara.skills.SearchFunctions
import com.google.android.material.button.MaterialButton

class TabAdapter(data:List<TabModel>, searchFunctions: SearchFunctions) :RecyclerView.Adapter<TabAdapter.FeedModelViewHolder>() {
    val array = data
    val funcs = searchFunctions
    class FeedModelViewHolder(private val button: MaterialButton) : RecyclerView.ViewHolder(button)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedModelViewHolder {
        return  FeedModelViewHolder(MaterialButton(parent.context))
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: FeedModelViewHolder, position: Int) {
        val materialButton = holder.itemView as MaterialButton
        materialButton.text = array[position].text
        materialButton.setOnClickListener {
            funcs.onTabTrigger(array[position])
        }
    }

}
