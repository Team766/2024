package com.team766.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.team766.logging.Category;
import com.team766.logging.LogEntry;
import com.team766.logging.LogValue;
import com.team766.logging.LogWriter;
import com.team766.logging.Logger;

public class PlotViewer implements WebServer.Handler {
	private static final String ENDPOINT = "/plots";

	private static final String[] COLORS = new String[] {
		"red", "green", "blue", "yellow", "gray", "magenta", "cyan",
		"darkred", "lime", "orange", "purple", "darkblue", "black",
	};

	private static String makePlotData(List<Long> labels, List<List<Double>> data) {
		String r = "<script type=\"application/json\" id=\"plot-data\">\n{\n";
		r += "\"labels\": [\n";
		r += labels.stream().map(l -> Long.toString(l)).collect(Collectors.joining(","));
		r += "],\n\"datasets\": [\n";
		r += IntStream.range(0, data.size()).mapToObj(i ->
			"{ \"label\": \"Value " + i + "\", \"borderColor\": \"" + COLORS[i % COLORS.length] + "\", \"data\": [" +
			data.get(i).stream().map(v -> Double.toString(v)).collect(Collectors.joining(",")) +
			"] }\n").collect(Collectors.joining(","));
		r += "]\n";
		r += "}\n</script>";
		return r;
	}

	private static boolean isNumeric(LogValue value) {
		return value.hasBoolValue() || value.hasFloatValue() || value.hasIntValue();
	}

	private static double getNumericAsDouble(LogValue value) {
		if (value.hasBoolValue()) {
			return value.getBoolValue() ? 1 : 0;
		}
		if (value.hasFloatValue()) {
			return value.getFloatValue();
		}
		if (value.hasIntValue()) {
			return value.getIntValue();
		}
		throw new IllegalArgumentException("LogValue is not numeric: " + value.toString());
	}

