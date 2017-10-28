/*
 * ObjectComparator.java
 *
 * Tigase Jabber/XMPP Utils
 * Copyright (C) 2004-2017 "Tigase, Inc." <office@tigase.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 */
package tigase.util;

import java.util.Comparator;
import java.io.Serializable;

/**
 * Describe class ObjectComparator here.
 *
 *
 * Created: Tue May 17 23:53:20 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class ObjectComparator implements Comparator<Object>, Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Describe <code>compare</code> method here.
   *
   * @param object an <code>Object</code> value
   * @param object1 an <code>Object</code> value
   * @return an <code>int</code> value
   */
  public int compare(final Object o1, final Object o2) {
    return o1.getClass().getName().compareTo(o2.getClass().getName());
  }

} // ObjectComparator