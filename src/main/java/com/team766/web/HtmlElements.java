package com.team766.web;

class HtmlElements {
    public static String buildDropDown(
            final String valueName, final String current, final String... options) {
        String id = valueName.replace(' ', '_');
        String out = "<select name=\"" + id + "\">";

        for (String s : options) {
            if (s.equals(current)) {
                out += "<option value=\"" + s + "\" selected >" + s + "</option>";
            } else {
                out += "<option value=\"" + s + "\">" + s + "</option>";
            }
        }

        return out + "</select>";
    }

    public static String buildForm(final String valueName, final double v) {
        return buildForm(valueName, Double.toString(v));
    }

    public static String buildForm(final String valueName, final String v) {
        String id = valueName.replace(' ', '_');
        return "<label for=\""
                + id.toUpperCase()
                + "\">"
                + valueName
                + ":</label>"
                + "<input name=\""
                + id.toLowerCase()
                + "\" id=\""
                + id.toUpperCase()
                + "\" type=\"text\" value=\""
                + v
                + "\"/>";
    }

    public static String buildForm(final String valueName) {
        return buildForm(valueName, 0.0);
    }
}
