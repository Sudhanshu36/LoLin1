/*
 * This file is part of LoLin1.
 *
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */

package org.jorge.lolin1.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public abstract class PicassoUtils {

    public static void loadInto(Context context, String path, int errorResId, ImageView target, Object tag) {
        Picasso.with(context)
                .load(path)
                .error(errorResId)
                .tag(tag)
                .into(target);
    }
}