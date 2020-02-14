<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="grid-x grid-padding-x">
    <div class="cell">
        <div class="log-container">
            <div class="log-header">
                <ul class="tab">
                    <li>app-log</li>
                    <li>app-log2</li>
                </ul>
                <a class="bite-tail" title="Scroll to End of Log">
                    <span class="tail-status"></span>
                </a>
            </div>
            <div class="missile-route">
                <div class="stack"></div>
            </div>
            <pre id="app-log" class="log-tail"></pre>
            <pre id="app-log2" class="log-tail"></pre>
        </div>
    </div>
</div>
<link rel="stylesheet" href="/assets/css/logtail-viewer.css">
<script src="/assets/js/logtail-viewer.js"></script>
<script>
    $(function() {
        let logViewer = new LogtailViewer("/logtail", "app-log,app-log2");
        $(".bite-tail").click(function() {
            let logtail = $(this).closest(".log-container").find(".log-tail");
            logViewer.switchTailBite(logtail, !logtail.data("bite"));
        });
        try {
            logViewer.openSocket();
        } catch (e) {
            logViewer.printErrorMessage("Socket connection failed");
        }
    });
</script>