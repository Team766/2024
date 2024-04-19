package com.team766.web.dashboard;

public class StatusLight extends Widget {
    private String m_name;
    private String m_color = "gray";
    private int m_width = 150;
    private int m_height = 150;
    private String m_style = "";

    public StatusLight(final String name) {
        this(name, DEFAULT_SORT_ORDER);
    }

    public StatusLight(final String name, final int sortOrder) {
        super(sortOrder);

        m_name = name;
    }

    public void setName(final String name) {
        m_name = name;
    }

    public void setSize(final int width, final int height) {
        m_width = width;
        m_height = height;
    }

    public void setColor(final String color) {
        m_color = color;
    }

    public void setStyle(final String style) {
        m_style = style;
    }

    @Override
    public String render() {
        return "<div style=\"display:inline-block;background:"
                + m_color
                + ";width:"
                + m_width
                + "px;height:"
                + m_height
                + "px;"
                + m_style
                + "\"><span style=\"background:white\">"
                + m_name
                + "</span></div>";
    }
}
