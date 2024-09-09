package com.team766.web;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class AutonomousSelector<
                AutonomousMode extends AutonomousSelector.Selectable<AutonomousMode>>
        implements WebServer.Handler {
    // TODO: Remove this after migration to MF3 is complete.
    public interface Selectable<S extends Selectable<S>> {
        String name();

        S clone();
    }

    private static final String ENDPOINT = "/auton";

    private final AutonomousMode[] m_autonModes;
    private final String[] m_autonModeNames;
    private AutonomousMode m_selectedAutonMode;

    private static String getSavedAutonMode() {
        return Preferences.userNodeForPackage(AutonomousSelector.class).get("AutonomousMode", null);
    }

    private static void setSavedAutonMode(String modeName) {
        Preferences.userNodeForPackage(AutonomousSelector.class).put("AutonomousMode", modeName);
    }

    public AutonomousSelector(final AutonomousMode[] autonModes) {
        final String savedAutonMode = getSavedAutonMode();
        m_autonModes = autonModes;
        m_autonModeNames = Arrays.stream(autonModes).map(m -> m.name()).toArray(String[]::new);
        m_selectedAutonMode =
                Arrays.stream(autonModes)
                        .filter(m -> m.name().equals(savedAutonMode))
                        .findFirst()
                        .orElse(null);
        if (m_selectedAutonMode == null) {
            if (m_autonModes.length > 0) {
                if (savedAutonMode != null) {
                    Logger.get(Category.AUTONOMOUS)
                            .logRaw(
                                    Severity.WARNING,
                                    "Could not find the saved autonomous mode \""
                                            + savedAutonMode
                                            + "\" in AutonomousModes.java");
                }
                m_selectedAutonMode = m_autonModes[0];
                setSavedAutonMode(m_selectedAutonMode.name());
            } else {
                Logger.get(Category.AUTONOMOUS)
                        .logRaw(
                                Severity.WARNING,
                                "No autonomous modes were declared in AutonomousModes.java");
            }
        }
    }

    public AutonomousMode getSelectedAutonMode() {
        return m_selectedAutonMode;
    }

    @Override
    public String endpoint() {
        return ENDPOINT;
    }

    @Override
    public String handle(final Map<String, Object> params) {
        String locationReplaceScript = "";
        final String selectedAutonModeName = (String) params.get("AutoMode");
        if (selectedAutonModeName != null) {
            final Optional<AutonomousMode> selectedAutonMode =
                    Arrays.stream(m_autonModes)
                            .filter(m -> m.name().equals(selectedAutonModeName))
                            .findFirst();
            if (selectedAutonMode.isEmpty()) {
                Logger.get(Category.AUTONOMOUS)
                        .logData(
                                Severity.ERROR,
                                "Internal framework error: Inconsistent name for selected autonomous mode (selected: %s ; available: %s). Autonomous mode will not run.",
                                selectedAutonModeName,
                                Arrays.stream(m_autonModes)
                                        .map(m -> m.name())
                                        .collect(Collectors.joining(",")));
            } else {
                m_selectedAutonMode = selectedAutonMode.get().clone();
                setSavedAutonMode(m_selectedAutonMode.name());
            }

            locationReplaceScript =
                    "<script>window.location.replace(window.location.pathname);</script>";
        }
        final String selectedAutonModeNameUI =
                m_selectedAutonMode != null ? m_selectedAutonMode.name() : "<none>";
        return String.join(
                "\n",
                new String[] {
                    locationReplaceScript,
                    "<h1>Autonomous Mode Selector</h1>",
                    "<h3 id=\"current-mode\">Current Mode: " + selectedAutonModeNameUI + "</h1>",
                    "<form>",
                    "<p>"
                            + HtmlElements.buildDropDown(
                                    "AutoMode", selectedAutonModeNameUI, m_autonModeNames)
                            + "</p>",
                    "<input type=\"submit\" value=\"Submit\"></form>",
                    "<script>",
                    "  function refreshAutoMode() {",
                    "    var xhttp = new XMLHttpRequest();",
                    "    xhttp.onreadystatechange = function() {",
                    "      if (this.readyState == 4 && this.status == 200) {",
                    "        var newDoc = new DOMParser().parseFromString(this.responseText, 'text/html')",
                    "        var oldMode = document.getElementById('current-mode');",
                    "        oldMode.parentNode.replaceChild(",
                    "            document.importNode(newDoc.querySelector('#current-mode'), true),",
                    "            oldMode);",
                    "     }",
                    "    };",
                    "    xhttp.open('GET', \"" + ENDPOINT + "\", true);",
                    "    xhttp.send();",
                    "  }",
                    "  setInterval(refreshAutoMode, 1000);",
                    "</script>",
                });
    }

    @Override
    public String title() {
        return "Autonomous Selector";
    }
}
