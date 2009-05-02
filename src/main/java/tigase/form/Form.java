/*  Tigase Project
 *  Copyright (C) 2004-2007 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */
package tigase.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tigase.xml.Element;

/**
 * 
 * <p>
 * Created: 2007-05-27 11:41:02
 * </p>
 * 
 * @author bmalkow
 * @version $Rev:43 $
 */
public class Form {

	private List<Field> fields = new ArrayList<Field>();

	private Map<String, Field> fieldsByVar = new HashMap<String, Field>();

	private String instruction;

	private Logger log = Logger.getLogger(this.getClass().getName());

	private String title;

	private String type;

	public Form(Element form) {
		this.type = form.getAttribute("type");
		log.finest("Retriving Data Form type " + this.type);
		List<Element> children = form.getChildren();
		if (children != null) {
			for (Element sub : children) {
				if ("title".equals(sub.getName())) {
					this.title = sub.getCData();
					log.finest("read Data Form title [" + this.title + "]");
				} else if ("instructions".equals(sub.getName())) {
					this.instruction = sub.getCData();
					log.finest("read Data Form instruction [" + this.instruction + "]");
				} else if ("field".equals(sub.getName())) {
					Field field = new Field(sub);
					log.finest("read Data Form field [" + field.getVar() + "]");
					this.fields.add(field);
					this.fieldsByVar.put(field.getVar(), field);
				}
			}
		}
	}

	public Form(String type, String title, String instruction) {
		this.type = type;
		this.title = title;
		this.instruction = instruction;
	}

	public void addField(final Field field) {
		Field cf = field.getVar() != null ? this.fieldsByVar.get(field.getVar()) : null;
		if (cf != null) {
			int p = this.fields.indexOf(cf);
			this.fields.remove(cf);
			this.fields.add(p, field);
		} else {
			this.fields.add(field);
		}
		if (field.getVar() != null) {
			this.fieldsByVar.put(field.getVar(), field);
		}
	}

	public void clear() {
		this.fields.clear();
		this.fieldsByVar.clear();
	}

	public void copyValuesFrom(Element form) {
		log.finest("Copying values from form ");
		List<Element> children = form.getChildren();
		if (children != null) {
			for (Element sub : children) {
				if ("field".equals(sub.getName())) {
					Field field = new Field(sub);

					Field f = fieldsByVar.get(field.getVar());
					if (f != null) {
						f.setValues(field.getValues());
					} else {
						log.warning("Field " + field.getVar() + " is not declared in form '" + title + "'!");
					}
				}
			}
		}
	}

	public void copyValuesFrom(Form form) {
		for (Field field : form.fields) {
			Field f = fieldsByVar.get(field.getVar());
			if (f != null) {
				f.setValues(field.getValues());
			} else {
				log.warning("Field " + field.getVar() + " is not declared in form '" + title + "'!");
			}
		}
	}

	public Field get(String var) {
		return this.fieldsByVar.get(var);
	}

	public List<Field> getAllFields() {
		return this.fields;
	}

	public Boolean getAsBoolean(String var) {
		Field f = get(var);
		if (f != null) {
			String v = f.getValue();
			if (v == null) {
				return null;
			} else if ("1".equals(v) || "true".equals(v)) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else
			return null;
	}

	/**
	 * @param var
	 * @return
	 */
	public Integer getAsInteger(String var) {
		Field f = get(var);
		if (f != null) {
			String v = f.getValue();
			return Integer.parseInt(v);
		} else
			return null;
	}

	public String getAsString(String var) {
		Field f = get(var);
		if (f != null) {
			String v = f.getValue();
			return v;
		} else
			return null;
	}

	public String[] getAsStrings(String var) {
		Field f = get(var);
		if (f != null) {
			String[] v = f.getValues();
			return v;
		} else
			return null;
	}

	public Element getElement() {
		Element form = new Element("x");
		form.setAttribute("xmlns", "jabber:x:data");
		form.setAttribute("type", type);

		if (this.title != null) {
			form.addChild(new Element("title", this.title));
		}
		if (this.instruction != null) {
			form.addChild(new Element("instructions", this.instruction));
		}
		for (Field field : this.fields) {
			form.addChild(field.getElement());
		}
		return form;
	}

	/**
	 * @return Returns the instruction.
	 */
	public String getInstruction() {
		return instruction;
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	public boolean is(String var) {
		return this.fieldsByVar.containsKey(var);
	}

	public void removeField(final String var) {
		Field cf = this.fieldsByVar.remove(var);
		if (cf != null) {
			this.fields.remove(cf);
		}
	}

	/**
	 * @param instruction
	 *            The instruction to set.
	 */
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
}