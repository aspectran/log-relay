<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<dl class="tabs b0" data-tabs>
    <dd class="tabs-title is-active" id="tab-1"><a href="#content-1"><span class="bullet icon-archive"></span> app-log <span class="indicator icon-eye blink on"></span></a></dd>
    <dd class="tabs-title" id="tab-2"><a href="#content-2"><span class="bullet icon-archive"></span> access-log <span class="indicator icon-eye"></span></a></dd>
    <dd class="tabs-title" id="tab-3"><a href="#content-3"><span class="bullet icon-archive"></span> crawler-1 <span class="indicator icon-eye"></span></a></dd>
    <dd class="tabs-title" id="tab-4"><a href="#content-4"><span class="bullet icon-archive"></span> crawler-2 <span class="indicator icon-eye"></span></a></dd>
</dl>
<div class="grid-x">
    <div class="cell">
        <div class="log-container">
            <div class="log-header">
                <h4 class="ellipses">~/crawler/crawling_with_hashtags/logs/instagram.log</h4>
                <a class="bite-tail" title="Scroll to End of Log">
                    <span class="tail-status"></span>
                </a>
            </div>
            <div class="missile-route">
                <div class="stack"></div>
            </div>
            <pre id="app-log" class="log-tail"></pre>
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