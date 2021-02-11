/*
 * Copyright (C) 2021  Tomas Novosad
 * VSB-TUO, Faculty of Electrical Engineering and Computer Science
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package cz.vsb.genetics.svc.common;

import java.util.HashSet;
import java.util.Set;

public enum SvType {
    BND,
    CNV,
    DEL,
    INS,
    DUP,
    INV,
    UNK
    ;

    public static Set<SvType> getSvTypes(String svTypes) {
        Set<SvType> types = new HashSet<>();

        for (String type : svTypes.split(",")) {
            type = type.trim().toUpperCase();
            try {
                SvType svType = SvType.valueOf(type);
                types.add(svType);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Skipping invalid structural variant type provided: " + type);
            }
        }

        return types;
    }
}