	@Override
	public String handle(Map<String, Object> params) {
		final String categoryName = (String)params.get("category");
		final String selectedFormat = (String)params.get("format");

		final var formatStrings = LogWriter.instance.listFormatStrings();
		final List<LogEntry> formattedEntries = categoryName == null ?
			List.of() :
			Logger.get(Category.valueOf(categoryName)).recentEntries()
				.stream()
				.filter(e -> e.hasMessageIndex())
				.collect(Collectors.toList());
		
		final int selectedFormatIndex = formatStrings.indexOf(selectedFormat);

		/*final int numFields = selectedFormat == null ?
			0 :
			(int)selectedFormat.chars().filter(ch -> ch == '%').count();*/

		List<Long> labels = new ArrayList<Long>(formattedEntries.size());
		List<List<Double>> data = null;
		for (LogEntry e : formattedEntries) {
			if (e.getMessageIndex() != selectedFormatIndex) {
				continue;
			}
			if (data == null) {
				data = new ArrayList<List<Double>>();
				final long numValues = e.getArgList().stream().filter(PlotViewer::isNumeric).count();
				for (long i = 0; i < numValues; ++i) {
					data.add(new ArrayList<Double>(formattedEntries.size()));
				}
			}
			labels.add(Math.round(e.getTime() * 1e-6));
			int iter = 0;
			for (LogValue v : e.getArgList()) {
				if (!isNumeric(v)) {
					continue;
				}
				try {
					data.get(iter).add(getNumericAsDouble(v));
					++iter;
				} catch (IndexOutOfBoundsException ex) {
					throw new RuntimeException("Inconsistent argument count", ex);
				}
			}
			if (data.size() != iter) {
				throw new RuntimeException("Inconsistent argument count");
			}
		}
		if (data == null) {
			data = new ArrayList<List<Double>>();
		}

		return String.join("\n", new String[]{
			"<h1>Plot Viewer: " + categoryName + "</h1>",
			"<form id=\"select-form\" method=\"POST\" action=\"" + ENDPOINT + "\"><p>",
			HtmlElements.buildDropDown(
				"category",
				categoryName,
				Arrays.stream(Category.values()).map(Category::name)::iterator),
			HtmlElements.buildDropDown(
				"format",
				selectedFormat,
				formattedEntries.stream()
					.map(e -> e.getMessageIndex())
					.distinct()
					.map(i -> formatStrings.get(i))
					::iterator),
			"<input type=\"submit\" value=\"View\">",
			"</p></form>",
			makePlotData(labels, data),
			"<input type=\"button\" onclick=\"refresh();\" value=\"Refresh\" />",
			"<input type=\"checkbox\" id=\"refresh-enabled\" checked=\"checked\" />",
			"<label for=\"refresh-enabled\">Enable automatic refresh</label>",
			"<div style=\"width: 100%; height: 500px\"><canvas id=\"plot\" width=\"400\" height=\"400\"></canvas></div>",
			"<div>Click and drag to zoom in. Ctrl/Cmd-Click to pan. Double click to zoom out.</div>",
			"<script src=\"/static/chart.min.js\"></script>",
			"<script src=\"/static/hammerjs.min.js\"></script>",
			"<script src=\"/static/chartjs-plugin-zoom.min.js\"></script>",
			"<script>",
			"  function pad(num, size) {",
			"    return ('000' + num).slice(-size);",
			"  }",
			"  function timeToString(msecs) {",
			"    const d = new Date(msecs);",
			"    return d.getHours() + ':' + pad(d.getMinutes(), 2) + ':' + pad(d.getSeconds(), 2) + '.' + pad(d.getMilliseconds(), 3);",
			"  }",
			"  const ctx = document.getElementById('plot').getContext('2d');",
			"  const platform = navigator?.userAgentData?.platform || navigator?.platform",
			"  const chart = new Chart(ctx, {",
			"    type: 'scatter',",
			"    data: JSON.parse(document.getElementById('plot-data').innerHTML),",
			"    options: {",
			"      showLine: true,",
			"      responsive: true,",
			"      maintainAspectRatio: false,",
			"      animation: false,",
			"      scales: {",
			"        x: {",
			"          ticks: {",
			"            callback: function(value, index, ticks) {",
			"              return timeToString(value);",
			"            },",
			"          },",
			"        },",
			"      },",
			"      plugins: {",
			"        tooltip: {",
			"          callbacks: {",
			"            label: function(item) {",
			"              const label = chart.data.labels[item.dataIndex];",
			"              return timeToString(label) + ': ' + item.formattedValue;",
			"            },",
			"          },",
			"        },",
			"        zoom: {",
			"          pan: {",
			"            enabled: true,",
			"            mode: 'xy',",
			"            modifierKey: platform.toUpperCase().includes('MAC') ? 'meta' : 'ctrl',",
			"          },",
			"          zoom: {",
			"            mode: 'xy',",
			"            drag: {",
			"              enabled: true,",
			"              borderColor: 'rgb(54, 162, 235)',",
			"              borderWidth: 1,",
			"              backgroundColor: 'rgba(54, 162, 235, 0.3)',",
			"            },",
			"          },",
			"        },",
			"      },",
			"      onClick: function(event) {",
			"        if (event.native.detail == 2) {",
			"          event.chart.resetZoom();",
			"        }",
			"      },",
			"    },",
			"  });",
			"  function afterLoad(event) {",
			"    var plotData = JSON.parse(document.getElementById('plot-data').innerHTML);",
			"    if (chart.data.datasets.length != plotData.datasets.length) {",
			"      chart.data = plotData;",
			"    } else {",
			"      chart.data.labels = plotData.labels;",
			"      for (var i = 0; i < chart.data.datasets.length; i++) {",
			"        chart.data.datasets[i].data = plotData.datasets[i].data;",
			"      }",
			"    }",
			"    chart.update();",
			"  }",
			"  document.addEventListener('DOMContentLoaded', afterLoad);",
			"  function refresh() {",
			"    var xhttp = new XMLHttpRequest();",
			"    xhttp.onreadystatechange = function() {",
			"      if (this.readyState == 4 && this.status == 200) {",
			"        var newDoc = new DOMParser().parseFromString(this.responseText, 'text/html')",
			"        var oldPlotData = document.getElementById('plot-data');",
			"        oldPlotData.parentNode.replaceChild(",
			"            document.importNode(newDoc.querySelector('#plot-data'), true),",
			"            oldPlotData);",
			"        afterLoad();",
			"      }",
			"    };",
			"    xhttp.open('POST', window.location.href, true);",
			"    var formElement = document.getElementById('select-form');",
			"    xhttp.send(new URLSearchParams(new FormData(formElement)));",
			"  }",
			"  setInterval(function(){",
			"    if (document.getElementById('refresh-enabled').checked) {",
			"      refresh();",
			"    }",
			"  }, 500);",
			"</script>",
		});
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}

	@Override
	public String title() {
		return "Plot Viewer";
	}
}
